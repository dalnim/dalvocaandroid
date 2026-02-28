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
 * "무료 앱들" 화면용 카드 어댑터. 첫 항목은 헤더(QR 섹션 + 메시지), 이어서 선택된 카드에 테두리, 앱 설명 + App Store / Google Play 버튼.
 */
public class FreeAppCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CARD = 1;

    private final Context context;
    private List<OtherAppsInfo.Entry> list;
    private int selectedPosition = 0;
    private final boolean showShareSection;
    private final OnBindHeaderListener onBindHeaderListener;

    public interface OnBindHeaderListener {
        void onBindHeader(View headerView);
    }

    public FreeAppCardAdapter(Context context, List<OtherAppsInfo.Entry> list) {
        this(context, list, false, null);
    }

    public FreeAppCardAdapter(Context context, List<OtherAppsInfo.Entry> list, boolean showShareSection, OnBindHeaderListener onBindHeaderListener) {
        this.context = context;
        this.list = list;
        this.showShareSection = showShareSection;
        this.onBindHeaderListener = onBindHeaderListener;
    }

    public void setSelectedPosition(int position) {
        int old = selectedPosition;
        selectedPosition = position;
        if (old >= 0 && old < list.size()) notifyItemChanged(old + 1);
        if (position >= 0 && position < list.size()) notifyItemChanged(position + 1);
    }

    public void setList(List<OtherAppsInfo.Entry> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_CARD;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_free_apps_header, parent, false);
            return new HeaderViewHolder(view);
        }
        ItemFreeAppCardBinding binding = ItemFreeAppCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            if (onBindHeaderListener != null) {
                onBindHeaderListener.onBindHeader(holder.itemView);
            }
            return;
        }
        CardViewHolder cardHolder = (CardViewHolder) holder;
        int listIndex = position - 1;
        OtherAppsInfo.Entry entry = list.get(listIndex);
        cardHolder.binding.tvAppName.setText(entry.nameResId);
        cardHolder.binding.tvDescription.setText(entry.descriptionResId);
        boolean selected = (listIndex == selectedPosition);
        float density = context.getResources().getDisplayMetrics().density;
        int strokeWidthPx = (int) (density * (selected ? 3 : 1));
        cardHolder.binding.card.setStrokeWidth(strokeWidthPx);
        int strokeColor = selected ? ContextCompat.getColor(context, R.color.primaryColor) : ContextCompat.getColor(context, R.color.textSecondaryColor);
        cardHolder.binding.card.setStrokeColor(strokeColor);

        boolean isKorean = Locale.getDefault().getLanguage().equals(Locale.KOREAN.getLanguage());
        int appStoreResId = isKorean ? R.drawable.ic_badge_app_store_ko : R.drawable.ic_badge_app_store_en;
        int googlePlayResId = isKorean ? R.drawable.ic_badge_google_play_ko : R.drawable.ic_badge_google_play_en;
        boolean hasAppStore = entry.appStoreUrl != null && !entry.appStoreUrl.isEmpty();
        boolean hasPlayStore = entry.playStoreUrl != null && !entry.playStoreUrl.isEmpty();

        cardHolder.binding.ivAppStore.setVisibility(hasAppStore ? View.VISIBLE : View.GONE);
        cardHolder.binding.ivAppStore.setImageResource(appStoreResId);
        cardHolder.binding.ivAppStore.setOnClickListener(hasAppStore ? v -> Utils.openWeb(context, entry.appStoreUrl) : null);

        cardHolder.binding.ivGooglePlay.setVisibility(hasPlayStore ? View.VISIBLE : View.GONE);
        cardHolder.binding.ivGooglePlay.setImageResource(googlePlayResId);
        cardHolder.binding.ivGooglePlay.setOnClickListener(hasPlayStore ? v -> OpenViewUtil.openGooglePlayStore(context, entry.playStoreUrl) : null);

        cardHolder.itemView.setOnClickListener(v -> setSelectedPosition(cardHolder.getBindingAdapterPosition() - 1));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : 1 + list.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        final ItemFreeAppCardBinding binding;

        CardViewHolder(ItemFreeAppCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
