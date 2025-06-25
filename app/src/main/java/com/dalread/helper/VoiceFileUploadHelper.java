package com.dalread.helper;

import android.content.Context;
import android.net.Uri;

import com.dalread.BaseApplication;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnVoiceFileInfoDownloadListener;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.UserUtil;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class VoiceFileUploadHelper {
    private Context context;
    private SharedPreferencesDB sharedPreferences;
    BaseApplication application;
    public VoiceFileUploadHelper(Context context, BaseApplication application) {
        this.context = context;
        this.sharedPreferences = SharedPreferencesDB.getInstance(context);
        this.application = application;
    }

    public void uploadVoiceFile(IVocaFullPlayTTSItem voca, OnVoiceFileInfoDownloadListener onVoiceFileInfoDownloadListener) {
        final File recordFile = BaseVoca.getVoiceFileInApp(context, voca.getVIPath());
        if (recordFile != null && recordFile.exists()) {
            String voiceFileName = recordFile.getName();
            Uri uri = Uri.fromFile(recordFile);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(BaseVoca.getVoiceFolderPathOnFirebase(voca.getVIPath()) + uri.getLastPathSegment());
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("application/octet-stream")
                    .build();
            UploadTask uploadTask = storageReference.putFile(uri, metadata);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                //Just increase recording file version on server only. Local DB's version will be updated in other place.
                application.getDalAiImpl().updateVoiceFileInfo(
                        String.valueOf(UserUtil.getUserID(context)),
                        sharedPreferences.getLangStudyCode(),
                        voca.getVIVocaId().toString(),
                        voca.getVIVocaType(),
                        Constant.FILE.EXTENTION_SPEAKING,
                        recordFile.length(),
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                if (response) {
                                    VoiceFileDownloadHelper downloadFileHelper = new VoiceFileDownloadHelper(context, true, null);
                                    downloadFileHelper.getServerVoiceFileInfo(voiceFileName, onVoiceFileInfoDownloadListener);
                                } else {
                                    onVoiceFileInfoDownloadListener.onFailure(voiceFileName);
                                }
//                                if (response) {
//                                    Voca.executeRealmTransaction(realm -> {
//                                        VocaDownload vocaDownload = realm.where(VocaDownload.class)
//                                                .equalTo("name", voca.getVIPath())
//                                                .findFirst();
//                                        if (vocaDownload == null) {
//                                            vocaDownload = new VocaDownload();
//                                            vocaDownload.setName(voca.getVIPath());
//                                            vocaDownload.setVersion(0);
//                                            realm.copyToRealm(vocaDownload);
//                                        } else {
//                                            int version = vocaDownload.getVersion() + 1;
//                                            vocaDownload.setVersion(version);
//                                        }
//                                    });
//                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                onVoiceFileInfoDownloadListener.onFailure(voiceFileName);
                            }
                        }
                );
                application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DATA_CHANGED, null));
            });
        }
    }
}
