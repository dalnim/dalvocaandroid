package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dalread.base.BaseInit;
import com.dalread.base.BaseSplashActivity;
import com.dalread.network.events.ErrorEvent;
import com.dalread.network.events.SuccessEvent;

/**
 * Created by Dalnim on 10/27/2021.
 */
public class SplashActivity extends BaseSplashActivity implements BaseInit {
//    private ActivitySplashBinding binding;
//
//    public static Intent createIntentNewTask(Context context, final String data) {
//        Intent intent = new Intent(context, SplashActivity.class);
//        intent.putExtra(Constant.KEY_DATA_SHARE_APP, data);
//        return intent;
//    }

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
        super.initView();

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
}