package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnClickListener;
import com.dalread.model.ServerModel;
import com.dalread.util.Constant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ServerPlayerAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<ServerModel> data;
    private OnClickListener listener;
    private String[] names;

    public ServerPlayerAdapter(Context context, List<ServerModel> data, OnClickListener listener) {
        this.context = context;
        setData(data);
        this.listener = listener;
        names = context.getResources().getStringArray(R.array.player_server);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_server, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind(data.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void setData(List<ServerModel> data) {
        this.data = data;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.tvDescription) TextView tvDescription;
        @BindView(R.id.ivInfo) ImageView ivInfo;
        private ServerModel item;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(ServerModel item) {
            this.item = item;
            tvName.setText(item.getTitle());
            if (item.getType() <= Constant.PLAYER.SERVER.TYPE.NONE) {
                tvDescription.setVisibility(View.GONE);
                ivInfo.setVisibility(View.GONE);
            } else {
                tvDescription.setText(names[item.getType()]);
                tvDescription.setVisibility(View.VISIBLE);
                ivInfo.setVisibility(View.VISIBLE);
            }
        }

        @OnClick({R.id.llItem, R.id.ivInfo})
        void onClick(View v) {
            if (listener != null) {
                listener.onClick(v, item);
            }
        }
    }
}
