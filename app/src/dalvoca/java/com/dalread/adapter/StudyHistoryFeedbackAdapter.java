package com.dalread.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnStudyHistoryFeedbackClickListener;
import com.dalread.model.VocaFeedback;
import com.dalread.util.Constant;
import com.dalread.util.DateUtils;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StudyHistoryFeedbackAdapter extends RecyclerView.Adapter {

    private static final int TYPE_SUM = 0;
    private static final int TYPE_ITEM = TYPE_SUM + 1;

    private ArrayList<Object> data = new ArrayList<>();
    private boolean displayPronunciation;
    private OnStudyHistoryFeedbackClickListener listener;

    public StudyHistoryFeedbackAdapter(boolean displayPronunciation) {
        this.displayPronunciation = displayPronunciation;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) instanceof VocaFeedback)
            return TYPE_ITEM;
        return TYPE_SUM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SUM)
            return new SumHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_history, parent, false));
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_feedback, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SumHolder) {
            ((SumHolder) holder).bind((String) data.get(position), listener);
        } else if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind((VocaFeedback) data.get(position), displayPronunciation, listener);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<VocaFeedback> vocas) {
        data.clear();
        int size = vocas.size();
        if (size > 0) {
            VocaFeedback previousVoca = vocas.get(0);
            data.add(previousVoca);
            int count = 1;
            if (size > 1) {
                for (int i = 1; i < vocas.size(); i++) {
                    VocaFeedback voca = vocas.get(i);
                    data.add(voca);
                    if (DateUtils.isSameDate(
                            new Date(DateUtils.secondsToMillis(voca.getFeedbackDate())),
                            new Date(DateUtils.secondsToMillis(previousVoca.getFeedbackDate()))
                    )) {
                        count++;
                    } else {
                        int pos = data.size() - 1 - count;
                        data.add(pos, getHeaderText(previousVoca, count));
                        count = 1;
                    }
                    if (i < vocas.size() - 1) {
                        previousVoca = voca;
                    } else {
                        int pos = data.size() - count;
                        data.add(pos, getHeaderText(voca, count));
                    }
                }
            } else {
                data.add(0, getHeaderText(previousVoca, count));
            }
        }
    }

    private String getHeaderText(VocaFeedback voca, int count) {
        return voca.getFeedbackDate() + Constant.BREAK_SIGN + count;
    }

    public void setListener(OnStudyHistoryFeedbackClickListener listener) {
        this.listener = listener;
    }

    public void notifyItemChanged(VocaFeedback voca) {
        notifyItemChanged(data.indexOf(voca));
    }

    public static class SumHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_count)
        TextView tvCount;

        private String tpl = "%1$s (%2$s)";
        private OnStudyHistoryFeedbackClickListener listener;
        private Date date;

        public SumHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(String header, OnStudyHistoryFeedbackClickListener listener) {
            this.listener = listener;

            String[] split = header.split(Constant.BREAK_SIGN);
            try {
                date = new Date(DateUtils.secondsToMillis(Long.parseLong(split[0])));
                String strDate = DateUtils.getDateVoca(itemView.getContext(), date);
                String count = split[1];
                tvCount.setText(String.format(tpl, strDate, count));
            } catch (Exception e) {
                e.printStackTrace();
                tvCount.setText("");
            }
        }

        @OnClick(R.id.ic_play_all)
        void onClick() {
            if (listener != null) {
                listener.onPlayAllClick(date);
            }
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_word_name)
        TextView tvWordName;
        @BindView(R.id.tv_word_meaning)
        TextView tvWordMeaning;
        @BindView(R.id.tv_feedback)
        TextView tvFeedback;
        @BindView(R.id.tv_tutor_name)
        TextView tvTutorName;
        @BindView(R.id.ic_play)
        ImageView icPlay;

        private VocaFeedback voca;
        private OnStudyHistoryFeedbackClickListener listener;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(VocaFeedback voca, boolean displayPronunciation, OnStudyHistoryFeedbackClickListener listener) {
            this.voca = voca;
            this.listener = listener;

            String text = Voca.getVocaDisplay(voca);
            if (displayPronunciation && !TextUtils.isEmpty(voca.getPronounce())) {
                text += " [" + voca.getPronounce() + "]";
            }
            tvWordName.setText(text);
            text = voca.getMeaning();
            tvWordMeaning.setText(text);
            text = voca.getFeedbackMessage();
            tvFeedback.setText(text);
            text = Voca.getEvaluateText(tvTutorName.getContext(), voca.getEvaluateVocaGrade());
            if (!TextUtils.isEmpty(voca.getTutorName())) {
                text += " (" + voca.getTutorName() + ")";
            }
            tvTutorName.setText(text);
            Voca.updateIconSpeaker(icPlay, voca);
        }

        @OnClick({R.id.ic_play, R.id.v_item})
        void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.ic_play:
                    if (listener != null) {
                        listener.onPlayClick(voca);
                    }
                    break;
                case R.id.v_item:
                    if (listener != null) {
                        listener.onInfoClick(voca);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
