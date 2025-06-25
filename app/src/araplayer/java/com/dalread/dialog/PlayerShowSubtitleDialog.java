package com.dalread.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.databinding.DialogPlayerShowSubtitleDialogBinding;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.util.StringUtils;
import com.dalread.util.Utils;

public class PlayerShowSubtitleDialog extends BasePlayerDialog implements View.OnClickListener, DialogInterface.OnDismissListener {
    private boolean isListenComprehensionMode;
    private boolean isCCRepeatMode;
    private boolean isABRepeatMode;
    private boolean isListeningRecorded;
    private boolean isShowAdvancedMode;
    private Object data;
    private Context context;
    private OnClickDialogListener listener;
    private SUBTITLE_TYPE subtitleType;
    public enum SUBTITLE_TYPE {
        TABLE_VIEW,
        FULL_SCREEN
    }

    private DialogPlayerShowSubtitleDialogBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerShowSubtitleDialogBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

//    @Override
//    protected int getContentViewId() {
//        return R.layout.dialog_player_show_subtitle_dialog;
//    }

    public PlayerShowSubtitleDialog(@NonNull Context context, boolean isShowAdvancedMode, boolean isListenComprehensionMode, boolean isCCRepeatMode, boolean isABRepeatMode, boolean isListeningRecorded, SUBTITLE_TYPE subtitleType, OnClickDialogListener listener) {
        super(context);
        setDialogSizeWider(true);
        setOnDismissListener(this);
        this.context = context;
        this.isShowAdvancedMode = isShowAdvancedMode;
        this.isListenComprehensionMode = isListenComprehensionMode;
        this.isCCRepeatMode = isCCRepeatMode;
        this.isABRepeatMode = isABRepeatMode;
        this.isListeningRecorded = isListeningRecorded;
        this.subtitleType = subtitleType;
        this.listener = listener;
        updateMenuList();

    }

    private void updateMenuList() {
        showAllMenusExceptFixedMenus();
        hideSomeMenus();
        if (subtitleType == SUBTITLE_TYPE.TABLE_VIEW) {
            hideMenusOnTableView();
        }

        if (isListenComprehensionMode || isCCRepeatMode || isABRepeatMode) {
            hideMenusDuringRepeat();
            showMenusDuringRepeat();
        }

        if (isListeningRecorded) {
            hideMenuDeleteRecordingFile();
        }
        hideMenuAdvancedMode();
    }

    public void showOpenHanjaMenu(DicModel data) {
        binding.llOpenAraHanjaWithHanja.setVisibility(View.GONE);
        if (data != null) {
            String subtitle = data.getVocaDisplay();
            if (!Utils.isEmpty(subtitle) && StringUtils.containChineseCharacters(subtitle)) {
                binding.llOpenAraHanjaWithHanja.setVisibility(View.VISIBLE);
            }
        }
    }

    public void showAllMenusExceptFixedMenus() {
        binding.llDictationMode.setVisibility(View.VISIBLE);
        binding.llPlayThisSubtitle.setVisibility(View.VISIBLE);
        binding.llDeleteTheSubtitle.setVisibility(View.VISIBLE);
        binding.llShowNormalPlayingMenuUi.setVisibility(View.VISIBLE);
        binding.llEditThisSubtitle.setVisibility(View.VISIBLE);
    }

    public void hideSomeMenus() {
        binding.llListenComprehensionExit.setVisibility(View.GONE);
        binding.llListenComprehension1StartOver.setVisibility(View.GONE);
    }


    public void hideMenusOnTableView() {
        binding.llShowNormalPlayingMenuUi.setVisibility(View.GONE);
    }

    public void hideMenusDuringRepeat() {
        binding.llListenComprehension1.setVisibility(View.GONE);
        binding.llListenComprehension2.setVisibility(View.GONE);
        binding.llDictationMode.setVisibility(View.GONE);
        binding.llOpenPhraseInformation.setVisibility(View.GONE);
        binding.llDeleteTheSubtitle.setVisibility(View.GONE);
        binding.llMergeWithPreviousSubtitle.setVisibility(View.GONE);
        binding.llMergeWithNextSubtitle.setVisibility(View.GONE);
        binding.llDivideThisSubtitle.setVisibility(View.GONE);
//        binding.llEditThisSubtitle.setVisibility(View.GONE);
        binding.llShowNormalPlayingMenuUi.setVisibility(View.GONE);
        binding.llPlayThisSubtitle.setVisibility(View.GONE); //TODO : Dalnim This doesn't work because updateMenuListByType is called then make this VISISLE again.
    }

    public void showMenusDuringRepeat() {
        binding.llListenComprehensionExit.setVisibility(View.VISIBLE);
        //TODO : 듣기 연습 1 도중에 "듣기 연습1 처음 부터 다시 시작"을 할려고 하는 코드인데 잘 안된다. 일단 메뉴를 안보이게 한다.
//        binding.llListenComprehension1StartOver.setVisibility(View.VISIBLE);
    }

    public void hideMenuDeleteRecordingFile() {
        binding.llDeleteRecordingFile.setVisibility(View.GONE);
    }

    public void hideMenuAdvancedMode() {
        int isVisible = isShowAdvancedMode ? View.VISIBLE : View.GONE;
        binding.llEditThisSubtitle.setVisibility(isVisible);
        binding.llCopyTheSubtitle.setVisibility(isVisible);
//        binding.llListenComprehension2.setVisibility(isVisible);
        binding.llShowNormalPlayingMenuUi.setVisibility(isVisible);
        binding.llDictationMode.setVisibility(isVisible);
        binding.llPlayThisSubtitle.setVisibility(isVisible);
        binding.llMergeWithNextSubtitle.setVisibility(isVisible);
        binding.llMergeWithPreviousSubtitle.setVisibility(isVisible);
        binding.llDeleteTheSubtitle.setVisibility(isVisible);
        binding.llDivideThisSubtitle.setVisibility(isVisible);
        binding.llChangeSubtitleKnownStatus.setVisibility(isVisible);
    }

    public void setData(DicModel data) {
        this.data = data;
        if (TextUtils.isEmpty(data.getRecordedPath())) {
            hideMenuDeleteRecordingFile();
        }

        showOpenHanjaMenu(data);
    }

    @Override
    protected void initOnClickListener() {
        binding.llChangeWordKnownStatus.setOnClickListener(this);
        binding.llChangeSubtitleKnownStatus.setOnClickListener(this);
        binding.llShowNormalPlayingMenuUi.setOnClickListener(this);
        binding.llPlayThisSubtitle.setOnClickListener(this);
        binding.llDictationMode.setOnClickListener(this);
        binding.llListenComprehensionExit.setOnClickListener(this);
        binding.llListenComprehension1StartOver.setOnClickListener(this);
        binding.llListenComprehension1.setOnClickListener(this);
        binding.llListenComprehension2.setOnClickListener(this);
        binding.llCopyTheSubtitle.setOnClickListener(this);
        binding.llGptWithThisSubtitle.setOnClickListener(this);
        binding.llDeleteTheSubtitle.setOnClickListener(this);
        binding.llWebDicationary.setOnClickListener(this);
        binding.llTranslateThisSubtitle.setOnClickListener(this);
        binding.llOpenPhraseInformation.setOnClickListener(this);
        binding.llEditThisSubtitle.setOnClickListener(this);
        binding.llMergeWithPreviousSubtitle.setOnClickListener(this);
        binding.llMergeWithNextSubtitle.setOnClickListener(this);
        binding.llDivideThisSubtitle.setOnClickListener(this);
        binding.llDeleteRecordingFile.setOnClickListener(this);
        binding.llDeleteRecordingFile.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llChangeWordKnownStatus:
            case R.id.llChangeSubtitleKnownStatus:
            case R.id.llShowNormalPlayingMenuUi:
            case R.id.llEditThisSubtitle:
            case R.id.llCopyTheSubtitle:
            case R.id.llGptWithThisSubtitle:
            case R.id.llDeleteTheSubtitle:
            case R.id.llOpenPhraseInformation:
            case R.id.llDictationMode:
            case R.id.llListenComprehension2:
            case R.id.llListenComprehensionExit:
            case R.id.llListenComprehension1StartOver:
            case R.id.llWebDicationary:
            case R.id.llTranslateThisSubtitle:
            case R.id.llDeleteRecordingFile:
            case R.id.llMergeWithPreviousSubtitle:
            case R.id.llMergeWithNextSubtitle:
            case R.id.llDivideThisSubtitle:
            case R.id.llOpenAraHanjaWithHanja:
                this.view = v;
                break;
            default:
                this.view = null;
                break;
        }
        dismiss();
        if (listener != null) {
            listener.onClick(v, data);
        }
    }

//
//    @OnClick({
//            R.id.llChangeWordKnownStatus, R.id.llChangeSubtitleKnownStatus, R.id.llShowNormalPlayingMenuUi,
//            R.id.llPlayThisSubtitle, R.id.llDictationMode, R.id.llListenComprehensionExit, R.id.llListenComprehension1StartOver,
//            R.id.llListenComprehension1, R.id.llListenComprehension2,
//            R.id.llCopyTheSubtitle, R.id.llGptWithThisSubtitle, R.id.llDeleteTheSubtitle,
//            R.id.llWebDicationary, R.id.llTranslateThisSubtitle,
//            R.id.llOpenPhraseInformation, R.id.llEditThisSubtitle, R.id.llMergeWithPreviousSubtitle, R.id.llMergeWithNextSubtitle,
//            R.id.llDivideThisSubtitle, R.id.llDeleteRecordingFile, R.id.llOpenAraHanjaWithHanja, R.id.tv_cancel})
//    void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.llChangeWordKnownStatus:
//            case R.id.llChangeSubtitleKnownStatus:
//            case R.id.llShowNormalPlayingMenuUi:
//            case R.id.llEditThisSubtitle:
//            case R.id.llCopyTheSubtitle:
//            case R.id.llGptWithThisSubtitle:
//            case R.id.llDeleteTheSubtitle:
//            case R.id.llOpenPhraseInformation:
//            case R.id.llDictationMode:
//            case R.id.llListenComprehension2:
//            case R.id.llListenComprehensionExit:
//            case R.id.llListenComprehension1StartOver:
//            case R.id.llWebDicationary:
//            case R.id.llTranslateThisSubtitle:
//            case R.id.llDeleteRecordingFile:
//            case R.id.llMergeWithPreviousSubtitle:
//            case R.id.llMergeWithNextSubtitle:
//            case R.id.llDivideThisSubtitle:
//            case R.id.llOpenAraHanjaWithHanja:
//                this.view = view;
//                break;
//            default:
//                this.view = null;
//                break;
//        }
//        dismiss();
//        if (listener != null) {
//            listener.onClick(view, data);
//        }
//    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (listener != null) {
            listener.onDismiss(view, -1);
        }
    }
}
