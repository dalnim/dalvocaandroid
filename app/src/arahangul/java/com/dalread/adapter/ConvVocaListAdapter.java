package com.dalread.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemContentSearchBinding;
import com.dalread.databinding.ItemVoca4buttonsBinding;
import com.dalread.databinding.ItemWordListPlayerHeaderBinding;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.WordListHeaderModel;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NonConstantResourceId")
public class ConvVocaListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_SEARCH = 0;
    private static final int TYPE_CONTENT = TYPE_SEARCH + 1;
    private static final int TYPE_HEADER = TYPE_CONTENT + 1;

    private final int countOfNonData = 1; // Need to set 2 later to display TYPE_SEARCH
    private final List<IVocaFullPlayTTSItem> dataList = new ArrayList<>();
    private OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;
    private OnDoubleClickListener onDoubleClickListener;
    private Context context;
    private boolean show4Buttons;
    private boolean isFirstTime;
    private IVocaFullPlayTTSItem isRecordingVoca;

    public ConvVocaListAdapter(Context context, OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow, OnDoubleClickListener onDoubleClickListener) {
        this.context = context;
        isFirstTime = true;
        this.onDoubleClickListenerOnBaseVocaKnow = onDoubleClickListenerOnBaseVocaKnow;
        this.onDoubleClickListener = onDoubleClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
//            return TYPE_SEARCH;
//        } else if (position == 1) {
            return TYPE_HEADER;
        }
        return TYPE_CONTENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_SEARCH:
                return new ContentSearchHolder(ItemContentSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            case TYPE_HEADER:
                return new HeaderViewHolder(ItemWordListPlayerHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

        }
        return new VocaHolder(ItemVoca4buttonsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_SEARCH:
                ((ContentSearchHolder) holder).bind(position);
                break;
            case TYPE_HEADER:
                ((HeaderViewHolder) holder).bind(position);
                break;
            case TYPE_CONTENT:
                ((VocaHolder) holder).bind((IVocaFullPlayTTSItem) dataList.get(position - countOfNonData), position);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return dataList.size() + countOfNonData;
    }

    public void setData(List<IVocaFullPlayTTSItem> iVocaFullItemList) {
        dataList.clear();
        dataList.addAll(iVocaFullItemList);
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
            if (dataList.size() < dataCountToShowSearchUI && !isSearching()) {
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
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
//        @BindView(R.id.tvType) TextView tvType;
//        @BindView(R.id.tvTitle) TextView tvTitle;
//        @BindView(R.id.icPlayAll) View icPlayAll;
        private WordListHeaderModel item;

        private ItemWordListPlayerHeaderBinding binding;
        HeaderViewHolder(ItemWordListPlayerHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
//            binding.tvType.setOnClickListener(this);
//            binding.tvTitle.setOnClickListener(this);
            binding.icPlayAll.setOnClickListener(this);
            binding.ivShowRecordedVoice.setOnClickListener(this);
        }


        public void bind(int position) {
//            this.item = item;
            binding.icPlayAll.setVisibility(View.GONE);
//            if (item.isHideType()) {
//                binding.tvType.setVisibility(View.GONE);
//            } else {
//                VocaKnow.updateIconVocaKnow(context, binding.tvType, item.getKnow());
//                binding.tvType.setVisibility(View.VISIBLE);
//            }
//            binding.tvTitle.setText(item.getName());
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.icPlayAll:
                case R.id.ivShowRecordedVoice:
                    if (onDoubleClickListener != null) {
                        onDoubleClickListener.onClick(view, null);
                    }
                    break;
            }
        }
    }

//    public int notifyPlaylistItemChanged(IVocaFullPlayTTSItem item) {
//        int pos = data.indexOf(item);
//        if (pos >= 0)
//            data.set(pos, item);
//        notifyItemChanged(pos);
//        return pos;
//    }

    public IVocaFullPlayTTSItem getItem(IVocaFullPlayTTSItem voca) {
        if (voca != null) {
            for (IVocaFullPlayTTSItem item : dataList) {
                if (Voca.isSameVoca(voca, item)) {
                    return item;
                }
            }
        }
        return null;
    }

    public void notifyItemChanged(IVocaFullPlayTTSItem voca) {
        if (voca == null)
            return;

        for (int i = 0; i < dataList.size(); i++) {
            IVocaFullPlayTTSItem item = dataList.get(i);
            if (Voca.isSameVoca(voca, item)) {
                dataList.set(i, voca);
                notifyItemChanged(i+countOfNonData);
                break;
            }
        }
    }

    public void setIsRecordingVoca(IVocaFullPlayTTSItem voca) {
        this.isRecordingVoca = voca;
        notifyDataSetChanged();
    }

    class VocaHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private IVocaFullPlayTTSItem voca;

        private ItemVoca4buttonsBinding binding;
        VocaHolder(ItemVoca4buttonsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.vItem.setOnClickListener(this);
            binding.ivSpeaker.setOnClickListener(this);
            binding.ivMic.setOnClickListener(this);
//            binding.ivKnow.setOnClickListener(this);
            binding.ivKnown.setOnClickListener(this);
            binding.ivGrade1.setOnClickListener(this);
            binding.ivGrade2.setOnClickListener(this);
            binding.ivUnknown.setOnClickListener(this);
            binding.ivBookmark.setOnClickListener(this);
            binding.ivEditView.setOnClickListener(this);
        }

        public void bind(IVocaFullPlayTTSItem voca, int position) {
            this.voca = voca;
            binding.tvIndex.setText(voca.getVIIndex().toString());
            //Use this for double click event
            binding.ivKnow.setOnClickListener(new DoubleClick(onDoubleClickListenerOnBaseVocaKnow, voca));
            VocaKnow.updateIconVocaBookmark(binding.ivBookmark, voca, true);
            VocaKnow.updateIconVocaKnow(binding.ivKnow, voca);
            Voca.updateIconSpeaker(binding.ivSpeaker, voca);
            Voca.updateIconMic(binding.ivMic, voca);
            displayVoca(voca);
            displayPronounce();
            displayMeaning(); //binding.tvMeaning.setText(voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)));
            displayMeaningDetailed(voca);
            displayMeaningEnglish(voca);
            display4Buttons();
            Voca.updateMicSpeakerVisibility(isRecordingVoca, voca, binding.ivSpeaker, binding.ivMic, false);
            hideIconsForAraHangul();
        }

        private void hideIconsForAraHangul() {
            if (AppFlavorUtil.isAraHangulApp()) {
                binding.ivEditView.setVisibility(View.GONE);
                binding.llKnowValue.setVisibility(View.GONE);
            }
        }

        private void displayVoca(IVocaFullPlayTTSItem voca) {
            String text = voca.getVIVoca();
            String vocaTts = voca.getVIVocaTTS();
            if (!Utils.isEmpty(vocaTts)) {
                if (!text.equals(vocaTts)) {
                    text = text + " : " + vocaTts;
                }
            }
            binding.tvVoca.setText(text);
        }

        private void displayMeaningDetailed(IVocaFullPlayTTSItem voca) {
            String meaningDetailed = Voca.getMeaningDetailedOrEnglishMeaningDetailed(context, voca);
            binding.tvMeaningDetailed.setVisibility(Utils.isEmpty(meaningDetailed) ? View.GONE : View.VISIBLE);
            binding.tvMeaningDetailed.setText(meaningDetailed);
        }

        private void display4Buttons() {
            if (show4Buttons) {
                binding.ll4Buttons.setVisibility(View.VISIBLE);
                binding.ivKnow.setVisibility(View.VISIBLE);
            } else {
                binding.ll4Buttons.setVisibility(View.GONE);
            }
        }

        private void displayPronounce() {
            Voca.displayShortOrLongPronounce(context, voca, binding.tvShortPronounce, binding.tvLongPronounce);
        }

        private void displayMeaning() {
            if (voca.getVIPronounce().equals(voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)))) {
                binding.tvMeaning.setVisibility(View.GONE);
            } else {
                binding.tvMeaning.setVisibility(View.VISIBLE);
                binding.tvMeaning.setText(Voca.getMeaningOrEnglishMeaning(context, voca));
            }
        }
        private void displayMeaningEnglish(IVocaFullPlayTTSItem voca) {
            if (Utils.needToDisplayMeaningEnglish(context) && !Utils.isEmpty(voca.getVIMeaningEng())) {
                binding.tvMeaningEnglish.setVisibility(View.VISIBLE);
                binding.tvMeaningEnglish.setText(Voca.wrapMeaningEnglish(voca));
            } else {
                binding.tvMeaningEnglish.setVisibility(View.GONE);
            }
            if (Utils.needToDisplayMeaningEnglish(context) && !Utils.isEmpty(voca.getVIMeaningEngDetailed())) {
                binding.tvMeaningEnglishDetailed.setVisibility(View.VISIBLE);
                binding.tvMeaningEnglishDetailed.setText(Voca.wrapMeaningEnglishDetailed(voca));
            } else {
                binding.tvMeaningEnglishDetailed.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ivBookmark:
                case R.id.iv_known: //Button
                case R.id.iv_grade_1:
                case R.id.iv_grade_2:
                case R.id.iv_unknown:
//                    if (UserUtil.isLoggedIn(context, true)) {
                        if (onDoubleClickListenerOnBaseVocaKnow != null) {
                            onDoubleClickListenerOnBaseVocaKnow.onClick(view, voca);
                        }
//                    }
                break;
                case R.id.ivMic:
//                    if (UserUtil.isLoggedIn(context, true)) {
                        if (onDoubleClickListener != null) {
//                        onPlaylistItemClickListener.onItemClick(voca);
                            onDoubleClickListener.onClick(view, voca);
                        }
//                    }
                    break;
                default:
                    if (onDoubleClickListener != null) {
//                        onPlaylistItemClickListener.onItemClick(voca);
                        onDoubleClickListener.onClick(view, voca);
                    }
                    break;

            }
        }
    }
}
