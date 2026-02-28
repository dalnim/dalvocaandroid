package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
 * "무료 앱들" / "앱 공유" 화면. 플러터 AraShareAppScreen과 동일:
 * showShareSection=true 시 제목 "앱 공유", 상단에 현재 앱 QR 카드 + 공유 버튼, 하단 무료 앱 목록. 한 번에 스크롤.
 * showShareSection=false 시 제목 "무료 앱들", 무료 앱 목록만.
 */
public class FreeAppsActivity extends BaseActivity {

    private static final String EXTRA_CURRENT_APP_ID = "current_app_id";
    private static final String EXTRA_SELECTED_APP_ID = "selected_app_id";
    private static final String EXTRA_SHOW_SHARE_SECTION = "show_share_section";

    public static Intent createIntent(Context context, int currentAppId, int selectedAppId) {
        return createIntent(context, currentAppId, selectedAppId, false);
    }

    public static Intent createIntent(Context context, int currentAppId, int selectedAppId, boolean showShareSection) {
        Intent i = new Intent(context, FreeAppsActivity.class);
        i.putExtra(EXTRA_CURRENT_APP_ID, currentAppId);
        i.putExtra(EXTRA_SELECTED_APP_ID, selectedAppId);
        i.putExtra(EXTRA_SHOW_SHARE_SECTION, showShareSection);
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
        boolean showShareSection = getIntent().getBooleanExtra(EXTRA_SHOW_SHARE_SECTION, false);

        if (showShareSection) {
            binding.header.setTitle(getString(R.string.share_app_screen_title));
        } else {
            binding.header.setTitle(getString(R.string.share_app_other_apps));
        }

        List<OtherAppsInfo.Entry> list = OtherAppsInfo.getOtherApps(currentAppId);
        adapter = new FreeAppCardAdapter(this, list, showShareSection, headerView -> {
            View sectionShareApp = headerView.findViewById(R.id.sectionShareApp);
            TextView tvShareAppName = headerView.findViewById(R.id.tvShareAppName);
            Button btnShareApp = headerView.findViewById(R.id.btnShareApp);
            if (showShareSection && sectionShareApp != null) {
                sectionShareApp.setVisibility(View.VISIBLE);
                OtherAppsInfo.Entry currentEntry = OtherAppsInfo.getEntry(currentAppId);
                if (currentEntry != null && tvShareAppName != null) {
                    tvShareAppName.setText(currentEntry.nameResId);
                }
                if (btnShareApp != null) {
                    btnShareApp.setOnClickListener(v -> {
                        OtherAppsInfo.Entry entry = OtherAppsInfo.getEntry(currentAppId);
                        if (entry == null) return;
                        String appName = getString(entry.nameResId);
                        String iosUrl = entry.appStoreUrl != null ? entry.appStoreUrl : "";
                        String androidUrl = entry.playStoreUrl != null ? entry.playStoreUrl : "";
                        StringBuilder sb = new StringBuilder();
                        sb.append(appName).append("\n");
                        if (!iosUrl.isEmpty()) {
                            sb.append(getString(R.string.share_app_iphone)).append(": ").append(iosUrl).append("\n");
                        }
                        if (!androidUrl.isEmpty()) {
                            sb.append(getString(R.string.share_app_android)).append(": ").append(androidUrl);
                        }
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString().trim());
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, null));
                    });
                }
            } else if (sectionShareApp != null) {
                sectionShareApp.setVisibility(View.GONE);
            }
        });
        binding.rvCards.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCards.setAdapter(adapter);

        int selectedPosition = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).appId == selectedAppId) {
                selectedPosition = i;
                break;
            }
        }
        adapter.setSelectedPosition(selectedPosition);
        // 앱 공유로 진입 시 맨 위(QR 섹션)가 보이도록 0으로, 무료 앱 목록에서 진입 시 선택한 카드로 스크롤
        final int scrollToAdapterPosition = showShareSection ? 0 : (selectedPosition + 1);
        binding.rvCards.post(() -> binding.rvCards.smoothScrollToPosition(scrollToAdapterPosition));
    }

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }
}
