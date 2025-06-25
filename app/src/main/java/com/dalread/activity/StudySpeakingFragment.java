package com.dalread.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseVocaStudyFragment;
import com.dalread.base.EnumLanguage;
import com.dalread.model.VocaStudy;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BasePermissionUtils;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Locale;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

public class StudySpeakingFragment extends BaseVocaStudyFragment {

    @BindView(R.id.ic_mic)
    ImageView icMic;
    @BindView(R.id.ic_record)
    ImageView icRecord;
    @BindView(R.id.tv_record)
    TextView tvRecord;
    @BindView(R.id.tv_record_time)
    TextView tvRecordTime;
    @BindView(R.id.tv_record_time_left)
    TextView tvRecordTimeLeft;
    @BindView(R.id.tv_practice)
    TextView tvPractice;
    @BindView(R.id.ic_play_record)
    ImageView icPlayRecord;

    @BindColor(R.color.colorRed)
    int clRecording;
    @BindColor(R.color.colorBlack)
    int clStop;

    @BindString(R.string.tpl_record_time_left)
    String tplRecordTimeLeft;
    @BindString(R.string.uploading)
    String strUploading;

    private CountDownTimer countDownTimer;
    private MediaRecorder recorder;
    private boolean recorderReady;
    private boolean recording;
    protected MediaPlayer mediaPlayer;
    private boolean playerReady;
    private boolean playing;
    private Vibrator vibrator;
    private long[] vibratePattern;
    private VocaStudy voca;
    private String recordFileName;
    private File voiceFolder;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_study_speaking;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initTimer();
        initVibrator();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case BasePermissionUtils.REQUEST_CODE_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (BasePermissionUtils.checkWriteExternalStorage(getActivity(), true)) {
                        onRecorderClick();
                    }
                }
                break;
            case BasePermissionUtils.REQUEST_CODE_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onRecorderClick();
                }
                break;
        }
    }

    @OnClick({R.id.ic_mic, R.id.ic_record, R.id.ic_play_record})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ic_mic:
            case R.id.ic_record:
                if (recording) {
                    handleRecorder();
                } else {
                    if (BasePermissionUtils.checkRecordAudio(getActivity(), true)) {
                        if (BasePermissionUtils.checkWriteExternalStorage(getActivity(), true)) {
                            onRecorderClick();
                        }
                    }
                }
                break;
            case R.id.ic_play_record:
                if (playing) {
                    handlePlayer();
                } else {
                    onPlayClick();
                }
                break;
        }
    }

    public void finishSpeaking(final boolean selfStudy) {
        if (voca != null) {
            final File recordFile = BaseVoca.getVoiceFileOnLocal(voiceFolder, recordFileName);
            if (recordFile != null && recordFile.exists()) {
                Loading.show(getContext(), strUploading);
                Uri uri = Uri.fromFile(recordFile);
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(BaseVoca.getVoiceFolderPathOnFirebase(recordFileName) + uri.getLastPathSegment());
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("application/octet-stream")
                        .build();
                UploadTask uploadTask = storageReference.putFile(uri, metadata);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if (Utils.isDebug()) {
                            ToastUtil.getInstance(getContext()).show("Dev: Upload successfully");
                        }
                        application.getDalAiImpl().updateFinishStudy(
                                String.valueOf(sharedPreferences.getRealUid()),
                                sharedPreferences.getLangStudyCode(),
                                voca.getTutorId(),
                                String.valueOf(voca.getVocaId()),
                                voca.getType(),
                                voca.getId(),
                                Constant.FILE.EXTENTION_SPEAKING,
                                recordFile.length(),
                                null
                        );
                        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DATA_CHANGED, null));
                        Loading.hide();
                    }
                }).addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        if (Utils.isDebug()) {
                            ToastUtil.getInstance(getContext()).show("Dev: Upload failed");
                        }
                        Loading.hide();
                    }
                });
            }
        }
    }

    @Override
    public void onDestroy() {
        destroyTimer();
        destroyRecorder();
        destroyPlayer();

        super.onDestroy();
    }

    private void initData() {
        voca = (VocaStudy) getArguments().getSerializable(Constant.BUNDLE.KEY_VOCA);
        if (voca != null) {
            recordFileName = BaseVoca.getOutputRecordingFileName(
                    EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi(),
                    voca.getType(),
                    voca.getVocaId(),
                    sharedPreferences.getRealUid()
            );
        }
        voiceFolder = BaseVoca.getVoiceFolderOnLocal(getContext());
    }

    private void initTimer() {
        countDownTimer = new CountDownTimer(Constant.RECORD_TIME_MAX, Constant.RECORD_TIME_TICK) {

            @Override
            public void onTick(long millisUntilFinished) {
                float timeLeft = millisUntilFinished / 1000f;
                updateRecordTime(Math.round(timeLeft));
            }

            @Override
            public void onFinish() {
                updateRecordTime(0);
                handleRecorder();
            }
        };
    }

    private void initVibrator() {
        vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        vibratePattern = new long[]{0,
                1000 /*vibrate*/, 1000 /*rest*/,
                1000 /*vibrate*/, 1000 /*rest*/,
                1000 /*vibrate*/, 1000 /*rest*/,
                1000 /*vibrate*/, 1000 /*rest*/,
                1000 /*vibrate*/};
    }

    private void onPlayClick() {
        initPlayer();
        if (playerReady) {
            handlePlayer();
        } else {
            DLog.i(getLogTag(), "Player is not ready");
        }
    }

    protected void initPlayer() {
        if (voca != null) {
            File recordFile = BaseVoca.getVoiceFileOnLocal(voiceFolder, recordFileName);
            if (recordFile != null && recordFile.exists()) {
                Uri uri = Uri.fromFile(recordFile);
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setLooping(true);
                try {
                    mediaPlayer.setDataSource(activity.getApplicationContext(), uri);
                    mediaPlayer.prepare();
                    playerReady = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handlePlayer() {
        playing = !playing;
        updatePlayer();
    }

    private void updatePlayer() {
        if (playing) {
            mediaPlayer.start();

            icPlayRecord.setImageResource(R.drawable.ic_stop_black_24dp);
        } else {
            mediaPlayer.stop();
            mediaPlayer.reset();
            playerReady = false;

            icPlayRecord.setImageResource(R.drawable.ic_play_black_24dp);
        }
    }

    private void onRecorderClick() {
        initRecorder();
        if (recorderReady) {
            handleRecorder();
        } else {
            DLog.i(getLogTag(), "Recorder is not ready");
        }
    }

    private void initRecorder() {
        if (voca != null) {
            recorder = BaseVoca.setupMediaRecorder();
            File recordFile = BaseVoca.getVoiceFileOnLocal(voiceFolder, recordFileName + Constant.FILE.SUFFIX_TEMP);
            if (recordFile != null) {
                recorder.setOutputFile(recordFile.getPath());
            }
            try {
                recorder.prepare();
                recorderReady = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRecorder() {
        recording = !recording;
        updateRecorder();
    }

    private void updateRecorder() {
        if (recording) {
            try {
                recorder.start();
            } catch (IllegalStateException e) {
                ToastUtil.getInstance(activity).show(R.string.msg_cannot_record_while_calling);
                recorder.reset();
                recorderReady = false;
                recording = false;
                return;
            }

            activity.displayStar();

            countDownTimer.start();

            icMic.setImageResource(R.drawable.ic_mic_black_24dp);
            icMic.setColorFilter(clRecording);
            icRecord.setColorFilter(clRecording);
            tvRecord.setText(R.string.recording);
            tvRecordTimeLeft.setVisibility(View.VISIBLE);
            tvPractice.setVisibility(View.GONE);
        } else {
            try {
                recorder.stop();
            } catch (Exception e) {
                return; // we will have an exception if calling stop right after start: https://developer.android.com/reference/android/media/MediaRecorder#stop()
            }
            recorder.reset();
            recorderReady = false;
            File recordFileTemp = BaseVoca.getVoiceFileOnLocal(voiceFolder, recordFileName + Constant.FILE.SUFFIX_TEMP);
            if (recordFileTemp != null && recordFileTemp.exists()) {
                File recordFile = BaseVoca.getVoiceFileOnLocal(voiceFolder, recordFileName);
                recordFileTemp.renameTo(recordFile);
            }

            countDownTimer.cancel();

            icMic.setImageResource(R.drawable.ic_mic_none_black_24dp);
            icMic.setColorFilter(clStop);
            icRecord.setColorFilter(clStop);
            tvRecord.setText(R.string.ready_to_record);
            tvRecordTimeLeft.setVisibility(View.GONE);
            tvPractice.setVisibility(View.VISIBLE);

            activity.showFinishButton();
        }
    }

    private void updateRecordTime(int secondsLeft) {
        String text = String.format(tplRecordTimeLeft, secondsLeft);
        tvRecordTimeLeft.setText(text);

        int secondsRecording = (int) (Constant.RECORD_TIME_MAX / 1000) - secondsLeft;
        int minutesDisplay = secondsRecording / 60;
        int secondsDisplay = secondsRecording % 60;
        text = minutesDisplay + ":" + String.format(Locale.getDefault(), "%02d", secondsDisplay);
        tvRecordTime.setText(text);

        if (secondsLeft == 10 && vibrator != null && vibrator.hasVibrator()) {
//            vibrator.vibrate(vibratePattern, -1);
        }
    }

    private void destroyTimer() {
        countDownTimer.cancel();
        countDownTimer = null;
    }

    private void destroyRecorder() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        File recordFileTemp = BaseVoca.getVoiceFileOnLocal(voiceFolder, recordFileName + Constant.FILE.SUFFIX_TEMP);
        if (recordFileTemp != null && recordFileTemp.exists()) {
            recordFileTemp.delete();
        }
    }

    private void destroyPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
