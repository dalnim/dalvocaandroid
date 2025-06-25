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
import com.dalread.component.FuriganaView;
import com.dalread.listener.OnClickListener;
import com.dalread.model.BookmarkPlayerModel;
import com.dalread.util.TimeUtil;
import com.dalread.util.UtilImage;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookmarkPlayerAdapter extends BaseAdapter<BookmarkPlayerAdapter.ViewHolder> {

    private SwipeRecyclerView rvList;
    private List<BookmarkPlayerModel> mDataList;
    private OnClickListener listener;
    private boolean isEdit = false;
    private Context context;

    public BookmarkPlayerAdapter(Context context, SwipeRecyclerView rvList, OnClickListener listener) {
        super(context);
        this.context = context;
        this.rvList = rvList;
        this.listener = listener;
    }

    @Override
    public void notifyDataSetChanged(List<?> dataList) {
        this.mDataList = (List<BookmarkPlayerModel>) dataList;
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(getInflater().inflate(R.layout.item_player_bookmark, parent, false));
        viewHolder.rvList = rvList;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final BookmarkPlayerModel model = mDataList.get(position);
        holder.setData(model, position);
        holder.llItem.setOnClickListener(view -> {
            model.setSelected(!model.isSelected());
            holder.setSelectedItem(model);
            notifyItemChanged(position);
            if (listener != null) {
                listener.onClick(holder.llItem, position);
            }
        });
    }

    public void changeMode(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        @BindView(R.id.llItem) LinearLayout llItem;
        @BindView(R.id.ivRemove) ImageView rvRemove;
        @BindView(R.id.izbVideoThumbnail) ImageView izbVideoThumbnail;
        @BindView(R.id.tvContent) FuriganaView tvContent;
        @BindView(R.id.ivMove) ImageView ivMove;
        @BindView(R.id.tvTime) TextView tvTime;
        SwipeRecyclerView rvList;
        int position = -1;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ivMove.setOnTouchListener(this);
            tvContent.setOnTouchListener(this);
            tvContent.setTutor(true);
            rvRemove.setVisibility(isEdit ? View.VISIBLE : View.GONE);
        }

        public void setData(BookmarkPlayerModel model, int position) {
            this.position = position;
            setSelectedItem(model);
            tvContent.resetText();
            tvContent.setJText(model.getContent());
            tvTime.setText(TimeUtil.getDisplay(model.getStart()) + " ~ " + TimeUtil.getDisplay(model.getEnd()));
            UtilImage.getThumbnailVideoFile(context, izbVideoThumbnail, model.getPath(), model.getEnd(), model.getEnd(), false, null);
        }

        @OnClick(R.id.ivRemove)
        void onClick(View v) {
            if (listener != null) {
                listener.onClick(v, position);
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    rvList.startDrag(this);
                    break;
                case MotionEvent.ACTION_UP:
                    if (v.getId() == R.id.tvContent) {
                        llItem.performClick();
                    }
                    break;
            }
            return false;
        }

        private void setSelectedItem(BookmarkPlayerModel model) {
            llItem.setBackgroundResource(model.isSelected() ? R.color.color_player_repeat_background : R.color.transparent);
        }
    }

}