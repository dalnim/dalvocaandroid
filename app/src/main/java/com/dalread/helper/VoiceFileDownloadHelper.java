package com.dalread.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;

import com.dalread.BaseApplication;
import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.dialog.ConfirmDownloadVoiceFileDialog;
import com.dalread.dialog.ConfirmationDialog;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnDownloadFileListener;
import com.dalread.listener.OnVoiceFileInfoDownloadListener;
import com.dalread.model.VocaDownload;
import com.dalread.network.DalApiListener;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringJoiner;

public class VoiceFileDownloadHelper {
//    protected Activity activity;
    private Context context;
    private ArrayList<File> downloadFiles;
    private ArrayList<VocaDownload> serverVocaDownloads, localVocaDownloads;
    protected LinkedHashMap<IVocaFullPlayTTSItem, ArrayList<String>> itemMap;
    private OnDownloadFileListener onDownloadFileListener;
    private int downloadPos;
    private File voiceFolder;
    private BaseApplication application;
    protected SharedPreferencesDB sharedPreferences;
    private boolean isDownloadMyVoiceFile;

    public VoiceFileDownloadHelper(Context context, boolean isDownloadMyVoiceFile, OnDownloadFileListener onDownloadFileListener) {
        this.context = context;
        this.onDownloadFileListener = onDownloadFileListener;
        this.isDownloadMyVoiceFile = isDownloadMyVoiceFile;
        preInit();
        initListener();
    }

    public boolean isDownloadMyVoiceFile() {
        return isDownloadMyVoiceFile;
    }

    public void setDownloadMyVoiceFile(boolean downloadMyVoiceFile) {
        isDownloadMyVoiceFile = downloadMyVoiceFile;
    }

    private void initListener() {

    }

    private void preInit() {
        application = BaseApplication.getInstance();
        sharedPreferences = application.getSharedPref();
    }



    protected String getVoiceFileNameList(ArrayList<IVocaFullPlayTTSItem> vocaList) {
        String strFileNames = "";
        if (isDownloadMyVoiceFile) {
            if (!Utils.isEmpty(vocaList)) {
                IVocaFullPlayTTSItem voca = vocaList.get(0);
                strFileNames = BaseVoca.getMyOutputRecordingFileName(context, voca);
                itemMap.put(voca, new ArrayList<>(Arrays.asList(strFileNames)));
            }
        } else {
            strFileNames = getPreferredNativeSpeakerFileNames(vocaList);
        }
        return strFileNames;
    }
    protected String getPreferredNativeSpeakerFileNames(ArrayList<IVocaFullPlayTTSItem> vocaList) {
        StringJoiner sjFileNames = new StringJoiner(",");
        String strIds = sharedPreferences.getPreferredNativeSpeakers();
        String[] preferredNativeSpeakerIds = strIds.isEmpty() ? new String[0] : strIds.split(",");
        int studyLangId = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
        for (IVocaFullPlayTTSItem voca : vocaList) {
            int vocaType = voca.getVIVocaType();
            int vocaId = voca.getVIVocaId();
            ArrayList<String> fileNames = new ArrayList<>();
            for (String preferredNativeSpeakerId : preferredNativeSpeakerIds) {
                int nativeSpeakerId = Integer.parseInt(preferredNativeSpeakerId);
                String fileName = BaseVoca.getOutputRecordingFileName(studyLangId, vocaType, vocaId, nativeSpeakerId);
                sjFileNames.add(fileName);
                fileNames.add(fileName);
            }
            if (sharedPreferences.getIncludeMyVoice()) {
                int uid = UserUtil.getUserID(context);
                String fileName = BaseVoca.getOutputRecordingFileName(studyLangId, vocaType, vocaId, uid);
                sjFileNames.add(fileName);
            }
            DLog.d("", "getPreferredNativeSpeakerFileNames - add id=" + vocaId);
            itemMap.put(voca, fileNames);
        }
//        if (!strFileNames.isEmpty()) {
//            strFileNames = strFileNames.substring(1);
//        }
        return sjFileNames.toString();
    }



    public void getServerVoiceFileInfo(String voiceFileName,OnVoiceFileInfoDownloadListener onVoiceFileInfoDownloadListener) {
        if (Utils.isConnected(context) && !Utils.isEmpty(voiceFileName)) {
            application.getDalAiImpl().getMultipleVoiceFileVersion(
                    sharedPreferences.getUid(),
                    sharedPreferences.getLangStudyCode(),
                    voiceFileName,
                    new DalApiListener<List<VocaDownload>>() {
                        @Override
                        public void onSuccess(final List<VocaDownload> response) {
                            if (onVoiceFileInfoDownloadListener != null) {
                                if (!Utils.isEmpty(response)) {
                                    onVoiceFileInfoDownloadListener.onSuccess(response.get(0), voiceFileName);
                                } else {
                                    onVoiceFileInfoDownloadListener.onFailure(voiceFileName);
                                }
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                            if (onVoiceFileInfoDownloadListener != null)
                                onVoiceFileInfoDownloadListener.onFailure(voiceFileName);
                        }
                    }
            );
        }
    }

    protected void checkVersionAndDownload(String fileNames,OnVoiceFileInfoDownloadListener onVoiceFileInfoDownloadListener) {
        if (!fileNames.isEmpty() && Utils.isConnected(context)) {
            final String[] names = fileNames.split(",");
            BaseVoca.executeRealmTransaction(realm -> localVocaDownloads = (ArrayList<VocaDownload>) realm.copyFromRealm(
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
                            for (VocaDownload serverVocaDownload : response) {
                                if (serverVocaDownload.getVersion() > -1) {
                                    String name = serverVocaDownload.getName();
                                    File file = BaseVoca.getVoiceFileInApp(context, name);
                                    if (file.exists()) {
                                        VocaDownload localVocaDownload = getLocalVocaDownloadFromList(name);
                                        if (localVocaDownload == null
                                                || localVocaDownload.getVersion() < serverVocaDownload.getVersion()) {
                                            downloadFiles.add(file);
                                            downloadSize += serverVocaDownload.getFileSize();
                                            serverVocaDownloads.add(serverVocaDownload);
                                        }
                                    } else {
                                        downloadFiles.add(file);
                                        downloadSize += serverVocaDownload.getFileSize();
                                        serverVocaDownloads.add(serverVocaDownload);
                                    }
                                    if (onVoiceFileInfoDownloadListener != null) {
                                        onVoiceFileInfoDownloadListener.onSuccess(serverVocaDownload, name);
                                    }
                                } else if (serverVocaDownload.getReplacementVersion() > -1) {
                                    final String replacementName = serverVocaDownload.getReplacementName();
                                    File replacementFile = BaseVoca.getVoiceFileOnLocal(voiceFolder, replacementName);
                                    if (replacementFile.exists()) {
                                        final VocaDownload[] localVocaDownload = new VocaDownload[1];
                                        BaseVoca.executeRealmTransaction(realm -> localVocaDownload[0] = realm.copyFromRealm(
                                                realm.where(VocaDownload.class).equalTo("name", replacementName).findFirst()
                                        ));
                                        if (localVocaDownload[0] == null
                                                || localVocaDownload[0].getVersion() < serverVocaDownload.getReplacementVersion()) {
                                            downloadFiles.add(replacementFile);
                                            downloadSize += serverVocaDownload.getFileSize();
                                            serverVocaDownloads.add(serverVocaDownload);
                                        } else {
                                            copyFile(replacementFile, serverVocaDownload.getName());
                                        }
                                    } else {
                                        downloadFiles.add(replacementFile);
                                        downloadSize += serverVocaDownload.getFileSize();
                                        serverVocaDownloads.add(serverVocaDownload);
                                    }
                                }
                            }
                            if (onDownloadFileListener != null) {
                                onDownloadFileListener.fileCountToDownload(downloadFiles.size());
                            }

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
                            if (onDownloadFileListener != null) {
                                onDownloadFileListener.fileCountToDownload(0);
                            }
                            DLog.i("", "getVoiceFileVersion onFailure");
                        }
                    }
            );
        } else {
            if (onDownloadFileListener != null) {
                onDownloadFileListener.fileCountToDownload(0);
            }
            DLog.i("", "No file names or no internet");
        }
    }

    protected void checkVersionAndDownload(String fileNames) {
        if (!fileNames.isEmpty() && Utils.isConnected(context)) {
            final String[] names = fileNames.split(",");
            BaseVoca.executeRealmTransaction(realm -> localVocaDownloads = (ArrayList<VocaDownload>) realm.copyFromRealm(
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
                            for (VocaDownload serverVocaDownload : response) {
                                if (serverVocaDownload.getVersion() > -1) {
                                    String name = serverVocaDownload.getName();
                                    File file = BaseVoca.getVoiceFileInApp(context, name);
                                    if (file.exists()) {
                                        VocaDownload localVocaDownload = getLocalVocaDownloadFromList(name);
                                        if (localVocaDownload == null
                                                || localVocaDownload.getVersion() < serverVocaDownload.getVersion()) {
                                            downloadFiles.add(file);
                                            downloadSize += serverVocaDownload.getFileSize();
                                            serverVocaDownloads.add(serverVocaDownload);
                                        }
                                    } else {
                                        downloadFiles.add(file);
                                        downloadSize += serverVocaDownload.getFileSize();
                                        serverVocaDownloads.add(serverVocaDownload);
                                    }
                                } else if (serverVocaDownload.getReplacementVersion() > -1) {
                                    final String replacementName = serverVocaDownload.getReplacementName();
                                    File replacementFile = BaseVoca.getVoiceFileOnLocal(voiceFolder, replacementName);
                                    if (replacementFile.exists()) {
                                        final VocaDownload[] localVocaDownload = new VocaDownload[1];
                                        BaseVoca.executeRealmTransaction(realm -> localVocaDownload[0] = realm.copyFromRealm(
                                                realm.where(VocaDownload.class).equalTo("name", replacementName).findFirst()
                                        ));
                                        if (localVocaDownload[0] == null
                                                || localVocaDownload[0].getVersion() < serverVocaDownload.getReplacementVersion()) {
                                            downloadFiles.add(replacementFile);
                                            downloadSize += serverVocaDownload.getFileSize();
                                            serverVocaDownloads.add(serverVocaDownload);
                                        } else {
                                            copyFile(replacementFile, serverVocaDownload.getName());
                                        }
                                    } else {
                                        downloadFiles.add(replacementFile);
                                        downloadSize += serverVocaDownload.getFileSize();
                                        serverVocaDownloads.add(serverVocaDownload);
                                    }
                                }
                            }
                            if (onDownloadFileListener != null) {
                                onDownloadFileListener.fileCountToDownload(downloadFiles.size());
                            }

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
                            if (onDownloadFileListener != null) {
                                onDownloadFileListener.fileCountToDownload(0);
                            }
                            DLog.i("", "getVoiceFileVersion onFailure");
                        }
                    }
            );
        } else {
            if (onDownloadFileListener != null) {
                onDownloadFileListener.fileCountToDownload(0);
            }
            DLog.i("", "No file names or no internet");
        }
    }

    private VocaDownload getLocalVocaDownloadFromList(String name) {
        for (VocaDownload localVocaDownload : localVocaDownloads) {
            if (localVocaDownload.getName().equals(name)) {
                return localVocaDownload;
            }
        }
        return null;
    }

    private void downloadFileFromFirebase() {
        //This is for in the Real Activity
//        if (isFinishing() || isDestroyed()) {
//            DLog.i("", "Exit screen, don't download anymore");
//        } else
        if (++downloadPos < downloadFiles.size()) {
            DLog.i("", "downloadFileFromFirebase: " + (downloadPos + 1) + "/" + downloadFiles.size());
            final File file = downloadFiles.get(downloadPos);
            final String fileName = file.getName();
            BaseStorageUtil.createFolder(file.getParent()); //If I don't make folder for the voice file to download, downloading is failed.
            Uri uri = Uri.fromFile(file);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(BaseVoca.getVoiceFolderPathOnFirebase(fileName) + uri.getLastPathSegment());
            storageReference.getFile(file)
                    .addOnSuccessListener(taskSnapshot -> {
                        DLog.i("", "Download " + fileName + " onSuccess");
                        saveServerVocaDownloadAndCopyFile(file);
                        downloadFileFromFirebase();
                        if (onDownloadFileListener != null) {
                            onDownloadFileListener.onDownloadOneItemSuccess(downloadPos, fileName);
                        }
                    })
                    .addOnFailureListener(e -> {
                        DLog.i("", "Download " + fileName + " onFailure");
                        saveServerVocaDownloadAndCopyFile(null);
                        downloadFileFromFirebase();
                        if (onDownloadFileListener != null) {
                            onDownloadFileListener.onDownloadOneItemFailure(downloadPos, fileName);
                        }
                    });
        } else {
            DLog.i("", "Download finished");
            if (onDownloadFileListener != null) {
                onDownloadFileListener.onFinish();
                onDownloadFileListener = null;
            }
        }
    }

    private void saveServerVocaDownloadAndCopyFile(File srcFile) {
        if (downloadPos > -1 && downloadPos < serverVocaDownloads.size()) {
            final VocaDownload vocaDownload = serverVocaDownloads.get(downloadPos);
            if (srcFile != null && srcFile.getName().equals(vocaDownload.getReplacementName())) {
                final VocaDownload vD = new VocaDownload();
                vD.setName(vocaDownload.getReplacementName());
                vD.setVersion(vocaDownload.getReplacementVersion());
                BaseVoca.executeRealmTransaction(realm -> realm.copyToRealmOrUpdate(vD));
                copyFile(srcFile, vocaDownload.getName());
            } else {
                BaseVoca.executeRealmTransaction(realm -> realm.copyToRealmOrUpdate(vocaDownload));
            }
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

    public void preparePlayStudentVoice(IVocaFullPlayTTSItem voca) {
        ArrayList<IVocaFullPlayTTSItem> vocas = new ArrayList<>();
        vocas.add(voca);
        preparePlayStudentVoice(vocas);
    }

    public void preparePlayStudentVoice(ArrayList<IVocaFullPlayTTSItem> vocas) {
//        this.vocaList = vocas;
        itemMap = new LinkedHashMap<>();
        downloadFiles = new ArrayList<>();
        downloadPos = -1;
        serverVocaDownloads = new ArrayList<>();
        String strFileNames = "";
        for (IVocaFullPlayTTSItem voca : vocas) {
            String fileName = voca.getVIPath();
            strFileNames += "," + fileName;
            ArrayList<String> fileNames = new ArrayList<>();
            fileNames.add(fileName);
            itemMap.put(voca, fileNames);
        }
        if (!strFileNames.isEmpty()) {
            strFileNames = strFileNames.substring(1);
        }
        checkVersionAndDownload(strFileNames);
    }

    public void preparePlay1stNativeSpeakerAndMyVoiceOnce(IVocaFullPlayTTSItem voca) {
        itemMap = new LinkedHashMap<>();
        String strIds = sharedPreferences.getPreferredNativeSpeakers();
        int nativeSpeakerId;
        if (strIds.isEmpty()) {
            nativeSpeakerId = 0;
        } else {
            String[] preferredNativeSpeakerIds = strIds.split(",");
            nativeSpeakerId = Integer.parseInt(preferredNativeSpeakerIds[0]);
        }
        ArrayList<String> fileNames = new ArrayList<>();
        if (nativeSpeakerId > 0) {
            int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
            int vocaType = voca.getVIVocaType();
            int vocaId = voca.getVIVocaId();
            String fileName = BaseVoca.getOutputRecordingFileName(studyLang, vocaType, vocaId, nativeSpeakerId);
            fileNames.add(fileName);
        }
        itemMap.put(voca, fileNames);
    }

    public void checkVersionAndDownload(ArrayList<IVocaFullPlayTTSItem> vocas) {
        downloadFiles = new ArrayList<>();
        downloadPos = -1;
        serverVocaDownloads = new ArrayList<>();
        String strFileNames = "";
        String strIds = sharedPreferences.getPreferredNativeSpeakers();
        String[] preferredNativeSpeakerIds = strIds.isEmpty() ? new String[0] : strIds.split(",");
        int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
        for (IVocaFullPlayTTSItem voca : vocas) {
            int vocaType = voca.getVIVocaType();
            int vocaId = voca.getVIVocaId();
            for (String preferredNativeSpeakerId : preferredNativeSpeakerIds) {
                int nativeSpeakerId = Integer.parseInt(preferredNativeSpeakerId);
                String fileName = BaseVoca.getOutputRecordingFileName(studyLang, vocaType, vocaId, nativeSpeakerId);
                strFileNames += "," + fileName;
            }
            if (sharedPreferences.getIncludeMyVoice()) {
                int uid = UserUtil.getUserID(context);
                String fileName = BaseVoca.getOutputRecordingFileName(studyLang, vocaType, vocaId, uid);
                strFileNames += "," + fileName;
            }
        }
        if (!strFileNames.isEmpty()) {
            strFileNames = strFileNames.substring(1);
        }
        checkVersionAndDownload(strFileNames);
    }

    public void checkVersionAndDownloadByUid(ArrayList<IVocaFullPlayTTSItem> vocas, String strIdListByComma,OnVoiceFileInfoDownloadListener onVoiceFileInfoDownloadListener) {
        StringJoiner sjFileNames = new StringJoiner(",");
        downloadFiles = new ArrayList<>();
        downloadPos = -1;
        serverVocaDownloads = new ArrayList<>();
        String[] preferredNativeSpeakerIds = strIdListByComma.split(",");
        int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
        for (IVocaFullPlayTTSItem voca : vocas) {
            int vocaType = voca.getVIVocaType();
            int vocaId = voca.getVIVocaId();
            for (String preferredNativeSpeakerId : preferredNativeSpeakerIds) {
                int nativeSpeakerId = Integer.parseInt(preferredNativeSpeakerId);
                String fileName = BaseVoca.getOutputRecordingFileName(studyLang, vocaType, vocaId, nativeSpeakerId);
                sjFileNames.add(fileName);
            }
        }
        checkVersionAndDownload(sjFileNames.toString(), onVoiceFileInfoDownloadListener);
    }

    public void preparePlayVocaNoDownload(IVocaFullPlayTTSItem voca) {
        ArrayList<IVocaFullPlayTTSItem> vocas = new ArrayList<>();
        vocas.add(voca);
        preparePlayVocaNoDownload(vocas);
    }

    public void preparePlayVocaNoDownload(ArrayList<IVocaFullPlayTTSItem> vocas) {
        itemMap = new LinkedHashMap<>();
        String strIds = sharedPreferences.getPreferredNativeSpeakers();
        String[] preferredNativeSpeakerIds = strIds.isEmpty() ? new String[0] : strIds.split(",");
        int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
        for (IVocaFullPlayTTSItem voca : vocas) {
            int vocaType = voca.getVIVocaType();
            int vocaId = voca.getVIVocaId();
            ArrayList<String> fileNames = new ArrayList<>();
            for (String preferredNativeSpeakerId : preferredNativeSpeakerIds) {
                int nativeSpeakerId = Integer.parseInt(preferredNativeSpeakerId);
                String fileName = BaseVoca.getOutputRecordingFileName(studyLang, vocaType, vocaId, nativeSpeakerId);
                fileNames.add(fileName);
            }
            itemMap.put(voca, fileNames);
        }
    }

}
