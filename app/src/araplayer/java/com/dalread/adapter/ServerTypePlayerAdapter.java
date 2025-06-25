package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnClickListener;
import com.dalread.model.ServerTypeModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ServerTypePlayerAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<ServerTypeModel> data;
    private OnClickListener listener;

    public ServerTypePlayerAdapter(Context context, List<ServerTypeModel> data, OnClickListener listener) {
        this.context = context;
        setData(data);
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_server_type, parent, false));
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

    public void setData(List<ServerTypeModel> data) {
        this.data = data;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvName) TextView tvName;
        private ServerTypeModel item;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(ServerTypeModel item) {
            this.item = item;
            tvName.setText(item.getName());
        }

        @OnClick(R.id.llItem)
        void onClick(View v) {
            if (listener != null) {
                listener.onClick(v, item);
            }
        }
    }
}
