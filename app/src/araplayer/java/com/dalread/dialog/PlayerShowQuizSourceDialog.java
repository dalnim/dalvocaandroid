package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerShowQuizSourceBinding;

public class PlayerShowQuizSourceDialog extends BasePlayerDialog implements View.OnClickListener {

    private com.dalread.listener.OnClickListener listener;
    private DialogPlayerShowQuizSourceBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerShowQuizSourceBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

//    @Override
//    protected int getContentViewId() {
//        return R.layout.dialog_player_show_quiz_source;
//    }

    public PlayerShowQuizSourceDialog(@NonNull Context context, com.dalread.listener.OnClickListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void initOnClickListener() {
        binding.tvSolveAQuiz.setOnClickListener(this);
        binding.tvWordList.setOnClickListener(this);
        binding.tvSubtitleList.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            listener.onClick(v, null);
        }
    }

//
//    @OnClick({R.id.tv_solve_a_quiz, R.id.tv_word_list, R.id.tv_subtitle_list, R.id.tv_cancel})
//    void onClick(View view) {
//        dismiss();
//        if (listener != null) {
//            listener.onClick(view, null);
//        }
//    }
}
