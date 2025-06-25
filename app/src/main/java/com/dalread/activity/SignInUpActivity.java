package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.dalread.BuildConfig;
import com.dalread.R;
import com.dalread.base.BaseActivityNoHeader;
import com.dalread.base.BaseInit;
import com.dalread.base.EnumFlavor;
import com.dalread.base.EnumLanguage;
import com.dalread.databinding.ActivitySignInUpBinding;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.ErrorEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.EditTextUtils;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dalnim on 9/23/2021.
 */
public class SignInUpActivity extends BaseActivityNoHeader implements BaseInit {

    private boolean isLoginMode;
    private int currentIndex = 0; // default
    private ActivitySignInUpBinding binding;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, SignInUpActivity.class);
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
        binding = ActivitySignInUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //TODO : make it possible at activity too.
        //setCanceledOnTouchOutside(true);
        ButterKnife.bind(this);
        binding.etMail.addTextChangedListener(createTextWatcher(binding.etMail));
        binding.etPassword.addTextChangedListener(createTextWatcher(binding.etPassword));
        binding.etRetypePassword.addTextChangedListener(createTextWatcher(binding.etRetypePassword));
        binding.etRetypePassword.addTextChangedListener(createTextWatcher(binding.etRetypePassword));
        binding.etName.addTextChangedListener(createTextWatcher(binding.etName));

        updateButtonOk();
        initData();
        if (BuildConfig.FLAVOR.equals(EnumFlavor.DALVOCA.getName())) {
            binding.btnCancel.setVisibility(View.GONE);
//            binding.vDividerCancel.getRoot().setVisibility(View.GONE);
        }
        binding.etMail.requestFocus();

        binding.spLevel.setSelection(currentIndex);
        binding.spLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.etName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    isUserInfoValidBeforeSignUp();
                    return true;
                }
                return false;
            }
        });
        binding.etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkLogInIDAsEmail();
            }
            return false;
        });


    }

    @Override
    public void initView(View v) {

    }

    @Override
    public void initData() {
        isLoginMode = true;
        binding.etPassword.setText(R.string.login_password);
        binding.etMail.setText(mSharedPref.getEmail());
        binding.etMail.setSelection(binding.etMail.getText().length());
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
        if (event.getScreen() == BaseEvent.Screen.LOGIN) {
            switch (event.getEventType()) {
                case LOGIN:
                    hideLoading();
                    ToastUtil.getInstance(this).show(R.string.msg_login_fail);
                    break;
            }
        } else if (event.getScreen() == BaseEvent.Screen.SIGN_UP) {
            switch (event.getEventType()) {
                case SIGN_UP:
                    hideLoading();
                    ToastUtil.getInstance(this).show(R.string.msg_signup_fail);
                    break;
                case ISNEWUSER:
                    hideLoading();
                    ToastUtil.getInstance(this).show(R.string.msg_signup_fail_idMail_alreadyUsed);
                    break;
//                case LOGIN:
//                    hideLoading();
//                    ToastUtil.getInstance(this).show(R.string.msg_login_fail);
//                    onBackPressed();
//                    break;
            }
        }
    }

    @Override
    public void onSuccessEvent(SuccessEvent event) {
        if (event.getScreen() == BaseEvent.Screen.LOGIN) {
            switch (event.getEventType()) {
                case LOGIN:
                    hideLoading();
                    ToastUtil.getInstance(this).show(R.string.msg_login_success);
                    // This not duplicated. Need to forward posting this event for Main Screen again to refresh layout.
                    mApplication.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.LOGIN, event.getModel()));
                    onBackPressed();
                    break;
            }
        } else if (event.getScreen() == BaseEvent.Screen.SIGN_UP) {
            switch (event.getEventType()) {
                case SIGN_UP:
                    ToastUtil.getInstance(this).show(R.string.msg_signup_success);
                    checkLogInIDAsEmail();
                    break;
                case ISNEWUSER:
                    signUp();
                    break;
//                case LOGIN:
//                    hideLoading();
//                    ToastUtil.getInstance(this).show(R.string.msg_login_success);
//                    mApplication.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.LOGIN, null));
//                    onBackPressed();
//                    break;
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
            binding.btnLogIn.setBackgroundResource(R.drawable.bg_border_4dp_primary_color);
        } else {
            binding.btnLogIn.setBackgroundResource(R.drawable.bg_border_4dp_gray);
        }
    }

    private boolean isOkButtonEnable() {
        final String mMail = binding.etMail.getText().toString();
        final String mPass = binding.etPassword.getText().toString();
        return Utils.isValidMail(mMail) && !Utils.isEmpty(mPass);
    }


    @OnClick({R.id.tvChangePassword, R.id.btnLogIn, R.id.llSignUpHeader, R.id.llLogInHeader, R.id.btnSignUp})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvChangePassword:
                startActivity(ChangePasswordActivity.createIntent(this));
                onBackPressed();
                break;
            case R.id.llSignUpHeader:
                onClickBtnSignUp();
                break;
            case R.id.llLogInHeader:
                onClickBtnLogIn();
                break;
            case R.id.btnLogIn:
                checkLogInIDAsEmail();
                break;
            case R.id.btnSignUp:
                if (isUserInfoValidBeforeSignUp()) {
                    signUpWhenNewUser();
                }
                break;
        }
    }

    public void onClickBtnSignUp() {
        if (binding.llSignUpBody.getVisibility() != View.VISIBLE) {
            binding.tvSignInUpMessage.setText(R.string.sign_up_message);
            binding.tvSignUp.setTextColor(ContextCompat.getColor(this, R.color.primaryColor));
            binding.tvSignUpMarker.setVisibility(View.VISIBLE);
            binding.llSignUpBody.setVisibility(View.VISIBLE);

            binding.etMail.setText("");
            binding.etPassword.setText("");
            binding.etPassword.setHint(R.string.sign_up_password);
            binding.etRetypePassword.setText("");
            binding.etName.setText("");

            binding.tvLogIn.setTextColor(ContextCompat.getColor(this, R.color.textSignInUpMessageColor));
            binding.tvLogInMarker.setVisibility(View.INVISIBLE);
            binding.llLogInBody.setVisibility(View.GONE);
        }
    }

    public void onClickBtnLogIn() {
        if (binding.llLogInBody.getVisibility() != View.VISIBLE) {
            binding.tvSignInUpMessage.setText(R.string.login_message_to_sign_up);
            binding.tvSignUp.setTextColor(ContextCompat.getColor(this, R.color.textSignInUpMessageColor));
            binding.tvSignUpMarker.setVisibility(View.INVISIBLE);
            binding.llSignUpBody.setVisibility(View.GONE);

            binding.etMail.setText("");
            binding.etPassword.setText("");
            binding.etPassword.setHint(R.string.login_password);

            binding.tvLogIn.setTextColor(ContextCompat.getColor(this, R.color.primaryColor));
            binding.tvLogInMarker.setVisibility(View.VISIBLE);
            binding.llLogInBody.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btnCancel)
    public void onClickBtnCancel() {
        onBackPressed();
    }

    private void checkLogInIDAsEmail() {
        final String mEmail = binding.etMail.getText().toString().trim();
        final String mPassword = binding.etPassword.getText().toString();
        if (!Utils.isValidMail(mEmail)) {
            ToastUtil.getInstance(this).show(R.string.error_msg_check_mail);
            return;
        }
        showLoading();
        if (BuildConfig.FLAVOR.equals(EnumFlavor.DALVOCA.getName())) {
            mApplication.getDalAiImpl().login(
                    BaseEvent.Screen.LOGIN,
                    mEmail, mPassword,
                    mSharedPref.getLangStudyCode()
            );
        } else {
            mApplication.getDalAiImpl().login(BaseEvent.Screen.LOGIN, mEmail, mPassword, EnumLanguage.findByFormatApi(Constant.API.STUDY_LANG).getIdApi());
        }
    }

    private boolean isUserInfoValidBeforeSignUp() {
        final String mMail = binding.etMail.getText().toString().trim();
        final String mPassword = binding.etPassword.getText().toString().trim();
        final String mRetypePassword = binding.etRetypePassword.getText().toString().trim();
        final String mName = binding.etName.getText().toString().trim();

        if (!Utils.isValidMail(mMail)) {
            ToastUtil.getInstance(this).show(R.string.error_msg_check_mail);
            return false;
        }
        if (!Utils.checkLengthPassword(mPassword)) {
            ToastUtil.getInstance(this).show(R.string.error_msg_longer_than_4_characters_password);
            return false;
        }
        if (!mPassword.equals(mRetypePassword)) {
            ToastUtil.getInstance(this).show(R.string.error_msg_password_does_not_match);
            return false;
        }

        if (!Utils.checkLengthUserName(mName)) {
            ToastUtil.getInstance(this).show(R.string.error_msg_longer_than_2_characters_name);
            return false;
        }
//        if (BuildConfig.FLAVOR.equals(EnumFlavor.DALVOCA.getName())) {
//            mApplication.getDalAiImpl().signUp(
//                    BaseEvent.Screen.SIGN_UP,
//                    mMail, mPassword,
//                    mName,
//                    mSharedPref.getLangStudyCode(),
//                    mSharedPref.getDisplayLanguage(),
//                    Constant.SIGN_UP_LEVELS[currentIndex]
//            );
//        } else {
//            mApplication.getDalAiImpl().signUp(BaseEvent.Screen.SIGN_UP, mMail, mPassword, mName, EnumLanguage.findByFormatApi(Constant.API.STUDY_LANG).getIdApi(), mSharedPref.getSettingMotherTongue(), Constant.SIGN_UP_LEVELS[currentIndex]);
//        }
        return true;
    }

    private void signUpWhenNewUser() {
        showLoading();
        final String idMail = binding.etMail.getText().toString().trim();
        mApplication.getDalAiImpl().isNewUser(BaseEvent.Screen.SIGN_UP, idMail);
    }

    private void signUp() {
        final String mMail = binding.etMail.getText().toString().trim();
        final String mPassword = binding.etPassword.getText().toString().trim();
        final String mName = binding.etName.getText().toString().trim();

        showLoading();
        //Todo : remove if statement. make it one.
        if (BuildConfig.FLAVOR.equals(EnumFlavor.DALVOCA.getName())) {
            mApplication.getDalAiImpl().signUp(
                    BaseEvent.Screen.SIGN_UP,
                    mMail, mPassword,
                    mName,
                    mSharedPref.getLangStudyCode(),
                    mSharedPref.getMotherTongueLanguage(),
                    Constant.SIGN_UP_LEVELS[currentIndex]
            );
        } else {
            mApplication.getDalAiImpl().signUp(BaseEvent.Screen.SIGN_UP, mMail, mPassword, mName, EnumLanguage.findByFormatApi(Constant.API.STUDY_LANG).getIdApi(), mSharedPref.getSettingMotherTongue(), Constant.SIGN_UP_LEVELS[currentIndex]);
        }
    }
}
