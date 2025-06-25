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
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.ItemContentSearchBinding;
import com.dalread.databinding.ItemVoca4buttonsBinding;
import com.dalread.databinding.ItemWordListPlayerHeaderBinding;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.WordListHeaderModel;
import com.dalread.model.WordListType;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.BaseVoca;
import com.dalread.util.CopyTextUtil;
import com.dalread.util.OpenViewUtil;
import com.dalread.util.StringUtils;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;
import com.zerobranch.layout.SwipeLayout;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NonConstantResourceId")
public class ConvVocaListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_SEARCH = 0;
    private static final int TYPE_CONTENT = TYPE_SEARCH + 1;
    private static final int TYPE_HEADER = TYPE_CONTENT + 1;

    private int countOfNonData = 0; // Need to set 2 later to display TYPE_SEARCH
    private final List<IVocaFullPlayTTSItem> itemList = new ArrayList<>();
    private OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;
    private OnDoubleClickListener onDoubleClickListener;
    private Context context;
    private String keyword = "";
    private int bookId;
    private boolean show4Buttons;
    private boolean isFirstTime;
    private boolean isShowPlayAll;
    private boolean isSwipeToDelete = false;
    private WordListType wordListType;
    private IVocaFullPlayTTSItem isRecordingVoca;
    private SharedPreferencesDB sharedPreferences;

    public ConvVocaListAdapter(Context context, OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow, OnDoubleClickListener onDoubleClickListener) {
        this.context = context;
        isFirstTime = true;
        isShowPlayAll = true;
        this.onDoubleClickListenerOnBaseVocaKnow = onDoubleClickListenerOnBaseVocaKnow;
        this.onDoubleClickListener = onDoubleClickListener;
        sharedPreferences = SharedPreferencesDB.getInstance(context);
    }

    @Override
    public int getItemViewType(int position) {
//        if (position == 0) {
//            return TYPE_HEADER;
//        }
        Object object = itemList.get(position);
        if (object instanceof WordListHeaderModel) {
            return TYPE_HEADER;
        }
        return TYPE_CONTENT;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Don't use TYPE_SEARCH and TYPE_HEADER
        switch (viewType) {
            case TYPE_SEARCH:
                return new ContentSearchHolder(ItemContentSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            case TYPE_HEADER:
                return new HeaderViewHolder(ItemWordListPlayerHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
//        return new VocaHolder(parent.getContext(), ItemVoca4buttonsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), this, onDoubleClickListenerOnBaseVocaKnow, onDoubleClickListener, keyword, show4Buttons, wordListType, isRecordingVoca, sharedPreferences);

        return new VocaHolder(ItemVoca4buttonsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
//            case TYPE_SEARCH:
//                ((ContentSearchHolder) holder).bind(position);
//                break;
            case TYPE_HEADER:
                ((HeaderViewHolder) holder).bind(position);
                break;
            case TYPE_CONTENT:
                ((VocaHolder) holder).bind((IVocaFullPlayTTSItem) itemList.get(position - countOfNonData), position);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return itemList.size() + countOfNonData;
    }

    public void setData(List<IVocaFullPlayTTSItem> iVocaFullItemList) {
        itemList.clear();
        itemList.addAll(iVocaFullItemList);
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public void setShowPlayAll(boolean showPlayAll) {
        isShowPlayAll = showPlayAll;
    }

    public void setShow4Buttons(boolean show4Buttons) {
        this.show4Buttons = show4Buttons;
    }

    public void setSwipeToDelete(boolean swipeToDelete) {
        isSwipeToDelete = swipeToDelete;
    }

    public void setWordListType(WordListType wordListType) {
        this.wordListType = wordListType;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    class ContentSearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemContentSearchBinding binding;
        ContentSearchHolder(ItemContentSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
            hideInitSearchBtn(binding);
        }

        private void hideSearchUIForSmallList(com.dalread.databinding.ItemContentSearchBinding binding) {
            int dataCountToShowSearchUI = 10;
            binding.llSearch.setVisibility(View.VISIBLE);
            if (itemList.size() < dataCountToShowSearchUI && !isSearching()) {
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
        private WordListHeaderModel item;

        private ItemWordListPlayerHeaderBinding binding;
        HeaderViewHolder(ItemWordListPlayerHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.icPlayAll.setOnClickListener(this);
            binding.ivShowRecordedVoice.setOnClickListener(this);
        }


        public void bind(int position) {
            binding.icPlayAll.setVisibility(isShowPlayAll ? View.VISIBLE : View.GONE);
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

    public IVocaFullPlayTTSItem getItem(IVocaFullPlayTTSItem voca) {
        if (voca != null) {
            for (IVocaFullPlayTTSItem item : itemList) {
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

        for (int i = 0; i < itemList.size(); i++) {
            IVocaFullPlayTTSItem item = itemList.get(i);
            if (Voca.isSameVoca(voca, item)) {
                itemList.set(i, voca);
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
            binding.ivArrowRight.setOnClickListener(this);
            binding.ivSpeaker.setOnClickListener(this);
            binding.ivMic.setOnClickListener(this);
            binding.ivKnow.setOnClickListener(this);
            binding.ivKnown.setOnClickListener(this);
            binding.ivGrade1.setOnClickListener(this);
            binding.ivGrade2.setOnClickListener(this);
            binding.ivUnknown.setOnClickListener(this);
            binding.ivBookmark.setOnClickListener(this);
            binding.ivEditView.setOnClickListener(this);
            binding.ivGptIcon.setOnClickListener(this);
            binding.ivCopy.setOnClickListener(this);
            binding.ivTranslate.setOnClickListener(this);
            binding.rightView.setOnClickListener(this);
            binding.ivWebDictionary.setOnClickListener(this);
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

        public void bind(IVocaFullPlayTTSItem voca, int position) {
            this.voca = voca;
//            binding.swipeLayout.setEnabledSwipe(wordListType.equals(WordListType.SEARCH_HISTORY) ? true : false);
            binding.swipeLayout.setEnabledSwipe(isSwipeToDelete);

            binding.tvIndex.setText((voca.getVIIndex().toString() + " " + voca.getPersonAB()).trim());
            binding.ivKnow.setOnClickListener(new DoubleClick(onDoubleClickListenerOnBaseVocaKnow, voca));
            VocaKnow.updateIconVocaBookmark(binding.ivBookmark, voca, true);
            VocaKnow.updateIconVocaKnowText(binding.ivKnow, voca);
            BaseVoca.updateIconSpeaker2(binding.ivSpeaker, voca);
            if (UserUtil.isEditContentUser(context)) {
                Voca.updateIconMic(binding.ivMic, voca);
            }
            setTextViewValue(binding.tvVoca, voca.getVIVoca(), isSearchInStudyLang());
            displayWebDictionary();
            displayTranslate();
            displayPronounce();
            displayMeaning(); //binding.tvMeaning.setText(voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)));
            displayMeaningDetailed(voca);
            displayMeaningEnglish(voca);
            display4Buttons();
            displayEditViewButton();
            displayMicButton();
            displayCopyButton();
            displayBookmark();
            displayVocaKnow();
            if (UserUtil.isEditContentUser(context)) {
                Voca.updateMicSpeakerVisibility(isRecordingVoca, voca, binding.ivSpeaker, binding.ivMic, false);
            }
        }
        private void displayWebDictionary() {
//            if (bookId > 0) {
                binding.ivWebDictionary.setVisibility(View.GONE);
//            }
        }
        private void displayTranslate() {
            binding.ivTranslate.setVisibility(View.GONE);
        }
        private void displayBookmark() {
            binding.ivBookmark.setVisibility(View.VISIBLE);
//            if (wordListType == WordListType.USER_VOCA_BOOK_LOCAL) {
//                binding.ivBookmark.setVisibility(View.GONE);
//            } else {
//                binding.ivBookmark.setVisibility(View.VISIBLE);
//            }
        }
        private void displayVocaKnow() {
            if (AppFlavorUtil.isAraHangulApp()) {
                binding.llKnowValue.setVisibility(View.GONE);
            } else {
                binding.llKnowValue.setVisibility(View.VISIBLE);
            }
//            if (wordListType == WordListType.USER_VOCA_BOOK_LOCAL) {
//                binding.llKnowValue.setVisibility(View.GONE);
//            } else {
//                if (UserUtil.isDebugOrAdminUser(context))
//                    binding.llKnowValue.setVisibility(View.VISIBLE);
//                else
//                    binding.llKnowValue.setVisibility(View.GONE);
//            }
        }
        private void displayCopyButton() {
            binding.ivCopy.setVisibility(View.GONE);
//            binding.ivCopy.setVisibility(ConversationBookUtil.isConversationBook(wordListType, bookId) ? View.VISIBLE : View.GONE);
        }

        private void displayEditViewButton() {
            binding.ivEditView.setVisibility(View.GONE);
//            if ((UserUtil.isEditContentFullOrAdminUser(context)) || (wordListType == WordListType.USER_VOCA_BOOK_LOCAL)) {
//                binding.ivEditView.setVisibility(View.VISIBLE);
//            } else {
//                binding.ivEditView.setVisibility(UserUtil.canEditContentAfterAppUseCount(context) ? View.VISIBLE : View.GONE);
//            }
        }

        private void displayMicButton() {
            binding.ivMic.setVisibility(UserUtil.isDebugOrAdminUser(context) ? View.VISIBLE : View.GONE);
        }

        private void setTextViewValue(TextView textView, String string, boolean isShowColorKeyword) {
            if (Utils.hasValue(string)) {
                if (isShowColorKeyword && keyword.length() > 0) {
                    textView.setText(StringUtils.getSpanDefaultColorOnKeyword(string, keyword));
                } else {
                    textView.setText(string);
                }
            }
        }
        private boolean isSearchInStudyLang() {
            return sharedPreferences.getSearchInStudyLang();
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
            setTextViewValue(binding.tvMeaning, Voca.getMeaningOrEnglishMeaning(context, voca), !isSearchInStudyLang());
//            binding.tvMeaning.setText(Voca.getMeaningOrEnglishMeaning(context, voca));
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
                case R.id.iv_known:
                case R.id.iv_grade_1:
                case R.id.iv_grade_2:
                case R.id.iv_unknown:
                    if (UserUtil.isLoggedIn(context, true)) {
                        if (onDoubleClickListenerOnBaseVocaKnow != null) {
                            onDoubleClickListenerOnBaseVocaKnow.onClick(view, voca);
                        }
                    }
                break;
                case R.id.ivMic:
                    if (UserUtil.isLoggedIn(context, true)) {
                        if (onDoubleClickListener != null) {
//                        onPlaylistItemClickListener.onItemClick(voca);
                            onDoubleClickListener.onClick(view, voca);
                        }
                    }
                    break;
                case R.id.right_view:
                    notifyItemRemoved(getAbsoluteAdapterPosition());
                    itemList.remove(voca);
                    if (onDoubleClickListener != null) {
                        onDoubleClickListener.onClick(view, voca);
                    }
                    break;
                case R.id.ivCopy:
                    CopyTextUtil.copyToClipboardShowWhatCopied(context, voca.getVIVoca());
                    break;
                case R.id.ivWebDictionary:
                    OpenViewUtil.openExternalWebDictionary(context, voca);
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
