package com.dalread.base;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dalread.R;
import com.dalread.util.DLog;

import butterknife.BindView;
import butterknife.OnClick;

public abstract class BaseActivityTextLeft extends BaseActivityNoHeader {

    @BindView(R.id.vLayoutHeader) View vLayoutHeader;
    @BindView(R.id.ivHeaderLeft) ImageView ivHeaderLeft;
    @BindView(R.id.tvHeaderTitle) TextView tvHeaderTitle;
    @BindView(R.id.tvHeaderRight) TextView tvHeaderRight;

    protected abstract void onClickIcLeft(View v);
    protected abstract void onClickIcRight(View v);

    protected void showTwoImage(int ivLeft, int ivRight) {
        ivHeaderLeft.setImageResource(ivLeft);
        tvHeaderRight.setText(getString(ivRight));
        ivHeaderLeft.setVisibility(View.VISIBLE);
        tvHeaderRight.setVisibility(View.VISIBLE);
    }

    protected void showLeftImage(int ivLeft) {
        ivHeaderLeft.setImageResource(ivLeft);
        ivHeaderLeft.setVisibility(View.VISIBLE);
        tvHeaderRight.setVisibility(View.INVISIBLE);
    }

    protected void showRightImage(int ivRight) {
        tvHeaderRight.setText(getString(ivRight));
        ivHeaderLeft.setVisibility(View.INVISIBLE);
        tvHeaderRight.setVisibility(View.VISIBLE);
    }

    protected void showTitle(int value) {
        tvHeaderTitle.setText(getString(value));
    }

    protected void showTitle(String value) {
        tvHeaderTitle.setText(value);
    }

    @OnClick(R.id.ivHeaderLeft)
    protected void onClickLeft(View v) {
        DLog.d(TAG, "onClickLeft");
        onClickIcLeft(v);
    }

    @OnClick(R.id.tvHeaderRight)
    protected void onClickRight(View v) {
        DLog.d(TAG, "onClickRight");
        onClickIcRight(v);
    }

    protected void showBackIcon() {
        showLeftImage(R.drawable.ic_back);
    }

    public View getLayoutHeader() {
        return vLayoutHeader;
    }

    public void setLayoutHeader(View vLayoutHeader) {
        this.vLayoutHeader = vLayoutHeader;
    }
}
