package com.dalread.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemGroupBinding;
import com.dalread.databinding.ItemGroupedRadicalBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.DIC_HANJA;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressLint("NonConstantResourceId")
public class RadicalGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String PAYLOAD_SELECTED = "PAYLOAD_SELECTED";

    private static final int TYPE_GROUP = 0;
    private static final int TYPE_RADICAL = 1;

    private final List<Object> data = new ArrayList<>();
    private int selectedPosition;
    private OnClickListener listener;
    private Context context;

    public RadicalGroupAdapter(Context context, OnClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position) instanceof Long ? TYPE_GROUP : TYPE_RADICAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_GROUP) {
            return new GroupHolder(ItemGroupBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
        return new RadicalHolder(ItemGroupedRadicalBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GroupHolder) {
            ((GroupHolder) holder).bind((Long) data.get(position));
        } else if (holder instanceof RadicalHolder) {
            ((RadicalHolder) holder).bind((DIC_HANJA) data.get(position));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else if (holder instanceof RadicalHolder) {
            ((RadicalHolder) holder).bindBackground();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(Map<Long, List<DIC_HANJA>> groupMap, Long selectedStroke, int selectedPosition) {
        data.clear();
        this.selectedPosition = selectedPosition;
        if (groupMap.containsKey(selectedStroke)) {
            data.addAll(groupMap.get(selectedStroke));
        }
//        //Call first item
//        if (getItemCount() > 0) {
//            selectedPosition = 0; // the first radical of the first group
//            if (listener != null) {
//                listener.onClick(null, data.get(selectedPosition));
//            }
//        }
    }
//
//    public void setData(Map<Long, List<DIC_HANJA>> groupMap) {
//        data.clear();
//        selectedPosition = -1;
//        for (Long group : groupMap.keySet()) {
//            data.add(group);
//            data.addAll(groupMap.get(group));
//        }
//        if (getItemCount() > 1) {
//            selectedPosition = 1; // the first radical of the first group
//            if (listener != null) {
//                listener.onClick(null, data.get(selectedPosition));
//            }
//        }
//    }

    static class GroupHolder extends RecyclerView.ViewHolder {

        private ItemGroupBinding binding;
        GroupHolder(ItemGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        public void bind(long group) {
            binding.tvGroup.setText(String.valueOf(group));
        }
    }

    class RadicalHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemGroupedRadicalBinding binding;
        RadicalHolder(ItemGroupedRadicalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.vItem.setOnClickListener(this);
        }


        public void bind(DIC_HANJA hanja) {
            binding.tvRadical.setText(hanja.getVOCA());
            binding.tvPronounce.setText(hanja.getMEANING1() + " " + hanja.getPRONOUNCE1_FIRST());
            bindBackground();
        }

        public void bindBackground() {
            binding.vItem.setBackgroundColor(
                    getAbsoluteAdapterPosition() == selectedPosition
                            ? ContextCompat.getColor(context, R.color.color_grouped_radical_selected)
                            : ContextCompat.getColor(context, R.color.color_grouped_radical_normal)
            );
        }

        @Override
        public void onClick(View view) {
            int oldSelectedPosition = selectedPosition;
            selectedPosition = getAbsoluteAdapterPosition();
            notifyItemChanged(oldSelectedPosition, PAYLOAD_SELECTED);
            notifyItemChanged(selectedPosition, PAYLOAD_SELECTED);
            if (listener != null) {
                listener.onClick(null, data.get(selectedPosition));
            }
        }
    }
}
