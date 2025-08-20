package com.dalread.activity;

import static com.dalread.util.CopyTextUtil.copyToClipboard;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.base.BaseDialogListener;
import com.dalread.base.EnumType;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.ActivityMultiplayerWatchAdBinding;
import com.dalread.dialog.TypeInputDialog;
import com.dalread.util.PointUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.KeyboardUtil;
import com.dalread.util.rewardPoint.RewardCodeManager;

import java.util.List;

public class MultiPlayerWatchAdActivity extends AppCompatActivity {
    
    private ActivityMultiplayerWatchAdBinding binding;
    private PointUtil pointUtil;
    protected SharedPreferencesDB sharedPreferences;
    private RewardCodeManager rewardCodeManager;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMultiplayerWatchAdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Status bar와 시스템 버튼 바 색상을 홈 화면과 동일하게 설정
        setupSystemUI();
        
        initData();
        setClickListeners();
        setupToolbar();
    }
    
    private void setupSystemUI() {
        // Status bar 색상을 검은색으로 설정
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        
        // 시스템 버튼 바 색상을 검은색으로 설정 (API 21 이상)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(android.R.color.black));
        }
        
        // 시스템 UI 플래그 설정
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }
    
    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_get_points));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        
        // 제목을 중앙에 배치
        TextView titleTextView = new TextView(this);
        titleTextView.setText(getString(R.string.title_get_points));
        titleTextView.setTextColor(getResources().getColor(android.R.color.white));
        titleTextView.setTextSize(20);
        titleTextView.setTypeface(null, android.graphics.Typeface.BOLD);
        
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = android.view.Gravity.CENTER;
        titleTextView.setLayoutParams(layoutParams);
        
        binding.toolbar.addView(titleTextView);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    private void initData() {
        pointUtil = new PointUtil(this);
        sharedPreferences = SharedPreferencesDB.getInstance(getApplicationContext());
        rewardCodeManager = new RewardCodeManager();
        refreshRemainPoint();
        
        // URL 텍스트에 밑줄 효과 추가
        binding.tvFreeRewardCodeUrl.setPaintFlags(binding.tvFreeRewardCodeUrl.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        
        // 디버그 모드일 때만 개발자용 기능들 표시
        if (Utils.isDebug()) {
            binding.btnGenerateRewardCode.setVisibility(View.VISIBLE);
            binding.tvGenerateRewardCode.setVisibility(View.VISIBLE);
        } else {
            binding.btnGenerateRewardCode.setVisibility(View.INVISIBLE);
            binding.tvGenerateRewardCode.setVisibility(View.INVISIBLE);
        }
    }
    
    private void setClickListeners() {
        binding.btnWatchRewardedAd.setOnClickListener(v -> watchRewardedAd());
        
        binding.btnRewardCodeInput.setOnClickListener(v -> showRewardCodeInput());

        // 무료 리워드 코드 받기 텍스트 클릭 리스너
        binding.tvFreeRewardCodeUrl.setOnClickListener(v -> openFreeRewardCodeUrl());

        // 새로 추가된 버튼의 클릭 리스너
        binding.btnGenerateRewardCode.setOnClickListener(v -> {
            List<RewardCodeManager.RewardCodeInfo> validCodesWithInfo = rewardCodeManager.getValidCodesWithInfo();
            if (validCodesWithInfo != null && !validCodesWithInfo.isEmpty()) {
                RewardCodeManager.RewardCodeInfo codeInfo = validCodesWithInfo.get(0);
                String code = codeInfo.getCode();
                String validDate = codeInfo.getFormattedValidDate();
                
                // 클립보드에 복사
                copyToClipboard(MultiPlayerWatchAdActivity.this, code);
                
                // TextView에 코드와 유효 날짜 표시
                String displayText = getString(R.string.format_generated_code, code, validDate);
                binding.tvGenerateRewardCode.setText(displayText);
                
                // 토스트 메시지 표시
                ToastUtil.getInstance(this).show(getString(R.string.toast_code_copied_to_clipboard, code));
            } else {
                ToastUtil.getInstance(this).show(getString(R.string.toast_code_generation_failed));
            }
        });
    }
    
    private void refreshRemainPoint() {
        int point = pointUtil.getPoint();
        binding.tvRemainPoint.setText(getResources().getQuantityString(R.plurals.point, point, point));
    }
    
    private void playPointAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.text_scale_anim);
        binding.tvRemainPoint.startAnimation(animation);
    }
    
    private void openFreeRewardCodeUrl() {
        try {
            // Google 웹사이트 URL (나중에 수정 가능)
            String url = "https://www.google.com";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            ToastUtil.getInstance(this).show(getString(R.string.toast_browser_open_failed));
        }
    }
    
    private void watchRewardedAd() {
        // 기존 홈 화면과 동일한 광고 보기 로직
        binding.btnWatchRewardedAd.setVisibility(View.INVISIBLE);
        pointUtil.showRewardedAd(createRewardPointListener());
    }
    
    private PointUtil.OnRewardPointListener createRewardPointListener() {
        return new PointUtil.OnRewardPointListener() {
            @Override
            public void onSuccess() {
                refreshRemainPoint();
            }

            @Override
            public void onContinue() {}

            @Override
            public void onCancel() {}

            @Override
            public void onFail() {}
        };
    }
    
    private void showRewardCodeInput() {
        BaseDialogListener listener = new BaseDialogListener() {
            @Override
            public void onBaseDialogListenerShow(EnumType type, BaseDialog dialog, View v, int position, Object data) {

            }

            @Override
            public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object data) {
                if (data instanceof String) {
                    String code = (String) data;
                    RewardCodeManager.RewardCodeValidationResult result = rewardCodeManager.validateUserCode(MultiPlayerWatchAdActivity.this, code);
                    
                    switch (result) {
                        case VALID:
                            pointUtil.addPoint(3);
                            rewardCodeManager.saveUsedCode(MultiPlayerWatchAdActivity.this, code);
                            refreshRemainPoint();
        
                            // 포인트 애니메이션 2번 실행
                            playPointAnimation();
        
                            ToastUtil.getInstance(MultiPlayerWatchAdActivity.this).show(getString(R.string.toast_points_earned));
                            break;
                        case ALREADY_USED:
                            ToastUtil.getInstance(MultiPlayerWatchAdActivity.this).show(getString(R.string.toast_code_already_used));
                            break;
                        case INVALID:
                            ToastUtil.getInstance(MultiPlayerWatchAdActivity.this).show(getString(R.string.toast_code_invalid));
                            break;
                    }
                    KeyboardUtil.hideSoftKeyboard(MultiPlayerWatchAdActivity.this); // 키보드 숨기기
                    dialog.dismiss();
                }
            }

            @Override
            public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {
                KeyboardUtil.hideSoftKeyboard(MultiPlayerWatchAdActivity.this); // 키보드 숨기기
            }

            @Override
            public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {

            }

            @Override
            public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {

            }
        };

        TypeInputDialog inputDialog = new TypeInputDialog(this, listener);
        inputDialog.setTitle(getString(R.string.dialog_title_reward_code_input));
        inputDialog.setSubTitle(getString(R.string.dialog_subtitle_reward_code_input));
        inputDialog.setHint(R.string.hint_enter_a_message);
        inputDialog.show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 광고 시청 후 돌아왔을 때 포인트 애니메이션 2번 실행
        if (sharedPreferences.isPointAdded()) {
            playPointAnimation();
            sharedPreferences.setPointAdded(false);
        }
        refreshRemainPoint();
    }
}