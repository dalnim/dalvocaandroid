package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.database.sqlite.model.SubtitleModel;
import com.dalread.databinding.PopupEditSubtitleBinding;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.util.DLog;
import com.dalread.util.StringUtils;
import com.dalread.util.Utils;

import org.jetbrains.annotations.NotNull;

public class EditSubtitleDialog extends BasePlayerDialog implements View.OnClickListener {
    private com.dalread.listener.OnClickDialogListener listener;
    private DicModel dicModel;
    private PopupEditSubtitleBinding binding;
    private String studyLanguageContentBefore;
    private String tongueLanguageContentBefore;
    private boolean isBack = false;
    private boolean isUpdateSubtitle;
    private Context context;
    long backKeyPressedTime;
    @Override
    protected View getContentView() {
        binding = PopupEditSubtitleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public EditSubtitleDialog(@NonNull Context context, DicModel dicModel, boolean isUpdateSubtitle, com.dalread.listener.OnClickDialogListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.dicModel = dicModel;
        this.isUpdateSubtitle = isUpdateSubtitle;
        studyLanguageContentBefore = dicModel.getVocaDisplay() == null ? "" : dicModel.getVocaDisplay();
        tongueLanguageContentBefore = dicModel.getMeaning() == null ? "" : dicModel.getMeaning();
        binding.etStudyLanguageContent.setText(studyLanguageContentBefore);
        binding.etTongueLanguageContent.setText(tongueLanguageContentBefore);

        hideMenusOnReleaseMode();
        updatePlayIconToPlayImage();
        updateMicToReadyToRecordImage();

        setDialogSizeWider(true);
        setCanceledOnTouchOutside(false);
    }

    private void hideMenusOnReleaseMode() {
        if (Utils.isDebugOrAdminUser(context)) {

        } else {

        }
    }

    private void updatePlayIcon() {
        if (isPlayIcon()) {
            updatePlayIconToPauseImage();
        } else {
            updatePlayIconToPlayImage();
        }
    }

    private boolean isPlayIcon() {
        return (Integer) binding.ivLMPlay.getTag() == R.drawable.ic_new_play;
    }

    private void updatePlayIconToPauseImage() {
        binding.ivLMPlay.setImageResource(R.drawable.ic_new_pause);
        binding.ivLMPlay.setTag(R.drawable.ic_new_pause);
    }

    private void updatePlayIconToPlayImage() {
        binding.ivLMPlay.setImageResource(R.drawable.ic_new_play);
        binding.ivLMPlay.setTag(R.drawable.ic_new_play);
    }

    public void updateMicToBeingRecordingImage() {
        binding.ivSTT.setImageResource(R.drawable.ic_mic_black_24dp);
        binding.ivSTT.setTag(R.drawable.ic_mic_black_24dp);
    }

    public void updateMicToReadyToRecordImage() {
        binding.ivSTT.setImageResource(R.drawable.ic_mic_none_black_24dp);
        binding.ivSTT.setTag(R.drawable.ic_mic_none_black_24dp);
    }

    private SubtitleModel getChangedSubtitle() {
        final String studyLanguageContent = getStudyLanguageContent();
        final String tongueLanguageContent = getTongueLanguageContent();
        SubtitleModel subtitleModel = new SubtitleModel();
        subtitleModel.setSubtitle(studyLanguageContent);
        subtitleModel.setMeaning(tongueLanguageContent);
        return subtitleModel;

    }

    @NotNull
    private String getStudyLanguageContent() {
        return StringUtils.getTrimTextFromTextView(binding.etStudyLanguageContent);
    }

    @NotNull
    private String getTongueLanguageContent() {
        return StringUtils.getTrimTextFromTextView(binding.etTongueLanguageContent);
    }

    private boolean isDialogChanged() {
        final String studyLanguageContent = getStudyLanguageContent();
        final String tongueLanguageContent = getTongueLanguageContent();

        return !studyLanguageContent.equals(studyLanguageContentBefore) ||
                !tongueLanguageContent.equals(tongueLanguageContentBefore);
    }

    private void saveChanges(View v) {
        if (isDialogChanged() && listener != null) {
            listener.onClick(v, getChangedSubtitle());
            listener.onDismiss(v, null);
        }
        dismiss();
    }

    @Override
    public void onBackPressed() {
        DLog.e(TAG, "onBackPressed()");
        //Don't check back button is clicked twice.
//        if(System.currentTimeMillis()>backKeyPressedTime+2000){
//            backKeyPressedTime = System.currentTimeMillis();
//            ToastUtil.getInstance(getContext()).show("Click back button again to exit");
//        } else {
            if (isDialogChanged()) {
                final YesNoDialog dialog = new YesNoDialog(getContext(), R.string.warning, R.string.msg_save_changes, null, new OnYesNoClickListener() {
                    @Override
                    public void onYesClick(View view, Object object) {
                        saveChanges(binding.ivUpdateOrAdd);
                    }

                    @Override
                    public void onNoClick(View view, Object object) {
                        EditSubtitleDialog.super.onBackPressed();
                    }
                });
                dialog.show();
            } else {
                super.onBackPressed();
            }
            if (listener != null) {
                listener.onDismiss(null, null);
            }
        }
//    }

    @Override
    protected void initOnClickListener() {
        binding.ivUpdateOrAdd.setOnClickListener(this);
        binding.ivLMPlay.setOnClickListener(this);
        binding.ivSTT.setOnClickListener(this);
        binding.ivWebDictionary.setOnClickListener(this);
        binding.ivTranslate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivUpdateOrAdd:
                saveChanges(v);
                break;
            case R.id.ivTranslate:
            case R.id.ivWebDictionary:
                if (listener != null) {
                    listener.onClick(v, getChangedSubtitle());
                }
                break;
            case R.id.ivLMPlay:
                if (listener != null) {
                    listener.onClick(v, null);
                    updatePlayIcon();
                }
                break;
            case R.id.ivSTT:
                if (listener != null) {
                    listener.onClick(v, null);
                    updatePlayIconToPauseImage();
                }
                break;
        }
    }

//    @OnClick({R.id.ivUpdateOrAdd, R.id.ivLMPlay, R.id.ivSTT, R.id.ivWebDictionary, R.id. ivTranslate})
//    void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.ivUpdateOrAdd:
//                saveChanges(v);
//                break;
//            case R.id.ivTranslate:
//            case R.id.ivWebDictionary:
//                if (listener != null) {
//                    listener.onClick(v, getChangedSubtitle());
//                }
//                break;
//            case R.id.ivLMPlay:
//                if (listener != null) {
//                    listener.onClick(v, null);
//                    updatePlayIcon();
//                }
//                break;
//            case R.id.ivSTT:
//                if (listener != null) {
//                    listener.onClick(v, null);
//                    updatePlayIconToPauseImage();
//                }
//                break;
//        }
//    }

}
