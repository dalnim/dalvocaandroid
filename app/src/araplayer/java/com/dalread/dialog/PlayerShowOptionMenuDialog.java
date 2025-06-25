package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerOptionMenuBinding;
import com.dalread.model.PlayerFileModel;

public class PlayerShowOptionMenuDialog extends BasePlayerDialog implements View.OnClickListener {
//    @BindView(R.id.ll_translate_subtitle) View ll_translate_subtitle;
    private OnClickListener listener;
    private DialogPlayerOptionMenuBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerOptionMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

//    @Override
//    protected int getContentViewId() {
//        return R.layout.dialog_player_option_menu;
//    }

    public PlayerShowOptionMenuDialog(@NonNull Context context, PlayerFileModel playerFileModel, OnClickListener listener) {
        super(context);
//        ll_translate_subtitle.setVisibility(StorageUtil.isSubDatabaseFileExist(playerFileModel.getPath()) ? View.VISIBLE : View.GONE);
        binding.llTranslateSubtitle.setVisibility(playerFileModel.isHasSubRuby(context) ? View.VISIBLE : View.GONE);
        this.listener = listener;
    }

    @Override
    protected void initOnClickListener() {
        binding.tvTranslateSubtitle.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            listener.onClick(this, v.getId());
        }
    }

//
//    @OnClick({R.id.tv_translate_subtitle, R.id.tv_cancel})
//    void onClick(View view) {
//        dismiss();
//        if (listener != null) {
//            listener.onClick(this, view.getId());
//        }
//    }
}
