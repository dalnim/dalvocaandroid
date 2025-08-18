package com.dalread.activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.util.PointUtil;

public class MultiPlayerWatchAdActivity extends AppCompatActivity {
    
    private TextView tvRemainPoint;
    private Button btnWatchRewardedAd;
    private Button btnRewardCode;
    private PointUtil pointUtil;
    protected SharedPreferencesDB sharedPreferences;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_watch_ad);
        
        initViews();
        initData();
        setClickListeners();
    }
    
    private void initViews() {
        tvRemainPoint = findViewById(R.id.tvRemainPoint);
        btnWatchRewardedAd = findViewById(R.id.btnWatchRewardedAd);
        btnRewardCode = findViewById(R.id.btnRewardCode);
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
        if (sharedPreferences.isPointAdded()) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.text_scale_anim);
            tvRemainPoint.startAnimation(animation);
            sharedPreferences.setPointAdded(false);
        }
        tvRemainPoint.setText(getResources().getQuantityString(R.plurals.point, point, point));
    }
    
    private void watchRewardedAd() {
        // 기존 홈 화면과 동일한 광고 보기 로직
        btnWatchRewardedAd.setVisibility(View.INVISIBLE);
        pointUtil.showRewardedAd(new PointUtil.OnRewardPointListener() {
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
        });
    }
    
    private void showRewardCodeInput() {
        // 리워드 코드 입력 다이얼로그 또는 액티비티 구현
        // 임시로 토스트 메시지만 표시
        android.widget.Toast.makeText(this, "Reward Code Input", android.widget.Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        refreshRemainPoint();
    }
}
