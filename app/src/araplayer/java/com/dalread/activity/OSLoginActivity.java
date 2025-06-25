package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.dalread.R;
import com.dalread.base.BaseActivityNoHeader;
import com.dalread.base.BaseInit;
import com.dalread.databinding.ActivityOsLoginBinding;
import com.dalread.network.DalApiListener;
import com.dalread.network.OpenSubtitleApi;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.ErrorEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.EditTextUtils;
import com.dalread.util.SnackbarUtil;
import com.dalread.util.StringUtils;
import com.dalread.util.Utils;

/**
 * Created by Dalnim on 9/23/2021.
 */
public class OSLoginActivity extends BaseActivityNoHeader implements BaseInit {
    private ActivityOsLoginBinding binding;
    private OpenSubtitleApi openSubtitleApi;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, OSLoginActivity.class);
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
        binding = ActivityOsLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        openSubtitleApi = new OpenSubtitleApi(this);
        binding.etMail.addTextChangedListener(textWatcher);
        binding.etPassword.addTextChangedListener(textWatcher);
        updateLoginBtnState();
        initData();

        binding.etMail.requestFocus();
        binding.tvSignInUpMessage2.setText(StringUtils.getUnderlineOnWholeText(this, binding.tvSignInUpMessage2.getText().toString()));
        binding.tvSignInUpMessage2.setOnClickListener( v -> {
            Utils.openOpenSubtitleWebSite(OSLoginActivity.this);
        });
        binding.etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkLogInIDAsEmail();
                return true;
            }
            return false;
        });
        EditTextUtils.setClearButtonOnTouchListener(binding.etMail);
        EditTextUtils.setClearButtonOnTouchListener(binding.etPassword);
        binding.btnLogIn.setOnClickListener(v -> checkLogInIDAsEmail());
        binding.btnCancel.setOnClickListener(v -> onBackPressed());

    }

    @Override
    public void initView(View v) {

    }

    @Override
    public void initData() {
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
            updateLoginBtnState();
            EditTextUtils.updateClearButtonVisibility(binding.etMail);
            EditTextUtils.updateClearButtonVisibility(binding.etPassword);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void updateLoginBtnState() {
        final String mEmail = binding.etMail.getText().toString().trim();
        final String mPass = binding.etPassword.getText().toString();
        binding.btnLogIn.setEnabled(!Utils.isEmpty(mEmail) && !Utils.isEmpty(mPass));
    }

    private void checkLogInIDAsEmail() {
        final String id = StringUtils.getUsernameFromEmail(binding.etMail.getText().toString().trim());
        final String password = binding.etPassword.getText().toString();

        showLoading();
        OpenSubtitleApi.OSLoginParam param = new OpenSubtitleApi.OSLoginParam(id, password);
        openSubtitleApi.login(param, new DalApiListener<OpenSubtitleApi.OSLoginResponse>() {
            @Override
            public void onSuccess(OpenSubtitleApi.OSLoginResponse response) {
                mSharedPref.setOpenSubtitleLoginToken(response.getToken());
                hideLoading();
                mApplication.getEventBus().post(new SuccessEvent(BaseEvent.Screen.OPEN_SUBTITLE_LOGIN, BaseEvent.EventType.LOGIN, ""));
                onBackPressed();
            }

            @Override
            public void onFailure(String error) {
                mSharedPref.setOpenSubtitleLoginToken("");
                SnackbarUtil.getInstance(OSLoginActivity.this).show(R.string.msg_login_fail_to_open_subtitle);
                hideLoading();
            }
        });
    }
}
