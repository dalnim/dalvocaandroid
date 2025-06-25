package com.dalread.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.dalread.R;
import com.dalread.databinding.ItemSearchHanjaSentenceBinding;
import com.dalread.databinding.ItemSearchHanjaWordBinding;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.HanjaItem;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NonConstantResourceId")
public class SearchHanjaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<HanjaItem> hanjaList = new ArrayList<>();
    private String keyword = "";
    private OnClickListener onClickListener;
    private OnDoubleClickListener onDoubleClickListener;
    private final Context context;

    public SearchHanjaAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return (hanjaList.get(position).getHI_VOCA().length() <= Constant.MAX_VOCA_COUNT_TO_USE_WORD_HOLDER)
                ? Constant.API_VALUE.VALUE_VOCA_TYPE_WORD
                : Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE;
    }

    private int getItemViewLayout(int viewType) {
        return viewType == Constant.API_VALUE.VALUE_VOCA_TYPE_WORD
                ? R.layout.item_search_hanja_word
                : R.layout.item_search_hanja_sentence;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Dalnim - There are 2 type of layout for one view holder. So receive as ViewBinding in the HanjaHolder.
        return new HanjaHolder(
                viewType == Constant.API_VALUE.VALUE_VOCA_TYPE_WORD
                        ? ItemSearchHanjaWordBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
                        : ItemSearchHanjaSentenceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HanjaHolder) {
            ((HanjaHolder) holder).bind(hanjaList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return hanjaList.size();
    }

    public void setHanjaList(List<HanjaItem> hanjas) {
        hanjaList.clear();
        if (hanjas != null) {
            hanjaList.addAll(hanjas);
        }
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setOnClickListener(OnClickListener listener) {
        onClickListener = listener;
    }

    public void setOnDoubleClickListener(OnDoubleClickListener onDoubleClickListener) {
        this.onDoubleClickListener = onDoubleClickListener;
    }

    public void notifyItemChanged(HanjaItem hanjaItem) {
        if (hanjaItem == null)
            return;

        for (int i = 0; i < hanjaList.size(); i++) {
            HanjaItem hanja = hanjaList.get(i);
            if ((hanja.getHI_VOCA_TYPE() == hanjaItem.getHI_VOCA_TYPE()) && (hanja.getHI_ID().equals(hanjaItem.getHI_ID()))) {
                hanjaList.set(i, hanjaItem);
                notifyItemChanged(i);
                break;
            }
        }
    }

    class HanjaHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private HanjaItem hanja;

        private ViewBinding binding;
        private ItemSearchHanjaWordBinding bindingWord;
        private ItemSearchHanjaSentenceBinding bindingSentence;
        HanjaHolder(ViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            if (binding instanceof ItemSearchHanjaWordBinding) {
                this.bindingWord = (ItemSearchHanjaWordBinding)binding;
            } else if (binding instanceof ItemSearchHanjaSentenceBinding) {
                this.bindingSentence = (ItemSearchHanjaSentenceBinding)binding;
            }
            setOnClickListeners();
        }
        private TextView getTvVoca() {
            if (binding instanceof ItemSearchHanjaWordBinding) {
                return bindingWord.tvVoca;
            }
            return bindingSentence.tvVoca;
        }
        private TextView getTvMeaningPronounce() {
            if (binding instanceof ItemSearchHanjaWordBinding) {
                return bindingWord.tvMeaningPronounce;
            }
            return bindingSentence.tvMeaningPronounce;
        }
        private TextView getTvMeaning() {
            if (binding instanceof ItemSearchHanjaWordBinding) {
                return bindingWord.tvMeaning;
            }
            return bindingSentence.tvMeaning;
        }
//        private TextView getTvKnow() {
//            if (binding instanceof ItemSearchHanjaWordBinding) {
//                return bindingWord.tvKnow;
//            }
//            return bindingSentence.tvKnow;
//        }
        private ImageView getIvKnow() {
            if (binding instanceof ItemSearchHanjaWordBinding) {
                return bindingWord.ivKnow;
            }
            return bindingSentence.ivKnow;
        }
        private ImageView getIvBookmark() {
            if (binding instanceof ItemSearchHanjaWordBinding) {
                return bindingWord.ivBookmark;
            }
            return bindingSentence.ivBookmark;
        }
        private LinearLayout getVItem() {
            if (binding instanceof ItemSearchHanjaWordBinding) {
                return bindingWord.vItem;
            }
            return bindingSentence.vItem;
        }
        private void setOnClickListeners() {
            getIvKnow().setOnClickListener(this);
            getIvBookmark().setOnClickListener(this);
            getVItem().setOnClickListener(this);
        }

        public void bind(HanjaItem hanja) {
            this.hanja = hanja;
            DLog.e("bind", hanja.getHI_VOCA() + " " + hanja.getHI_MEANING1());
            getIvKnow().setOnClickListener(new DoubleClick(onDoubleClickListener, hanja));
            updateBookmarkIcon(hanja);
//            getIvBookmark().setImageResource(hanja.getHI_BOOKMARK() != null && hanja.getHI_BOOKMARK() == 1
//                    ? R.drawable.ic_favorite_new_on
//                    : R.drawable.ic_favorite_new_off);

            VocaKnow.updateIconVocaKnow(context,
                    getIvKnow(),
                    hanja.getHI_VOCA_KNOW() == null
                            ? Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED
                            : hanja.getHI_VOCA_KNOW().intValue()
            );

            getTvVoca().setText(hanja.getHI_VOCA_WITH_VOCAORI());

            getTvMeaningPronounce().setText(Html.fromHtml(Voca.getColoredPronounceWithMeaning(hanja, context)));

            showMeaningOnSentence();
        }

        private void showMeaningOnSentence() {
            if (Utils.isEmpty(hanja.getHI_MEANING_AND_DETAILED())) {
                getTvMeaning().setVisibility(View.GONE);
            } else {
                getTvMeaning().setVisibility(View.VISIBLE);
                getTvMeaning().setText(hanja.getHI_MEANING_AND_DETAILED());
            }
        }

        private void updateBookmarkIcon(HanjaItem hanja) {
            getIvBookmark().setImageResource(hanja.getHI_BOOKMARK() != null && hanja.getHI_BOOKMARK() == 1
                    ? R.drawable.ic_favorite_new_on
                    : R.drawable.ic_favorite_new_off);
        }

        @Override
        public void onClick(View view) {
            if (onClickListener != null) {
                if (view.getId() == R.id.ivBookmark) {
                    if (UserUtil.isLoggedIn(context, true)) {
                        hanja.swapHI_BOOKMARK();
                        updateBookmarkIcon(hanja);
                    } else {
                        return;
                    }
                }
                onClickListener.onClick(view, hanja);
            }
        }
    }
}
