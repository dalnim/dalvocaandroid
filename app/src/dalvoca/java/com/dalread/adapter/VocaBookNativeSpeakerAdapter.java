package com.dalread.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnVocaClickListener;
import com.dalread.model.VocaInBook;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VocaBookNativeSpeakerAdapter extends RecyclerView.Adapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_LOAD_MORE = TYPE_ITEM + 1;

    private ArrayList<Object> data;
    private OnVocaClickListener onVocaClickListener;

    public VocaBookNativeSpeakerAdapter() {
        data = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        Object aData = data.get(position);
        if (aData instanceof VocaInBook)
            return TYPE_ITEM;
        return TYPE_LOAD_MORE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM)
            return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voca_book_native_speaker, parent, false));
        return new LoadMoreHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind((VocaInBook) data.get(position), onVocaClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<VocaInBook> vocas) {
        data.clear();
        data.addAll(vocas);
    }

    public void setLoadMore(boolean enable) {
        int pos = data.size();
        if (enable) {
            data.add(null);
            notifyItemInserted(pos);
        } else if (data.remove(null)) {
            notifyItemRemoved(pos);
        }
    }

    public void setOnVocaClickListener(OnVocaClickListener onVocaClickListener) {
        this.onVocaClickListener = onVocaClickListener;
    }

    public void notifyVocaChanged(VocaInBook voca) {
        notifyItemChanged(data.indexOf(voca));
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_index)
        TextView tvIndex;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_meaning)
        TextView tvMeaning;
        @BindView(R.id.ic_play)
        ImageView icPlay;
        @BindView(R.id.ic_record)
        ImageView icRecord;

        @BindColor(R.color.color_play)
        int clPlay;
        @BindColor(R.color.color_stop)
        int clStop;

        private VocaInBook voca;
        private OnVocaClickListener onVocaClickListener;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(VocaInBook voca, OnVocaClickListener onVocaClickListener) {
            this.voca = voca;
            this.onVocaClickListener = onVocaClickListener;

            String text = Voca.getVocaDisplay(voca);
            if (TextUtils.isEmpty(text)) {
                text = voca.getVoca();
            }
            tvName.setText(text);
            text = voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(icPlay.getContext()));
            tvMeaning.setText(text);
            text = String.valueOf(voca.getIndex());
            tvIndex.setText(text);

            Voca.updateIconSpeaker(icPlay, voca);

            if (voca.isVIChecked()) { // is recording
                icRecord.setImageResource(R.drawable.ic_mic_black_24dp);
                icRecord.setColorFilter(clPlay);
            } else {
                icRecord.setImageResource(R.drawable.ic_mic_none_black_24dp);
                icRecord.setColorFilter(clStop);
            }
        }

        @OnClick({R.id.ic_play, R.id.ic_record})
        void onClick(View view) {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.ic_play:
                    if (onVocaClickListener != null) {
                        onVocaClickListener.onPlayClick(voca);
                    }
                    break;
                case R.id.ic_record:
                    if (onVocaClickListener != null) {
                        onVocaClickListener.onInfoClick(voca);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static class LoadMoreHolder extends RecyclerView.ViewHolder {

        public LoadMoreHolder(View itemView) {
            super(itemView);
        }
    }
}
