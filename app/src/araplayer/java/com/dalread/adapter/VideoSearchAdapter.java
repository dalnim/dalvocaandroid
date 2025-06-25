package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemVideoSearchBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.VideoInformationModel;
import com.dalread.util.Constant;
import com.dalread.util.TextViewUtil;
import com.dalread.util.UtilImage;

import java.util.ArrayList;
import java.util.List;

public class VideoSearchAdapter extends BaseAdapter<VideoSearchAdapter.ViewHolder> {

    private List<VideoInformationModel> items = new ArrayList<>();
    private OnClickListener listener;
    private Context context;
    private int typeDisplay = Constant.PLAYER.THE_MOVIE_DB.TYPE.NONE;

    public VideoSearchAdapter(Context context, OnClickListener listener) {
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
        return new ViewHolder(ItemVideoSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

//        return new ViewHolder(getInflater().inflate(R.layout.item_video_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public void notifyDataSetChanged(List<?> dataList) {

    }

    public void setData(List<VideoInformationModel> items) {
        setData(items, Constant.PLAYER.THE_MOVIE_DB.TYPE.NONE);
    }

    public void setData(List<VideoInformationModel> items, int type) {
        if (items == null) {
            this.items.clear();
            notifyDataSetChanged();
            return;
        }
        this.typeDisplay = type;
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//        @BindView(R.id.iv_image) ImageView ivImage;
//        @BindView(R.id.tv_title) TextView tvTitle;
//        @BindView(R.id.tv_description) TextView tvDescription;

        private ItemVideoSearchBinding binding;
        ViewHolder(ItemVideoSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
//            binding.llItem.setOnClickListener(this);
            binding.ivImage.setOnClickListener(this);
            binding.llVideoList.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener == null) return;
            listener.onClick(view, items.get(getBindingAdapterPosition()));
        }

//        @OnClick(R.id.llItem)
//        void onClick(View v) {
//            if (listener == null) return;
//            listener.onClick(v, items.get(getAdapterPosition()));
//        }

        public void bind(VideoInformationModel item) {
            UtilImage.getThumbnail(context, binding.ivImage, item.getPosterURL(), null);
            if (typeDisplay == Constant.PLAYER.THE_MOVIE_DB.TYPE.SEASON) {
                binding.tvTitle.setText(context.getString(R.string.format_episodes, item.getNameDisplay()));
            } else if (typeDisplay == Constant.PLAYER.THE_MOVIE_DB.TYPE.EPISODE) {
                binding.tvTitle.setText(context.getString(R.string.format_episode_name, item.getEpisodeNumber(), item.getNameDisplay()));
            } else {
                binding.tvTitle.setText(item.getNameDisplayWithOrigianlName());
            }
//            tvTitle.setText(typeDisplay == Constant.PLAYER.THE_MOVIE_DB.TYPE.SEASON ? context.getString(R.string.format_episodes, item.getNameDisplay()) :
//                    item.getEpisodeNumber() + " : " + item.getNameDisplay());
            TextViewUtil.makeTextViewMiddleTruncate(binding.tvTitle, 3);
            String description = Constant.BASE_BLANK;
            if (isNone()) {
                description += item.getMediaType() + " / ";
            }
            description += item.getDate();
            binding.tvDescription.setText(description);
        }
    }

    public int getTypeDisplay() {
        return typeDisplay;
    }

    public boolean isNone() {
        return getTypeDisplay() == Constant.PLAYER.THE_MOVIE_DB.TYPE.NONE;
    }

    public boolean isSeason() {
        return getTypeDisplay() == Constant.PLAYER.THE_MOVIE_DB.TYPE.SEASON;
    }

    public boolean isEpisode() {
        return getTypeDisplay() == Constant.PLAYER.THE_MOVIE_DB.TYPE.EPISODE;
    }
}