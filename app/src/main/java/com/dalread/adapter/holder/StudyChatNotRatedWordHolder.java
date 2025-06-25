package com.dalread.adapter.holder;

import android.content.Context;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemStudyChatNotRatedWordBinding;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.listener.OnVocaStudyChatClickListener;
import com.dalread.model.VocaStudyChat;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;

public class StudyChatNotRatedWordHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Context context;
    private VocaStudyChat voca;
    private OnVocaStudyChatClickListener listener;
    private OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;


    private ItemStudyChatNotRatedWordBinding binding;
    public StudyChatNotRatedWordHolder(ItemStudyChatNotRatedWordBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        binding.ivKnown.setOnClickListener(this);
        binding.ivGrade1.setOnClickListener(this);
        binding.ivGrade2.setOnClickListener(this);
        binding.ivUnknown.setOnClickListener(this);
    }

    public void bind(Context context, VocaStudyChat voca, OnVocaStudyChatClickListener listener, OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow) {
        this.context = context;
        this.voca = voca;
        this.listener = listener;
        this.onDoubleClickListenerOnBaseVocaKnow = onDoubleClickListenerOnBaseVocaKnow;
        bindWord();
    }


    private void bindWord() {
        binding.tvWord.setText(BaseVoca.getVocaDisplay(voca));
        binding.tvWord.setTextColor(ContextCompat.getColor(context, R.color.color_ruby_text_unknown_meaning));
    }


    @Override
    public void onClick(View view) {
        if (onDoubleClickListenerOnBaseVocaKnow != null) {
            onDoubleClickListenerOnBaseVocaKnow.onClick(view, voca);
        }
        if (listener != null) {
            listener.onVocaKnowClick(voca, Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED);
        }
    }
}
