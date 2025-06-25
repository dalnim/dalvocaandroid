package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.HowToUseAdapter;
import com.dalread.base.BaseVocaActivity;
import com.dalread.component.SeparatorDecoration;
import com.dalread.listener.OnHowToUseClickListener;
import com.dalread.util.Constant;
import com.dalread.util.ToastUtil;

import butterknife.BindArray;
import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;

public class HowToUseActivity extends BaseVocaActivity {

    @BindView(R.id.rv_manual) RecyclerView rvManual;
    @BindColor(R.color.color_divider) int clDivider;
    @BindDimen(R.dimen.divider_height) float dividerHeight;
    @BindArray(R.array.how_to_use_list) String[] howToUseList;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_how_to_use;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRecyclerView();
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

    }

    private void initRecyclerView() {
        HowToUseAdapter adapter = new HowToUseAdapter(howToUseList);
        adapter.setListener(onHowToUseClickListener);
        rvManual.setAdapter(adapter);
        rvManual.setLayoutManager(new LinearLayoutManager(this));
        rvManual.addItemDecoration(new SeparatorDecoration(this, clDivider, dividerHeight));
    }

    private OnHowToUseClickListener onHowToUseClickListener = new OnHowToUseClickListener() {

        @Override
        public void onClick(int position) {
            switch (position) {
                case 0:
                    Intent intent = new Intent(HowToUseActivity.this, AppIntroductionActivity.class);
                    intent.putExtra(Constant.BUNDLE.KEY_FROM_MENU, true);
                    openNewScreen(intent);
                    break;
                default:
                    ToastUtil.getInstance(HowToUseActivity.this).show(R.string.msg_under_development);
                    break;
            }
        }
    };
}
