package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingFlowParams;
import com.dalread.R;
import com.dalread.databinding.ItemInAppBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraThemeUtil;

import java.util.List;

public class InAppPointListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private List<BillingFlowParams.ProductDetailsParams> productList;
  private OnClickListener onClickListener;
  private Context context;
  public InAppPointListAdapter(Context context, List<BillingFlowParams.ProductDetailsParams> productList, OnClickListener onClickListener) {
    this.context = context;
    this.productList = productList;
    this.onClickListener = onClickListener;
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemInAppBinding binding = ItemInAppBinding.inflate(inflater, parent, false);
        return new InAppViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
      ((InAppViewHolder) holder).bind(position);
  }

  @Override
  public int getItemCount() {
    return productList.size();
  }

  private class InAppViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ItemInAppBinding binding;
    private BillingFlowParams.ProductDetailsParams productDetailsParams;

    public InAppViewHolder(ItemInAppBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    private void initListener() {
      binding.tvInAppPurchase.setOnClickListener(this);
    }

    public void bind(int position) {
      initListener();
      productDetailsParams = productList.get(position);
      binding.tvInAppTitle.setText(productDetailsParams.zza().getName());
      binding.tvInAppDescription.setText(productDetailsParams.zza().getDescription());
      initColor();
    }
    private void initColor() {
      AraThemeUtil.setBackgroundColor(context, binding.root, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.multiPlayerBackgroundBlackColor : R.color.backgroundCellColor);
      AraThemeUtil.setTextColor(context, binding.tvInAppTitle, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.textPrimaryWhiteColor : R.color.DayBlackNightLightWhite);
      AraThemeUtil.setTextColor(context, binding.tvInAppDescription, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.textPrimaryWhiteColor : R.color.DayBlackNightLightWhite);
    }
    @Override
    public void onClick(View v) {
      if (onClickListener != null) {
        onClickListener.onClick(v, productDetailsParams);
      }
    }
  }
}