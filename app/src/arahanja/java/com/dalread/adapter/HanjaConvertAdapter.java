package com.dalread.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemVocaHanjaWordGridBinding;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.DIC_HANJA_SENTENCE;
import com.dalread.model.HanjaItem;
import com.dalread.util.StringUtils;
import com.dalread.util.Utils;
import com.dalread.util.VocaKnow;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NonConstantResourceId")
public class HanjaConvertAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HanjaItem> hanjaItems = new ArrayList<>();
    private OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;
    private OnDoubleClickListener onDoubleClickListenerForCommon;
    private Context context;
    private int maxLengthOfWordInOneChar = 1;

    public HanjaConvertAdapter(Context context, OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow, OnDoubleClickListener onDoubleClickListenerForCommon) {
        this.context = context;
        this.onDoubleClickListenerOnBaseVocaKnow = onDoubleClickListenerOnBaseVocaKnow;
        this.onDoubleClickListenerForCommon = onDoubleClickListenerForCommon;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HanjaWordGridHolder(ItemVocaHanjaWordGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((HanjaWordGridHolder) holder).bind((HanjaItem) hanjaItems.get(position));
    }

    @Override
    public int getItemCount() {
        return hanjaItems.size();
    }

    public void setData(List<HanjaItem> hanjaItems) {
        this.hanjaItems.clear();;
        this.hanjaItems.addAll(hanjaItems);
        hanjaItems.stream().filter(hanjaItem -> hanjaItem.getHI_VOCA().length() == 1).forEach(hanjaItem -> {
            int meaningLength;
            if (hanjaItem instanceof DIC_HANJA_SENTENCE) {
                meaningLength = ((DIC_HANJA_SENTENCE) hanjaItem).getMEANING_KO().length();
            } else {
                meaningLength = (hanjaItem.getHI_MEANING1() + " " + hanjaItem.getHI_PRONOUNCE1_FIRST()).length();
            }
            if (meaningLength > maxLengthOfWordInOneChar) {
                maxLengthOfWordInOneChar = meaningLength;
            }
        });
    }

    class HanjaWordGridHolder extends RecyclerView.ViewHolder {
        private HanjaItem hanjaItem;
        private int paddingHorizontal;
        private ItemVocaHanjaWordGridBinding binding;
        HanjaWordGridHolder(ItemVocaHanjaWordGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View rootView = binding.getRoot();
            paddingHorizontal = context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin_item);
            rootView.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
            itemView.setMinimumWidth((int) (binding.tvMeaningPronounceHanja.getTextSize() * maxLengthOfWordInOneChar + paddingHorizontal * 2));
            ViewGroup.LayoutParams lp = rootView.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            if (lp instanceof FlexboxLayoutManager.LayoutParams) {
                FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) lp;
                flexboxLp.setFlexGrow(1.0f);
            }
        }

        void bind(HanjaItem hanjaItem) {
            this.hanjaItem = hanjaItem;
            binding.vItem.setOnClickListener(new DoubleClick(onDoubleClickListenerOnBaseVocaKnow, onDoubleClickListenerForCommon, hanjaItem));

            binding.tvVoca.setText(hanjaItem.getHI_VOCA());
            if (VocaKnow.isVocaTypeSentence(hanjaItem.getHI_VOCA_TYPE())) {
//                String voca = hanjaItem.getHI_VOCA();
//                binding.llHanjaWordGrid.getLayoutParams().width = (int) context.getResources().getDimension(R.dimen.study_hanja_small_box_width) * voca.length();
//                binding.llHanjaWordGrid.requestLayout();

                if (Utils.isEmpty(((DIC_HANJA_SENTENCE) hanjaItem).getMEANING_KO())) {
                    binding.tvMeaningPronounceHanja.setText(StringUtils.makeBoldOnText(((DIC_HANJA_SENTENCE) hanjaItem).getPRONOUNCE()));
                } else {
                    binding.tvMeaningPronounceHanja.setText(StringUtils.makeBoldOnPronounceOnHanjaSentence(((DIC_HANJA_SENTENCE) hanjaItem).getPRONOUNCE(), ((DIC_HANJA_SENTENCE) hanjaItem).getMEANING_KO()));
                }

            } else {
                binding.tvMeaningPronounceHanja.setText(StringUtils.makeBoldOnPronounceOnHanjaWord(hanjaItem.getHI_MEANING1(), hanjaItem.getHI_PRONOUNCE1_FIRST()));
            }
            binding.tvVoca.setTextColor(VocaKnow.getVocaColor(context, hanjaItem.getHI_VOCA_KNOW().intValue()));
        }
    }
}
