package com.dalread.activity;

import com.dalread.base.BaseInit;
import com.dalread.base.BaseSplashActivity;

/**
 * Created by Dalnim on 10/27/2021.
 */
public class SplashActivity extends BaseSplashActivity implements BaseInit {
    protected long getSplashTime() {
        long splashTime = 100; //Constant.SPLASH_TIME;
        if (getIntent().hasExtra(getIntent().EXTRA_PROCESS_TEXT)) {
            //skip splash screen.
            splashTime = 0L;
        }
        return splashTime;
    }
}