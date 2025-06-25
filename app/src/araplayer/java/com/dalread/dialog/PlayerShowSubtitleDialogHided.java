package com.dalread.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerShowSubtitleDialogHidedBinding;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.model.SubtitleHideModel;
import com.dalread.util.Constant;

public class PlayerShowSubtitleDialogHided extends BasePlayerDialog implements View.OnClickListener, DialogInterface.OnDismissListener {
    private OnClickDialogListener listener;
    private SubtitleHideModel subtitleHideModel;
    private Integer shorter_3_known_words_number;
    private Integer longer_20_words_number;
    private boolean isSelected_music_symbol;
    private boolean isSelected_paired_bracket;
    private boolean isSelected_all_capitals;
    private boolean isSelected_including_url;
    private boolean isSelected_no_study_lang_character;
    private boolean isSelected_shorter_3_known_words;
    private boolean isSelected_longer_20_words;
    private boolean isSelected_except_unknown_subtitles;
    private boolean isSelected_known_subtitles;

    private DialogPlayerShowSubtitleDialogHidedBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerShowSubtitleDialogHidedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        subtitleHideModel = new SubtitleHideModel();

        shorter_3_known_words_number = mSharedPref.getSubtitleToHideShorter3KnownWordsNumber();
        longer_20_words_number = mSharedPref.getSubtitleToHideLonger20WordsNumber();
        isSelected_music_symbol = mSharedPref.getSubtitleToHideMusicSymbol();
        isSelected_paired_bracket = mSharedPref.getSubtitleToHidePairedBracket();
        isSelected_all_capitals = mSharedPref.getSubtitleToHideAllCapitals();
        isSelected_including_url = mSharedPref.getSubtitleToHideIncludingUrl();
        isSelected_no_study_lang_character = mSharedPref.getSubtitleToHideNoStudyLangCharacter();
        isSelected_shorter_3_known_words = mSharedPref.getSubtitleToHideShorter3KnownWords();
        isSelected_longer_20_words = mSharedPref.getSubtitleToHideLonger20Words();
        isSelected_except_unknown_subtitles = mSharedPref.getSubtitleToHideExceptUnknownSubtitles();
        isSelected_known_subtitles = mSharedPref.getSubtitleToHideKnownSubtitles();

        binding.scSubtitleToHideMusicSymbol.setChecked(isSelected_music_symbol);
        binding.scSubtitleToHidePairedBracket.setChecked(isSelected_paired_bracket);
        binding.scSubtitleToHideAllCapitals.setChecked(isSelected_all_capitals);
        binding.scSubtitleToHideIncludingUrl.setChecked(isSelected_including_url);
        binding.scSubtitleToHideNoStudyLangCharacter.setChecked(isSelected_no_study_lang_character);
        binding.scSubtitleToHideShorter3KnownWords.setChecked(isSelected_shorter_3_known_words);
        binding.scSubtitleToHideLonger20Words.setChecked(isSelected_longer_20_words);
        binding.scSubtitleToHideExceptUnknownSubtitles.setChecked(isSelected_except_unknown_subtitles);
        binding.scSubtitleToHideKnownSubtitles.setChecked(isSelected_known_subtitles);

        updateVisibilityHideShort3KnownWord();
        updateTitleInHideShort3KnownWord();
        updateVisibilityHideLonger20Word();
        updateTitleInHideLonger20Word();
    }

    private void updateVisibilityHideShort3KnownWord() {
        binding.llSubtitleToHideShorter3KnownWords.setVisibility(isSelected_shorter_3_known_words ? View.VISIBLE : View.GONE);
        binding.vSubtitleToHideShorter3KnownWords.setVisibility(isSelected_shorter_3_known_words ? View.VISIBLE : View.GONE);
    }

    private void updateTitleInHideShort3KnownWord() {
        binding.tvSubtitleToHideShorter3KnownWords.setText(res.getString(R.string.all_subtitles_except_shorter_than_3_known_words_number, shorter_3_known_words_number));
    }

    private void updateVisibilityHideLonger20Word() {
        binding.llSubtitleToHideLonger20Words.setVisibility(isSelected_longer_20_words ? View.VISIBLE : View.GONE);
    }
    private void updateTitleInHideLonger20Word() {
        binding.tvSubtitleToHideLonger20Words.setText(res.getString(R.string.all_subtitles_except_longer_20_words_number, longer_20_words_number));
    }
    public PlayerShowSubtitleDialogHided(@NonNull Context context, OnClickDialogListener listener) {
        super(context);
        setDialogSizeWider(true);
        this.listener = listener;
        setOnDismissListener(this);
    }

    @Override
    protected void initOnClickListener() {
        binding.llSubtitleToHideShorter3KnownWords.setOnClickListener(this);
        binding.llSubtitleToHideLonger20Words.setOnClickListener(this);
        binding.scSubtitleToHideMusicSymbol.setOnClickListener(this);
        binding.scSubtitleToHidePairedBracket.setOnClickListener(this);
        binding.scSubtitleToHideAllCapitals.setOnClickListener(this);
        binding.scSubtitleToHideIncludingUrl.setOnClickListener(this);
        binding.scSubtitleToHideShorter3KnownWords.setOnClickListener(this);
        binding.scSubtitleToHideNoStudyLangCharacter.setOnClickListener(this);
        binding.scSubtitleToHideKnownSubtitles.setOnClickListener(this);
        binding.scSubtitleToHideExceptUnknownSubtitles.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
        binding.tvApply.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_subtitle_to_hide_shorter_3_known_words:
            case R.id.ll_subtitle_to_hide_longer_20_words:
                break;
            case R.id.sc_subtitle_to_hide_music_symbol:
                isSelected_music_symbol = !isSelected_music_symbol;
                mSharedPref.setSubtitleToHideMusicSymbol(isSelected_music_symbol);
                break;
            case R.id.sc_subtitle_to_hide_paired_bracket:
                isSelected_paired_bracket = !isSelected_paired_bracket;
                mSharedPref.setSubtitleToHidePairedBracket(isSelected_paired_bracket);
                break;
            case R.id.sc_subtitle_to_hide_all_capitals:
                isSelected_all_capitals = !isSelected_all_capitals;
                mSharedPref.setSubtitleToHideAllCapitals(isSelected_all_capitals);
                break;
            case R.id.sc_subtitle_to_hide_including_url:
                isSelected_including_url = !isSelected_including_url;
                mSharedPref.setSubtitleToHideIncludingUrl(isSelected_including_url);
                break;
            case R.id.sc_subtitle_to_hide_no_study_lang_character:
                isSelected_no_study_lang_character = !isSelected_no_study_lang_character;
                mSharedPref.setSubtitleToHideNoStudyLangCharacter(isSelected_no_study_lang_character);
                break;
            case R.id.sc_subtitle_to_hide_shorter_3_known_words:
                isSelected_shorter_3_known_words = !isSelected_shorter_3_known_words;
                mSharedPref.setSubtitleToHideShorter3KnownWords(isSelected_shorter_3_known_words);
                updateVisibilityHideShort3KnownWord();
                break;
            case R.id.sc_subtitle_to_hide_longer_20_words:
                isSelected_longer_20_words = !isSelected_longer_20_words;
                mSharedPref.setSubtitleToHideLonger20Words(isSelected_longer_20_words);
                updateVisibilityHideLonger20Word();
                break;
            case R.id.sc_subtitle_to_hide_known_subtitles:
                isSelected_known_subtitles = !isSelected_known_subtitles;
                mSharedPref.setSubtitleToHideKnownSubtitles(isSelected_known_subtitles);
                break;
            case R.id.sc_subtitle_to_hide_except_unknown_subtitles:
                isSelected_except_unknown_subtitles = !isSelected_except_unknown_subtitles;
                mSharedPref.setSubtitleToHideExceptUnknownSubtitles(isSelected_except_unknown_subtitles);
                break;
            case R.id.tv_cancel:
                dismiss();
                if (listener != null) {
                    listener.onDismiss(v, null);
                }
                break;
            case R.id.tv_apply:
                dismiss();
                if (listener != null) {
                    subtitleHideModel.setSelected_music_symbol(isSelected_music_symbol);
                    subtitleHideModel.setSelected_paired_bracket(isSelected_paired_bracket);
                    subtitleHideModel.setSelected_all_capitals(isSelected_all_capitals);
                    subtitleHideModel.setSelected_including_url(isSelected_including_url);
                    subtitleHideModel.setSelected_no_study_lang_character(isSelected_no_study_lang_character);
                    subtitleHideModel.setSelected_shorter_3_known_words(isSelected_shorter_3_known_words);
                    if (isSelected_shorter_3_known_words) {
                        subtitleHideModel.setNumber_shorter_3_known_words(shorter_3_known_words_number);
                    } else {
                        subtitleHideModel.setNumber_shorter_3_known_words(Constant.PLAYER.DEFAULT_SUBTITLE_TO_HIDE_SHORTER_3_KNOWN_WORDS_NONE_NUMBER);
                    }
                    subtitleHideModel.setSelected_longer_20_words(isSelected_longer_20_words);
                    if (isSelected_longer_20_words) {
                        subtitleHideModel.setNumber_longer_20_words(longer_20_words_number);
                    } else {
                        subtitleHideModel.setNumber_longer_20_words(Constant.PLAYER.DEFAULT_SUBTITLE_TO_HIDE_LONGER_20_WORDS_NUMBER_DEFAULT);
                    }
                    subtitleHideModel.setSelected_show_always_unknown_subtitles(isSelected_except_unknown_subtitles);
                    subtitleHideModel.setSelected_known_subtitles(isSelected_known_subtitles);
                    listener.onClick(v, subtitleHideModel);
                }
                break;
        }
    }


//    @OnClick({
//            R.id.ll_subtitle_to_hide_shorter_3_known_words, R.id.ll_subtitle_to_hide_longer_20_words, R.id.sc_subtitle_to_hide_music_symbol,
//            R.id.sc_subtitle_to_hide_paired_bracket, R.id.sc_subtitle_to_hide_all_capitals, R.id.sc_subtitle_to_hide_including_url, R.id.sc_subtitle_to_hide_shorter_3_known_words,
//            R.id.sc_subtitle_to_hide_no_study_lang_character, R.id.sc_subtitle_to_hide_known_subtitles, R.id.sc_subtitle_to_hide_except_unknown_subtitles, R.id.tv_cancel, R.id.tv_apply})
//    void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.ll_subtitle_to_hide_shorter_3_known_words:
//            case R.id.ll_subtitle_to_hide_longer_20_words:
//                break;
//            case R.id.sc_subtitle_to_hide_music_symbol:
//                isSelected_music_symbol = !isSelected_music_symbol;
//                mSharedPref.setSubtitleToHideMusicSymbol(isSelected_music_symbol);
//                break;
//            case R.id.sc_subtitle_to_hide_paired_bracket:
//                isSelected_paired_bracket = !isSelected_paired_bracket;
//                mSharedPref.setSubtitleToHidePairedBracket(isSelected_paired_bracket);
//                break;
//            case R.id.sc_subtitle_to_hide_all_capitals:
//                isSelected_all_capitals = !isSelected_all_capitals;
//                mSharedPref.setSubtitleToHideAllCapitals(isSelected_all_capitals);
//                break;
//            case R.id.sc_subtitle_to_hide_including_url:
//                isSelected_including_url = !isSelected_including_url;
//                mSharedPref.setSubtitleToHideIncludingUrl(isSelected_including_url);
//                break;
//            case R.id.sc_subtitle_to_hide_no_study_lang_character:
//                isSelected_no_study_lang_character = !isSelected_no_study_lang_character;
//                mSharedPref.setSubtitleToHideNoStudyLangCharacter(isSelected_no_study_lang_character);
//                break;
//            case R.id.sc_subtitle_to_hide_shorter_3_known_words:
//                isSelected_shorter_3_known_words = !isSelected_shorter_3_known_words;
//                mSharedPref.setSubtitleToHideShorter3KnownWords(isSelected_shorter_3_known_words);
//                updateVisibilityHideShort3KnownWord();
//                break;
//            case R.id.sc_subtitle_to_hide_longer_20_words:
//                isSelected_longer_20_words = !isSelected_longer_20_words;
//                mSharedPref.setSubtitleToHideLonger20Words(isSelected_longer_20_words);
//                updateVisibilityHideLonger20Word();
//                break;
//            case R.id.sc_subtitle_to_hide_known_subtitles:
//                isSelected_known_subtitles = !isSelected_known_subtitles;
//                mSharedPref.setSubtitleToHideKnownSubtitles(isSelected_known_subtitles);
//                break;
//            case R.id.sc_subtitle_to_hide_except_unknown_subtitles:
//                isSelected_except_unknown_subtitles = !isSelected_except_unknown_subtitles;
//                mSharedPref.setSubtitleToHideExceptUnknownSubtitles(isSelected_except_unknown_subtitles);
//                break;
//            case R.id.tv_cancel:
//                dismiss();
//                if (listener != null) {
//                    listener.onDismiss(view, null);
//                }
//                break;
//            case R.id.tv_apply:
//                dismiss();
//                if (listener != null) {
//                    subtitleHideModel.setSelected_music_symbol(isSelected_music_symbol);
//                    subtitleHideModel.setSelected_paired_bracket(isSelected_paired_bracket);
//                    subtitleHideModel.setSelected_all_capitals(isSelected_all_capitals);
//                    subtitleHideModel.setSelected_including_url(isSelected_including_url);
//                    subtitleHideModel.setSelected_no_study_lang_character(isSelected_no_study_lang_character);
//                    subtitleHideModel.setSelected_shorter_3_known_words(isSelected_shorter_3_known_words);
//                    if (isSelected_shorter_3_known_words) {
//                        subtitleHideModel.setNumber_shorter_3_known_words(shorter_3_known_words_number);
//                    } else {
//                        subtitleHideModel.setNumber_shorter_3_known_words(Constant.PLAYER.DEFAULT_SUBTITLE_TO_HIDE_SHORTER_3_KNOWN_WORDS_NONE_NUMBER);
//                    }
//                    subtitleHideModel.setSelected_longer_20_words(isSelected_longer_20_words);
//                    if (isSelected_longer_20_words) {
//                        subtitleHideModel.setNumber_longer_20_words(longer_20_words_number);
//                    } else {
//                        subtitleHideModel.setNumber_longer_20_words(Constant.PLAYER.DEFAULT_SUBTITLE_TO_HIDE_LONGER_20_WORDS_NUMBER_DEFAULT);
//                    }
//                    subtitleHideModel.setSelected_show_always_unknown_subtitles(isSelected_except_unknown_subtitles);
//                    subtitleHideModel.setSelected_known_subtitles(isSelected_known_subtitles);
//                    listener.onClick(view, subtitleHideModel);
//                }
//                break;
//        }
//    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if ((listener != null) && (view != null)){
            listener.onDismiss(view, -1);
        }
    }
}
