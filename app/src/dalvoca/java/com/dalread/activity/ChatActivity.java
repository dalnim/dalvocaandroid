package com.dalread.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BasePlayVocaActivity;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;

public class ChatActivity extends BasePlayVocaActivity {

    @BindView(R.id.nav_bottom) BottomNavigationView bottomNavigationView;
    private int currentBottomNavigationId;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_chat;
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
        ToastUtil.getInstance(this).show(R.string.msg_under_development);
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
                        case R.id.nav_chat:
                            Utils.loadFragment(
                                    ChatActivity.this,
                                    new ChatRoomFragment(),
                                    getFragmentContainerId(),
                                    false
                            );
                            currentBottomNavigationId = id;
                            toolbar.setTitle(R.string.chat);
                            toolbar.setIconRight(R.drawable.ic_add_black_24dp);
                            return true;
                        case R.id.nav_people:
                            Utils.loadFragment(
                                    ChatActivity.this,
                                    new PeopleFragment(),
                                    getFragmentContainerId(),
                                    false
                            );
                            currentBottomNavigationId = id;
                            toolbar.setTitle(R.string.people);
                            toolbar.setIconRight(R.drawable.ic_more_vert_white_24dp);
                            return true;
                    }
                }
                return false;
            }
        });
        if (getSupportFragmentManager().getFragments().isEmpty()) {
            bottomNavigationView.setSelectedItemId(R.id.nav_chat);
        }
    }
}
