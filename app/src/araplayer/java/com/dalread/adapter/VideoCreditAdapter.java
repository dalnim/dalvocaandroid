package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemVideoCreditBinding;
import com.dalread.databinding.ItemVideoCreditHeaderBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.VideoInformationModel;
import com.dalread.util.UtilImage;
import com.dalread.util.Utils;

import java.util.ArrayList;

public class VideoCreditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_HEADER = R.layout.item_video_credit_header;
    private final int VIEW_TYPE_ITEM = R.layout.item_video_credit;

    private ArrayList<VideoInformationModel.People> items;
    private Context context;
    private OnClickListener listener;
    private String director;

    public VideoCreditAdapter(Context context, OnClickListener listener) {
        this.context = context;
        items = new ArrayList<>();
        this.listener = listener;
        director = context.getString(R.string.director);
    }

    @Override
    public int getItemViewType(int position) {
        if (Utils.isEmpty(items.get(position).getName()))
            return VIEW_TYPE_HEADER;
        return VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER)
            return new HeaderViewHolder(ItemVideoCreditHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        return new ItemViewHolder(ItemVideoCreditBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(items.get(position));
        } else {
            ((ItemViewHolder) holder).bind(items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setData(ArrayList<VideoInformationModel.People> data) {
        this.items.clear();
        this.items.addAll(data);
        notifyDataSetChanged();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.tvTitle) TextView tvTitle;
        private ItemVideoCreditHeaderBinding binding;
        HeaderViewHolder(ItemVideoCreditHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(VideoInformationModel.People item) {
            binding.tvTitle.setText(item.getJob());
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        @BindView(R.id.izb_profile) ImageView izbProfile;
//        @BindView(R.id.tv_name) TextView tvName;
//        @BindView(R.id.tv_character_name) TextView tvCharacterName;

        private ItemVideoCreditBinding binding;
        ItemViewHolder(ItemVideoCreditBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.llItem.setOnClickListener(this);
            binding.ivProfile.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, items.get(getAdapterPosition()));
            }
        }


//
//        @OnClick({R.id.llItem, R.id.izb_profile})
//        void onClick(View v) {
//            if (listener == null) return;
//            listener.onClick(v, items.get(getAdapterPosition()));
//        }

        public void bind(VideoInformationModel.People item) {
            UtilImage.getThumbnailCircle(context, binding.ivProfile, R.mipmap.ic_no_avatar, item.getImagePath(), null);
            if (item.getJob().contentEquals(director)) {
                binding.tvName.setText(item.getJob());
                binding.tvCharacterName.setText(item.getName());
            } else {
                binding.tvName.setText(item.getName());
                binding.tvCharacterName.setText(item.getCharacter());
            }
        }
    }
}
