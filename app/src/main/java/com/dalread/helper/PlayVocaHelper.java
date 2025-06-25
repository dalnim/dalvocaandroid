package com.dalread.helper;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.text.TextUtils;

import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;

//Same as PlayTTSHelper but will delete this later
@Deprecated
public class PlayVocaHelper {

    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private ArrayList<Voice> studyVoices;
    private TextToSpeech motherTongueTTS;
    private UtteranceProgressListener motherTongueListener;
    public TextToSpeech studyTTS;
    private UtteranceProgressListener studyListener;
    private String studyLocaleName;
    private MediaPlayer mediaPlayer;
    private int readCount;
    private boolean includeMyVoice;
    private boolean includeMeaning;
    private LinkedHashMap<IVocaFullPlayTTSItem, ArrayList<String>> itemMap;
    private int playlistPos;
    private int subPlaylistPos;
    private int studyVoicePos;
    private File myVoice;
    private int playVoiceCount;
    private boolean isPlaying;
    private boolean isPlayingMulti;
    private boolean isStudentVoice;
    private boolean playOnce;
    private Handler handler;
    private Runnable retryRunnable;
    private int retry;
    private boolean isAraPlayer = false;
    // https://github.com/dalnim/IssueOnly/issues/118#issuecomment-665396347
    private boolean includeMotherTongueSubtitle;
    private boolean playDifficultWordsBeforePlayingSubtitle;
    private String subtitleMotherTongue;
    private String subtitleStudyLang; //Dalnim added

    public PlayVocaHelper(Context context) {
        this.context = context;
        studyVoices = new ArrayList<>();
        handler = new Handler();
        retryRunnable = () -> {
            retry++;
            DLog.i(TAG, "retry = " + retry);
            playVoiceOrTTS();
        };
        initPlaylist();
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
        initPos();
    }

    private void initPos() {
        subPlaylistPos = studyVoicePos = -1;
        myVoice = null;
        playVoiceCount = 0;
        retry = 0;
    }

    public void initMotherTongueTTS(Locale locale) {
        if (motherTongueTTS != null) return;
        motherTongueTTS = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                motherTongueTTS.setLanguage(locale);
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
                            subPlaylistPos = 0;
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

    public void initStudyTTS(Locale locale, int readCount, int speed) {
        if (studyTTS != null) return;
        DLog.d(TAG, "initStudyTTS");
        studyTTS = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                DLog.d(TAG, "initStudyTTS SUCCESS");
                studyTTS.setLanguage(locale);
                setReadCount(readCount);
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
                setStudyTTSSpeed(speed);
            } else {
                DLog.d(TAG, "initStudyTTS ERROR");
                studyTTS = null;
            }
        }, "com.google.android.tts");
        studyLocaleName = locale.getDisplayName();
    }

    public void setStudyTTSSpeed(int speed) {
        if (studyTTS != null) {
            float speechRate = speed / 50f;
            if (speechRate < .5f) {
                speechRate = .5f;
            }
            DLog.i(TAG, "speed = " + speed);
            DLog.i(TAG, "speechRate = " + speechRate);
            studyTTS.setSpeechRate(speechRate);
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
    }

    public void initMediaPlayer() {
        if (mediaPlayer != null) return;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;

        if (studyTTS != null) {
            studyVoices.clear();
            Set<Voice> voices = null;
            try {
                voices = studyTTS.getVoices();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (voices != null && !voices.isEmpty()) {
                Set<Voice> setVoices = new HashSet<>();
                for (Voice voice : voices) {
                    if (voice.getLocale() != null
                            && studyLocaleName.equals(voice.getLocale().getDisplayName())
                            && !voice.isNetworkConnectionRequired()) {
                        setVoices.add(voice);
                    }
                    int count = setVoices.size();
                    if (count == Constant.VOICE_MAX_SIZE || count == readCount) {
                        break;
                    }
                }
                studyVoices = new ArrayList<>(setVoices);
                if (!studyVoices.isEmpty() && studyVoices.size() < readCount) {
                    int count = studyVoices.size();
                    int pos = 0;
                    while (studyVoices.size() < readCount) {
                        studyVoices.add(studyVoices.get(pos));
                        if (++pos == count) {
                            pos = 0;
                        }
                    }
                }
            }
        }
    }

    public void setIncludeMyVoice(boolean includeMyVoice) {
        this.includeMyVoice = includeMyVoice;
    }

    public boolean isIncludeMyVoice() {
        return includeMyVoice;
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
        } else if (playOnce) {
            initPlaylist();
        } else {
            playlistPos = 0; // play first item
            if (isPlayingMulti() && !isAraPlayer) {
                playVoice(BaseVoca.getVoiceFileOnLocal(context, Constant.FILE.END_LIST_FILE_NAME), mp -> {
                    if (isPlaying && mp != null) {
                        mp.reset();
                        initPos();
                        playVoca();
                    }
                });
            } else {
                initPos();
                if (isAraPlayer) {
                    callOnFinishedPlayVoice();
                } else {
                    playVoca();
                }
            }
        }
    }

    public void pause() {
        isPlaying = false;
        handler.removeCallbacks(retryRunnable);
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
        isPlaying = true;
        if (pos > -1
                && pos < itemMap.size()
                && pos != playlistPos) {
            try {
                studyListener.onDone(String.valueOf(((IVocaFullPlayTTSItem) itemMap.keySet().toArray()[playlistPos]).getVIId()));
            } catch (Exception e) {
//            e.printStackTrace();
            }
            playlistPos = pos;
        }
        playVoca();
    }

    public void stop() {
        isPlaying = false;
        setPlayingMulti(false);
        handler.removeCallbacks(retryRunnable);
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
            studyListener.onDone(String.valueOf(((IVocaFullPlayTTSItem) itemMap.keySet().toArray()[playlistPos]).getVIId()));
        } catch (Exception e) {
//            e.printStackTrace();
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
            isPlaying = true;
            this.itemMap.clear();
            this.itemMap.putAll(itemMap);
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
        if (motherTongueTTS != null) {
            DLog.d(TAG, "playVoca - isAraPlayer=" + isAraPlayer() + " - isIncludeMotherTongueSubtitle=" + isIncludeMotherTongueSubtitle() +
                    " - getSubtitleMotherTongue=" + getSubtitleMotherTongue());
            if (isToPlaySubtitleBothLang()) { //Dalnim modified to play subtitle study lang too
                playSubtitle();
            } else if (includeMeaning
                    && isPlayingMulti()
                    && (!isStudentVoice || playlistPos == 0)
                    && !TextUtils.isEmpty(BaseVoca.getMeaningTTS(voca, context))) {
                if (isAraPlayer() && !isPlayDifficultWordsBeforePlayingSubtitle()) {
                    motherTongueTTS.playSilentUtterance(Constant.SILENCE_TIME, TextToSpeech.QUEUE_FLUSH, String.valueOf(voca.getVIId()));
                    return;
                }
                motherTongueTTS.speak(BaseVoca.getMeaningTTS(voca, context), TextToSpeech.QUEUE_FLUSH, null, String.valueOf(voca.getVIId()));
                motherTongueTTS.playSilentUtterance(Constant.SILENCE_TIME, TextToSpeech.QUEUE_ADD, null);
            } else {
                motherTongueTTS.playSilentUtterance(Constant.SILENCE_TIME, TextToSpeech.QUEUE_FLUSH, String.valueOf(voca.getVIId()));
            }
        } else {
            subPlaylistPos = 0;
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
        DLog.d(TAG, "playVoiceOrTTS");
        File voiceFolder = BaseVoca.getVoiceFolderOnLocal(context);
        IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) itemMap.keySet().toArray()[playlistPos];
        DLog.d(TAG, "isStudentVoice=" + isStudentVoice + " - includeMyVoice=" + includeMyVoice);
        String utteranceId = String.valueOf(voca.getVIId());
        if (isAraPlayer() && !isPlayDifficultWordsBeforePlayingSubtitle()) {
            playlistPos = itemMap.size();
            finishCurrentItem(utteranceId);
            return;
        }
        if (!isStudentVoice && includeMyVoice && myVoice == null && !Utils.isEmpty(voca.getVIPath())) {
            myVoice = BaseVoca.getVoiceFileOnLocal(voiceFolder, voca.getVIPath());
        }
        String text = BaseVoca.getVocaTTS(voca);
        ArrayList<String> fileNames = itemMap.get(voca);
        DLog.d(TAG, "fileNames=" + fileNames);
        if (fileNames == null || fileNames.isEmpty() || !hasVoiceFile(voiceFolder, fileNames)) {
            if (isStudentVoice) {
                if (isPlayingMulti() && retry < Constant.PLAY_STUDENT_VOICE_RETRY_MAX_TIME) {
                    waitForRetry();
                } else {
                    finishCurrentItem(utteranceId);
                }
            } else {
                playTTS(text, utteranceId);
            }
        } else {
            boolean played = false;
            do {
                File file = BaseVoca.getVoiceFileOnLocal(voiceFolder, fileNames.get(subPlaylistPos));
                if (file.exists()) {
                    playVoiceCount++;
                    playVoice(file, mp -> {
                        if (isPlaying && mp != null) {
                            mp.reset();
                            playMyVoice();
                        }
                    });
                    played = true;
                } else if (++subPlaylistPos >= fileNames.size()) {
                    subPlaylistPos = 0;
                }
            } while (!played);
        }
    }

    private boolean hasVoiceFile(File voiceFolder, ArrayList<String> fileNames) {
        for (String fileName : fileNames) {
            if (!Utils.isEmpty(fileName)) {
                File file = BaseVoca.getVoiceFileOnLocal(voiceFolder, fileName);
                if (file.exists()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void playMyVoice() {
        DLog.d(TAG, "playMyVoice");
        if (isStudentVoice) {
            IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) itemMap.keySet().toArray()[playlistPos];
            String utteranceId = String.valueOf(voca.getVIId());
            finishCurrentItem(utteranceId);
        } else {
            if (myVoice != null && myVoice.exists()) {
                playVoice(myVoice, mp -> {
                    if (isPlaying && mp != null) {
                        mp.reset();
                        playNextCount();
                    }
                });
            } else {
                playNextCount();
            }
        }
    }

    private void playNextCount() {
        DLog.d(TAG, "playNextCount - playVoiceCount=" + playVoiceCount + " - readCount=" + readCount);
        IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) itemMap.keySet().toArray()[playlistPos];
        String utteranceId = String.valueOf(voca.getVIId());
        if (playVoiceCount < readCount) {
            if (itemMap.get(voca) != null && ++subPlaylistPos < itemMap.get(voca).size()) {
                playVoiceOrTTS();
            } else {
                playTTS(BaseVoca.getVocaTTS(voca), utteranceId);
            }
        } else {
            finishCurrentItem(utteranceId);
        }
    }

    private void playVoice(File file, MediaPlayer.OnCompletionListener listener) {
        DLog.d(TAG, "playVoice + file=" + file.toString());
        try {
            Uri uri = Uri.fromFile(file);
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(listener);
            mediaPlayer.start();
        } catch (Exception e) {
//            e.printStackTrace();

            listener.onCompletion(mediaPlayer);
        }
    }

    private void playTTS(String text, String utteranceId) {
        DLog.d(TAG, "playTTS - text=" + text + " - utteranceId=" + utteranceId + " - studyTTS=" + studyTTS);
        playVoiceCount++;
        if (studyTTS == null) {
            playNextCount();
        } else {
            if (!studyVoices.isEmpty()) {
                if (++studyVoicePos >= studyVoices.size()) {
                    studyVoicePos = 0;
                }
                Voice voice = studyVoices.get(studyVoicePos);
                studyTTS.setVoice(voice);
            }
            studyTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            studyTTS.playSilentUtterance(Constant.SILENCE_TIME, TextToSpeech.QUEUE_ADD, utteranceId);
        }
    }

    public void playStudentVoice(LinkedHashMap<IVocaFullPlayTTSItem, ArrayList<String>> itemMap) {
        DLog.d(TAG, "playStudentVoice");
        isStudentVoice = true;
        play(itemMap);
    }

    public void play1stNativeSpeakerAndMyVoiceOnce(LinkedHashMap<IVocaFullPlayTTSItem, ArrayList<String>> itemMap) {
        if (!itemMap.isEmpty()) {
            isPlaying = true;
            playOnce = true;
            this.itemMap.putAll(itemMap);
            playlistPos = 0;
            subPlaylistPos = 0;
            IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) itemMap.keySet().toArray()[playlistPos];
            File voiceFolder = BaseVoca.getVoiceFolderOnLocal(context);
            myVoice = BaseVoca.getVoiceFileOnLocal(voiceFolder, voca.getVIPath());
            String text = BaseVoca.getVocaTTS(voca);
            final String utteranceId = String.valueOf(voca.getVIId());
            if (motherTongueListener != null) {
                motherTongueListener.onStart(utteranceId);
            }
            ArrayList<String> fileNames = itemMap.get(voca);
            if (fileNames == null || fileNames.isEmpty()) {
                playTTS1stNativeSpeaker(text, utteranceId);
            } else {
                File file = BaseVoca.getVoiceFileOnLocal(voiceFolder, fileNames.get(subPlaylistPos));
                if (file.exists()) {
                    playVoice(file, mp -> {
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
        playVoiceCount++;
        if (studyTTS == null) {
            playMyVoiceOnce(utteranceId);
        } else {
            studyTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            studyTTS.playSilentUtterance(Constant.SILENCE_TIME, TextToSpeech.QUEUE_ADD, utteranceId);
        }
    }

    private void playMyVoiceOnce(final String utteranceId) {
        if (myVoice != null && myVoice.exists()) {
            playVoice(myVoice, mp -> {
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
        handler.postDelayed(retryRunnable, Constant.PLAY_STUDENT_VOICE_DELAY_TIME);
    }

    private boolean isPlayingMulti() {
        return isPlayingMulti;
    }

    public void setPlayingMulti(boolean playingMulti) {
        isPlayingMulti = playingMulti;
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
        return subtitleMotherTongue;
    }

    public void setSubtitleMotherTongue(String subtitleMotherTongue) {
        this.subtitleMotherTongue = subtitleMotherTongue;
    }

    public String getSubtitleStudyLang() {
        return subtitleStudyLang;
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
}
