package com.dalread.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.databinding.ItemTermIctBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.util.Utils;
import com.dalread.util.stt.SttModel;

import java.util.ArrayList;
import java.util.List;
@SuppressLint("NonConstantResourceId")
public class IctTermListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SttModel> itemList = new ArrayList<>();
    private OnClickListener onClickListener;
    private Context context;

    public IctTermListAdapter(Context context, OnClickListener onClickListener) {
        this.context = context;

        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ModelHolder(ItemTermIctBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ModelHolder) holder).bind((SttModel) itemList.get(position), position);
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItemList(List<SttModel> itemList) {
        this.itemList.clear();
        this.itemList.addAll(itemList);
    }

    public void addItemInList(SttModel model) {
        itemList.add(model);
    }

    class ModelHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SttModel model;

        private ItemTermIctBinding binding;
        ModelHolder(ItemTermIctBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.vItem.setOnClickListener(this);
        }

        public void bind(SttModel model, int position) {
            this.model = model;
            setTextViewValue(binding.tvDifficultWordAndMeaning, model.SMgetDifficultWordAndMeaning(), false);
            setTextViewValue(binding.tvStudyLangValue, model.SMgetSentence(), false);
            setTextViewValue(binding.tvMotherTongueLangValue, model.SMgetTranslation(), false);
//            setTextViewValue(binding.tvStartEndTime, model.getStartEndTime(), false);
        }

        private void setTextViewValue(TextView textView, String string, boolean isShowColorKeyword) {
            textView.setVisibility(View.GONE);
            if (Utils.hasValue(string)) {
                textView.setText(string);
                textView.setVisibility(View.VISIBLE);
            }
        }


        @Override
        public void onClick(View view) {
            if (onClickListener != null) {
                onClickListener.onClick(view, model);
            }
        }
    }
}
