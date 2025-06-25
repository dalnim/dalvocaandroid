package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.ItemVoca4buttonsBinding;
import com.dalread.databinding.ItemWordListPlayerHeaderBinding;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.VocaKnowGroupSelected;
import com.dalread.model.WordListHeaderModel;
import com.dalread.model.WordListType;
import com.dalread.util.BaseVoca;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.CopyTextUtil;
import com.dalread.util.OpenViewUtil;
import com.dalread.util.StringUtils;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class VocaListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_CONTENT = 0;
    private static final int TYPE_HEADER = TYPE_CONTENT + 1;

    private int countOfNonData = 0; // Need to set 2 later to display TYPE_SEARCH
    private VocaKnowGroupSelected.Type type;
    private final List<Object> itemList = new ArrayList<>();
    private OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;
    private OnDoubleClickListener onDoubleClickListener;
    private Context context;
    private String keyword = "";
    private boolean show4Buttons;
    private boolean isFirstTime;
    private boolean isShowPlayAll;
    private boolean isSwipeToDelete = false;
    private WordListType wordListType;
    private IVocaFullPlayTTSItem isRecordingVoca;
    private SharedPreferencesDB sharedPreferences;

    public VocaListAdapter(Context context, OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow, OnDoubleClickListener onDoubleClickListener) {
        this.context = context;
        isFirstTime = true;
        isShowPlayAll = true;
        this.onDoubleClickListenerOnBaseVocaKnow = onDoubleClickListenerOnBaseVocaKnow;
        this.onDoubleClickListener = onDoubleClickListener;
        sharedPreferences = SharedPreferencesDB.getInstance(context);
    }

    @Override
    public int getItemViewType(int position) {
        Object object = itemList.get(position);
        if (object instanceof WordListHeaderModel) {
            return TYPE_HEADER;
        }
        return TYPE_CONTENT;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(ItemWordListPlayerHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            default:
                return new VocaHolder(ItemVoca4buttonsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                ((HeaderViewHolder) holder).bind((WordListHeaderModel)itemList.get(position - countOfNonData));
                break;
            case TYPE_CONTENT:
                ((VocaHolder) holder).bind((IVocaFullPlayTTSItem) itemList.get(position - countOfNonData));
                break;
        }
    }


    @Override
    public int getItemCount() {
        return itemList.size() + countOfNonData;
    }

    public void setData(VocaKnowGroupSelected<? extends IVocaBasicItem> vocaKnowGroupSelected) {
        itemList.clear();
        if (vocaKnowGroupSelected.isTypeAmkiGrade()) {
            itemList.addAll(vocaKnowGroupSelected.getAmkiGradeList());
        } else {
            itemList.addAll(vocaKnowGroupSelected.getVocaListByAlphabetOrder());
        }
    }
    public void notifyItemChanged(IVocaBasicItem voca) {
        int index = itemList.indexOf(voca);
        if (index != -1) {
            notifyItemChanged(index);
        }
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

    public class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        private WordListHeaderModel item;

        private ItemWordListPlayerHeaderBinding binding;
        HeaderViewHolder(ItemWordListPlayerHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.ivKnow.setVisibility(View.GONE);
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.icPlayAll.setOnClickListener(this);
            binding.ivShowRecordedVoice.setOnClickListener(this);
        }


        public void bind(WordListHeaderModel item) {
            this.item = item;
            binding.ivWordList.setVisibility(View.GONE);
            binding.ivShowRecordedVoice.setVisibility(View.GONE);
            binding.icPlayAll.setVisibility(View.VISIBLE);
            binding.tvItemTitle.setText(item.getName());
            BaseVocaKnow.updateIconVocaKnow(binding.ivKnow, item.getKnow());
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.icPlayAll:
                    if (onDoubleClickListener != null) {
                        onDoubleClickListener.onClick(view, item);
                    }
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
            binding.ivCopy.setOnClickListener(this);
            binding.ivTranslate.setOnClickListener(this);
            binding.ivWebDictionary.setOnClickListener(this);
            binding.rightView.setOnClickListener(this);
//            binding.swipeLayout.setOnActionsListener(new SwipeLayout.SwipeActionsListener() {
//                @Override
//                public void onOpen(int direction, boolean isContinuous) {
//                    if (direction == SwipeLayout.RIGHT) {
//                        // was executed swipe to the right
//                        int i = 0;
//                    } else if (direction == SwipeLayout.LEFT) {
//                        // was executed swipe to the left
//                        int i = 0;
//                    }
//                }
//
//                @Override
//                public void onClose() {
//                    int i = 0;
//                    // the main view has returned to the default state
//                }
//            });
        }

        public void bind(IVocaFullPlayTTSItem voca) {
            this.voca = voca;
//            binding.swipeLayout.setEnabledSwipe(wordListType.equals(WordListType.SEARCH_HISTORY) ? true : false);
            binding.tvIndex.setText((voca.getVIIndex().toString() + " " + voca.getPersonAB()).trim());
            binding.ivKnow.setOnClickListener(new DoubleClick(onDoubleClickListenerOnBaseVocaKnow, voca));
            binding.ivTranslate.setVisibility(View.GONE);
            BaseVocaKnow.updateIconVocaBookmark(binding.ivBookmark, voca, true);
            BaseVocaKnow.updateIconVocaKnowText(binding.ivKnow, voca);
            BaseVoca.updateIconSpeaker(binding.ivSpeaker, voca);
            binding.ivMic.setVisibility(View.GONE);
//            Voca.updateIconMic(binding.ivMic, voca);
            setTextViewValue(binding.tvVoca, voca.getVIVoca(), isSearchInStudyLang());
            displayWebDictionary();
            displayPronounce();
            displayMeaning();
            displayMeaningDetailed(voca);
            displayMeaningEnglish(voca);
            display4Buttons();
            displayCopyButton();
            displayEditViewButton();
            BaseVoca.updateMicSpeakerVisibility(isRecordingVoca, voca, binding.ivSpeaker, binding.ivMic, false);
//            displaySpeakerButton();
            displayMicButton();
        }
        private void displayCopyButton() {
            binding.ivCopy.setVisibility(View.GONE);
//            binding.ivCopy.setVisibility(ConversationBookUtil.isConversationBook(wordListType, bookId) ? View.VISIBLE : View.GONE);
        }
        private void displayWebDictionary() {
//            if (bookId > 0) {
            binding.ivWebDictionary.setVisibility(View.GONE);
//            }
        }
        private void displaySpeakerButton() {
            binding.ivSpeaker.setVisibility(UserUtil.isDebugOrAdminUser(context) ? View.VISIBLE : View.GONE);
        }
        private void displayMicButton() {
            binding.ivMic.setVisibility(UserUtil.isDebugOrAdminUser(context) ? View.VISIBLE : View.GONE);
        }
        private void displayEditViewButton() {
            binding.ivEditView.setVisibility(View.GONE);
//            if (UserUtil.isEditContentFullOrAdminUser(context)) {
//                binding.ivEditView.setVisibility(View.VISIBLE);
//            } else {
//                binding.ivEditView.setVisibility(UserUtil.canEditContentAfterAppUseCount(context) ? View.VISIBLE : View.GONE);
//            }
////            binding.ivEditView.setVisibility(UserUtil.isEditContentUser(context) ? View.VISIBLE : View.GONE);
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
            String meaningDetailed = BaseVoca.getMeaningDetailedOrEnglishMeaningDetailed(context, voca);
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
            BaseVoca.displayShortOrLongPronounce(context, voca, binding.tvShortPronounce, binding.tvLongPronounce);
        }

        private void displayMeaning() {
            setTextViewValue(binding.tvMeaning, BaseVoca.getMeaningOrEnglishMeaning(context, voca), !isSearchInStudyLang());
//            binding.tvMeaning.setText(Voca.getMeaningOrEnglishMeaning(context, voca));
        }
        private void displayMeaningEnglish(IVocaFullPlayTTSItem voca) {
            if (Utils.needToDisplayMeaningEnglish(context) && !Utils.isEmpty(voca.getVIMeaningEng())) {
                binding.tvMeaningEnglish.setVisibility(View.VISIBLE);
                binding.tvMeaningEnglish.setText(BaseVoca.wrapMeaningEnglish(voca));
            } else {
                binding.tvMeaningEnglish.setVisibility(View.GONE);
            }
            if (Utils.needToDisplayMeaningEnglish(context) && !Utils.isEmpty(voca.getVIMeaningEngDetailed())) {
                binding.tvMeaningEnglishDetailed.setVisibility(View.VISIBLE);
                binding.tvMeaningEnglishDetailed.setText(BaseVoca.wrapMeaningEnglishDetailed(voca));
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