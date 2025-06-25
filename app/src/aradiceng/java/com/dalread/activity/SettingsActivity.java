package com.dalread.activity;

import android.os.Bundle;
import android.view.View;

import com.dalread.base.BaseSettingsActivity;

public class SettingsActivity extends BaseSettingsActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void hideMenus() {
        super.hideMenus();
        binding.llTableHeaderChooseLanguage.setVisibility(View.GONE);
        binding.llStudyLang.setVisibility(View.GONE);
        binding.llMotherTongueLang.setVisibility(View.GONE);
        binding.llMenuDisplayLang.setVisibility(View.GONE);
        binding.llTableHeaderPlayVoice.setVisibility(View.GONE);
        binding.llReadCountValueToPlayTts.setVisibility(View.GONE);
        binding.llPreferredNativeSpeakersCount.setVisibility(View.GONE);
        binding.llIncludeMyVoice.setVisibility(View.GONE);
        binding.llPlayDifficultWordsBeforePlayingSubtitles.setVisibility(View.GONE);
        binding.llIncludeMeaning.setVisibility(View.GONE);
        binding.llTableGroupTtsSpeed.setVisibility(View.GONE);
        binding.llAutoPlay.setVisibility(View.GONE);
        binding.llAllowChat.setVisibility(View.GONE);
        binding.llCountOfNotRatedWordBeforePlaying.setVisibility(View.GONE);
        binding.llDisplayPronunciation.setVisibility(View.GONE);
        binding.llIncludeMotherTongueSubtitle.setVisibility(View.GONE);
        binding.llWebDictionary.setVisibility(View.GONE);
        binding.llKeepPlayingOnBackgroundMode.setVisibility(View.GONE);
        binding.llPlayVideoAutomaticallyWhenOpenIt.setVisibility(View.GONE);
        binding.llPlayVideoFromWhereYouLeft.setVisibility(View.GONE);
        binding.llHide4ButtonsOnPlayingScreen.setVisibility(View.GONE);
        binding.llShowVideoInCameraFolder.setVisibility(View.GONE);
        binding.llDisplayPronunciation.setVisibility(View.GONE);
        binding.llAllowChat.setVisibility(View.GONE);
        binding.llShareMyRecordings.setVisibility(View.GONE);
        binding.llTranslateSubtitleFromServer.setVisibility(View.GONE);
        binding.llSyncSubtitlesAtServer.setVisibility(View.GONE);
        binding.llTableHeaderQuiz.setVisibility(View.GONE);
        binding.llMaxQuiz.setVisibility(View.GONE);
        binding.llDisplayEnglishMeaningToo.setVisibility(View.GONE);
        binding.llPauseTimeToRepeatTts.setVisibility(View.GONE);
        binding.llTableHeaderEtc.setVisibility(View.GONE);
        binding.llHanjaFontSize.setVisibility(View.GONE);
    }
}