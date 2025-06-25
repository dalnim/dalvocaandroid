package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dalread.R;
import com.dalread.base.BaseActivityTextLeft;
import com.dalread.base.BaseInit;
import com.dalread.base.EnumLanguage;
import com.dalread.base.ITTSListener;
import com.dalread.model.WordModel;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.ErrorEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.LanguageUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by JetVHS on 3/5/2017.
 */
public class WordActivity extends BaseActivityTextLeft implements BaseInit {

    private static final String KEY_DATA = "key_data";
    private static final String KEY_FROM_DALREAD = "key_from_dalread";

    @BindView(R.id.ivStatus)
    ImageView ivStatus;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvSpeak)
    TextView tvSpeak;
    @BindView(R.id.ivFavorite)
    ImageView ivFavorite;
    @BindView(R.id.ivSpeak)
    ImageView ivSpeck;
    @BindView(R.id.ivDicSystem)
    ImageView ivDicSystem;
    @BindView(R.id.tvMean)
    TextView tvMean;
    @BindView(R.id.tvHanaj)
    TextView tvHanaj;
    private WordModel wordModel;
    private boolean isFromDalRead = false;

    public static Intent createIntent(Context context, final WordModel wordModel, final boolean isFromDalRead) {
        Intent intent = new Intent(context, WordActivity.class);
        intent.putExtra(KEY_DATA, wordModel);
        intent.putExtra(KEY_FROM_DALREAD, isFromDalRead);
        return intent;
    }

    @Override
    protected void onClickIcLeft(View v) {
        onBackPressed();
    }

    @Override
    protected void onClickIcRight(View v) {
        startActivity(WordChangeActivity.createIntent(this, wordModel));
    }

    @Override
    protected void onPermissionSuccess(int request_code) {

    }

    @Override
    protected void onPermissionFail(int request_code) {

    }

    @Override
    protected void onChangeLanguage() {

    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_word);
        ButterKnife.bind(this);
//        checkTTSIntent();
        initAudioManager();
        initData();
    }

    @Override
    public void initView(View v) {

    }

    @Override
    public void initData() {
        showTitle(R.string.word_definition_title);
        showTwoImage(R.drawable.ic_back, R.string.word_definition_edit);
        if (getIntent() != null) {
            wordModel = getIntent().getParcelableExtra(KEY_DATA);
            isFromDalRead = getIntent().getBooleanExtra(KEY_FROM_DALREAD, false);
        }
        if (wordModel == null) {
            ToastUtil.getInstance(this).show(R.string.error_msg_open_failed_wordlist);
            finish();
            return;
        }
        DLog.d(TAG, wordModel.toString());
        initTTS();
        setTTSListener(new ITTSListener() {
            @Override
            public void onTTSStart(String utteranceId) {
                checkSpeck();
            }

            @Override
            public void onTTSDone(String utteranceId) {
                checkSpeck();
            }

            @Override
            public void onTTSError(String utteranceId) {
                checkSpeck();
            }

            @Override
            public void onComplete(int size) {
                if (size <= 0) {
                    checkAndSpeckText();
                }
            }
        });
        setTtsListener2(new ITTSListener() {
            @Override
            public void onTTSStart(String utteranceId) {
                checkSpeck();
            }

            @Override
            public void onTTSDone(String utteranceId) {
                checkSpeck();
            }

            @Override
            public void onTTSError(String utteranceId) {
                checkSpeck();
            }

            @Override
            public void onComplete(int size) {
                if (size <= 0) {
                    checkAndSpeckText();
                }
            }
        });
        tvName.setText(wordModel.getWord());
        if (!Utils.isEmpty(wordModel.getPronounce())) {
            tvSpeak.setText("\\" + wordModel.getPronounce() + "\\");
        }
        updateTextViewMean();
        updateButtonFavorite(wordModel.isBookmark());
        ivStatus.setImageResource(wordModel.getKnow().equalsIgnoreCase(Constant.WORD_KNOW_STATUS.WORD_KNOWN_UNKNOWN)
                || wordModel.getKnow().equalsIgnoreCase(Constant.WORD_KNOW_STATUS.WORD_KNOWN_NOTRATED) ?
                R.drawable.ic_unknown : R.drawable.ic_known);
        // check JA string
        if (!Utils.isEmpty(wordModel.getWord()) && LanguageUtil.checkJapanWord(wordModel.getWord())) {
            mApplication.getDalAiImpl().getHanajInfo(BaseEvent.Screen.WORD, mSharedPref.getToken(), mSharedPref.getUid(), EnumLanguage.findByFormatApi(Constant.API.STUDY_LANG).getIdApi(), mSharedPref.getSettingMotherTongue(), wordModel.getWord());
        }
    }

    private void updateTextViewMean() {
        if (Utils.isEmpty(wordModel.getMeaning())) {
            tvMean.setVisibility(View.GONE);
        } else {
            tvMean.setText(wordModel.getMeaning());
            tvMean.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onIOnCreate(Bundle savedInstanceState) {
        initView();
    }

    @Override
    public void onIRestart() {

    }

    @Override
    public void onIStart() {

    }

    @Override
    public void onIResume() {

    }

    @Override
    public void onIPause() {

    }

    @Override
    public void onIStop() {
    }

    @Override
    public void onIDestroy() {
    }

    @Override
    public void onIActivityResult(int requestCode, int resultCode, Intent data) {
        callBackTTS(requestCode, resultCode);
    }

    private void callBackTTS(final int requestCode, final int resultCode) {
        if (requestCode == TTS_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                initTTS();
            } else {
                startActivity(ConfirmTTSActivity.createIntent(this, BaseEvent.Screen.WORD));
            }
        }
    }

    @Override
    public void onIBackPressed() {
        mApplication.getEventBus().post(new SuccessEvent(isFromDalRead ? BaseEvent.Screen.DAL_READ : BaseEvent.Screen.WORD_LIST, BaseEvent.EventType.CALL_BACK_WORD, wordModel));
    }

    @OnClick(R.id.ivFavorite)
    public void clickFavorite() {
        if (isNetwork()) {
            showLoading();
            if (wordModel.isBookmark()) {
//                mApplication.getDalAiImpl().bookmarkDel(BaseEvent.Screen.WORD, mSharedPref.getSessionID(), mSharedPref.getUid(), Constant.API.STUDY_LANG, wordModel.getWord(), wordModel.getAllPosCurrentWord());
                mApplication.getDalAiImpl().bookmarkDel(BaseEvent.Screen.WORD, mSharedPref.getToken(), mSharedPref.getUid(), EnumLanguage.findByFormatApi(Constant.API.STUDY_LANG).getIdApi(), wordModel.getVoca(), wordModel.getVocaId(), wordModel.getVocaType());
            } else {
//                mApplication.getDalAiImpl().bookmarkAdd(BaseEvent.Screen.WORD, mSharedPref.getSessionID(), mSharedPref.getUid(), Constant.API.STUDY_LANG, wordModel.getWord(), wordModel.getAllPosCurrentWord());
                mApplication.getDalAiImpl().bookmarkAdd(BaseEvent.Screen.WORD, mSharedPref.getToken(), mSharedPref.getUid(), EnumLanguage.findByFormatApi(Constant.API.STUDY_LANG).getIdApi(), wordModel.getVoca(), wordModel.getVocaId(), wordModel.getVocaType());
            }
        }
    }

    @OnClick(R.id.ivStatus)
    public void clickStatus() {
        if (isNetwork()) {
            showLoading();
            if (wordModel.getKnow().equalsIgnoreCase(Constant.WORD_KNOW_STATUS.WORD_KNOWN_KNOWN)) {
                mApplication.getDalAiImpl().wordUnknown(BaseEvent.Screen.WORD, mSharedPref.getToken(), mSharedPref.getUid(), EnumLanguage.findByFormatApi(Constant.API.STUDY_LANG).getIdApi(), wordModel.getVoca(), wordModel.getVocaId());
            } else {
                mApplication.getDalAiImpl().wordKnown(BaseEvent.Screen.WORD, mSharedPref.getToken(), mSharedPref.getUid(), EnumLanguage.findByFormatApi(Constant.API.STUDY_LANG).getIdApi(), wordModel.getVoca(), wordModel.getVocaId());
            }
        }
    }

    @OnClick(R.id.ivSpeak)
    public void clickSpeck() {
        if (checkTTSSupported(BaseEvent.Screen.WORD)) {
            ivSpeck.setSelected(!ivSpeck.isSelected());
            checkAndSpeckText();
        }
    }

    private void checkSpeck() {
        if (!ivSpeck.isSelected() || !checkTTSSupported(BaseEvent.Screen.WORD)) {
            stopTTS();
            return;
        }
    }

    private void checkAndSpeckText() {
        if (!ivSpeck.isSelected() || !checkTTSSupported(BaseEvent.Screen.WORD)) {
            stopTTS();
            return;
        }
        initSpeckText();
    }

    private void initSpeckText() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callSpeakWordModel(wordModel);
            }
        });
    }

    @Override
    public void onErrorEvent(ErrorEvent event) {
        if (event.getScreen() == BaseEvent.Screen.WORD) {
            switch (event.getEventType()) {
                case GET_HANAJ_INFO:
                    hideLoading();
                    break;
                case BOOKMARK_ADD:
                    showError(R.string.error_msg_bookmark_api_fail);
                    break;
                case BOOKMARK_DEL:
                    showError(R.string.error_msg_bookmark_api_fail);
                    break;
                case WORD_KNOWN:
                    showError(R.string.error_msg_word_unknown_api_fail);
                    break;
                case WORD_UNKNOWN:
                    showError(R.string.error_msg_word_unknown_api_fail);
                    break;
            }
        }
    }

    @Override
    public void onSuccessEvent(SuccessEvent event) {
        if (event.getScreen() == BaseEvent.Screen.WORD) {
            switch (event.getEventType()) {
                case CALL_BACK_WORD:
                    final WordModel temp = (WordModel) event.getModel();
                    if (temp != null) {
                        wordModel = temp;
                        DLog.d(TAG, wordModel.toString());
                        updateTextViewMean();
                    }
                    break;
                case GET_HANAJ_INFO:
                    final String strHana = (String) event.getModel();
                    tvHanaj.setText(Utils.isEmpty(strHana) ? Constant.BASE_BLANK : Utils.fromHtml(strHana));
                    hideLoading();
                    break;
                case BOOKMARK_ADD:
                    updateButtonFavorite(true);
                    break;
                case BOOKMARK_DEL:
                    updateButtonFavorite(false);
                    break;
                case WORD_KNOWN:
                    updateButtonStatus(true);
                    break;
                case WORD_UNKNOWN:
                    updateButtonStatus(false);
                    break;
                case CALL_BACK_TTS:
                    startInstallTSS();
                    break;
                case CALL_BACK_TTS_SETTING:
                    isTTSSettingHasChanged = true;
                    break;
            }
        }
    }

    @Override
    public void onIUserLeaveHint() {

    }

    private void showError(int idStr) {
        ToastUtil.getInstance(this).show(idStr);
        hideLoading();
    }

    private void updateButtonFavorite(boolean isBookmark) {
        wordModel.setBookmark(isBookmark);
        ivFavorite.setSelected(isBookmark);
        hideLoading();
    }

    private void updateButtonStatus(boolean isKnown) {
        if (isKnown) {
            ivStatus.setImageResource(R.drawable.ic_known);
            wordModel.setKnow(Constant.WORD_KNOW_STATUS.WORD_KNOWN_KNOWN);
        } else {
            ivStatus.setImageResource(R.drawable.ic_unknown);
            wordModel.setKnow(Constant.WORD_KNOW_STATUS.WORD_KNOWN_UNKNOWN);
        }
        hideLoading();
    }
}
