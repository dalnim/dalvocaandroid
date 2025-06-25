package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.dalread.R;
import com.dalread.databinding.ItemPlayerMusicCategoryListLandscapeBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.MusicCategoryModel;
import com.dalread.util.Utils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MusicCategoryListAdapter extends RecyclerSwipeAdapter<MusicCategoryListAdapter.MusicCategoryListViewHolder> {

    private Context context;
    private List<MusicCategoryModel> musicCategoryList;
    private OnClickListener listener;
    private MusicCategoryModel currentMusicCategoryModel;

    public MusicCategoryListAdapter(Context context, MusicCategoryModel currentMusicCategoryModel, OnClickListener listener) {
        this.context = context;
        this.currentMusicCategoryModel = currentMusicCategoryModel;
        this.listener = listener;
    }


    @Override
    public MusicCategoryListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MusicCategoryListViewHolder(ItemPlayerMusicCategoryListLandscapeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
//        return new MusicCategoryListViewHolder(inflater.inflate(R.layout.item_player_music_category_list, parent, false)) ;
    }

    @Override
    public void onBindViewHolder(MusicCategoryListViewHolder viewHolder, int position) {
        ((MusicCategoryListViewHolder)viewHolder).bindData(musicCategoryList.get(position));
    }

    @Override
    public int getItemCount() {
        if (Utils.isEmptyCollection(musicCategoryList))
            return 0;
        return musicCategoryList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public void setData(List<MusicCategoryModel> musicCategoryList) {
        this.musicCategoryList = musicCategoryList;
        notifyDataSetChanged();
    }


    public class MusicCategoryListViewHolder extends RecyclerView.ViewHolder {
        private ItemPlayerMusicCategoryListLandscapeBinding binding;
        private MusicCategoryModel item;
        public MusicCategoryListViewHolder(@NonNull ItemPlayerMusicCategoryListLandscapeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            ButterKnife.bind(this, binding.getRoot());
        }

        public void bindData(MusicCategoryModel item) {
            this.item = item;
            binding.tvMusicCategoryTitle.setText(item.getTitle());

            updateTitleColor();
        }

        private void updateTitleColor() {

            if (item.getId() == currentMusicCategoryModel.getId()) {
                binding.tvMusicCategoryTitle.setTextColor(ContextCompat.getColor(context, R.color.primaryColor));
                binding.tvMusicCategoryMarker.setVisibility(View.VISIBLE);
            } else {
                binding.tvMusicCategoryTitle.setTextColor(ContextCompat.getColor(context, R.color.textSecondaryColor));
                binding.tvMusicCategoryMarker.setVisibility(View.INVISIBLE);
            }

        }

        @OnClick({R.id.llItem})
        void onItemClick(View view) {
            if (listener != null) {
                listener.onClick(view, item);
            }
        }
    }
}
