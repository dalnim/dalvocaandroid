package com.dalread.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnPracticeClickListener;
import com.dalread.model.VocaPractice;
import com.dalread.util.Constant;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Voca;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelfPracticeSpeakingAdapter extends RecyclerView.Adapter {

    private ArrayList<VocaPractice> vocas;
    private boolean displayVoca;
    private boolean displayPronunciation;
    private OnPracticeClickListener listener;

    public SelfPracticeSpeakingAdapter(ArrayList<VocaPractice> vocas, boolean displayPronunciation) {
        this.vocas = vocas;
        this.displayPronunciation = displayPronunciation;
        displayVoca = true;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_self_practice_speaking, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind(vocas.get(position), displayVoca, displayPronunciation, listener);
        }
    }

    @Override
    public int getItemCount() {
        return vocas.size();
    }

    public void setListener(OnPracticeClickListener listener) {
        this.listener = listener;
    }

    public int notifyItemChanged(VocaPractice item) {
        int pos = vocas.indexOf(item);
        notifyItemChanged(pos);
        return pos;
    }

    public void hideAllWords() {
        displayVoca = false;
        notifyDataSetChanged();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.v_item)
        View vItem;
        @BindView(R.id.tv_index)
        TextView tvIndex;
        @BindView(R.id.tv_word_name)
        TextView tvWordName;
        @BindView(R.id.tv_word_pronounce)
        TextView tvWordPronounce;
        @BindView(R.id.tv_word_meaning)
        TextView tvWordMeaning;
        @BindView(R.id.ic_play)
        ImageView icPlay;

        private VocaPractice voca;
        private boolean displayVoca;
        private OnPracticeClickListener listener;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(VocaPractice voca, boolean displayVoca, boolean displayPronunciation, OnPracticeClickListener listener) {
            this.voca = voca;
            this.displayVoca = displayVoca;
            this.listener = listener;

            String text = voca.getIndex() + " " + voca.getPersonAB();
            tvIndex.setText(text);
            text = Voca.getVocaDisplay(voca);
            if (displayVoca) {
                tvWordName.setText(text);
            } else {
                tvWordName.setText(Voca.getStarForText(text));
            }
            if (displayPronunciation && !TextUtils.isEmpty(voca.getPronounce())) {
                text = "[" + voca.getPronounce() + "]";
            } else {
                text = "";
            }
            tvWordPronounce.setText(text);
            text = voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(icPlay.getContext()));
            tvWordMeaning.setText(text);
            vItem.setBackgroundResource(voca.isVIChecked() ? R.color.color_playing_background : R.color.colorBackground);
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
                    if (voca != null && !displayVoca) {
                        String text = Voca.getVocaDisplay(voca);
                        tvWordName.setText(text);
                        tvWordName.removeCallbacks(displayStarRunnable);
                        tvWordName.postDelayed(displayStarRunnable, Constant.SELF_PRACTICE_DISPLAY_VOCA_TIME);
                    }
                    break;
                default:
                    break;
            }
        }

        private Runnable displayStarRunnable = new Runnable() {

            @Override
            public void run() {
                String text = Voca.getVocaDisplay(voca);
                tvWordName.setText(Voca.getStarForText(text));
            }
        };
    }
}
