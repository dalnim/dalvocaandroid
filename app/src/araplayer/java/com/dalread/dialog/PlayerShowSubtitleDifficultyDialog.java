package com.dalread.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerShowSubtitleDialogDifficultyBinding;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.util.Constant;
import com.dalread.util.Voca;

public class PlayerShowSubtitleDifficultyDialog extends BasePlayerDialog implements View.OnClickListener, DialogInterface.OnDismissListener {

//    @BindView(R.id.sc_1st_amki_grade_subtitles) SwitchCompat sc1stAmki;
//    @BindView(R.id.sc_2nd_amki_grade_subtitles) SwitchCompat sc2ndAmki;
//    @BindView(R.id.sc_difficult_subtitles) SwitchCompat scDifficult;
//    @BindView(R.id.sc_known_subtitles) SwitchCompat scKnown;
//    @BindView(R.id.sc_subtitles_that_needs_to_practice_pronunciation) SwitchCompat scSubtitles;

    private OnClickDialogListener listener;
    private int[] difficultArray;

    private DialogPlayerShowSubtitleDialogDifficultyBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerShowSubtitleDialogDifficultyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

//    @Override
//    protected int getContentViewId() {
//        return R.layout.dialog_player_show_subtitle_dialog_difficulty;
//    }

    public PlayerShowSubtitleDifficultyDialog(@NonNull Context context, int[] array, OnClickDialogListener listener) {
        super(context);
        this.difficultArray = array;
        this.listener = listener;
        setOnDismissListener(this);
        Voca.setSwitchCompatSelected(binding.sc1stAmkiGradeSubtitles, difficultArray[Constant.PLAYER.SUB_TITLE.DIFFICULT.ALL_1ST_AMKI]);
        Voca.setSwitchCompatSelected(binding.sc2ndAmkiGradeSubtitles, difficultArray[Constant.PLAYER.SUB_TITLE.DIFFICULT.ALL_2ND_AMKI]);
        Voca.setSwitchCompatSelected(binding.scDifficultSubtitles, difficultArray[Constant.PLAYER.SUB_TITLE.DIFFICULT.ALL_DIFFICULT]);
        Voca.setSwitchCompatSelected(binding.scKnownSubtitles, difficultArray[Constant.PLAYER.SUB_TITLE.DIFFICULT.ALL_DIFFICULT_WITHOUT]);
        Voca.setSwitchCompatSelected(binding.scSubtitlesThatNeedsToPracticePronunciation, difficultArray[Constant.PLAYER.SUB_TITLE.DIFFICULT.ALL_DIFFICULT_PRONUNCIATION]);
    }

    @Override
    protected void initOnClickListener() {
        binding.tvOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            difficultArray[Constant.PLAYER.SUB_TITLE.DIFFICULT.ALL_1ST_AMKI] = Voca.getSwitchCompatStatusByPlayDifficult(binding.sc1stAmkiGradeSubtitles);
            difficultArray[Constant.PLAYER.SUB_TITLE.DIFFICULT.ALL_2ND_AMKI] = Voca.getSwitchCompatStatusByPlayDifficult(binding.sc2ndAmkiGradeSubtitles);
            difficultArray[Constant.PLAYER.SUB_TITLE.DIFFICULT.ALL_DIFFICULT] = Voca.getSwitchCompatStatusByPlayDifficult(binding.scDifficultSubtitles);
            difficultArray[Constant.PLAYER.SUB_TITLE.DIFFICULT.ALL_DIFFICULT_WITHOUT] = Voca.getSwitchCompatStatusByPlayDifficult(binding.scKnownSubtitles);
            difficultArray[Constant.PLAYER.SUB_TITLE.DIFFICULT.ALL_DIFFICULT_PRONUNCIATION] = Voca.getSwitchCompatStatusByPlayDifficult(binding.scSubtitlesThatNeedsToPracticePronunciation);
            listener.onClick(v, difficultArray);
        }
    }


//    @OnClick(R.id.tv_ok)
//    void onClick(View view) {
//        dismiss();
//        if (listener != null) {
//            difficultArray[Constant.PLAYER.SUB_TITLE.DIFFICULT.ALL_1ST_AMKI] = Voca.getSwitchCompatStatusByPlayDifficult(binding.sc1stAmkiGradeSubtitles);
//            difficultArray[Constant.PLAYER.SUB_TITLE.DIFFICULT.ALL_2ND_AMKI] = Voca.getSwitchCompatStatusByPlayDifficult(binding.sc2ndAmkiGradeSubtitles);
//            difficultArray[Constant.PLAYER.SUB_TITLE.DIFFICULT.ALL_DIFFICULT] = Voca.getSwitchCompatStatusByPlayDifficult(binding.scDifficultSubtitles);
//            difficultArray[Constant.PLAYER.SUB_TITLE.DIFFICULT.ALL_DIFFICULT_WITHOUT] = Voca.getSwitchCompatStatusByPlayDifficult(binding.scKnownSubtitles);
//            difficultArray[Constant.PLAYER.SUB_TITLE.DIFFICULT.ALL_DIFFICULT_PRONUNCIATION] = Voca.getSwitchCompatStatusByPlayDifficult(binding.scSubtitlesThatNeedsToPracticePronunciation);
//            listener.onClick(view, difficultArray);
//        }
//    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (listener != null) {
            listener.onDismiss(view, -1);
        }
    }
}
