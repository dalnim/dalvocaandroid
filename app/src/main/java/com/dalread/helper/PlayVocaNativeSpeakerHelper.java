package com.dalread.helper;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnPlayVocaListener;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class PlayVocaNativeSpeakerHelper {

    private Context context;
    private OnPlayVocaListener onPlayVocaListener;
    private TextToSpeech studyTTS;
    private MediaPlayer mediaPlayer;
    private int readCount;
    private ArrayList<IVocaFullPlayTTSItem> playlistItems;
    private IVocaFullPlayTTSItem playlistItem;
    private int playlistPos;
    private boolean isPlaying;
    private int playVoiceCount;
    private boolean willStopSoon;

    public PlayVocaNativeSpeakerHelper(Context context) {
        this.context = context;

        initPlaylist();
    }

    private void initPlaylist() {
        if (playlistItems == null) {
            playlistItems = new ArrayList<>();
        } else {
            playlistItems.clear();
        }
        playlistItem = null;
        playlistPos = -1;
        initPos();
    }

    private void initPos() {
        playVoiceCount = 0;
    }

    public void initStudyTTS(final Locale locale, final int readCount) {
        studyTTS = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                studyTTS.setLanguage(locale);
                PlayVocaNativeSpeakerHelper.this.readCount = readCount;
                studyTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                    @Override
                    public void onStart(String utteranceId) {
                    }

                    @Override
                    public void onDone(final String utteranceId) {
                        if (!willStopSoon) {
                            playMyVoice();
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                        onDone(utteranceId);
                    }
                });
            } else {
                studyTTS = null;
            }
        });
    }

    public void initPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    public void setOnPlayVocaListener(OnPlayVocaListener onPlayVocaListener) {
        this.onPlayVocaListener = onPlayVocaListener;
    }

    public void clearOnPlayVocaListener() {
        setOnPlayVocaListener(null);
    }

    public boolean hasOnPlayVocaListener() {
        return onPlayVocaListener != null;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void play(ArrayList<IVocaFullPlayTTSItem> playlistItems) {
        if (!playlistItems.isEmpty()) {
            this.playlistItems.addAll(playlistItems);
            playlistPos = 0;
            isPlaying = true;
            playVoca();
        }
    }

    private void playVoca() {
        if (!playlistItems.isEmpty() && playlistPos < playlistItems.size()) {
            playlistItem = playlistItems.get(playlistPos);
            if (playlistItem != null) {
                willStopSoon = false;
                playTTS();
                if (onPlayVocaListener != null) {
                    onPlayVocaListener.onPlay(playlistItem);
                }
            }
        } else {
            stop();
        }
    }

    private void playTTS() {
        playVoiceCount++;
        if (studyTTS == null) {
            playMyVoice();
        } else {
            File file = BaseVoca.getVoiceFileInApp(context, playlistItem.getVIPath());
//            File file = BaseVoca.getVoiceFileOnLocal(context, playlistItem.getVIPath());
            if (file.exists()) {
                playMyVoice(file);
            } else { // play TTS
                String text = BaseVoca.getVocaTTS(playlistItem);
                String utteranceId = String.valueOf(playlistItem.getVIId());
                studyTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                studyTTS.playSilentUtterance(Constant.SILENCE_TIME, TextToSpeech.QUEUE_ADD, utteranceId);
            }
        }
    }

    private void playMyVoice() {
        File file = BaseVoca.getVoiceFileInApp(context, playlistItem.getVIPath());
//        File file = BaseVoca.getVoiceFileOnLocal(context, playlistItem.getVIPath());
        if (file.exists()) {
            playMyVoice(file);
        } else {
            playNextCount();
        }
    }

    private void playMyVoice(File file) {
        playVoice(file, mp -> {
            if (!willStopSoon) {
                mp.reset();
                playNextCount();
            }
        });
    }

    public boolean playMyVoiceOnce(IVocaFullPlayTTSItem voca) {
        File file = BaseVoca.getVoiceFileOnLocal(context, voca.getVIPath());
        if (file.exists()) {
            if (onPlayVocaListener != null) {
                onPlayVocaListener.onPlay(voca);
            }
            playVoice(file, mp -> {
                if (onPlayVocaListener != null) {
                    onPlayVocaListener.onStop(voca);
                }
                if (mp != null) {
                    mp.reset();
                }
            });
            return true;
        }
        return false;
    }

    private void playVoice(File file, MediaPlayer.OnCompletionListener listener) {
        try {
            Uri uri = Uri.fromFile(file);
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(listener);
            mediaPlayer.start();
        } catch (Exception e) {
            listener.onCompletion(mediaPlayer);
        }
    }

    private void playNextCount() {
        if (playVoiceCount < readCount) {
            playTTS();
        } else {
            finishCurrentItem();
        }
    }

    private void finishCurrentItem() {
        if (onPlayVocaListener != null) {
            onPlayVocaListener.onStop(playlistItem);
        }
        mediaPlayer.reset();
        playVoiceCount = 0;
        if (++playlistPos >= playlistItems.size()) {
            playlistPos = 0;
        }
        playVoca();
    }

    public void pause() {
        willStopSoon = true;
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
        if (pos > -1
                && pos < playlistItems.size()
                && pos != playlistPos) {
            if (onPlayVocaListener != null && playlistItem != null) {
                onPlayVocaListener.onStop(playlistItem);
            }
            playlistPos = pos;
        }
        playVoca();
    }

    public void stop() {
        willStopSoon = true;
        if (studyTTS != null) {
            studyTTS.stop();
        }
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        if (onPlayVocaListener != null && playlistItem != null) {
            onPlayVocaListener.onStop(playlistItem);
        }
        initPlaylist();
        isPlaying = false;
    }

    public void destroy() {
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
}
