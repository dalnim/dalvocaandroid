package com.dalread.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.component.Toolbar;
import com.dalread.composition.PlayTTS;
import com.dalread.databinding.ActivityPracticeHandWritingHangulBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.ConfirmationDialog;
import com.dalread.dialog.UploadOrRecordAgainDialog;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.VocaDownload;
import com.dalread.model.VocaInBook;
import com.dalread.model.VocaPractice;
import com.dalread.network.DalApiListener;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.BasePermissionUtils;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.LanguageUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rm.freedrawview.PathDrawnListener;
import com.rm.freedrawview.PathRedoUndoCountChangeListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import io.realm.Realm;

public class PracticeHandWritingHangulActivity extends BaseActivity { //BaseDalVocaPlayVocaActivity {
    private Context context;
    private PlayTTS playTTS;
    private AlertDialog alertDialog;
    protected List<VocaInBook> vocaList;
    private int pos;
//    private boolean visible;
    private MediaRecorder recorder;
    private boolean recorderReady;
    private boolean recording;
    private UploadOrRecordAgainDialog uploadOrRecordAgainDialog;
    private File voiceFolder;
    private ConfirmationDialog confirmPracticeMoreDialog;
    private CountDownTimer startPracticeTimer;
    private CountDownTimer correctPracticeTimer;
    private CountDownTimer finishPracticeTimer;
    private VocaInBook voca;
    private boolean isShowIntroDialogAtFirstTime = true;

    private ActivityPracticeHandWritingHangulBinding binding;
    protected View getContentView() {
        binding = ActivityPracticeHandWritingHangulBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playTTS = new PlayTTS(this);
        initData();
        initLayout();
        getData();
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, PracticeHandWritingHangulActivity.class);
        return intent;
    }

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
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
    public void onResume() {
        super.onResume();

        initPlayVocaHelper();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case BasePermissionUtils.REQUEST_CODE_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (BasePermissionUtils.checkWriteExternalStorage(this, true)) {
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

    @Override
    protected void onDestroy() {
        destroyTimer();

        super.onDestroy();
    }

    protected void initData() {
        context = this;
        vocaList = new ArrayList<>();
        pos = 0;
        turnEyeOn();
//        visible = true;
        voiceFolder = BaseVoca.getVoiceFolderOnLocal(context);
        startPracticeTimer = new CountDownTimer(Constant.START_PRACTICE_HANGUL_COUNT_DOWN_TIME, Constant.PRACTICE_HANGUL_COUNT_DOWN_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
                String text = getString(R.string.tpl_start, millisUntilFinished / 1000);
                binding.tvCountdownStartPractice.setText(text);
            }

            @Override
            public void onFinish() {
                binding.tvCountdownStartPractice.setVisibility(View.GONE);
                startPractice();
            }
        };
        correctPracticeTimer = new CountDownTimer(Constant.INCORRECT_PRACTICE_HANGUL_COUNT_DOWN_TIME, Constant.PRACTICE_HANGUL_COUNT_DOWN_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                onResultTimerFinish();
            }
        };
    }

    private boolean isDisplayBackgroundHint() {
        boolean result = true;
        boolean isKeepDisplayingBackgroundHintWhenWriting = sharedPreferences.getKeepDisplayingBackgroundHintWhenWriting();
        boolean isEyeOn = isEyeOn();
        if (isKeepDisplayingBackgroundHintWhenWriting) {
            if (isEyeOn) {
                result = true;
            } else {
                result = false;
            }
        } else {
            if (isEyeOn) {
                result = true;
            } else {
                result = false;
            }
        }
        return result;
    }

    private void initLayout() {
        // voca
        binding.tvHint.setTypeface(Typeface.createFromAsset(getAssets(), Constant.FONT.KANJI_STROKE_ORDERS));
        binding.fdvVoca.setPaintColor(Color.RED);
        binding.fdvVoca.setPaintWidthDp(8);
        binding.fdvVoca.setOnPathDrawnListener(new PathDrawnListener() {
            @Override
            public void onPathStart() {
//                visible = false;
                turnEyeOffWhenStartWriting();
                displayBackgroundText();
            }

            @Override
            public void onNewPathDrawn() {

            }
        });
        binding.fdvVoca.setPathRedoUndoCountChangeListener(new PathRedoUndoCountChangeListener() {

            @Override
            public void onUndoCountChanged(int undoCount) {
                if (undoCount == 0) {
                    binding.ivUndo.setColorFilter(BaseBindUtils.getDisableColor());
                } else {
                    binding.ivUndo.setColorFilter(BaseBindUtils.getEnableColor());

                }
            }

            @Override
            public void onRedoCountChanged(int redoCount) {
            }
        });
        // others
        alertDialog = new AlertDialog(context);
        uploadOrRecordAgainDialog = new UploadOrRecordAgainDialog(context, new UploadOrRecordAgainDialog.OnClickListener() {

            @Override
            public void onRecordClick() {
                onRecorderClick();
            }

            @Override
            public void onUploadClick() {
                uploadVoiceFile();
            }
        });
        confirmPracticeMoreDialog = new ConfirmationDialog(context, new ConfirmationDialog.OnDialogClickListener() {

            @Override
            public void onPositive(DialogInterface dialog) {
                dialog.dismiss();
                getData();
            }

            @Override
            public void onNegative(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        confirmPracticeMoreDialog.setMyTitle("");
        confirmPracticeMoreDialog.setMessage(R.string.tpl_practice_hangul_more);
        confirmPracticeMoreDialog.setPositiveText(R.string.yes);
        confirmPracticeMoreDialog.setNegativeText(R.string.no);
        binding.btnNext.setEnabled(false);
    }

    private void turnEyeOffWhenStartWriting() {
        if (!sharedPreferences.getKeepDisplayingBackgroundHintWhenWriting()) {
            turnEyeOff();
        }
    }

    protected void getData() {
        final int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                application.getDalAiImpl().getVocasToPracticeAlpahbet(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        sharedPreferences.getMotherTongueLangCode(),
                        new DalApiListener<List<VocaPractice>>() {

                            @Override
                            public void onSuccess(List<VocaPractice> response) {
                                int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
                                for (VocaInBook voca : response) {
                                    voca.setPath(BaseVoca.getOutputRecordingFileName(
                                            studyLang,
                                            voca.getVocaType(),
                                            voca.getVocaId(),
                                            uid
                                    ));
                                    vocaList.add(voca);
                                }
                                if (!vocaList.isEmpty()) {
                                    playTTS.checkVersionAndDownload(new ArrayList<IVocaFullPlayTTSItem>(vocaList));
                                    countdownToStartPractice();
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    protected void countdownToStartPractice() {
        if (isShowIntroDialogAtFirstTime) {
            isShowIntroDialogAtFirstTime = false;
            AlertDialog alertDialog = new AlertDialog(context);
            alertDialog.show(R.string.dialog_message_start_hanul_writing, R.string.yes, (dialog, which) -> {
                dialog.dismiss();
                startPractice();
            });
        } else {
            startPractice();
        }

        //Don't delete this. It shows "START" for 3 seconds and start practicing.
//        tvCountdownStartPractice.setVisibility(View.VISIBLE);
//        startPracticeTimer.start();
    }

    private void startPractice() {
        binding.btnNext.setEnabled(true);
        voca = vocaList.get(pos);
        showVocaMeaning();
        uploadOrRecordAgainDialog.setVoca(voca);
//        playVoca(true);
        updateLayout();
        String vocaTTS = BaseVoca.getVocaTTS(voca);
        binding.tvHint.setText(isDisplayBackgroundHint() ? vocaTTS : "");
        String turn = (pos + 1) + "/" + vocaList.size();
        binding.tvPracticeTurn.setText(turn);

        countdownToFinishPractice(vocaTTS.length());
    }

    private void showVocaMeaning() {
        binding.tvMeaning.setText("");
        if (voca != null) {
            String info = voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(this));
            binding.tvMeaning.setText(Utils.isEmpty(info) ? "" : info);
        }
    }

    private void countdownToFinishPractice(int length) {
        long time = (long) (Constant.PRACTICE_HANGUL_BASE_TIME * length * 1.5);
        finishPracticeTimer = new CountDownTimer(time, Constant.PRACTICE_HANGUL_COUNT_DOWN_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
                String text = String.valueOf(millisUntilFinished / 1000);
                binding.tvCountdownFinishPractice.setText(text);
            }

            @Override
            public void onFinish() {
                binding.tvCountdownFinishPractice.setText("0");
                finishPractice();
            }
        };
        finishPracticeTimer.start();
    }

    private void finishPractice() {
        binding.btnNext.setEnabled(false);

        playTTS.playTTSHelper.stop();
        forceStopRecording();

        turnEyeOn();
        displayBackgroundText();
        correctPracticeTimer.start();

    }

    private void onResultTimerFinish() {
        voca = null;
        binding.tvHint.setText("");
        if (++pos < vocaList.size()) {
            startPractice();
        } else {
            pos = 0;
            vocaList.clear();
            binding.tvCountdownFinishPractice.setText("");
            binding.tvPracticeTurn.setText("");
            confirmPracticeMoreDialog.show();
        }
    }

    private void updateLayout() {
        displayBackgroundText();
        binding.fdvVoca.undoAll();
    }

    private void displayBackgroundText() {
        if (isDisplayBackgroundHint()) {
            if (voca != null) {
                String vocaDisplay = BaseVoca.getVocaDisplay(voca);
                binding.tvHint.setText(vocaDisplay);
                binding.ivEye.setImageResource(R.drawable.ic_visibility_black_24dp);
            }
        } else {
            binding.tvHint.setText("");
            binding.ivEye.setImageResource(R.drawable.ic_visibility_off_black_24dp);
        }
    }

    private void turnEyeOff() {
        binding.ivEye.setImageResource(R.drawable.ic_visibility_off_black_24dp);
        binding.ivEye.setTag(false);
    }

    private void turnEyeOn() {
        binding.ivEye.setImageResource(R.drawable.ic_visibility_black_24dp);
        binding.ivEye.setTag(true);
    }

    private void switchEyeStatus() {
        if (isEyeOn()) {
            turnEyeOff();
        } else {
            turnEyeOn();
        }
    }
    private boolean isEyeOn() {
        return (boolean) binding.ivEye.getTag();
    }

    private void initPlayVocaHelper() {
        if (!playTTS.playTTSHelper.hasMotherTongueListener()) {
            playTTS.playTTSHelper.setMotherTongueListener(new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {
                    updateItemStatus(true);
                }

                @Override
                public void onDone(String utteranceId) {
                }

                @Override
                public void onError(String utteranceId) {
                }
            });
        }
        if (!playTTS.playTTSHelper.hasStudyListener()) {
            playTTS.playTTSHelper.setStudyListener(new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {
                    updateItemStatus(false);
                }

                @Override
                public void onError(String utteranceId) {
                    updateItemStatus(false);
                }
            });
        }
    }

    private void updateItemStatus(boolean playing) {
        final VocaInBook voca = vocaList.get(pos);
        voca.setVIPlaying(playing);
        binding.ivSpeaker.post(new Runnable() {

            @Override
            public void run() {
                BaseVoca.updateIconSpeaker(binding.ivSpeaker, voca);
            }
        });
    }

    private void playVoca(boolean once) {
        if (!recording && voca != null) {
            boolean isPlaying = voca.isVIPlaying();
            playTTS.playTTSHelper.stop();
            if (!isPlaying) {
                if (once) {
                    playTTS.preparePlay1stNativeSpeakerAndMyVoiceOnce(voca);
                } else {
                    playTTS.preparePlayVocaNoDownload(voca);
                }
            }
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
            File recordFile = BaseVoca.getVoiceFileOnLocal(voiceFolder, voca.getVIPath());
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
                ToastUtil.getInstance(context).show(R.string.msg_cannot_record_while_calling);
                recorder.reset();
                recorderReady = false;
                recording = false;
                return;
            }
            binding.tvToast.setVisibility(View.VISIBLE);
            binding.ivMic.setImageResource(R.drawable.ic_mic_black_24dp);
            binding.ivMic.setColorFilter(BaseBindUtils.getMicPlayColor());
        } else {
            try {
                recorder.stop();
            } catch (Exception e) {
                return; // we will have an exception if calling stop right after start: https://developer.android.com/reference/android/media/MediaRecorder#stop()
            }
            recorder.reset();
            recorderReady = false;
            binding.tvToast.setVisibility(View.GONE);
            binding.ivMic.setImageResource(R.drawable.ic_mic_none_black_24dp);
            binding.ivMic.setColorFilter(BaseBindUtils.getMicStopColor());
            uploadOrRecordAgainDialog.show();
        }
    }

    private void forceStopRecording() {
        if (recording) {
            recording = false;
            try {
                recorder.stop();
            } catch (Exception e) {
                return; // we will have an exception if calling stop right after start: https://developer.android.com/reference/android/media/MediaRecorder#stop()
            }
            recorder.reset();
            recorderReady = false;
            binding.tvToast.setVisibility(View.GONE);
            binding.ivMic.setImageResource(R.drawable.ic_mic_none_black_24dp);
            binding.ivMic.setColorFilter(BaseBindUtils.getMicStopColor());
        }
    }

    private void uploadVoiceFile() {
        VocaInBook voca = vocaList.get(pos);
        final String vocaId = String.valueOf(voca.getVocaId());
        final int vocaType = voca.getVocaType();
        final String path = voca.getVIPath();
        final File recordFile = BaseVoca.getVoiceFileOnLocal(voiceFolder, path);
        if (recordFile != null && recordFile.exists()) {
            Uri uri = Uri.fromFile(recordFile);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(BaseVoca.getVoiceFolderPathOnFirebase(path) + uri.getLastPathSegment());
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("application/octet-stream")
                    .build();
            UploadTask uploadTask = storageReference.putFile(uri, metadata);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    application.getDalAiImpl().updateVoiceFileInfo(
                            String.valueOf(sharedPreferences.getRealUid()),
                            sharedPreferences.getLangStudyCode(),
                            vocaId,
                            vocaType,
                            Constant.FILE.EXTENTION_SPEAKING,
                            recordFile.length(),
                            new DalApiListener<Boolean>() {

                                @Override
                                public void onSuccess(Boolean response) {
                                    if (response) {
                                        BaseVoca.executeRealmTransaction(new Realm.Transaction() {

                                            @Override
                                            public void execute(Realm realm) {
                                                VocaDownload vocaDownload = realm.where(VocaDownload.class)
                                                        .equalTo("name", path)
                                                        .findFirst();
                                                if (vocaDownload == null) {
                                                    vocaDownload = new VocaDownload();
                                                    vocaDownload.setName(path);
                                                    vocaDownload.setVersion(0);
                                                    realm.copyToRealm(vocaDownload);
                                                } else {
                                                    int version = vocaDownload.getVersion() + 1;
                                                    vocaDownload.setVersion(version);
                                                }
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(String error) {
                                }
                            }
                    );
                }
            }).addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        }
    }

    private void destroyTimer() {
        startPracticeTimer.cancel();
        correctPracticeTimer.cancel();
        if (finishPracticeTimer != null) {
            finishPracticeTimer.cancel();
        }
    }

    @OnClick({R.id.ivUndo, R.id.ivEye, R.id.icRefresh, R.id.ivSpeaker, R.id.ivMic, R.id.btnNext})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ivUndo:
                binding.fdvVoca.undoLast();
                break;
            case R.id.ivEye:
                switchEyeStatus();
                displayBackgroundText();
                break;
            case R.id.icRefresh:
                binding.fdvVoca.undoAll();
                break;
            case R.id.ivSpeaker:
                playVoca(false);
                break;
            case R.id.ivMic:
                if (recording) {
                    handleRecorder();
                } else {
                    if (BasePermissionUtils.checkRecordAudio(this, true)) {
                        if (BasePermissionUtils.checkWriteExternalStorage(this, true)) {
                            onRecorderClick();
                        }
                    }
                }
                break;
            case R.id.btnNext:
                finishPracticeTimer.cancel();
                finishPractice();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (!recording) {
            super.onBackPressed();
        }
    }
}
