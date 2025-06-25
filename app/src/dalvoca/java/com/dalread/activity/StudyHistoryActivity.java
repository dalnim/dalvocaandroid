package com.dalread.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BasePlayVocaActivity;
import com.dalread.util.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;

public class StudyHistoryActivity extends BasePlayVocaActivity {

    @BindView(R.id.nav_bottom) BottomNavigationView bottomNavigationView;
    private int currentBottomNavigationId;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_study_history;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBottomNavigation();
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
                        case R.id.nav_date:
                            Utils.loadFragment(StudyHistoryActivity.this, new StudyHistoryDateFragment(), getFragmentContainerId(), false);
                            currentBottomNavigationId = id;
                            toolbar.setTitle(R.string.study_history_date);
                            return true;
                        case R.id.nav_word:
                            Utils.loadFragment(StudyHistoryActivity.this, new StudyHistoryWordFragment(), getFragmentContainerId(), false);
                            currentBottomNavigationId = id;
                            toolbar.setTitle(R.string.study_history_word);
                            return true;
                        case R.id.nav_feedback:
                            Utils.loadFragment(StudyHistoryActivity.this, new StudyHistoryFeedbackFragment(), getFragmentContainerId(), false);
                            currentBottomNavigationId = id;
                            toolbar.setTitle(R.string.nav_title_feedback);
                            return true;
                    }
                }
                return false;
            }
        });
        if (getSupportFragmentManager().getFragments().isEmpty()) {
            bottomNavigationView.setSelectedItemId(R.id.nav_date);
        }
    }
}
