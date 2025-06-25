package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityNormalTextViewForHuriganaBinding;
import com.dalread.model.HanjaItem;
import com.dalread.util.Constant;
import com.dalread.util.CopyTextUtil;
import com.dalread.util.Utils;

public class NormalTextViewForHuriganaActivity extends BaseHanjaInfoActivity {
    private ActivityNormalTextViewForHuriganaBinding binding;
    private HanjaItem hanjaItem;
    public static Intent createIntent(Context context, HanjaItem item) {
        Intent intent = new Intent(context, NormalTextViewForHuriganaActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, item);
        return intent;
    }

    @Override
    protected View getContentView() {
        binding = ActivityNormalTextViewForHuriganaBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hanjaItem = (HanjaItem) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_BOOK);
        if (hanjaItem != null) {
            binding.tvContent.setText(hanjaItem.getHI_ALL_TEXT_FOR_NORMAL_TEXT());
        }
        binding.fabCopy.setOnClickListener( v -> {
            String text = binding.tvContent.getText().toString();
            if (Utils.isNotEmpty(text)) {
                CopyTextUtil.copyToClipboard(this, text, R.string.copied);
            }
        });
    }

    @Override
    protected void getData() {

    }

    @Override
    protected RecyclerView getRvSearch() {
        return binding.rvSearch;
    }

}