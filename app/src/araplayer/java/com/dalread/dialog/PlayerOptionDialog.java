package com.dalread.dialog;

import android.content.DialogInterface;
import android.view.View;

import com.dalread.R;
import com.dalread.base.BasePlayerActivity;
import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerOptionInScrollableMenuBinding;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.Constant;
import com.dalread.util.RepeatUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

public class PlayerOptionDialog extends BasePlayerDialog implements View.OnClickListener, DialogInterface.OnDismissListener {
    private PlayerFileModel playerFileModel;
    private SingleChoiceDialog singleChoiceDialog;
    private BasePlayerActivity activity;
    private OnClickDialogListener listener;
    private boolean isSkipPlayingNoSubtitlePart;
    private DialogPlayerOptionInScrollableMenuBinding binding;
    private boolean isRightHandMode = true;

    @Override
    protected View getContentView() {
        binding = DialogPlayerOptionInScrollableMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public PlayerOptionDialog(BasePlayerActivity activity, PlayerFileModel playerFileModel, OnClickDialogListener listener) {
        super(activity);
        setDialogSizeWider(true);
        this.activity = activity;
        this.playerFileModel = playerFileModel;
        this.listener = listener;
        setOnDismissListener(this);

        singleChoiceDialog = new SingleChoiceDialog(activity);

        updateMenusVisibility(playerFileModel);
        setValue();
        setIsSkipPlayingNoSubtitlePart(false);
    }

    private void updateMenusVisibility(PlayerFileModel playerFileModel) {
        if (playerFileModel == null || (Utils.isEmpty(playerFileModel.getSubPath1()) && Utils.isEmpty(playerFileModel.getSubPath2()))) {
            binding.llPlayBeforeASubtitle.setVisibility(View.GONE);
            binding.llPlayAfterASubtitle.setVisibility(View.GONE);
            binding.llKeepPlayingBetweenSubtitle.setVisibility(View.GONE);
        } else {
            binding.llPlayBeforeASubtitle.setVisibility(View.VISIBLE);
            binding.llPlayAfterASubtitle.setVisibility(View.VISIBLE);
            binding.llKeepPlayingBetweenSubtitle.setVisibility(View.VISIBLE);
        }
    }

    private void setValue() {
        binding.tvPlayBeforeASubtitle.setText(RepeatUtil.getMinRepeatCountFormat(activity, playerFileModel.getVideoModel().getPlayBeforeSubtitle()));
        binding.tvPlayAfterASubtitle.setText(RepeatUtil.getMinRepeatCountFormat(activity, playerFileModel.getVideoModel().getPlayAfterSubtitle()));
        binding.tvKeepPlayingBetweenSubtitle.setText(RepeatUtil.getMinRepeatCountFormat(activity, playerFileModel.getVideoModel().getKeepPlayBetweenSubtitle()));
    }

    @Override
    protected void initOnClickListener() {
        binding.llPlayBeforeASubtitle.setOnClickListener(this);
        binding.llPlayAfterASubtitle.setOnClickListener(this);
        binding.llKeepPlayingBetweenSubtitle.setOnClickListener(this);
        binding.scSkipPlayingNoSubtitlePart.setOnClickListener(this);
        binding.tvOK.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llPlayBeforeASubtitle:
            case R.id.llPlayAfterASubtitle:
            case R.id.llKeepPlayingBetweenSubtitle:
                openChangePlayMinSubtitle(v.getId());
                break;
            case R.id.scSkipPlayingNoSubtitlePart:
                isSkipPlayingNoSubtitlePart = !isSkipPlayingNoSubtitlePart;
                listener.onClick(v, isSkipPlayingNoSubtitlePart);
                break;
            case R.id.tvOK:
                dismiss();
                break;
        }
    }

//    @OnClick({
//            R.id.llPlayBeforeASubtitle, R.id.llPlayAfterASubtitle, R.id.llKeepPlayingBetweenSubtitle, R.id.scSkipPlayingNoSubtitlePart,
//            R.id.tvOK})
//    void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.llPlayBeforeASubtitle:
//            case R.id.llPlayAfterASubtitle:
//            case R.id.llKeepPlayingBetweenSubtitle:
//                openChangePlayMinSubtitle(v.getId());
//                break;
//            case R.id.scSkipPlayingNoSubtitlePart:
//                isSkipPlayingNoSubtitlePart = !isSkipPlayingNoSubtitlePart;
//                listener.onClick(v, isSkipPlayingNoSubtitlePart);
//                break;
//            case R.id.tvOK:
//                dismiss();
//                break;
//        }
//    }

    private void openChangePlayMinSubtitle(int viewId) {
        float count = 0;
        switch (viewId) {
            case R.id.llPlayBeforeASubtitle:
                count = playerFileModel.getVideoModel().getPlayBeforeSubtitle();
                break;
            case R.id.llPlayAfterASubtitle:
                count = playerFileModel.getVideoModel().getPlayAfterSubtitle();
                break;
            case R.id.llKeepPlayingBetweenSubtitle:
                count = playerFileModel.getVideoModel().getKeepPlayBetweenSubtitle();
                break;
        }
        int index = (int) (count * Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_INDEX) + Constant.READ_COUNT_MAX;
        final String[] readCountValues = Voca.getRepeatCountFloatValues();
        singleChoiceDialog.setRightHandMode(isRightHandMode);
        singleChoiceDialog.show(
                R.string.time_second_title,
                readCountValues,
                index,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        int which = (int) object;
                        if (index == which) return;
                        updateUIPlayTimeRepetition(viewId, which);
                    }

                    @Override
                    public void onDismiss(View view, Object object) {
                    }
                });
    }

    private void updateUIPlayTimeRepetition(int viewId, int index) {
        float value = (float) (index - Constant.READ_COUNT_MAX) / Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_INDEX;
        switch (viewId) {
            case R.id.llPlayBeforeASubtitle:
                playerFileModel.getVideoModel().setPlayBeforeSubtitle(value);
                binding.tvPlayBeforeASubtitle.setText(RepeatUtil.getMinRepeatCountFormat(activity, playerFileModel.getVideoModel().getPlayBeforeSubtitle()));
                break;
            case R.id.llPlayAfterASubtitle:
                playerFileModel.getVideoModel().setPlayAfterSubtitle(value);
                binding.tvPlayAfterASubtitle.setText(RepeatUtil.getMinRepeatCountFormat(activity, playerFileModel.getVideoModel().getPlayAfterSubtitle()));
                break;
            default:
                playerFileModel.getVideoModel().setKeepPlayBetweenSubtitle(value);
                binding.tvKeepPlayingBetweenSubtitle.setText(RepeatUtil.getMinRepeatCountFormat(activity, playerFileModel.getVideoModel().getKeepPlayBetweenSubtitle()));
                break;
        }
        activity.updateVideoModel(playerFileModel.getVideoModel());
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (listener != null) {
            listener.onDismiss(view, -1);
        }
    }

    public void setRightHandMode(boolean rightHandMode) {
        isRightHandMode = rightHandMode;
    }
    public void setIsSkipPlayingNoSubtitlePart(boolean isSkipPlayingNoSubtitlePart) {
        this.isSkipPlayingNoSubtitlePart = isSkipPlayingNoSubtitlePart;
        binding.scSkipPlayingNoSubtitlePart.setChecked(isSkipPlayingNoSubtitlePart);
    }
}
