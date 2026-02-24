package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dalread.R;
import com.dalread.adapter.FreeAppCardAdapter;
import com.dalread.base.BaseActivity;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityFreeAppsBinding;
import com.dalread.util.OtherAppsInfo;

import java.util.List;

/**
 * "무료 앱들" 화면. 플러터 AraShareAppScreen(showOnlyOtherApps)과 동일:
 * 다른 앱 카드 목록, 선택한 카드에 테두리, 앱 설명 + App Store / Google Play 버튼.
 */
public class FreeAppsActivity extends BaseActivity {

    private static final String EXTRA_CURRENT_APP_ID = "current_app_id";
    private static final String EXTRA_SELECTED_APP_ID = "selected_app_id";

    public static Intent createIntent(Context context, int currentAppId, int selectedAppId) {
        Intent i = new Intent(context, FreeAppsActivity.class);
        i.putExtra(EXTRA_CURRENT_APP_ID, currentAppId);
        i.putExtra(EXTRA_SELECTED_APP_ID, selectedAppId);
        return i;
    }

    private ActivityFreeAppsBinding binding;
    private FreeAppCardAdapter adapter;

    @Override
    protected View getContentView() {
        binding = ActivityFreeAppsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int currentAppId = getIntent().getIntExtra(EXTRA_CURRENT_APP_ID, 4);
        int selectedAppId = getIntent().getIntExtra(EXTRA_SELECTED_APP_ID, -1);

        List<OtherAppsInfo.Entry> list = OtherAppsInfo.getOtherApps(currentAppId);
        adapter = new FreeAppCardAdapter(this, list);
        binding.rvCards.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCards.setAdapter(adapter);

        int selectedPosition = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).appId == selectedAppId) {
                selectedPosition = i;
                break;
            }
        }
        final int scrollToPosition = selectedPosition;
        adapter.setSelectedPosition(selectedPosition);
        binding.rvCards.post(() -> binding.rvCards.smoothScrollToPosition(scrollToPosition));
    }

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }
}
