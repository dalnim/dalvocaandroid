package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import com.dalread.R;
import com.dalread.databinding.ConversationOptionBottomSheetDialogBinding;
import com.dalread.helper.ConversationHelper;
import com.dalread.model.WordListType;
import com.dalread.util.PointGptUtil;
import com.dalread.util.Utils;

public class ConversationOptionBottomSheetDialog extends BaseBottomSheetDialog<ConversationOptionBottomSheetDialogBinding> {
    private ConversationHelper.PracticeType practiceType = ConversationHelper.PracticeType.CHOOSE_WORD;
    private boolean isDisplayTranslation = true;
    private boolean isPlayStt = false;

    public ConversationOptionBottomSheetDialog(Context context, ConversationOptionBottomSheetDialogBinding binding, boolean isFirstOpen, WordListType wordListType) {
        super(context, binding);
        setContentView(binding.getRoot());
        getSharedPreferences();
        binding.tvPoint.setText(context.getString(R.string.conversation_option_sheet_text_view_point, PointGptUtil.getPoint(context), PointGptUtil.pointForConversation));

        binding.tbShowTranslation.setChecked(isDisplayTranslation);
        binding.tbPlayTts.setChecked(isPlayStt);
        initListener();
        hideOrShowUi(isFirstOpen, wordListType);
    }
    private void hideOrShowUi(boolean isFirstOpen, WordListType wordListType) {
        binding.rgPracticeType.setVisibility(isFirstOpen ? View.VISIBLE : View.GONE);

        int visibilityForQuiz = View.GONE;
        if (wordListType != WordListType.QUIZ_LIST) {
            visibilityForQuiz = View.VISIBLE;
        }
        binding.tbShowTranslation.setVisibility(visibilityForQuiz);
        binding.tvPoint.setVisibility(visibilityForQuiz);
        binding.rgPracticeType.setVisibility(visibilityForQuiz);
    }

    private void initListener() {
        binding.rgPracticeType.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rbSpeakingMode:
                    practiceType = ConversationHelper.PracticeType.SPEAKING;
                    break;
                case R.id.rbWordMatchingMode:
                    practiceType = ConversationHelper.PracticeType.CHOOSE_WORD;
                    break;
            }
            sharedPreferences.setConversationPracticeType(practiceType.name());
        });
        binding.tbShowTranslation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.setShowMeaning(isChecked);
        });
        binding.tbPlayTts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.setPlayTtsOnConversation(isChecked);
        });
    }
    private void getSharedPreferences() {
        String practiceTypeString = sharedPreferences.getConversationPracticeType();
        if (Utils.isEmpty(practiceTypeString))
            practiceTypeString = ConversationHelper.PracticeType.CHOOSE_WORD.name();
        practiceType = ConversationHelper.PracticeType.valueOf(practiceTypeString);
        switch (practiceType) {
            case SPEAKING:
                binding.rgPracticeType.check(R.id.rbSpeakingMode);
                break;
            case CHOOSE_WORD:
                binding.rgPracticeType.check(R.id.rbWordMatchingMode);
                break;
        }

        isDisplayTranslation = sharedPreferences.isShowMeaning();
        isPlayStt = sharedPreferences.isPlayTtsOnConversation();
    }
}
