package com.dalread.activity;

import android.view.View;

import com.dalread.base.BaseSettingsActivity;

public class SettingsActivity extends BaseSettingsActivity {
    @Override
    protected void hideMenus() {
        super.hideMenus();
        binding.llWebDictionary.setVisibility(View.GONE);
        binding.llKeepPlayingOnBackgroundMode.setVisibility(View.GONE);
        binding.llPlayVideoAutomaticallyWhenOpenIt.setVisibility(View.GONE);
        binding.llPlayVideoFromWhereYouLeft.setVisibility(View.GONE);
        binding.llShowVideoInCameraFolder.setVisibility(View.GONE);
        binding.llDisplayPronunciation.setVisibility(View.GONE);
        binding.llTranslateSubtitleFromServer.setVisibility(View.GONE);
        binding.llSyncSubtitlesAtServer.setVisibility(View.GONE);
        binding.llCountOfNotRatedWordBeforePlaying.setVisibility(View.GONE);
        binding.llHanjaFontSize.setVisibility(View.GONE);
        binding.llDisplayEnglishMeaningToo.setVisibility(View.GONE);
    }


//
//    @BindView(R.id.tv_study_value)
//    TextView tvStudyValue;
//    @BindView(R.id.tv_mother_tongue_value)
//    TextView tvMotherTongueValue;
//    @BindView(R.id.tv_menu_value)
//    TextView tvMenuValue;
//    @BindView(R.id.tv_read_count_value)
//    TextView tvReadCountValue;
//    @BindView(R.id.tv_preferred_native_speakers_count)
//    TextView tvPreferredNativeSpeakersCount;
//    @BindView(R.id.sc_include_my_voice)
//    SwitchCompat scIncludeMyVoice;
//    @BindView(R.id.sc_include_meaning)
//    SwitchCompat scIncludeMeaning;
//    @BindView(R.id.sc_background_mode)
//    SwitchCompat scBackgroundMode;
//    @BindView(R.id.tv_let_study)
//    TextView tvLetStudy;
//    @BindView(R.id.sb_speed)
//    SeekBar sbSpeed;
//    @BindView(R.id.tv_speed)
//    TextView tvSpeed;
//    @BindView(R.id.sc_display_pronunciation)
//    SwitchCompat scDisplayPronunciation;
//    @BindView(R.id.tv_max_homework)
//    TextView tvMaxHomework;
//    @BindView(R.id.tv_max_quiz)
//    TextView tvMaxQuiz;
//    @BindView(R.id.tv_max_hanja_quiz)
//    TextView tvMaxHanjaQuiz;
//    @BindView(R.id.sc_allow_chat)
//    SwitchCompat scAllowChat;
//    @BindView(R.id.sc_share_my_recordings)
//    SwitchCompat scShareMyRecordings;
//    @BindView(R.id.sc_auto_play)
//    SwitchCompat scAutoPlay;
//    @BindView(R.id.tv_app_version_value)
//    TextView tvAppVersionValue;
//
//    private boolean dataChanged;
//    private AlertDialog alertDialog;
//    private SingleChoiceDialog singleChoiceDialog;
//    private String[] studyLanguages;
//    private int selectedStudyLanguage;
//    private EnumLanguage studyLanguage;
//    private String[] displayLanguages;
//    private int selectedDisplayLanguage;
//    private int selectedMenuLanguage;
//    private String[] readCountValues;
//    private int selectedReadCountValue;
//    private boolean includeMyVoice;
//    private boolean includeMeaning;
//    private boolean backgroundMode;
//    private int ttsSpeed;
//    private boolean displayPronunciation;
//    private String[] maxValues;
//    private int selectedMaxHomework;
//    private int selectedMaxQuiz;
//    private int selectedMaxHanjaQuiz;
//    private boolean allowChat;
//    private boolean shareMyRecordings;
//    private boolean autoPlay;
//
//    @Override
//    protected int getContentViewId() {
//        return R.layout.activity_settings;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        init();
//    }
//
//    @Override
//    public void onHeaderLeftClick() {
//        onBackPressed();
//    }
//    @Override
//    public void onHeaderLeft2Click() {
//    }
//    @Override
//    public void onHeaderRightClick() {
//    }
//
//    @Override
//    public void onHeaderIconRightClick() {
//
//    }
//
//    @Override
//    public void onHeaderTextRightClick() {
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        String text = String.valueOf(sharedPreferences.getPreferredNativeSpeakersCount());
//        tvPreferredNativeSpeakersCount.setText(text);
//    }
//
//    @Override
//    protected void onDestroy() {
//        updateSettings();
//
//        super.onDestroy();
//    }
//
//    private void init() {
//        alertDialog = new AlertDialog(this);
//        singleChoiceDialog = new SingleChoiceDialog(this);
//        studyLanguages = EnumLanguage.getStudyLanguages();
//        studyLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage());
//        selectedStudyLanguage = Arrays.asList(EnumLanguage.getStudyLanguagesFormatApi()).indexOf(studyLanguage.getFormatApi());
//        displayLanguages = EnumLanguage.getDisplayLanguages();
//        EnumLanguage displayLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getDisplayLanguage());
//        List<String> languages = Arrays.asList(displayLanguages);
//        selectedDisplayLanguage = languages.indexOf(displayLanguage.getFormatUser());
//        EnumLanguage menuLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getMenuLanguage());
//        selectedMenuLanguage = languages.indexOf(menuLanguage.getFormatUser());
//        readCountValues = Voca.getReadCountValues();
//        String readCount = sharedPreferences.getReadCount();
//        selectedReadCountValue = Arrays.asList(readCountValues).indexOf(readCount);
//        includeMyVoice = sharedPreferences.getIncludeMyVoice();
//        includeMeaning = sharedPreferences.getIncludeMeaning();
//        backgroundMode = sharedPreferences.getBackgroundMode();
//        displayPronunciation = sharedPreferences.getDisplayPronunciation();
//        maxValues = Voca.getMaxHomeworkValues();
//        List maxValueList = Arrays.asList(maxValues);
//        String maxHomework = String.valueOf(sharedPreferences.getMaxHomework());
//        selectedMaxHomework = maxValueList.indexOf(maxHomework);
//        String maxQuiz = String.valueOf(sharedPreferences.getMaxQuiz());
//        selectedMaxQuiz = maxValueList.indexOf(maxQuiz);
//        String maxHanjaQuiz = String.valueOf(sharedPreferences.getMaxHanjaQuiz());
//        selectedMaxHanjaQuiz = maxValueList.indexOf(maxHanjaQuiz);
//        allowChat = sharedPreferences.getAllowChat();
//        shareMyRecordings = sharedPreferences.getShareMyRecordings();
//        autoPlay = sharedPreferences.getAutoPlayInRecordingAll();
//
//        tvStudyValue.setText(studyLanguage.getFormatUser());
//        tvMotherTongueValue.setText(displayLanguage.getFormatUser());
//        tvMenuValue.setText(menuLanguage.getFormatUser());
//        tvReadCountValue.setText(readCount);
//        scIncludeMyVoice.setChecked(includeMyVoice);
//        scIncludeMeaning.setChecked(includeMeaning);
//        scBackgroundMode.setChecked(backgroundMode);
//        initTTSSpeed();
//        scDisplayPronunciation.setChecked(displayPronunciation);
//        tvMaxHomework.setText(maxHomework);
//        tvMaxQuiz.setText(maxQuiz);
//        tvMaxHanjaQuiz.setText(maxHanjaQuiz);
//        scAllowChat.setChecked(allowChat);
//        scShareMyRecordings.setChecked(shareMyRecordings);
//        scAutoPlay.setChecked(autoPlay);
//        tvAppVersionValue.setText(Utils.getAppVersion());
//    }
//
//    private void initTTSSpeed() {
//        sbSpeed.setOnSeekBarChangeListener(null);
//        if (studyLanguage == EnumLanguage.CHINESE_SIMPLIFIED) {
//            ttsSpeed = sharedPreferences.getSettingTTSSpeedChinese();
//        } else if (studyLanguage == EnumLanguage.ENGLISH) {
//            ttsSpeed = sharedPreferences.getSettingTTSSpeedEnglish();
//        } else if (studyLanguage == EnumLanguage.JAPANESE) {
//            ttsSpeed = sharedPreferences.getSettingTTSSpeedJapanese();
//        } else if (studyLanguage == EnumLanguage.KOREAN) {
//            ttsSpeed = sharedPreferences.getSettingTTSSpeedKorean();
//        } else if (studyLanguage == EnumLanguage.HANJA) {
//            ttsSpeed = sharedPreferences.getSettingTTSSpeedHanja();
//        }
//        sbSpeed.setProgress(ttsSpeed);
//        tvSpeed.setText(String.valueOf(ttsSpeed));
//        tvLetStudy.setText(studyLanguage.getLetStudy());
//        sbSpeed.setOnSeekBarChangeListener(onSeekBarChangeListener);
//    }
//
//    @OnClick({R.id.v_study_value, R.id.v_mother_tongue_value, R.id.v_menu_value,
//            R.id.v_read_count_value, R.id.v_preferred_native_speakers, R.id.btn_say,
//            R.id.v_max_homework, R.id.v_max_quiz, R.id.v_max_hanja_quiz})
//    void onClick(View view) {
//        int id = view.getId();
//        switch (id) {
//            case R.id.v_study_value:
//                singleChoiceDialog.show(
//                        R.string.study,
//                        studyLanguages,
//                        selectedStudyLanguage,
//                        R.string.ok,
//                        R.string.cancel,
//                        new OnClickDialogListener() {
//                            @Override
//                            public void onClick(View view, Object object) {
//                                final int which = (int) object;
//                                if (which != selectedStudyLanguage) {
//                                    dataChanged = true;
//                                    String text = studyLanguages[selectedStudyLanguage = which];
//                                    tvStudyValue.setText(text);
//                                    Voca.setStudyLanguage(sharedPreferences, studyLanguage = EnumLanguage.findByFormatApi(EnumLanguage.getStudyLanguagesFormatApi()[selectedStudyLanguage]));
//                                    application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.RUBY_ACTIVITY, BaseEvent.EventType.DISPLAY_LANGUAGE_CHANGED, null));
//                                    initTTSSpeed();
//                                    playVocaHelper.initStudyTTS(
//                                            studyLanguage.getLocale(),
//                                            Integer.parseInt(sharedPreferences.getReadCount()),
//                                            ttsSpeed
//                                    );
//                                    int uid = getUserID();
//                                    if (uid > 0 && Utils.isConnected(SettingsActivity.this)) {
//                                        application.getDalAiImpl().changeStudyLang(
//                                                String.valueOf(uid),
//                                                sharedPreferences.getLangStudyCode()
//                                        );
//                                        application.getDalAiImpl().updateStudyLangAndMotherTongue();
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onDismiss(View view, Object object) {
//
//                            }
//                        });
//                break;
//            case R.id.v_mother_tongue_value:
//                singleChoiceDialog.show(
//                        R.string.mother_tongue,
//                        displayLanguages,
//                        selectedDisplayLanguage,
//                        R.string.ok,
//                        R.string.cancel,
//                        new OnClickDialogListener() {
//                            @Override
//                            public void onClick(View view, Object object) {
//                                final int which = (int) object;
//                                if (which != selectedDisplayLanguage) {
//                                    String text = displayLanguages[selectedDisplayLanguage = which];
//                                    tvMotherTongueValue.setText(text);
//                                    sharedPreferences.setDisplayLanguage(EnumLanguage.findByFormatUser(text).getFormatApi());
//                                    application.getDalAiImpl().updateStudyLangAndMotherTongue();
//                                }
//                            }
//
//                            @Override
//                            public void onDismiss(View view, Object object) {
//
//                            }
//                        });
//                break;
//            case R.id.v_menu_value:
//                singleChoiceDialog.show(
//                        R.string.menu,
//                        displayLanguages,
//                        selectedMenuLanguage,
//                        R.string.ok,
//                        R.string.cancel,
//                        new OnClickDialogListener() {
//                            @Override
//                            public void onClick(View view, Object object) {
//                                final int which = (int) object;
//                                if (which != selectedMenuLanguage) {
//                                    sharedPreferences.setMenuLanguage(EnumLanguage.findByFormatUser(displayLanguages[which]).getFormatApi());
//                                    application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DISPLAY_LANGUAGE_CHANGED, null));
//                                    application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.RUBY_ACTIVITY, BaseEvent.EventType.DISPLAY_LANGUAGE_CHANGED, null));
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                                        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
//                                        finishAffinity();
//                                        startActivity(i);
//                                    } else {
//                                        recreate();
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onDismiss(View view, Object object) {
//
//                            }
//                        });
//                break;
//            case R.id.v_read_count_value:
//                singleChoiceDialog.show(
//                        R.string.read_count,
//                        readCountValues,
//                        selectedReadCountValue,
//                        R.string.ok,
//                        R.string.cancel,
//                        new OnClickDialogListener() {
//                            @Override
//                            public void onClick(View view, Object object) {
//                                final int which = (int) object;
//                                if (which != selectedReadCountValue) {
//                                    String text = readCountValues[selectedReadCountValue = which];
//                                    tvReadCountValue.setText(text);
//                                    sharedPreferences.setReadCount(text);
//                                    playVocaHelper.setReadCount(Utils.parseInt(text));
//                                }
//                            }
//
//                            @Override
//                            public void onDismiss(View view, Object object) {
//
//                            }
//                        });
//                break;
//            case R.id.v_preferred_native_speakers:
//                int uid = getUserID();
//                if (uid > 0) {
//                    if (Utils.isConnected(this)) {
//                        openNewScreen(PreferredNativeSpeakersActivity.class);
//                    } else {
//                        alertDialog.showNoInternet();
//                    }
//                } else {
//                    alertDialog.showLogInRequired();
//                }
//                break;
//            case R.id.btn_say:
//                playVocaHelper.stopStudyTTS();
//                playVocaHelper.playStudyTTS(studyLanguage.getLetStudy());
//                break;
//            case R.id.v_max_homework:
//                singleChoiceDialog.show(
//                        R.string.max_homework,
//                        maxValues,
//                        selectedMaxHomework,
//                        R.string.ok,
//                        R.string.cancel,
//                        new OnClickDialogListener() {
//                            @Override
//                            public void onClick(View view, Object object) {
//                                final int which = (int) object;
//                                if (which != selectedMaxHomework) {
//                                    dataChanged = true;
//                                    String text = maxValues[selectedMaxHomework = which];
//                                    tvMaxHomework.setText(text);
//                                    sharedPreferences.setMaxHomework(Integer.parseInt(text));
//                                }
//                            }
//
//                            @Override
//                            public void onDismiss(View view, Object object) {
//
//                            }
//                        });
//                break;
//            case R.id.v_max_quiz:
//                singleChoiceDialog.show(
//                        R.string.max_quiz,
//                        maxValues,
//                        selectedMaxQuiz,
//                        R.string.ok,
//                        R.string.cancel,
//                        new OnClickDialogListener() {
//                            @Override
//                            public void onClick(View view, Object object) {
//                                final int which = (int) object;
//                                if (which != selectedMaxQuiz) {
//                                    dataChanged = true;
//                                    String text = maxValues[selectedMaxQuiz = which];
//                                    tvMaxQuiz.setText(text);
//                                    sharedPreferences.setMaxQuiz(Integer.parseInt(text));
//                                }
//                            }
//
//                            @Override
//                            public void onDismiss(View view, Object object) {
//
//                            }
//                        });
//                break;
//            case R.id.v_max_hanja_quiz:
//                singleChoiceDialog.show(
//                        R.string.max_hanja_quiz,
//                        maxValues,
//                        selectedMaxHanjaQuiz,
//                        R.string.ok,
//                        R.string.cancel,
//                        new OnClickDialogListener() {
//                            @Override
//                            public void onClick(View view, Object object) {
//                                final int which = (int) object;
//                                if (which != selectedMaxHanjaQuiz) {
//                                    dataChanged = true;
//                                    String text = maxValues[selectedMaxHanjaQuiz = which];
//                                    tvMaxHanjaQuiz.setText(text);
//                                    sharedPreferences.setMaxHanjaQuiz(Integer.parseInt(text));
//                                }
//                            }
//
//                            @Override
//                            public void onDismiss(View view, Object object) {
//
//                            }
//                        });
//                break;
//            default:
//                break;
//        }
//    }
//
//    @OnCheckedChanged({R.id.sc_include_my_voice, R.id.sc_include_meaning, R.id.sc_background_mode, R.id.sc_display_pronunciation,
//            R.id.sc_allow_chat, R.id.sc_share_my_recordings, R.id.sc_auto_play})
//    void onCheckedChanged(CompoundButton button, boolean checked) {
//        int id = button.getId();
//        switch (id) {
//            case R.id.sc_include_my_voice:
//                sharedPreferences.setIncludeMyVoice(includeMyVoice = checked);
//                playVocaHelper.setIncludeMyVoice(includeMyVoice);
//                break;
//            case R.id.sc_include_meaning:
//                sharedPreferences.setIncludeMeaning(includeMeaning = checked);
//                playVocaHelper.setIncludeMeaning(includeMeaning);
//                break;
//            case R.id.sc_background_mode:
//                sharedPreferences.setBackgroundMode(backgroundMode = checked);
//                updateBackgroundMode();
//                break;
//            case R.id.sc_display_pronunciation:
//                sharedPreferences.setDisplayPronunciation(displayPronunciation = checked);
//                break;
//            case R.id.sc_allow_chat:
//                dataChanged = true;
//                sharedPreferences.setAllowChat(allowChat = checked);
//                break;
//            case R.id.sc_share_my_recordings:
//                dataChanged = true;
//                sharedPreferences.setShareMyRecordings(shareMyRecordings = checked);
//                break;
//            case R.id.sc_auto_play:
//                sharedPreferences.setAutoPlayInRecordingAll(autoPlay = checked);
//                break;
//            default:
//                break;
//        }
//    }
//
//    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
//
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            tvSpeed.setText(String.valueOf(progress));
//        }
//
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//        }
//
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//            ttsSpeed = seekBar.getProgress();
//            if (studyLanguage == EnumLanguage.CHINESE_SIMPLIFIED) {
//                sharedPreferences.setSettingTTSSpeedChinese(ttsSpeed);
//            } else if (studyLanguage == EnumLanguage.ENGLISH) {
//                sharedPreferences.setSettingTTSSpeedEnglish(ttsSpeed);
//            } else if (studyLanguage == EnumLanguage.JAPANESE) {
//                sharedPreferences.setSettingTTSSpeedJapanese(ttsSpeed);
//            } else if (studyLanguage == EnumLanguage.KOREAN) {
//                sharedPreferences.setSettingTTSSpeedKorean(ttsSpeed);
//            } else if (studyLanguage == EnumLanguage.HANJA) {
//                sharedPreferences.setSettingTTSSpeedHanja(ttsSpeed);
//            }
//            playVocaHelper.stopStudyTTS();
//            playVocaHelper.setStudyTTSSpeed(ttsSpeed);
//            playVocaHelper.playStudyTTS(studyLanguage.getLetStudy());
//        }
//    };
//
//    private void updateSettings() {
//        if (dataChanged) {
//            if (getUserID() > 0 && Utils.isConnected(this)) {
//                application.getDalAiImpl().updateSettingValue(
//                        Integer.parseInt(maxValues[selectedMaxHomework]),
//                        Integer.parseInt(maxValues[selectedMaxQuiz]),
//                        Integer.parseInt(maxValues[selectedMaxHanjaQuiz]),
//                        allowChat,
//                        shareMyRecordings
//                );
//            }
//        }
//    }
}
