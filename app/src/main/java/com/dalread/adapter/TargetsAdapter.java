package com.dalread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnTargetsClickListener;
import com.dalread.model.VocaMemorize;
import com.dalread.util.BaseVoca;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.Constant;
import com.dalread.util.LanguageUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TargetsAdapter extends RecyclerView.Adapter {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = TYPE_HEADER + 1;

    private ArrayList<Object> data;
    private int count1;
    private int count2;
    private int count99;
    private boolean displayPronunciation;
    private OnTargetsClickListener listener;
    private boolean isOtherUser;
    private Context context;
    public TargetsAdapter(Context context, boolean displayPronunciation, boolean isOtherUser) {
        data = new ArrayList<>();
        this.context = context;
        this.displayPronunciation = displayPronunciation;
        this.isOtherUser = isOtherUser;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position) instanceof Integer ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER)
            return new HeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_targets, parent, false));
        if (isOtherUser)
            return new ItemOtherUserHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voca_other_user, parent, false));
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_targets, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            int grade = (int) data.get(position);
            int count;
            if (grade == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1) {
                count = count1;
            } else if (grade == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2) {
                count = count2;
            } else {
                count = count99;
            }
            ((HeaderHolder) holder).bind(grade, count, isOtherUser, listener);
        } else if (holder instanceof ItemOtherUserHolder) {
            ((ItemOtherUserHolder) holder).bind((VocaMemorize) data.get(position), displayPronunciation, listener);
        } else if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind((VocaMemorize) data.get(position), displayPronunciation, listener);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<VocaMemorize> grade1, List<VocaMemorize> grade2, List<VocaMemorize> grade99) {
        data.clear();
        count1 = count2 = count99 = 0;
        if (grade1 != null && !grade1.isEmpty()) {
            data.add(Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1);
            data.addAll(grade1);
            count1 = grade1.size();
        }
        if (grade2 != null && !grade2.isEmpty()) {
            data.add(Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2);
            data.addAll(grade2);
            count2 = grade2.size();
        }
        if (grade99 != null && !grade99.isEmpty()) {
            data.add(Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
            data.addAll(grade99);
            count99 = grade99.size();
        }
    }

    public void setListener(OnTargetsClickListener listener) {
        this.listener = listener;
    }

    public void notifyPlayedOrStopped(VocaMemorize voca) {
        notifyItemChanged(data.indexOf(voca));
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_grade_number)
        TextView tvGradeNumber;
        @BindView(R.id.tv_grade_text)
        TextView tvGradeText;
        @BindView(R.id.ic_play_all)
        View icPlayAll;

        @BindString(R.string.tpl_grade)
        String tplGrade;
        @BindString(R.string.tpl_exclude)
        String tplExclude;

        private int grade;
        private OnTargetsClickListener listener;

        public HeaderHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(int grade, int count, boolean isOtherUser, OnTargetsClickListener listener) {
            this.grade = grade;
            this.listener = listener;

            String text;
            if (grade == Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN) {
                text = Constant.AMKI_GRADE.DISPLAY_UNKNOWN;
                tvGradeNumber.setBackgroundResource(R.drawable.bg_circle_voca_know_unknown);
            } else if (grade == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1) {
                text = Constant.AMKI_GRADE.DISPLAY_A;
                tvGradeNumber.setBackgroundResource(R.drawable.bg_circle_voca_know_amki_grade_1);
            } else {
                text = Constant.AMKI_GRADE.DISPLAY_B;
                tvGradeNumber.setBackgroundResource(R.drawable.bg_circle_voca_know_amki_grade_2);
            }
            tvGradeNumber.setText(text);
            if (grade == Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN) {
                text = String.format(tplExclude, count);
            } else {
                text = String.format(tplGrade, BaseVoca.ordinal(grade), count);
            }
            tvGradeText.setText(text);
            icPlayAll.setVisibility(isOtherUser ? View.GONE : View.VISIBLE);
        }

        @OnClick({R.id.ic_play_all})
        void onClick() {
            if (listener != null) {
                listener.onPlayAllSoundClick(grade);
            }
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_study_count)
        TextView tvStudyCount;
        @BindView(R.id.tv_word_name)
        TextView tvWordName;
        @BindView(R.id.tv_word_meaning)
        TextView tvWordMeaning;
        @BindView(R.id.tv_grade_number)
        TextView tvGradeNumber;
        @BindView(R.id.ic_play)
        ImageView icPlay;

        @BindString(R.string.tpl_study_count)
        String tplStudyCount;

        private VocaMemorize voca;
        private OnTargetsClickListener listener;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(VocaMemorize voca, boolean displayPronunciation, OnTargetsClickListener listener) {
            this.voca = voca;
            this.listener = listener;

            String text = String.format(tplStudyCount, voca.getStudyCount());
            tvStudyCount.setText(text);
            text = BaseVoca.getVocaDisplay(voca);
            if (displayPronunciation && !TextUtils.isEmpty(voca.getPronounce())) {
                text += " [" + voca.getPronounce() + "]";
            }
            tvWordName.setText(text);
            text = voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context));
            tvWordMeaning.setText(text);
            BaseVocaKnow.updateIconVocaKnow(context, tvGradeNumber, voca.getVocaKnow());
            BaseVoca.updateIconSpeaker(icPlay, voca);
        }

        @OnClick({R.id.ic_play, R.id.v_item, R.id.tv_grade_number})
        void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.ic_play:
                    if (listener != null) {
                        listener.onPlaySoundClick(voca);
                    }
                    break;
                case R.id.v_item:
                    if (listener != null) {
                        listener.onInfoClick(voca);
                    }
                    break;
                case R.id.tv_grade_number:
                    if (listener != null) {
                        listener.onGradeClick(voca);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public class ItemOtherUserHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvWordName;
        @BindView(R.id.tv_meaning)
        TextView tvWordMeaning;
        @BindView(R.id.ic_play)
        ImageView icPlay;
        @BindView(R.id.ic_send)
        ImageView icSend;

        private VocaMemorize voca;
        private OnTargetsClickListener listener;

        public ItemOtherUserHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(VocaMemorize voca, boolean displayPronunciation, OnTargetsClickListener listener) {
            this.voca = voca;
            this.listener = listener;

            String text = BaseVoca.getVocaDisplay(voca);
            if (displayPronunciation && !TextUtils.isEmpty(voca.getPronounce())) {
                text += " [" + voca.getPronounce() + "]";
            }
            tvWordName.setText(text);
            text = voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context));
            tvWordMeaning.setText(text);
            BaseVoca.updateIconSpeaker(icPlay, voca);
            icSend.setVisibility(voca.isVIChecked() ? View.GONE : View.VISIBLE);
        }

        @OnClick({R.id.ic_play, R.id.v_item, R.id.ic_send})
        void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.ic_play:
                    if (listener != null) {
                        listener.onPlaySoundClick(voca);
                    }
                    break;
                case R.id.v_item:
                    if (listener != null) {
                        listener.onInfoClick(voca);
                    }
                    break;
                case R.id.ic_send:
                    if (listener != null) {
                        listener.onGradeClick(voca);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
