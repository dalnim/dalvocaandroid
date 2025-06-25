package com.dalread.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnFeedbackClickListener;
import com.dalread.model.VocaFeedback;
import com.dalread.util.Voca;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackAdapter extends RecyclerView.Adapter {

    private static final int TYPE_SUM = 0;
    private static final int TYPE_ITEM = TYPE_SUM + 1;

    private List<VocaFeedback> vocaFeedbacks;
    private long studyCount;
    private OnFeedbackClickListener listener;

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_SUM : TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SUM)
            return new SumHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_homework, parent, false));
        return new FeedbackHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SumHolder) {
            ((SumHolder) holder).bind(studyCount, getItemCount() - 1, listener);
        } else if (holder instanceof FeedbackHolder) {
            ((FeedbackHolder) holder).bind(vocaFeedbacks.get(position - 1), listener);
        }
    }

    @Override
    public int getItemCount() {
        return 1 + (vocaFeedbacks == null ? 0 : vocaFeedbacks.size());
    }

    public void setData(List<VocaFeedback> studyWords, long studyCount) {
        this.vocaFeedbacks = studyWords;
        this.studyCount = studyCount;
    }

    public void setListener(OnFeedbackClickListener listener) {
        this.listener = listener;
    }

    public static class SumHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_word_count)
        TextView tvWordCount;
        @BindView(R.id.tv_feedback_count)
        TextView tvWordFeedback;

        @BindString(R.string.tpl_word_count)
        String tplWordCount;
        @BindString(R.string.tpl_feedback_count)
        String tplFeedbackCount;

        private OnFeedbackClickListener listener;

        public SumHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(long wordCount, long feedbackCount, OnFeedbackClickListener listener) {
            this.listener = listener;

            String text = String.format(tplWordCount, wordCount);
            tvWordCount.setText(text);
            text = String.format(tplFeedbackCount, feedbackCount);
            tvWordFeedback.setText(text);
        }

        @OnClick({R.id.ic_play_all})
        void onClick() {
            if (listener != null) {
                listener.onPlayAllClick();
            }
        }
    }

    public static class FeedbackHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_word_name)
        TextView tvWordName;
        @BindView(R.id.tv_word_meaning)
        TextView tvWordMeaning;
        @BindView(R.id.tv_tutor_name)
        TextView tvTutorName;
        @BindView(R.id.tv_feedback)
        TextView tvFeedback;
        @BindView(R.id.ic_play)
        ImageView icPlay;

        private VocaFeedback voca;
        private OnFeedbackClickListener listener;

        public FeedbackHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(VocaFeedback voca, OnFeedbackClickListener listener) {
            this.voca = voca;
            this.listener = listener;

            String text = Voca.getVocaDisplay(voca);
            if (!TextUtils.isEmpty(voca.getPronounce())) {
                text += " [" + voca.getPronounce() + "]";
            }
            tvWordName.setText(text);
            text = voca.getMeaning();
            tvWordMeaning.setText(text);
            text = voca.getFeedbackScore() + "/5 - " + voca.getTutorName(); // temp display score, will show star view later
            tvTutorName.setText(text);
            text = voca.getFeedbackMessage();
            tvFeedback.setText(text);
            tvFeedback.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
            Voca.updateIconSpeaker(icPlay, voca);
        }

        @OnClick({R.id.ic_play, R.id.btn_confirm})
        void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.ic_play:
                    if (listener != null) {
                        listener.onPlayClick(voca);
                    }
                    break;
                case R.id.btn_confirm:
                    if (listener != null) {
                        listener.onConfirmClick(voca);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
