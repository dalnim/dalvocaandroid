package com.dalread.activity;

import com.dalread.base.BaseInit;
import com.dalread.base.BaseSplashActivity;
import com.dalread.util.Constant;

/**
 * Created by Dalnim on 10/27/2021.
 */
public class SplashActivity extends BaseSplashActivity implements BaseInit {

    protected long getSplashTime() {
        long splashTime = Constant.SPLASH_TIME;
        if (getIntent().hasExtra(Constant.OPEN_ARA_HANJA_DATA_KEY)) {
            // Open AraHanja from AraPlayer, skip splash screen.
            splashTime = 0L;
        }
        return splashTime;
    }
//    @Override
//    protected void onPermissionSuccess(int request_code) {
//    }
//
//    @Override
//    protected void onPermissionFail(int request_code) {
//    }
//
//    @Override
//    protected void onChangeLanguage() {
//    }
//
//    @Override
//    public void initView() {
//        super.initView();
//    }
//
//    @Override
//    public void initView(View v) {
//    }
//
//    @Override
//    public void initData() {
//
//    }
//
//    @Override
//    public void onIOnCreate(Bundle savedInstanceState) {
//        initView();
//    }
//
//    @Override
//    public void onIRestart() {
//    }
//
//    @Override
//    public void onIStart() {
//    }
//
//    @Override
//    public void onIResume() {
//    }
//
//    @Override
//    public void onIPause() {
//    }
//
//    @Override
//    public void onIStop() {
//    }
//
//    @Override
//    public void onIDestroy() {
//    }
//
//    @Override
//    public void onIActivityResult(int requestCode, int resultCode, Intent data) {
//    }
//
//    @Override
//    public void onIBackPressed() {
//    }
//
//    @Override
//    public void onErrorEvent(ErrorEvent event) {
//    }
//
//    @Override
//    public void onSuccessEvent(SuccessEvent event) {
//    }
//
//    @Override
//    public void onIUserLeaveHint() {
//    }
}