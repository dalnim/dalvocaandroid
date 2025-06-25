package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemPlaylistSongBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.PlaylistModel;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlaylistToAddSongsAdapter extends RecyclerView.Adapter<PlaylistToAddSongsAdapter.PlaylistViewHolder> {
    private Context context;
    private List<PlaylistModel> items;
    private OnClickListener listener;

    public PlaylistToAddSongsAdapter(Context context, List<PlaylistModel> items, OnClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistViewHolder(ItemPlaylistSongBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        holder.bindData(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setData(List<PlaylistModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {
        private ItemPlaylistSongBinding binding;
        private PlaylistModel playlistModel;

        public PlaylistViewHolder(ItemPlaylistSongBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            ButterKnife.bind(this, binding.getRoot());
        }

        public void bindData(PlaylistModel item) {
            playlistModel = item;
            binding.ivEdit.setVisibility(View.GONE);
            binding.tvPlaylistName.setTextColor(ContextCompat.getColor(context, R.color.white));
            binding.tvPlaylistName.setText(item.getName());
            int songTextId;
            if (item.getTotalSongs() > 1) {
                songTextId = R.string.more_than_1_songs;
            } else {
                songTextId = R.string.less_or_equal_than_1_songs;
            }
            binding.tvTotalSong.setText(String.format(context.getString(songTextId), item.getTotalSongs()));
        }

        @OnClick({R.id.llPlaylist})
        void onItemClick(View view) {
            if (listener != null) {
                listener.onClick(view, playlistModel);
            }
        }
    }
}
