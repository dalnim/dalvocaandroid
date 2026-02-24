package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemFreeAppCardBinding;
import com.dalread.util.OpenViewUtil;
import com.dalread.util.OtherAppsInfo;
import com.dalread.util.Utils;

import java.util.List;
import java.util.Locale;

/**
 * "무료 앱들" 화면용 카드 어댑터. 선택된 카드에 테두리, 앱 설명 + App Store / Google Play 버튼.
 */
public class FreeAppCardAdapter extends RecyclerView.Adapter<FreeAppCardAdapter.CardViewHolder> {

    private final Context context;
    private List<OtherAppsInfo.Entry> list;
    private int selectedPosition = 0;

    public FreeAppCardAdapter(Context context, List<OtherAppsInfo.Entry> list) {
        this.context = context;
        this.list = list;
    }

    public void setSelectedPosition(int position) {
        int old = selectedPosition;
        selectedPosition = position;
        if (old >= 0 && old < list.size()) notifyItemChanged(old);
        if (position >= 0 && position < list.size()) notifyItemChanged(position);
    }

    public void setList(List<OtherAppsInfo.Entry> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFreeAppCardBinding binding = ItemFreeAppCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        OtherAppsInfo.Entry entry = list.get(position);
        holder.binding.tvAppName.setText(entry.nameResId);
        holder.binding.tvDescription.setText(entry.descriptionResId);
        boolean selected = (position == selectedPosition);
        float density = context.getResources().getDisplayMetrics().density;
        int strokeWidthPx = (int) (density * (selected ? 3 : 1));
        holder.binding.card.setStrokeWidth(strokeWidthPx);
        int strokeColor = selected ? ContextCompat.getColor(context, R.color.primaryColor) : ContextCompat.getColor(context, R.color.textSecondaryColor);
        holder.binding.card.setStrokeColor(strokeColor);

        boolean isKorean = Locale.getDefault().getLanguage().equals(Locale.KOREAN.getLanguage());
        int appStoreResId = isKorean ? R.drawable.ic_badge_app_store_ko : R.drawable.ic_badge_app_store_en;
        int googlePlayResId = isKorean ? R.drawable.ic_badge_google_play_ko : R.drawable.ic_badge_google_play_en;
        boolean hasAppStore = entry.appStoreUrl != null && !entry.appStoreUrl.isEmpty();
        boolean hasPlayStore = entry.playStoreUrl != null && !entry.playStoreUrl.isEmpty();

        holder.binding.ivAppStore.setVisibility(hasAppStore ? View.VISIBLE : View.GONE);
        holder.binding.ivAppStore.setImageResource(appStoreResId);
        holder.binding.ivAppStore.setOnClickListener(hasAppStore ? v -> Utils.openWeb(context, entry.appStoreUrl) : null);

        holder.binding.ivGooglePlay.setVisibility(hasPlayStore ? View.VISIBLE : View.GONE);
        holder.binding.ivGooglePlay.setImageResource(googlePlayResId);
        holder.binding.ivGooglePlay.setOnClickListener(hasPlayStore ? v -> OpenViewUtil.openGooglePlayStore(context, entry.playStoreUrl) : null);

        holder.itemView.setOnClickListener(v -> setSelectedPosition(holder.getBindingAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        final ItemFreeAppCardBinding binding;

        CardViewHolder(ItemFreeAppCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
