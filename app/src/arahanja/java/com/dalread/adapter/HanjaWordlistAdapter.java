package com.dalread.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemContentSearchBinding;
import com.dalread.databinding.ItemSearchHanjaWord4buttonsBinding;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.DIC_HANJA_SENTENCE;
import com.dalread.model.HanjaItem;
import com.dalread.util.Constant;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import java.util.ArrayList;
import java.util.List;
@SuppressLint("NonConstantResourceId")
public class HanjaWordlistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_SEARCH = 0;
    private static final int TYPE_CONTENT = TYPE_SEARCH + 1;

    private final List<HanjaItem> data = new ArrayList<>();
    private OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;
    private OnDoubleClickListener onDoubleClickListenerForCommon;
    private OnClickListener onSearchClickListener;
    private Context context;
    private boolean show4Buttons;
    private boolean isFirstTime;

    public HanjaWordlistAdapter(Context context, OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow, OnDoubleClickListener onDoubleClickListenerForCommon, OnClickListener onSearchClickListener) {
        this.context = context;
        isFirstTime = true;
        this.onDoubleClickListenerOnBaseVocaKnow = onDoubleClickListenerOnBaseVocaKnow;
        this.onDoubleClickListenerForCommon = onDoubleClickListenerForCommon;
        this.onSearchClickListener = onSearchClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_SEARCH;
        }
        return TYPE_CONTENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_SEARCH:
                return new ContentSearchHolder(ItemContentSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

        }
        return new HanjaHolder(ItemSearchHanjaWord4buttonsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_SEARCH:
                ((ContentSearchHolder) holder).bind(position);
                break;
            case TYPE_CONTENT:
                ((HanjaHolder) holder).bind((HanjaItem) data.get(position - 1), position);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    public void setData(List<HanjaItem> hanjaItemList) {
        data.clear();
        data.addAll(hanjaItemList);
    }

    public void setShow4Buttons(boolean show4Buttons) {
        this.show4Buttons = show4Buttons;
    }

    class ContentSearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemContentSearchBinding binding;
        ContentSearchHolder(ItemContentSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
//            ButterKnife.bind(this, binding.getRoot());
            setOnClickListeners();
            hideInitSearchBtn(binding);


        }

        private void hideSearchUIForSmallList(com.dalread.databinding.ItemContentSearchBinding binding) {
            int dataCountToShowSearchUI = 10;
            binding.llSearch.setVisibility(View.VISIBLE);
            if (data.size() < dataCountToShowSearchUI && !isSearching()) {
                binding.llSearch.setVisibility(View.GONE);
            }
        }

        private void hideInitSearchBtn(com.dalread.databinding.ItemContentSearchBinding binding) {
            if (isFirstTime == true) {
                binding.btnResetSearch.setVisibility(View.GONE);
            }
            isFirstTime = false;
        }

        private boolean isSearching() {
            return binding.btnResetSearch.getVisibility() == View.VISIBLE;
        }

        private void setOnClickListeners() {
            binding.btnResetSearch.setOnClickListener(this);
            binding.etSearchKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    boolean result = false;
                    if (i == EditorInfo.IME_ACTION_SEARCH) {
                        String strKeyword = binding.etSearchKeyword.getText().toString().trim();
                        binding.btnResetSearch.setVisibility(Utils.isEmpty(strKeyword) ? View.GONE : View.VISIBLE);
                        onSearchClickListener.onClick(textView, strKeyword);
                        Utils.hideSoftKeyboard(context, binding.etSearchKeyword);
                        result = true;
                    }
                    return result;
                }
            });
        }


        void bind(int position) {
            hideSearchUIForSmallList(binding);
        }

        @Override
        public void onClick(View view) {
            if ((view.getId() == R.id.btnResetSearch)) {
                binding.btnResetSearch.setVisibility(View.GONE);
                binding.etSearchKeyword.setText("");
            }
            String strKeyword = binding.etSearchKeyword.getText().toString().trim();
            onSearchClickListener.onClick(view, strKeyword);
        }
    }

    class HanjaHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private HanjaItem hanja;

        private ItemSearchHanjaWord4buttonsBinding binding;
        HanjaHolder(ItemSearchHanjaWord4buttonsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.vItem.setOnClickListener(this);
            binding.ivKnow.setOnClickListener(this);
            binding.ivKnown.setOnClickListener(this);
            binding.ivGrade1.setOnClickListener(this);
            binding.ivGrade2.setOnClickListener(this);
            binding.ivUnknown.setOnClickListener(this);
            binding.ivBookmark.setOnClickListener(this);
        }

        public void bind(HanjaItem hanja, int position) {
            this.hanja = hanja;
            binding.ivKnow.setOnClickListener(new DoubleClick(onDoubleClickListenerOnBaseVocaKnow, hanja));
            updateBookmarkIcon(hanja);

            VocaKnow.updateIconVocaKnow(context,
                    binding.ivKnow,
                    hanja.getHI_VOCA_KNOW() == null
                            ? Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED
                            : hanja.getHI_VOCA_KNOW().intValue()
            );
            binding.tvIndex.setText(hanja.getHI_INDEX().toString());
            binding.tvVoca.setText(hanja.getHI_VOCA_WITH_VOCAORI());
            if (hanja.getHI_VOCA().length() > Constant.MAX_VOCA_COUNT_TO_USE_WORD_HOLDER - 1) {
                binding.llVocaPronounce.setOrientation(LinearLayout.VERTICAL);
            } else {
                binding.llVocaPronounce.setOrientation(LinearLayout.HORIZONTAL);
            }

            binding.tvMeaning.setVisibility(View.GONE);
            if (hanja instanceof DIC_HANJA_SENTENCE) {
                DIC_HANJA_SENTENCE dicHanjaSentence = (DIC_HANJA_SENTENCE) hanja;
                String meaning = dicHanjaSentence.getMEANING_KO();
                if (Utils.hasValue(meaning)) {
                    binding.tvMeaning.setText(meaning);
                    binding.tvMeaning.setVisibility(View.VISIBLE);
                }
            }
            if (show4Buttons) {
                binding.ll4Buttons.setVisibility(View.VISIBLE);
                binding.ivKnow.setVisibility(View.VISIBLE);
            } else {
                binding.ll4Buttons.setVisibility(View.GONE);
            }

            binding.tvMeaningPronounce.setText(Html.fromHtml(Voca.getColoredPronounceWithMeaning(hanja, context)));
        }

        private void updateBookmarkIcon(HanjaItem hanja) {
            binding.ivBookmark.setImageResource(hanja.getHI_BOOKMARK() != null && hanja.getHI_BOOKMARK() == 1
                    ? R.drawable.ic_favorite_new_on
                    : R.drawable.ic_favorite_new_off);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ivBookmark:
//                    if (UserUtil.isLoggedIn(context, true)) {
//                        hanja.swapHI_BOOKMARK();
//                        updateBookmarkIcon(hanja);
//                    } else {
//                        return;
//                    }
//                    if (onDoubleClickListenerOnBaseVocaKnow != null) {
//                        onDoubleClickListenerOnBaseVocaKnow.onClick(view, hanja);
//                    }
//                    break;
                case R.id.iv_known:
                case R.id.iv_grade_1:
                case R.id.iv_grade_2:
                case R.id.iv_unknown:
                    if (!UserUtil.isLoggedIn(context, true)) {
                        return;
                    }
                    if (onDoubleClickListenerOnBaseVocaKnow != null) {
                        onDoubleClickListenerOnBaseVocaKnow.onClick(view, hanja);
                    }
                break;
                default:
                    if (onDoubleClickListenerForCommon != null) {
                        onDoubleClickListenerForCommon.onClick(view, hanja);
                    }
                    break;

            }

        }
    }
}
