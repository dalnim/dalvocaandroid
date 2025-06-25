package com.dalread.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.dalread.DalFlavor;
import com.dalread.databinding.ActivitySplashBinding;
import com.dalread.network.events.ErrorEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

/**
 * Created by JetVHS on 3/4/2017.
 */
public class BaseSplashActivity extends BaseActivityNoHeader implements BaseInit {
    protected ActivitySplashBinding binding;

//    public static Intent createIntentNewTask(Context context, final String data) {
//        Intent intent = new Intent(context, BaseSplashActivity.class);
//        intent.putExtra(Constant.KEY_DATA_SHARE_APP, data);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    private View getContentView() {
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    protected long getSplashTime() {
        return Constant.SPLASH_TIME;
    }

    @Override
    public void initView() {
        setContentView(getContentView());
        Utils.removeAllNotification(this);
        long splashTime = getSplashTime();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DalFlavor.initData(BaseSplashActivity.this, mSharedPref);
            }
        }, splashTime);
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

//    private void openMainMovie() {
//        Bundle b = getIntent().getExtras();
//        String mText = null;
//        if (b != null) {
//            mText = b.getString(Constant.KEY_DATA_SHARE_APP);
//        }
//        Log.d(TAG, "mText=" + mText);
//        if (!Utils.isEmpty(mText)) {
//            mApplication.setOpenConfirm(false);
//            mSharedPref.setCopiedText(mText);
//            startActivity(MainActivityMovie.createIntent(this, mText));
//        } else {
//            startActivity(MainActivityMovie.createIntent(this));
//        }
//        finishAffinity();
//    }
}