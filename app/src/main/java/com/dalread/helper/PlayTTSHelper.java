package com.dalread.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.text.TextUtils;

import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnPlayMyVoiceOnce;
import com.dalread.listener.OnShufflePlayListListener;
import com.dalread.service.AlarmReceiver;
import com.dalread.util.BaseCollectionUtil;
import com.dalread.util.BaseFileUtil;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.LanguageUtil;
import com.dalread.util.RepeatUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.TtsVoiceListUtil;
import com.dalread.util.Utils;
import com.dalread.util.studylang.AbstractStudyLang;
import com.dalread.util.studylang.StudyLangFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

//Same as PlayVocaHelper but will use this later
public class PlayTTSHelper {
    private final String TAG = this.getClass().getSimpleName();
    private static SharedPreferencesDB sharedPreferences;
    private Context context;
    private List<Voice> studyTtsVoices;
    private TextToSpeech motherTongueTTS;
    private UtteranceProgressListener motherTongueListener;
    public TextToSpeech studyTTS;
    private UtteranceProgressListener studyListener;
    private OnShufflePlayListListener onShufflePlayListListener;
    private OnPlayMyVoiceOnce onPlayMyVoiceOnce;
    private MediaPlayer mediaPlayer;
    public boolean isPlayTTSAtFirst; //이건 왜 전부 false만 세팅할까? 뭐하는거지?
//    private int totalCountToPlayVoiceOrTts; //Read count per word in Setting
    private int playVoiceOrTtsPos; //0 to readCount(Read count per word in Setting)
    private int playVoiceFilePos; //음성 파일 재생할 인덱스
    private int playTtsPos; //TTS재생할 인덱스
    private int playlistPos;
//    private int subPlaylistPos;
//    private int studyVoicePos; //playTtsPos와 같은거 같음.
//    private boolean includeMyVoice;
    private boolean includeMeaning;
    private LinkedHashMap<IVocaFullPlayTTSItem, ArrayList<String>> itemMap;
    private AbstractStudyLang studyLang;
    private File myVoiceFile;
    private boolean isPlayMotherTongueFirstRoundOnConversation = false;
    private boolean isConversationBook = false;
    private boolean isPlaying;
    private boolean isPlayingMultiFiles;
    private boolean isStudentVoice;
    public boolean playOnce;
    public boolean isPlayMyVoiceOnce;
    private Handler handler;
    private Runnable playVoiceOrTtsRunnable;
    private Runnable finishCurrentItemRunnable;
    private int retryCountAfterPlayWithPause; //Nowhere uses this, just count. 음성을 플레이하고 나서 말하기 연습을 위해서 잠시 쉰 횟수, 사용하는곳은 없음
    private boolean isAraPlayer = false;
    // https://github.com/dalnim/IssueOnly/issues/118#issuecomment-665396347
    private boolean includeMotherTongueSubtitle;
    private boolean playDifficultWordsBeforePlayingSubtitle;
    private String subtitleMotherTongue;
    private String subtitleStudyLang; //Dalnim added
    private AlarmManager alarmManager;
    private boolean isPlayEndlessly;

    public PlayTTSHelper(Context context) {
        this.context = context;
        studyTtsVoices = new ArrayList<>();
        handler = new Handler();
        initData();
        initRunnable();
        initStudyLang();
        initPlaylist();
    }

    private void initData() {
        sharedPreferences = SharedPreferencesDB.getInstance(context);
    }

    public void initRunnable() {
        playVoiceOrTtsRunnable = () -> {
            handlePlayVoiceOrTts();
        };
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        finishCurrentItemRunnable = () -> {
            handleFinishCurrentItem();
        };
    }

    public void handlePlayVoiceOrTts() {
        retryCountAfterPlayWithPause++;
        DLog.i(TAG, "retry = " + retryCountAfterPlayWithPause);
        playVoiceOrTTS();
    }

    public void handleFinishCurrentItem(){
        shuffleVoiceFileNames();
        IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) itemMap.keySet().toArray()[playlistPos];
        String utteranceId = String.valueOf(voca.getVIVocaId());
        finishCurrentItem(utteranceId);
    }

    private void removeRunnables() {
        handler.removeCallbacks(playVoiceOrTtsRunnable);
        handler.removeCallbacks(finishCurrentItemRunnable);

        alarmManager.cancel(getPlayVoiceOrTTSPendingIntent());
        alarmManager.cancel(getFinishCurrentTTSPendingIntent());
    }

    private PendingIntent getPlayVoiceOrTTSPendingIntent() {
        Intent playVoiceOrTTSIntent = getAlarmIntent(AlarmReceiver.ACTION_PLAY_VOICE_OR_TTS);
        return getAlarmPendingIntent(playVoiceOrTTSIntent, AlarmReceiver.PLAY_NEXT_TTS_REQUEST_CODE);
    }

    private PendingIntent getFinishCurrentTTSPendingIntent() {
        Intent playVoiceOrTTSIntent = getAlarmIntent(AlarmReceiver.ACTION_FINISH_CURRENT_TTS);
        return getAlarmPendingIntent(playVoiceOrTTSIntent, AlarmReceiver.FINISH_CURRENT_TTS_REQUEST_CODE);
    }

    private Intent getAlarmIntent(String action) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(action);
        return intent;
    }

    private PendingIntent getAlarmPendingIntent(Intent intent, int requestCode) {
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    private void initStudyLang() {
        studyLang = StudyLangFactory.create(context);
    }

    private void initPlaylist() {
        if (itemMap == null) {
            itemMap = new LinkedHashMap<>();
        } else {
            itemMap.clear();
        }
        playlistPos = -1;
        isStudentVoice = false;
        playOnce = false;
        isPlayMyVoiceOnce = false;
        initPos();
    }

    private void initPos() {
//        subPlaylistPos = studyVoicePos = -1;
//        subPlaylistPos = -1;
        myVoiceFile = null;
        updateValue_playVoiceOrTtsPos(0); //playVoiceCount = 0;
        updateValue_playVoiceFilePos(0);
        updateValue_playTtsPos(-1);
        retryCountAfterPlayWithPause = 0;
    }

    public void initMotherTongueTTS() {
        if (motherTongueTTS != null) return;
        motherTongueTTS = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                motherTongueTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                    @Override
                    public void onStart(String utteranceId) {
                        if (motherTongueListener != null) {
                            motherTongueListener.onStart(utteranceId);
                        }
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if (motherTongueListener != null) {
                            motherTongueListener.onDone(utteranceId);
                        }
                        if (isToPlaySubtitleMotherTongue()) {
                            if (utteranceId.equals(getSubtitleMotherTongue())) {
                                setSubtitleMotherTongue(Constant.BASE_BLANK);
                                playVoca();
                            }
                        } else if (isPlaying) {
//                            updateValue_playVoiceFilePos(0);// subPlaylistPos = 0;
                            playVoiceOrTTS();
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                        if (motherTongueListener != null) {
                            motherTongueListener.onError(utteranceId);
                        }
                    }
                });
            } else {
                motherTongueTTS = null;
            }
        });
    }

    private void updateMotherTongueLocale(IVocaFullPlayTTSItem voca) {
        Locale locale= getMotherTongueLocale(voca);
        motherTongueTTS.setLanguage(locale);
    }

    private Locale getMotherTongueLocale(IVocaFullPlayTTSItem voca) {
        Locale locale;
        if (voca == null) {
            locale = EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage()).getLocale();
        } else {
            if (Utils.isEmpty(voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)))) {
                //If there is no meaning, then AraPlayer shows English meaning as default, so use English TTS for this case.
                locale = EnumLanguage.findByFormatApi(EnumLanguage.ENGLISH.getFormatApi()).getLocale();
            } else {
                locale = EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage()).getLocale();
            }
        }
        return locale;
    }
//    public void initStudyTTS(Locale locale, int readCount, int speed) {
    public void initStudyTTS(int readCount) {
        if (studyTTS != null) return;
        DLog.d(TAG, "initStudyTTS");
        Locale locale = studyLang.getLocale();
        studyTTS = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                DLog.d(TAG, "initStudyTTS SUCCESS");
                studyTTS.setLanguage(locale);
                setTotalCountToPlayVoiceOrTts(readCount);
                studyTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                    @Override
                    public void onStart(String utteranceId) {
                        if (studyListener != null) {
                            studyListener.onStart(utteranceId);
                        }
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if (isToPlaySubtitleStudyLang()) { //Dalnim added to stop playing subtitle study lang.
                            if (utteranceId.equals(getSubtitleStudyLang())) {
                                setSubtitleStudyLang(Constant.BASE_BLANK);
                                playVoca();
                            }
                        } else if (isPlaying) {
                            if (playOnce) {
                                playMyVoiceOnce(utteranceId);
                            } else {
                                playMyVoice();
                            }
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                        if (studyListener != null) {
                            studyListener.onError(utteranceId);
                        }
                    }
                });
                setStudyTTSSpeed(studyLang.getTTSSpeedInSetting());
            } else {
                DLog.d(TAG, "initStudyTTS ERROR");
                studyTTS = null;
            }
        }, "com.google.android.tts");
    }

    public void setStudyTTSSpeed(int speed) {
        if (studyTTS != null) {
            studyTTS.setSpeechRate(studyLang.getTtsSpeechRate());
        }
    }

    public void playStudyTTS(String text) {
        if (studyTTS != null) {
            studyTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    public void stopStudyTTS() {
        if (studyTTS != null) {
            studyTTS.stop();
        }
    }

    public void setOnPlayMyVoiceOnceListener(OnPlayMyVoiceOnce onPlayMyVoiceOnce) {
        this.onPlayMyVoiceOnce = onPlayMyVoiceOnce;
    }
    public boolean hasOnPlayMyVoiceOnceListener() {
        return onPlayMyVoiceOnce != null;
    }
    public void setMotherTongueListener(UtteranceProgressListener listener) {
        motherTongueListener = listener;
    }

    public boolean hasMotherTongueListener() {
        return motherTongueListener != null;
    }

    public void setStudyListener(UtteranceProgressListener listener) {
        studyListener = listener;
    }


    public boolean hasStudyListener() {
        return studyListener != null;
    }

    public void clearListener() {
        setMotherTongueListener(null);
        setStudyListener(null);
        setOnShufflePlayListListener(null);
    }

    public void initMediaPlayer() {
        if (mediaPlayer != null) return;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void setTotalCountToPlayVoiceOrTts(int totalCountToPlayVoiceOrTts) {
//        this.totalCountToPlayVoiceOrTts = totalCountToPlayVoiceOrTts;

        if (studyTTS != null) {
            studyTtsVoices.clear();
            Set<Voice> voices = null;
            try {
                voices = studyTTS.getVoices();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (voices != null && !voices.isEmpty()) {
                studyTtsVoices = TtsVoiceListUtil.getTotalCountToPlayVoiceOrTts(context, totalCountToPlayVoiceOrTts, voices, studyLang);
            }
        }
    }

    public boolean isIncludeMyVoice() {
        return sharedPreferences.getIncludeMyVoice();
    }

    public boolean isIncludeMeaning() {
        return includeMeaning;
    }

    public void setIncludeMeaning(boolean includeMeaning) {
        this.includeMeaning = includeMeaning;
    }

    private void finishCurrentItem(String utteranceId) {
        DLog.d(TAG, "finishCurrentItem - playlistPos=" + playlistPos + " - size=" + itemMap.size() + " - isAraPlayer=" + isAraPlayer);
        if (studyListener != null) {
            studyListener.onDone(utteranceId);
        }
        if (playlistPos < itemMap.size() - 1) {
            playlistPos++; // play next item
            initPos();
            playVoca();
        } else if (playOnce || isPlayMyVoiceOnce) {
            if (isPlayMyVoiceOnce && hasOnPlayMyVoiceOnceListener()) {
                onPlayMyVoiceOnce.onDone(utteranceId);
            }
            initPlaylist();
        } else {
            playlistPos = 0; // play first item
            if (isPlayingMultiFiles() && !isAraPlayer) {
                playVoiceFile(BaseVoca.getVoiceFileOnLocal(context, Constant.FILE.END_LIST_FILE_NAME), mp -> {
                    if (isPlaying && mp != null) {
                        mp.reset();
                        initPos();
                        if (isConversationBook && sharedPreferences.isPlayMotherTongueOnlyFirstRoundFinishOnConversation()) {
                            isPlayMotherTongueFirstRoundOnConversation = true;
                        } else {
                            isPlayMotherTongueFirstRoundOnConversation = false;
                        }
                        if (onShufflePlayListListener == null) {
                            playVoca();
                        } else {
                            onShufflePlayListListener.onShuffle();
                        }
                    }
                });
            } else {
                initPos();
                if (isAraPlayer) {
                    callOnFinishedPlayVoice();
                } else {
                    if (onShufflePlayListListener == null) {
                        playVoca();
                    } else {
                        onShufflePlayListListener.onShuffle();
                    }
                }
            }
        }
    }

    public void pause() {
        updateValue_isPlaying(false); //isPlaying = false;
        removeRunnables();
        if (motherTongueTTS != null) {
            motherTongueTTS.stop();
        }
        if (studyTTS != null) {
            studyTTS.stop();
        }
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        initPos();
    }

    public void resume(int pos) {
        updateValue_isPlaying(true); //isPlaying = true;
        if (pos > -1
                && pos < itemMap.size()
                && pos != playlistPos) {
            try {
                studyListener.onDone(String.valueOf(((IVocaFullPlayTTSItem) itemMap.keySet().toArray()[playlistPos]).getVIVocaId()));
            } catch (Exception e) {
//            e.printStackTrace();
            }
            playlistPos = pos;
        }
        playVoca();
    }

    public void stop() {
        updateValue_isPlaying(false); //isPlaying = false;
        setPlayingMultiFiles(false);
        removeRunnables();
        if (motherTongueTTS != null) {
            motherTongueTTS.stop();
        }
        if (studyTTS != null) {
            studyTTS.stop();
        }
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try {
            if (studyListener != null) {
                String value = String.valueOf(((IVocaFullPlayTTSItem) itemMap.keySet().toArray()[playlistPos]).getVIVocaId());
                studyListener.onDone(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initPlaylist();
    }

    public void destroy() {
        if (motherTongueTTS != null) {
            motherTongueTTS.stop();
            motherTongueTTS.shutdown();
            motherTongueTTS = null;
        }
        if (studyTTS != null) {
            studyTTS.stop();
            studyTTS.shutdown();
            studyTTS = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void play(LinkedHashMap<IVocaFullPlayTTSItem, ArrayList<String>> itemMap) {
        if (!itemMap.isEmpty()) {
            updateValue_isPlaying(true); //isPlaying = true;
            this.itemMap.clear();
            this.itemMap.putAll(itemMap);
            initPos();
            playlistPos = 0;
            playVoca();
        } else if (isAraPlayer() && isIncludeMotherTongueSubtitle()) {
            playSubtitle();
        }
    }

    private void playVoca() {
        if (itemMap.isEmpty()) {
            finishCurrentItem(Constant.BASE_BLANK);
            return;
        }
        IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) itemMap.keySet().toArray()[playlistPos];
        if (isPlayMyVoiceOnce) {
            myVoiceFile = BaseVoca.getVoiceFileInApp(context, voca.getVIPath());
//            myVoiceFile = Voca.getVoiceFileOnLocal(Voca.getVoiceFolderOnLocal(context), voca.getVIPath());
            playMyVoiceOnce(voca.getVIVocaId().toString());
        } else if (motherTongueTTS != null) {
            DLog.d(TAG, "playVoca - isAraPlayer=" + isAraPlayer() + " - isIncludeMotherTongueSubtitle=" + isIncludeMotherTongueSubtitle() +
                    " - getSubtitleMotherTongue=" + getSubtitleMotherTongue());
            if (isToPlaySubtitleBothLang()) { //Dalnim modified to play subtitle study lang too
                playSubtitle();
            } else if (includeMeaning
                    && isPlayingMultiFiles()
                    && (!isStudentVoice || playlistPos == 0)
                    && !TextUtils.isEmpty(BaseVoca.getMeaningTTS(voca, context))) {
                if (isAraPlayer() && !isPlayDifficultWordsBeforePlayingSubtitle()) {
                    motherTongueTTS.playSilentUtterance(Constant.SILENCE_TIME, TextToSpeech.QUEUE_FLUSH, String.valueOf(voca.getVIVocaId()));
                    return;
                }
                if (isPlayMotherTongueFirstRoundOnConversation) {
                    //영어회화 AB모드에서는 한 라운드가 끝나면 모국어는 더 이상 플레이하지 않는다. 첫번째 라운드만 플레이 해준다.
                    motherTongueTTS.playSilentUtterance(0, TextToSpeech.QUEUE_FLUSH, String.valueOf(voca.getVIVocaId()));
                } else {
                    updateMotherTongueLocale(voca);
                    motherTongueTTS.speak(BaseVoca.getMeaningTTS(voca, context), TextToSpeech.QUEUE_FLUSH, null, String.valueOf(voca.getVIVocaId()));
                    motherTongueTTS.playSilentUtterance(Constant.SILENCE_TIME, TextToSpeech.QUEUE_ADD, null);
                }
            } else {
                //그냥 스피커 버튼을 눌러서 한개만 재생할때는 모국어는 플레이 하지않는다. (화면을 보고 있기 때문이다.)
                motherTongueTTS.playSilentUtterance(0, TextToSpeech.QUEUE_FLUSH, String.valueOf(voca.getVIVocaId()));
            }
        } else {
            updateValue_playVoiceFilePos(0); //subPlaylistPos = 0;
            playVoiceOrTTS();
        }
    }

    //Dalnim added
    //--------
    private boolean isToPlaySubtitleBothLang() {
        return isTurnOnPlaySubtitle()
                && isHasSubtitleBothToPlay();
    }

    private boolean isToPlaySubtitleMotherTongue() {
        return isTurnOnPlaySubtitle()
                && isHasSubtitleMotherTongueToPlay();
    }
    private boolean isToPlaySubtitleStudyLang() {
        return isTurnOnPlaySubtitle()
                && isHasSubtitleStudyLangToPlay();
    }
    private boolean isTurnOnPlaySubtitle() {
        return isAraPlayer()
                && isIncludeMotherTongueSubtitle();
    }

    private boolean isHasSubtitleBothToPlay() {
        return !Utils.isEmpty((getSubtitleMotherTongue().trim() + getSubtitleStudyLang().trim()));
    }
    private boolean isHasSubtitleMotherTongueToPlay() {
        return !Utils.isEmpty(getSubtitleMotherTongue().trim());
    }
    private boolean isHasSubtitleStudyLangToPlay() {
        return !Utils.isEmpty(getSubtitleStudyLang().trim());
    }

    private void playSubtitle() {
        playSubtitleStudyLang();
        playSubtitleMotherTongue();
    }
    private void playSubtitleMotherTongue() {
        if (!Utils.isEmpty(getSubtitleMotherTongue())) {
            motherTongueTTS.speak(getSubtitleMotherTongue(), TextToSpeech.QUEUE_FLUSH, null, getSubtitleMotherTongue());
            motherTongueTTS.playSilentUtterance(Constant.SILENCE_TIME, TextToSpeech.QUEUE_ADD, null);
        }
    }

    private void playSubtitleStudyLang() {
        if (!Utils.isEmpty(getSubtitleStudyLang())) {
            studyTTS.speak(getSubtitleStudyLang(), TextToSpeech.QUEUE_FLUSH, null, getSubtitleStudyLang());
            studyTTS.playSilentUtterance(Constant.SILENCE_TIME, TextToSpeech.QUEUE_ADD, null);
        }
    }
    //--------
    private void playVoiceOrTTS() {
        File voiceFolder = BaseVoca.getVoiceFolderOnLocal(context);
        IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) itemMap.keySet().toArray()[playlistPos];
        String utteranceId = String.valueOf(voca.getVIVocaId());
        if (isAraPlayer() && !isPlayDifficultWordsBeforePlayingSubtitle()) {
            playlistPos = itemMap.size();
            finishCurrentItem(utteranceId);
            return;
        }
        if (!isStudentVoice && isIncludeMyVoice() && myVoiceFile == null && voca.hasVIVoiceFile()) {
            myVoiceFile = BaseVoca.getVoiceFileInApp(context, BaseVoca.getMyOutputRecordingFileName(context, voca));
        }
        String text = BaseVoca.getVocaTTS(voca);
        ArrayList<String> voiceFileNames = itemMap.get(voca);
        DLog.d(TAG, "fileNames=" + voiceFileNames);

        if ((playVoiceOrTtsPos == 0) && (isPlayTTSAtFirst)) {
            playTTS(voca);
        } else {
            if (Utils.isEmpty(voiceFileNames)) {
                playTTS(voca);
            } else {
                if (hasVoiceFileInDevice(voiceFolder, voiceFileNames, playVoiceFilePos)) {
                    File file = BaseVoca.getVoiceFileInApp(context, voiceFileNames.get(playVoiceFilePos));
                    if (file.exists()) {
//                        updateValue_playVoiceFilePos(playVoiceFilePos + 1);
                        updateValue_playVoiceOrTtsPos(playVoiceOrTtsPos + 1);
                        playVoiceFile(file, mp -> {
                            if (isPlaying && mp != null) {
                                mp.reset();
                                playMyVoice();
                            }
                        });
                    } else {
                        playTTS(voca);
                    }
                } else {
                    playTTS(voca);
                }
                updateValue_playVoiceFilePos(playVoiceFilePos + 1);
            }
        }

//        if ((playVoiceOrTtsPos == 0) && (isPlayTTSAtFirst)) {
//            playTTS(voca);
//        } else {
//            if (fileNames == null || fileNames.isEmpty() || !hasVoiceFile(voiceFolder, fileNames)) {
//                if (isStudentVoice) {
//                    if (isPlayingMulti() && retry < Constant.PLAY_STUDENT_VOICE_RETRY_MAX_TIME) {
//                        waitForRetry();
//                    } else {
//                        finishCurrentItem(utteranceId);
//                    }
//                } else {
//                    playTTS(voca);
//                }
//            } else if (Utils.isIndexInsideRange(fileNames, playVoiceFilePos)){
//                //Loop until play voice file.
//                boolean played = false;
//                do {
//                    File file = Voca.getVoiceFileOnLocal(voiceFolder, fileNames.get(playVoiceFilePos));
//                    updateValue_playVoiceFilePos(playVoiceFilePos + 1);
//                    if (file.exists()) {
//                        updateValue_playVoiceOrTtsPos(playVoiceOrTtsPos + 1);
//                        playVoiceFile(file, mp -> {
//                            if (isPlaying && mp != null) {
//                                mp.reset();
//                                playMyVoice();
//                            }
//                        });
//                        played = true;
//                    } else if (playVoiceFilePos >= fileNames.size()) {
//                        updateValue_playVoiceFilePos(0);
//                    }
//                } while (!played);
//            } else {
//                playTTS(voca);
//            }
//        }

    }

    private int getVoiceFileIndex(File voiceFolder, ArrayList<String> fileNames) {
        int index = -1;
        if (!Utils.isEmpty(fileNames)) {
            for (String fileName : fileNames) {
                index++;
                if (index < playVoiceFilePos)
                    continue;

                if (!Utils.isEmpty(fileName)) {
                    if (BaseFileUtil.isVoiceFileExist(voiceFolder.getPath(), fileName))
                        return index;
                }
            }
        }
        return index;
    }

    private boolean hasVoiceFileInDevice(File voiceFolder, ArrayList<String> fileNames, int playVoiceFilePos) {
        boolean result = false;
        if (Utils.isIndexInsideRange(fileNames, playVoiceFilePos)) {
            String fileName = fileNames.get(playVoiceFilePos);
            if (BaseFileUtil.isVoiceFileExist(voiceFolder.getPath(), fileName)) {
                result = true;
            }
        }
        return result;

//        for (int i = playVoiceFilePos; i < fileNames.size(); i++) {
//            String fileName = fileNames.get(i);
//            if (!Utils.isEmpty(fileName)) {
//                if (BaseFileUtil.isVoiceFileExist(voiceFolder.getPath(), fileName)) {
//                    updateValue_playVoiceFilePos(i);
//                    return true;
//                }
//            }
//        }
//        return false;
    }

    private void playMyVoice() {
        DLog.d(TAG, "playMyVoice");
        IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) itemMap.keySet().toArray()[playlistPos];
        if (isStudentVoice) {

            String utteranceId = String.valueOf(voca.getVIVocaId());
            finishCurrentItem(utteranceId);
        } else {
            if (myVoiceFile != null && myVoiceFile.exists()) {
                playVoiceFile(myVoiceFile, mp -> {
                    if (isPlaying && mp != null) {
                        mp.reset();
                        playNextCount(voca);
                    }
                });
            } else {
                playNextCount(voca);
            }
        }
    }

    private void playNextCount(IVocaFullPlayTTSItem voca) {
        int totalCountToPlayVoiceOrTts = RepeatUtil.getRepeatValueWithoutDifficultWords(context, voca);
        DLog.d(TAG, "playNextCount - playVoiceCount=" + playVoiceOrTtsPos + " - readCount=" + totalCountToPlayVoiceOrTts);
        int pauseTimeToRepeatTts = getPauseTimeToRepeatTts();
        if (pauseTimeToRepeatTts == 0) {
            //Don't need to use alarmManager to pause a little.
            if (playVoiceOrTtsPos < totalCountToPlayVoiceOrTts || isPlayEndlessly) {
                handlePlayVoiceOrTts();
            } else {
                handleFinishCurrentItem();
            }
        } else {
            long alarmTimeAtUTC = System.currentTimeMillis() + pauseTimeToRepeatTts;
            PendingIntent pendingIntent;
            if (playVoiceOrTtsPos < totalCountToPlayVoiceOrTts || isPlayEndlessly) {
                //            handler.postDelayed(playVoiceOrTtsRunnable, getPauseTimeToRepeatTts());
                    pendingIntent = getPlayVoiceOrTTSPendingIntent();
            } else {
                //            handler.postDelayed(finishCurrentItemRunnable, getPauseTimeToRepeatTts());
                    pendingIntent = getFinishCurrentTTSPendingIntent();
            }
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeAtUTC, pendingIntent);
        }
    }

    private int getPauseTimeToRepeatTts() {
        int pauseTimeToRepeatTts = 0;

        if (isPauseTimeToRepeatTts()) {
            int timePerChar = studyLang.getPauseTimeToRepeatTts();
            IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) itemMap.keySet().toArray()[playlistPos];
            String aaa = voca.getVIVoca();
            if (!Utils.isEmpty(aaa)) {
                int charCount = aaa.replace(" ", "").length();
                pauseTimeToRepeatTts = charCount * timePerChar;
                int minMilliTimeToShowToast = 1500;
                if (pauseTimeToRepeatTts > minMilliTimeToShowToast)
                    ToastUtil.getInstance(context).show(R.string.toast_repeat_after_tts);
            }
        }
//        if (UserUtil.isDebugOrAdminUser(context))
//            ToastUtil.getInstance(context).show("pauseTimeToRepeatTts : " + pauseTimeToRepeatTts);
        return pauseTimeToRepeatTts;
    }

    private boolean isPauseTimeToRepeatTts() {
        return SharedPreferencesDB.getInstance(context).isPauseTimeToRepeatTts();
    }

    private void shuffleVoiceFileNames() {
        IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) itemMap.keySet().toArray()[playlistPos];
        ArrayList<String> voiceFileNames = itemMap.get(voca);
        Collections.shuffle(voiceFileNames);
        itemMap.put(voca, voiceFileNames);
    }

    private void playVoiceFile(File file, MediaPlayer.OnCompletionListener listener) {
        DLog.d(TAG, "playVoice + file=" + file.toString());
        try {
            Uri uri = Uri.fromFile(file);
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(listener);
            setMediaPlayerSpeed();
            mediaPlayer.start();
        } catch (Exception e) {
//            e.printStackTrace();

            listener.onCompletion(mediaPlayer);
        }
    }

    public void setMediaPlayerSpeed() {
        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(studyLang.getMediaPlaySpeed()));
    }

    private void playTTS(IVocaFullPlayTTSItem voca) {
        String utteranceId = String.valueOf(voca.getVIVocaId());
        String text = BaseVoca.getVocaTTS(voca);

        DLog.d(TAG, "playTTS - text=" + text + " - utteranceId=" + utteranceId + " - studyTTS=" + studyTTS);
        updateValue_playVoiceOrTtsPos(playVoiceOrTtsPos + 1); //playVoiceCount++;
        updateValue_playTtsPos(playTtsPos + 1);
        if (studyTTS == null) {
            playNextCount(voca);
        } else if (!Utils.isIndexInsideRange(studyTtsVoices, playTtsPos)) {
            //음성파일 재생후, TTS를 재생하는데, TTS재생이 끝났으면, 다시 음성파일 재생부터 할려고 pos를 초기화 시킨다.
            updateValue_playVoiceFilePos(0);
            updateValue_playTtsPos(-1);
            playNextCount(voca);
        } else {
            if (!studyTtsVoices.isEmpty()) {
//                if (++studyVoicePos >= studyTtsVoices.size()) {
//                    studyVoicePos = 0;
//                }
//                Voice voice = studyTtsVoices.get(studyVoicePos);
                if (playTtsPos >= studyTtsVoices.size()) {
                    updateValue_playTtsPos(-1);
                }
                setStudyTtsVoice(voca);
//                Voice voice = studyTtsVoices.get(playTtsPos);
//                if (Utils.isEmpty(voca.getPersonAB())) {
//                    studyTTS.setVoice(voice);
//                } else {
//                    //A와 B의 발음을 다른 사람으로 한다.
//                    String personAB = voca.getPersonAB();
//                    if (personAB.equals("B")) {
//                        if (BaseCollectionUtil.isIndexInsideList(studyTtsVoices, playTtsPos + 1)) {
//                            voice = studyTtsVoices.get(playTtsPos + 1);
//                            studyTTS.setVoice(voice);
//                        } else {
//                            studyTTS.setVoice(voice);
//                        }
//                    } else {
//                        studyTTS.setVoice(voice);
//                    }
//                }
            }
            studyTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            studyTTS.playSilentUtterance(Constant.SILENCE_TIME, TextToSpeech.QUEUE_ADD, utteranceId);
        }
    }

    private void setStudyTtsVoice(IVocaFullPlayTTSItem voca) {
        Voice voice = studyTtsVoices.get(playTtsPos);
        if (Utils.isEmpty(voca.getPersonAB())) {
            studyTTS.setVoice(voice);
        } else {
            String personAB = voca.getPersonAB();
            boolean isPersonBAndValidIndex = personAB.equals("B") && BaseCollectionUtil.isIndexInsideList(studyTtsVoices, playTtsPos + 1);

            if (isPersonBAndValidIndex) {
                voice = studyTtsVoices.get(playTtsPos + 1);
            }

            studyTTS.setVoice(voice);
        }
    }

    public void playStudentVoice(LinkedHashMap<IVocaFullPlayTTSItem, ArrayList<String>> itemMap) {
        DLog.d(TAG, "playStudentVoice");
        isStudentVoice = true;
        play(itemMap);
    }

    public void play1stNativeSpeakerAndMyVoiceOnce(LinkedHashMap<IVocaFullPlayTTSItem, ArrayList<String>> itemMap) {
        if (!itemMap.isEmpty()) {
            updateValue_isPlaying(true); //isPlaying = true;
            playOnce = true;
            this.itemMap.putAll(itemMap);
            playlistPos = 0;
            updateValue_playVoiceFilePos(0);//subPlaylistPos = 0;
            IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) itemMap.keySet().toArray()[playlistPos];
            File voiceFolder = BaseVoca.getVoiceFolderOnLocal(context);
            myVoiceFile = BaseVoca.getVoiceFileOnLocal(voiceFolder, voca.getVIPath());
            String text = BaseVoca.getVocaTTS(voca);
            final String utteranceId = String.valueOf(voca.getVIVocaId());
            if (motherTongueListener != null) {
                motherTongueListener.onStart(utteranceId);
            }
            ArrayList<String> fileNames = itemMap.get(voca);
            if (fileNames == null || fileNames.isEmpty()) {
                playTTS1stNativeSpeaker(text, utteranceId);
            } else {
                File file = BaseVoca.getVoiceFileOnLocal(voiceFolder, fileNames.get(playVoiceFilePos));
                if (file.exists()) {
                    playVoiceFile(file, mp -> {
                        if (isPlaying && mp != null) {
                            mp.reset();
                            playMyVoiceOnce(utteranceId);
                        }
                    });
                } else {
                    playTTS1stNativeSpeaker(text, utteranceId);
                }
            }
        }
    }

    private void playTTS1stNativeSpeaker(String text, String utteranceId) {
        updateValue_playVoiceOrTtsPos(playVoiceOrTtsPos + 1); //playVoiceCount++;
        if (studyTTS == null) {
            playMyVoiceOnce(utteranceId);
        } else {
            studyTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            studyTTS.playSilentUtterance(Constant.SILENCE_TIME, TextToSpeech.QUEUE_ADD, utteranceId);
        }
    }

    private void playMyVoiceOnce(final String utteranceId) {
        if (myVoiceFile != null && myVoiceFile.exists()) {
            playVoiceFile(myVoiceFile, mp -> {
                if (isPlaying && mp != null) {
                    mp.reset();
                    finishCurrentItem(utteranceId);
                }
            });
        } else {
            finishCurrentItem(utteranceId);
        }
    }

    private void waitForRetry() {
        DLog.d(TAG, "waitForRetry");
        handler.postDelayed(playVoiceOrTtsRunnable, Constant.PLAY_STUDENT_VOICE_DELAY_TIME);
    }

    private boolean isPlayingMultiFiles() {
        return isPlayingMultiFiles;
    }

    public void setPlayingMultiFiles(boolean playingMultiFiles) {
        isPlayingMultiFiles = playingMultiFiles;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isAraPlayer() {
        return isAraPlayer;
    }

    public void setAraPlayer(boolean araPlayer) {
        isAraPlayer = araPlayer;
    }

    public boolean isIncludeMotherTongueSubtitle() {
        return includeMotherTongueSubtitle;
    }

    public void setIncludeMotherTongueSubtitle(boolean includeMotherTongueSubtitle) {
        this.includeMotherTongueSubtitle = includeMotherTongueSubtitle;
    }

    public boolean isPlayDifficultWordsBeforePlayingSubtitle() {
        return playDifficultWordsBeforePlayingSubtitle;
    }

    public void setPlayDifficultWordsBeforePlayingSubtitle(boolean playDifficultWordsBeforePlayingSubtitle) {
        this.playDifficultWordsBeforePlayingSubtitle = playDifficultWordsBeforePlayingSubtitle;
    }

    public String getSubtitleMotherTongue() {
        return Utils.isEmpty(subtitleMotherTongue) ? "" : subtitleMotherTongue;
    }

    public void setSubtitleMotherTongue(String subtitleMotherTongue) {
        this.subtitleMotherTongue = subtitleMotherTongue;
    }

    public String getSubtitleStudyLang() {
        return Utils.isEmpty(subtitleStudyLang) ? "" : subtitleStudyLang;
    }

    public void setSubtitleStudyLang(String subtitleStudyLang) {
        this.subtitleStudyLang = subtitleStudyLang;
    }

    private void callOnFinishedPlayVoice() {
        stop();
        resetPlayPlayer();
        if (motherTongueListener != null) {
            motherTongueListener.onDone(Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.KEY_PLAY_DONE);
        }
    }

    public void resetPlayPlayer() {
        DLog.d(TAG, "resetPlayPlayer");
        setAraPlayer(false);
        setIncludeMeaning(false);
        setIncludeMotherTongueSubtitle(false);
        setPlayDifficultWordsBeforePlayingSubtitle(false);
        setSubtitleMotherTongue(Constant.BASE_BLANK);
    }

    public boolean isTTSSpeaking() {
        return motherTongueTTS.isSpeaking() || studyTTS.isSpeaking();
    }

    private void updateValue_isPlaying(boolean value) {
        this.isPlaying = value;
    }

    private void updateValue_playlistPos(int value) {
        this.playlistPos = value;
    }
    private void updateValue_playVoiceOrTtsPos(int value) {
        this.playVoiceOrTtsPos = value;
    }
    private void updateValue_playVoiceFilePos(int value) {
        this.playVoiceFilePos = value;
    }
    private void updateValue_playTtsPos(int value) {
        this.playTtsPos = value;
    }

    public void setOnShufflePlayListListener(OnShufflePlayListListener onShufflePlayListListener) {
        this.onShufflePlayListListener = onShufflePlayListListener;
    }

    public void setPlayEndlessly(boolean playEndlessly) {
        isPlayEndlessly = playEndlessly;
    }

    public void setConversationBook(boolean conversationBook) {
        isConversationBook = conversationBook;
    }
}
