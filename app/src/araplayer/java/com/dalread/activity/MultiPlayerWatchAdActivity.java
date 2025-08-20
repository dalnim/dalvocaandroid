package com.dalread.activity;

import static com.dalread.util.CopyTextUtil.copyToClipboard;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.base.BaseDialogListener;
import com.dalread.base.EnumType;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.dialog.TypeInputDialog;
import com.dalread.util.PointUtil;
import com.dalread.util.rewardPoint.RewardCodeManager;

import java.util.List;

public class MultiPlayerWatchAdActivity extends AppCompatActivity {
    
    private TextView tvRemainPoint;
    private Button btnWatchRewardedAd;
    private Button btnRewardCode;
    private Button btnGenerateRewardCode; // 새로 추가된 버튼
    private PointUtil pointUtil;
    protected SharedPreferencesDB sharedPreferences;
    private Toolbar toolbar;
    private RewardCodeManager rewardCodeManager;
    
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
        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.black));
        
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
        btnGenerateRewardCode = findViewById(R.id.btn_generate_reward_code); // 새로 추가된 버튼 초기화
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
        rewardCodeManager = new RewardCodeManager();
        refreshRemainPoint();
    }
    
    private void setClickListeners() {
        btnWatchRewardedAd.setOnClickListener(v -> watchRewardedAd());
        
        btnRewardCode.setOnClickListener(v -> showRewardCodeInput());

        // 새로 추가된 버튼의 클릭 리스너
        btnGenerateRewardCode.setOnClickListener(v -> {
            List<String> validCodes = rewardCodeManager.getValidCodes();
            if (validCodes != null && !validCodes.isEmpty()) {
                String code = validCodes.get(0);
                copyToClipboard(MultiPlayerWatchAdActivity.this, code);
                Toast.makeText(MultiPlayerWatchAdActivity.this, "생성된 코드: " + code, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MultiPlayerWatchAdActivity.this, "코드를 생성할 수 없습니다.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MultiPlayerWatchAdActivity.this, "+3 포인트 획득!", Toast.LENGTH_SHORT).show();
                            break;
                        case ALREADY_USED:
                            Toast.makeText(MultiPlayerWatchAdActivity.this, "이미 사용된 코드입니다.", Toast.LENGTH_SHORT).show();
                            break;
                        case INVALID:
                            Toast.makeText(MultiPlayerWatchAdActivity.this, "유효하지 않은 코드입니다.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    dialog.dismiss();
                }
            }

            @Override
            public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {
                dialog.dismiss();
            }

            @Override
            public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {

            }

            @Override
            public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {

            }
        };

        TypeInputDialog inputDialog = new TypeInputDialog(this, listener);
        inputDialog.setTitle("리워드 코드 입력");
        inputDialog.setHint(R.string.hint_enter_a_message);
        inputDialog.show();
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