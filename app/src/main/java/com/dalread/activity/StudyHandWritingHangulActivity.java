package com.dalread.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.component.Toolbar;
import com.dalread.composition.PlayTTS;
import com.dalread.databinding.ActivityStudyHandWritingHangulBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.RecordAndUploadDialog;
import com.dalread.model.VocaBook;
import com.dalread.model.VocaDownload;
import com.dalread.model.VocaInBook;
import com.dalread.network.DalApiListener;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.BasePermissionUtils;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.LanguageUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
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

public class StudyHandWritingHangulActivity extends BaseActivity { //BaseDalVocaPlayVocaActivity {
    private Context context;
    protected PlayTTS playTTS;
    private AlertDialog alertDialog;
    private VocaBook vocaBook;
    protected ArrayList<VocaInBook> vocas;
    private int pos;
//    private boolean displayBackgroundHint;
    private RecordAndUploadDialog recordAndUploadDialog;
    private File voiceFolder;

    private ActivityStudyHandWritingHangulBinding binding;
    protected View getContentView() {
        binding = ActivityStudyHandWritingHangulBinding.inflate(getLayoutInflater());
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
        getData(String.valueOf(vocaBook.getId()));
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

    protected void initData() {
        context = this;
        vocaBook = (VocaBook) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_BOOK);
        vocas = new ArrayList<>();
        pos = 0;
//        displayBackgroundHint = true;
        voiceFolder = BaseVoca.getVoiceFolderInApp(context);
        turnEyeOn();
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
        binding.vStudy.tvVoca.setTypeface(Typeface.createFromAsset(getAssets(), Constant.FONT.KANJI_STROKE_ORDERS));
        binding.vStudy.fdvVoca.setPaintColor(Color.RED);
        binding.vStudy.fdvVoca.setPaintWidthDp(8);
        binding.vStudy.fdvVoca.setOnPathDrawnListener(new PathDrawnListener() {
            @Override
            public void onPathStart() {
                turnEyeOffWhenStartWriting();
//                displayBackgroundHint = true;
//                    displayVocas();
//                    displayTTS();
                displayBackgroundText();
            }

            @Override
            public void onNewPathDrawn() {

            }
        });
        binding.vStudy.fdvVoca.setPathRedoUndoCountChangeListener(new PathRedoUndoCountChangeListener() {

            @Override
            public void onUndoCountChanged(int undoCount) {
                if (undoCount == 0) {
                    binding.vStudy.ivUndo.setColorFilter(BaseBindUtils.getDisableColor());
                } else {
                    binding.vStudy.ivUndo.setColorFilter(BaseBindUtils.getEnableColor());

                }
            }

            @Override
            public void onRedoCountChanged(int redoCount) {
            }
        });
        // others
        alertDialog = new AlertDialog(context);
        recordAndUploadDialog = new RecordAndUploadDialog(this, voiceFolder, recordFile -> uploadVoiceFile());
    }

    private void turnEyeOffWhenStartWriting() {
        if (!sharedPreferences.getKeepDisplayingBackgroundHintWhenWriting()) {
            turnEyeOff();
            displayVocas();
            displayTTS();
        }
    }

    protected void getData(String vocaBooksIdListByComma) {
        final int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                application.getDalAiImpl().getVocasFromAllVocaBook(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        sharedPreferences.getMotherTongueLangCode(),
                        vocaBooksIdListByComma,
                        Constant.API_VALUE.VALUE_SERVER_BOOK,
                        0,
                        Constant.LOADING_MAX_ITEM,
                        Constant.MAKE_RUBY_TEXT ? 1 : 0,
                        new DalApiListener<List<VocaInBook>>() {

                            @Override
                            public void onSuccess(List<VocaInBook> response) {

                                int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
                                for (VocaInBook voca : response) {
                                    voca.setPath(BaseVoca.getOutputRecordingFileName(
                                            studyLang,
                                            voca.getVocaType(),
                                            voca.getVocaId(),
                                            uid
                                    ));
                                    vocas.add(voca);
                                }
                                playTTS.checkVersionAndDownload(new ArrayList<>(vocas));
                                updateLayout();
                                //TODO : Add option to play TTS automatically
//                                icPlay.postDelayed(() -> playVoca(true), Constant.ON_RESUME_DELAY);
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

    protected void updateLayout() {
        displayVocas();
        displayTTS();
        displayDescription();
        displayBackgroundText();
        displayIconLeft();
        binding.vStudy.fdvVoca.undoAll();
    }

    private void displayVocas() {
        StringBuilder text = new StringBuilder();
        for (VocaInBook voca : vocas) {
            String vocaDisplay = BaseVoca.getVocaDisplay(voca);
            if (!isDisplayBackgroundHint() && vocas.indexOf(voca) == pos) {
                text.append(BaseVoca.getStarForText(vocaDisplay));
            } else {
                text.append(vocaDisplay);
            }
        }
        if (!TextUtils.isEmpty(text)) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), pos, pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.vStudy.tvSentence.setText(spannableStringBuilder);
        }
    }

    private void displayTTS() {
        if (!vocas.isEmpty() && pos < vocas.size()) {
            VocaInBook voca = vocas.get(pos);
            String vocaText = voca.getVIVoca();
            String extraText = "";
//            String text = voca.getVIVoca() + " : " + voca.getVIVocaTTS();// Voca.getVocaTTS(voca);
            if (!voca.getVIVoca().equals(voca.getVIVocaTTS())) {
                extraText = " : " + voca.getVIVocaTTS();
            }

            if (!TextUtils.isEmpty(voca.getPronounce())) {
                extraText += " (" + voca.getPronounce() + ")";
            }
            if (!isDisplayBackgroundHint()) {
                vocaText = BaseVoca.getStarForText(vocaText);
            }

            String result = vocaText;
            if (!Utils.isEmpty(extraText)) {
                result += extraText;
            }
            binding.tvPronounce.setText(result);
        }
    }

    private void displayDescription() {
        if (!vocas.isEmpty() && pos < vocas.size()) {
            String meaning = vocas.get(pos).getVIMeaning(LanguageUtil.getMotherTongueLanguage(context));
            String meaningDetailed = vocas.get(pos).getVIMeaningDetailed(LanguageUtil.getMotherTongueLanguage(context));
            String result = "";
            if (Utils.isEmpty(meaning)) {
                result = meaningDetailed;
            } else {
                if (Utils.isEmpty(meaningDetailed)) {
                    result = meaning;
                } else {
                    result = meaning + Constant.BREAK_CHARACTER + meaningDetailed;
                 }
            }
            binding.vStudy.tvMeaning.setText(result);
        }
    }

    private void visibleDescription() {
        binding.vStudy.tvMeaning.setVisibility(View.VISIBLE);
    }

    private void invisibleDescription() {
        binding.vStudy.tvMeaning.setVisibility(View.INVISIBLE);
    }

    private void displayBackgroundText() {
        if (isDisplayBackgroundHint()) {
            if (!vocas.isEmpty() && pos < vocas.size()) {
                int bookId = vocaBook.getId();
                if (bookId == Constant.BOOK.KO.HANGUL.CONSONANTS) {
                    binding.vStudy.tvVoca.setBackgroundResource(HANGUL_BOOK_CONSONANTS[pos]);
                } else if (bookId == Constant.BOOK.KO.HANGUL.VOWELS) {
                    binding.vStudy.tvVoca.setBackgroundResource(HANGUL_BOOK_VOWELS[pos]);
                } else {
                    VocaInBook voca = vocas.get(pos);
                    String vocaDisplay = BaseVoca.getVocaDisplay(voca);
                    binding.vStudy.tvVoca.setText(vocaDisplay);
                }
//                turnEyeOn();
            }
        } else {
            binding.vStudy.tvVoca.setText("");
            binding.vStudy.tvVoca.setBackground(null);
//            turnEyeOff();
        }
    }

    private void turnEyeOff() {
        binding.vStudy.ivEye.setImageResource(R.drawable.ic_visibility_off_black_24dp);
        binding.vStudy.ivEye.setTag(false);

        invisibleDescription();
    }

    private void turnEyeOn() {
        binding.vStudy.ivEye.setImageResource(R.drawable.ic_visibility_black_24dp);
        binding.vStudy.ivEye.setTag(true);

        visibleDescription();
    }

    private void switchEyeStatus() {
        if (isEyeOn()) {
            turnEyeOff();
        } else {
            turnEyeOn();
        }
    }
    private boolean isEyeOn() {
        return (boolean) binding.vStudy.ivEye.getTag();
    }
    private void displayIconLeft() {
        if (isFirstWord()) {
            binding.vStudy.ivArrowLeft.setColorFilter(BaseBindUtils.getDisableColor());
        } else {
            binding.vStudy.ivArrowLeft.setColorFilter(BaseBindUtils.getEnableColor());
        }
    }

    private boolean isFirstWord() {
        return pos == 0;
    }

    private boolean isLastWord() {
        return vocas.isEmpty() || pos == vocas.size() - 1;
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
        final VocaInBook voca = vocas.get(pos);
        voca.setVIPlaying(playing);
        binding.ivSpeaker.post(() -> BaseVoca.updateIconSpeaker(binding.ivSpeaker, voca));
    }

    private void playVoca(boolean once) {
        VocaInBook voca = vocas.get(pos);
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

    private void onRecorderClick() {
        if (!vocas.isEmpty() && pos < vocas.size()) {
            VocaInBook voca = vocas.get(pos);
            recordAndUploadDialog.setRecordFile(voca);
            recordAndUploadDialog.show();
        }
    }

    private void uploadVoiceFile() {
        VocaInBook voca = vocas.get(pos);
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
            uploadTask.addOnSuccessListener(taskSnapshot -> application.getDalAiImpl().updateVoiceFileInfo(
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
                                BaseVoca.executeRealmTransaction(realm -> {
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
                                });
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                        }
                    }
            )).addOnFailureListener(exception -> {
            });
        }
    }

    @OnClick({R.id.ivUndo, R.id.ivEye, R.id.ivRefresh, R.id.ivArrowLeft, R.id.ivArrowRight, R.id.ivSpeaker, R.id.ivRecord})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ivUndo:
                binding.vStudy.fdvVoca.undoLast();
                break;
            case R.id.ivEye:
                switchEyeStatus();
//                displayBackgroundHint = !displayBackgroundHint;
                displayVocas();
                displayTTS();
                displayBackgroundText();
                break;
            case R.id.ivRefresh:
                binding.vStudy.fdvVoca.undoAll();
                break;
            case R.id.ivArrowLeft:
//                displayBackgroundHint = true;
                if (!isFirstWord()) {
                    pos--;
                    updateLayout();
                    //TODO : Add option to play TTS automatically
//                    playVoca(true);
                }
                break;
            case R.id.ivArrowRight:
//                displayBackgroundHint = true;
                if (isLastWord()) {
                    pos = 0;
                    ToastUtil.getInstance(context).show( R.string.msg_back_to_beginning);
                } else {
                    pos++;
                }
                updateLayout();
                //TODO : Add option to play TTS automatically
//                playVoca(true);
                break;
            case R.id.ivSpeaker:
                playVoca(false);
                break;
            case R.id.ic_record:
                if (BasePermissionUtils.checkRecordAudio(this, true)) {
                    if (BasePermissionUtils.checkWriteExternalStorage(this, true)) {
                        onRecorderClick();
                    }
                }
                break;
            default:
                break;
        }
    }

    private static final int[] HANGUL_BOOK_CONSONANTS = {
            R.drawable.ic_hangul_1313_giyeok,
            R.drawable.ic_hangul_1314_nieun,
            R.drawable.ic_hangul_1315_digeut,
            R.drawable.ic_hangul_1316_rieul,
            R.drawable.ic_hangul_1317_mieum,
            R.drawable.ic_hangul_1318_bieup,
            R.drawable.ic_hangul_1319_shiot,
            R.drawable.ic_hangul_1320_ieung,
            R.drawable.ic_hangul_1321_jieut,
            R.drawable.ic_hangul_1486_chieut,
            R.drawable.ic_hangul_1322_kieuk,
            R.drawable.ic_hangul_1323_tieut,
            R.drawable.ic_hangul_1324_pieup,
            R.drawable.ic_hangul_1325_hieut,
            R.drawable.ic_hangul_1326_ssanggiyeok,
            R.drawable.ic_hangul_1327_ssangdigeut,
            R.drawable.ic_hangul_1328_ssangbieup,
            R.drawable.ic_hangul_1329_ssangshiot,
            R.drawable.ic_hangul_1485_ssangjieut
    };
    private static final int[] HANGUL_BOOK_VOWELS = {
            R.drawable.ic_hangul_1330_a,
            R.drawable.ic_hangul_1331_ya,
            R.drawable.ic_hangul_1332_eo,
            R.drawable.ic_hangul_1333_yeo,
            R.drawable.ic_hangul_1334_o,
            R.drawable.ic_hangul_1335_yo,
            R.drawable.ic_hangul_1336_u,
            R.drawable.ic_hangul_1337_yu,
            R.drawable.ic_hangul_1338_eu,
            R.drawable.ic_hangul_1339_i,
            R.drawable.ic_hangul_1340_ae,
            R.drawable.ic_hangul_1341_yae,
            R.drawable.ic_hangul_1342_e,
            R.drawable.ic_hangul_1343_ye,
            R.drawable.ic_hangul_1344_wa,
            R.drawable.ic_hangul_1345_wae,
            R.drawable.ic_hangul_1346_oe,
            R.drawable.ic_hangul_1347_wo,
            R.drawable.ic_hangul_1348_we,
            R.drawable.ic_hangul_1349_wi,
            R.drawable.ic_hangul_1350_ui
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DLog.i(getLogTag(), "onStart");
        playTTS.stopPlayVoca();
    }
}
