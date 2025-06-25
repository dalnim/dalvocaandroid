package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dalread.R;
import com.dalread.base.BaseActivityNoHeader;
import com.dalread.base.BaseInit;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.ErrorEvent;
import com.dalread.network.events.SuccessEvent;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by JetVHS on 2/26/2017.
 */
public class ConfirmTTSActivity extends BaseActivityNoHeader implements BaseInit {

    private static final String KEY_SCREEN = "key_screen";
    private BaseEvent.Screen screen;

    public static Intent createIntent(Context context, BaseEvent.Screen screen) {
        Intent intent = new Intent(context, ConfirmTTSActivity.class);
        intent.putExtra(KEY_SCREEN, screen);
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
        setContentView(R.layout.activity_confirm_tts);
        ButterKnife.bind(this);
        initData();
    }

    @Override
    public void initView(View v) {

    }

    @Override
    public void initData() {
        if (getIntent() != null) {
            screen = (BaseEvent.Screen) getIntent().getSerializableExtra(KEY_SCREEN);
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

    }

    @Override
    public void onIBackPressed() {

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

    @OnClick(R.id.btnOk)
    public void onClickBtnOk() {
        mApplication.getEventBus().post(new SuccessEvent(screen, BaseEvent.EventType.CALL_BACK_TTS, null));
    }

    @OnClick(R.id.btnCancel)
    public void onClickBtnCancel() {
        onBackPressed();
    }
}
