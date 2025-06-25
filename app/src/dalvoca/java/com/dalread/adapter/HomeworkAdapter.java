package com.dalread.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnHomeworkClickListener;
import com.dalread.model.VocaStudy;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Voca;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeworkAdapter extends RecyclerView.Adapter {

    private static final int TYPE_SUM = 0;
    private static final int TYPE_ITEM = TYPE_SUM + 1;

    private List<VocaStudy> vocaStudies;
    private long feedbackCount;
    private boolean displayPronunciation;
    private OnHomeworkClickListener listener;

    public HomeworkAdapter(boolean displayPronunciation) {
        this.displayPronunciation = displayPronunciation;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_SUM : TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SUM)
            return new SumHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_homework, parent, false));
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homework, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SumHolder) {
            ((SumHolder) holder).bind(getItemCount() - 1, feedbackCount, listener);
        } else if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind(vocaStudies.get(position - 1), displayPronunciation, listener); // position includes the summary view, - 1 to get correct index
        }
    }

    @Override
    public int getItemCount() {
        return 1 + (vocaStudies == null ? 0 : vocaStudies.size());
    }

    public void setData(List<VocaStudy> vocaStudies, long feedbackCount) {
        this.vocaStudies = vocaStudies;
        this.feedbackCount = feedbackCount;
    }

    public void setListener(OnHomeworkClickListener listener) {
        this.listener = listener;
    }

    public void notifyItemRemoved(VocaStudy voca) {
        int pos = vocaStudies.indexOf(voca);
        if (pos > -1) {
            vocaStudies.remove(pos);
            notifyItemRemoved(pos + 1);
            notifyItemChanged(0);
        }
    }

    public static class SumHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_word_count)
        TextView tvWordCount;
        @BindView(R.id.tv_feedback_count)
        TextView tvWordFeedback;

        @BindString(R.string.tpl_word_count)
        String tmplWordCount;
        @BindString(R.string.tpl_feedback_count)
        String tmplFeedbackCount;

        private OnHomeworkClickListener listener;

        public SumHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(int wordCount, long feedbackCount, OnHomeworkClickListener listener) {
            this.listener = listener;

            String text = String.format(tmplWordCount, wordCount);
            tvWordCount.setText(text);
            text = String.format(tmplFeedbackCount, feedbackCount);
            tvWordFeedback.setText(text);
        }

        @OnClick({R.id.ic_play_all})
        void onClick() {
            if (listener != null) {
                listener.onPlayAllClick();
            }
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_word_name)
        TextView tvWordName;
        @BindView(R.id.tv_word_meaning)
        TextView tvWordMeaning;
        @BindView(R.id.tv_tutor_name)
        TextView tvTutorName;
        @BindView(R.id.ic_play)
        ImageView icPlay;

        private VocaStudy voca;
        private boolean displayPronunciation;
        private OnHomeworkClickListener listener;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(VocaStudy voca, boolean displayPronunciation, OnHomeworkClickListener listener) {
            this.voca = voca;
            this.displayPronunciation = displayPronunciation;
            this.listener = listener;

            displayStarOrVoca();
            String text = voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(icPlay.getContext()));
            tvWordMeaning.setText(text);
            text = voca.getTutorName();
            tvTutorName.setText(text);
            Voca.updateIconSpeaker(icPlay, voca);
        }

        private void displayStarOrVoca() {
            String text;
            if (voca.isDisplayed()) {
                text = Voca.getVocaDisplay(voca);
                if (displayPronunciation && !TextUtils.isEmpty(voca.getPronounce())) {
                    text += " [" + voca.getPronounce() + "]";
                }
            } else {
                text = Voca.getStarForText(Voca.getVocaDisplay(voca));
            }
            tvWordName.setText(text);
        }

        @OnClick({R.id.ic_play, R.id.btn_study, R.id.v_foreground, R.id.tv_word_name})
        void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.ic_play:
                    if (listener != null) {
                        listener.onPlayClick(voca);
                    }
                    break;
                case R.id.btn_study:
                    if (listener != null) {
                        listener.onStudyClick(voca);
                    }
                    break;
                case R.id.v_foreground:
                    if (listener != null) {
                        listener.onInfoClick(voca);
                    }
                    break;
                case R.id.tv_word_name:
                    voca.setDisplayed(!voca.isDisplayed());
                    displayStarOrVoca();
                    break;
                default:
                    break;
            }
        }
    }
}
