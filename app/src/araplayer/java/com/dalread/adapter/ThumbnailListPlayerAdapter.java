package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.databinding.ItemThumbnailListPlayerItemBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.Constant;
import com.dalread.util.TimeUtil;
import com.dalread.util.UtilImage;

import java.util.ArrayList;

public class ThumbnailListPlayerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private PlayerFileModel playerFileModel;
    private ArrayList<Long> thumbnailIntervalList;
    private Context context;
    private OnClickListener listener;

    public ThumbnailListPlayerAdapter(Context context, PlayerFileModel playerFileModel, OnClickListener listener) {
        this.playerFileModel = playerFileModel;
        this.context = context;
        this.listener = listener;
        thumbnailIntervalList = new ArrayList<>();
    }

//    @Override
//    public int getItemViewType(int position) {
//        return R.layout.item_thumbnail_list_player_item;
//    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemRubyViewHolder(ItemThumbnailListPlayerItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View view = inflater.inflate(ItemThumbnailListPlayerItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//        return new ItemRubyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Long thumbnailTime = thumbnailIntervalList.get(position);
        ((ItemRubyViewHolder) holder).bind(thumbnailTime, position);
    }

    @Override
    public int getItemCount() {
        return thumbnailIntervalList.size();
    }

    public void setThumbnailIntervalList(ArrayList<Long> thumbnailIntervalList) {
        this.thumbnailIntervalList.clear();
        this.thumbnailIntervalList.addAll(thumbnailIntervalList);
        notifyDataSetChanged();
    }

    public class ItemRubyViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, View.OnClickListener {
//        @BindView(R.id.tvVideoStartTime) TextView tvVideoStartTime;
//        @BindView(R.id.izbVideoThumbnail) ImageView izbVideoThumbnail;

        public int position;

        private ItemThumbnailListPlayerItemBinding binding;
        ItemRubyViewHolder(ItemThumbnailListPlayerItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
            resizeBackdropImageView();
        }

        private void setOnClickListeners() {
            binding.izbVideoThumbnail.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, position);
            }
        }


//        public ItemRubyViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//            resizeBackdropImageView();
//        }

        private void resizeBackdropImageView() {
            binding.izbVideoThumbnail.post(new Runnable() {
                @Override
                public void run() {
                    binding.izbVideoThumbnail.getLayoutParams().height = (int) (binding.izbVideoThumbnail.getWidth() * Constant.PLAYER.THUMBNAIL_HEIGHT_RATIO_BY_WIDTH);
                }
            });
        }

        public void bind(Long thumbnailTime, int position) {
            this.position = position;
            String displayTime = TimeUtil.getDisplay(thumbnailTime);
            binding.tvVideoStartTime.setText((position + 1 ) + "/" + thumbnailIntervalList.size() + ") " + displayTime);
            binding.izbVideoThumbnail.post(() -> UtilImage.getThumbnailVideoFile(context, binding.izbVideoThumbnail, playerFileModel.getPath(), thumbnailTime, playerFileModel.getVideoModel().getDuration(), false, null));
        }

//        @OnClick({R.id.izbVideoThumbnail})
//        void onClick(View v) {
//            if (listener != null) {
//                listener.onClick(v, position);
//            }
//        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return false;
        }
    }
}
