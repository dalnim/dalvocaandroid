package com.dalread.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.VocaPractice;
import com.dalread.network.DalApiImpl;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.ToastUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

public class SelfPracticeSpeakingPlayVocaHelper extends BasePlayVocaHelper {

    private Context context;
    private SharedPreferencesDB sharedPreferences;
    private DalApiImpl dalApi;
    private OnPlayStatusChangeListener listener;
    private int studyLangId;
    private MediaRecorder mediaRecorder;
    private boolean isReadyToRecord;
    private File recordFile;
    private boolean useRecorder;
    private ArrayList<VocaPractice> vocas;
    private VocaPractice voca; // current playing voca
    private int turnPos;
    private String selfRole;
    private File voiceFolder;
    private File practiceFolder;
    private File beepFile;
    private File endListFile;
    private CheckVoiceFileHelper checkVoiceFileHelper;
    private int nativeSpeakerId;
    private boolean isFirstRound;

    public SelfPracticeSpeakingPlayVocaHelper(Context context, SharedPreferencesDB sharedPreferences, DalApiImpl dalApi, OnPlayStatusChangeListener listener) {
        super(context);

        this.context = context;
        this.sharedPreferences = sharedPreferences;
        this.dalApi = dalApi;
        this.listener = listener;

        initMediaPlayer();
        initTTS();
    }

    private void initTTS() {
        String strStudyLang = sharedPreferences.getStudyLanguage();
        EnumLanguage studyLanguage = EnumLanguage.findByFormatApi(strStudyLang);
        int ttsSpeed = BaseVoca.getTTSSpeed(studyLanguage, sharedPreferences);
        initTTS(studyLanguage.getLocale(), ttsSpeed);
        studyLangId = studyLanguage.getIdApi();
    }

    public void initData() {
        turnPos = -1;
        checkVoiceFileHelper = new CheckVoiceFileHelper(context, voiceFolder);
        nativeSpeakerId = getFirstPreferredNativeSpeakerId();
        isFirstRound = false;
    }

    public void startFirstRound(boolean useRecorder) {
        isFirstRound = true;
        startRound(useRecorder);
    }

    public void startRound(boolean useRecorder) {
        this.useRecorder = useRecorder;
        turnPos = 0;
        startTurn();
    }

    private void startTurn() {
        if (!vocas.isEmpty() && turnPos < vocas.size()) {
            voca = vocas.get(turnPos);
            if (selfRole.equals(voca.getPersonAB())) {
                startYourTurn(voca);
            } else {
                startAppTurn(voca);
            }
        }
    }

    private void startAppTurn(final VocaPractice voca) {
        voca.setVIPlaying(true);
        listener.onAppTurn(voca);
        String fileName = getFirstNativeSpeakerVoiceFileName(voca);
        checkVoiceFileHelper.checkVoiceFile(
                fileName,
                dalApi,
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                new CheckVoiceFileHelper.OnVoiceFileCheckListener() {

                    @Override
                    public void onFinish(String fileName) {
                        if (isPracticing()) {
                            File file = BaseVoca.getVoiceFileOnLocal(voiceFolder, fileName);
                            onFinish(file);
                        }
                    }

                    @Override
                    public void onFinish(File file) {
                        if (isPracticing()) {
                            boolean result = play(file, new MediaPlayer.OnCompletionListener() {

                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    finishTurn(voca);
                                }
                            });
                            if (!result) {
                                String utteranceId = String.valueOf(voca.getId());
//                                play(BaseVoca.getVocaTTS(voca), utteranceId, new UtteranceProgressListener() {
//
//                                    @Override
//                                    public void onStart(String utteranceId) {
//                                    }
//
//                                    @Override
//                                    public void onDone(String utteranceId) {
//                                        finishTurn(voca);
//                                    }
//
//                                    @Override
//                                    public void onError(String utteranceId) {
//                                        finishTurn(voca);
//                                    }
//                                });
                            }
                        }
                    }
                }
        );
    }

    private void startYourTurn(final VocaPractice voca) {
        if (isFirstRound) {
            voca.setVIPlaying(true);
            listener.onYourTurn(voca);
            String fileName = getFirstNativeSpeakerVoiceFileName(voca);
            checkVoiceFileHelper.checkVoiceFile(
                    fileName,
                    dalApi,
                    sharedPreferences.getUid(),
                    sharedPreferences.getLangStudyCode(),
                    new CheckVoiceFileHelper.OnVoiceFileCheckListener() {

                        @Override
                        public void onFinish(String fileName) {
                            if (isPracticing()) {
                                File file = BaseVoca.getVoiceFileOnLocal(voiceFolder, fileName);
                                onFinish(file);
                            }
                        }

                        @Override
                        public void onFinish(File file) {
                            if (isPracticing()) {
                                boolean result = play(file, new MediaPlayer.OnCompletionListener() {

                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        prepareRecord(voca);
                                    }
                                });
                                if (!result) {
                                    String utteranceId = String.valueOf(voca.getId());
//                                    play(BaseVoca.getVocaTTS(voca), utteranceId, new UtteranceProgressListener() {
//
//                                        @Override
//                                        public void onStart(String utteranceId) {
//                                        }
//
//                                        @Override
//                                        public void onDone(String utteranceId) {
//                                            prepareRecord(voca);
//                                        }
//
//                                        @Override
//                                        public void onError(String utteranceId) {
//                                            prepareRecord(voca);
//                                        }
//                                    });
                                }
                            }
                        }
                    });
        } else {
            prepareRecord(voca);
        }
    }

    private void prepareRecord(final VocaPractice voca) {
        if (useRecorder) {
            mediaRecorder = BaseVoca.setupMediaRecorder();
            recordFile = BaseVoca.getVoiceFileOnLocal(practiceFolder, voca.getVIPath());
            if (recordFile != null) {
                mediaRecorder.setOutputFile(recordFile.getPath());
            }
            try {
                mediaRecorder.prepare();
                isReadyToRecord = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isReadyToRecord) {
                boolean result = play(beepFile, new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        startRecord(voca);
                    }
                });
                if (!result) {
                    startRecord(voca);
                }
            } else {
                finishTurn(voca);
            }
        } else {
            playMyPracticeVoice(voca, new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    finishTurn(voca);
                }
            });
        }
    }

    private void startRecord(VocaPractice voca) {
        // start recorder
        try {
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            ToastUtil.getInstance(context).show(R.string.msg_cannot_record_while_calling);
            mediaRecorder.reset();
            isReadyToRecord = false;
            return;
        }

        // update UI
        voca.setVIPlaying(false);
        voca.setVIChecked(true);
        listener.onRecord(voca);
    }

    public void stopRecord() {
        // stop recorder
        if (!stopRecorder())
            return;

        // update UI
        finishTurn(voca);

        // upload
        if (recordFile != null && recordFile.exists()) {
            final String fileName = recordFile.getName();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(
                    BaseVoca.getPracticeSpeakingFolderPathOnFirebase(fileName) + fileName
            );
            Uri uri = Uri.fromFile(recordFile);
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("application/octet-stream")
                    .build();
            UploadTask uploadTask = storageReference.putFile(uri, metadata);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    DLog.i(getLogTag(), "Upload " + fileName + " onFailure");
                }
            }).addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception exception) {
                    DLog.i(getLogTag(), "Upload " + fileName + " onFailure");
                }
            });
        }
    }

    private boolean stopRecorder() {
        try {
            mediaRecorder.stop();
        } catch (Exception e) {
            return false; // we will have an exception if calling stop right after start: https://developer.android.com/reference/android/media/MediaRecorder#stop()
        }
        mediaRecorder.reset();
        isReadyToRecord = false;
        return true;
    }

    private void finishTurn(VocaPractice voca) {
        voca.setVIPlaying(false);
        voca.setVIChecked(false);
        listener.onFinishTurn(voca);
        this.voca = null;
        if (isPracticing()) {
            if (++turnPos < vocas.size()) {
                startTurn();
            } else {
                finishRound();
            }
        }
    }

    private void finishRound() {
        isFirstRound = false;
        turnPos = -1;
        play(endListFile, null);
        listener.onFinishRound();
    }

    public void stopPractice() {
        super.stop();

        if (useRecorder) {
            stopRecorder();
        }

        turnPos = -1;
        if (voca != null) {
            finishTurn(voca);
        }
    }

    public void checkNativeSpeakerVoiceFileAndPlay(final VocaPractice voca) {
        String fileName = getFirstNativeSpeakerVoiceFileName(voca);
        checkVoiceFileHelper.checkVoiceFile(
                fileName,
                dalApi,
                sharedPreferences.getUid(),
                sharedPreferences.getLangStudyCode(),
                new CheckVoiceFileHelper.OnVoiceFileCheckListener() {

                    @Override
                    public void onFinish(String fileName) {
                        File file = BaseVoca.getVoiceFileOnLocal(voiceFolder, fileName);
                        onFinish(file);
                    }

                    @Override
                    public void onFinish(File file) {
                        playNativeSpeakerVoiceAndMyPracticeVoice(voca, file);
                    }
                }
        );
    }

    private void playNativeSpeakerVoiceAndMyPracticeVoice(final VocaPractice voca, final File file) {
        boolean result = play(file, new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                playMyPracticeVoice(voca, new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playNativeSpeakerVoiceAndMyPracticeVoice(voca, file);
                    }
                });
            }
        });
        if (result) {
            notifyLayoutPlay(voca);
        } else {
            String utteranceId = String.valueOf(voca.getId());
//            play(BaseVoca.getVocaTTS(voca), utteranceId, new UtteranceProgressListener() {
//
//                @Override
//                public void onStart(String utteranceId) {
//                    notifyLayoutPlay(voca);
//                }
//
//                @Override
//                public void onDone(String utteranceId) {
//                    playMyPracticeVoice(voca, new MediaPlayer.OnCompletionListener() {
//
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//                            playNativeSpeakerVoiceAndMyPracticeVoice(voca, file);
//                        }
//                    });
//                }
//
//                @Override
//                public void onError(String utteranceId) {
//                    onDone(utteranceId);
//                }
//            });
        }
    }

    public void playMyPracticeVoice(final VocaPractice voca, final MediaPlayer.OnCompletionListener listener) {
        String fileName = voca.getVIPath();
        File file = BaseVoca.getVoiceFileOnLocal(practiceFolder, fileName);
        boolean result = play(file, new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                boolean isPlaying = voca.isVIPlaying();
                if (isPlaying) {
                    if (listener == null) {
                        playMyPracticeVoice(voca, null);
                    } else {
                        listener.onCompletion(mp);
                    }
                }
            }
        });
        if (result) {
            notifyLayoutPlay(voca);
        }
    }

    private void notifyLayoutPlay(VocaPractice voca) {
        this.voca = voca;
        boolean isPlaying = voca.isVIPlaying();
        if (!isPlaying) {
            voca.setVIPlaying(true);
            this.listener.onPlay(voca);
        }
    }

    public void stopVoca() {
        super.stop();

        notifyLayoutStop();
    }

    private void notifyLayoutStop() {
        if (voca != null) {
            voca.setVIPlaying(false);
            listener.onStop(voca);
            voca = null;
        }
    }

    private void pauseTurn() {
        voca.setVIPlaying(false);
        voca.setVIChecked(false);
        listener.onFinishTurn(voca);
    }

    public void pausePractice() {
        super.stop();

        pauseTurn();
    }

    public void resumePractice() {
        startTurn();
    }

    public void resumePractice(VocaPractice voca) {
        this.voca = voca;
        turnPos = vocas.indexOf(voca);
        if (selfRole.equals(voca.getPersonAB())) {
            startYourTurn(voca);
        } else {
            startAppTurn(voca);
        }
    }

    public boolean isPracticing() {
        return turnPos > -1;
    }

    private int getFirstPreferredNativeSpeakerId() {
        String strIds = sharedPreferences.getPreferredNativeSpeakers();
        if (!strIds.isEmpty()) {
            String[] preferredNativeSpeakerIds = strIds.split(",");
            if (preferredNativeSpeakerIds.length > 0) {
                return Integer.parseInt(preferredNativeSpeakerIds[0]);
            }
        }
        return 0;
    }

    private String getFirstNativeSpeakerVoiceFileName(VocaPractice voca) {
        if (nativeSpeakerId == 0)
            return null;
        return BaseVoca.getOutputRecordingFileName(
                studyLangId,
                voca.getVocaType(),
                voca.getVocaId(),
                nativeSpeakerId
        );
    }

    private String getLogTag() {
        return "SelfPracticeSpeakingPlayVocaHelper";
    }

    public void setVocas(ArrayList<VocaPractice> vocas) {
        this.vocas = vocas;
    }

    public void setSelfRole(String selfRole) {
        this.selfRole = selfRole;
    }

    public void setVoiceFolder(File voiceFolder) {
        this.voiceFolder = voiceFolder;

        beepFile = BaseVoca.getVoiceFileOnLocal(voiceFolder, Constant.FILE.BEEP_FILE_NAME);
        endListFile = BaseVoca.getVoiceFileOnLocal(voiceFolder, Constant.FILE.END_LIST_FILE_NAME);
    }

    public void setPracticeFolder(File practiceFolder) {
        this.practiceFolder = practiceFolder;
    }

    public interface OnPlayStatusChangeListener {

        void onAppTurn(VocaPractice voca);

        void onYourTurn(VocaPractice voca);

        void onRecord(VocaPractice voca);

        void onFinishTurn(VocaPractice voca);

        void onFinishRound();

        void onPlay(VocaPractice voca);

        void onStop(VocaPractice voca);
    }
}
