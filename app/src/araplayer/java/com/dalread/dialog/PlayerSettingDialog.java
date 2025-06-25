package com.dalread.dialog;

import android.content.DialogInterface;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.dalread.R;
import com.dalread.base.BasePlayerActivity;
import com.dalread.base.BasePlayerDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.DialogPlayerSettingInScrollableMenuBinding;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.Constant;
import com.dalread.util.FileUtil;
import com.dalread.util.RepeatUtil;
import com.dalread.util.Utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlayerSettingDialog extends BasePlayerDialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, DialogInterface.OnDismissListener {
    private PlayerFileModel playerFileModel;
    private SingleChoiceDialog singleChoiceDialog;
    private BasePlayerActivity activity;
    private OnClickDialogListener listener;

    private enum TransparencyType {HIDDEN_BUTTONS, SUBTITLE_VIEW};

    private DialogPlayerSettingInScrollableMenuBinding binding;
    private boolean isRightHandMode = true;
    private String[] tapForBackWardRange;
    private int swipeForwardBackwardValue, swipeForwardBackwardIndex;
    private int tapForBackWardValue, tapForBackWardIndex;
    private boolean displayStartEndTimeInSubtitleView;
    private boolean playMusicBetweenLyricsOnly;
    private boolean playAllDifficultWordsBeforePlayingMusic;
    private boolean playTitleByTtsBeforePlaying;
    private boolean includeMotherTongueSubtitle;

    private SharedPreferencesDB sharedPreferences;

    @Override
    protected View getContentView() {
        binding = DialogPlayerSettingInScrollableMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public PlayerSettingDialog(BasePlayerActivity activity, PlayerFileModel playerFileModel, OnClickDialogListener listener) {
        super(activity);
        setDialogSizeWider(true);
        this.activity = activity;
        this.playerFileModel = playerFileModel;
        this.listener = listener;
        setOnDismissListener(this);

        singleChoiceDialog = new SingleChoiceDialog(activity);

        sharedPreferences = SharedPreferencesDB.getInstance(getContext());
        updateMenusVisibility(playerFileModel);
        setValue();
        updateMenusLabel();
    }

    private void updateMenusLabel() {
        if (FileUtil.isVideoApp()) {
            binding.scPlayMusicBetweenLyricsOnly.setText(R.string.play_video_between_subtitles_only);
        } else {
            binding.scPlayMusicBetweenLyricsOnly.setText(R.string.play_music_between_lyrics_only);
        }
    }

    private void updateMenusVisibility(PlayerFileModel playerFileModel) {
        if (playerFileModel == null || (Utils.isEmpty(playerFileModel.getSubPath1()) && Utils.isEmpty(playerFileModel.getSubPath2()))) {
        } else {
        }

        if (FileUtil.isVideoApp()) {
            binding.llPlayAllDifficultWordBeforePlayingMusic.setVisibility(View.GONE);
            binding.llPlayTitleByTtsBeforePlaying.setVisibility(View.GONE);
        } else {
            binding.llPlayAllDifficultWordBeforePlayingMusic.setVisibility(View.VISIBLE);
            binding.llPlayTitleByTtsBeforePlaying.setVisibility(View.VISIBLE);
        }
        //Or use this
//        binding.llPlayTitleByTtsBeforePlaying.setVisibility(FileUtil.isAudioFormat(playerFileModel.getVideoModel().getName()) ? View.VISIBLE : View.GONE);
    }

    private void setValue() {
        displayStartEndTimeInSubtitleView = sharedPreferences.getDisplayStartEndTimeInSubtitleView();
        binding.scDisplayStartEndTime.setChecked(displayStartEndTimeInSubtitleView);

        playMusicBetweenLyricsOnly = sharedPreferences.getPlayMusicBetweenLyricsOnly();
        binding.scPlayMusicBetweenLyricsOnly.setChecked(playMusicBetweenLyricsOnly);

        playTitleByTtsBeforePlaying = sharedPreferences.getPlayTitleByTtsBeforePlaying();
        binding.scPlayTitleByTtsBeforePlaying.setChecked(playTitleByTtsBeforePlaying);

        playAllDifficultWordsBeforePlayingMusic = sharedPreferences.getPlayAllDifficultWordsBeforePlayingMusic();
        binding.scPlayAllDifficultWordBeforePlayingMusic.setChecked(playAllDifficultWordsBeforePlayingMusic);

        updateTextHiddenButtonsTransparencyOnFullScreen(mSharedPref.getHiddenButtonsTransparencyOnFullScreen());
        updateTextSubtitleViewTransparencyOnFullScreen(mSharedPref.getSubtitleViewTransparencyOnFullScreen());
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
        binding.llSubtitleViewTransparencyOnFullScreen.setOnClickListener(this);
        binding.tvOK.setOnClickListener(this);

        binding.scDisplayStartEndTime.setOnCheckedChangeListener(this);
        binding.scPlayMusicBetweenLyricsOnly.setOnCheckedChangeListener(this);
        binding.scDisplayStartEndTime.setOnCheckedChangeListener(this);
        binding.scPlayAllDifficultWordBeforePlayingMusic.setOnCheckedChangeListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llSwipeForwardBackward:
            case R.id.llTapOnForwardBackward:
                openChangeForBackWardMove(v.getId());
                break;
            case R.id.llHiddenButtonsTransparencyOnFullScreen:
                openChangeHiddenButtonsTransparency(TransparencyType.HIDDEN_BUTTONS);
                break;
            case R.id.llSubtitleViewTransparencyOnFullScreen:
                openChangeHiddenButtonsTransparency(TransparencyType.SUBTITLE_VIEW);
                break;
            case R.id.tvOK:
                dismiss();
                break;
        }
    }


//    @OnClick({
//            R.id.llSwipeForwardBackward, R.id.llTapOnForwardBackward,
//            R.id.llHiddenButtonsTransparencyOnFullScreen, R.id.llSubtitleViewTransparencyOnFullScreen, R.id.tvOK})
//    void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.llSwipeForwardBackward:
//            case R.id.llTapOnForwardBackward:
//                openChangeForBackWardMove(v.getId());
//                break;
//            case R.id.llHiddenButtonsTransparencyOnFullScreen:
//                openChangeHiddenButtonsTransparency(TransparencyType.HIDDEN_BUTTONS);
//                break;
//            case R.id.llSubtitleViewTransparencyOnFullScreen:
//                openChangeHiddenButtonsTransparency(TransparencyType.SUBTITLE_VIEW);
//                break;
//            case R.id.tvOK:
//                dismiss();
//                break;
//        }
//    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.scDisplayStartEndTime:
                sharedPreferences.setDisplayStartEndTimeInSubtitleView(displayStartEndTimeInSubtitleView = isChecked);
                break;
            case R.id.scPlayMusicBetweenLyricsOnly:
                sharedPreferences.setPlayMusicBetweenLyricsOnly(playMusicBetweenLyricsOnly = isChecked);
                break;
            case R.id.scPlayTitleByTtsBeforePlaying:
                sharedPreferences.setPlayTitleByTtsBeforePlaying(playTitleByTtsBeforePlaying = isChecked);
                break;
            case R.id.scIncludeMotherTongueSubtitle:
                sharedPreferences.setIncludeMotherTongueSubtitle(includeMotherTongueSubtitle = isChecked);
                break;
            case R.id.scPlayAllDifficultWordBeforePlayingMusic:
                sharedPreferences.setPlayAllDifficultWordsBeforePlayingMusic(playAllDifficultWordsBeforePlayingMusic = isChecked);
                break;
        }
    }

//    @OnCheckedChanged({
//            R.id.scDisplayStartEndTime, R.id.scPlayMusicBetweenLyricsOnly,
//            R.id.scPlayTitleByTtsBeforePlaying, R.id.scPlayAllDifficultWordBeforePlayingMusic
//    })
//    void onCheckedChanged(CompoundButton button, boolean checked) {
//        int id = button.getId();
//        switch (id) {
//            case R.id.scDisplayStartEndTime:
//                sharedPreferences.setDisplayStartEndTimeInSubtitleView(displayStartEndTimeInSubtitleView = checked);
//                break;
//            case R.id.scPlayMusicBetweenLyricsOnly:
//                sharedPreferences.setPlayMusicBetweenLyricsOnly(playMusicBetweenLyricsOnly = checked);
//                break;
//            case R.id.scPlayTitleByTtsBeforePlaying:
//                sharedPreferences.setPlayTitleByTtsBeforePlaying(playTitleByTtsBeforePlaying = checked);
//                break;
//            case R.id.scIncludeMotherTongueSubtitle:
//                sharedPreferences.setIncludeMotherTongueSubtitle(includeMotherTongueSubtitle = checked);
//                break;
//            case R.id.scPlayAllDifficultWordBeforePlayingMusic:
//                sharedPreferences.setPlayAllDifficultWordsBeforePlayingMusic(playAllDifficultWordsBeforePlayingMusic = checked);
//                break;
//        }
//    }

    private void openChangeForBackWardMove(int viewId) {
        int index = viewId == R.id.llSwipeForwardBackward ? swipeForwardBackwardIndex : tapForBackWardIndex;
        singleChoiceDialog.setRightHandMode(isRightHandMode);
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
        int value = Integer.parseInt(tapForBackWardRange[index]);
        switch (viewId) {
            case R.id.llSwipeForwardBackward:
                mSharedPref.setPlayerSwipeForBackWard(swipeForwardBackwardValue = value);
                mSharedPref.setPlayerSwipeForBackWardIndex(swipeForwardBackwardIndex = index);
                binding.tvSwipeForwardBackward.setText(RepeatUtil.getMinRepeatCountFormat(activity, playerFileModel.getVideoModel().getPlayBeforeSubtitle()));
                updateUISwipeForBackWard();
                break;
            case R.id.llTapOnForwardBackward:
                mSharedPref.setPlayerTapForBackWard(tapForBackWardValue = value);
                mSharedPref.setPlayerTapForBackWardIndex(tapForBackWardIndex = index);
                binding.tvTapOnForwardBackward.setText(RepeatUtil.getMinRepeatCountFormat(activity, playerFileModel.getVideoModel().getPlayAfterSubtitle()));
                updateUITapForBackWard();
                break;

        }
    }

    private void openChangeHiddenButtonsTransparency(TransparencyType transparencyType) {
        List<String> listTransparencyValues = IntStream.rangeClosed(0, 100)
                .boxed()
                .map(e -> e.toString() + "%")
                .collect(Collectors.toList());
        String[] arrayTransparencyValues = listTransparencyValues.stream().toArray(String[]::new);
        int index = isTransparencyTypeSubtitleView(transparencyType) ? mSharedPref.getSubtitleViewTransparencyOnFullScreen() : mSharedPref.getHiddenButtonsTransparencyOnFullScreen();
        @StringRes int titleId = isTransparencyTypeSubtitleView(transparencyType) ? R.string.transparency_subtitle_view : R.string.transparency_hidden_buttons_full_screen;
        singleChoiceDialog.setRightHandMode(isRightHandMode);
        singleChoiceDialog.show(
                titleId,
                arrayTransparencyValues,
                index,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        Integer which = (Integer) object;
                        if (index == which) return;
                        if (transparencyType == TransparencyType.SUBTITLE_VIEW) {
                            mSharedPref.setSubtitleViewTransparencyOnFullScreen(which);
                            updateTextSubtitleViewTransparencyOnFullScreen(which);
                        } else {
                            mSharedPref.setHiddenButtonsTransparencyOnFullScreen(which);
                            updateTextHiddenButtonsTransparencyOnFullScreen(which);
                        }
                        dismiss();
                    }

                    @Override
                    public void onDismiss(View view, Object object) {
                    }
                });
    }

    private void updateTextHiddenButtonsTransparencyOnFullScreen(Integer value) {
        binding.tvHiddenButtonsTransparencyOnFullScreen.setText(value + "%");
    }

    private void updateTextSubtitleViewTransparencyOnFullScreen(Integer value) {
        binding.tvSubtitleViewTransparencyOnFullScreen.setText(value + "%");
    }

    private boolean isTransparencyTypeSubtitleView(TransparencyType transparencyType) {
        return transparencyType == TransparencyType.SUBTITLE_VIEW;
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
