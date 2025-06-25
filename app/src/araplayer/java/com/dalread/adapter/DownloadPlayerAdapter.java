package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dalread.R;
import com.dalread.databinding.ItemPlayerDownloadBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.DownloadModel;
import com.dalread.util.Constant;
import com.dalread.util.DownloadUtil;
import com.dalread.util.Utils;
import java.util.List;

public class DownloadPlayerAdapter extends RecyclerView.Adapter<DownloadPlayerAdapter.ItemHolder> {

    private Context context;
    private List<DownloadModel> datas;
    private OnClickListener listener;

    public DownloadPlayerAdapter(Context context, List<DownloadModel> data, OnClickListener listener) {
        this.context = context;
        setDatas(data);
        this.listener = listener;
    }

    @Override
    public DownloadPlayerAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(ItemPlayerDownloadBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.bind(datas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public DownloadModel getItem(int position) {
        return datas.get(position);
    }

    public void setDatas(List<DownloadModel> datas) {
        this.datas = datas;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemPlayerDownloadBinding binding;
        private DownloadModel item;

        public ItemHolder(@NonNull ItemPlayerDownloadBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.ivStatus.setOnClickListener(this);
            binding.llItem.setOnClickListener(this);
        }

        public void bind(DownloadModel item, int position) {
            this.item = item;
            this.item.setPosition(position);
            binding.tvName.setText(item.getName());
            String status;
            String downloadingInfo = DownloadUtil.getDownloadInfo(item);
            switch (item.getStatus()) {
                case Constant.PLAYER.SERVER.DOWNLOAD.STATUS.COMPLETE:
                    status = context.getString(R.string.download_msg_completed);
                    binding.ivStatus.setImageResource(R.drawable.ic_download_completed_gray_24dp);
                    break;
                case Constant.PLAYER.SERVER.DOWNLOAD.STATUS.DOWNLOAD:
                    binding.ivStatus.setImageResource(R.drawable.ic_download_gray_24dp);
                    status = context.getString(R.string.download_msg_downloading)  + " " + downloadingInfo;
                    if (Utils.isEmpty(downloadingInfo)) {
                        status = context.getString(R.string.download_msg_download_fail);
                    }
                    break;
                case Constant.PLAYER.SERVER.DOWNLOAD.STATUS.PAUSE:
                    binding.ivStatus.setImageResource(R.drawable.ic_download_pause_gray_24dp);
                    status = context.getString(R.string.download_msg_pause)  + " " + downloadingInfo;
                    if (Utils.isEmpty(downloadingInfo)) {
                        status = context.getString(R.string.download_msg_download_fail);
                    }
                    break;
                default:
                    status = context.getString(R.string.download_msg_waiting);
                    binding.ivStatus.setImageResource(R.drawable.ic_download_pause_gray_24dp);
                    break;
            }
            binding.tvDescription.setText(status);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.llItem:
                case R.id.ivStatus:
                    if (item.getStatus() == Constant.PLAYER.SERVER.DOWNLOAD.STATUS.COMPLETE) {
                        return;
                    }
                    if (listener != null) {
                        listener.onClick(v, item);
                    }
                    break;
            }
        }
    }
}
