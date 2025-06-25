package com.dalread.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CompoundButton;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.DialogPlayerListenComprehensionOptionInScrollableMenuBinding;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.util.Voca;

import java.util.Arrays;

public class PlayerListenComprehensionOptionDialog extends BasePlayerDialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, DialogInterface.OnDismissListener {
    private OnClickDialogListener listener;
    private Context context;
    private DialogPlayerListenComprehensionOptionInScrollableMenuBinding binding;
    private boolean playDifficultWordsBeforePlayingSubtitles;
    private boolean includeMotherTongueSubtitle;
    private boolean includeMyVoice;
    private boolean includeMeaning;

    private String[] readCountValues;
    private int selectedReadCountValue;
    private SingleChoiceDialog singleChoiceDialog;

    private SharedPreferencesDB sharedPreferences;

    @Override
    protected View getContentView() {
        binding = DialogPlayerListenComprehensionOptionInScrollableMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public PlayerListenComprehensionOptionDialog(Context context, OnClickDialogListener listener) {
        super(context);
        setDialogSizeWider(true);
        this.context = context;
        this.listener = listener;
        setOnDismissListener(this);

        sharedPreferences = SharedPreferencesDB.getInstance(getContext());
        initDialog();
        setValue();

    }

    private void initDialog() {
        singleChoiceDialog = new SingleChoiceDialog(context);
    }

    private void setValue() {
        playDifficultWordsBeforePlayingSubtitles = sharedPreferences.getPlayDifficultWordsBeforePlayingSubtitles();
        binding.scPlayDifficultWordsBeforePlayingSubtitles.setChecked(playDifficultWordsBeforePlayingSubtitles);
        showHideDetailsLayout(playDifficultWordsBeforePlayingSubtitles);

        includeMeaning = sharedPreferences.getIncludeMeaning();
        binding.scIncludeMeaning.setChecked(includeMeaning);

        includeMyVoice = sharedPreferences.getIncludeMyVoice();
        binding.scIncludeMyVoice.setChecked(includeMyVoice);

        includeMotherTongueSubtitle = sharedPreferences.getIncludeMotherTongueSubtitle();
        binding.scIncludeMotherTongueSubtitle.setChecked(includeMotherTongueSubtitle);

        readCountValues = Voca.getReadCountValues();
        String readCount = sharedPreferences.getReadCount();
        selectedReadCountValue = Arrays.asList(readCountValues).indexOf(readCount);
        binding.tvReadCountValue.setText(readCount);

    }

    @Override
    protected void initOnClickListener() {
        binding.llReadCountValue.setOnClickListener(this);
        binding.tvOK.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llReadCountValue:
                showReadCountValue();
                break;
            case R.id.tvOK:
                dismiss();
                break;
        }
    }

//    @OnClick({R.id.llReadCountValue, R.id.tvOK})
//    void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.llReadCountValue:
//                showReadCountValue();
//                break;
//            case R.id.tvOK:
//                dismiss();
//                break;
//        }
//    }

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
                            binding.tvReadCountValue.setText(text);
                            sharedPreferences.setReadCount(text);
                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.scIncludeMyVoice:
                sharedPreferences.setIncludeMyVoice(includeMyVoice = isChecked);
                break;
            case R.id.scIncludeMeaning:
                sharedPreferences.setIncludeMeaning(includeMeaning = isChecked);
                break;
            case R.id.scPlayDifficultWordsBeforePlayingSubtitles:
                sharedPreferences.setPlayDifficultWordsBeforePlayingSubtitles(playDifficultWordsBeforePlayingSubtitles = isChecked);
                showHideDetailsLayout(isChecked);
                break;
            case R.id.scIncludeMotherTongueSubtitle:
                sharedPreferences.setIncludeMotherTongueSubtitle(includeMotherTongueSubtitle = isChecked);
                break;
        }
    }


//    @OnCheckedChanged({R.id.scIncludeMyVoice, R.id.scIncludeMeaning, R.id.scPlayDifficultWordsBeforePlayingSubtitles, R.id.scIncludeMotherTongueSubtitle})
//    void onCheckedChanged(CompoundButton button, boolean checked) {
//        int id = button.getId();
//        switch (id) {
//            case R.id.scIncludeMyVoice:
//                sharedPreferences.setIncludeMyVoice(includeMyVoice = checked);
//                break;
//            case R.id.scIncludeMeaning:
//                sharedPreferences.setIncludeMeaning(includeMeaning = checked);
//                break;
//            case R.id.scPlayDifficultWordsBeforePlayingSubtitles:
//                sharedPreferences.setPlayDifficultWordsBeforePlayingSubtitles(playDifficultWordsBeforePlayingSubtitles = checked);
//                showHideDetailsLayout(checked);
//                break;
//            case R.id.scIncludeMotherTongueSubtitle:
//                sharedPreferences.setIncludeMotherTongueSubtitle(includeMotherTongueSubtitle = checked);
//                break;
//        }
//    }

    private void showHideDetailsLayout(boolean isCheckedPlayDifficultWords) {
        if (isCheckedPlayDifficultWords) {
            binding.layoutPlayDifficultWordsDetails.setVisibility(View.VISIBLE);
        } else {
            binding.layoutPlayDifficultWordsDetails.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (listener != null) {
            listener.onDismiss(view, -1);
        }
    }
}

