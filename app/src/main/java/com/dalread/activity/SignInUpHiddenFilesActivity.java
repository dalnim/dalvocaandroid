package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.dalread.R;
import com.dalread.base.BaseActivityNoHeader;
import com.dalread.base.BaseInit;
import com.dalread.databinding.ActivitySignInHiddenFilesBinding;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.ErrorEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * Created by Dalnim on 9/23/2021.
 */
public class SignInUpHiddenFilesActivity extends BaseActivityNoHeader implements BaseInit {

    private boolean isLoginMode;
    private int currentIndex = 0; // default
    private ActivitySignInHiddenFilesBinding binding;

    public static Intent createIntent(Context context, boolean isLoginMode) {
        Intent intent = new Intent(context, SignInUpHiddenFilesActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_DATA, isLoginMode);
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
        binding = ActivitySignInHiddenFilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ButterKnife.bind(this);
        initData();
        updateUIForLoginOrSingup();
        Utils.showSoftKeyboard(this, binding.etPassword);
    }

    private void updateUIForLoginOrSingup() {
        if (isLoginMode) {
            binding.tvSignInUpMessage.setText(R.string.log_in_message_hidden_files);

            binding.etPassword.setText("");
            binding.etPassword.setHint(R.string.login_password);

            binding.llLogInBody.setVisibility(View.VISIBLE);
            binding.llSignUpBody.setVisibility(View.GONE);

        } else {
            binding.tvSignInUpMessage.setText(R.string.sign_up_message_hidden_files);

            binding.etPassword.setText("");
            binding.etPassword.setHint(R.string.sign_up_password);
            binding.etRetypePassword.setText("");

            binding.llLogInBody.setVisibility(View.GONE);
            binding.llSignUpBody.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initView(View v) {

    }

    @Override
    public void initData() {
        this.isLoginMode = getIntent().getExtras().getBoolean(Constant.PLAYER.INTENT.KEY_DATA, true);
        binding.etPassword.setText(R.string.login_password);
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
        Utils.hideSoftKeyboard(this, binding.etPassword);
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

    @OnEditorAction(R.id.etPassword)
    boolean onDonePressed(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            checkPasswordToLogIn();
        }
        return false;
    }

    @OnClick({R.id.tvChangePassword, R.id.btnLogIn, R.id.btnSignUp})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvChangePassword:
                startActivity(ChangePasswordHiddenFilesActivity.createIntent(this));
                onBackPressed();
                break;
            case R.id.btnLogIn:
                checkPasswordToLogIn();
                break;
            case R.id.btnSignUp:
                if (isPasswordValid()) {
                    signUp();
                }
                break;
        }
    }

    @OnClick(R.id.btnCancel)
    public void onClickBtnCancel() {
        onBackPressed();
    }

    private void checkPasswordToLogIn() {
        final String mPassword = binding.etPassword.getText().toString().trim();
        String storedPassword = mSharedPref.getPasswordHiddenFiles();

        if (mPassword.equals(storedPassword.trim())) {
            mApplication.getEventBus().post(new SuccessEvent(BaseEvent.Screen.SIGN_IN_UP_HIDDEN_FILES, BaseEvent.EventType.LOGIN, true));
            onBackPressed();
        } else {
            ToastUtil.getInstance(this).show(R.string.error_msg_password_does_not_match);
        }
    }

    private boolean isPasswordValid() {
        final String mPassword = binding.etPassword.getText().toString().trim();
        final String mRetypePassword = binding.etRetypePassword.getText().toString().trim();

        if (!Utils.checkLengthPassword(mPassword)) {
            ToastUtil.getInstance(this).show(R.string.error_msg_longer_than_4_characters_password);
            return false;
        }
        if (!mPassword.equals(mRetypePassword)) {
            ToastUtil.getInstance(this).show(R.string.error_msg_password_does_not_match);
            return false;
        }

        return true;
    }

    private void signUp() {
        final String mPassword = binding.etPassword.getText().toString().trim();
        mSharedPref.setPasswordHiddenFiles(mPassword);
        mApplication.getEventBus().post(new SuccessEvent(BaseEvent.Screen.SIGN_IN_UP_HIDDEN_FILES, BaseEvent.EventType.LOGIN, true));
        onBackPressed();
    }
}
