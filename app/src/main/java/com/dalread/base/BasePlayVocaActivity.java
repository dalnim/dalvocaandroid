package com.dalread.base;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.dialog.ConfirmDownloadVoiceFileDialog;
import com.dalread.dialog.ConfirmationDialog;
import com.dalread.helper.PlayVocaHelper;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.VocaDownload;
import com.dalread.network.DalApiListener;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Utils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;

public abstract class BasePlayVocaActivity extends BaseVocaKnowActivity {
    //TODO : If I remove these codes, I met this error.
    //Field 'com.dalread.base.BaseVocaKnowActivity.clDivider' is inaccessible to class 'com.dalread.activity.BaseConvActivity_ViewBinding'
    @BindColor(R.color.color_divider)
    protected int clDivider;
    @BindDimen(R.dimen.divider_height)
    protected float dividerHeight;
    //--------

    protected PlayVocaHelper playVocaHelper;
    private ArrayList<File> downloadFiles;
    private ArrayList<VocaDownload> serverVocaDownloads, localVocaDownloads;
    private ArrayList<IVocaFullPlayTTSItem> vocas;
    private LinkedHashMap<IVocaFullPlayTTSItem, ArrayList<String>> itemMap;
    private int downloadPos;
    private File voiceFolder;
    private int playingState;


//    protected BaseSubDatabase subDatabase;

//    protected ConvCommonMenuDialog convCommonMenuDialog;
//    @Override
//    protected int getContentViewId() {
//        return 0;
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
        initSubDatabase();
        initPlayVocaHelper();
    }

    public PlayVocaHelper getPlayVocaHelper() {
        return playVocaHelper;
    }
    public void stopPlayVoca() {
        if (playVocaHelper != null) {
            playVocaHelper.clearListener();
            playVocaHelper.stop();
        }
        clearDownloadList();
    }
    public void clearDownloadList() {
        if (downloadFiles != null) {
            downloadFiles.clear();
        }
        if (serverVocaDownloads != null) {
            serverVocaDownloads.clear();
        }
    }

    private void initSubDatabase() {
//        if (subDatabase != null) {
//            subDatabase.close();
//        }
//        subDatabase = null;
//        String destPathWithFileName = BaseStorageUtil.getAraConvDBPathWithFileName(context);
//        subDatabase = SubDatabase.getInstance(this, destPathWithFileName);
    }

    @Override
    protected void udpateBookmarkInLocalDB(IVocaBasicItem item) {
//        subDatabase.swapBookmark(item);

    }

//    protected final DialogInterface.OnClickListener onDialogItemClickListener = (dialog, which) -> {
//        switch (which) {
//            case R.id.llRefreshVocaFromServer:
////                refreshVocaFromServer();
//                break;
//            case R.id.tvBackToHome:
//                backToHome();
//                break;
//            default:
//                break;
//        }
//    };

    protected void getData() {

    }

    @Override
    protected void onStart() {
        if (playVocaHelper != null) {
            playVocaHelper.stop();
            playVocaHelper.clearListener();
        }
        super.onStart();
        DLog.i(getLogTag(), "onStart");
    }

    @Override
    protected void onPause() {
        DLog.i(getLogTag(), "onPause");
        if (isFinishing()) {
            stopPlayVoca();
//        } else if (!backgroundMode) {
//            playVocaHelper.stop();
        }
        super.onPause();
    }

    public void preparePlayVoca(IVocaFullPlayTTSItem voca) {
        preparePlayVoca(voca, false);
    }

    public void preparePlayVoca(IVocaFullPlayTTSItem voca, boolean isPlayingMulti) {
        ArrayList<IVocaFullPlayTTSItem> vocas = new ArrayList<>();
        vocas.add(voca);
        preparePlayVoca(vocas, isPlayingMulti);
    }

//    public void preparePlayPlayer(List<DicModel> list,
//                                  boolean isPlayingMulti) {
//        DLog.d(getLogTag(), "preparePlayPlayer - isPlayingMulti=" + isPlayingMulti + " - size=" + list.size());
//        playVocaHelper.setAraPlayer(true);
//        playVocaHelper.setIncludeMeaning(sharedPreferences.getIncludeMeaning());
//        playVocaHelper.setIncludeMyVoice(sharedPreferences.getIncludeMyVoice());
//        playVocaHelper.setIncludeMotherTongueSubtitle(sharedPreferences.getIncludeMotherTongueSubtitle());
//        playVocaHelper.setReadCount(Integer.parseInt(sharedPreferences.getReadCount()));
//        ArrayList<PlaylistItem> vocas = new ArrayList<>();
//        vocas.addAll(list);
//        preparePlayVoca(vocas, isPlayingMulti);
//    }

    public void preparePlayVoca(ArrayList<IVocaFullPlayTTSItem> vocas) {
        preparePlayVoca(vocas, true);
    }

    public void preparePlayVoca(ArrayList<IVocaFullPlayTTSItem> vocas, boolean isPlayingMulti) {
        DLog.d(getLogTag(), "preparePlayVoca - size=" + vocas.size());
        this.vocas = vocas;
        itemMap = new LinkedHashMap<>();
        downloadFiles = new ArrayList<>();
        downloadPos = -1;
        serverVocaDownloads = new ArrayList<>();
        String strFileNames = getPreferredNativeSpeakerFileNames();
        DLog.d(getLogTag(), "strFileNames=" + strFileNames + " - size=" + itemMap.size());
        playVocaHelper.setPlayingMulti(isPlayingMulti);
        playVocaHelper.play(itemMap);
        checkVersionAndDownload(strFileNames);
    }

    private String getPreferredNativeSpeakerFileNames() {
        String strFileNames = "";
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
                strFileNames += "," + fileName;
                fileNames.add(fileName);
            }
            if (sharedPreferences.getIncludeMyVoice()) {
                int uid = getUserID();
                String fileName = BaseVoca.getOutputRecordingFileName(studyLang, vocaType, vocaId, uid);
                strFileNames += "," + fileName;
            }
            DLog.d(getLogTag(), "getPreferredNativeSpeakerFileNames - add id=" + vocaId);
            itemMap.put(voca, fileNames);
        }
        if (!strFileNames.isEmpty()) {
            strFileNames = strFileNames.substring(1);
        }
        return strFileNames;
    }

    private void checkVersionAndDownload(String fileNames) {
        if (!fileNames.isEmpty() && Utils.isConnected(this)) {
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
                                deleteUnversionFile(serverVocaDownload.getName(), serverVocaDownload.getVersion());
                                deleteUnversionFile(serverVocaDownload.getReplacementName(), serverVocaDownload.getReplacementVersion());
                                if (serverVocaDownload.getVersion() > -1) {
                                    String name = serverVocaDownload.getName();
                                    File file = BaseVoca.getVoiceFileOnLocal(voiceFolder, name);
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
                            if (!Utils.hasWifiConnected(BasePlayVocaActivity.this) && downloadSize > Constant.DOWNLOAD_CONFIRM_SIZE) {
                                new ConfirmDownloadVoiceFileDialog(
                                        BasePlayVocaActivity.this,
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

    private VocaDownload getLocalVocaDownload(String name) {
        for (VocaDownload localVocaDownload : localVocaDownloads) {
            if (localVocaDownload.getName().equals(name)) {
                return localVocaDownload;
            }
        }
        return null;
    }

    private void downloadFileFromFirebase() {
        if (isFinishing() || isDestroyed()) {
            DLog.i(getLogTag(), "Exit screen, don't download anymore");
        } else if (++downloadPos < downloadFiles.size()) {
            DLog.i(getLogTag(), "downloadFileFromFirebase: " + (downloadPos + 1) + "/" + downloadFiles.size());
            final File file = downloadFiles.get(downloadPos);
            final String fileName = file.getName();
            Uri uri = Uri.fromFile(file);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(BaseVoca.getVoiceFolderPathOnFirebase(fileName) + uri.getLastPathSegment());
            storageReference.getFile(file)
                    .addOnSuccessListener(taskSnapshot -> {
                        DLog.i(getLogTag(), "Download " + fileName + " onSuccess");
                        saveServerVocaDownloadAndCopyFile(file);
                        downloadFileFromFirebase();
                    })
                    .addOnFailureListener(e -> {
                        DLog.i(getLogTag(), "Download " + fileName + " onFailure");
                        saveServerVocaDownloadAndCopyFile(null);
                        downloadFileFromFirebase();
                    });
        } else {
            DLog.i(getLogTag(), "Download finished");
//            if (onDownloadFinishListener != null) {
//                onDownloadFinishListener.onFinish();
//                onDownloadFinishListener = null;
//            }
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
        this.vocas = vocas;
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
        playVocaHelper.playStudentVoice(itemMap);
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
        playVocaHelper.play1stNativeSpeakerAndMyVoiceOnce(itemMap);
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
                int uid = getUserID();
                String fileName = BaseVoca.getOutputRecordingFileName(studyLang, vocaType, vocaId, uid);
                strFileNames += "," + fileName;
            }
        }
        if (!strFileNames.isEmpty()) {
            strFileNames = strFileNames.substring(1);
        }
        checkVersionAndDownload(strFileNames);
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
        playVocaHelper.play(itemMap);
    }

//    public void prepareDownloadVoca(PlaylistItem voca, OnDownloadFinishListener onDownloadFinishListener) {
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

    protected void initPlayVocaHelper() {
        playVocaHelper = application.getPlayVocaHelper();
//        playVocaHelper = new PlayVocaHelper(this);
        voiceFolder = BaseVoca.getVoiceFolderOnLocal(this);

        playVocaHelper.initMotherTongueTTS(EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage()).getLocale());
        EnumLanguage studyLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage());
        int ttsSpeed = 0;
        if (studyLanguage == EnumLanguage.CHINESE_SIMPLIFIED) {
            ttsSpeed = sharedPreferences.getSettingTTSSpeedChinese();
        } else if (studyLanguage == EnumLanguage.ENGLISH) {
            ttsSpeed = sharedPreferences.getSettingTTSSpeedEnglish();
        } else if (studyLanguage == EnumLanguage.JAPANESE) {
            ttsSpeed = sharedPreferences.getSettingTTSSpeedJapanese();
        } else if (studyLanguage == EnumLanguage.KOREAN) {
            ttsSpeed = sharedPreferences.getSettingTTSSpeedKorean();
        } else if (studyLanguage == EnumLanguage.HANJA) {
            ttsSpeed = sharedPreferences.getSettingTTSSpeedHanja();
        }
        playVocaHelper.initStudyTTS(
                studyLanguage.getLocale(),
                Integer.parseInt(sharedPreferences.getReadCount()),
                ttsSpeed
        );
//        playVocaHelper.setIncludeMyVoice(sharedPreferences.getIncludeMyVoice());
        setIncludeMeaning();
        playVocaHelper.initMediaPlayer();
    }


    private void handleTTSPlay(IVocaFullPlayTTSItem model, boolean isAutoPlay) {
        DLog.d(getLogTag(), "handlePlay");
        boolean isTTSPlaying = model.isVIPlaying();
        playVocaHelper.stop();
        if (!isTTSPlaying) {
            preparePlayVoca(model);
        }
    }

    private void stopTTSPlaying(IVocaFullPlayTTSItem model) {
        boolean isTTSPlaying = model.isVIPlaying();
        if (isTTSPlaying) {
            playVocaHelper.stop();
        }

    }



    public void setIncludeMeaning() {
        setIncludeMeaning(sharedPreferences.getIncludeMeaning());
    }

    public void setIncludeMeaning(boolean isIncludeMeaning) {
        playVocaHelper.setIncludeMeaning(isIncludeMeaning);
    }

    @Override
    public void onHeaderLeftClick() {

    }

    @Override
    public void onHeaderLeft2Click() {

    }

    @Override
    public void onHeaderRightClick() {

    }

    @Override
    public void onHeaderIconRightClick() {

    }

    @Override
    public void onHeaderTextRightClick() {

    }

    @Override
    protected void updateVocaKnowInDB(IVocaBasicItem voca, int newVocaKnow) {
        voca.setVIVocaKnow(newVocaKnow);
//        subDatabase.updateVocaKnow(voca);
    }
    @Override
    protected void updateVocaKnowPronounceInDB(IVocaBasicItem voca, int newVocaKnowPronounce) {
        voca.setVIVocaKnowPronounce(newVocaKnowPronounce);
//        subDatabase.updateVocaKnow(voca);
    }

    private void deleteUnversionFile(String name, int version) {
        if (!TextUtils.isEmpty(name) && version == -1) {
            DLog.i(getLogTag(), name + " has version = -1 on server.");
            File file = BaseVoca.getVoiceFileOnLocal(voiceFolder, name);
            if (file != null && file.exists()) {
                boolean deleted = file.delete();
                DLog.i(getLogTag(), name + " is deleted " + (deleted ? " successfully." : " UNSUCCESSFULLY."));
            } else {
                DLog.i(getLogTag(), name + " does not exist on device.");
            }
        }
    }
}
