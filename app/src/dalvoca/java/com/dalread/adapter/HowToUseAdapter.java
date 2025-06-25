package com.dalread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnHowToUseClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HowToUseAdapter extends RecyclerView.Adapter {

    private final String[] data;
    private OnHowToUseClickListener listener;

    public HowToUseAdapter(String[] data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_how_to_use, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind(data[position], listener);
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.length;
    }

    public void setListener(OnHowToUseClickListener listener) {
        this.listener = listener;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvName;

        private OnHowToUseClickListener listener;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(String name, OnHowToUseClickListener listener) {
            this.listener = listener;

            tvName.setText(name);
        }

        @OnClick({R.id.v_item})
        void onClick(View view) {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.v_item:
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
