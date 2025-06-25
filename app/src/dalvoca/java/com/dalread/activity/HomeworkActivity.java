package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BasePlayVocaActivity;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

public class HomeworkActivity extends BasePlayVocaActivity {

    @BindView(R.id.nav_bottom) BottomNavigationView bottomNavigationView;
    private int currentBottomNavigationId;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_homework;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBottomNavigation();
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {

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

    private void initBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id != currentBottomNavigationId) {
                    switch (id) {
                        case R.id.nav_homework:
                            final HomeworkFragment homeworkFragment = new HomeworkFragment();
                            Utils.loadFragment(
                                    HomeworkActivity.this,
                                    homeworkFragment,
                                    getFragmentContainerId(),
                                    false
                            );
                            currentBottomNavigationId = id;
                            toolbar.setTitle(R.string.nav_title_homework);
                            toolbar.showIconRight();
                            toolbar.getIconRight().setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    homeworkFragment.onDeleteAllClick();
                                }
                            });
                            return true;
                        case R.id.nav_know:
                            Utils.loadFragment(
                                    HomeworkActivity.this,
                                    new DoYouKnowFragment(),
                                    getFragmentContainerId(),
                                    false
                            );
                            currentBottomNavigationId = id;
                            toolbar.setTitle(R.string.do_you_know_this);
                            toolbar.hideIconRight();
                            toolbar.getIconRight().setOnClickListener(null);
                            return true;
                    }
                }
                return false;
            }
        });
        if (getSupportFragmentManager().getFragments().isEmpty()) {
            Intent intent = getIntent();
            if (intent != null) {
                String data = intent.getStringExtra(Constant.BUNDLE.KEY_METHOD_NAME);
                if (Constant.NOTIFICATION_VALUE.DO_YOU_KNOW_THIS_VOCA.equals(data)) {
                    bottomNavigationView.setSelectedItemId(R.id.nav_know);
                    return;
                }
            }
            bottomNavigationView.setSelectedItemId(R.id.nav_homework);
        }
    }
}
