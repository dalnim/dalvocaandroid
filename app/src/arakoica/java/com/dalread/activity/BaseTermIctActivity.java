package com.dalread.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseActivity;
import com.dalread.composition.VocaKnowActivity;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.BaseVoca;
import com.dalread.util.Utils;

import butterknife.BindColor;
import butterknife.BindDimen;

@SuppressLint("NonConstantResourceId")
public abstract class BaseTermIctActivity extends BaseActivity {
    //TODO : If I remove these codes, I met this error.
    @BindColor(R.color.color_divider)
    protected int clDivider;
    @BindDimen(R.dimen.divider_height)
    protected float dividerHeight;

    protected VocaKnowActivity vocaKnowActivity;
    protected SubDatabase subDatabase;
    protected OnDoubleClickListener onDoubleClickListenerForCommon;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSubDatabase();
        vocaKnowActivity = new VocaKnowActivity(this, subDatabase);
        initListener(); //Do this before initLayout
        initData();
        initLayout();
        initDialog();
        initSearch();
        initListener();
    }
    private void initSubDatabase() {
        if (subDatabase != null) {
            subDatabase.close();
        }
        subDatabase = null;
        String destPathWithFileName = BaseStorageUtil.getAraConvDBPathWithFileName(context);
        subDatabase = SubDatabase.getInstance(this, destPathWithFileName);
    }

    @Override
    protected void onDestroy() {
        unRegisterEventBus();

        super.onDestroy();
    }

    protected void initData() {

    }

    protected void initLayout() {

    }

    protected void initDialog() {
    }

    protected void initSearch() {

    }

    protected void initListener() {
        onDoubleClickListenerForCommon = new OnDoubleClickListener() {
            @Override
            public void onClick(View view, Object object) {
                if (object instanceof IVocaBasicItem) {
                    switch (view.getId()) {
                        case R.id.tvVoca:
                        case R.id.ivCopy:
                            BaseVoca.openCopyDialog(context, (IVocaBasicItem) object);
                            break;
                    }

                } else if (object instanceof String) {
                    String text = (String) object;
                    switch (view.getId()) {
                        case R.id.tvVoca:
                            Utils.copyToClipboard(context, text, R.string.copied);
                            break;
                    }
                }
            }

            @Override
            public void onDoubleClick(View view, Object object) {

            }
        };
    }

    protected void backToHome() {
        backToHome(MainHomeActivity.class);
    }
}
