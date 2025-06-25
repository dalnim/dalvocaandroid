package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.dalread.R;
import com.dalread.base.BaseActivityNoHeader;
import com.dalread.base.BaseInit;
import com.dalread.databinding.ActivityChangePasswordBinding;
import com.dalread.databinding.ActivityChangePasswordHiddenFilesBinding;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.ErrorEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by JetVHS on 2/26/2017.
 */
public class ChangePasswordHiddenFilesActivity extends BaseActivityNoHeader implements BaseInit {
    private ActivityChangePasswordHiddenFilesBinding binding;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, ChangePasswordHiddenFilesActivity.class);
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
        binding = ActivityChangePasswordHiddenFilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        binding.etCurrentPassword.addTextChangedListener(textWatcher);
        binding.etNewPassword.addTextChangedListener(textWatcher);
        binding.etRetypePassword.addTextChangedListener(textWatcher);
        updateButtonOk();
        initData();
        Utils.showSoftKeyboard(this, binding.etCurrentPassword);
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
        Utils.hideSoftKeyboard(this, binding.etNewPassword);
    }

    @Override
    public void onErrorEvent(ErrorEvent event) {

    }

    @Override
    public void onSuccessEvent(SuccessEvent event) {
    }

    @Override
    public void onIUserLeaveHint() {

    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateButtonOk();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void updateButtonOk() {
        final String mCurrentPass = binding.etCurrentPassword.getText().toString();
        final String mNewPass = binding.etNewPassword.getText().toString();
        final String mRetypePass = binding.etRetypePassword.getText().toString();
        binding.btnOk.setEnabled(!Utils.isEmpty(mCurrentPass) && !Utils.isEmpty(mNewPass) && !Utils.isEmpty(mRetypePass));
    }

    @OnClick(R.id.btnOk)
    public void onClickBtnOk() {
        checkPassword();
    }

    @OnClick(R.id.btnCancel)
    public void onClickBtnCancel() {
        onBackPressed();
    }

    private void checkPassword() {
        final String mCurrentPassword = binding.etCurrentPassword.getText().toString();
        final String mNewPassword = binding.etNewPassword.getText().toString();
        final String mRetypePassword = binding.etRetypePassword.getText().toString();

        String storedPassword = mSharedPref.getPasswordHiddenFiles();

        if (!mCurrentPassword.equals(storedPassword.trim())) {
            ToastUtil.getInstance(this).show(R.string.error_msg_current_password_is_wrong);
            return;
        }

        if (!Utils.checkLengthPassword(mNewPassword) || !Utils.checkLengthPassword(mRetypePassword)) {
            ToastUtil.getInstance(this).show(R.string.error_msg_longer_than_4_characters_password);
            return;
        }
        if (!mNewPassword.equals(mRetypePassword)) {
            ToastUtil.getInstance(this).show(R.string.error_msg_password_does_not_match);
            return;
        }


        final String mPassword = binding.etNewPassword.getText().toString().trim();
        mSharedPref.setPasswordHiddenFiles(mPassword);
        mApplication.getEventBus().post(new SuccessEvent(BaseEvent.Screen.SIGN_IN_UP_HIDDEN_FILES, BaseEvent.EventType.LOGIN, true));
        onBackPressed();
    }
}
