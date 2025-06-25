package com.dalread.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnDoubleClickPlayerListener;
import com.dalread.model.SubtitleMeaningModel;
import com.dalread.util.Constant;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SubtitleMeaningAdapter extends BaseAdapter<SubtitleMeaningAdapter.ViewHolder> {

    private List<SubtitleMeaningModel> items = new ArrayList<>();
    private OnDoubleClickPlayerListener listener;
    private Context context;
    private boolean displayPronunciation;
    boolean isDoubleClick = false;
    final Handler mHandler = new Handler(Looper.getMainLooper());
    long numberOfTaps = 0;

    public SubtitleMeaningAdapter(Context context, boolean displayPronunciation, OnDoubleClickPlayerListener listener) {
        super(context);
        this.context = context;
        this.displayPronunciation = displayPronunciation;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_player_subtitle_meaning, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), position);
    }

    @Override
    public void notifyDataSetChanged(List<?> dataList) {

    }

    public void setData(ArrayList<SubtitleMeaningModel> items) {
        if (items == null) return;
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvPronounce) TextView tvPronounce;
        @BindView(R.id.tvMeaning) TextView tvMeaning;
        @BindView(R.id.icPlay) ImageView icPlay;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.llItem, R.id.icPlay})
        void onClick(View v) {
            if (listener == null) return;
            switch (v.getId()) {
                case R.id.llItem:
                    if (numberOfTaps == 0) {
                        numberOfTaps++;
                        mHandler.postDelayed(() -> {
                            isDoubleClick = numberOfTaps > 1;
                            numberOfTaps = 0;
                            if (isDoubleClick) {
                                listener.onDoubleClick(v, getAdapterPosition());
                            } else {
                                listener.onClick(v, getAdapterPosition());
                            }
                        }, ViewConfiguration.getDoubleTapTimeout());
                    } else {
                        numberOfTaps++;
                    }
                    break;
                case R.id.icPlay:
                    listener.onClick(v, items.get(getAdapterPosition()));
                    break;
            }

        }

        public void bind(SubtitleMeaningModel model, int position) {
            model.getDicModel().setPosition(position);
            String strWord = Voca.getVocaDisplay(model.getDicModel());
            if (model.getDicModel().getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                if (model.getDicModel().getVocaKnowPronounce() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                    tvPronounce.setTextColor(ContextCompat.getColor(context, R.color.color_ruby_text_normal));
                } else {
                    tvPronounce.setTextColor(ContextCompat.getColor(context, R.color.color_ruby_text_unknown_pronounce));
                }
            } else {
                tvPronounce.setTextColor(ContextCompat.getColor(context, R.color.color_ruby_text_unknown_meaning));
            }
            strWord = combinePronounce(model, strWord);
            tvPronounce.setText(strWord);
            tvMeaning.setText(model.getDicModel().getMeaning());
            Voca.updateIconSpeaker(icPlay, model.getDicModel());
        }

        private String combinePronounce(SubtitleMeaningModel model, String strWord) {
            if (displayPronunciation && !TextUtils.isEmpty(model.getDicModel().getPronounce())
                    && (model.getDicModel().getVocaKnow() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN || model.getDicModel().getVocaKnowPronounce() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)) {
                strWord += " [" + model.getDicModel().getPronounce() + "]";
            }
            return strWord;
        }

    }

}