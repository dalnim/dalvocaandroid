package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.databinding.ItemAppDownloadBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.AppInfoModel;

import java.util.List;

public class AppDownloadListAdapter extends RecyclerView.Adapter<AppDownloadListAdapter.AppsViewHolder> {

    private final List<AppInfoModel> appList;
    private final LayoutInflater inflater;
    private static OnClickListener listener;
    private static Context context;
    public AppDownloadListAdapter(Context contextLocal,List<AppInfoModel> appList, OnClickListener listener) {
        context = contextLocal;
        this.appList = appList;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAppDownloadBinding binding = ItemAppDownloadBinding.inflate(inflater, parent, false);
        return new AppsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AppsViewHolder holder, int position) {
        holder.bind(appList.get(position));
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }
    static class AppsViewHolder extends RecyclerView.ViewHolder {
        private final ItemAppDownloadBinding binding;

        public AppsViewHolder(ItemAppDownloadBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AppInfoModel appInfoModel) {
            binding.appTitle.setText(appInfoModel.getTitle());
            binding.appDescription.setText(appInfoModel.getDescription());
            binding.appIcon.setImageDrawable(appInfoModel.getIcon());
            binding.downloadButton.setOnClickListener(v -> {
                // Implement download action
                if (listener != null) {
                    listener.onClick(v, appInfoModel);
                }
            });
        }
    }


}