package com.dalread.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.databinding.ItemVocaHanjaWordGridBinding;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.DIC_HANJA;
import com.dalread.model.DIC_HANJA_SENTENCE;
import com.dalread.model.HanjaItem;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NonConstantResourceId")
public class VocaHanjaSentenceInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Object> data = new ArrayList<>();
    private List<HanjaItem> hanjas = new ArrayList<>();
    private OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;
    private OnDoubleClickListener onDoubleClickListenerForCommon;
    private Context context;

    public VocaHanjaSentenceInfoAdapter(Context context, OnDoubleClickListener onDoubleClickListener, OnDoubleClickListener onDoubleClickListenerForCommon) {
        this.context = context;
        this.onDoubleClickListenerOnBaseVocaKnow = onDoubleClickListener;
        this.onDoubleClickListenerForCommon = onDoubleClickListenerForCommon;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HanjaHolder(ItemVocaHanjaWordGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((HanjaHolder) holder).bind((DIC_HANJA) data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void setData(DIC_HANJA_SENTENCE hanjaSentence) {
        data.clear();
        hanjas = Voca.getHanjaWordItemListFromContent(hanjaSentence.getALL_TEXT(), false);
        if (!hanjas.isEmpty()) {
            data.addAll(hanjas);
        }
    }

    public void setData(String content) {
        data.clear();
        hanjas = Voca.getHanjaWordItemListFromContent(content, false);
        if (!hanjas.isEmpty()) {
            data.addAll(hanjas);
        }
    }

    class HanjaHolder extends RecyclerView.ViewHolder {

        private DIC_HANJA hanja;
        private ItemVocaHanjaWordGridBinding binding;
        HanjaHolder(ItemVocaHanjaWordGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(DIC_HANJA hanja) {
            this.hanja = hanja;
            binding.vItem.setOnClickListener(new DoubleClick(onDoubleClickListenerOnBaseVocaKnow, onDoubleClickListenerForCommon, hanja));
            String text = hanja.getVOCA();
            binding.tvVoca.setText(text);
            binding.tvVoca.setTextColor(VocaKnow.getVocaColor(context, hanja.getVOCA_KNOW().intValue()));
            binding.tvMeaningPronounceHanja.setText(hanja.getMEANING1() + " " + hanja.getPRONOUNCE1_FIRST());
        }
    }
}
