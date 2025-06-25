package com.dalread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnSymbolListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SymbolAdapter extends RecyclerView.Adapter {

    private String[] data;
    private OnSymbolListener listener;

    public SymbolAdapter(String[] data, OnSymbolListener listener) {
        setData(data);
        setListener(listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_symbol, parent, false));
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

    public void setData(String[] data) {
        this.data = data;
    }

    public void setListener(OnSymbolListener listener) {
        this.listener = listener;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_a_symbol)
        TextView tvSymbol;

        private String symbol;
        private OnSymbolListener listener;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(String symbol, OnSymbolListener listener) {
            this.symbol = symbol;
            this.listener = listener;

            tvSymbol.setText(symbol);
        }

        @OnClick(R.id.tv_a_symbol)
        void onClick() {
            if (listener != null) {
                listener.onClick(symbol);
            }
        }
    }
}
