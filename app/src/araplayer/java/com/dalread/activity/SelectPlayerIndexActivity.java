package com.dalread.activity;

import android.content.Intent;
import android.view.View;

import com.dalread.adapter.SelectIndexPlayerAdapter;
import com.dalread.base.BasePlayerActivity;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityPlayerSelectIndexBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

public class SelectPlayerIndexActivity extends BasePlayerActivity {
    private SelectIndexPlayerAdapter adapter;
    private String[] items = new String[]{};
    private int index;
    private ActivityPlayerSelectIndexBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityPlayerSelectIndexBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void setFullscreen() {

    }

    @Override
    public void initView() {
        adapter = new SelectIndexPlayerAdapter(this, items, onClickListener);
        binding.rvContent.setAdapter(adapter);
        binding.rvContent.setLayoutManager(new CenterLayoutManager(this));
        binding.rvContent.addItemDecoration(new SeparatorDecoration(this, BaseBindUtils.getDividerColor(), BaseBindUtils.getDividerHeight(this)));
        initData();
    }

    @Override
    public void initData() {
        items = getIntent().getStringArrayExtra(Constant.PLAYER.INTENT.KEY_DATA);
        index = getIntent().getIntExtra(Constant.PLAYER.INTENT.KEY_INDEX, 0);
        adapter.setLastCheckPosition(index);
        adapter.setData(items);
    }

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }
    @Override
    public void onHeaderLeft2Click() {
    }
    @Override
    public void onHeaderRightClick() {

    }

    @Override
    public void onHeaderIconRightClick() {

    }

    @Override
    public void onHeaderTextRightClick() {
        Intent intent = getIntent();
        intent.putExtra(Constant.PLAYER.INTENT.KEY_INDEX, index);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_WORD, Utils.parseInt(items[index]));
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    private OnClickListener onClickListener = (view, object) -> {
        index = (int) object;
        binding.header.getTvRight().setVisibility(index >= 0 ? View.VISIBLE : View.INVISIBLE);
    };
}
