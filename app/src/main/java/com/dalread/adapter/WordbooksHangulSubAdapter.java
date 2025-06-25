package com.dalread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnWordbooksHangulSubListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WordbooksHangulSubAdapter extends RecyclerView.Adapter {

    private int[] data;
    private OnWordbooksHangulSubListener listener;

    public WordbooksHangulSubAdapter(int[] data, OnWordbooksHangulSubListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wordbooks_hangul_sub, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind(data[position], listener);
        }
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ic_hangul)
        ImageView icHangul;

        private OnWordbooksHangulSubListener listener;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(int aData, OnWordbooksHangulSubListener listener) {
            this.listener = listener;

            icHangul.setImageResource(aData);
        }

        @OnClick({R.id.ic_hangul})
        void onClick(View view) {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.ic_hangul:
                    if (listener != null) {
                        listener.onClick(getAdapterPosition());
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
