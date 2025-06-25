package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerDialogueListBinding;

import butterknife.ButterKnife;

public class ThumbnailListPlayerDialog extends BasePlayerDialog implements View.OnClickListener {
    private com.dalread.listener.OnClickListener listener;
//    private Object data;
    private DialogPlayerDialogueListBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerDialogueListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public ThumbnailListPlayerDialog(@NonNull Context context, boolean onlyForAutoScroll, com.dalread.listener.OnClickListener listener) {
        super(context);
        this.listener = listener;
//        this.data = data;
//        setContentView(R.layout.dialog_player_dialogue_list);
        ButterKnife.bind(this);
        if (onlyForAutoScroll) {
            binding.llRegenerateThumbnailList.setVisibility(View.GONE);
            binding.llKnownPhrases.setVisibility(View.GONE);
            binding.llUnknownPhrases.setVisibility(View.GONE);
        }
    }

//    public void setData(Object data) {
//        this.data = data;
//    }

    @Override
    protected void initOnClickListener() {
        binding.llRegenerateThumbnailList.setOnClickListener(this);
        binding.llAutoScrolling.setOnClickListener(this);
        binding.llKnownPhrases.setOnClickListener(this);
        binding.llUnknownPhrases.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }
        dismiss();
        listener.onClick(v, null);
    }

//
//    @OnClick({R.id.llRegenerateThumbnailList, R.id.llAutoScrolling, R.id.llKnownPhrases, R.id.llUnknownPhrases, R.id.tvCancel})
//    void onClick(View view) {
//        if (listener == null) {
//            return;
//        }
//        dismiss();
//        listener.onClick(view, null);
//    }
}
