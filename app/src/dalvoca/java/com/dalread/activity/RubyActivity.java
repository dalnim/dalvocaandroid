package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseDalVocaPlayVocaActivity;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

public class RubyActivity extends BaseDalVocaPlayVocaActivity {

    @BindView(R.id.nav_bottom) BottomNavigationView bottomNavigationView;
    private int currentBottomNavigationId;
    private int type;
    private boolean isReload = false;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_ruby;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEventBus();
        initData();
        initBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isReload) {
            Intent i = new Intent(this, RubyActivity.class);
            finish();
            startActivity(i);
            isReload = false;
        }
    }

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }
    @Override
    public void onHeaderLeft2Click() {
    }
    @Override
    public void onHeaderRightClick() {

    }

    @Override
    public void onHeaderIconRightClick() {

    }

    @Override
    public void onHeaderTextRightClick() {

    }

    @Override
    protected void onDestroy() {
        unRegisterEventBus();
        super.onDestroy();
    }

    private void initData() {
        final Intent intent = getIntent();
        if (intent != null) {
            type = intent.getIntExtra(Constant.BUNDLE.KEY_RUBY_TYPE, Constant.RUBY.TYPE.TEXT);
        } else {
            type = Constant.RUBY.TYPE.TEXT;
        }
    }

    private void initBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id != currentBottomNavigationId) {
                    switch (id) {
                        case R.id.nav_etc:
                            if (type == Constant.RUBY.TYPE.TABLE) {
                                Utils.loadFragment(RubyActivity.this, new RubyTableFragment(), getFragmentContainerId(), false);
                                toolbar.setTitle(R.string.admin_ruby_table);
                            } else {
                                Utils.loadFragment(RubyActivity.this, new RubyTextFragment(), getFragmentContainerId(), false);
                                toolbar.setTitle(R.string.admin_ruby_text_view);
                            }
                            currentBottomNavigationId = id;
                            return true;
                    }
                }
                return false;
            }
        });
        if (getSupportFragmentManager().getFragments().isEmpty()) {
            bottomNavigationView.setSelectedItemId(R.id.nav_etc);
        }
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.RUBY_ACTIVITY) {
            isReload = true;
            DLog.d(getLogTag(), "isReload=" + isReload);
        }
    }
}
