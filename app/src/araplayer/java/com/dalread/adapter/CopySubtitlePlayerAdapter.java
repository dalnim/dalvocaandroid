package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.databinding.ItemPlayerOptionBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.SubtitleLanguageModel;
import com.dalread.util.SubtitleUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.List;

public class CopySubtitlePlayerAdapter extends BaseAdapter<CopySubtitlePlayerAdapter.ViewHolder> {

    private SwipeRecyclerView rvList;
    private List<SubtitleLanguageModel> list;
    private OnClickListener listener;
    private Context context;

    public CopySubtitlePlayerAdapter(Context context, SwipeRecyclerView rvList, OnClickListener listener) {
        super(context);
        this.context = context;
        this.rvList = rvList;
        this.listener = listener;
    }

    @Override
    public void notifyDataSetChanged(List<?> dataList) {
        this.list = (List<SubtitleLanguageModel>) dataList;
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(ItemPlayerOptionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        viewHolder.rvList = rvList;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SubtitleLanguageModel model = list.get(position);
        holder.setData(model, position);
        holder.binding.llItem.setOnClickListener(view -> {
            model.setSelected(!model.isSelected());
            holder.setSelectedItem(model);
            notifyItemChanged(position);
            if (listener != null) {
                listener.onClick(holder.binding.llItem, position);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        SwipeRecyclerView rvList;

        private ItemPlayerOptionBinding binding;
        ViewHolder(ItemPlayerOptionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.ivMove.setOnTouchListener(this);
        }

        public void setData(SubtitleLanguageModel model, int position) {
            binding.tvName.setText(SubtitleUtil.getLanguageName(model.getLanguage()));
            setSelectedItem(model);
        }

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

        private void setSelectedItem(SubtitleLanguageModel model) {
            binding.ivCheck.setVisibility(model.isSelected() ? View.VISIBLE : View.GONE);
        }
    }

}