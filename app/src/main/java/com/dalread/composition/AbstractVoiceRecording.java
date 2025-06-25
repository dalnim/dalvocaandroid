package com.dalread.composition;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.widget.ImageView;

import com.dalread.R;
import com.dalread.util.BasePermissionUtils;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.BaseVoca;
import com.dalread.util.DLog;
import com.dalread.util.ToastUtil;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public abstract class AbstractVoiceRecording {
    private Activity activity;
    public MediaPlayer mediaPlayer;
    public MediaRecorder mediaRecorder;
    public boolean recorderReady;
    public boolean recording;
    public File recordFile;
    protected ImageView ivMic;
    protected ImageView ivSpeaker;

    public AbstractVoiceRecording(Activity activity) {
        this.activity = activity;
        initMediaPlayer();
    }

    public AbstractVoiceRecording(Activity activity, ImageView ivMic, ImageView ivSpeaker) {
        this.activity = activity;
        this.ivMic = ivMic;
        this.ivSpeaker = ivSpeaker;
        initMediaPlayer();
    }

    public ImageView getIvMic() {
        return ivMic;
    }

    public void setIvMic(ImageView ivMic) {
        this.ivMic = ivMic;
    }

    public ImageView getIvSpeaker() {
        return ivSpeaker;
    }

    public void setIvSpeaker(ImageView ivSpeaker) {
        this.ivSpeaker = ivSpeaker;
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void initMediaRecorder() {
        if (BasePermissionUtils.checkRecordAudio(activity, true)) {
            // At this moment this app saves the recorded file in the app folder so don't need write permission.
            // But if I change the Save Folder out of the app folder, then I need to ask Write Permission.
//            if (PermissionUtils.checkWriteExternalStorage(activity, true)) {
                mediaRecorder = setupMediaRecorder();
                if (recordFile != null) {
                    BaseStorageUtil.createFolder(FilenameUtils.getPath(recordFile.getPath()));
                    mediaRecorder.setOutputFile(recordFile.getPath());
                }
                try {
                    mediaRecorder.prepare();
                    recorderReady = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    recorderReady = false;
                }
//            }
        }
    }

    //Has duplicated method in Voca (Need to delete it later)
    private MediaRecorder setupMediaRecorder() {
        MediaRecorder recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC); //Don't use HE_AAC, it records metal grinding noise too.
        recorder.setAudioEncodingBitRate(48000); //Same as iOS
        recorder.setAudioSamplingRate(16000); //Same as iOS
        return recorder;
    }

    public void playFile(Context context, boolean isRepeat) {
        if (recordFile != null && recordFile.exists()) {
            try {
                Uri uri = Uri.fromFile(recordFile);
                mediaPlayer.setDataSource(context, uri);
                mediaPlayer.setLooping(isRepeat);
                mediaPlayer.prepare();
                mediaPlayer.start();
                BaseVoca.updateIconSpeaker(ivSpeaker, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateRecorder() {
        recording = !recording;
        if (recording) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        try {
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            ToastUtil.getInstance(activity).show(R.string.msg_cannot_record_while_calling);
            mediaRecorder.reset();
            recorderReady = false;
            recording = false;
            return;
        }
        BaseVoca.updateIconMic(ivMic, true);
//            tvRecording.setVisibility(View.VISIBLE);
    }
    public void stopRecording() {
        try {
            mediaRecorder.stop();
        } catch (Exception e) {
            e.printStackTrace();
            return; // we will have an exception if calling stop right after start: https://developer.android.com/reference/android/media/MediaRecorder#stop()
        }
        mediaRecorder.reset();
        recorderReady = false;
        BaseVoca.updateIconMic(ivMic, false);
    }
    public void onClickMic() {
        if (recording) {
            updateRecorder();
//            ivMic.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    updateRecorder();
//                }
//            }, Constant.STOP_CHAT_VOICE_DELAY_TIME);
        } else {
            if (mediaPlayer.isPlaying()) {
                stopPlaying();
            }
            initMediaRecorder();
            if (recorderReady) {
                updateRecorder();
            } else {
                recording = false;
                DLog.i(getClass().getSimpleName(), "Recorder is not ready");
            }
        }
    }

    public void onClickSpeaker(boolean isRepeat) {
        if (mediaPlayer.isPlaying()) {
            stopPlaying();
        } else {
            if (recording) {
                updateRecorder();
            }
            playFile(activity, isRepeat);
//                    Voca.updateIconMic(ivMic, true);
        }
    }

    public void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        BaseVoca.updateIconSpeaker(ivSpeaker, false);
        BaseVoca.updateIconMic(ivMic, false);
    }

    public void destroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    public void dismiss() {
        if (mediaPlayer.isPlaying()) {
            stopPlaying();
        }
        if (recording) {
            updateRecorder();
        }
    }
}
