package com.dalread.adapter;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.model.SubtitleLanguageModel;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OptionPlayerAdapter extends BaseAdapter<OptionPlayerAdapter.ViewHolder> {

    private SwipeRecyclerView rvList;
    private List<SubtitleLanguageModel> mDataList;
    private Context context;

    public OptionPlayerAdapter(Context context, SwipeRecyclerView rvList) {
        super(context);
        this.context = context;
        this.rvList = rvList;
    }

    @Override
    public void notifyDataSetChanged(List<?> dataList) {
        this.mDataList = (List<SubtitleLanguageModel>) dataList;
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(getInflater().inflate(R.layout.item_player_option, parent, false));
        viewHolder.rvList = rvList;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SubtitleLanguageModel subtitleLanguageModel = mDataList.get(position);
        holder.setData(subtitleLanguageModel, position);
        holder.llItem.setOnClickListener(view -> {
            notifyItemChanged(position);
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        @BindView(R.id.llItem) LinearLayout llItem;
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.ivMove) ImageView ivMove;
        SwipeRecyclerView rvList;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ivMove.setOnTouchListener(this);
        }

        public void setData(SubtitleLanguageModel model, int position) {
            tvName.setText(EnumLanguage.getNameByIdApi(model.getLanguage()));
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
    }

}