package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerShowSortMenuBinding;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraThemeUtil;

public class PlayerShowSortMenuDialog extends BasePlayerDialog implements View.OnClickListener {
    private OnClickListener listener;
    private Context context;
    private DialogPlayerShowSortMenuBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerShowSortMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

//    @Override
//    protected int getContentViewId() {
//        return R.layout.dialog_player_show_sort_menu;
//    }

    public PlayerShowSortMenuDialog(@NonNull Context context, OnClickListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        initColor();
    }
    private void initColor() {
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            AraThemeUtil.setBackgroundColor(context, binding.llMain, R.color.multiPlayerBackgroundLightBlackColor);
            AraThemeUtil.setTextColor(context, binding.tvSortCreatedDateAsc, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvSortCreatedDateDesc, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvSortFileSizeAsc, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvSortFileSizeDesc, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvSortFileNameAsc, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvSortFileNameDesc, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvSortDifficultyAsc, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvSortDifficultyDesc, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(context, binding.tvCancel, R.color.textPrimaryWhiteColor);
        }
    }

    @Override
    protected void initOnClickListener() {
        binding.tvSortCreatedDateAsc.setOnClickListener(this);
        binding.tvSortCreatedDateDesc.setOnClickListener(this);
        binding.tvSortFileSizeAsc.setOnClickListener(this);
        binding.tvSortFileSizeDesc.setOnClickListener(this);
        binding.tvSortFileNameAsc.setOnClickListener(this);
        binding.tvSortFileNameDesc.setOnClickListener(this);
        binding.tvSortDifficultyAsc.setOnClickListener(this);
        binding.tvSortDifficultyDesc.setOnClickListener(this);
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
//    @OnClick({R.id.tv_sort_created_date_asc, R.id.tv_sort_created_date_desc, R.id.tv_sort_file_size_asc, R.id.tv_sort_file_size_desc, R.id.tv_sort_file_name_asc, R.id.tv_sort_file_name_desc, R.id.tv_sort_difficulty_asc, R.id.tv_sort_difficulty_desc, R.id.tv_cancel})
//    void onClick(View view) {
//        dismiss();
//        if (listener != null) {
//            listener.onClick(this, view.getId());
//        }
//    }
}
