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
import com.dalread.databinding.ItemGroupedRadicalBinding;
import com.dalread.listener.OnClickListener;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NonConstantResourceId")
public class StrokeGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String PAYLOAD_SELECTED = "PAYLOAD_SELECTED";

    private final List<String> data = new ArrayList<>();
    private int selectedPosition;
    private OnClickListener listener;

    private Context context;

    public StrokeGroupAdapter(Context context, OnClickListener listener) {
        this.context = context;
        this.listener = listener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GroupHolder(ItemGroupedRadicalBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GroupHolder) {
            ((GroupHolder) holder).bind(data.get(position));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else if (holder instanceof GroupHolder) {
            ((GroupHolder) holder).bindBackground();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<String> groupList, int selectedPosition) {
        data.clear();
        data.addAll(groupList);
        if (getItemCount() > 0) {
            this.selectedPosition = selectedPosition;
            if (listener != null) {
                listener.onClick(null, data.get(selectedPosition));
            }
        }
    }


    class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemGroupedRadicalBinding binding;
        GroupHolder(ItemGroupedRadicalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.vItem.setOnClickListener(this);
        }

        public void bind(Object group) {
            binding.tvRadical.setText(String.valueOf(group));
            binding.tvPronounce.setVisibility(View.GONE);
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
