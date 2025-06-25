package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dalread.R;
import com.dalread.base.BaseActivityNoHeader;
import com.dalread.base.BaseInit;
import com.dalread.base.EnumLanguage;
import com.dalread.model.WordModel;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.ErrorEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by JetVHS on 3/18/2017.
 */
public class WordChangeActivity extends BaseActivityNoHeader implements BaseInit {

    private static final String KEY_MEAN = "key_mean";
    private WordModel wordModel;
    @BindView(R.id.etMean)
    EditText etMean;
    @BindView(R.id.btnOk)
    TextView btnOk;
    @BindView(R.id.btnCancel)
    TextView btnCancel;

    public static Intent createIntent(Context context, WordModel wordModel) {
        Intent intent = new Intent(context, WordChangeActivity.class);
        intent.putExtra(KEY_MEAN, wordModel);
        return intent;
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
        setContentView(R.layout.activity_change_mean);
        ButterKnife.bind(this);
        initData();
    }

    @Override
    public void initView(View v) {

    }

    @Override
    public void initData() {
        if (getIntent() != null) {
            wordModel = getIntent().getParcelableExtra(KEY_MEAN);
        }
        if (wordModel == null) {
            finish();
            return;
        }
        etMean.setText(wordModel.getMeaning());
        etMean.setSelection(etMean.getText().length());
        etMean.setCursorVisible(true);
        etMean.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLayoutButtonOk(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        updateLayoutButtonOk(wordModel.getMeaning());
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

    }

    @Override
    public void onIBackPressed() {
        Utils.hideSoftKeyboard(this, etMean);
    }

    @Override
    public void onErrorEvent(ErrorEvent event) {
        if (event.getScreen() == BaseEvent.Screen.WORD_MEANING) {
            switch (event.getEventType()) {
                case UPDATE_WORD_MEANING:
                    ToastUtil.getInstance(WordChangeActivity.this).show(R.string.error_msg_word_change_fail);
                    hideLoading();
                    break;
            }
        }
    }

    @Override
    public void onSuccessEvent(SuccessEvent event) {
        if (event.getScreen() == BaseEvent.Screen.WORD_MEANING) {
            switch (event.getEventType()) {
                case UPDATE_WORD_MEANING:
                    ToastUtil.getInstance(WordChangeActivity.this).show(R.string.error_msg_word_change_success);
                    hideLoading();
                    mApplication.getEventBus().post(new SuccessEvent(BaseEvent.Screen.WORD, BaseEvent.EventType.CALL_BACK_WORD, wordModel));
                    onBackPressed();
                    break;
            }
        }
    }

    @Override
    public void onIUserLeaveHint() {

    }

    private void updateLayoutButtonOk(String value) {
        btnOk.setEnabled(!Utils.isEmpty(value) && !value.equals(wordModel.getMeaning()));
    }

    @OnClick(R.id.btnOk)
    public void clickBtnOk() {
        if (isNetwork()) {
            showLoading();
            wordModel.setMeaning(etMean.getText().toString());
            mApplication.getDalAiImpl().updateWordMeaning(BaseEvent.Screen.WORD_MEANING, mSharedPref.getToken(), mSharedPref.getUid(), EnumLanguage.findByFormatApi(Constant.API.STUDY_LANG).getIdApi(), mSharedPref.getSettingMotherTongueCode(), wordModel.getVocaId(), wordModel.getVocaType(), wordModel.getVoca(), wordModel.getMeaning(), wordModel.getMeaningDetailed(), wordModel.getPronounce());
        }
    }

    @OnClick(R.id.btnCancel)
    public void clickBtnCancel() {
        onBackPressed();
    }
}
