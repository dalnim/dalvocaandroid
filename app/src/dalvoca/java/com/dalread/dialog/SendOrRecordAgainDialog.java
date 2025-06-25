package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.ToastUtil;
import com.dalread.util.Voca;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class SendOrRecordAgainDialog extends BottomSheetDialog implements DialogInterface.OnDismissListener {

    @BindView(R.id.ic_play)
    ImageView icPlay;
    @BindView(R.id.ic_record)
    ImageView icRecord;
    @BindView(R.id.tv_recording)
    TextView tvRecording;
    @BindView(R.id.btn_send)
    Button btnSend;

    @BindColor(R.color.color_play)
    int clPlay;
    @BindColor(R.color.color_black_speaker_icon)
    int clStop;
    @BindColor(R.color.colorBlue)
    int clEnable;
    @BindColor(R.color.colorButtonOkDisable)
    int clDisable;

    private final Context context;
    private final File chatFolder;
    private final int chatRoomId;
    private final int senderId;
    private final OnSendClickListener listener;
    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private boolean recorderReady;
    private boolean recording;
    private File recordFile;
    private String downloadURL;

    public SendOrRecordAgainDialog(@NonNull Context context, File chatFolder, int chatRoomId, int senderId, OnSendClickListener listener) {
        super(context);

        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_send_or_record_again);
        ButterKnife.bind(this);

        this.context = context;
        this.chatFolder = chatFolder;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.listener = listener;

        initMediaPlayer();

        setOnDismissListener(this);
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void show() {
        super.show();

        disableBtnSend();

        long now = System.currentTimeMillis();
        String hashCode = Voca.md5(String.valueOf(now));
        downloadURL = Voca.getChatRecordingDowloadURL(chatRoomId, senderId, hashCode);
        recordFile = Voca.getChatFileOnLocal(chatFolder, downloadURL);
    }

    @OnClick({R.id.ic_close, R.id.ic_play, R.id.ic_record, R.id.btn_send})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ic_play:
                if (mediaPlayer.isPlaying()) {
                    stopVoca();
                } else {
                    if (recording) {
                        updateRecorder();
                    }
                    playVoca();
                }
                break;
            case R.id.ic_record:
                if (recording) {
                    icRecord.postDelayed(this::updateRecorder, Constant.STOP_CHAT_VOICE_DELAY_TIME);
                } else {
                    if (mediaPlayer.isPlaying()) {
                        stopVoca();
                    }
                    initMediaRecorder();
                    if (recorderReady) {
                        updateRecorder();
                    } else {
                        DLog.i(getClass().getSimpleName(), "Recorder is not ready");
                    }
                }
                break;
            case R.id.btn_send:
                dismiss();
                if (listener != null) {
                    if (recording) {
                        updateRecorder();
                    }
                    listener.onClick(downloadURL, recordFile);
                }
                break;
            default:
                dismiss();
                break;
        }
    }

    private void playVoca() {
        if (recordFile != null && recordFile.exists()) {
            try {
                Uri uri = Uri.fromFile(recordFile);
                mediaPlayer.setDataSource(context, uri);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
                icPlay.setColorFilter(clPlay);
                disableBtnSend();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void stopVoca() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        icPlay.setColorFilter(clStop);
        enableBtnSend();
    }

    private void initMediaRecorder() {
        mediaRecorder = Voca.setupMediaRecorder();
        if (recordFile != null) {
            mediaRecorder.setOutputFile(recordFile.getPath());
        }
        try {
            mediaRecorder.prepare();
            recorderReady = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRecorder() {
        recording = !recording;
        if (recording) {
            try {
                mediaRecorder.start();
            } catch (IllegalStateException e) {
                ToastUtil.getInstance(context).show( R.string.msg_cannot_record_while_calling);
                mediaRecorder.reset();
                recorderReady = false;
                recording = false;
                return;
            }
            icRecord.setImageResource(R.drawable.ic_mic_black_24dp);
            icRecord.setColorFilter(clPlay);
            tvRecording.setVisibility(View.VISIBLE);
            disableBtnSend();
        } else {
            try {
                mediaRecorder.stop();
            } catch (Exception e) {
                return; // we will have an exception if calling stop right after start: https://developer.android.com/reference/android/media/MediaRecorder#stop()
            }
            mediaRecorder.reset();
            recorderReady = false;
            icRecord.setImageResource(R.drawable.ic_mic_none_black_24dp);
            icRecord.setColorFilter(clStop);
            tvRecording.setVisibility(View.INVISIBLE);
            enableBtnSend();
        }
    }

    private void enableBtnSend() {
        btnSend.setEnabled(true);
        btnSend.setBackgroundColor(clEnable);
    }

    private void disableBtnSend() {
        btnSend.setEnabled(false);
        btnSend.setBackgroundColor(clDisable);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mediaPlayer.isPlaying()) {
            stopVoca();
        }
        if (recording) {
            updateRecorder();
        }
    }

    // destroy components in this dialog manually
    public void onDestroy() {
        mediaPlayer.release();
        mediaPlayer = null;
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    public interface OnSendClickListener {

        void onClick(String downloadURL, File recordFile);
    }
}
