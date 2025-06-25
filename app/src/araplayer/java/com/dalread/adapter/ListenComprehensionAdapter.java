package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemPlayerListenComprehensionBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.ListenComprehensionModel;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.List;

public class ListenComprehensionAdapter extends BaseAdapter<ListenComprehensionAdapter.ViewHolder> {

    private SwipeRecyclerView rvList;
    private List<ListenComprehensionModel> list;
    private OnClickListener listener;
    private Context context;

    public ListenComprehensionAdapter(Context context, SwipeRecyclerView rvList, OnClickListener listener) {
        super(context);
        this.context = context;
        this.rvList = rvList;
        this.listener = listener;
    }

    @Override
    public void notifyDataSetChanged(List<?> dataList) {
        this.list = (List<ListenComprehensionModel>) dataList;
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemPlayerListenComprehensionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
//        ViewHolder viewHolder = new ViewHolder(getInflater().inflate(R.layout.item_player_listen_comprehension, parent, false));
//        viewHolder.rvList = rvList;
//        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(list.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, View.OnClickListener {

//        @BindView(R.id.tvName) TextView tvName;
//        @BindView(R.id.tvDescription) TextView tvDescription;
//        @BindView(R.id.ivMove) ImageView ivMove;
//        private SwipeRecyclerView rvList;
        private ListenComprehensionModel item;


        private ItemPlayerListenComprehensionBinding binding;
        ViewHolder(ItemPlayerListenComprehensionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
            binding.ivMove.setOnTouchListener(this);
        }
        private void setOnClickListeners() {
            binding.ivRemove.setOnClickListener(this);
//            binding.llItem.setOnClickListener(this);
        }
        public void setData(ListenComprehensionModel model) {
            this.item = model;
            updateVisibilityRemoveButton();
            binding.tvName.setText(context.getString(model.getTitle()));
            binding.tvDescription.setText(context.getString(model.getCount() > 1 ?
                            R.string.listen_comprehension_format_times : R.string.listen_comprehension_format_time,
                    model.getCount()));
        }

        private void updateVisibilityRemoveButton() {
            binding.ivRemove.setVisibility(item.getIndex() == 0 ? View.INVISIBLE : View.VISIBLE);
        }


        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, item);
            }
        }

//        public ViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//            ivMove.setOnTouchListener(this);
//        }



//        @OnClick(R.id.llItem)
//        void onClick(View v) {
//            if (listener != null) {
//                listener.onClick(v, item);
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