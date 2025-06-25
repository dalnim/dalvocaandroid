package com.dalread.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.UtteranceProgressListener;
import android.text.Editable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.dalread.R;
import com.dalread.base.BaseActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.component.Toolbar;
import com.dalread.composition.PlayTTS;
import com.dalread.databinding.ActivityPracticeTypingHangulBinding;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import io.realm.Realm;

public class PracticeTypingHangulActivity extends BaseActivity {
    private Context context;
    private PlayTTS playTTS;
    private AlertDialog alertDialog;
    protected List<VocaInBook> vocaList;
    private List<VocaInBook> incorrectVocas;
    private CountDownTimer startPracticeTimer;
    protected CountDownTimer finishPracticeTimer;
    private CountDownTimer correctPracticeTimer;
    private CountDownTimer incorrectPracticeTimer;
    private int pos;
    protected VocaInBook voca;
    private ConfirmationDialog confirmPracticeMoreDialog;
    private MediaRecorder recorder;
    private boolean recorderReady;
    private boolean recording;
    private UploadOrRecordAgainDialog uploadOrRecordAgainDialog;
    private File voiceFolder;
    private boolean isShowIntroDialogAtFirstTime = true;

    private ActivityPracticeTypingHangulBinding binding;
    protected View getContentView() {
        binding = ActivityPracticeTypingHangulBinding.inflate(getLayoutInflater());
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
        Intent intent = new Intent(context, PracticeTypingHangulActivity.class);
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
        incorrectVocas = new ArrayList<>();
        turnEyeOn();
        initStartPracticeTimer();
        correctPracticeTimer = new CountDownTimer(Constant.CORRECT_PRACTICE_HANGUL_COUNT_DOWN_TIME, Constant.PRACTICE_HANGUL_COUNT_DOWN_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                onResultTimerFinish();
            }
        };
        incorrectPracticeTimer = new CountDownTimer(Constant.INCORRECT_PRACTICE_HANGUL_COUNT_DOWN_TIME, Constant.PRACTICE_HANGUL_COUNT_DOWN_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                onResultTimerFinish();
            }
        };
        pos = 0;
        voiceFolder = BaseVoca.getVoiceFolderOnLocal(context);
    }

    protected void initStartPracticeTimer() {
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
        alertDialog = new AlertDialog(context);
        initTypingEditText();
        initConfirmPracticeMoreDiglog();
        initUploadOrRecordAgainDialog();
    }

    private void initUploadOrRecordAgainDialog() {
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
    }

    private void initConfirmPracticeMoreDiglog() {
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
        confirmPracticeMoreDialog.setPositiveText(R.string.yes);
        confirmPracticeMoreDialog.setNegativeText(R.string.no);
    }

    private void initTypingEditText() {
        binding.etInput.setCursorVisible(false);
        binding.etInput.setHorizontallyScrolling(false);
        binding.etInput.setMaxLines(Integer.MAX_VALUE);
        binding.etInput.setTextIsSelectable(false); //TODO : This doesn't work.
        binding.etInput.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS); //In xml's code doesn't work. So I added it here too.
        binding.tvHint.setOnClickListener( v -> {
            Utils.showSoftKeyboard(context, binding.etInput);
        });
    }

    protected void getData() {
        final int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().getVocasToPracticeAlpahbet(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        sharedPreferences.getMotherTongueLangCode(),
                        new DalApiListener<List<VocaPractice>>() {

                            @Override
                            public void onSuccess(List<VocaPractice> response) {
                                Loading.hide();
                                int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
                                if (incorrectVocas.isEmpty()) {
                                    for (VocaInBook voca : response) {
                                        voca.setPath(BaseVoca.getOutputRecordingFileName(
                                                studyLang,
                                                voca.getVocaType(),
                                                voca.getVocaId(),
                                                uid
                                        ));
                                        vocaList.add(voca);
                                    }
                                } else {
                                    vocaList.addAll(incorrectVocas);
                                    int missingCount = Constant.PRACTICE_HANGUL_SENTENCE_MAX - vocaList.size();
                                    if (missingCount > 0) {
                                        for (int i = 0; i < missingCount && i < response.size(); i++) {
                                            VocaPractice voca = response.get(i);
                                            boolean contain = false;
                                            for (VocaInBook incorrectVoca : incorrectVocas) {
                                                if (incorrectVoca.getId() == voca.getId()) {
                                                    contain = true;
                                                    break;
                                                }
                                            }
                                            if (!contain) {
                                                voca.setPath(BaseVoca.getOutputRecordingFileName(
                                                        studyLang,
                                                        voca.getVocaType(),
                                                        voca.getVocaId(),
                                                        uid
                                                ));
                                                vocaList.add(voca);
                                            }
                                        }
                                    }
                                    Collections.shuffle(vocaList);
                                    incorrectVocas.clear();
                                }
                                if (!vocaList.isEmpty()) {
                                    playTTS.checkVersionAndDownload(new ArrayList<IVocaFullPlayTTSItem>(vocaList));
                                    countdownToStartPractice();
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
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
            alertDialog.show(R.string.dialog_message_start_hanul_typing, R.string.yes, (dialog, which) -> {
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
//        binding.etInput.setCursorVisible(true);
        Utils.showSoftKeyboard(context, binding.etInput);

        voca = vocaList.get(pos);
        showInfoMenu();
        showVocaMeaning();
        uploadOrRecordAgainDialog.setVoca(voca);
//        playVoca(true);
        updateVisible();
        String turn = (pos + 1) + "/" + vocaList.size();
        binding.tvPracticeTurn.setText(turn);

        String vocaTTS = BaseVoca.getVocaTTS(voca);
        countdownToFinishPractice(vocaTTS.length());
    }

    private void showInfoMenu() {
        if (voca != null) {
            String info = voca.getVIMeaningDetailed(LanguageUtil.getMotherTongueLanguage(this));
            binding.header.getTvRight().setVisibility(Utils.isEmpty(info) ? View.GONE : View.VISIBLE);
        }
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
//        etInput.setCursorVisible(false);
//        Utils.hideSoftKeyboard(context, etInput);

        forceStopRecording();

        String input = binding.etInput.getText().toString();
        String vocaTTS = BaseVoca.getVocaTTS(voca);
        turnEyeOn();
        boolean correct = input.equals(vocaTTS);
        if (correct) {
            correctPracticeTimer.start();
        } else {
            binding.etInput.setText("");
            binding.tvHint.setText(vocaTTS);
            incorrectVocas.add(voca);
            ToastUtil.getInstance(context).show(R.string.does_not_match);
            incorrectPracticeTimer.start();
        }
    }

    private void onResultTimerFinish() {
        voca = null;
        binding.etInput.setText("");
        binding.tvHint.setText("");
        if (++pos < vocaList.size()) {
            startPractice();
        } else {
            pos = 0;
            binding.tvCountdownFinishPractice.setText("");
            binding.tvPracticeTurn.setText("");
            showScore();
            vocaList.clear();
        }
    }

    private void showScore() {
        int totalCount = vocaList.size();
        int incorrectCount = incorrectVocas.size();
        int correctCount = totalCount - incorrectCount;
        int score = (int) (Constant.TEST_MAX_SCORE * ((float) correctCount / totalCount));// Constant.TEST_MAX_SCORE / vocaList.size() * correctCount;
        String message = getString(R.string.tpl_practice_hangul_score, score);
        confirmPracticeMoreDialog.setMessage(message);
        confirmPracticeMoreDialog.show();
    }

    private void updateVisible() {
        if (isDisplayBackgroundHint()) {
            binding.tvHint.setTextColor(ContextCompat.getColor(this, R.color.color_stop));
            binding.ivEye.setImageResource(R.drawable.ic_visibility_black_24dp);
            if (voca != null) {
                String vocaTTS = BaseVoca.getVocaTTS(voca);
                binding.tvHint.setText(vocaTTS);
            }
        } else {
            binding.ivEye.setImageResource(R.drawable.ic_visibility_off_black_24dp);
            binding.tvHint.setTextColor(ContextCompat.getColor(this, R.color.transparent));

        }
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
        if (voca != null) {
            voca.setVIPlaying(playing);
        }
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
                ToastUtil.getInstance(context).show( R.string.msg_cannot_record_while_calling);
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
        if (startPracticeTimer != null) {
            startPracticeTimer.cancel();
            startPracticeTimer = null;
        }
        if (correctPracticeTimer != null) {
            correctPracticeTimer.cancel();
            correctPracticeTimer = null;
        }
        if (incorrectPracticeTimer != null) {
            incorrectPracticeTimer.cancel();
            incorrectPracticeTimer = null;
        }
        if (finishPracticeTimer != null) {
            finishPracticeTimer.cancel();
            finishPracticeTimer = null;
        }
    }

    @OnTextChanged(value = R.id.etInput, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterInputChanged(Editable editable) {
        String input = editable.toString();
        if (isDisplayBackgroundHint()) {
            if (Utils.isEmpty(input)) {
//                ToastUtil.getInstance(this).show("isDisplayBackgroundHint and input is empty");

                updateVisible();

            } else {
//                ToastUtil.getInstance(this).show("isDisplayBackgroundHint and input is NOT empty");
                turnEyeOffWhenStartWriting();
                updateVisible();
            }
//        } else {
//            ToastUtil.getInstance(this).show("isDisplayBackgroundHint not");
        }

//        if (isDisplayBackgroundHint() && !input.isEmpty()) {
//            updateVisible();
//        } else {
//            turnEyeOffWhenStartWriting();
//            updateVisible();
//        }
        if (voca != null) {
            String vocaTTS = BaseVoca.getVocaTTS(voca);
            boolean correct = input.equals(vocaTTS);
            if (correct) {
                finishPracticeTimer.cancel();
                finishPractice();
            }
        }
    }

    private void turnEyeOffWhenStartWriting() {
        if (!sharedPreferences.getKeepDisplayingBackgroundHintWhenWriting()) {
            turnEyeOff();
            updateVisible();
        }
    }

    @OnEditorAction(R.id.etInput)
    boolean onDonePressed(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            finishPracticeTimer.cancel();
            finishPractice();
            return true;
        }
        return false;
    }

    @OnClick({R.id.ivSpeaker, R.id.ivEye, R.id.ivMic})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ivEye:
                switchEyeStatus();
                updateVisible();
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
}
