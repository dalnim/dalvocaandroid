package com.dalread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnWordClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StudyWritingEnglishAdapter extends RecyclerView.Adapter {

    private List<String> data;
    private OnWordClickListener listener;

    public StudyWritingEnglishAdapter(List<String> data, OnWordClickListener listener) {
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
            ((ItemHolder) holder).bind(data.get(position), listener);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public void setListener(OnWordClickListener listener) {
        this.listener = listener;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvVoca)
        TextView tvVoca;

        private String aData;
        private OnWordClickListener listener;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(String aData, OnWordClickListener listener) {
            this.aData = aData;
            this.listener = listener;

            tvVoca.setText(aData);
        }

        @OnClick(R.id.tvVoca)
        void onClick(View view) {
            if (listener != null) {
                listener.onClick(aData, getAdapterPosition());
            }
        }
    }
}
