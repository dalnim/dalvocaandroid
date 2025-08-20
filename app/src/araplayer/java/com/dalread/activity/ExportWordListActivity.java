package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BasePlayerActivity;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityExportWordListBinding;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.CopyTextUtil;
import com.dalread.util.VocaKnow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExportWordListActivity extends BasePlayerActivity {

    private ActivityExportWordListBinding binding;
    private PlayerFileModel playerFileModel;
    
    // VocaKnow 선택 상태
    private boolean isKnownSelected = false;
    private boolean isGrade1Selected = false;
    private boolean isGrade2Selected = false;
    private boolean isUnknownSelected = false;
    
    // 단어 데이터
    private List<DicModel> allWords = new ArrayList<>();
    private List<DicModel> knownWords = new ArrayList<>();
    private List<DicModel> grade1Words = new ArrayList<>();
    private List<DicModel> grade2Words = new ArrayList<>();
    private List<DicModel> unknownWords = new ArrayList<>();

    public static Intent createIntent(Context context, PlayerFileModel playerFileModel) {
        Intent intent = new Intent(context, ExportWordListActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        return intent;
    }

    @Override
    protected View getContentView() {
        binding = ActivityExportWordListBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar();
        setupClickListeners();
    }

    @Override
    public void initView() {
        initData();
        if (playerFileModel == null) {
            DLog.e("ExportWordListActivity", "playerFileModel is null!");
            return;
        }

        createSubDatabase(playerFileModel);
        loadWordData();
        updateUI();
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        playerFileModel = intent.getParcelableExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE);
    }

    private void setupToolbar() {
        binding.header.setTitle(R.string.export_word_list);
        binding.header.setIconLeft(R.drawable.ic_back);
    }

    private void setupClickListeners() {
        // VocaKnow 버튼 클릭 리스너
        binding.llKnown.setOnClickListener(v -> toggleVocaKnowSelection(Constant.VOCA_KNOW.VOCA_KNOW_KNOWN));
        binding.llGrade1.setOnClickListener(v -> toggleVocaKnowSelection(Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1));
        binding.llGrade2.setOnClickListener(v -> toggleVocaKnowSelection(Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2));
        binding.llUnknown.setOnClickListener(v -> toggleVocaKnowSelection(Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN));
        
        // 내보내기 버튼 클릭 리스너
        binding.btnExport.setOnClickListener(v -> exportSelectedWords());
    }

    private void toggleVocaKnowSelection(int vocaKnowType) {
        switch (vocaKnowType) {
            case Constant.VOCA_KNOW.VOCA_KNOW_KNOWN:
                isKnownSelected = !isKnownSelected;
                break;
            case Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1:
                isGrade1Selected = !isGrade1Selected;
                break;
            case Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2:
                isGrade2Selected = !isGrade2Selected;
                break;
            case Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN:
                isUnknownSelected = !isUnknownSelected;
                break;
        }
        updateUI();
        updateWordList();
    }

    private void loadWordData() {
        if (playerFileModel == null) return;
        
        // 기존 WordListPlayerActivity와 동일한 방식으로 단어 데이터 로드
        allWords.clear();
        knownWords.clear();
        grade1Words.clear();
        grade2Words.clear();
        unknownWords.clear();
        
        // 데이터베이스에서 단어 가져오기 (임시로 더미 데이터 사용)
        // TODO: 실제 데이터베이스 연동 필요
        loadWordsFromDatabase();
        
        // VocaKnow별로 분류
        for (DicModel word : allWords) {
            if (VocaKnow.isKnown(word)) {
                knownWords.add(word);
            } else if (VocaKnow.isAmkiGrade1(word)) {
                grade1Words.add(word);
            } else if (VocaKnow.isAmkiGrade2(word)) {
                grade2Words.add(word);
            } else if (VocaKnow.isUnknown(word)) {
                unknownWords.add(word);
            }
        }
    }

    private void loadWordsFromDatabase() {
        if (getSubDatabase() == null) return;
        
        // WordListPlayerActivity와 동일한 방식으로 데이터베이스에서 단어 로드
        allWords.clear();
        allWords.addAll(getSubDatabase().getDics());
    }

    private void updateUI() {
        // 버튼 선택 상태에 따라 투명도와 테두리 변경
        updateButtonState(binding.llKnown, isKnownSelected, knownWords.size());
        updateButtonState(binding.llGrade1, isGrade1Selected, grade1Words.size());
        updateButtonState(binding.llGrade2, isGrade2Selected, grade2Words.size());
        updateButtonState(binding.llUnknown, isUnknownSelected, unknownWords.size());
    }

    private void updateButtonState(View button, boolean isSelected, int count) {
        // 투명도 설정
        button.setAlpha(isSelected ? 1.0f : 0.6f);
        
        // 테두리 설정
        if (isSelected) {
            button.setBackgroundTintList(null); // 원래 배경색 유지
            button.setBackgroundResource(R.drawable.btn_no_border_voca_know_known); // 임시로 known 배경 사용
            button.setBackgroundTintList(null);
            // 검은색 테두리 추가
            button.setPadding(button.getPaddingLeft() + 2, button.getPaddingTop() + 2, 
                           button.getPaddingRight() + 2, button.getPaddingBottom() + 2);
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.BLACK));
        } else {
            // 원래 상태로 복원
            button.setPadding(8, 8, 8, 8);
            // 각 버튼의 원래 배경색으로 복원
            if (button == binding.llKnown) {
                button.setBackgroundResource(R.drawable.btn_no_border_voca_know_known);
            } else if (button == binding.llGrade1) {
                button.setBackgroundResource(R.drawable.btn_no_border_voca_know_amki_grade_1);
            } else if (button == binding.llGrade2) {
                button.setBackgroundResource(R.drawable.btn_no_border_voca_know_amki_grade_2);
            } else if (button == binding.llUnknown) {
                button.setBackgroundResource(R.drawable.btn_no_border_voca_know_unknown);
            }
            button.setBackgroundTintList(null);
        }
        
        // 카운트 업데이트
        if (button == binding.llKnown) {
            binding.tvKnownCount.setText(String.valueOf(count));
        } else if (button == binding.llGrade1) {
            binding.tvGrade1Count.setText(String.valueOf(count));
        } else if (button == binding.llGrade2) {
            binding.tvGrade2Count.setText(String.valueOf(count));
        } else if (button == binding.llUnknown) {
            binding.tvUnknownCount.setText(String.valueOf(count));
        }
    }

    private void updateWordList() {
        // 선택된 VocaKnow에 따라 단어 필터링
        List<DicModel> selectedWords = new ArrayList<>();
        
        if (isKnownSelected) {
            selectedWords.addAll(knownWords);
        }
        if (isGrade1Selected) {
            selectedWords.addAll(grade1Words);
        }
        if (isGrade2Selected) {
            selectedWords.addAll(grade2Words);
        }
        if (isUnknownSelected) {
            selectedWords.addAll(unknownWords);
        }
        
        // 알파벳 순으로 정렬
        Collections.sort(selectedWords, (o1, o2) -> 
            o1.getVocaDisplay().toLowerCase().compareTo(o2.getVocaDisplay().toLowerCase())
        );
        
        // 단어 목록 텍스트 생성
        StringBuilder wordListText = new StringBuilder();
        for (DicModel word : selectedWords) {
            if (wordListText.length() > 0) {
                wordListText.append("\n");
            }
            wordListText.append(word.getVocaDisplay());
        }
        
        // 단어 목록과 단어 수 표시
        binding.tvWordList.setText(wordListText.toString());
        binding.tvWordCount.setText(" (" + selectedWords.size() + "개)");
    }

    private void exportSelectedWords() {
        // 선택된 VocaKnow에 따라 단어 필터링
        List<DicModel> selectedWords = new ArrayList<>();
        
        if (isKnownSelected) {
            selectedWords.addAll(knownWords);
        }
        if (isGrade1Selected) {
            selectedWords.addAll(grade1Words);
        }
        if (isGrade2Selected) {
            selectedWords.addAll(grade2Words);
        }
        if (isUnknownSelected) {
            selectedWords.addAll(unknownWords);
        }
        
        if (selectedWords.isEmpty()) {
            Toast.makeText(this, "선택된 단어가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 알파벳 순으로 정렬
        Collections.sort(selectedWords, (o1, o2) -> 
            o1.getVocaDisplay().toLowerCase().compareTo(o2.getVocaDisplay().toLowerCase())
        );
        
        // 단어 목록 텍스트 생성
        StringBuilder wordListText = new StringBuilder();
        for (DicModel word : selectedWords) {
            if (wordListText.length() > 0) {
                wordListText.append("\n");
            }
            wordListText.append(word.getVocaDisplay());
        }
        
        // CopyTextUtil을 사용하여 클립보드로 복사
        String toastText = CopyTextUtil.getMessageInToastToShow(this, wordListText.toString());
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }

    @Override
    protected void setFullscreen() {
        // BasePlayerActivity의 추상 메서드 구현
        // 이 액티비티에서는 전체화면 모드를 사용하지 않음
    }
}
