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
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.databinding.ActivityExportWordListBinding;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.VocaKnow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        // VocaKnow 버튼 상태 업데이트
        updateButtonState(binding.llKnown, isKnownSelected, knownWords.size());
        updateButtonState(binding.llGrade1, isGrade1Selected, grade1Words.size());
        updateButtonState(binding.llGrade2, isGrade2Selected, grade2Words.size());
        updateButtonState(binding.llUnknown, isUnknownSelected, unknownWords.size());
        
        // 카운트 업데이트
        binding.tvKnownCount.setText(String.valueOf(knownWords.size()));
        binding.tvGrade1Count.setText(String.valueOf(grade1Words.size()));
        binding.tvGrade2Count.setText(String.valueOf(grade2Words.size()));
        binding.tvUnknownCount.setText(String.valueOf(unknownWords.size()));
    }

    private void updateButtonState(View button, boolean isSelected, int count) {
        if (isSelected) {
            button.setAlpha(1.0f);
        } else {
            button.setAlpha(0.6f);
        }
    }

    private void updateWordList() {
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
        
        // 알파벳순 정렬
        Collections.sort(selectedWords, new Comparator<DicModel>() {
            @Override
            public int compare(DicModel o1, DicModel o2) {
                return o1.getVocaDisplay().compareToIgnoreCase(o2.getVocaDisplay());
            }
        });
        
        // 단어 목록 텍스트 생성
        StringBuilder wordListText = new StringBuilder();
        if (selectedWords.isEmpty()) {
            wordListText.append("VocaKnow를 선택하면 해당하는 단어들이 여기에 표시됩니다.");
        } else {
            for (DicModel word : selectedWords) {
                wordListText.append(word.getVocaDisplay()).append("\n");
            }
        }
        
        binding.tvWordList.setText(wordListText.toString());
    }

    private void exportSelectedWords() {
        List<DicModel> selectedWords = new ArrayList<>();
        
        if (isKnownSelected) selectedWords.addAll(knownWords);
        if (isGrade1Selected) selectedWords.addAll(grade1Words);
        if (isGrade2Selected) selectedWords.addAll(grade2Words);
        if (isUnknownSelected) selectedWords.addAll(unknownWords);
        
        if (selectedWords.isEmpty()) {
            Toast.makeText(this, "내보낼 단어를 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // TODO: 실제 내보내기 로직 구현
        Toast.makeText(this, selectedWords.size() + "개 단어를 내보냅니다.", Toast.LENGTH_SHORT).show();
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
