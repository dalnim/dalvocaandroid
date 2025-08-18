package com.dalread.activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.util.PointUtil;

public class MultiPlayerWatchAdActivity extends AppCompatActivity {
    
    private TextView tvRemainPoint;
    private Button btnWatchRewardedAd;
    private Button btnRewardCode;
    private PointUtil pointUtil;
    protected SharedPreferencesDB sharedPreferences;
    private Toolbar toolbar;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_watch_ad);
        
        // Status bar와 시스템 버튼 바 색상을 홈 화면과 동일하게 설정
        setupSystemUI();
        
        initViews();
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
    
    private void initViews() {
        tvRemainPoint = findViewById(R.id.tvRemainPoint);
        btnWatchRewardedAd = findViewById(R.id.btnWatchRewardedAd);
        btnRewardCode = findViewById(R.id.btnRewardCode);
        toolbar = findViewById(R.id.toolbar);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("포인트 얻기");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        
        // 제목을 중앙에 배치
        TextView titleTextView = new TextView(this);
        titleTextView.setText("포인트 얻기");
        titleTextView.setTextColor(getResources().getColor(android.R.color.white));
        titleTextView.setTextSize(20);
        titleTextView.setTypeface(null, android.graphics.Typeface.BOLD);
        
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = android.view.Gravity.CENTER;
        titleTextView.setLayoutParams(layoutParams);
        
        toolbar.addView(titleTextView);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    private void initData() {
        pointUtil = new PointUtil(this);
        sharedPreferences = SharedPreferencesDB.getInstance(getApplicationContext());
        refreshRemainPoint();
        int i = 0;
    }
    
    private void setClickListeners() {
        btnWatchRewardedAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 기존 홈 화면과 동일한 광고 보기 로직
                watchRewardedAd();
            }
        });
        
        btnRewardCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 리워드 코드 입력 로직 구현
                showRewardCodeInput();
            }
        });
    }
    
    private void refreshRemainPoint() {
        int point = pointUtil.getPoint();
        tvRemainPoint.setText(getResources().getQuantityString(R.plurals.point, point, point));
    }
    
    private void watchRewardedAd() {
        // 기존 홈 화면과 동일한 광고 보기 로직
        btnWatchRewardedAd.setVisibility(View.INVISIBLE);
        pointUtil.showRewardedAd(createRewardPointListener());
    }
    
    private PointUtil.OnRewardPointListener createRewardPointListener() {
        return new PointUtil.OnRewardPointListener() {
            @Override
            public void onSuccess() {
                refreshRemainPoint();
            }

            @Override
            public void onContinue() {
                // 구현 필요
            }

            @Override
            public void onCancel() {
                // 구현 필요
            }

            @Override
            public void onFail() {
                // 구현 필요
            }
        };
    }
    
    private void showRewardCodeInput() {
        // 리워드 코드 입력 다이얼로그 또는 액티비티 구현
        // 임시로 토스트 메시지만 표시
        android.widget.Toast.makeText(this, "Reward Code Input", android.widget.Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 광고 시청 후 돌아왔을 때 포인트 애니메이션 실행
        if (sharedPreferences.isPointAdded()) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.text_scale_anim);
            tvRemainPoint.startAnimation(animation);
            sharedPreferences.setPointAdded(false);
        }
        refreshRemainPoint();
    }
}
