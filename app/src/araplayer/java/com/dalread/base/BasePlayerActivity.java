package com.dalread.base;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.activity.EditMeaningActivity;
import com.dalread.activity.PracticeConversationActivity;
import com.dalread.activity.QuizPlayerActivity;
import com.dalread.activity.WordInfoActivity;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.composition.PlayTTS;
import com.dalread.composition.VocaKnowActivity;
import com.dalread.database.VideoModelQuery;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnDownloadFinishListener;
import com.dalread.listener.OnHeaderListener;
import com.dalread.model.Lyric;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.VideoModel;
import com.dalread.model.VocaDownload;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseVoca;
import com.dalread.util.BaseVocaQuiz;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.FileUtil;
import com.dalread.util.LyricUtils;
import com.dalread.util.NetworkUtil;
import com.dalread.util.StorageUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaQuiz;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.IntStream;

public abstract class BasePlayerActivity extends BaseActivity implements IPlayerActivity, OnHeaderListener {
    protected abstract void setFullscreen();
    protected BaseEvent.Screen screen;
//    public PlayVocaHelper playVocaHelper;
    protected SubDatabase dicDatabase; //영어 사전 DB
    protected SubDatabase subDatabase; //각 영화별 자막 DB
    protected VocaKnowActivity vocaKnowActivity;
    public PlayTTS playTTS;
    private boolean backgroundMode;
    private ArrayList<IVocaFullPlayTTSItem> vocas;
    private LinkedHashMap<IVocaFullPlayTTSItem, ArrayList<String>> itemMap;
    private ArrayList<File> downloadFiles;
    private int downloadPos;
    private ArrayList<VocaDownload> serverVocaDownloads, localVocaDownloads;
    private File voiceFolder;
    private OnDownloadFinishListener onDownloadFinishListener;
    public EnumLanguage studyLanguage, motherTongueLanguage;
    public static final int REQUEST_QUIZ_SCREEN = 999;
    public PlayerFileModel playerFileModel;

    private SingleChoiceDialog singleChoiceDialog;
    private int selectedVideoThumbailAutoScrollIntervalCountValue;
    private int autoScrollInterval; //milliseconds
    private String[] readVideoThumbnailAutoScrollIntervalValues;
    private LinearSmoothScroller linearSmoothScroller;
    protected boolean isRunningAutoScroll;
    private boolean isHasInComingCall = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DLog.i(getLogTag(), "onCreate");
        setFullscreen();
        preInit();
        playTTS = new PlayTTS(this);
        alertDialog = new AlertDialog(this);
        singleChoiceDialog = new SingleChoiceDialog(this);
        initView();
        sharedPreferences.setFirstLaunchApp(false);
        initDicDatabase();
    }

    @Override
    protected void onStart() {
        playTTS.stopPlayVoca();
//        if (playVocaHelper != null) {
//            playVocaHelper.stop();
//            playVocaHelper.clearListener();
//        }
        super.onStart();
        DLog.i(getLogTag(), "onStart");
    }

    @Override
    protected void onPause() {
        DLog.i(getLogTag(), "onPause");
        if (isFinishing()) {
            playTTS.stopPlayVoca();
//            stopPlayVoca();
        } else if (!backgroundMode) {
            playTTS.stop();
//            playVocaHelper.stop();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        DLog.i(getLogTag(), "onDestroy");
        unRegisterEventBus();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_QUIZ_SCREEN) {
            if (resultCode == Activity.RESULT_OK) {
                if (playerFileModel != null) {
                    syncVocaKnow(playerFileModel.getVideoModel());
                    updateVideoModel(playerFileModel.getVideoModel());
                }
            }
        }
    }

    private void preInit() {
//        playVocaHelper = application.getPlayVocaHelper();
        voiceFolder = BaseVoca.getVoiceFolderOnLocal(this);
        updateBackgroundMode();
        getDataLanguages();
    }

    public void getDataLanguages() {
        studyLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage());
        motherTongueLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage());
    }

    public int getUserID() {
        return sharedPreferences.getUidDefault();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getRealUid() > 0; //Dalnim updated this.
    }

    private void initSubDatabase(String subPath) {
        subDatabase = SubDatabase.getInstance(this, subPath);
    }

    private void initDicDatabase() {
        dicDatabase = SubDatabase.getDicDatabaseInstance(this);
    }

    public void createSubDatabase(PlayerFileModel playerFileModel) {
        String subtitleDatabasePath = StorageUtil.generateSubtitleSQLitePathUnderAndroidFolder(this, playerFileModel.getVideoModel().getPath(), playerFileModel);
        if (!StorageUtil.isSubDatabaseFileExist(subtitleDatabasePath)) {
            StorageUtil.copySubtitleDatabseFileToVideoFolder(this, R.raw.subtitle_ruby, subtitleDatabasePath);
        }

        if (StorageUtil.isSubDatabaseFileExist(subtitleDatabasePath)) {
            initSubDatabase(subtitleDatabasePath);
        } else {
            setSubDatabase(null);
        }
    }

//    private void createSubDatabase(String subPath) {
//        if (StorageUtil.isSubDatabaseFileExist(this, subPath)) {
//            initSubDatabase(subPath);
//        } else {
//            setSubDatabase(null);
//        }
//    }

    public void setSubDatabase(SubDatabase subDatabase) {
        this.subDatabase = subDatabase;
    }

    public SubDatabase getSubDatabase() {
        return subDatabase;
    }

    public SubDatabase getDicDatabase() {
        return dicDatabase;
    }

    public boolean isNetwork() {
        return Utils.isConnected(this);
    }

    protected void updateBackgroundMode() {
        backgroundMode = sharedPreferences.getBackgroundMode();
    }

//    public PlayVocaHelper getPlayVocaHelper() {
//        return playVocaHelper;
//    }

//    public void stopPlayVoca() {
//        if (playVocaHelper != null) {
//            playVocaHelper.clearListener();
//            playVocaHelper.stop();
//        }
//        clearDownloadList();
//    }

//    public void preparePlayVoca(IVocaFullPlayTTSItem voca) {
//        preparePlayVoca(voca, false);
//    }
//
//    public void preparePlayVoca(IVocaFullPlayTTSItem voca, boolean isPlayingMulti) {
//        ArrayList<IVocaFullPlayTTSItem> vocas = new ArrayList<>();
//        vocas.add(voca);
//        preparePlayVoca(vocas, isPlayingMulti);
//    }
//
//    public void preparePlayPlayer(List<DicModel> list,
//                                  boolean isPlayingMulti) {
//        DLog.d(getLogTag(), "preparePlayPlayer - isPlayingMulti=" + isPlayingMulti + " - size=" + list.size());
//        playVocaHelper.setAraPlayer(true);
//        playVocaHelper.setIncludeMeaning(sharedPreferences.getIncludeMeaning());
//        playVocaHelper.setIncludeMyVoice(sharedPreferences.getIncludeMyVoice());
//        playVocaHelper.setIncludeMotherTongueSubtitle(sharedPreferences.getIncludeMotherTongueSubtitle());
//        playVocaHelper.setReadCount(Integer.parseInt(sharedPreferences.getReadCount()));
//        ArrayList<IVocaFullPlayTTSItem> vocas = new ArrayList<>();
//        vocas.addAll(list);
//        preparePlayVoca(vocas, isPlayingMulti);
//    }
//
//    public void preparePlayVoca(ArrayList<IVocaFullPlayTTSItem> vocas) {
//        preparePlayVoca(vocas, true);
//    }
//
//    public void preparePlayVoca(ArrayList<IVocaFullPlayTTSItem> vocas, boolean isPlayingMulti) {
//        DLog.d(getLogTag(), "preparePlayVoca - size=" + vocas.size());
//        this.vocas = vocas;
//        itemMap = new LinkedHashMap<>();
//        downloadFiles = new ArrayList<>();
//        downloadPos = -1;
//        serverVocaDownloads = new ArrayList<>();
//        String strFileNames = getPreferredNativeSpeakerFileNames();
//        DLog.d(getLogTag(), "strFileNames=" + strFileNames + " - size=" + itemMap.size());
//        playVocaHelper.setPlayingMulti(isPlayingMulti);
//        playVocaHelper.play(itemMap);
//        checkVersionAndDownload(strFileNames);
//    }
//
//    private String getPreferredNativeSpeakerFileNames() {
//        String strFileNames = "";
//        String strIds = sharedPreferences.getPreferredNativeSpeakers();
//        String[] preferredNativeSpeakerIds = strIds.isEmpty() ? new String[0] : strIds.split(",");
//        int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
//        for (IVocaFullPlayTTSItem voca : vocas) {
//            int vocaType = voca.getVIVocaType();
//            int vocaId = voca.getVIVocaId();
//            ArrayList<String> fileNames = new ArrayList<>();
//            for (String preferredNativeSpeakerId : preferredNativeSpeakerIds) {
//                int nativeSpeakerId = Integer.parseInt(preferredNativeSpeakerId);
//                String fileName = BaseVoca.getOutputRecordingFileName(studyLang, vocaType, vocaId, nativeSpeakerId);
//                strFileNames += "," + fileName;
//                fileNames.add(fileName);
//            }
//            if (sharedPreferences.getIncludeMyVoice()) {
//                int uid = getUserID();
//                String fileName = BaseVoca.getOutputRecordingFileName(studyLang, vocaType, vocaId, uid);
//                strFileNames += "," + fileName;
//            }
//            DLog.d(getLogTag(), "getPreferredNativeSpeakerFileNames - add id=" + vocaId);
//            itemMap.put(voca, fileNames);
//        }
//        if (!strFileNames.isEmpty()) {
//            strFileNames = strFileNames.substring(1);
//        }
//        return strFileNames;
//    }
//
//    private void checkVersionAndDownload(String fileNames) {
//        if (!fileNames.isEmpty() && Utils.isConnected(this)) {
//            final String[] names = fileNames.split(",");
//            BaseVoca.executeRealmTransaction(realm -> localVocaDownloads = (ArrayList<VocaDownload>) realm.copyFromRealm(
//                    realm.where(VocaDownload.class).in("name", names).findAll()
//            ));
//            application.getDalAiImpl().getMultipleVoiceFileVersion(
//                    sharedPreferences.getUid(),
//                    sharedPreferences.getLangStudyCode(),
//                    fileNames,
//                    new DalApiListener<List<VocaDownload>>() {
//
//                        @Override
//                        public void onSuccess(final List<VocaDownload> response) {
//                            int downloadSize = 0;
//                            for (VocaDownload serverVocaDownload : response) {
//                                if (serverVocaDownload.getVersion() > -1) {
//                                    String name = serverVocaDownload.getName();
//                                    File file = BaseVoca.getVoiceFileOnLocal(voiceFolder, name);
//                                    if (file.exists()) {
//                                        VocaDownload localVocaDownload = getLocalVocaDownload(name);
//                                        if (localVocaDownload == null
//                                                || localVocaDownload.getVersion() < serverVocaDownload.getVersion()) {
//                                            downloadFiles.add(file);
//                                            downloadSize += serverVocaDownload.getFileSize();
//                                            serverVocaDownloads.add(serverVocaDownload);
//                                        }
//                                    } else {
//                                        downloadFiles.add(file);
//                                        downloadSize += serverVocaDownload.getFileSize();
//                                        serverVocaDownloads.add(serverVocaDownload);
//                                    }
//                                } else if (serverVocaDownload.getReplacementVersion() > -1) {
//                                    final String replacementName = serverVocaDownload.getReplacementName();
//                                    File replacementFile = BaseVoca.getVoiceFileOnLocal(voiceFolder, replacementName);
//                                    if (replacementFile.exists()) {
//                                        final VocaDownload[] localVocaDownload = new VocaDownload[1];
//                                        BaseVoca.executeRealmTransaction(realm -> localVocaDownload[0] = realm.copyFromRealm(
//                                                realm.where(VocaDownload.class).equalTo("name", replacementName).findFirst()
//                                        ));
//                                        if (localVocaDownload[0] == null
//                                                || localVocaDownload[0].getVersion() < serverVocaDownload.getReplacementVersion()) {
//                                            downloadFiles.add(replacementFile);
//                                            downloadSize += serverVocaDownload.getFileSize();
//                                            serverVocaDownloads.add(serverVocaDownload);
//                                        } else {
//                                            copyFile(replacementFile, serverVocaDownload.getName());
//                                        }
//                                    } else {
//                                        downloadFiles.add(replacementFile);
//                                        downloadSize += serverVocaDownload.getFileSize();
//                                        serverVocaDownloads.add(serverVocaDownload);
//                                    }
//                                }
//                            }
//                            if (!Utils.hasWifiConnected(BasePlayerActivity.this) && downloadSize > Constant.DOWNLOAD_CONFIRM_SIZE) {
//                                new ConfirmDownloadVoiceFileDialog(
//                                        BasePlayerActivity.this,
//                                        new ConfirmationDialog.OnDialogClickListener() {
//
//                                            @Override
//                                            public void onPositive(DialogInterface dialog) {
//                                                dialog.dismiss();
//                                                downloadFileFromFirebase();
//                                            }
//
//                                            @Override
//                                            public void onNegative(DialogInterface dialog) {
//                                                dialog.dismiss();
//                                            }
//                                        }
//                                ).show(downloadSize);
//                            } else {
//                                downloadFileFromFirebase();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(String error) {
//                            DLog.i(getLogTag(), "getVoiceFileVersion onFailure");
//                        }
//                    }
//            );
//        } else {
//            DLog.i(getLogTag(), "No file names or no internet");
//        }
//    }
//
//    private VocaDownload getLocalVocaDownload(String name) {
//        for (VocaDownload localVocaDownload : localVocaDownloads) {
//            if (localVocaDownload.getName().equals(name)) {
//                return localVocaDownload;
//            }
//        }
//        return null;
//    }
//
//    private void downloadFileFromFirebase() {
//        if (isFinishing() || isDestroyed()) {
//            DLog.i(getLogTag(), "Exit screen, don't download anymore");
//        } else if (++downloadPos < downloadFiles.size()) {
//            DLog.i(getLogTag(), "downloadFileFromFirebase: " + (downloadPos + 1) + "/" + downloadFiles.size());
//            final File file = downloadFiles.get(downloadPos);
//            final String fileName = file.getName();
//            Uri uri = Uri.fromFile(file);
//            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(BaseVoca.getVoiceFolderPathOnFirebase(fileName) + uri.getLastPathSegment());
//            storageReference.getFile(file)
//                    .addOnSuccessListener(taskSnapshot -> {
//                        DLog.i(getLogTag(), "Download " + fileName + " onSuccess");
//                        saveServerVocaDownloadAndCopyFile(file);
//                        downloadFileFromFirebase();
//                    })
//                    .addOnFailureListener(e -> {
//                        DLog.i(getLogTag(), "Download " + fileName + " onFailure");
//                        saveServerVocaDownloadAndCopyFile(null);
//                        downloadFileFromFirebase();
//                    });
//        } else {
//            DLog.i(getLogTag(), "Download finished");
//            if (onDownloadFinishListener != null) {
//                onDownloadFinishListener.onFinish();
//                onDownloadFinishListener = null;
//            }
//        }
//    }
//
//    private void saveServerVocaDownloadAndCopyFile(File srcFile) {
//        if (downloadPos > -1 && downloadPos < serverVocaDownloads.size()) {
//            final VocaDownload vocaDownload = serverVocaDownloads.get(downloadPos);
//            if (srcFile != null && srcFile.getName().equals(vocaDownload.getReplacementName())) {
//                final VocaDownload vD = new VocaDownload();
//                vD.setName(vocaDownload.getReplacementName());
//                vD.setVersion(vocaDownload.getReplacementVersion());
//                BaseVoca.executeRealmTransaction(realm -> realm.copyToRealmOrUpdate(vD));
//                copyFile(srcFile, vocaDownload.getName());
//            } else {
//                BaseVoca.executeRealmTransaction(realm -> realm.copyToRealmOrUpdate(vocaDownload));
//            }
//        }
//    }
//
//    private void copyFile(File srcFile, String dstFileName) {
//        File dstFile = BaseVoca.getVoiceFileOnLocal(voiceFolder, dstFileName);
//        try {
//            BaseVoca.copyFile(srcFile, dstFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void clearDownloadList() {
//        if (downloadFiles != null) {
//            downloadFiles.clear();
//        }
//        if (serverVocaDownloads != null) {
//            serverVocaDownloads.clear();
//        }
//    }
//
//    public void preparePlayStudentVoice(IVocaFullPlayTTSItem voca) {
//        ArrayList<IVocaFullPlayTTSItem> vocas = new ArrayList<>();
//        vocas.add(voca);
//        preparePlayStudentVoice(vocas);
//    }
//
//    public void preparePlayStudentVoice(ArrayList<IVocaFullPlayTTSItem> vocas) {
//        this.vocas = vocas;
//        itemMap = new LinkedHashMap<>();
//        downloadFiles = new ArrayList<>();
//        downloadPos = -1;
//        serverVocaDownloads = new ArrayList<>();
//        String strFileNames = "";
//        for (IVocaFullPlayTTSItem voca : vocas) {
//            String fileName = voca.getVIPath();
//            strFileNames += "," + fileName;
//            ArrayList<String> fileNames = new ArrayList<>();
//            fileNames.add(fileName);
//            itemMap.put(voca, fileNames);
//        }
//        if (!strFileNames.isEmpty()) {
//            strFileNames = strFileNames.substring(1);
//        }
//        playVocaHelper.playStudentVoice(itemMap);
//        checkVersionAndDownload(strFileNames);
//    }
//
//    public void preparePlay1stNativeSpeakerAndMyVoiceOnce(IVocaFullPlayTTSItem voca) {
//        itemMap = new LinkedHashMap<>();
//        String strIds = sharedPreferences.getPreferredNativeSpeakers();
//        int nativeSpeakerId;
//        if (strIds.isEmpty()) {
//            nativeSpeakerId = 0;
//        } else {
//            String[] preferredNativeSpeakerIds = strIds.split(",");
//            nativeSpeakerId = Integer.parseInt(preferredNativeSpeakerIds[0]);
//        }
//        ArrayList<String> fileNames = new ArrayList<>();
//        if (nativeSpeakerId > 0) {
//            int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
//            int vocaType = voca.getVIVocaType();
//            int vocaId = voca.getVIVocaId();
//            String fileName = BaseVoca.getOutputRecordingFileName(studyLang, vocaType, vocaId, nativeSpeakerId);
//            fileNames.add(fileName);
//        }
//        itemMap.put(voca, fileNames);
//        playVocaHelper.play1stNativeSpeakerAndMyVoiceOnce(itemMap);
//    }
//
//    public void checkVersionAndDownload(ArrayList<IVocaFullPlayTTSItem> vocas) {
//        downloadFiles = new ArrayList<>();
//        downloadPos = -1;
//        serverVocaDownloads = new ArrayList<>();
//        String strFileNames = "";
//        String strIds = sharedPreferences.getPreferredNativeSpeakers();
//        String[] preferredNativeSpeakerIds = strIds.isEmpty() ? new String[0] : strIds.split(",");
//        int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
//        for (IVocaFullPlayTTSItem voca : vocas) {
//            int vocaType = voca.getVIVocaType();
//            int vocaId = voca.getVIVocaId();
//            for (String preferredNativeSpeakerId : preferredNativeSpeakerIds) {
//                int nativeSpeakerId = Integer.parseInt(preferredNativeSpeakerId);
//                String fileName = BaseVoca.getOutputRecordingFileName(studyLang, vocaType, vocaId, nativeSpeakerId);
//                strFileNames += "," + fileName;
//            }
//            if (sharedPreferences.getIncludeMyVoice()) {
//                int uid = getUserID();
//                String fileName = BaseVoca.getOutputRecordingFileName(studyLang, vocaType, vocaId, uid);
//                strFileNames += "," + fileName;
//            }
//        }
//        if (!strFileNames.isEmpty()) {
//            strFileNames = strFileNames.substring(1);
//        }
//        checkVersionAndDownload(strFileNames);
//    }
//
//    public void preparePlayVocaNoDownload(IVocaFullPlayTTSItem voca) {
//        ArrayList<IVocaFullPlayTTSItem> vocas = new ArrayList<>();
//        vocas.add(voca);
//        preparePlayVocaNoDownload(vocas);
//    }
//
//    public void preparePlayVocaNoDownload(ArrayList<IVocaFullPlayTTSItem> vocas) {
//        itemMap = new LinkedHashMap<>();
//        String strIds = sharedPreferences.getPreferredNativeSpeakers();
//        String[] preferredNativeSpeakerIds = strIds.isEmpty() ? new String[0] : strIds.split(",");
//        int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
//        for (IVocaFullPlayTTSItem voca : vocas) {
//            int vocaType = voca.getVIVocaType();
//            int vocaId = voca.getVIVocaId();
//            ArrayList<String> fileNames = new ArrayList<>();
//            for (String preferredNativeSpeakerId : preferredNativeSpeakerIds) {
//                int nativeSpeakerId = Integer.parseInt(preferredNativeSpeakerId);
//                String fileName = BaseVoca.getOutputRecordingFileName(studyLang, vocaType, vocaId, nativeSpeakerId);
//                fileNames.add(fileName);
//            }
//            itemMap.put(voca, fileNames);
//        }
//        playVocaHelper.play(itemMap);
//    }
//
//    public void prepareDownloadVoca(IVocaFullPlayTTSItem voca, OnDownloadFinishListener onDownloadFinishListener) {
//        vocas = new ArrayList<>();
//        vocas.add(voca);
//        this.onDownloadFinishListener = onDownloadFinishListener;
//        itemMap = new LinkedHashMap<>();
//        downloadFiles = new ArrayList<>();
//        downloadPos = -1;
//        serverVocaDownloads = new ArrayList<>();
//        String strFileNames = getPreferredNativeSpeakerFileNames();
//        checkVersionAndDownload(strFileNames);
//    }
//
//    protected void initPlayVocaHelper() {
//        playVocaHelper.initMotherTongueTTS(EnumLanguage.findByFormatApi(sharedPreferences.getDisplayLanguage()).getLocale());
//        EnumLanguage studyLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage());
//        int ttsSpeed = 0;
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
//        playVocaHelper.initStudyTTS(
//                studyLanguage.getLocale(),
//                Integer.parseInt(sharedPreferences.getReadCount()),
//                ttsSpeed
//        );
//        playVocaHelper.setIncludeMyVoice(sharedPreferences.getIncludeMyVoice());
//        setIncludeMeaning();
//        playVocaHelper.initMediaPlayer();
//    }
//
//    public void setIncludeMeaning() {
//        setIncludeMeaning(sharedPreferences.getIncludeMeaning());
//    }
//
//    public void setIncludeMeaning(boolean isIncludeMeaning) {
//        playVocaHelper.setIncludeMeaning(isIncludeMeaning);
//    }

    public void callAsyncTask(OnAsyncTaskListener listener) {
        callAsyncTask(listener, null, 0, true);
    }

    public void callAsyncTask(OnAsyncTaskListener listener, int type) {
        callAsyncTask(listener, null, type, true);
    }

    public void callAsyncTask(OnAsyncTaskListener listener, int type, boolean isLoading) {
        callAsyncTask(listener, null, type, isLoading);
    }

    public void callAsyncTask(OnAsyncTaskListener listener, Object data, int type) {
        callAsyncTask(listener, data, type, true);
    }

    public void callAsyncTask(OnAsyncTaskListener listener, Object data, int type, boolean isLoading) {
        new CustomAsyncTask(this, listener, data, type, isLoading).execute();
    }

    public void syncVocaKnow(VideoModel videoModel) {
        if (getSubDatabase() != null) {
            int vocaKnowCount = getSubDatabase().getVocaKnowCount();
            int vocaKnowAll = getSubDatabase().getVocaKnowAll();
            videoModel.setVocaKnowCount(vocaKnowCount);
            videoModel.setVocaKnowAll(vocaKnowAll);
            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.WORD_LIST, BaseEvent.EventType.DATA_CHANGED, null));
        }
    }

    public void updateVideoModel(PlayerFileModel playerFileModel) {
        if (playerFileModel != null) {
            VideoModel videoModel = playerFileModel.getVideoModel();
            if (videoModel != null) {
                videoModel.setVideoFromNetwork(playerFileModel.isLocal() ? Constant.INT_BOOLEAN.FASLE : Constant.INT_BOOLEAN.TRUE);
                updateVideoModel(videoModel);
            }
        }
    }

    public void updateVideoModel(VideoModel videoModel) {
        VideoModelQuery.update(Voca.getRealm(), videoModel);
    }

    protected void resetSubtitleFileAndDatabase(PlayerFileModel playerFileModel, String subPath, int subPathIndex) {

        StorageUtil.removeSubtitleSQLiteFile(this, playerFileModel);
        resetSubtitleFile(playerFileModel.getVideoModel(), subPath, subPathIndex);
        updateMusicInfo(playerFileModel, subPath);
        updateVideoModel(playerFileModel.getVideoModel());
        createSubDatabase(playerFileModel);
    }

    private void updateMusicInfo(PlayerFileModel playerFileModel, String subPath) {
        if (isAudioFormat()) {
            if (Utils.isEmpty(subPath)) {
                getMusicInfoFromMediaFile(playerFileModel);

            } else {
                getMusicInfoFromLyricFile(playerFileModel, subPath);
            }
        }
    }

    private void getMusicInfoFromLyricFile(PlayerFileModel playerFileModel, String subPath) {
        Lyric lyric = LyricUtils.parseLyric(new File(subPath), "UTF-8"); //TODO : Get Encoding
        playerFileModel.getVideoModel().setDisplayTitle(lyric.getTitle());
        playerFileModel.getVideoModel().setTitleTts(lyric.getTitleTts());
        playerFileModel.getVideoModel().setArtist(lyric.getArtist());
        playerFileModel.getVideoModel().setArtistTts(lyric.getArtistTts());
        playerFileModel.getVideoModel().setAlbum(lyric.getAlbum());
    }

    private void getMusicInfoFromMediaFile(PlayerFileModel playerFileModel) {
        AudioFile audioFile = null;
        try {
            audioFile = AudioFileIO.read(new File(playerFileModel.getPath()));
            Tag tag = audioFile.getTag();
            if (tag != null) {
                playerFileModel.getVideoModel().setDisplayTitle(tag.getFirst(FieldKey.TITLE));
                playerFileModel.getVideoModel().setTitleTts("");
                playerFileModel.getVideoModel().setArtist(tag.getFirst(FieldKey.ARTIST));
                playerFileModel.getVideoModel().setArtistTts("");
                playerFileModel.getVideoModel().setAlbum(tag.getFirst(FieldKey.ALBUM));
            }
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        }
    }

    public boolean isAudioFormat() {
        return FileUtil.isAudioFormat(playerFileModel.getName());
    }

    private void resetSubtitleFile(VideoModel videoModel, String subPath, int subPathIndex) {
        videoModel.setAnalyzeAgain(Constant.INT_BOOLEAN.TRUE);
        if (subPathIndex == Constant.PLAYER.INTENT.SUBPATH_INDEX_1) {
            videoModel.setVocaKnowCount(0);
            videoModel.setVocaKnowAll(0);
            videoModel.setDelaySubtitles(0);
            videoModel.setSubPath1(subPath);
//            videoModel.setSubPathOriginal(subPath);
            videoModel.setSubtitleEncodingIndex1(1);
            videoModel.setSubtitleEncoding1(Constant.PLAYER.OPTION.SUBTITLE.ENCODING.AUTO);
            videoModel.setDisplayTitle("");
            videoModel.setTitleTts("");
            videoModel.setArtist("");
            videoModel.setArtistTts("");
            videoModel.setAlbum("");
        } else {
            videoModel.setSubPath2(subPath);
            videoModel.setSubtitleEncodingIndex2(1);
            videoModel.setSubtitleEncoding2(Constant.PLAYER.OPTION.SUBTITLE.ENCODING.AUTO);
        }
    }

    protected List<DicModel> getDicModelListQuiz(PlayerFileModel playerFileModel) {
        List<DicModel> list = new ArrayList<>();
        if (getSubDatabase() == null) {
            createSubDatabase(playerFileModel);
        }
        int quizCount = Integer.parseInt(Constant.PLAYER.QUIZ.MAX_QUIZ_COUNT_RANGE[sharedPreferences.getPlayerMaxQuizCount()]);
        return getSubDatabase().getDicModelListForQuiz(quizCount);
//        list.addAll(getSubDatabase().getDicModelListForQuiz(Utils.parseInt(Constant.PLAYER.QUIZ.MAX_QUIZ_COUNT_RANGE[sharedPreferences.getPlayerMaxQuizCount()])));
//        return list;
    }

    protected List<VocaStudyChatExam> generateDicModelListQuiz(PlayerFileModel playerFileModel, List<DicModel> modelListQuiz) {
        return VocaQuiz.generateVocaStudyChatExamFromDicModelList(this, modelListQuiz);
    }

    protected void openQuizPlayerScreenWithLocalData() {
        openQuizPlayerScreenWithLocalData(playerFileModel);
    }

    public void openPracticeConversationActivityWithLocalData(PlayerFileModel playerFileModel) {
        List<DicModel> modelListQuiz = getDicModelListQuiz(playerFileModel);
        if (modelListQuiz.size() < Constant.PLAYER.QUIZ.QUIZ_ANSWERS) {
            ToastUtil.getInstance(this).show(R.string.msg_need_4_words_to_make_quiz);
            return;
        }

        List<VocaStudyChatExam> listQuiz = generateDicModelListQuiz(playerFileModel, modelListQuiz);
////        if (listQuiz.size() < Constant.PLAYER.QUIZ.QUIZ_ANSWERS) {
////            ToastUtil.getInstance(this).show(R.string.msg_need_4_words);
////            return;
////        }
//
//        Intent intent = new Intent(this, QuizPlayerActivity.class);
////        intent.putExtra(Constant.INTENT.KEY_PATH, playerFileModel.getSubPath1());
//        intent.putExtra(Constant.INTENT.KEY_QUIZ_SOURCE, Constant.INTENT.QUIZ_SOURCE.LOCAL);
//        intent.putExtra(Constant.INTENT.KEY_DATA, (Serializable)listQuiz);
//        startActivityForResult(intent, REQUEST_QUIZ_SCREEN);
        startActivity(PracticeConversationActivity.createIntentByQuizList(this, listQuiz));
    }

    public void openQuizPlayerScreenWithLocalData(PlayerFileModel playerFileModel) {
        List<DicModel> modelListQuiz = getDicModelListQuiz(playerFileModel);
        if (modelListQuiz.size() < Constant.PLAYER.QUIZ.QUIZ_ANSWERS) {
            ToastUtil.getInstance(this).show(R.string.msg_need_4_words_to_make_quiz);
            return;
        }

        List<VocaStudyChatExam> listQuiz = generateDicModelListQuiz(playerFileModel, modelListQuiz);
//        if (listQuiz.size() < Constant.PLAYER.QUIZ.QUIZ_ANSWERS) {
//            ToastUtil.getInstance(this).show(R.string.msg_need_4_words);
//            return;
//        }

        Intent intent = new Intent(this, QuizPlayerActivity.class);
//        intent.putExtra(Constant.INTENT.KEY_PATH, playerFileModel.getSubPath1());
        intent.putExtra(Constant.INTENT.KEY_QUIZ_SOURCE, Constant.INTENT.QUIZ_SOURCE.LOCAL);
        intent.putExtra(Constant.INTENT.KEY_DATA, (Serializable)listQuiz);
        startActivityForResult(intent, REQUEST_QUIZ_SCREEN);
    }

    public void openQuizPlayerScreenWithServerData() {
        Intent intent = new Intent(this, QuizPlayerActivity.class);
        intent.putExtra(Constant.INTENT.KEY_QUIZ_SOURCE, Constant.INTENT.QUIZ_SOURCE.SERVER);
        intent.putExtra(Constant.INTENT.KEY_MULTIPLE_CHOICE_QUESTION_TYPE, BaseVocaQuiz.generateMultipleChoiceQuestionType(this));
        startActivityForResult(intent, REQUEST_QUIZ_SCREEN);
    }

    public boolean isHasSubRuby() {
        return getSubDatabase() != null && getSubDatabase().isHasSubRuby();
    }


    public void rotateToLandscapeToShowImage() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        //Hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected void restoreToPortraitFromShowImage() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void saveVocaKnowValueInDB(IVocaBasicItem iVocaBasicItem) {
        subDatabase.updateVocaKnowInDB(iVocaBasicItem);
        dicDatabase.updateVocaKnowInDicDB(iVocaBasicItem);
    }

    public void saveVocaKnowValue(IVocaBasicItem item) {
        saveVocaKnowValueInDB(item);
//        saveVocaKnowValueInServer(item.getVIVocaType(), item.getVIVocaId(), item.getVIVocaKnow(), item.getVIVocaKnowPronounce());
//        saveVocaKnowValueInServerWhenSubtitleHasOneWord(item);
    }

    private void saveVocaKnowValueInServer(int vocaType, int vocaId, int vocaKnow, int vocaKnowPronounce) {
        application.getDalAiImpl().changeMultipleVocaKnow(
                String.valueOf(vocaKnow),
                String.valueOf(vocaKnowPronounce),
                String.valueOf(vocaId),
                String.valueOf(vocaType),
                null);
    }

    private void saveVocaKnowValueInServerWhenSubtitleHasOneWord(IVocaBasicItem item) {
        if (isVocaTypeSubtitleAndHasOneWord(item)) {
            //Save VocaBase word's KNOW value in server too.
            saveVocaKnowValueInServer(item.getVIVocaTypeBase(), item.getVIVocaIdBase(), item.getVIVocaKnow(), item.getVIVocaKnowPronounce());
        }
    }

    protected void updateBookmarkValue(IVocaBasicItem iVocaBasicItem) {
        iVocaBasicItem.swapVIBookmark();
        saveVocaBookmarkValue(iVocaBasicItem);
    }

    private void saveVocaBookmarkValue(IVocaBasicItem item) {
        getSubDatabase().updateBookmarkInDB(item);
        dicDatabase.updateBookmarkInDicDB(item);
//        saveBookmarkValueInServer(item);
    }

    private void saveBookmarkValueInServer(IVocaBasicItem item) {
        if (item.isVIBookmark()) {
            application.getDalAiImpl().addToBookmark(item.getVIVoca(), String.valueOf(item.getVIVocaId()), item.getVIVocaType(), null);
        } else {
            application.getDalAiImpl().deleteFromBookmark(item.getVIVoca(), String.valueOf(item.getVIVocaId()), item.getVIVocaType(), null);
        }
    }

    public boolean isVocaTypeSubtitleAndHasOneWord(IVocaBasicItem iVocaBasicItem) {
        return isVocaTypeSubtitle(iVocaBasicItem) && (iVocaBasicItem.getVIVocaTypeBase() == Constant.API_VALUE.VALUE_VOCA_TYPE_WORD);
    }

    private boolean isVocaTypeSubtitle(IVocaBasicItem iVocaBasicItem) {
        return (iVocaBasicItem.getVIVocaType() == Constant.API_VALUE.VALUE_VOCA_TYPE_SUBTITLE) || (iVocaBasicItem.getVIVocaType() == Constant.API_VALUE.VALUE_VOCA_TYPE_SUBTITLE_UNTUNED);
    }

    public void openPhraseInformation(IVocaFullItem iVocaFullItem) {
        if (Utils.isConnected(this)) {
//            if (data instanceof IVocaFullItem) {
//                IVocaFullItem iVocaFullItem = (IVocaFullItem) data;
                if (iVocaFullItem.getVIVocaId() >= 0) {
                    openNewScreen(
                            WordInfoActivity.createIntent(this, iVocaFullItem)
                    );
                } else {
                    openEditMeaningScreen(iVocaFullItem);
                }
//            }
        } else {
            this.alertDialog.showNoInternet();
        }
    }

    //TODO : Don't get dicModel from database here, get it before calling this method and send it as a parameter.
    public void openEditMeaningScreen(IVocaFullItem iVocaFullItem) {
        if (NetworkUtil.isNetworkConnetedIfNotShowWarningAsPopup(this)) {
            try {
//                final VocaStudyChat item = (VocaStudyChat) data;
//                IVocaFullItem iVocaFullItem = (IVocaFullItem) data;
                DicModel dicModel = this.getSubDatabase().getDicModelByVocaTypeVocaId(iVocaFullItem);

                Intent intent = new Intent(this, EditMeaningActivity.class);
                intent.putExtra(Constant.BUNDLE.KEY_VOCA, (IVocaFullItem)dicModel);
                openNewScreen(intent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    protected void initAutoScrollIntervalCountValue() {
        selectedVideoThumbailAutoScrollIntervalCountValue = sharedPreferences.getSelectedVideoThumbailAutoScrollIntervalCountValue();
        readVideoThumbnailAutoScrollIntervalValues = Arrays.stream(IntStream.range(1, 201).toArray())
                .mapToObj(String::valueOf)
                .toArray(String[]::new);

        String strInterval = readVideoThumbnailAutoScrollIntervalValues[selectedVideoThumbailAutoScrollIntervalCountValue];
        autoScrollInterval = getAutoScrollInterval(strInterval);

        setListenerScroller();

        getAutoScrollRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isRunningAutoScroll) {
                    if (newState == SCROLL_STATE_IDLE) {
                        //To start over when autoscrolling reaches end of list
                        isRunningAutoScroll = false;
                        updateAutoScrollIconToPlay();
                        int totalItem = recyclerView.getLayoutManager().getItemCount();
                        int lastVisibleItem = 0;
                        if (getAutoScrollLayoutManager() instanceof LinearLayoutManager) {
                            lastVisibleItem =
                                    ((LinearLayoutManager) getAutoScrollLayoutManager()).findLastCompletelyVisibleItemPosition();
                        } else if (getAutoScrollLayoutManager() instanceof GridLayoutManager) {
                            lastVisibleItem = ((GridLayoutManager) getAutoScrollLayoutManager()).findLastCompletelyVisibleItemPosition();
                        }
                        if (totalItem <= lastVisibleItem + 1) {
                            ToastUtil.getInstance(BasePlayerActivity.this).show(R.string.msg_play_again_started_over);
                            recyclerView.scrollToPosition(0);
                            recyclerView.post(() -> startAutoScrolling());

                        }
                    } else if (newState == SCROLL_STATE_DRAGGING) {
                        //This is called when I touch a thumbnail during autoscrolling.
                        isRunningAutoScroll = false;
                        updateAutoScrollIconToPlay();
                    }
                }
            }
        });
    }

    private int getAutoScrollInterval(String strInterval) {
        return Integer.parseInt(strInterval) * 100;
    }

    private void setListenerScroller() {
        linearSmoothScroller = new LinearSmoothScroller(this) {
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return autoScrollInterval / (float)displayMetrics.densityDpi;
            }
        };
    }

    protected void startAutoScrolling() {
        linearSmoothScroller.setTargetPosition(getAutoScrollRecyclerViewAdapter().getItemCount() - 1);
        getAutoScrollLayoutManager().startSmoothScroll(linearSmoothScroller);
        isRunningAutoScroll = true;
        updateAutoScrollIcon();
    }

    private void updateAutoScrollIcon() {
        if ((Integer) getIconTag() == R.drawable.ic_new_play) {
            updateAutoScrollIconToPause();
        } else {
            updateAutoScrollIconToPlay();
        }
    }

    protected void updateAutoScrollIconToPlay() {

    }

    protected void updateAutoScrollIconToPause() {

    }

    protected RecyclerView getAutoScrollRecyclerView() {
        return null;
    }

    protected RecyclerView.Adapter getAutoScrollRecyclerViewAdapter() {
        return null;
    }

    protected Object getIconTag() {
        return null;
    }

    protected void showAutoScrollIntervalDialog() {
        singleChoiceDialog.show(
                R.string.auto_scroll_list_interval,
                readVideoThumbnailAutoScrollIntervalValues,
                selectedVideoThumbailAutoScrollIntervalCountValue,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        selectedVideoThumbailAutoScrollIntervalCountValue = (int) object;
                        String strInterval = readVideoThumbnailAutoScrollIntervalValues[selectedVideoThumbailAutoScrollIntervalCountValue];
                        autoScrollInterval = getAutoScrollInterval(strInterval);
                        setListenerScroller();
                        startAutoScrolling();
                        sharedPreferences.setSelectedVideoThumbailAutoScrollIntervalCountValue(selectedVideoThumbailAutoScrollIntervalCountValue);
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    protected void stopAutoScrolling() {
        if (isRunningAutoScroll) {
            getAutoScrollRecyclerView().stopScroll();
            updateAutoScrollIconToPlay();
        }
    }

    protected void onHeaderAutoScrollIconClick() {
        if (isRunningAutoScroll) {
            stopAutoScrolling();
        } else {
            startAutoScrolling();
        }
    }

    protected RecyclerView.LayoutManager getAutoScrollLayoutManager() {
        return null;
    }

}
