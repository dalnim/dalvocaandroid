package com.dalread.composition;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.speech.tts.UtteranceProgressListener;
import android.widget.ImageView;

import com.dalread.BaseApplication;
import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.dialog.ConfirmDownloadVoiceFileDialog;
import com.dalread.dialog.ConfirmationDialog;
import com.dalread.helper.PlayTTSHelper;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnDownloadFinishListener;
import com.dalread.listener.OnPlaylistItemClickListener;
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

public abstract class AbstractPlayTTS {
    protected Activity activity;
    public PlayTTSHelper playTTSHelper;
    private ArrayList<File> downloadFiles;
    private boolean isDownloadFiles = false;
    private ArrayList<VocaDownload> serverVocaDownloads, localVocaDownloads;
    protected LinkedHashMap<IVocaFullPlayTTSItem, ArrayList<String>> itemMap;
    private OnDownloadFinishListener onDownloadFinishListener;
    private OnPlaylistItemClickListener onPlaylistItemClickListener;
    private UtteranceProgressListener motherTongueListener;
    private UtteranceProgressListener studyListener;
//    private StudyLangBase studyLangBase;
    private int downloadPos;
    private File voiceFolder;
    private boolean isPlayMyVoiceOnce;
    private BaseApplication application;
    protected SharedPreferencesDB sharedPreferences;

    public int playingState;
    private static final int STATE_STOP = 0;
    private static final int STATE_PAUSE = STATE_STOP + 1;
    private static final int STATE_PLAY_SINGLE = STATE_PAUSE + 1;
    private static final int STATE_PLAY_MULTI = STATE_PLAY_SINGLE + 1;
    public AbstractPlayTTS(Activity activity) {
        this.activity = activity;
        preInit();
        initPlayTTSHelper();
        initListener();
        stopPlayVoca();
    }

    private void initListener() {
        onPlaylistItemClickListener = new OnPlaylistItemClickListener() {
            @Override
            public void onItemClick(Object item) {
                int a = 0;
                a = 1;
            }

            @Override
            public void onPlayClick(Object item) {
                int a = 0;
                a = 1;
            }
        };
    }

    private void preInit() {
        application = BaseApplication.getInstance();
        sharedPreferences = application.getSharedPref();
    }

    public void stopPlayVoca() {
        if (playTTSHelper != null) {
            playTTSHelper.clearListener();
            playTTSHelper.stop();
        }
        clearDownloadList();
    }

    public void stopMyVoiceOnce() {
        stopPlayVoca();
        playingState = STATE_STOP;
    }

    public void clearDownloadList() {
        if (downloadFiles != null) {
            downloadFiles.clear();
        }
        if (serverVocaDownloads != null) {
            serverVocaDownloads.clear();
        }
    }
    
    protected void onStart() {
        if (playTTSHelper != null) {
            playTTSHelper.stop();
            playTTSHelper.clearListener();
        }
    }

//    public void preparePlayVoca(IVocaFullPlayTTSItem voca) {
//        preparePlayVoca(voca, false);
//    }

//    public void preparePlayVocaSingle(IVocaFullPlayTTSItem voca) {
//        preparePlayVoca(voca, false);
//    }

    public void preparePlayMyVoiceOnce(IVocaFullPlayTTSItem voca) {
        ArrayList<IVocaFullPlayTTSItem> vocas = new ArrayList<>();
        vocas.add(voca);
        playTTSHelper.isPlayTTSAtFirst = false;
        playTTSHelper.isPlayMyVoiceOnce = true; //밑의 isPlayMyVoiceOnce와 차이는 뭘까?
        isPlayMyVoiceOnce = true;
        playingState = STATE_STOP; //WIll play only once so set STOP here.
        preparePlayVoca(vocas, false);
    }

    public void preparePlayVocaSingle(IVocaFullPlayTTSItem voca) {
        ArrayList<IVocaFullPlayTTSItem> vocas = new ArrayList<>();
        vocas.add(voca);
        playTTSHelper.isPlayMyVoiceOnce = false;
        isPlayMyVoiceOnce = false;
        preparePlayVoca(vocas, false);
    }

    public void preparePlayVoca(ArrayList<IVocaFullPlayTTSItem> vocas) {
        playTTSHelper.isPlayMyVoiceOnce = false;
        isPlayMyVoiceOnce = false;
        preparePlayVoca(vocas, true);
    }

    public void preparePlayVoca(ArrayList<IVocaFullPlayTTSItem> vocaList, boolean isPlayingMultiFiles) {
        DLog.d("", "preparePlayVoca - size=" + vocaList.size());
//        this.vocaList = vocas;
        itemMap = new LinkedHashMap<>();
        downloadFiles = new ArrayList<>();
        downloadPos = -1;
        serverVocaDownloads = new ArrayList<>();
        String strFileNames = getVoiceFileNameList(vocaList);
        DLog.d("", "strFileNames=" + strFileNames + " - size=" + itemMap.size());
        playTTSHelper.setPlayingMultiFiles(isPlayingMultiFiles);
        playTTSHelper.play(itemMap);
        checkVersionAndDownload(strFileNames);
    }

//    protected String getPreferredNativeSpeakerFileNames() {
//        String strFileNames = "";
//        String strIds = sharedPreferences.getPreferredNativeSpeakers();
//        String[] preferredNativeSpeakerIds = strIds.isEmpty() ? new String[0] : strIds.split(",");
//        int studyLangId = EnumLanguage.getStudyLanguageId(activity);
//        for (IVocaFullPlayTTSItem voca : vocas) {
//            int vocaType = voca.getVIVocaType();
//            int vocaId = voca.getVIVocaId();
//            ArrayList<String> fileNames = new ArrayList<>();
//            for (String preferredNativeSpeakerId : preferredNativeSpeakerIds) {
//                int nativeSpeakerId = Integer.parseInt(preferredNativeSpeakerId);
//                String fileName = BaseVoca.getOutputRecordingFileName(studyLangId, vocaType, vocaId, nativeSpeakerId);
//                strFileNames += "," + fileName;
//                fileNames.add(fileName);
//            }
//            if (sharedPreferences.getIncludeMyVoice()) {
//                int uid = UserUtil.getUserID(activity);
//                String fileName = BaseVoca.getOutputRecordingFileName(studyLangId, vocaType, vocaId, uid);
//                strFileNames += "," + fileName;
//            }
//            DLog.d("", "getPreferredNativeSpeakerFileNames - add id=" + vocaId);
//            itemMap.put(voca, fileNames);
//        }
//        if (!strFileNames.isEmpty()) {
//            strFileNames = strFileNames.substring(1);
//        }
//        return strFileNames;
//    }

    protected String getVoiceFileNameList(ArrayList<IVocaFullPlayTTSItem> vocaList) {
        String strFileNames = "";
        if (isPlayMyVoiceOnce) {
            if (!Utils.isEmpty(vocaList)) {
                IVocaFullPlayTTSItem voca = vocaList.get(0);
                strFileNames = BaseVoca.getMyOutputRecordingFileName(activity, voca);
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
                int uid = UserUtil.getUserID(activity);
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

    protected void checkVersionAndDownload(String fileNames) {
        if (isPlayMyVoiceOnce)
            return;

        if (isDownloadFiles && !fileNames.isEmpty() && Utils.isConnected(activity)) {
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
                                    File file = BaseVoca.getVoiceFileInApp(activity, name);
                                    if (file.exists()) {
                                        VocaDownload localVocaDownload = getLocalVocaDownload(name);
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
                            if (!Utils.hasWifiConnected(activity) && downloadSize > Constant.DOWNLOAD_CONFIRM_SIZE) {
                                new ConfirmDownloadVoiceFileDialog(
                                        activity,
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
                            DLog.i("", "getVoiceFileVersion onFailure");
                        }
                    }
            );
        } else {
            DLog.i("", "No file names or no internet");
        }
    }

    private VocaDownload getLocalVocaDownload(String name) {
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
                    })
                    .addOnFailureListener(e -> {
                        DLog.i("", "Download " + fileName + " onFailure");
                        saveServerVocaDownloadAndCopyFile(null);
                        downloadFileFromFirebase();
                    });
        } else {
            DLog.i("", "Download finished");
            if (onDownloadFinishListener != null) {
                onDownloadFinishListener.onFinish();
                onDownloadFinishListener = null;
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
        playTTSHelper.playStudentVoice(itemMap);
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
        playTTSHelper.play1stNativeSpeakerAndMyVoiceOnce(itemMap);
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
                int uid = UserUtil.getUserID(activity);
                String fileName = BaseVoca.getOutputRecordingFileName(studyLang, vocaType, vocaId, uid);
                strFileNames += "," + fileName;
            }
        }
        if (!strFileNames.isEmpty()) {
            strFileNames = strFileNames.substring(1);
        }
        checkVersionAndDownload(strFileNames);
    }

    public void checkVersionAndDownloadByUid(ArrayList<IVocaFullPlayTTSItem> vocas, String strIdListByComma) {
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
        checkVersionAndDownload(sjFileNames.toString());
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
        playTTSHelper.setPlayEndlessly(true);
        playTTSHelper.play(itemMap);
    }

//    public void prepareDownloadVoca(IVocaFullPlayTTSItem voca, OnDownloadFinishListener onDownloadFinishListener) {
//        vocaList = new ArrayList<>();
//        vocaList.add(voca);
//        this.onDownloadFinishListener = onDownloadFinishListener;
//        itemMap = new LinkedHashMap<>();
//        downloadFiles = new ArrayList<>();
//        downloadPos = -1;
//        serverVocaDownloads = new ArrayList<>();
//        String strFileNames = getPreferredNativeSpeakerFileNames();
//        checkVersionAndDownload(strFileNames);
//    }

    protected void initPlayTTSHelper() {
        playTTSHelper = application.getPlayTTSHelper();
//        playTTSHelper = application.getPlayVocaHelper();
        voiceFolder = BaseVoca.getVoiceFolderInApp(activity);

        playTTSHelper.initMotherTongueTTS();

//        EnumLanguage studyLanguage = studyLangBase.getEnumLanguage();// EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage());
//        int ttsSpeed = studyLangBase.getTTSSpeedInSetting(activity);
//
//        if (studyLanguage == EnumLanguage.CHINESE_SIMPLIFIED) {
//            ttsSpeed = sharedPreferences.getSettingTTSSpeedChinese();
//        } else if (studyLanguage == EnumLanguage.ENGLISH) {
//            ttsSpeed = sharedPreferences.getSettingTTSSpeedEnglish();
//        } else if (studyLanguage == EnumLanguage.JAPANESE) {
//            ttsSpeed = sharedPreferences.getSettingTTSSpeedJapanese();
//        } else if (studyLanguage == EnumLanguage.KOREAN) {
//            ttsSpeed = sharedPreferences.getSettingTTSSpeedKorean();
//        } else if (studyLanguage == EnumLanguage.HANJA) {
//            ttsSpeed = sharedPreferences.getSettingTTSSpeedHanja();
//        }

//        playTTSHelper.initStudyTTS(
//                studyLanguage.getLocale(),
//                Integer.parseInt(sharedPreferences.getReadCount()),
//                studyLangBase.getTTSSpeedInSetting(activity)
//        );
        playTTSHelper.initStudyTTS(Integer.parseInt(sharedPreferences.getReadCount()));
//        playTTSHelper.setIncludeMyVoice(sharedPreferences.getIncludeMyVoice());
        setIncludeMeaning();
        playTTSHelper.initMediaPlayer();

//        if (!playVocaHelper.hasMotherTongueListener()) {
//            playVocaHelper.setMotherTongueListener(new UtteranceProgressListener() {
//
//                @Override
//                public void onStart(String utteranceId) {
////                    updateItemStatus(utteranceId, true);
//                }
//
//                @Override
//                public void onDone(String utteranceId) {
//                }
//
//                @Override
//                public void onError(String utteranceId) {
//                }
//            });
//        }
//        if (!playVocaHelper.hasStudyListener()) {
//            playVocaHelper.setStudyListener(new UtteranceProgressListener() {
//
//                @Override
//                public void onStart(String utteranceId) {
//                }
//
//                @Override
//                public void onDone(String utteranceId) {
////                    updateItemStatus(utteranceId, false);
//                }
//
//                @Override
//                public void onError(String utteranceId) {
////                    updateItemStatus(utteranceId, false);
//                }
//            });
//        }
    }

    //AraPlayer only?
    private void handleTTSPlay(IVocaFullPlayTTSItem model, boolean isAutoPlay) {
        DLog.d("", "handlePlay");
        boolean isTTSPlaying = model.isVIPlaying();
        playTTSHelper.stop();
        if (!isTTSPlaying) {
//            preparePlayVoca(model);
            playSingle(model);
        }
    }
    //AraPlayer only?
    private void stopTTSPlaying(IVocaFullPlayTTSItem model) {
        boolean isTTSPlaying = model.isVIPlaying();
        if (isTTSPlaying) {
            playTTSHelper.stop();
        }

    }



    public void setIncludeMeaning() {
        setIncludeMeaning(sharedPreferences.getIncludeMeaning());
    }

    public void setIncludeMeaning(boolean isIncludeMeaning) {
        playTTSHelper.setIncludeMeaning(isIncludeMeaning);
    }

    public void stop() {
        playTTSHelper.stop();
    }

    public void pause() {
        playTTSHelper.pause();
    }

    public void resume(int pos) {
        playTTSHelper.resume(pos);
    }

    public int getPlayingState() {
        return playingState;
    }

    public void setPlayingState(int playingState) {
        this.playingState = playingState;
    }

    public void playMyVoiceOnce(IVocaFullPlayTTSItem item) {
        preparePlayMyVoiceOnce(item);
        playingState = STATE_PLAY_SINGLE;
    }
    public void playSingle(IVocaFullPlayTTSItem item) {
        preparePlayVocaSingle(item);
        playingState = STATE_PLAY_SINGLE;
    }

    public void playPlaylist(ArrayList<IVocaFullPlayTTSItem> playlistItems) {
        preparePlayVoca(playlistItems);
        playingState = STATE_PLAY_MULTI;
    }

    public void pausePlaylist() {
        pause();
        playingState = STATE_PAUSE;

    }

    public void resumePlaylist(int pos) {
        resume(pos);
        playingState = STATE_PLAY_MULTI;
    }

    public void stopPlaylist() {
        stop();
        clearDownloadList();
        playingState = STATE_STOP;
    }

    public boolean canPlay() {
        return isPlayStatePause() || isPlayStateStop();
    }
    public boolean isPlaying() {
        return isPlayStatePlaySingle() || isPlayStatePlayMulti();
    }
    public boolean isPlayStateStop() {
        return playingState == STATE_STOP;
    }
    public boolean isPlayStatePause() {
        return playingState == STATE_PAUSE;
    }
    public boolean isPlayStatePlaySingle() {
        return playingState == STATE_PLAY_SINGLE;
    }
    public boolean isPlayStatePlayMulti() {
        return playingState == STATE_PLAY_MULTI;
    }

    public void onSpeakerClick(IVocaFullPlayTTSItem vocaItem, ImageView imageView) {
        if ((isPlayStateStop()) || (isPlayStatePause())) {
            startPlayTTSBySpeakerClick(vocaItem, imageView);
        } else if (isPlayStatePlaySingle()) {
            stopPlayTTSBySpeakerClick(vocaItem, imageView);
        }
    }

    private void startPlayTTSBySpeakerClick(IVocaFullPlayTTSItem vocaItem, ImageView imageView) {
        playSingle(vocaItem);
        BaseVoca.setIsPlayingTTS(vocaItem, true);
        BaseVoca.updateIconSpeaker(imageView, vocaItem);
    }

    private void stopPlayTTSBySpeakerClick(IVocaFullPlayTTSItem vocaItem, ImageView imageView) {
        stopPlaylist();
        BaseVoca.setIsPlayingTTS(vocaItem, false);
        BaseVoca.updateIconSpeaker(imageView, vocaItem);
    }

    public void destroyPlayVocaHelper() {
        playTTSHelper.destroy();
        playTTSHelper = null;
    }

    public void setDownloadFiles(boolean downloadFiles) {
        isDownloadFiles = downloadFiles;
    }
}
