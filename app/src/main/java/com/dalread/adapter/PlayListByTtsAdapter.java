package com.dalread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemPlaylistByTtsBinding;
import com.dalread.databinding.ItemPlaylistByTtsHeaderBinding;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.BackupRecording;
import com.dalread.util.BaseVoca;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.RepeatUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;

public class PlayListByTtsAdapter extends RecyclerView.Adapter {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = TYPE_HEADER + 1;

    private List<Object> items;
    private boolean displayPronunciation;
    private boolean isRandomPlayInPlayListByTts = false;
    private boolean isConversationBook = false;
    private Context context;
    private OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;
    private OnDoubleClickListener onDoubleClickListener;
    private boolean isListenComprehension2;
    private int currentListenComprehensionCount;

    /*
     * @deprecated Replaced by {@link #PlayListByTtsAdapter()}
     */
    @Deprecated
    public PlayListByTtsAdapter(Context context, boolean displayPronunciation) {
        this.context = context;
        this.items = new ArrayList<>();
        this.displayPronunciation = displayPronunciation;
    }

    public PlayListByTtsAdapter(Context context, boolean displayPronunciation, OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow, OnDoubleClickListener onDoubleClickListener) {
        this.context = context;
        this.items = new ArrayList<>();
        this.displayPronunciation = displayPronunciation;
        this.onDoubleClickListenerOnBaseVocaKnow = onDoubleClickListenerOnBaseVocaKnow;
        this.onDoubleClickListener = onDoubleClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new HeaderHolder(ItemPlaylistByTtsHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
        return new ItemHolder(ItemPlaylistByTtsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            ((HeaderHolder) holder).bind();
        } else if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind(items.get(position-1), displayPronunciation);
        }
    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
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

    public void setBackupRecordingData(List<BackupRecording> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public int notifyPlaylistItemChanged(Object item) {
        int pos = items.indexOf(item);
        notifyItemChanged(pos + 1);
        return pos;
    }

    public void setListenComprehension2(boolean isListenComprehension2) {
        this.isListenComprehension2 = isListenComprehension2;
    }
    public void setCurrentListenComprehensionCount(int currentListenComprehensionCount) {
        this.currentListenComprehensionCount = currentListenComprehensionCount;
    }

    public void setConversationBook(boolean conversationBook) {
        isConversationBook = conversationBook;
    }

    //TODO : Need to check the VocaType too.
    public IVocaFullPlayTTSItem getItemByVocaId(String vocaId) {
        if (!TextUtils.isEmpty(vocaId)) {
            for (Object object : items) {
                if (object instanceof IVocaFullPlayTTSItem) {
                    IVocaFullPlayTTSItem item = (IVocaFullPlayTTSItem) object;
                    if (vocaId.equals(String.valueOf(item.getVIVocaId()))) {
                        return item;
                    }
                }
            }
        }
        return null;
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        private ItemPlaylistByTtsHeaderBinding binding;
        HeaderHolder(ItemPlaylistByTtsHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.scRandomPlayInPlayListByTts.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   isRandomPlayInPlayListByTts = !isRandomPlayInPlayListByTts;
                   binding.scRandomPlayInPlayListByTts.setChecked(isRandomPlayInPlayListByTts);
                   onDoubleClickListener.onClick(view, isRandomPlayInPlayListByTts);
               }
           });
        }

        public void bind() {
            binding.tvVocaCountAll.setText(context.getString(R.string.title_voca_count , items.size()));
            binding.scRandomPlayInPlayListByTts.setVisibility(isConversationBook ? View.GONE : View.VISIBLE);
            binding.scRandomPlayInPlayListByTts.setChecked(isRandomPlayInPlayListByTts);
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindString(R.string.tpl_version)
        String tplVersion;
        @BindColor(R.color.color_ruby_text_normal)
        int clNormal;
        @BindColor(R.color.color_ruby_text_unknown_pronounce)
        int clUnknownPronounce;
        @BindColor(R.color.color_ruby_text_unknown_meaning)
        int clUnknownMeaning;

        private Object item;
        private boolean displayPronunciation;
//        private OnPlaylistItemClickListener listener;

        private ItemPlaylistByTtsBinding binding;
        ItemHolder(ItemPlaylistByTtsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.llMain.setOnClickListener(this);
            binding.ivSpeaker.setOnClickListener(this);
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
                            onDoubleClickListenerOnBaseVocaKnow.onClick(view, item);
                        }
                    }
                    break;
                case R.id.ivMic:
                    if (UserUtil.isLoggedIn(context, true)) {
                        if (onDoubleClickListener != null) {
                            onDoubleClickListener.onClick(view, item);
                        }
                    }
                    break;
                default:
                    if (onDoubleClickListener != null) {
                        onDoubleClickListener.onClick(view, item);
                    }
                    break;

            }
        }

        public void bind(Object item, boolean displayPronunciation) {
            this.item = item;
            this.displayPronunciation = displayPronunciation;

            if (item instanceof IVocaFullPlayTTSItem){
                IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) item;

                binding.ivKnow.setOnClickListener(new DoubleClick(onDoubleClickListenerOnBaseVocaKnow, voca));
                BaseVocaKnow.updateIconVocaBookmark(binding.ivBookmark, voca, true);
                BaseVocaKnow.updateIconVocaKnowText(binding.ivKnow, voca);
                BaseVoca.updateIconSpeaker(binding.ivSpeaker, voca);

                binding.llRoot.setBackgroundResource(voca.isVIPlaying() ? R.color.backgroundCellSubtitleCurrentPlayingColor : R.color.backgroundCellColor);
                binding.icCheck.setVisibility(voca.isVIChecked() ? View.VISIBLE : View.INVISIBLE);
                String vocaDisplay = BaseVoca.getVocaDisplay(voca);
                String pronounce = voca.getVIPronounce();
                displayPronounce(voca);
////                if (VocaKnow.isVocaTypeWord(voca)) {
//                    if (((IVocaBasicItem) item).getVIVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
//                        if (((IVocaBasicItem) item).getVIVocaKnowPronounce() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
//                            //Let's set color later
////                            binding.tvVocaStudy.setTextColor(BaseColorUtil.getTextPrimaryColor(context));
//                        } else {
////                            binding.tvVocaStudy.setTextColor(BaseColorUtil.getVocaUnknownPronounceColor(context));
//                            vocaDisplay = combinePronounce(vocaDisplay, pronounce);
//                        }
//                    } else {
////                        binding.tvVocaStudy.setTextColor(BaseColorUtil.getVocaUnknownColor(context));
//                        vocaDisplay = combinePronounce(vocaDisplay, pronounce);
//                    }
////                } else {
////                    binding.tvFirst.setTextColor(BaseColorUtil.getTextPrimaryColor(context));
////                    text = combinePronounce(text, pronounce);
////                }
                binding.tvVoca.setText(vocaDisplay);
                binding.tvMeaning.setText(BaseVoca.getMeaningOrEnglishMeaning(context, voca));
//                binding.tvMeaning.setText(voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)));
                displayMeaningEnglish(voca);
                binding.tvIndex.setText((voca.getVIIndex().toString() + " " + voca.getPersonAB()).trim());

                if (isListenComprehension2) {
                    int totalComprehensionCount = RepeatUtil.getRepeatValueWithoutDifficultWords(context, voca);
                    int currentListenComprehensionCountToDisplay = (currentListenComprehensionCount + 1) > totalComprehensionCount ? totalComprehensionCount : (currentListenComprehensionCount + 1);
                    binding.tvRepeatCount.setText(currentListenComprehensionCountToDisplay + "/" + totalComprehensionCount);
                    binding.tvRepeatCount.setText(String.valueOf(totalComprehensionCount));
                    binding.tvRepeatCount.setVisibility(View.VISIBLE);

                } else {
                    binding.tvRepeatCount.setVisibility(View.GONE);
                }


            } else if (item instanceof BackupRecording) {
                BackupRecording backupRecording = (BackupRecording) item;
                binding.llRoot.setBackgroundResource(backupRecording.isPlaying() ? R.color.backgroundCellSubtitleCurrentPlayingColor : R.color.backgroundCellColor);
                binding.icCheck.setVisibility(backupRecording.isChecked() ? View.VISIBLE : View.INVISIBLE);
                String text = String.format(tplVersion, backupRecording.getVERSION());
                binding.tvVoca.setText(text);
                text = backupRecording.getDATE_STRING();
                binding.tvMeaning.setText(text);
                BaseVoca.updateIconPlayBackupRecording(binding.ivSpeaker, backupRecording);
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

//        private String combinePronounce(String strWord, String pronounce) {
//            if (displayPronunciation && !TextUtils.isEmpty(pronounce)) {
//                if (!(item instanceof IVocaBasicItem)
//                        || (((IVocaBasicItem) item).getVIVocaKnow() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN || ((IVocaBasicItem) item).getVIVocaKnowPronounce() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)) {
//                    strWord += " [" + pronounce + "]";
//                }
//            }
//            return strWord;
//        }
    }
}
