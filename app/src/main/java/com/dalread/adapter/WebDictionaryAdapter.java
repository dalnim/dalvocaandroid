package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.databinding.ItemWebDictionaryBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.WebDictionaryModel;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.List;

public class WebDictionaryAdapter extends BaseAdapter<WebDictionaryAdapter.ViewHolder> {

    private SwipeRecyclerView rvList;
    private List<WebDictionaryModel> mDataList;
    private OnClickListener listener;
    private Context context;

    public WebDictionaryAdapter(Context context, SwipeRecyclerView rvList, OnClickListener listener) {
        super(context);
        this.context = context;
        this.rvList = rvList;
        this.listener = listener;
    }

    @Override
    public void notifyDataSetChanged(List<?> dataList) {
        this.mDataList = (List<WebDictionaryModel>) dataList;
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemWebDictionaryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

//        ViewHolder viewHolder = new ViewHolder(getInflater().inflate(R.layout.item_web_dictionary, parent, false));
//        viewHolder.rvList = rvList;
//        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(mDataList.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, View.OnClickListener {

//        @BindView(R.id.tv_title) TextView tvTitle;
//        @BindView(R.id.tv_url) TextView tvUrl;
//        @BindView(R.id.iv_move) ImageView ivMove;
//        SwipeRecyclerView rvList;

        private ItemWebDictionaryBinding binding;
        ViewHolder(ItemWebDictionaryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
            binding.ivMove.setOnTouchListener(this);
        }

        private void setOnClickListeners() {
            binding.llItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, mDataList.get(getAdapterPosition()));
            }
        }

//        public ViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//            ivMove.setOnTouchListener(this);
//        }

        public void setData(WebDictionaryModel model) {
            binding.tvTitle.setText(model.getTitle());
            binding.tvUrl.setText(model.getUrl());
        }

//        @OnClick(R.id.llItem)
//        void onClick(View v) {
//            if (listener != null) {
//                listener.onClick(v, mDataList.get(getAdapterPosition()));
//            }
//        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    rvList.startDrag(this);
                    break;
            }
            return false;
        }
    }

}