package com.dalread.asyntask;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.BaseVoca;
import com.dalread.util.DLog;
import com.dalread.util.DateUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BackupVoicesTask {

    private Context context;
    private int uid;
    private int studyLang;
    private File voiceFolder;
    private String logTag;

    public BackupVoicesTask(Context context, int uid, int studyLang) {
        this.context = context;
        this.uid = uid;
        this.studyLang = studyLang;
        voiceFolder = BaseVoca.getVoiceFolderOnLocal(context);
        logTag = getClass().getSimpleName();
    }

    public void backup(IVocaFullPlayTTSItem voca) {
        ArrayList<IVocaFullPlayTTSItem> vocas = new ArrayList<>();
        vocas.add(voca);
        backup(vocas);
    }

    public void backup(ArrayList<IVocaFullPlayTTSItem> vocas) {
        if (vocas == null || vocas.isEmpty()) {
            DLog.i(logTag, "Finish backup");
            return;
        }

        IVocaFullPlayTTSItem voca = vocas.remove(0);
        if (voca.hasVIVoiceFile()) {
            downloadVoiceFile(voca, vocas);
        } else {
            DLog.i(logTag, voca.getVIVoca() + " has no voice file");
            backup(vocas);
        }
    }

    private void downloadVoiceFile(final IVocaFullPlayTTSItem voca, final ArrayList<IVocaFullPlayTTSItem> vocas) {
        final String fileName = BaseVoca.getOutputRecordingFileName(studyLang, voca.getVIVocaType(), voca.getVIVocaId(), uid);
        final File file = BaseVoca.getVoiceFileOnLocal(voiceFolder, fileName);
        if (file == null) {
            DLog.i(logTag, "Cannot get voice file for " + voca.getVIVoca());
            backup(vocas);
        } else {
            final StorageReference storageReference = FirebaseStorage.getInstance()
                    .getReference()
                    .child(
                            BaseVoca.getVoiceFolderPathOnFirebase(fileName) + fileName
                    );
            if (file.exists()) {
                DLog.i(logTag, fileName + " exists");
                getDateString(storageReference, file, voca, vocas);
            } else {
                storageReference.getFile(file)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                DLog.i(logTag, "Download " + fileName + " onSuccess");
                                getDateString(storageReference, file, voca, vocas);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {

                            @Override
                            public void onFailure(@NonNull Exception e) {
                                DLog.i(logTag, "Download " + fileName + " onFailure");
                                backup(vocas);
                            }
                        });
            }
        }
    }

    private void getDateString(final StorageReference storageReference, final File file, final IVocaFullPlayTTSItem voca, final ArrayList<IVocaFullPlayTTSItem> vocas) {
        storageReference.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {

                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        DLog.i(logTag, "getDateString for " + file.getName() + " onSuccess");
                        copyVoiceFileToLocalBackupFolder(storageMetadata.getCreationTimeMillis(), file, voca, vocas);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        DLog.i(logTag, "getDateString for " + file.getName() + " onFailure");
                        backup(vocas);
                    }
                });
    }

    private void copyVoiceFileToLocalBackupFolder(final long creationMillis, File file, final IVocaFullPlayTTSItem voca, final ArrayList<IVocaFullPlayTTSItem> vocas) {
        String backupFileName = BaseVoca.getBackupFileName(studyLang, voca.getVIVocaType(), voca.getVIVocaId(), uid, voca.getVIVoiceFileVersion(), creationMillis);
        File backupFile = BaseVoca.getBackupFileOnLocal(voiceFolder, studyLang, uid, backupFileName);
        try {
            BaseVoca.copyFile(file, backupFile);
            DLog.i(logTag, "Copy " + file.getName() + " to " + backupFileName + " onSuccess");
            saveVoiceFileToFirebaseBackupFolder(creationMillis, backupFile, voca, vocas);
        } catch (IOException e) {
            e.printStackTrace();
            DLog.i(logTag, "Copy " + file.getName() + " to " + backupFileName + " onFailure");
            backup(vocas);
        }
    }

    private void saveVoiceFileToFirebaseBackupFolder(final long creationMillis, final File backupFile, final IVocaFullPlayTTSItem voca, final ArrayList<IVocaFullPlayTTSItem> vocas) {
        String path = BaseVoca.getBackupPathOnFirebase(studyLang, uid);
        final String backupFileName = backupFile.getName();
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference()
                .child(path + backupFileName);
        Uri uri = Uri.fromFile(backupFile);
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("application/octet-stream")
                .build();
        UploadTask uploadTask = storageReference.putFile(uri, metadata);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                DLog.i(logTag, "Save " + backupFileName + " to Firebase onSuccess");
                saveBackupRecordingModelToFirestore(creationMillis, backupFileName, voca, vocas);
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                DLog.i(logTag, "Save " + backupFileName + " to Firebase onFailure");
                backup(vocas);
            }
        });
    }

    private void saveBackupRecordingModelToFirestore(final long creationMillis, final String backupFileName, final IVocaFullPlayTTSItem voca, final ArrayList<IVocaFullPlayTTSItem> vocas) {
        String id = backupFileName.substring(0, backupFileName.lastIndexOf("."));
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("DATE_STRING", DateUtils.getDateStringFormat().format(new Date(creationMillis)));
        dataMap.put("FILE_NAME", backupFileName);
        dataMap.put("ID", id);
        dataMap.put("VERSION", String.valueOf(voca.getVIVoiceFileVersion()));
        dataMap.put("VOCA_FILENAME", voca.getVIPath());
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("backup_voice/" + studyLang + "/" + uid);
        collectionReference.document(id).set(dataMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        DLog.i(logTag, "Save model for " + backupFileName + " to Firestore onSuccess");
                        backup(vocas);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        DLog.i(logTag, "Save model for " + backupFileName + " to Firestore onFailure");
                        backup(vocas);
                    }
                });
    }
}
