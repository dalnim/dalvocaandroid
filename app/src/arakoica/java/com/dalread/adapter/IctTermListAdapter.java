package com.dalread.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.database.sqlite.model.DIC_ICT_TERM;
import com.dalread.databinding.ItemTermIctBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.util.StringUtils;
import com.dalread.util.Utils;
import com.zerobranch.layout.SwipeLayout;

import java.util.ArrayList;
import java.util.List;
@SuppressLint("NonConstantResourceId")
public class IctTermListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<DIC_ICT_TERM> itemList = new ArrayList<>();
    private String keyword = "";
    private boolean isFilterMode = false;
    private boolean isSwipeToDelete = false;
    private OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;
    private OnDoubleClickListener onDoubleClickListenerForCommon;
    private OnClickListener onClickListener;
    private Context context;

    public IctTermListAdapter(Context context, OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow, OnDoubleClickListener onDoubleClickListenerForCommon, OnClickListener onClickListener) {
        this.context = context;
        this.onDoubleClickListenerOnBaseVocaKnow = onDoubleClickListenerOnBaseVocaKnow;
        this.onDoubleClickListenerForCommon = onDoubleClickListenerForCommon;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TermHolder(ItemTermIctBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TermHolder) holder).bind((DIC_ICT_TERM) itemList.get(position), position);
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItemList(List<DIC_ICT_TERM> itemList) {
        this.itemList.clear();
        this.itemList.addAll(itemList);
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setFilterMode(boolean filterMode) {
        isFilterMode = filterMode;
    }

    public void setSwipeToDelete(boolean swipeToDelete) {
        isSwipeToDelete = swipeToDelete;
    }

    class TermHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private DIC_ICT_TERM item;

        private ItemTermIctBinding binding;
        TermHolder(ItemTermIctBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.vItem.setOnClickListener(this);
            binding.dragItem.setOnClickListener(this);
            binding.ivBookmark.setOnClickListener(this);
            binding.rightView.setOnClickListener(this);
            binding.swipeLayout.setOnActionsListener(new SwipeLayout.SwipeActionsListener() {
                @Override
                public void onOpen(int direction, boolean isContinuous) {
                    if (direction == SwipeLayout.RIGHT) {
                        // was executed swipe to the right
                        int i = 0;
                    } else if (direction == SwipeLayout.LEFT) {
                        // was executed swipe to the left
                        int i = 0;
                    }
                }

                @Override
                public void onClose() {
                    int i = 0;
                    // the main view has returned to the default state
                }
            });
        }

        public void bind(DIC_ICT_TERM item, int position) {
            this.item = item;
            binding.swipeLayout.setEnabledSwipe(isSwipeToDelete); //This doesn't work. 항상 스와이핑 됨.
            updateBookmarkIcon(item);
            setTextViewValue(binding.tvEngAbbr, item.getFirstItemToDisplay(), true);
            setTextViewValue(binding.tvEngFull, item.getSecondItemToDisplay(), true);
            setTextViewValue(binding.tvKoTitle, item.getTERM_KO_TITLE(), true);
            setTextViewValue(binding.tvKoShortOrFull, item.getKoreanShortOrFullToDisplay(), false);
        }

        private void setTextViewValue(TextView textView, String string, boolean isShowColorKeyword) {
            textView.setVisibility(View.GONE);
            if (Utils.hasValue(string)) {
                if (isShowColorKeyword && isFilterMode && keyword.length() > 0) {
                    textView.setText(StringUtils.getSpanDefaultColorOnKeyword(string, keyword));
                } else {
                    textView.setText(string);
                }
                textView.setVisibility(View.VISIBLE);
            }
        }

        private void updateBookmarkIcon(DIC_ICT_TERM item) {
            binding.ivBookmark.setImageResource(item.getBOOKMARK() == 1
                    ? R.drawable.ic_favorite_new_on
                    : R.drawable.ic_favorite_new_off);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ivBookmark:
                    if (onClickListener != null) {
                        onClickListener.onClick(view, item);
                        updateBookmarkIcon(item);
                    }
                    break;
                case R.id.right_view:
                    notifyItemRemoved(getAbsoluteAdapterPosition());
                    itemList.remove(item);
                    if (onClickListener != null) {
                        onClickListener.onClick(view, item);
                    }
                    break;
                default:
                    if (onClickListener != null) {
                        onClickListener.onClick(view, item);
                    }
                    break;
            }
        }
    }
}
