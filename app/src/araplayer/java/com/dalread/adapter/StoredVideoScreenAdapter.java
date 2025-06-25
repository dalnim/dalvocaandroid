package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.dalread.R;
import com.dalread.database.sqlite.model.MultiPlayerVideoStoredModel;
import com.dalread.databinding.ItemPlayerStoredVideoScreenBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.util.UtilImage;
import com.dalread.util.Utils;

import java.util.List;

public class StoredVideoScreenAdapter extends RecyclerSwipeAdapter<StoredVideoScreenAdapter.ViewHolder> {
    private Context context;
    private List<MultiPlayerVideoStoredModel> list;
    private OnClickListener listener;


    public StoredVideoScreenAdapter(Context context, OnClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemPlayerStoredVideoScreenBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.bindData(list.get(position));
    }

    @Override
    public int getItemCount() {
        if (Utils.isEmptyCollection(list))
            return 0;
        return list.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public void setData(List<MultiPlayerVideoStoredModel> videoSeriesModelList) {
        this.list = videoSeriesModelList;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MultiPlayerVideoStoredModel model;
        private ItemPlayerStoredVideoScreenBinding binding;
        public ViewHolder(@NonNull ItemPlayerStoredVideoScreenBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }
        private void setOnClickListeners() {
            binding.root.setOnClickListener(this);
            binding.ivTvSeasonPoster.setOnClickListener(this);
        }
        public void bindData(MultiPlayerVideoStoredModel model) {
            this.model = model;
            updatePosterUI();
        }

        private void updatePosterUI() {
            binding.ivTvSeasonPoster.post(() -> {
                UtilImage.getThumbnailFromSavedVideoImageFile(context, binding.ivTvSeasonPoster, model, false, null);
            });
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onClick(v, model);
            }
        }
    }
}
