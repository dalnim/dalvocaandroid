package com.dalread.dialog;

import android.content.DialogInterface;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dalread.R;
import com.dalread.activity.MultiplePlayerActivity;
import com.dalread.base.BasePlayerDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.DialogMultiPlayerSettingBinding;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.Constant;
import com.dalread.util.ViewAnimatorUtil;

public class MultiPlayerSettingDialog extends BasePlayerDialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, DialogInterface.OnDismissListener {
    private PlayerFileModel playerFileModel;
    private SingleChoiceDialog singleChoiceDialog;
    private MultiplePlayerActivity activity;
    private OnClickDialogListener listener;
    private boolean showAdvancedMode;
    private boolean randomPlayMode;
    private DialogMultiPlayerSettingBinding binding;

    private String[] tapForBackWardRange;
    private int swipeForwardBackwardValue, swipeForwardBackwardIndex;
    private int tapForBackWardValue, tapForBackWardIndex;

    private SharedPreferencesDB sharedPreferences;
    private boolean hide4ButtonsOnPlayingScreen;
    private boolean isInInitMethod;
    @Override
    protected View getContentView() {
        binding = DialogMultiPlayerSettingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public MultiPlayerSettingDialog(MultiplePlayerActivity activity, OnClickDialogListener listener) {
        super(activity);
        setDialogSizeWider(true);
        this.activity = activity;
        this.listener = listener;
        isInInitMethod = true;
        sharedPreferences = SharedPreferencesDB.getInstance(activity);
        showAdvancedMode = sharedPreferences.isShowAdvancedMode();
        binding.scShowAdvancedMode.setChecked(showAdvancedMode);
        randomPlayMode = sharedPreferences.isRandomPlayMode();
        binding.scRandomPlayMode.setChecked(randomPlayMode);
        setOnDismissListener(this);
        singleChoiceDialog = new SingleChoiceDialog(activity);

        binding.llSwipeForwardBackward.setVisibility(View.GONE);
        binding.llTapOnForwardBackward.setVisibility(View.GONE);
        setValue();
        isInInitMethod = false;
    }


    private void setValue() {
        hide4ButtonsOnPlayingScreen = sharedPreferences.getHide4ButtonsOnPlayingScreen();
        binding.scHide4Buttons.setChecked(hide4ButtonsOnPlayingScreen);

        updateTextHiddenButtonsTransparencyOnFullScreen(mSharedPref.getHiddenButtonsTransparencyOnFullScreen());
        setForBackWardValue();
    }

    private void setForBackWardValue() {
        tapForBackWardRange = Constant.PLAYER.SETTING.TAP_FOR_BACK_WARD_RANGE;
        swipeForwardBackwardValue = mSharedPref.getPlayerSwipeForBackWard();
        swipeForwardBackwardIndex = mSharedPref.getPlayerSwipeForBackWardIndex();
        tapForBackWardValue = mSharedPref.getPlayerTapForBackWard();
        tapForBackWardIndex = mSharedPref.getPlayerTapForBackWardIndex();
        updateUISwipeForBackWard();
        updateUITapForBackWard();
    }

    @Override
    protected void initOnClickListener() {
        binding.llSwipeForwardBackward.setOnClickListener(this);
        binding.llTapOnForwardBackward.setOnClickListener(this);
        binding.llHiddenButtonsTransparencyOnFullScreen.setOnClickListener(this);
        binding.llShowAdvancedMode.setOnClickListener(this);
        binding.tvClose.setOnClickListener(this);

        binding.scHide4Buttons.setOnCheckedChangeListener(this);
        binding.scShowAdvancedMode.setOnCheckedChangeListener(this);
        binding.scRandomPlayMode.setOnCheckedChangeListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llSwipeForwardBackward:
            case R.id.llTapOnForwardBackward:
                openChangeForBackWardMove(v.getId());
                break;
            case R.id.llHiddenButtonsTransparencyOnFullScreen:
                ViewAnimatorUtil.openChangeHiddenButtonsTransparency(activity, newTransparencyValue -> {
                    updateTextHiddenButtonsTransparencyOnFullScreen(newTransparencyValue);
                    activity.refresh4ButtonsTransparency();
                });
                break;

            case R.id.tvClose:
                dismiss();
                break;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.scHide4Buttons:
                sharedPreferences.setHide4ButtonsOnPlayingScreen(hide4ButtonsOnPlayingScreen = isChecked);
                if (hide4ButtonsOnPlayingScreen == false) {
                    activity.refresh4ButtonsTransparency();
                } else {
                    activity.hideAllVideos4Buttons();
                }
                break;
            case R.id.scShowAdvancedMode:
                sharedPreferences.setShowAdvancedMode(showAdvancedMode = isChecked);
                if (isChecked && !isInInitMethod) {
                    showAdvancedModeDialog();
                }
                break;
            case R.id.scRandomPlayMode:
                sharedPreferences.setRandomPlayMode(randomPlayMode = isChecked);
                break;
        }
    }
    private void showAdvancedModeDialog() {
        AlertDialog alertDialog = new AlertDialog(activity);
        alertDialog.removeAnimation();
        alertDialog.show(R.string.msg_info_multiplayer_advanced_mode, R.string.ok, null);
    }
    private void openChangeForBackWardMove(int viewId) {
        int index = viewId == R.id.llSwipeForwardBackward ? swipeForwardBackwardIndex : tapForBackWardIndex;
        singleChoiceDialog.show(
                R.string.time_second_title,
                tapForBackWardRange,
                index,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        int which = (int) object;
                        if (index == which) return;
                        updateForBackWardMove(viewId, which);
                    }

                    @Override
                    public void onDismiss(View view, Object object) {
                    }
                });
    }

    private void updateForBackWardMove(int viewId, int index) {
//        int value = Integer.parseInt(tapForBackWardRange[index]);
//        switch (viewId) {
//            case R.id.llSwipeForwardBackward:
//                mSharedPref.setPlayerSwipeForBackWard(swipeForwardBackwardValue = value);
//                mSharedPref.setPlayerSwipeForBackWardIndex(swipeForwardBackwardIndex = index);
//                binding.tvSwipeForwardBackward.setText(activity.getString(R.string.format_second_int, value));
//                updateUISwipeForBackWard();
//                break;
//            case R.id.llTapOnForwardBackward:
//                mSharedPref.setPlayerTapForBackWard(tapForBackWardValue = value);
//                mSharedPref.setPlayerTapForBackWardIndex(tapForBackWardIndex = index);
//                binding.tvTapOnForwardBackward.setText(activity.getString(R.string.format_second_int, value));
//                updateUITapForBackWard();
//                break;
//        }
    }

    private void updateTextHiddenButtonsTransparencyOnFullScreen(Integer value) {
        binding.tvHiddenButtonsTransparencyOnFullScreen.setText(value + "%");
    }


    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (listener != null) {
            listener.onDismiss(view, -1);
        }
    }

    private void updateUISwipeForBackWard() {
        activity.runOnUiThread(() -> {
            generateTextView(binding.tvSwipeForwardBackward, swipeForwardBackwardValue);
        });
    }

    private void updateUITapForBackWard() {
        activity.runOnUiThread(() -> {
            generateTextView(binding.tvTapOnForwardBackward, tapForBackWardValue);
        });
    }

    private void generateTextView(TextView tv, int value) {
        tv.setText(getContext().getString(value > 1 ? R.string.seconds_format : R.string.second_format, value));
    }
}
