package com.dalread.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnStudyHistoryClickListener;
import com.dalread.model.VocaStudyHistory;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StudyHistoryWordAdapter extends RecyclerView.Adapter {

    private static final int TYPE_SUM = 0;
    private static final int TYPE_ITEM = TYPE_SUM + 1;
    private static final int TYPE_LOAD_MORE = TYPE_ITEM + 1;

    private ArrayList<Object> data = new ArrayList<>();
    private boolean displayPronunciation;
    private OnStudyHistoryClickListener listener;

    public StudyHistoryWordAdapter(boolean displayPronunciation) {
        this.displayPronunciation = displayPronunciation;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) instanceof Integer)
            return TYPE_SUM;
        if (data.get(position) instanceof VocaStudyHistory)
            return TYPE_ITEM;
        return TYPE_LOAD_MORE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SUM)
            return new SumHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_history, parent, false));
        if (viewType == TYPE_ITEM)
            return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_word, parent, false));
        return new LoadMoreHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SumHolder) {
            ((SumHolder) holder).bind((Integer) data.get(position), listener);
        } else if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind((VocaStudyHistory) data.get(position), displayPronunciation, listener);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<VocaStudyHistory> histories) {
        data.clear();
        data.add(histories.size());
        data.addAll(histories);
    }

    public void setListener(OnStudyHistoryClickListener listener) {
        this.listener = listener;
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

    public void notifyPlayingStatusChanged(VocaStudyHistory voca) {
        notifyItemChanged(data.indexOf(voca));
    }

    public static class SumHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_count)
        TextView tvCount;

        @BindString(R.string.tpl_aw_word_count)
        String tpl;

        private OnStudyHistoryClickListener listener;

        public SumHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(int count, OnStudyHistoryClickListener listener) {
            this.listener = listener;
            tvCount.setText(String.format(tpl, count));
        }

        @OnClick(R.id.ic_play_all)
        void onClick() {
            if (listener != null) {
                listener.onPlayAllClick(null);
            }
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_study_count)
        TextView tvStudyCount;
        @BindView(R.id.tv_word_name)
        TextView tvWordName;
        @BindView(R.id.tv_word_meaning)
        TextView tvWordMeaning;
        @BindView(R.id.ic_play)
        ImageView icPlay;

        @BindString(R.string.tpl_study_count)
        String tplStudyCount;

        private VocaStudyHistory history;
        private OnStudyHistoryClickListener listener;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(VocaStudyHistory history, boolean displayPronunciation, OnStudyHistoryClickListener listener) {
            this.history = history;
            this.listener = listener;

            String text = String.format(tplStudyCount, history.getStudyCount());
            tvStudyCount.setText(text);
            text = Voca.getVocaDisplay(history);
            if (displayPronunciation && !TextUtils.isEmpty(history.getPronounce())) {
                text += " [" + history.getPronounce() + "]";
            }
            tvWordName.setText(text);
            text = history.getVIMeaning(LanguageUtil.getMotherTongueLanguage(icPlay.getContext()));
            tvWordMeaning.setText(text);
            Voca.updateIconSpeaker(icPlay, history);
        }

        @OnClick({R.id.ic_play, R.id.v_item})
        void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.ic_play:
                    if (listener != null) {
                        listener.onPlayClick(history);
                    }
                    break;
                case R.id.v_item:
                    if (listener != null) {
                        listener.onInfoClick(history);
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
