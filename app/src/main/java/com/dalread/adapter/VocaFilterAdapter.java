package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemVocaFilterBinding;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.util.BaseVoca;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.StringUtils;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class VocaFilterAdapter extends RecyclerView.Adapter {
    private List<IVocaFullPlayTTSItem> items;
    private boolean displayPronunciation;
    private String keyword = "";
    private Context context;
    private OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;
    private OnDoubleClickListener onDoubleClickListener;

    public VocaFilterAdapter(Context context, boolean displayPronunciation, OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow, OnDoubleClickListener onDoubleClickListener) {
        this.context = context;
        this.items = new ArrayList<>();
        this.displayPronunciation = displayPronunciation;
        this.onDoubleClickListenerOnBaseVocaKnow = onDoubleClickListenerOnBaseVocaKnow;
        this.onDoubleClickListener = onDoubleClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(ItemVocaFilterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ItemHolder) holder).bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setData(List<IVocaFullPlayTTSItem> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public void addData(List<IVocaFullPlayTTSItem> items) {
        int start = this.items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(start, items.size());
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public int notifyPlaylistItemChanged(Object item) {
        int pos = items.indexOf(item);
        notifyItemChanged(pos);
        return pos;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private IVocaFullPlayTTSItem voca;

        private ItemVocaFilterBinding binding;
        ItemHolder(ItemVocaFilterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.llMain.setOnClickListener(this);
            binding.ivKnow.setOnClickListener(this);
            binding.ivBookmark.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ivBookmark:
                case R.id.ivKnow:
                    if (UserUtil.isLoggedIn(context, true)) {
                        if (onDoubleClickListenerOnBaseVocaKnow != null) {
                            onDoubleClickListenerOnBaseVocaKnow.onClick(view, voca);
                        }
                    }
                    break;
                case R.id.ivMic:
                    if (UserUtil.isLoggedIn(context, true)) {
                        if (onDoubleClickListener != null) {
                            onDoubleClickListener.onClick(view, voca);
                        }
                    }
                    break;
                default:
                    if (onDoubleClickListener != null) {
                        onDoubleClickListener.onClick(view, voca);
                    }
                    break;

            }
        }

        public void bind(IVocaFullPlayTTSItem voca) {
            this.voca = voca;

            binding.ivKnow.setOnClickListener(new DoubleClick(onDoubleClickListenerOnBaseVocaKnow, voca));
            BaseVocaKnow.updateIconVocaBookmark(binding.ivBookmark, voca, true);
            BaseVocaKnow.updateIconVocaKnowText(binding.ivKnow, voca);

            binding.ivCheck.setImageResource(voca.isVIChecked() ? R.drawable.ic_checkbox_1_checked : R.drawable.ic_checkbox_1_unchecked);
            String vocaDisplay = BaseVoca.getVocaDisplay(voca);
//            displayPronounce(voca); //일단 발음은 표시하지 말자.
            setTextViewValue(binding.tvVoca, vocaDisplay);
//            binding.tvVoca.setText(vocaDisplay);
            binding.tvMeaning.setText(BaseVoca.getMeaningOrEnglishMeaning(context, voca));
            displayMeaningEnglish(voca);
            binding.tvIndex.setText((voca.getVIIndex().toString() + " " + voca.getPersonAB()).trim());
        }
        private void setTextViewValue(TextView textView, String string) {
            if (Utils.hasValue(string)) {
                if (keyword.length() > 0) {
                    textView.setText(StringUtils.getSpanDefaultColorOnKeyword(string, keyword));
                } else {
                    textView.setText(string);
                }
            }
        }
        private void displayMeaningEnglish(IVocaFullPlayTTSItem voca) {
            if (Utils.needToDisplayMeaningEnglish(context) && !Utils.isEmpty(voca.getVIMeaningEng())) {
                binding.tvMeaningEnglish.setVisibility(View.VISIBLE);
                binding.tvMeaningEnglish.setText(BaseVoca.wrapMeaningEnglish(voca));
            } else {
                binding.tvMeaningEnglish.setVisibility(View.GONE);
            }
        }

        private void displayPronounce(IVocaFullPlayTTSItem voca) {
            BaseVoca.displayShortOrLongPronounce(context, voca, binding.tvShortPronounce, binding.tvLongPronounce);
        }
    }
}
