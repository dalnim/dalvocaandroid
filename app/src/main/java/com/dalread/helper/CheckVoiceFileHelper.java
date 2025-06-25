package com.dalread.helper;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.dalread.model.VocaDownload;
import com.dalread.network.DalApiImpl;
import com.dalread.network.DalApiListener;
import com.dalread.util.BaseVoca;
import com.dalread.util.DLog;
import com.dalread.util.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.realm.Realm;

public class CheckVoiceFileHelper {

    private Context context;
    private File voiceFolder;

    public CheckVoiceFileHelper(Context context, File voiceFolder) {
        this.context = context;
        this.voiceFolder = voiceFolder;
    }

    public void checkVoiceFile(final String fileName, DalApiImpl dalApi, String uid, int studyLang, final OnVoiceFileCheckListener listener) {
        if (!TextUtils.isEmpty(fileName) && Utils.isConnected(context)) {
            dalApi.getMultipleVoiceFileVersion(
                    uid,
                    studyLang,
                    fileName,
                    new DalApiListener<List<VocaDownload>>() {

                        @Override
                        public void onSuccess(List<VocaDownload> response) {
                            if (!response.isEmpty()) {
                                final VocaDownload vocaDownload = response.get(0);
                                if (vocaDownload.getVersion() > -1) {
                                    final VocaDownload[] localVocaDownload = new VocaDownload[1];
                                    BaseVoca.executeRealmTransaction(new Realm.Transaction() {

                                        @Override
                                        public void execute(Realm realm) {
                                            localVocaDownload[0] = realm.where(VocaDownload.class)
                                                    .equalTo("name", fileName)
                                                    .findFirst();
                                        }
                                    });
                                    if (localVocaDownload[0] == null || localVocaDownload[0].getVersion() < vocaDownload.getVersion()) {
                                        downloadFromFirebase(fileName, vocaDownload, listener);
                                        return;
                                    }
                                } else if (vocaDownload.getReplacementVersion() > -1) {
                                    final VocaDownload[] localVocaDownload = new VocaDownload[1];
                                    BaseVoca.executeRealmTransaction(new Realm.Transaction() {

                                        @Override
                                        public void execute(Realm realm) {
                                            localVocaDownload[0] = realm.where(VocaDownload.class)
                                                    .equalTo("name", vocaDownload.getReplacementName())
                                                    .findFirst();
                                        }
                                    });
                                    if (localVocaDownload[0] == null || localVocaDownload[0].getVersion() < vocaDownload.getReplacementVersion()) {
                                        downloadFromFirebase(vocaDownload.getReplacementName(), vocaDownload, listener);
                                        return;
                                    }
                                }
                            }
                            if (listener != null) {
                                listener.onFinish(fileName);
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                            if (listener != null) {
                                listener.onFinish(fileName);
                            }
                        }
                    }
            );
        } else if (listener != null) {
            listener.onFinish((File) null);
        }
    }

    private void downloadFromFirebase(final String fileName, final VocaDownload vocaDownload, final OnVoiceFileCheckListener listener) {
        final File file = BaseVoca.getVoiceFileOnLocal(voiceFolder, fileName);
        Uri uri = Uri.fromFile(file);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(
                BaseVoca.getVoiceFolderPathOnFirebase(fileName) + uri.getLastPathSegment()
        );
        storageReference.getFile(file)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        DLog.i(getLogTag(), "Download " + fileName + " onSuccess");
                        saveServerVocaDownloadAndCopyFile(file, vocaDownload);
                        if (listener != null) {
                            listener.onFinish(file);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        DLog.i(getLogTag(), "Download " + fileName + " onFailure");
                        saveServerVocaDownloadAndCopyFile(null, vocaDownload);
                        if (listener != null) {
                            listener.onFinish(file);
                        }
                    }
                });
    }

    private void saveServerVocaDownloadAndCopyFile(File srcFile, final VocaDownload vocaDownload) {
        if (srcFile != null && srcFile.getName().equals(vocaDownload.getReplacementName())) {
            final VocaDownload vD = new VocaDownload();
            vD.setName(vocaDownload.getReplacementName());
            vD.setVersion(vocaDownload.getReplacementVersion());
            BaseVoca.executeRealmTransaction(new Realm.Transaction() {

                @Override
                public void execute(@NonNull Realm realm) {
                    realm.copyToRealmOrUpdate(vD);
                }
            });
            copyFile(srcFile, vocaDownload.getName());
        } else {
            BaseVoca.executeRealmTransaction(new Realm.Transaction() {

                @Override
                public void execute(@NonNull Realm realm) {
                    realm.copyToRealmOrUpdate(vocaDownload);
                }
            });
        }
    }

    private void copyFile(File srcFile, String dstFileName) {
        File dstFile = BaseVoca.getVoiceFileOnLocal(voiceFolder, dstFileName);
        try {
            BaseVoca.copyFile(srcFile, dstFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getLogTag() {
        return "CheckVoiceFileHelper";
    }

    public interface OnVoiceFileCheckListener {

        void onFinish(String fileName);

        void onFinish(File file);
    }
}
