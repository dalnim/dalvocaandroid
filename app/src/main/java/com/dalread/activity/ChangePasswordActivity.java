package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.dalread.R;
import com.dalread.base.BaseActivityNoHeader;
import com.dalread.base.BaseInit;
import com.dalread.databinding.ActivityChangePasswordBinding;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.ErrorEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.EditTextUtils;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

/**
 * Created by JetVHS on 2/26/2017.
 */
public class ChangePasswordActivity extends BaseActivityNoHeader implements BaseInit {

    private ActivityChangePasswordBinding binding;
//    @BindView(R.id.etMail) EditText etMail;
//    @BindView(R.id.etCurrentPassword) EditText etCurrentPassword;
//    @BindView(R.id.etNewPassword) EditText etNewPassword;
//    @BindView(R.id.etRetypePassword) EditText etRetypePassword;
//    @BindView(R.id.btnOk) TextView btnOk;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
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
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.etMail.addTextChangedListener(createTextWatcher(binding.etMail));
        binding.etCurrentPassword.addTextChangedListener(createTextWatcher(binding.etCurrentPassword));
        binding.etNewPassword.addTextChangedListener(createTextWatcher(binding.etNewPassword));
        binding.etRetypePassword.addTextChangedListener(createTextWatcher(binding.etRetypePassword));
        //Change Password에서는 SignUpAcitvity에서 처럼 createTextWatcher의 onTextChanged가 처음에 안불려서 X 아이콘이 빈 에디트 텍스트에 보여서 첨에는 안보이게 따로 처리해준다.
        EditTextUtils.updateClearButtonVisibility(binding.etMail);
        EditTextUtils.updateClearButtonVisibility(binding.etCurrentPassword);
        EditTextUtils.updateClearButtonVisibility(binding.etNewPassword);
        EditTextUtils.updateClearButtonVisibility(binding.etRetypePassword);

        binding.btnOk.setOnClickListener( v -> {
            checkPassword();
        });
        binding.btnCancel.setOnClickListener( v -> {
            onBackPressed();
        });
        updateButtonOk();
        initData();
    }

    @Override
    public void initView(View v) {

    }

    @Override
    public void initData() {

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
        Utils.hideSoftKeyboard(this, binding.etMail);
    }

    @Override
    public void onErrorEvent(ErrorEvent event) {
        if (event.getScreen() == BaseEvent.Screen.CHANGE_PASSWORD) {
            switch (event.getEventType()) {
                case CHANGE_PASSWORD:
                    hideLoading();
                    ToastUtil.getInstance(this).show(R.string.msg_change_password_fail);
                    break;
            }
        }
    }

    @Override
    public void onSuccessEvent(SuccessEvent event) {
        if (event.getScreen() == BaseEvent.Screen.CHANGE_PASSWORD) {
            switch (event.getEventType()) {
                case CHANGE_PASSWORD:
                    final int msgId = (int) event.getModel();
                    hideLoading();
                    ToastUtil.getInstance(this).show(msgId);
                    if (msgId == R.string.msg_change_password_success) {
                        onBackPressed();
                    }
                    break;
            }
        }
    }

    @Override
    public void onIUserLeaveHint() {

    }
    private TextWatcher createTextWatcher(EditText editText) {
        EditTextUtils.setClearButtonOnTouchListener(editText);
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateButtonOk();
                EditTextUtils.updateClearButtonVisibility(editText);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    private void updateButtonOk() {
        if (isOkButtonEnable()) {
            binding.btnOk.setBackgroundResource(R.drawable.bg_border_4dp_primary_color);
        } else {
            binding.btnOk.setBackgroundResource(R.drawable.bg_border_4dp_gray);
        }
    }

    private boolean isOkButtonEnable() {
        final String mMail = binding.etMail.getText().toString();
        final String mCurrentPass = binding.etCurrentPassword.getText().toString();
        final String mNewPass = binding.etNewPassword.getText().toString();
        final String mRetypePass = binding.etRetypePassword.getText().toString();
        return Utils.isValidMail(mMail) && !Utils.isEmpty(mCurrentPass) && !Utils.isEmpty(mNewPass) && !Utils.isEmpty(mRetypePass);
    }

    private void checkPassword() {
        final String mMail = binding.etMail.getText().toString().trim();
        final String mCurrentPassword = binding.etCurrentPassword.getText().toString();
        final String mNewPassword = binding.etNewPassword.getText().toString();
        final String mNewRetypePassword = binding.etRetypePassword.getText().toString();
        if (!Utils.isValidMail(mMail)) {
            ToastUtil.getInstance(this).show(R.string.error_msg_check_mail);
            return;
        }
        if (!Utils.checkLengthPassword(mCurrentPassword) || !Utils.checkLengthPassword(mNewPassword) || !Utils.checkLengthPassword(mNewRetypePassword)) {
            ToastUtil.getInstance(this).show(R.string.error_msg_longer_than_4_characters_password);
            return;
        }
        if (!mNewPassword.equals(mNewRetypePassword)) {
            ToastUtil.getInstance(this).show(R.string.error_msg_new_password_does_not_match);
            return;
        }
        showLoading();
        mApplication.getDalAiImpl().resetPassword(BaseEvent.Screen.CHANGE_PASSWORD, mSharedPref.getToken(), mMail, mCurrentPassword, mNewPassword);
    }
}
