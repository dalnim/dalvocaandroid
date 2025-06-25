package com.dalread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnPlaylistItemClickListener;
import com.dalread.model.StudentVoice;
import com.dalread.util.Voca;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NativeSpeakersVoiceAdapter extends RecyclerView.Adapter {

    private SharedPreferencesDB sharedPreferences;
    private ArrayList<IVocaFullPlayTTSItem> items;
    private OnPlaylistItemClickListener listener;

    public NativeSpeakersVoiceAdapter(SharedPreferencesDB sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_native_speaker_voice, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind(sharedPreferences, items.get(position), listener);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setData(ArrayList<IVocaFullPlayTTSItem> items) {
        this.items = items;
    }

    public void setListener(OnPlaylistItemClickListener listener) {
        this.listener = listener;
    }

    public int notifyPlaylistItemChanged(IVocaFullPlayTTSItem item) {
        int pos = items.indexOf(item);
        notifyItemChanged(pos);
        return pos;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.v_item)
        View vItem;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_nation)
        TextView tvNation;
        @BindView(R.id.ic_check)
        ImageView icCheck;
        @BindView(R.id.ic_play)
        ImageView icPlay;
        @BindView(R.id.ic_avatar)
        ImageView icAvatar;

        private StudentVoice item;
        private OnPlaylistItemClickListener listener;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(SharedPreferencesDB sharedPreferences, IVocaFullPlayTTSItem playlistItem, OnPlaylistItemClickListener listener) {
            item = (StudentVoice) playlistItem;
            this.listener = listener;

            vItem.setBackgroundResource(item.isVIPlaying() ? R.color.color_playing_background : R.color.colorBackground);
            icCheck.setVisibility(item.isVIChecked() ? View.VISIBLE : View.INVISIBLE);
            String text = Voca.getUserDisplayName(sharedPreferences, item);
            tvName.setText(text);
            text = Voca.getUserDisplayLocation(itemView.getContext(), item);
            tvNation.setText(text);
            Voca.updateIconSpeaker(icPlay, item);
            icAvatar.setImageResource(Voca.getAvatarResource(item.getSex(), item.getAge()));
        }

        @OnClick({R.id.v_item, R.id.ic_play, R.id.ic_avatar})
        void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.v_item:
                    if (listener != null) {
                        listener.onItemClick(item);
                    }
                    break;
                case R.id.ic_play:
                    if (listener != null) {
                        listener.onPlayClick(item);
                    }
                    break;
                case R.id.ic_avatar:
                    break;
                default:
                    break;
            }
        }
    }
}
