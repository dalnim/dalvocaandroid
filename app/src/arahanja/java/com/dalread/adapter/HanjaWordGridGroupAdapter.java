package com.dalread.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.databinding.ItemDemoGroupBinding;
import com.dalread.databinding.ItemVocaHanjaWordGridBinding;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.HanjaItem;
import com.dalread.model.ParentGroup;
import com.dalread.util.Constant;
import com.dalread.util.ViewUtil;
import com.dalread.util.VocaKnow;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

@SuppressLint("NonConstantResourceId")
public class HanjaWordGridGroupAdapter extends AbstractExpandableItemAdapter<HanjaWordGridGroupAdapter.HanjaWordGridGroupHolder, HanjaWordGridGroupAdapter.HanjaWordGridChildHolder> {

    private final List<ParentGroup> data = new ArrayList<>();
    private OnDoubleClickListener listener;
    private OnClickListener sentenceListener;
    private Context context;
    private HashSet<Integer> hashSetGroupPosition = new LinkedHashSet<>();
    private RecyclerViewExpandableItemManager expMgr;

    public HanjaWordGridGroupAdapter(Context context, OnDoubleClickListener listener) {
        setHasStableIds(true); // this is required for expandable feature.
        this.context = context;
        this.listener = listener;
    }

    public void setSentenceActivityListener(OnClickListener listener) {
        this.sentenceListener = listener;
    }

    public void setExpMgr(RecyclerViewExpandableItemManager expMgr) {
        this.expMgr = expMgr;
    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return data.get(groupPosition).getChildList().size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return data.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return data.get(groupPosition).getChildList().get(childPosition).getHI_ID();
    }

    @NonNull
    @Override
    public HanjaWordGridGroupHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        return new HanjaWordGridGroupHolder(ItemDemoGroupBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @NonNull
    @Override
    public HanjaWordGridChildHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        return new HanjaWordGridChildHolder(ItemVocaHanjaWordGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindGroupViewHolder(@NonNull HanjaWordGridGroupHolder holder, int groupPosition, int viewType) {
        holder.bind(data.get(groupPosition));
    }

    @Override
    public void onBindChildViewHolder(@NonNull HanjaWordGridChildHolder holder, int groupPosition, int childPosition, int viewType) {
        holder.bind(data.get(groupPosition).getChildList().get(childPosition));
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(@NonNull HanjaWordGridGroupHolder holder, int groupPosition, int x, int y, boolean expand) {
        return !ViewUtil.hitTest(holder.binding.ivWordList, x, y);
    }

    public void setData(List<ParentGroup> parentGroupList) {
        data.clear();
        if (parentGroupList != null) {
            data.addAll(parentGroupList);
            setParentGroupPosition(parentGroupList);
        }
    }

    private void setParentGroupPosition(List<ParentGroup> parentGroupList) {
        int allItems = 0;
        hashSetGroupPosition.add(allItems);
        for (ParentGroup parentGroup : parentGroupList) {
            allItems += parentGroup.getChildList().size() + 1;
            hashSetGroupPosition.add(allItems);
        }

    }

    public boolean isPositionChild(int position) {
        if (hashSetGroupPosition.contains(position)) {
            return false;
        }
        return true;
    }


    class HanjaWordGridGroupHolder extends AbstractExpandableItemViewHolder implements View.OnClickListener {
//        @BindString(R.string.tpl_wb_known_word_count)
//        String tplKnownWordCount;

        ParentGroup parentGroup;

        private ItemDemoGroupBinding binding;
        HanjaWordGridGroupHolder(ItemDemoGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.ivWordList.setOnClickListener(this);
        }


        public void bind(ParentGroup parentGroup) {
            this.parentGroup = parentGroup;
            binding.tvGroup.setText(parentGroup.getText());
            Long countOfKnownWords = parentGroup.getChildList().stream()
                    .filter(e -> e.getHI_VOCA_KNOW() >= Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)
                    .count();
            binding.tvKnowWordCount.setText(String.format(context.getString(R.string.tpl_wb_known_word_count), countOfKnownWords, parentGroup.getChildList().size()));
        }

        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ivWordList:
                    if (sentenceListener != null) {
                        sentenceListener.onClick(view, parentGroup.getChildList());
                    }
                    break;
            }
        }
    }

    class HanjaWordGridChildHolder extends AbstractExpandableItemViewHolder {
        private HanjaItem hanjaItem;

        private ItemVocaHanjaWordGridBinding binding;
        HanjaWordGridChildHolder(ItemVocaHanjaWordGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(HanjaItem hanjaItem) {
            this.hanjaItem = hanjaItem;
            binding.vItem.setOnClickListener(new DoubleClick(listener, hanjaItem));
            binding.tvVoca.setText(hanjaItem.getHI_VOCA());
            binding.tvVoca.setTextColor(VocaKnow.getVocaColor(context, hanjaItem.getHI_VOCA_KNOW().intValue()));
            binding.tvMeaningPronounceHanja.setText(hanjaItem.getHI_MEANING1() + " " + hanjaItem.getHI_PRONOUNCE1_FIRST());
        }
    }
}
