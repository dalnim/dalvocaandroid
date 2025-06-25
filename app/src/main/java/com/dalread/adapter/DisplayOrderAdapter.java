package com.dalread.adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.helper.itemtouchhelper.ItemTouchHelperAdapter;
import com.dalread.helper.itemtouchhelper.ItemTouchHelperViewHolder;
import com.dalread.listener.OnDragListener;
import com.dalread.model.DisplayObject;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class DisplayOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private List<DisplayObject> displayObjects;
    private OnDragListener onDragListener;

    public DisplayOrderAdapter(List<DisplayObject> displayObjects) {
        this.displayObjects = displayObjects;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DisplayHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_display_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DisplayHolder) {
            ((DisplayHolder) holder).bind(displayObjects.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return displayObjects.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (displayObjects.get(fromPosition).getOrder() >= 0) {
            displayObjects.get(fromPosition).setOrder(toPosition);
        }
        if (displayObjects.get(toPosition).getOrder() >= 0) {
            displayObjects.get(toPosition).setOrder(fromPosition);
        }
        Collections.swap(displayObjects, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void setOnDragListener(OnDragListener onDragListener) {
        this.onDragListener = onDragListener;
    }

    class DisplayHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        @BindView(R.id.tv_display)
        TextView tvDisplay;
        @BindView(R.id.ic_check)
        View icCheck;

        private DisplayObject displayObject;

        public DisplayHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(DisplayObject displayObject) {
            this.displayObject = displayObject;

            tvDisplay.setText(displayObject.getName());
            icCheck.setVisibility(displayObject.getOrder() >= 0 ? View.VISIBLE : View.INVISIBLE);
        }

        @OnTouch(R.id.ic_drag)
        boolean onTouch(View view, MotionEvent event) {
            if (onDragListener != null) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    onDragListener.onDragStarted(this);
                }
            }
            return false;
        }

        @Override
        public void onItemSelected() {
        }

        @Override
        public void onItemClear() {
            if (onDragListener != null) {
                onDragListener.onDragStopped();
            }
        }

        @OnClick(R.id.v_container)
        void onClick(View view) {
            if (displayObject.getOrder() < 0) {
                displayObject.setOrder(getAdapterPosition());
                icCheck.setVisibility(View.VISIBLE);
            } else {
                displayObject.setOrder(-1);
                icCheck.setVisibility(View.INVISIBLE);
            }
            onItemClear();
        }
    }
}
