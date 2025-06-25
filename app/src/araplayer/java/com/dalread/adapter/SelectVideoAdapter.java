package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.databinding.ItemSelectVideoBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.StorageUtil;
import com.dalread.util.UtilImage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class SelectVideoAdapter extends BaseAdapter<SelectVideoAdapter.ViewHolder> {

    private List<PlayerFileModel> items = new ArrayList<>();
    private OnClickListener listener;
    private Context context;

    public SelectVideoAdapter(Context context, OnClickListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemSelectVideoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public void notifyDataSetChanged(List<?> dataList) {

    }

    public void setData(List<PlayerFileModel> items) {
        if (items == null) {
            this.items.clear();
            notifyDataSetChanged();
            return;
        }
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemSelectVideoBinding binding;
        ViewHolder(ItemSelectVideoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.llItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener == null) return;
            listener.onClick(view, items.get(getBindingAdapterPosition()));
        }

        public void bind(PlayerFileModel item) {
            binding.tvTitle.setText(item.getName());
            binding.izbVideoThumbnail.post(() -> {
                UtilImage.getThumbnailFromSavedVideoImageFile(context, binding.izbVideoThumbnail, item, false, null);
            });
            binding.tvDate.setText(DateFormat.getDateInstance(DateFormat.DEFAULT).format(item.getCreatedDate()));
            binding.tvSize.setText(StorageUtil.getDynamicSpace(item.getSize()));
        }
    }
}