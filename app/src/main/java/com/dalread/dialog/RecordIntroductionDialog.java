package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.ToastUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class RecordIntroductionDialog extends BottomSheetDialog implements DialogInterface.OnDismissListener {

    @BindView(R.id.ic_play)
    ImageView icPlay;
    @BindView(R.id.ic_record)
    ImageView icRecord;

    @BindColor(R.color.color_play)
    int clPlay;
    @BindColor(R.color.color_black_speaker_icon)
    int clStop;

    private final Context context;
    private final File introductionFile;
    private final String logTag;
    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private boolean recorderReady;
    private boolean recording;

    public RecordIntroductionDialog(@NonNull Context context, File introductionFile) {
        super(context);

        setContentView(R.layout.dialog_upload_or_record_again);
        ButterKnife.bind(this);

        setCancelable(true);
        setCanceledOnTouchOutside(true);

        this.context = context;
        this.introductionFile = introductionFile;
        logTag = getClass().getSimpleName();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        setOnDismissListener(this);
    }

    public void setHasIntroductionFile(boolean hasIntroductionFile) {
        icPlay.setImageResource(hasIntroductionFile || (introductionFile != null && introductionFile.exists())
                ? R.drawable.ic_volume_up_48dp
                : R.drawable.ic_volume_up_outline_48dp);
    }

    @OnClick({R.id.ic_close, R.id.ic_play, R.id.ic_record, R.id.btn_upload})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ic_play:
                if (mediaPlayer.isPlaying()) {
                    stopIntroduction();
                } else {
                    if (recording) {
                        updateRecorder();
                    }
                    playIntroduction();
                }
                break;
            case R.id.ic_record:
                if (recording) {
                    icRecord.removeCallbacks(stopRecordRunnable);
                    icRecord.postDelayed(stopRecordRunnable, Constant.STOP_CHAT_VOICE_DELAY_TIME);
                } else {
                    if (mediaPlayer.isPlaying()) {
                        stopIntroduction();
                    }
                    initMediaRecorder();
                    if (recorderReady) {
                        updateRecorder();
                    } else {
                        DLog.i(logTag, "Recorder is not ready");
                    }
                }
                break;
            case R.id.btn_upload:
                uploadIntroduction();
                dismiss();
                break;
            default:
                dismiss();
                break;
        }
    }

    private void playIntroduction() {
        try {
            Uri uri = Uri.fromFile(introductionFile);
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
            icPlay.setColorFilter(clPlay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopIntroduction() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        icPlay.setColorFilter(clStop);
    }

    private void initMediaRecorder() {
        mediaRecorder = BaseVoca.setupMediaRecorder();
        if (introductionFile != null) {
            mediaRecorder.setOutputFile(introductionFile.getPath());
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
                ToastUtil.getInstance(context).show(R.string.msg_cannot_record_while_calling);
                mediaRecorder.reset();
                recorderReady = false;
                recording = false;
                return;
            }
            icRecord.setImageResource(R.drawable.ic_mic_black_24dp);
            icRecord.setColorFilter(clPlay);
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
            icPlay.setImageResource(R.drawable.ic_volume_up_48dp);
        }
    }

    private final Runnable stopRecordRunnable = this::updateRecorder;

    private void uploadIntroduction() {
        if (introductionFile != null && introductionFile.exists()) {
            Uri uri = Uri.fromFile(introductionFile);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(Constant.FILE.FOLDER_INTRODUCTION + "/" + uri.getLastPathSegment());
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("application/octet-stream")
                    .build();
            UploadTask uploadTask = storageReference.putFile(uri, metadata);
            uploadTask.addOnSuccessListener(taskSnapshot -> DLog.i(logTag, "Upload introduction onSuccess"))
                    .addOnFailureListener(exception -> DLog.i(logTag, "Upload introduction onFailure"));
        } else {
            DLog.i(logTag, "No introduction file to upload");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mediaPlayer.isPlaying()) {
            stopIntroduction();
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
}
