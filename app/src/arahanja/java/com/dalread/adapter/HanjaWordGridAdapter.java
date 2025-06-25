package com.dalread.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemVocaHanjaWordGridBinding;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.HanjaItem;
import com.dalread.util.StringUtils;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NonConstantResourceId")
public class HanjaWordGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HanjaItem> hanjaItems = new ArrayList<>();
    private OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;
    private OnDoubleClickListener onDoubleClickListenerForCommon;
    private Context context;
    private HanjaItem hanjaItemToHighlight;

    public HanjaWordGridAdapter(Context context, OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow, OnDoubleClickListener onDoubleClickListenerForCommon) {
        this.context = context;
        this.onDoubleClickListenerOnBaseVocaKnow = onDoubleClickListenerOnBaseVocaKnow;
        this.onDoubleClickListenerForCommon = onDoubleClickListenerForCommon;
    }
    //Doesn't need this because there is only one type of viewType here.
//    @Override
//    public int getItemViewType(int position) {
//        return R.layout.item_voca_hanja_word_grid;
//    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HanjaWordGridHolder(ItemVocaHanjaWordGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((HanjaWordGridHolder) holder).bind((HanjaItem) hanjaItems.get(position), position);
    }

    @Override
    public int getItemCount() {
        return hanjaItems.size();
    }

    public void setData(List<HanjaItem> hanjaItems) {
        this.hanjaItems.clear();;
        this.hanjaItems.addAll(hanjaItems);
    }

    public void setHanjaItemToHighlight(HanjaItem hanjaItemToHighlight) {
        this.hanjaItemToHighlight = hanjaItemToHighlight;
    }

    public HanjaItem getHanjaItemToHighlight() {
        return this.hanjaItemToHighlight;
    }

//    public void setOnClickListener(OnDoubleClickListener listener) {
//        this.onDoubleClickListenerOnBaseVocaKnow = listener;
//    }

    class HanjaWordGridHolder extends RecyclerView.ViewHolder {
        private HanjaItem hanjaItem;

        private ItemVocaHanjaWordGridBinding binding;
        HanjaWordGridHolder(ItemVocaHanjaWordGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(HanjaItem hanjaItem, int position) {
            this.hanjaItem = hanjaItem;

            binding.vItem.setOnClickListener(new DoubleClick(onDoubleClickListenerOnBaseVocaKnow, onDoubleClickListenerForCommon, hanjaItem));
            getMeaningPronounceHanja();
//            binding.tvMeaningPronounceHanja.setText(hanjaItem.getHI_MEANING1() + " " + hanjaItem.getHI_PRONOUNCE1_FIRST());
            highLightHanja(); // Don't change order with binding.tvVoca (setTextColor will not work if hanjaItemToHighlight not null)
            binding.tvVoca.setText(hanjaItem.getHI_VOCA());
            binding.tvVoca.setTextColor(VocaKnow.getVocaColor(context, hanjaItem.getHI_VOCA_KNOW().intValue()));
        }

        private void getMeaningPronounceHanja() {
            if (VocaKnow.isVocaTypeWord(hanjaItem)) {
                if (Utils.isEmpty(hanjaItem.getHI_PRONOUNCE1_FIRST())) {
                    binding.tvMeaningPronounceHanja.setText(hanjaItem.getHI_MEANING1());
                } else {
//                    String strMeaningPronounceHanja = hanjaItem.getHI_MEANING1() + " " + hanjaItem.getHI_PRONOUNCE1_FIRST();
//                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(hanjaItem.getHI_MEANING1() + " " + hanjaItem.getHI_PRONOUNCE1_FIRST());
//                    spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), strMeaningPronounceHanja.length() - 1, strMeaningPronounceHanja.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    binding.tvMeaningPronounceHanja.setText(StringUtils.makeBoldOnPronounceOnHanjaWord(hanjaItem.getHI_MEANING1(), hanjaItem.getHI_PRONOUNCE1_FIRST()));
                }
            } else {
                binding.tvMeaningPronounceHanja.setText(hanjaItem.getHI_MEANING1() + " " + hanjaItem.getHI_PRONOUNCE1_FIRST());
            }
        }
        private void highLightHanja() {
            if (hanjaItemToHighlight != null) {
                if (Voca.isSameVoca(hanjaItem, hanjaItemToHighlight)) {
                    //TODO : want to set hanjaItemToHighlight to center of view.
                    binding.vItem.setBackgroundResource(R.drawable.background_border_light_black_for_word_grid_highlight);
                    binding.tvVoca.setTextColor(ContextCompat.getColor(context, R.color.textPrimaryWhiteColor));
                    binding.tvMeaningPronounceHanja.setTextColor(ContextCompat.getColor(context, R.color.textPrimaryWhiteColor));
                } else {
                    binding.vItem.setBackgroundResource(R.drawable.background_border_light_black_for_word_grid);
                    binding.tvVoca.setTextColor(ContextCompat.getColor(context, R.color.textPrimaryColor));
                    binding.tvMeaningPronounceHanja.setTextColor(ContextCompat.getColor(context, R.color.textPrimaryColor));
                }
            }
        }
    }
}
