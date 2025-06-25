package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.databinding.ActivitySentenceDiffWordsBinding;
import com.dalread.helper.AdjustTopSectionHeightHandler;
import com.dalread.helper.ExecutorHelper;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.BaseVocaList;
import com.dalread.util.Constant;
import com.dalread.util.KeyboardUtil;
import com.dalread.util.Loading;
import com.dalread.util.StringUtils;
import com.dalread.util.TranslateUtil;
import com.dalread.util.Utils;
import com.dalread.util.VocaListUtil;

import java.util.ArrayList;
import java.util.List;

public class SentenceDiffWordsActivity extends BaseConvActivity {
    private String sentence;
    private AdjustTopSectionHeightHandler separatorBarHandler;
    private ActivitySentenceDiffWordsBinding binding;
    private List<IVocaFullPlayTTSItem> difficultWordList;
    private List<IVocaFullPlayTTSItem> allWordList;

    public static Intent createIntent(Context context, String sentence) {
        Intent intent = new Intent(context, SentenceDiffWordsActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_EVENT_DATA, sentence);
        return intent;
    }
    protected View getContentView() {
        binding = ActivitySentenceDiffWordsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected int getContentViewId() {
        return 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initLayout() {
        super.initLayout();
        separatorBarHandler = new AdjustTopSectionHeightHandler(binding.vTopSection, binding.vAdjustHeightSection.vItem);

        initIvWordList();
        initIvRefresh();
        initIvClear();
        initIvTranslate();
        binding.tvTop.addTextChangedListener(textWatcher);
    }


    private void initIvWordList() {
        binding.vAdjustHeightSection.ivWordList.setOnClickListener( v -> {
            Intent intent = VocaListActivity.createIntentByVocaTypeId(SentenceDiffWordsActivity.this, BaseVocaList.convertToVocaTypeIdList(allWordList));
            vocaListActivityLauncher.launch(intent);
        });
    }
    private void initIvRefresh() {
        binding.vAdjustHeightSection.ivRefresh.setOnClickListener( v -> {
//            sentence = binding.tvTop.getText().toString();
            getDifficultWords(true);
        });
    }
    private void initIvClear() {
        binding.vAdjustHeightSection.ivClear.setOnClickListener( v -> {
            sentence = "";
            binding.tvTop.setText(sentence);
            getDifficultWords(false);
        });
    }

    private void initIvTranslate() {
        binding.vAdjustHeightSection.ivTranslate.setOnClickListener(view -> {
            TranslateUtil.openWebTranslate(context, sentence, () -> {});
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int visibility = View.VISIBLE;
            if (s.toString().isEmpty()) {
                visibility = View.INVISIBLE;
            }
            binding.vAdjustHeightSection.ivRefresh.setVisibility(visibility);
            binding.vAdjustHeightSection.ivClear.setVisibility(visibility);
            binding.vAdjustHeightSection.ivTranslate.setVisibility(visibility);
            binding.tvBottom.setVisibility(visibility);
            sentence = s.toString();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    @Override
    protected void initData() {
        sentence = getIntent().getStringExtra(Constant.BUNDLE.KEY_EVENT_DATA);
        allWordList = new ArrayList<>();
        difficultWordList = new ArrayList<>();
        getDifficultWords(false);
        binding.tvTop.setText(sentence);

    }

    private void getDifficultWords(boolean isDisplayFinishToast) {
        KeyboardUtil.hideSoftKeyboard(context, binding.tvTop);
        ExecutorHelper executorHelper = new ExecutorHelper();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(() -> Loading.showDelay(SentenceDiffWordsActivity.this));
                //ConvVocaListActivity에서 아는 단어를 바꾸면 DB에서 그냥 전부 다 다시 읽어오자. 시간 많이 걸리면 나중에 event를 날려서 바뀐것 데이타만 업데이트 하도록하자
                allWordList = VocaListUtil.getAllWordListOfFromDB(sentence, subDatabase);
                difficultWordList = BaseVocaList.getDifficultVocaListFromList(allWordList);
                //메모리 릭이 나서 이렇게 했다.
                runOnUiThread(() -> {
                    displayDiffVocas();
                    binding.tvBottom.requestLayout();//이게 없으면 oneLineDifficultWordAndMeaning부분만 업데이트 되고, 뷰 높이를 조절하면 전부 다 보인다. main쓰레드에서 불러야 하는데, Runnable자체가 main쓰레드이기 때문에 그냥 부르면 된다.
                    setColorOnDiffVocasOnSentence();
                    Loading.hide();
                });
                if (isDisplayFinishToast) {
//                    ToastUtil.getInstance(SentenceDiffWordsActivity.this).show("모르는 단어 찾기 완료");
                }
            }

            private void displayDiffVocas() {
                if (Utils.isEmpty(sentence)) {
                    binding.tvBottom.setText("");
                    binding.vAdjustHeightSection.ivWordList.setVisibility(View.INVISIBLE);
                } else {
                    String oneLineDifficultWordAndMeaning = BaseVocaKnow.getOneLineDifficultWordAndMeaning(context, difficultWordList);
                    if (Utils.isEmpty(oneLineDifficultWordAndMeaning)) {
                        binding.tvBottom.setText(R.string.all_known_vocas_in_sentence);
                    } else {
                        binding.tvBottom.setText(oneLineDifficultWordAndMeaning);
                    }
                    binding.vAdjustHeightSection.ivWordList.setVisibility(View.VISIBLE);
                }
            }
            private void setColorOnDiffVocasOnSentence() {
                binding.tvTop.setText(StringUtils.getSpanDefaultColorOnKeyword(sentence, BaseVocaList.getVocaStringList(difficultWordList)));
            }
        };
        executorHelper.executeTask(task);
    }

    private final ActivityResultLauncher<Intent> vocaListActivityLauncher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    if (data.getBooleanExtra(Constant.INTENT.KEY_DATA, false)) {
                        //해당 쉘의 단어를 다시 한다.
                        if (!allWordList.isEmpty()) {
                            getDifficultWords(false);
                        }
                    }
                }
            }
        });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Loading.hide();
    }
}