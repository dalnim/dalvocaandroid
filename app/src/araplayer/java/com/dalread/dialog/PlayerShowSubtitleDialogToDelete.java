package com.dalread.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerShowSubtitleDialogToDeleteBinding;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.model.SubtitleHideModel;

public class PlayerShowSubtitleDialogToDelete extends BasePlayerDialog implements View.OnClickListener, DialogInterface.OnDismissListener {

    private OnClickDialogListener listener;
    private SubtitleHideModel subtitleHideModel;
    private boolean isSelected_music_symbol;
    private boolean isSelected_paired_bracket;
    private boolean isSelected_all_capitals;
    private boolean isSelected_including_url;
    private boolean isSelected_no_study_lang_character;
    private boolean isSelected_1_word;
    private boolean isSelected_repeated_1_word;

    private DialogPlayerShowSubtitleDialogToDeleteBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerShowSubtitleDialogToDeleteBinding.inflate(getLayoutInflater());
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

        isSelected_music_symbol = mSharedPref.getSubtitleToHideMusicSymbol();
        isSelected_paired_bracket = mSharedPref.getSubtitleToHidePairedBracket();
        isSelected_all_capitals = mSharedPref.getSubtitleToHideAllCapitals();
        isSelected_including_url = mSharedPref.getSubtitleToHideIncludingUrl();
        isSelected_no_study_lang_character = mSharedPref.getSubtitleToHideNoStudyLangCharacter();
        isSelected_1_word = mSharedPref.getSubtitleToHide1Word();
        isSelected_repeated_1_word = mSharedPref.getSubtitleToHideRepeated1Word();

        binding.scSubtitleToHideMusicSymbol.setChecked(isSelected_music_symbol);
        binding.scSubtitleToHidePairedBracket.setChecked(isSelected_paired_bracket);
        binding.scSubtitleToHideAllCapitals.setChecked(isSelected_all_capitals);
        binding.scSubtitleToHideIncludingUrl.setChecked(isSelected_including_url);
        binding.scSubtitleToHideNoStudyLangCharacter.setChecked(isSelected_no_study_lang_character);
        binding.scSubtitleToHide1Word.setChecked(isSelected_1_word);
        binding.scSubtitleToHideRepeated1Word.setChecked(isSelected_repeated_1_word);
    }

    public PlayerShowSubtitleDialogToDelete(@NonNull Context context, OnClickDialogListener listener) {
        super(context);
        setDialogSizeWider(true);
        this.listener = listener;
        setOnDismissListener(this);
    }

    @Override
    protected void initOnClickListener() {
        binding.scSubtitleToHide1Word.setOnClickListener(this);
        binding.scSubtitleToHideRepeated1Word.setOnClickListener(this);
        binding.scSubtitleToHideMusicSymbol.setOnClickListener(this);
        binding.scSubtitleToHidePairedBracket.setOnClickListener(this);
        binding.scSubtitleToHideAllCapitals.setOnClickListener(this);
        binding.scSubtitleToHideIncludingUrl.setOnClickListener(this);
        binding.scSubtitleToHideNoStudyLangCharacter.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
        binding.tvApply.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sc_subtitle_to_hide_1_word:
                isSelected_1_word = !isSelected_1_word;
                mSharedPref.setSubtitleToHide1Word(isSelected_1_word);
                break;
            case R.id.sc_subtitle_to_hide_repeated_1_word:
                isSelected_repeated_1_word = !isSelected_repeated_1_word;
                mSharedPref.setSubtitleToHideRepeated1Word(isSelected_repeated_1_word);
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
            case R.id.tv_cancel:
                dismiss();
                if (listener != null) {
                    listener.onDismiss(v, null);
                }
            case R.id.tv_apply:
                dismiss();
                if (listener != null) {
                    subtitleHideModel.setSelected_music_symbol(isSelected_music_symbol);
                    subtitleHideModel.setSelected_paired_bracket(isSelected_paired_bracket);
                    subtitleHideModel.setSelected_all_capitals(isSelected_all_capitals);
                    subtitleHideModel.setSelected_including_url(isSelected_including_url);
                    subtitleHideModel.setSelected_no_study_lang_character(isSelected_no_study_lang_character);
                    subtitleHideModel.setSelected_1_word(isSelected_1_word);
                    subtitleHideModel.setSelected_repeated_1_word(isSelected_repeated_1_word);
                    listener.onClick(v, subtitleHideModel);
                }
                break;
        }
    }

//
//    @OnClick({
//            R.id.sc_subtitle_to_hide_1_word, R.id.sc_subtitle_to_hide_repeated_1_word, R.id.sc_subtitle_to_hide_music_symbol,
//            R.id.sc_subtitle_to_hide_paired_bracket, R.id.sc_subtitle_to_hide_all_capitals, R.id.sc_subtitle_to_hide_including_url,
//            R.id.sc_subtitle_to_hide_no_study_lang_character, R.id.tv_cancel, R.id.tv_apply})
//    void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.sc_subtitle_to_hide_1_word:
//                isSelected_1_word = !isSelected_1_word;
//                mSharedPref.setSubtitleToHide1Word(isSelected_1_word);
//                break;
//            case R.id.sc_subtitle_to_hide_repeated_1_word:
//                isSelected_repeated_1_word = !isSelected_repeated_1_word;
//                mSharedPref.setSubtitleToHideRepeated1Word(isSelected_repeated_1_word);
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
//            case R.id.tv_cancel:
//                dismiss();
//                if (listener != null) {
//                    listener.onDismiss(view, null);
//                }
//            case R.id.tv_apply:
//                dismiss();
//                if (listener != null) {
//                    subtitleHideModel.setSelected_music_symbol(isSelected_music_symbol);
//                    subtitleHideModel.setSelected_paired_bracket(isSelected_paired_bracket);
//                    subtitleHideModel.setSelected_all_capitals(isSelected_all_capitals);
//                    subtitleHideModel.setSelected_including_url(isSelected_including_url);
//                    subtitleHideModel.setSelected_no_study_lang_character(isSelected_no_study_lang_character);
//                    subtitleHideModel.setSelected_1_word(isSelected_1_word);
//                    subtitleHideModel.setSelected_repeated_1_word(isSelected_repeated_1_word);
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
