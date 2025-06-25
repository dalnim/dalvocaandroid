package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogInputWebDictionaryBinding;
import com.dalread.model.WebDictionaryModel;
import com.dalread.util.Utils;

public class WebDictionaryInputDialog extends BasePlayerDialog implements View.OnClickListener {
    private WebDictionaryModel item;
    private com.dalread.listener.OnClickListener listener;
    private DialogInputWebDictionaryBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogInputWebDictionaryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public WebDictionaryInputDialog(@NonNull Context context, WebDictionaryModel item, com.dalread.listener.OnClickListener listener) {
        super(context);
        this.listener = listener;
        this.item = item;
        initView();
    }

    @Override
    protected void initOnClickListener() {
        binding.btnSave.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCancel) {
            dismiss();
        } else {
            final String title = binding.etTitle.getText().toString();
            final String url = binding.etUrl.getText().toString();
            if (Utils.isEmpty(title) || Utils.isEmpty(url)) {
                return;
            }
            if (item == null) {
                item = new WebDictionaryModel();
            }
            item.setTitle(title);
            item.setUrl(url);
            dismiss();
            if (listener != null) {
                listener.onClick(v, item);
            }
        }
    }


//    @OnClick(R.id.btn_cancel)
//    void onCancel(View view) {
//        dismiss();
//    }
//
//    @OnClick(R.id.btn_save)
//    void onOK(View view) {
//        final String title = binding.etTitle.getText().toString();
//        final String url = binding.etUrl.getText().toString();
//        if (Utils.isEmpty(title) || Utils.isEmpty(url)) {
//            return;
//        }
//        if (item == null) {
//            item = new WebDictionaryModel();
//        }
//        item.setTitle(title);
//        item.setUrl(url);
//        dismiss();
//        if (listener != null) {
//            listener.onClick(view, item);
//        }
//    }

    private void initView() {
        if (item == null) return;
        binding.etTitle.setText(item.getTitle());
        binding.etUrl.setText(item.getUrl());
    }
}
