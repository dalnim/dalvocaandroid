package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnPracticeClickListener;
import com.dalread.model.VocaPractice;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VocaPracticeAdapter extends RecyclerView.Adapter {

    private ArrayList<VocaPractice> data;
    private OnPracticeClickListener listener;
    private Context context;
    public VocaPracticeAdapter(Context context, ArrayList<VocaPractice> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voca_book, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind(data.get(position), listener);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setListener(OnPracticeClickListener listener) {
        this.listener = listener;
    }

    public void notifyItemChanged(VocaPractice voca) {
        notifyItemChanged(data.indexOf(voca));
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_meaning)
        TextView tvMeaning;
        @BindView(R.id.tv_grade_number)
        TextView tvGradeNumber;
        @BindView(R.id.ic_play)
        ImageView icPlay;
        @BindView(R.id.ic_right)
        ImageView icRight;

        private VocaPractice voca;
        private OnPracticeClickListener listener;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(VocaPractice voca, OnPracticeClickListener listener) {
            this.voca = voca;
            this.listener = listener;

            String text = Voca.getVocaDisplay(voca);
            tvName.setText(text);
            text = voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context));
            tvMeaning.setText(text);
            VocaKnow.updateIconVocaKnow(context, tvGradeNumber, voca.getVocaKnow());
            Voca.updateIconSpeaker(icPlay, voca);
            icRight.setImageResource(R.drawable.ic_keyboard_arrow_right_36dp);
            icRight.setVisibility(View.VISIBLE);
        }

        @OnClick({R.id.ic_play, R.id.v_foreground, R.id.tv_grade_number})
        void onClick(View view) {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.ic_play:
                    if (listener != null) {
                        listener.onPlayClick(voca);
                    }
                    break;
                case R.id.v_foreground:
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
}
