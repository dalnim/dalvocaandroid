package com.dalread.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.dalread.R;
import com.dalread.activity.PreferredNativeSpeakersActivity;
import com.dalread.activity.WebDictionaryActivity;
import com.dalread.component.Toolbar;
import com.dalread.composition.PlayTTS;
import com.dalread.databinding.ActivitySettingsBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.helper.BaseSettingsActivityColorHelper;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseVoca;
import com.dalread.util.DarkThemeUtil;
import com.dalread.util.LanguageUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.studylang.AbstractStudyLang;
import com.dalread.util.studylang.StudyLangFactory;

import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;
import java.util.List;

public abstract class BaseSettingsActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private AlertDialog alertDialog;
    private SingleChoiceDialog singleChoiceDialog;

    private String[] studyLanguages;
    private int selectedStudyLanguage;
    private EnumLanguage enumStudyLanguage;
    private String[] languageList;
    private int selectedMenuLanguage;
    private int selectedDisplayLanguage;
    private String[] readCountValues;
    private int selectedReadCountValue;
    private String[] countOfNotRatedWordBeforePlayingValues;
    private int selectedCountOfNotRatedWordBeforePlayingValue;

    private boolean includeMyVoice;
    private boolean enableSecureScreen;
    private boolean includeMeaning;
    private boolean displayEnglishMeaningToo;
    private boolean keepDisplayingBackgroundHintWhenWriting;
    private boolean pauseTimeToRepeatTts;
    private boolean playMotherTongueOnlyFirstRoundOnConversation;
    private boolean includeMotherTongueSubtitle;
    private boolean playDifficultWordsBeforePlayingSubtitles;
    private AbstractStudyLang studyLang;
    private int ttsSpeed;
    protected boolean displayPronunciation;
    private String[] maxValues;
    private int selectedMaxHomework;
    private int selectedMaxQuiz;
    private boolean allowChat;
    private boolean shareMyRecordings;
    private boolean autoPlay;
    private boolean syncSubtitlesAtServer;
//    private boolean showVideoInCameraFolder;
    private boolean translateSubtitleFromServer;
    private boolean keepPlayingOnBackgroundMode;
    private boolean playVideoAutomaticallyWhenOpenIt;
    private boolean playVideoFromWhereYouLeft;
    private boolean hide4ButtonsOnPlayingScreen;
    private boolean showAdvancedMode;
    private int countOfNotRatedWordBeforePlaying;
    private String[] displayFontSizeList;
    private int[] displayFontSizeListRatioList;
    private String[] displayThemes;
    private int selectedTheme;
    private String[] displayGptNames;
    private int selectedGpt;
    private PlayTTS playTTS;
    public ActivitySettingsBinding binding;
    private boolean isInInitMethod;
    protected BaseSettingsActivityColorHelper colorHelper;
    @Override
    protected View getContentView() {
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Nullable
    public Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playTTS = new PlayTTS(this);
        init();
        hideMenus();
        updateUI();
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {

    }
    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.tvPreferredNativeSpeakersCount.setText(String.valueOf(sharedPreferences.getPreferredNativeSpeakersCount()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void init() {
        setListener();
        colorHelper = new BaseSettingsActivityColorHelper(this);
        isInInitMethod = true;
        alertDialog = new AlertDialog(this);
        singleChoiceDialog = new SingleChoiceDialog(this);

        studyLanguages = EnumLanguage.getStudyLanguages();
        enumStudyLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage());
        selectedStudyLanguage = Arrays.asList(EnumLanguage.getStudyLanguagesFormatApi()).indexOf(enumStudyLanguage.getFormatApi());
        binding.tvStudyLangValue.setText(enumStudyLanguage.getFormatUser());

        languageList = EnumLanguage.getLanguages();
        EnumLanguage displayLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage());
        List<String> languages = Arrays.asList(languageList);
        selectedDisplayLanguage = languages.indexOf(displayLanguage.getFormatUser());
        includeMyVoice = sharedPreferences.getIncludeMyVoice();
        enableSecureScreen = sharedPreferences.getEnableSecureScreen();
        includeMeaning = sharedPreferences.getIncludeMeaning();
        displayEnglishMeaningToo = sharedPreferences.getDisplayEnglishMeaningToo();
        keepDisplayingBackgroundHintWhenWriting = sharedPreferences.getKeepDisplayingBackgroundHintWhenWriting();
        pauseTimeToRepeatTts = sharedPreferences.isPauseTimeToRepeatTts();
        playMotherTongueOnlyFirstRoundOnConversation = sharedPreferences.isPlayMotherTongueOnlyFirstRoundFinishOnConversation();
        displayPronunciation = sharedPreferences.getDisplayPronunciation();
        maxValues = BaseVoca.getMaxHomeworkValues();
        List maxValueList = Arrays.asList(maxValues);
        String maxHomework = String.valueOf(sharedPreferences.getMaxHomework());
        selectedMaxHomework = maxValueList.indexOf(maxHomework);
        String maxQuiz = String.valueOf(sharedPreferences.getMaxQuiz());
        selectedMaxQuiz = maxValueList.indexOf(maxQuiz);
        allowChat = sharedPreferences.getAllowChat();
        shareMyRecordings = sharedPreferences.getShareMyRecordings();
        autoPlay = sharedPreferences.getAutoPlayInRecordingAll();

        includeMotherTongueSubtitle = sharedPreferences.getIncludeMotherTongueSubtitle();
        playDifficultWordsBeforePlayingSubtitles = sharedPreferences.getPlayDifficultWordsBeforePlayingSubtitles();

//        showVideoInCameraFolder = sharedPreferences.getShowVideoInDCIM();
        translateSubtitleFromServer = sharedPreferences.getTranslateSubtitleFromServer();
        syncSubtitlesAtServer = sharedPreferences.getSyncSubtitlesAtServer();
        keepPlayingOnBackgroundMode = sharedPreferences.getKeepPlayingOnBackgroundMode();
        playVideoAutomaticallyWhenOpenIt = sharedPreferences.getPlayVideoAutomaticallyWhenOpenIt();
        playVideoFromWhereYouLeft = sharedPreferences.getPlayVideoFromWhereYouLeft();
        hide4ButtonsOnPlayingScreen = sharedPreferences.getHide4ButtonsOnPlayingScreen();
        showAdvancedMode = sharedPreferences.isShowAdvancedMode();
        countOfNotRatedWordBeforePlaying = sharedPreferences.getCountOfNotRatedWordBeforePlaying();

        displayFontSizeList = this.getResources().getStringArray(R.array.array_display_font_size_list);
        displayFontSizeListRatioList = this.getResources().getIntArray(R.array.array_display_font_size_list_ratio);

        binding.tvMotherTongueLangValue.setText(displayLanguage.getFormatUser());
        EnumLanguage menuLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getMenuLanguage());
        selectedMenuLanguage = languages.indexOf(menuLanguage.getFormatUser());
        binding.tvMenuDisplayLangValue.setText(menuLanguage.getFormatUser());

        readCountValues = BaseVoca.getReadCountValues();
        String readCount = sharedPreferences.getReadCount();
        selectedReadCountValue = Arrays.asList(readCountValues).indexOf(readCount);

        countOfNotRatedWordBeforePlayingValues = BaseVoca.getCountNotRatedWordsOnlyToDisplayInSubtitle();
        Integer countOfNotRatedWordBeforePlayingValue = sharedPreferences.getCountOfNotRatedWordBeforePlaying();
        selectedCountOfNotRatedWordBeforePlayingValue = Arrays.asList(countOfNotRatedWordBeforePlayingValues).indexOf(countOfNotRatedWordBeforePlayingValue.toString());

        binding.tvReadCountValueToPlayTtsValue.setText(readCount);
        binding.scIncludeMyVoice.setChecked(includeMyVoice);
        binding.scIncludeMeaning.setChecked(includeMeaning);
        binding.scSecureScreen.setChecked(enableSecureScreen);
        binding.scDisplayEnglishMeaningToo.setChecked(displayEnglishMeaningToo);
        binding.scKeepDisplayingBackgroundHintWhenWriting.setChecked(keepDisplayingBackgroundHintWhenWriting);
        binding.scPauseTimeToRepeatTts.setChecked(pauseTimeToRepeatTts);
        binding.scPlayMotherTongueOnlyFirstRoundFinishOnConversation.setChecked(playMotherTongueOnlyFirstRoundOnConversation);

        binding.scIncludeMotherTongueSubtitle.setChecked(includeMotherTongueSubtitle);
        binding.scPlayDifficultWordsBeforePlayingSubtitles.setChecked(playDifficultWordsBeforePlayingSubtitles);
//        binding.scShowVideoInCameraFolder.setChecked(showVideoInCameraFolder);
        binding.scTranslateSubtitleFromServer.setChecked(translateSubtitleFromServer);
        binding.scSyncSubtitlesAtServer.setChecked(syncSubtitlesAtServer);
//        binding.scShowSubtitleTableWhenOpen.setChecked(sharedPreferences.getShowSubtitleTableWhenOpen());
        binding.scKeepPlayingOnBackgroundMode.setChecked(keepPlayingOnBackgroundMode);
        binding.scPlayVideoAutomaticallyWhenOpenIt.setChecked(playVideoAutomaticallyWhenOpenIt);
        binding.scPlayVideoFromWhereYouLeft.setChecked(playVideoFromWhereYouLeft);
        binding.scHide4ButtonsOnPlayingScreen.setChecked(hide4ButtonsOnPlayingScreen);
        binding.scShowAdvancedMode.setChecked(showAdvancedMode);
        binding.tvCountOfNotRatedWordBeforePlayingValue.setText(String.valueOf(selectedCountOfNotRatedWordBeforePlayingValue));

        displayThemes = EnumTheme.getNames(this);
        int themeStorage = sharedPreferences.getSelectedTheme();
        String themeStorageValue = EnumTheme.getNameFromId(this, themeStorage);
        selectedTheme = Arrays.asList(displayThemes).indexOf(themeStorageValue);
        binding.tvChooseDarkThemeValue.setText(themeStorageValue);


        displayGptNames = EnumGpt.getNames();
        updateSelectGptAndTextView(sharedPreferences.getSelectedGpt());


        initTts();

        binding.scDisplayPronunciation.setChecked(displayPronunciation);
//        binding.tvMaxHomework.setText(maxHomework);
        binding.tvMaxQuiz.setText(maxQuiz);
        binding.scAllowChat.setChecked(allowChat);
        binding.scShareMyRecordings.setChecked(shareMyRecordings);
        binding.scAutoPlay.setChecked(autoPlay);
        binding.tvAppVersionValue.setText(BaseVoca.getAppVersion());
        isInInitMethod = false;
    }



    protected void initTts() {
        studyLang = StudyLangFactory.create(context);
        initTtsSpeed();
    }

    private void initTtsSpeed() {
        binding.sbPlayTtsSpeed.setOnSeekBarChangeListener(null);
        ttsSpeed = studyLang.getTTSSpeedInSetting();
        binding.sbPlayTtsSpeed.setProgress(ttsSpeed);
        binding.tvPlayTtsSpeed.setText(String.valueOf(ttsSpeed));
        binding.tvLetStudy.setText(enumStudyLanguage.getLetStudy());
        binding.sbPlayTtsSpeed.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    private void setListener() {
        setOnClickListeners();
        setCheckedChangeListeners();
    }

    private void setOnClickListeners() {
        binding.llStudyLang.setOnClickListener(this);
        binding.llMenuDisplayLang.setOnClickListener(this);
        binding.llMotherTongueLang.setOnClickListener(this);
        binding.llReadCountValueToPlayTts.setOnClickListener(this);
        binding.llPreferredNativeSpeakersCount.setOnClickListener(this);
        binding.llGptWebMenuValue.setOnClickListener(this);
        binding.tvBtnSayTts.setOnClickListener(this);
        binding.llWebDictionary.setOnClickListener(this);
        binding.llChooseDarkTheme.setOnClickListener(this);
        binding.llChooseFoldersForMedia.setOnClickListener(this);
        binding.llChooseGpt.setOnClickListener(this);
        binding.llCountOfNotRatedWordBeforePlaying.setOnClickListener(this);
        binding.llHanjaFontSize.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.llStudyLang:
                showStudyLanguageDialog();
                break;
            case R.id.llMenuDisplayLang:
                showMenuLanguageDialog();
                break;
            case R.id.llMotherTongueLang:
                showMotherTongueDialog();
                break;
            case R.id.llReadCountValueToPlayTts:
                showReadCountValue();
                break;
            case R.id.llPreferredNativeSpeakersCount:
                showPreferredNativeSpeakers();
                break;
            case R.id.llGptWebMenuValue:
                showGptWebMenu();
                break;
            case R.id.tvBtnSayTts:
                playTTS.playTTSHelper.stopStudyTTS();
                playTTS.playTTSHelper.playStudyTTS(enumStudyLanguage.getLetStudy());
                break;
            case R.id.llWebDictionary:
                openWebDictionary();
                break;
            case R.id.llChooseDarkTheme:
                showChooseThemeDialog();
                break;
            case R.id.llChooseFoldersForMedia:
                showChooseMediaFolderSelectionDialog();
                break;
            case R.id.llChooseGpt:
                showChooseGpt();
                break;
            case R.id.llCountOfNotRatedWordBeforePlaying:
                showCountOfNotRatedWordBeforePlayingDialog();
                break;
            case R.id.llHanjaFontSize:
                showMenuHanjaFontSizeDialog();
                break;
            default:
                break;
        }
    }

    private void setCheckedChangeListeners() {
        binding.scIncludeMyVoice.setOnCheckedChangeListener(this);
        binding.scSecureScreen.setOnCheckedChangeListener(this);
        binding.scIncludeMeaning.setOnCheckedChangeListener(this);
        binding.scDisplayEnglishMeaningToo.setOnCheckedChangeListener(this);
        binding.scKeepDisplayingBackgroundHintWhenWriting.setOnCheckedChangeListener(this);
        binding.scIncludeMotherTongueSubtitle.setOnCheckedChangeListener(this);
        binding.scPlayDifficultWordsBeforePlayingSubtitles.setOnCheckedChangeListener(this);
        binding.scTranslateSubtitleFromServer.setOnCheckedChangeListener(this);
        binding.scDisplayPronunciation.setOnCheckedChangeListener(this);
        binding.scSyncSubtitlesAtServer.setOnCheckedChangeListener(this);
        binding.scPauseTimeToRepeatTts.setOnCheckedChangeListener(this);
        binding.scPlayMotherTongueOnlyFirstRoundFinishOnConversation.setOnCheckedChangeListener(this);
        binding.scShowAdvancedMode.setOnCheckedChangeListener(this);
        binding.scKeepPlayingOnBackgroundMode.setOnCheckedChangeListener(this);
        binding.scPlayVideoAutomaticallyWhenOpenIt.setOnCheckedChangeListener(this);
        binding.scPlayVideoFromWhereYouLeft.setOnCheckedChangeListener(this);
        binding.scHide4ButtonsOnPlayingScreen.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.scIncludeMyVoice:
                sharedPreferences.setIncludeMyVoice(includeMyVoice = isChecked);
//                playTTS.playTTSHelper.setIncludeMyVoice(includeMyVoice);
                break;
            case R.id.scSecureScreen:
                sharedPreferences.setEnableSecureScreen(enableSecureScreen = isChecked);
                break;
            case R.id.scPauseTimeToRepeatTts:
                sharedPreferences.setPauseTimeToRepeatTts(pauseTimeToRepeatTts = isChecked);
                break;
            case R.id.scPlayMotherTongueOnlyFirstRoundFinishOnConversation:
                sharedPreferences.setPlayMotherTongueOnlyFirstRoundFinishOnConversation(playMotherTongueOnlyFirstRoundOnConversation = isChecked);
                break;
            case R.id.scDisplayPronunciation:
                sharedPreferences.setDisplayPronunciation(displayPronunciation = isChecked);
                application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DISPLAY_PRONUNCIATION, null));
                break;
            case R.id.scIncludeMeaning:
                sharedPreferences.setIncludeMeaning(includeMeaning = isChecked);
                playTTS.playTTSHelper.setIncludeMeaning(includeMeaning);
                break;
            case R.id.scDisplayEnglishMeaningToo:
                sharedPreferences.setDisplayEnglishMeaningToo(displayEnglishMeaningToo = isChecked);
                application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DISPLAY_ENGLISH_MEANING_TOO, null));
                break;
            case R.id.scKeepDisplayingBackgroundHintWhenWriting:
                sharedPreferences.setKeepDisplayingBackgroundHintWhenWriting(keepDisplayingBackgroundHintWhenWriting = isChecked);
                break;
            case R.id.scIncludeMotherTongueSubtitle:
                sharedPreferences.setIncludeMotherTongueSubtitle(includeMotherTongueSubtitle = isChecked);
                break;
            case R.id.scPlayDifficultWordsBeforePlayingSubtitles:
                sharedPreferences.setPlayDifficultWordsBeforePlayingSubtitles(playDifficultWordsBeforePlayingSubtitles = isChecked);
                break;
//            case R.id.scShowVideoInCameraFolder:
//                sharedPreferences.setShowVideoInDCIM(showVideoInCameraFolder = checked);
//                break;
            case R.id.scTranslateSubtitleFromServer:
                sharedPreferences.setTranslateSubtitleFromServer(translateSubtitleFromServer = isChecked);
                break;
            case R.id.scSyncSubtitlesAtServer:
                if (syncSubtitlesAtServer == isChecked) return;
                sharedPreferences.setSyncSubtitlesAtServer(syncSubtitlesAtServer = isChecked);
//                callMakeRubyTextFromSubtitle();
                break;
            case R.id.scKeepPlayingOnBackgroundMode:
                sharedPreferences.setKeepPlayingOnBackgroundMode(keepPlayingOnBackgroundMode = isChecked);
                break;
            case R.id.scPlayVideoAutomaticallyWhenOpenIt:
                sharedPreferences.setPlayVideoAutomaticallyWhenOpenIt(playVideoAutomaticallyWhenOpenIt = isChecked);
                break;
            case R.id.scPlayVideoFromWhereYouLeft:
                sharedPreferences.setPlayVideoFromWhereYouLeft(playVideoFromWhereYouLeft = isChecked);
                break;
            case R.id.scHide4ButtonsOnPlayingScreen:
                sharedPreferences.setHide4ButtonsOnPlayingScreen(hide4ButtonsOnPlayingScreen = isChecked);
                break;
            case R.id.scShowAdvancedMode:
                sharedPreferences.setShowAdvancedMode(showAdvancedMode = isChecked);
                if (isChecked && !isInInitMethod) {
                    showAdvancedModeDialog();
                }
                break;
            default:
                break;
        }
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            binding.tvPlayTtsSpeed.setText(String.valueOf(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            ttsSpeed = seekBar.getProgress();
            studyLang.setTTSSpeedInSetting(ttsSpeed);

            playTTS.playTTSHelper.stopStudyTTS();
            playTTS.playTTSHelper.setStudyTTSSpeed(ttsSpeed);
            playTTS.playTTSHelper.playStudyTTS(enumStudyLanguage.getLetStudy());
        }
    };

    private void showMotherTongueDialog() {
        singleChoiceDialog.show(
                R.string.mother_tongue,
                languageList,
                selectedDisplayLanguage,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        int which = (int) object;
                        if (which != selectedDisplayLanguage) {
                            String text = languageList[selectedDisplayLanguage = which];
                            binding.tvMotherTongueLangValue.setText(text);
                            sharedPreferences.setMotherTongueLanguage(EnumLanguage.findByFormatUser(text).getFormatApi());
                            if (LanguageUtil.isMotherTongueLangEnglish(context)) {
                                displayEnglishMeaningToo = false;
                            }
                            sharedPreferences.setDisplayEnglishMeaningToo(displayEnglishMeaningToo);
                            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.MOTHER_TONGUE_LANGUAGE_CHANGED, null));
                            //Don't need to call this.
//                            application.getDalAiImpl().updateStudyLangAndMotherTongue();
                            ToastUtil.getInstance(BaseSettingsActivity.this).show(R.string.msg_change_tongue_language_analyze_again_video_information);
//                            alertDialog.show(getString(R.string.msg_change_tongue_language_analyze_again_video_information), null, null);
                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }
    private void showPreferredNativeSpeakers() {
        if (UserUtil.isDebugOrAdminUser(this)) {
            openNewScreen(PreferredNativeSpeakersActivity.class);
        }
    }
    protected void showGptWebMenu() {

    }

    protected void showAdvancedModeDialog() {

    }

    private void showReadCountValue() {
        singleChoiceDialog.show(
                R.string.read_count,
                readCountValues,
                selectedReadCountValue,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        if (which != selectedReadCountValue) {
                            String text = readCountValues[selectedReadCountValue = which];
                            binding.tvReadCountValueToPlayTtsValue.setText(text);
                            sharedPreferences.setReadCount(text);
//                            playTTS.playTTSHelper.setReadCount(Utils.parseInt(text));
                            playTTS.playTTSHelper.setTotalCountToPlayVoiceOrTts(Utils.parseInt(text));

                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    private void showStudyLanguageDialog() {
        singleChoiceDialog.show(
                R.string.study,
                studyLanguages,
                selectedStudyLanguage,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        if (which != selectedStudyLanguage) {
                            ToastUtil.getInstance(BaseSettingsActivity.this).clearInstance();
                            sharedPreferences.setStudyLanguage(EnumLanguage.findByFormatUser(studyLanguages[which]).getFormatApi());
                            //Was DISPLAY_LANGUAGE_CHANGED
                            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.STUDY_LANGUAGE_CHANGED, null));
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
//                                finishAffinity();
//                                startActivity(i);
//                            } else {
//                                recreate();
//                            }
                            initTtsSpeed();
                            playTTS.playTTSHelper.initStudyTTS(Integer.parseInt(sharedPreferences.getReadCount()));
                            if (UserUtil.isLoggedIn(context, true)) {
                                application.getDalAiImpl().changeStudyLang(
                                        String.valueOf(getUserID()),
                                        sharedPreferences.getLangStudyCode()
                                );
                                application.getDalAiImpl().updateStudyLangAndMotherTongue();
                            }
                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    private void showMenuLanguageDialog() {
        singleChoiceDialog.show(
                R.string.menu,
                languageList,
                selectedMenuLanguage,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        if (which != selectedMenuLanguage) {
                            ToastUtil.getInstance(BaseSettingsActivity.this).clearInstance();
                            sharedPreferences.setMenuLanguage(EnumLanguage.findByFormatUser(languageList[which]).getFormatApi());
                            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.MENU_LANGUAGE_CHANGED, null));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                                finishAffinity();
                                startActivity(i);
                            } else {
                                recreate();
                            }
                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    private void showMenuHanjaFontSizeDialog() {
        int selectedFontSizeIndex = getSelectedHanjaFontSizeIndex();
        singleChoiceDialog.show(
                R.string.menu_hanja_font_size,
                displayFontSizeList,
                selectedFontSizeIndex,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        if (which != selectedFontSizeIndex) {
                            sharedPreferences.setAraHanjaFontSizeRatio(displayFontSizeListRatioList[which] / 10.0f);
                            Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                            finishAffinity();
                            startActivity(i);
                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    private int getSelectedHanjaFontSizeIndex() {
        float storedAraHanjaFontSizeRatio = sharedPreferences.getAraHanjaFontSizeRatio(this);
        int selectedFontSizeIndex = 1;
        for(int i = 0; i < displayFontSizeListRatioList.length; i++) {
            float displayFontSizeListRatio = displayFontSizeListRatioList[i] / 10.0f;
            if (storedAraHanjaFontSizeRatio == displayFontSizeListRatio) {
                selectedFontSizeIndex = i;
                break;
            }
        }
        return selectedFontSizeIndex;
    }

    private String getStoredFontSize() {
        String result = "";
        int selectedFontSizeIndex = getSelectedHanjaFontSizeIndex();
        if (selectedFontSizeIndex >= 0 && getSelectedHanjaFontSizeIndex() <= displayFontSizeList.length) {
            result = displayFontSizeList[selectedFontSizeIndex];
        }
        return result;
    }

    protected void showChooseMediaFolderSelectionDialog() {

    }
    private void showChooseThemeDialog() {
        singleChoiceDialog.showWrapContentHeight(
                R.string.choose_theme,
                displayThemes,
                selectedTheme,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        if (which != selectedTheme) {
                            sharedPreferences.setSelectedTheme(which);
                            int darkLightMode = DarkThemeUtil.getDarkLightThemeFromStorage(sharedPreferences);//.getDarkLightThemeFromStorage();
                            AppCompatDelegate.setDefaultNightMode(darkLightMode);
//                            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.MENU_LANGUAGE_CHANGED, null));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                                finishAffinity();
                                startActivity(i);
                            } else {
                                recreate();
                            }
                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    private void showChooseGpt() {
        singleChoiceDialog.showWrapContentHeight(
                R.string.setting_menu_choose_gpt,
                displayGptNames,
                selectedGpt,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        if (which != selectedGpt) {
                            sharedPreferences.setSelectedGpt(which);
                            updateSelectGptAndTextView(which);
                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    private void updateSelectGptAndTextView(int which) {
        String gptName = EnumGpt.getNameFromId(which);
        selectedGpt = Arrays.asList(displayGptNames).indexOf(gptName);
        binding.tvChooseGptValue.setText(gptName);
    }

    private void showCountOfNotRatedWordBeforePlayingDialog() {
        singleChoiceDialog.show(
                R.string.count_not_rated_words_only_to_display_in_subtitle,
                countOfNotRatedWordBeforePlayingValues,
                selectedCountOfNotRatedWordBeforePlayingValue,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        if (which != selectedCountOfNotRatedWordBeforePlayingValue) {
                            countOfNotRatedWordBeforePlaying = which;
                            String strCountOfNotRatedWordBeforePlaying = countOfNotRatedWordBeforePlayingValues[which];
                            sharedPreferences.setCountOfNotRatedWordBeforePlaying(Integer.parseInt(strCountOfNotRatedWordBeforePlaying));
                            binding.tvCountOfNotRatedWordBeforePlayingValue.setText(strCountOfNotRatedWordBeforePlaying);
                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    private void updateUI() {
        binding.tvFontSize.setText(getStoredFontSize());

        if (Utils.isDebugOrAdminUser(this)) {

        } else {

        }

        binding.tvAppVersionValue.setText(BaseVoca.getAppVersion());
    }

    private void openWebDictionary() {
        Intent intent = new Intent(this, WebDictionaryActivity.class);
        startActivity(intent);
    }


    protected void hideMenus() {
        binding.llLangLevel.setVisibility(View.GONE);
        binding.llGptWebMenuValue.setVisibility(View.GONE);
        binding.llPlayMotherTongueOnlyFirstRoundFinishOnConversation.setVisibility(View.GONE);
        binding.llKeepDisplayingBackgroundHintWhenWriting.setVisibility(View.GONE);
        binding.llChooseGpt.setVisibility(View.GONE);
        binding.llShowAdvancedMode.setVisibility(View.GONE);
        binding.llChooseFoldersForMedia.setVisibility(View.GONE);
        binding.llSecureScreen.setVisibility(View.GONE);
    }
}
