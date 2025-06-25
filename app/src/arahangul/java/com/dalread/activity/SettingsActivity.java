package com.dalread.activity;

import android.os.Bundle;
import android.view.View;

import com.dalread.base.BaseSettingsActivity;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.LanguageUtil;

import org.greenrobot.eventbus.Subscribe;

public class SettingsActivity extends BaseSettingsActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hidePreferredNativeSpeakersCountArrow();
    }

    private void hidePreferredNativeSpeakersCountArrow() {
        binding.ivPreferredNativeSpeakersCountArrow.setVisibility(View.INVISIBLE);
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
            switch (successEvent.getEventType()) {
                case MOTHER_TONGUE_LANGUAGE_CHANGED:
                    hideMenus();
                    break;
            }
        }
    }

    @Override
    protected void hideMenus() {
        super.hideMenus();
        binding.llStudyLang.setVisibility(View.GONE);
        binding.llAutoPlay.setVisibility(View.GONE);
        binding.llAllowChat.setVisibility(View.GONE);
        binding.llCountOfNotRatedWordBeforePlaying.setVisibility(View.GONE);
        if (LanguageUtil.isStudyLangKorean(context)) {
            binding.llDisplayPronunciation.setVisibility(View.VISIBLE);
        } else {
            binding.llDisplayPronunciation.setVisibility(View.GONE);
        }
        binding.llIncludeMotherTongueSubtitle.setVisibility(View.GONE);
        binding.llWebDictionary.setVisibility(View.GONE);
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
        binding.llIncludeMyVoice.setVisibility(View.GONE);
        binding.llPauseTimeToRepeatTts.setVisibility(View.GONE);
        binding.llTableHeaderPlayVoice.setVisibility(View.GONE);
        binding.llDisplayPronunciation.setVisibility(View.GONE);
        binding.llPreferredNativeSpeakersCount.setVisibility(View.GONE);
        binding.llKeepPlayingOnBackgroundMode.setVisibility(View.GONE);
        binding.llPreferredNativeSpeakersCount.setVisibility(View.GONE);
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
    }

}