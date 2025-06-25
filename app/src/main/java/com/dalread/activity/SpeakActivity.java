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
import com.dalread.util.Constant;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by JetVHS on 3/19/2017.
 */
public class SpeakActivity extends BaseActivityNoHeader implements BaseInit{

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, SpeakActivity.class);
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
        setContentView(R.layout.activity_speak);
        ButterKnife.bind(this);
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

    @OnClick(R.id.btnSpeak1)
    public void clickSpeak1() {
        callBack(Constant.SPEAKS.SPEAK_1);
    }

    @OnClick(R.id.btnSpeak3)
    public void clickSpeak3() {
        callBack(Constant.SPEAKS.SPEAK_3);
    }

    @OnClick(R.id.btnSpeak5)
    public void clickSpeak5() {
        callBack(Constant.SPEAKS.SPEAK_5);
    }

    @OnClick(R.id.btnSpeak10)
    public void clickSpeak10() {
        callBack(Constant.SPEAKS.SPEAK_10);
    }

    @OnClick(R.id.btnCancel)
    public void clickCancel() {
        onBackPressed();
    }

    private void callBack(int index) {
        mApplication.getEventBus().post(new SuccessEvent(BaseEvent.Screen.WORD_LIST, BaseEvent.EventType.CALL_BACK_SPEAK, index));
        onBackPressed();
    }
}
