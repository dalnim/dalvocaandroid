package com.dalread.base;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dalread.dialog.ConfirmDownloadVoiceFileDialog;
import com.dalread.dialog.ConfirmationDialog;
import com.dalread.dialog.DownloadingVoiceFilesDialog;
import com.dalread.helper.PlayVocaNativeSpeakerHelper;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnOpenNewScreen;
import com.dalread.model.VocaDownload;
import com.dalread.network.DalApiListener;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class BasePlayVocaNativeSpeakerActivity extends BaseVocaActivity
        implements OnOpenNewScreen {

    protected Context context;
    protected PlayVocaNativeSpeakerHelper playVocaHelper;
    private boolean backgroundMode;
    private ArrayList<IVocaFullPlayTTSItem> vocas;
    private ArrayList<File> downloadFiles;
    private int downloadPos;
    private ArrayList<VocaDownload> serverVocaDownloads, localVocaDownloads;
    private File voiceFolder;
    protected DownloadingVoiceFilesDialog downloadingVoiceFilesDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        playVocaHelper = application.getPlayVocaNativeSpeakerHelper();
        EnumLanguage studyLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage());
        playVocaHelper.initStudyTTS(studyLanguage.getLocale(), 1);
        voiceFolder = Voca.getVoiceFolderOnLocal(context);
        downloadingVoiceFilesDialog = new DownloadingVoiceFilesDialog(context);
        updateBackgroundMode();
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            stopPlayVoca();
        } else if (!backgroundMode) {
            playVocaHelper.stop();
        }

        super.onPause();
    }

    @Override
    public void onOpen() {
        // stop playing vocas from current screen when opening a new screen
        if (playVocaHelper != null) {
            playVocaHelper.stop();
            playVocaHelper.clearOnPlayVocaListener();
        }
    }

    protected void updateBackgroundMode() {
        backgroundMode = sharedPreferences.getBackgroundMode();
    }

    public PlayVocaNativeSpeakerHelper getPlayVocaHelper() {
        return playVocaHelper;
    }

    public void stopPlayVoca() {
        if (playVocaHelper != null) {
            playVocaHelper.clearOnPlayVocaListener();
            playVocaHelper.stop();
        }
    }

    public void startPlayVoca(IVocaFullPlayTTSItem voca) {
        ArrayList<IVocaFullPlayTTSItem> vocas = new ArrayList<>();
        vocas.add(voca);
        startPlayVoca(vocas);
    }

    public void startPlayVoca(ArrayList<IVocaFullPlayTTSItem> vocas) {
        if (playVocaHelper != null) {
            playVocaHelper.play(vocas);
        }
    }

    public void checkVersionAndDownload(ArrayList<IVocaFullPlayTTSItem> vocas) {
        this.vocas = vocas;
        downloadFiles = new ArrayList<>();
        downloadPos = -1;
        serverVocaDownloads = new ArrayList<>();

        checkVersionAndDownload(getFileNames());
    }

    private String getFileNames() {
        StringBuilder fileNames = new StringBuilder();
        int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
        for (IVocaFullPlayTTSItem voca : vocas) {
            int vocaType = voca.getVIVocaType();
            int vocaId = voca.getVIVocaId();
            int uid = getUserID();
            String fileName = Voca.getOutputRecordingFileName(studyLang, vocaType, vocaId, uid);
            fileNames.append(",").append(fileName);
        }
        if (fileNames.length() > 0) {
            fileNames = new StringBuilder(fileNames.substring(1));
        }
        return fileNames.toString();
    }

    private void checkVersionAndDownload(String fileNames) {
        if (!fileNames.isEmpty() && Utils.isConnected(context)) {
            final String[] names = fileNames.split(",");
            Voca.executeRealmTransaction(realm -> localVocaDownloads = (ArrayList<VocaDownload>) realm.copyFromRealm(
                    realm.where(VocaDownload.class).in("name", names).findAll()
            ));
            application.getDalAiImpl().getMultipleVoiceFileVersion(
                    sharedPreferences.getUid(),
                    sharedPreferences.getLangStudyCode(),
                    fileNames,
                    new DalApiListener<List<VocaDownload>>() {

                        @Override
                        public void onSuccess(final List<VocaDownload> response) {
                            int downloadSize = 0;
                            for (VocaDownload vocaDownload : response) {
                                String name = vocaDownload.getName();
                                VocaDownload localVocaDownload = null;
                                for (VocaDownload vD : localVocaDownloads) {
                                    if (vD.getName().equals(name)) {
                                        localVocaDownload = vD;
                                        break;
                                    }
                                }
                                if (vocaDownload.getVersion() > -1) {
                                    File file = Voca.getVoiceFileOnLocal(voiceFolder, name);
                                    if (file.exists()) {
                                        if (localVocaDownload == null
                                                || localVocaDownload.getVersion() < vocaDownload.getVersion()) {
                                            downloadFiles.add(file);
                                            downloadSize += vocaDownload.getFileSize();
                                            serverVocaDownloads.add(vocaDownload);
                                        }
                                    } else {
                                        downloadFiles.add(file);
                                        downloadSize += vocaDownload.getFileSize();
                                        serverVocaDownloads.add(vocaDownload);
                                    }
                                }
                            }
                            downloadingVoiceFilesDialog.setMax(downloadFiles.size());
                            DLog.i(getLogTag(), "downloadSize = " + downloadSize);
                            if (!Utils.hasWifiConnected(context) && downloadSize > Constant.DOWNLOAD_CONFIRM_SIZE) {
                                new ConfirmDownloadVoiceFileDialog(
                                        context,
                                        new ConfirmationDialog.OnDialogClickListener() {

                                            @Override
                                            public void onPositive(DialogInterface dialog) {
                                                dialog.dismiss();
                                                downloadFileFromFirebase();
                                            }

                                            @Override
                                            public void onNegative(DialogInterface dialog) {
                                                dialog.dismiss();
                                            }
                                        }
                                ).show(downloadSize);
                            } else {
                                downloadFileFromFirebase();
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                            DLog.i(getLogTag(), "getVoiceFileVersion onFailure");
                        }
                    }
            );
        } else {
            DLog.i(getLogTag(), "No file names or no internet");
        }
    }

    private void downloadFileFromFirebase() {
        if (isFinishing() || isDestroyed()) {
            DLog.i(getLogTag(), "Exit screen, don't download anymore");
            onDownloadFileFromFirebaseFinish();
        } else if (++downloadPos < downloadFiles.size()) {
            int progress = downloadPos + 1;
            String strProgress = progress + "/" + downloadFiles.size();
            DLog.i(getLogTag(), "downloadFileFromFirebase: " + strProgress);
            downloadingVoiceFilesDialog.setProgress(progress);
            File file = downloadFiles.get(downloadPos);
            final String fileName = file.getName();
            Uri uri = Uri.fromFile(file);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(Voca.getVoiceFolderPathOnFirebase(fileName) + uri.getLastPathSegment());
            storageReference.getFile(file)
                    .addOnSuccessListener(taskSnapshot -> {
                        DLog.i(getLogTag(), "Download " + fileName + " onSuccess");
                        saveServerVocaDownload();
                        downloadFileFromFirebase();
                    })
                    .addOnFailureListener(e -> {
                        DLog.i(getLogTag(), "Download " + fileName + " onFailure");
                        saveServerVocaDownload();
                        downloadFileFromFirebase();
                    });
        } else {
            DLog.i(getLogTag(), "Download finished");
            onDownloadFileFromFirebaseFinish();
        }
    }

    private void saveServerVocaDownload() {
        if (downloadPos < serverVocaDownloads.size()) {
            final VocaDownload vocaDownload = serverVocaDownloads.get(downloadPos);
            Voca.executeRealmTransaction(realm -> realm.copyToRealmOrUpdate(vocaDownload));
        }
    }

    protected void onDownloadFileFromFirebaseFinish() {
        if (downloadingVoiceFilesDialog != null) {
            downloadingVoiceFilesDialog.dismiss();
        }
    }

    protected boolean playMyVoiceOnce(IVocaFullPlayTTSItem voca) {
        if (playVocaHelper != null) {
            return playVocaHelper.playMyVoiceOnce(voca);
        }
        return false;
    }
}
