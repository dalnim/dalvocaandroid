package com.dalread.activity;

import android.os.Bundle;
import android.view.View;

import com.dalread.R;
import com.dalread.base.BaseSettingsActivity;
import com.dalread.helper.ExecutorHelper;
import com.dalread.network.GptUserOption;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraConvUtil;
import com.dalread.util.LanguageUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.UpdateWordLevelTask;
import com.dalread.util.UserUtil;

import org.greenrobot.eventbus.Subscribe;

import butterknife.OnClick;

public class SettingsActivity extends BaseSettingsActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hidePreferredNativeSpeakersCountArrow();
    }

    @Override
    protected void init() {
        super.init();
        updateLangLevelText();
        displayPronunciation = sharedPreferences.getDisplayPronunciation(this);
        binding.scDisplayPronunciation.setChecked(displayPronunciation);
    }

    private void hidePreferredNativeSpeakersCountArrow() {
        binding.ivPreferredNativeSpeakersCountArrow.setVisibility(View.INVISIBLE);
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        switch (successEvent.getScreen()) {
            case MAIN:
                switch (successEvent.getEventType()) {
                    case MOTHER_TONGUE_LANGUAGE_CHANGED:
                        hideMenus();
                        break;
                }
                break;
            case SETTING:
                if (successEvent.getEventType() == BaseEvent.EventType.LANGUAGE_LEVEL_CHANGED) {
                    ExecutorHelper executorHelper = new ExecutorHelper();
                    Runnable task = new UpdateWordLevelTask(this, () -> {
                        ToastUtil.getInstance(SettingsActivity.this).show(R.string.toast_word_level_updated);
                    });
                    executorHelper.executeTask(task);
                    updateLangLevelText();
                }
                break;
        }
    }

    @OnClick({
            R.id.llLangLevel})
    protected void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        switch (id) {
            case R.id.llLangLevel:
                GptUserOption gptOptions = GptUserOption.getGptOptionsByLevel(sharedPreferences.getSettingMyLanguageLevel());
                AraConvUtil.openLanguageLevelDialog(this, gptOptions);
                break;
        }
    }
    @Override
    protected void showGptWebMenu() {
        openNewScreen(GptWebMenuActivity.class, true);
    }
    private void updateLangLevelText() {
        String level = UserUtil.getLangLevel(context, sharedPreferences.getSettingMyLanguageLevel());
        binding.tvLangLevel.setText(level);
    }
    @Override
    protected void hideMenus() {
        super.hideMenus();
        if (AppFlavorUtil.isAraConvApp())
            binding.llPlayMotherTongueOnlyFirstRoundFinishOnConversation.setVisibility(View.VISIBLE);

        binding.llStudyLang.setVisibility(View.GONE);
        binding.llAutoPlay.setVisibility(View.GONE);
        binding.llAllowChat.setVisibility(View.GONE);
        binding.llCountOfNotRatedWordBeforePlaying.setVisibility(View.GONE);
        if (LanguageUtil.isStudyLangKorean(context)) {
            binding.llDisplayPronunciation.setVisibility(View.VISIBLE);
            if (AppFlavorUtil.isAraHangulApp()) {
//                binding.llDisplayPronunciation.setVisibility(View.GONE);
                binding.llKeepDisplayingBackgroundHintWhenWriting.setVisibility(View.VISIBLE);
            }
        } else if (LanguageUtil.isStudyLangChinese(context)) {
            binding.llDisplayPronunciation.setVisibility(View.VISIBLE);
        } else if (LanguageUtil.isStudyLangJapanese(context)) {
            binding.llDisplayPronunciation.setVisibility(View.VISIBLE);
        } else {
            binding.llDisplayPronunciation.setVisibility(View.GONE);
        }
        binding.llIncludeMotherTongueSubtitle.setVisibility(View.GONE);
//        binding.llWebDictionary.setVisibility(View.GONE);
        binding.llPlayVideoAutomaticallyWhenOpenIt.setVisibility(View.GONE);
        binding.llPlayVideoFromWhereYouLeft.setVisibility(View.GONE);
        binding.llHide4ButtonsOnPlayingScreen.setVisibility(View.GONE);
        binding.llShowVideoInCameraFolder.setVisibility(View.GONE);
        binding.llAllowChat.setVisibility(View.GONE);
        binding.llShareMyRecordings.setVisibility(View.GONE);
        binding.llTranslateSubtitleFromServer.setVisibility(View.GONE);
        binding.llSyncSubtitlesAtServer.setVisibility(View.GONE);
        binding.llTableHeaderQuiz.setVisibility(View.GONE);
        binding.llMaxQuiz.setVisibility(View.GONE);
        binding.llHanjaFontSize.setVisibility(View.GONE);
        binding.llPlayDifficultWordsBeforePlayingSubtitles.setVisibility(View.GONE);
        binding.llIncludeMeaning.setVisibility(View.GONE);
        binding.llReadCountValueToPlayTts.setVisibility(View.GONE); //Don't need this. In AraConv When I click the Play button I can see the count there.
        binding.llPreferredNativeSpeakersCount.setVisibility(View.GONE);

        binding.llWebDictionary.setVisibility(View.GONE);
        if (LanguageUtil.isStudyLangEnglish(this)) {
            binding.llDisplayEnglishMeaningToo.setVisibility(View.GONE);
            sharedPreferences.setDisplayEnglishMeaningToo(false);
        } else {
            if (LanguageUtil.isMotherTongueLangEnglish(this)) {
                binding.llDisplayEnglishMeaningToo.setVisibility(View.GONE);
                sharedPreferences.setDisplayEnglishMeaningToo(false);
            } else {
                binding.llDisplayEnglishMeaningToo.setVisibility(View.VISIBLE);
            }
        }

        if (AppFlavorUtil.isAraHangulApp()) {
            binding.llPauseTimeToRepeatTts.setVisibility(View.GONE);
        }

        binding.llChooseGpt.setVisibility(View.VISIBLE);
    }

}