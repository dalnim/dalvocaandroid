package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnStudyWritingClickListener;
import com.dalread.model.VocaKnowMeaning;
import com.dalread.util.VocaKnow;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StudyWritingAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<VocaKnowMeaning> data;
    private OnStudyWritingClickListener listener;

    public StudyWritingAdapter(Context context, List<VocaKnowMeaning> data, OnStudyWritingClickListener listener) {
        this.context = context;
        setData(data);
        setListener(listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_writing_english, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind(data.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<VocaKnowMeaning> data) {
        this.data = data;
    }

    public void setListener(OnStudyWritingClickListener listener) {
        this.listener = listener;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvVoca) TextView tvVoca;
        private VocaKnowMeaning item;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(VocaKnowMeaning item) {
            this.item = item;
            tvVoca.setText(item.getVoca());
            tvVoca.setTextColor(VocaKnow.getVocaColor(context, item.getVocaKnow()));
        }

        @OnClick(R.id.tvVoca)
        void onClick(View v) {
            if (listener != null) {
                listener.onClick(item, getAdapterPosition());
            }
        }
    }
}
