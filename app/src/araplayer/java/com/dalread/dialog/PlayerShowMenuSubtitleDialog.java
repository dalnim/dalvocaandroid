package com.dalread.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerShowMenuSubtitleBinding;
import com.dalread.listener.OnClickDialogListener;

public class PlayerShowMenuSubtitleDialog extends BasePlayerDialog implements View.OnClickListener, DialogInterface.OnDismissListener {

//    @BindView(R.id.llTable) View llTable;
//    @BindView(R.id.tvTable) TextView tvTable;
    private OnClickDialogListener listener;
    private DialogPlayerShowMenuSubtitleBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerShowMenuSubtitleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

//    @Override
//    protected int getContentViewId() {
//        return R.layout.dialog_player_show_menu_subtitle;
//    }

    public PlayerShowMenuSubtitleDialog(@NonNull Context context, boolean isVerticalMode, boolean isShowTable, OnClickDialogListener listener) {
        super(context);
        this.listener = listener;
        setOnDismissListener(this);
        binding.tvTable.setText(isShowTable ? R.string.hide_the_table : R.string.show_the_table);
        binding.llTable.setVisibility(isVerticalMode ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void initOnClickListener() {
        binding.tvSubtitleList.setOnClickListener(this);
        binding.llTable.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            listener.onClick(v, v);
        }
    }
//
//    @OnClick({R.id.tvSubtitleList, R.id.llTable, R.id.tv_cancel})
//    void onClick(View view) {
//        dismiss();
//        if (listener != null) {
//            listener.onClick(view, view);
//        }
//    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (listener != null) {
            listener.onDismiss(view, -1);
        }
    }
}
