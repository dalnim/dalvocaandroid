package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerWordListBinding;

import butterknife.ButterKnife;

public class WordListPlayerDialog extends BasePlayerDialog implements View.OnClickListener {
    private com.dalread.listener.OnClickListener listener;
    private Object data;

    private DialogPlayerWordListBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerWordListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public WordListPlayerDialog(@NonNull Context context, Object data, com.dalread.listener.OnClickListener listener) {
        super(context);
        this.listener = listener;
        this.data = data;
//        setContentView(R.layout.dialog_player_word_list);
        ButterKnife.bind(this);

        boolean isWordList = false;
        if (data instanceof Boolean) {
            isWordList = (boolean) data;
        }
        if (isWordList) {
            binding.llChangeWordsKnownStatus.setVisibility(View.GONE);
            binding.llKnownPhrases.setVisibility(View.VISIBLE);
            binding.llUnknownPhrases.setVisibility(View.VISIBLE);
        } else {
            binding.llChangeWordsKnownStatus.setVisibility(View.VISIBLE);
            binding.llKnownPhrases.setVisibility(View.GONE);
            binding.llUnknownPhrases.setVisibility(View.GONE);
        }
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    protected void initOnClickListener() {
        binding.llChangeWordsKnownStatus.setOnClickListener(this);
        binding.llPlayAllWords.setOnClickListener(this);
        binding.llExportWordList.setOnClickListener(this);
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
        listener.onClick(v, data);
    }


//    @OnClick({R.id.llChangeWordsKnownStatus, R.id.llPlayAllWords, R.id.llKnownPhrases, R.id.llUnknownPhrases, R.id.tvCancel})
//    void onClick(View view) {
//        if (listener == null) {
//            return;
//        }
//        dismiss();
//        listener.onClick(view, data);
//    }
}
