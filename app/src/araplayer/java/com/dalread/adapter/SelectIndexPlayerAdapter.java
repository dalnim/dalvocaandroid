package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectIndexPlayerAdapter extends RecyclerView.Adapter<SelectIndexPlayerAdapter.ViewHolder> {

    private Context context;
    private String[] items;
    private OnClickListener listener;
    private int lastCheckPosition = RecyclerView.NO_POSITION;

    public SelectIndexPlayerAdapter(Context context, String[] items, OnClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    public void setLastCheckPosition(int lastCheckPosition) {
        this.lastCheckPosition = lastCheckPosition;
    }

    @Override
    public SelectIndexPlayerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_player_select_index, parent, false));
    }

    @Override
    public void onBindViewHolder(SelectIndexPlayerAdapter.ViewHolder viewHolder, int position) {
        viewHolder.bindData(items[position], position);
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public void setData(String[] items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.ivCheck) ImageView ivCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(String name, int position) {
            tvTitle.setText(name);
            ivCheck.setVisibility(lastCheckPosition == position ? View.VISIBLE : View.INVISIBLE);
        }

        @OnClick(R.id.llItem)
        void onItemClick(View v) {
            final int newPosition = getAdapterPosition();
            int oldPosition = lastCheckPosition;
            if (oldPosition != newPosition) {
                lastCheckPosition = newPosition;
            } else {
                lastCheckPosition = RecyclerView.NO_POSITION;
            }
            notifyItemChanged(oldPosition);
            notifyItemChanged(lastCheckPosition);
            if (listener != null) {
                listener.onClick(v, lastCheckPosition);
            }
        }
    }
}
