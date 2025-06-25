package com.dalread.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.billingclient.api.BillingFlowParams;
import com.dalread.R;
import com.dalread.adapter.InAppPointListAdapter;
import com.dalread.base.BaseActivity;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityInAppListBinding;
import com.dalread.dialog.YesNoDialog;
import com.dalread.helper.BillingClientHelper;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraThemeUtil;

import java.util.List;

public class InAppPointListParentActivity extends BaseActivity {
  private ActivityInAppListBinding binding;
  private BillingClientHelper billingClientHelper;
  private List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList;


  protected BillingClientHelper getBillingClientHelper() {
    return null;
  }
  protected View getContentView() {
    binding = ActivityInAppListBinding.inflate(getLayoutInflater());
    View view = binding.getRoot();
    return view;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    billingClientHelper = getBillingClientHelper();
    productDetailsParamsList = billingClientHelper.getProductDetailsParams();
    InAppPointListAdapter adapter = new InAppPointListAdapter(this, productDetailsParamsList, onClickListener);
    binding.rvInAppList.setAdapter(adapter);
    binding.rvInAppList.setLayoutManager(new LinearLayoutManager(this));
    initColor();
  }
  private void initColor() {
    AraThemeUtil.setBackgroundColor(this, binding.llRoot, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.multiPlayerBackgroundLightBlackColor : R.color.backgroundTableColor);
  }
  @Nullable
  public Toolbar getToolbar() {
    return binding.header;
  }
  private OnClickListener onClickListener = (view, object) -> {
    if (object instanceof BillingFlowParams.ProductDetailsParams) {
      BillingFlowParams.ProductDetailsParams selectedInApp = (BillingFlowParams.ProductDetailsParams) object;
      switch (view.getId()) {
        case R.id.tvInAppPurchase:
          final YesNoDialog dialog = new YesNoDialog(this, R.string.warning, getString(R.string.msg_warning_buy_inapp_point), null, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
              billingClientHelper.handleSelectedInApp(InAppPointListParentActivity.this, selectedInApp);
              onBackPressed();
            }

            @Override
            public void onNoClick(View view, Object object) {

            }
          });
          dialog.show();

          break;
      }
    }
  };

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
}
