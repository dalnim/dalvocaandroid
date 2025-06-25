package com.dalread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.helper.itemtouchhelper.ItemTouchHelperAdapter;
import com.dalread.helper.itemtouchhelper.ItemTouchHelperViewHolder;
import com.dalread.listener.OnDragListener;
import com.dalread.listener.OnVocaClickListener;
import com.dalread.model.VocaBook;
import com.dalread.model.VocaCountInBook;
import com.dalread.model.VocaInBook;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class VocaBookAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {

    private static final int TYPE_SUM = 0;
    private static final int TYPE_HEADER = TYPE_SUM + 1;
    private static final int TYPE_ITEM_OLD_STYLE = TYPE_HEADER + 1;
    private static final int TYPE_ITEM_NEW_STYLE = TYPE_ITEM_OLD_STYLE + 1;
    private static final int TYPE_LOAD_MORE = TYPE_ITEM_NEW_STYLE + 1;

    private ArrayList<Object> totalData;
    private ArrayList<Object> data;
    private OnVocaClickListener onVocaClickListener;
    private boolean selecting;
    private OnDragListener onDragListener;
    private boolean draggable;
    private int count1;
    private int count2;
    private int count99;
    private int countKnown;
    private int countUnknown;
    private boolean displayPronunciation;
    private boolean isBlinkMode;
    private boolean isAsteriskMode;
    private boolean isSearchTitle = true;
    private boolean isSearchMeaning = false;
    private String searchValue;
    private Context context;
    public VocaBookAdapter(Context context, boolean displayPronunciation) {
        totalData = new ArrayList<>();
        data = new ArrayList<>();
        this.context = context;
        this.displayPronunciation = displayPronunciation;
    }

    @Override
    public int getItemViewType(int position) {
        Object aData = data.get(position);
        if (aData instanceof VocaBook)
            return TYPE_SUM;
        if (aData instanceof Integer)
            return TYPE_HEADER;
        if (aData instanceof VocaInBook) {
            VocaInBook voca = (VocaInBook) aData;
            return Voca.isShow4Buttons(voca.getVocaKnow()) ? TYPE_ITEM_NEW_STYLE : TYPE_ITEM_OLD_STYLE;
        }
        return TYPE_LOAD_MORE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SUM)
            return new SumHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_voca_book, parent, false));
        if (viewType == TYPE_HEADER)
            return new HeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_targets, parent, false));
        if (viewType == TYPE_ITEM_OLD_STYLE)
            return new ItemOldStyleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voca_book, parent, false));
        if (viewType == TYPE_ITEM_NEW_STYLE)
            return new ItemNewStyleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voca_book_new, parent, false));
        return new LoadMoreHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SumHolder) {
            ((SumHolder) holder).bind((VocaBook) data.get(position), onVocaClickListener);
        } else if (holder instanceof HeaderHolder) {
            int vocaKnow = (int) data.get(position);
            int count;
            if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1) {
                count = count1;
            } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2) {
                count = count2;
            } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN) {
                count = count99;
            } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                count = countKnown;
            } else {
                count = countUnknown;
            }
            ((HeaderHolder) holder).bind(vocaKnow, count, onVocaClickListener);
        } else if (holder instanceof ItemHolder) {
            if (selecting) {
                ((ItemHolder) holder).bindSelect((VocaInBook) data.get(position), displayPronunciation, isAsteriskMode, onVocaClickListener, onDragListener);
            } else {
                ((ItemHolder) holder).bind((VocaInBook) data.get(position), displayPronunciation, draggable, isBlinkMode, isAsteriskMode, onVocaClickListener, onDragListener);
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public ArrayList<Object> generateData(List<Object> totalData) {
        final ArrayList<Object> temp = new ArrayList<>();
        if (Utils.isEmpty(searchValue)) {
            temp.addAll(totalData);
        } else {
            final int searchType = getSearchType(searchValue);
            final int searchIn = getValueSearchIn();
            DLog.d("generateData", "searchType=" + searchType + " - searchIn=" + searchIn);
            for (Object obj : totalData) {
                if (obj instanceof VocaInBook) {
                    final VocaInBook voca = (VocaInBook) obj;
                    if (checkSearch(searchType, searchIn, voca, searchValue)) {
                        temp.add(voca);
                    }
                } else {
                    temp.add(obj);
                }
            }
        }
        return temp;
    }

    private boolean checkSearch(int type, int searchIn, VocaInBook voca, String searchValue) {
        final String text = searchValue.replace(Constant.SEARCH.KEY_PERCENT, Constant.BASE_BLANK);
        DLog.d("checkSearch", "text=" + text);
        String str1;
        String str2 = Constant.BASE_BLANK;
        if (searchIn == Constant.SEARCH.VALUE.TITLE) {
            str1 = voca.getVocaDisplay();
        } else if (searchIn == Constant.SEARCH.VALUE.MEANING) {
            str1 = voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context));
        } else {
            str1 = voca.getVocaDisplay();
            str2 = voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context));
        }

        if (searchIn == Constant.SEARCH.VALUE.ALL) {
            if (type == Constant.SEARCH.TYPE.START) {
                return str1.startsWith(text) || str2.startsWith(text);
            } else if (type == Constant.SEARCH.TYPE.END) {
                return str1.endsWith(text) || str2.endsWith(text);
            }
            return str1.contains(text) || str2.contains(text);
        } else {
            if (type == Constant.SEARCH.TYPE.START) {
                return str1.startsWith(text);
            } else if (type == Constant.SEARCH.TYPE.END) {
                return str1.endsWith(text);
            }
            return str1.contains(text);
        }
    }

    private int getSearchType(String value) {
        if (value.startsWith(Constant.SEARCH.KEY_PERCENT))
            return Constant.SEARCH.TYPE.START;
        if (value.endsWith(Constant.SEARCH.KEY_PERCENT))
            return Constant.SEARCH.TYPE.END;
        return Constant.SEARCH.TYPE.MATCHED;
    }

    private int getValueSearchIn() {
        if (isSearchTitle() && isSearchMeaning())
            return Constant.SEARCH.VALUE.ALL;
        if (isSearchTitle())
            return Constant.SEARCH.VALUE.TITLE;
        return Constant.SEARCH.VALUE.MEANING;
    }

    public void setData(VocaBook book, List<VocaInBook> vocas) {
        totalData.clear();
        totalData.add(book);
        count1 = count2 = count99 = countKnown = countUnknown = 0;
        totalData.addAll(vocas);

        data.clear();
        data = generateData(totalData);
    }

    public void setData(VocaBook book, List<Object> grade1, List<Object> grade2, List<Object> grade99, List<Object> known, List<Object> unknown) {
        totalData.clear();
        totalData.add(book);
        count1 = count2 = count99 = countKnown = countUnknown = 0;
        if (grade1 != null && !grade1.isEmpty()) {
            totalData.add(Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1);
            final ArrayList<Object> tempGrade = generateData(grade1);
            totalData.addAll(tempGrade);
            count1 = tempGrade.size();
        }
        if (grade2 != null && !grade2.isEmpty()) {
            totalData.add(Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2);
            final ArrayList<Object> tempGrade = generateData(grade2);
            totalData.addAll(tempGrade);
            count2 = tempGrade.size();
        }
        if (unknown != null && !unknown.isEmpty()) {
            totalData.add(-1);
            final ArrayList<Object> tempGrade = generateData(unknown);
            totalData.addAll(tempGrade);
            countUnknown = tempGrade.size();
        }
        if (grade99 != null && !grade99.isEmpty()) {
            totalData.add(Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
            final ArrayList<Object> tempGrade = generateData(grade99);
            totalData.addAll(tempGrade);
            count99 = tempGrade.size();
        }
        if (known != null && !known.isEmpty()) {
            totalData.add(Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
            final ArrayList<Object> tempGrade = generateData(known);
            totalData.addAll(tempGrade);
            countKnown = tempGrade.size();
        }
        data.clear();
        data.addAll(totalData);
    }

    public void setLoadMore(boolean enable) {
        int pos = data.size();
        if (enable) {
            data.add(null);
            notifyItemInserted(pos);
        } else if (data.remove(null)) {
            notifyItemRemoved(pos);
        }
    }

    public boolean isSwipeable(int position) {
        int viewType = getItemViewType(position);
        return viewType == TYPE_ITEM_OLD_STYLE || viewType == TYPE_ITEM_NEW_STYLE;
    }

    public void setOnVocaClickListener(OnVocaClickListener onVocaClickListener) {
        this.onVocaClickListener = onVocaClickListener;
    }

    public void notifyRegisteredOrRemoved(VocaInBook voca) {
        notifyItemChanged(data.indexOf(voca));
    }

    public Object removeVoca(int position, boolean notify) {
        Object aData = data.remove(position);
        if (notify) {
            notifyItemRemoved(position);
        }
        return aData;
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
    }

    public void setBlinkMode(boolean isBlinkMode) {
        this.isBlinkMode = isBlinkMode;
        notifyDataSetChanged();
    }

    public void setAsteriskMode(boolean isAsteriskMode) {
        this.isAsteriskMode = isAsteriskMode;
        notifyDataSetChanged();
    }


    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        int viewType = getItemViewType(toPosition);
        if (viewType == TYPE_ITEM_OLD_STYLE || viewType == TYPE_ITEM_NEW_STYLE) {
            Collections.swap(data, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    public void setOnDragListener(OnDragListener onDragListener) {
        this.onDragListener = onDragListener;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public static class SumHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_count)
        TextView tvCount;

        @BindString(R.string.tpl_wb_known_word_count)
        String tplCount;

        private OnVocaClickListener onVocaClickListener;

        public SumHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(VocaBook book, OnVocaClickListener onVocaClickListener) {
            this.onVocaClickListener = onVocaClickListener;

            String text;
            if (book == null) {
                text = "";
                tvName.setText(text);
                tvCount.setText(text);
            } else {
                text = book.getName();
                tvName.setText(text);
                VocaCountInBook countInBook = book.getCountInBook();
                if (countInBook == null) {
                    text = String.format(tplCount, 0, 0);
                } else {
                    text = String.format(tplCount, countInBook.getKnownWordCount(), countInBook.getAllWordCount());
                }
                tvCount.setText(text);
            }
        }

        @OnClick({R.id.ic_play_all})
        void onClick(View view) {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.ic_play_all:
                    if (onVocaClickListener != null) {
                        onVocaClickListener.onPlayAllClick();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_grade_number)
        TextView tvGradeNumber;
        @BindView(R.id.tv_grade_text)
        TextView tvGradeText;

        @BindString(R.string.tpl_grade)
        String tplGrade;
        @BindString(R.string.tpl_exclude)
        String tplExclude;
        @BindString(R.string.tpl_known)
        String tplKnown;
        @BindString(R.string.tpl_unknown)
        String tplUnknown;

        private int vocaKnow;
        private OnVocaClickListener onVocaClickListener;

        public HeaderHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(int vocaKnow, int count, OnVocaClickListener onVocaClickListener) {
            this.vocaKnow = vocaKnow;
            this.onVocaClickListener = onVocaClickListener;

            VocaKnow.updateIconVocaKnow(context, tvGradeNumber, vocaKnow);
            String text;
            if (vocaKnow == -1) {
                text = String.format(tplUnknown, count);
            } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                text = String.format(tplKnown, count);
            } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN) {
                text = String.format(tplExclude, count);
            } else {
                text = String.format(tplGrade, Voca.ordinal(vocaKnow), count);
            }
            tvGradeText.setText(text);
        }

        @OnClick({R.id.ic_play_all})
        void onClick() {
            if (onVocaClickListener != null) {
                onVocaClickListener.onPlayAllClick(vocaKnow);
            }
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        @BindView(R.id.v_foreground)
        View vForeGround;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_meaning)
        TextView tvMeaning;
        @BindView(R.id.tv_index)
        TextView tvIndex;
        @BindView(R.id.ic_play)
        ImageView icPlay;
        @BindView(R.id.tv_evaluate)
        TextView tvEvaluate;
        @BindView(R.id.ic_right)
        ImageView icRight;
        @BindView(R.id.ic_drag)
        ImageView icDrag;

        @BindColor(R.color.color_ruby_text_normal)
        int clNormal;
        @BindColor(R.color.color_ruby_text_unknown_pronounce)
        int clUnknownPronounce;
        @BindColor(R.color.color_ruby_text_unknown_meaning)
        int clUnknownMeaning;

        protected VocaInBook voca;
        private boolean displayPronunciation;
        private boolean isBlinkMode;
        protected OnVocaClickListener onVocaClickListener;
        protected OnDragListener onDragListener;
        private int oldPos;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            oldPos = -1;
        }

        protected void innerBind(final VocaInBook voca, boolean displayPronunciation, boolean isAsteriskMode, final OnVocaClickListener onVocaClickListener, OnDragListener onDragListener) {
            this.voca = voca;
            this.displayPronunciation = displayPronunciation;
            isBlinkMode = false;
            this.onVocaClickListener = onVocaClickListener;
            this.onDragListener = onDragListener;

            String text = Voca.getVocaDisplay(voca);
            if (isAsteriskMode) {
                tvName.setTextColor(clNormal);
                text = Voca.changeVocaWithSmartAsterisk(text, voca.getVocaKnow());
            } else if (voca.getVocaType() == 1) {
                if (voca.getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                    if (voca.getVocaKnowPronounce() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                        tvName.setTextColor(clNormal);
                    } else {
                        tvName.setTextColor(clUnknownPronounce);
                        text = combinePronounce(text);
                    }
                } else {
                    tvName.setTextColor(clUnknownMeaning);
                    text = combinePronounce(text);
                }
            } else {
                tvName.setTextColor(clNormal);
                text = combinePronounce(text);
            }
            tvName.setText(text);
            text = voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context));
            tvMeaning.setText(text);
            text = voca.getIndex() + " " + voca.getPersonAB();
            tvIndex.setText(text);
            Voca.updateIconSpeaker(icPlay, voca);
            text = Voca.getEvaluateText(itemView.getContext(), voca.getEvaluateVocaGrade());
            tvEvaluate.setText(text);
            tvEvaluate.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        }

        private String combinePronounce(String strWord) {
            if (displayPronunciation && !TextUtils.isEmpty(voca.getPronounce())
                    && (voca.getVocaKnow() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN || voca.getVocaKnowPronounce() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)) {
                strWord += " [" + voca.getPronounce() + "]";
            }
            return strWord;
        }

        public void bind(final VocaInBook voca, boolean displayPronunciation, boolean draggable, boolean isBlinkMode, boolean isAsteriskMode, final OnVocaClickListener onVocaClickListener, OnDragListener onDragListener) {
            innerBind(voca, displayPronunciation, isAsteriskMode, onVocaClickListener, onDragListener);
            this.isBlinkMode = isBlinkMode;

            if (isBlinkMode) {
                icDrag.setVisibility(View.GONE);
                icRight.setVisibility(View.GONE);
            } else if (draggable) {
                icDrag.setVisibility(View.VISIBLE);
                icRight.setVisibility(View.GONE);
            } else {
                icDrag.setVisibility(View.GONE);
                icRight.setImageResource(R.drawable.ic_keyboard_arrow_right_36dp);
                icRight.setVisibility(View.VISIBLE);
            }
        }

        public void bindSelect(final VocaInBook voca, boolean displayPronunciation, boolean isAsteriskMode, final OnVocaClickListener onVocaClickListener, OnDragListener onDragListener) {
            innerBind(voca, displayPronunciation, isAsteriskMode, onVocaClickListener, onDragListener);

            icDrag.setVisibility(View.GONE);
            icRight.setImageResource(R.drawable.ic_check_box_black_24dp);
            icRight.setVisibility(voca.isVIChecked() ? View.VISIBLE : View.INVISIBLE);
        }

        @OnClick({R.id.ic_play, R.id.v_foreground})
        void onClick(View view) {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.ic_play:
                    if (onVocaClickListener != null) {
                        onVocaClickListener.onPlayClick(voca);
                    }
                    break;
                case R.id.v_foreground:
                    if (isBlinkMode) {
                        vForeGround.setBackgroundResource(R.color.color_blink);
                        vForeGround.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                vForeGround.setBackgroundResource(R.color.colorBackground);
                            }
                        }, Constant.STOP_BLINK_DELAY_TIME);
                    } else if (onVocaClickListener != null) {
                        onVocaClickListener.onInfoClick(voca);
                    }
                    break;
                default:
                    break;
            }
        }

        @OnTouch(R.id.ic_drag)
        boolean onTouch(View view, MotionEvent event) {
            if (onDragListener != null) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    oldPos = getAdapterPosition();
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
            if (onVocaClickListener != null) {
                int pos = getAdapterPosition();
                if (pos != oldPos) {
                    onVocaClickListener.onDisplayOrderChange(voca, pos);
                }
            }
            oldPos = -1;
        }
    }

    public class ItemOldStyleHolder extends ItemHolder {

        @BindView(R.id.tv_grade_number)
        TextView tvGradeNumber;

        public ItemOldStyleHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void innerBind(VocaInBook voca, boolean displayPronunciation, boolean isAsteriskMode, OnVocaClickListener onVocaClickListener, OnDragListener onDragListener) {
            super.innerBind(voca, displayPronunciation, isAsteriskMode, onVocaClickListener, onDragListener);

            VocaKnow.updateIconVocaKnow(context, tvGradeNumber, voca.getVocaKnow());
        }

        @OnClick({
                R.id.ic_play, R.id.v_foreground,    // both
                R.id.tv_grade_number                // old style
        })
        void onClick(View view) {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.tv_grade_number:
                    if (onVocaClickListener != null) {
                        onVocaClickListener.onVocaKnowClick(voca);
                    }
                    break;
                default:
                    super.onClick(view);
                    break;
            }
        }
    }

    public class ItemNewStyleHolder extends ItemHolder {

        public ItemNewStyleHolder(View itemView) {
            super(itemView);
        }

        @OnClick({
                R.id.ic_play, R.id.v_foreground,                                        // both
                R.id.btn_known, R.id.btn_grade_1, R.id.btn_grade_2, R.id.btn_exclude    // new style
        })
        void onClick(View view) {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.btn_known:
                    if (onVocaClickListener != null) {
                        onVocaClickListener.onVocaKnowClick(voca, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
                    }
                    break;
                case R.id.btn_grade_1:
                    if (onVocaClickListener != null) {
                        onVocaClickListener.onVocaKnowClick(voca, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1);
                    }
                    break;
                case R.id.btn_grade_2:
                    if (onVocaClickListener != null) {
                        onVocaClickListener.onVocaKnowClick(voca, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2);
                    }
                    break;
                case R.id.btn_exclude:
                    if (onVocaClickListener != null) {
                        onVocaClickListener.onVocaKnowClick(voca, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
                    }
                    break;
                default:
                    super.onClick(view);
                    break;
            }
        }
    }

    public class LoadMoreHolder extends RecyclerView.ViewHolder {

        public LoadMoreHolder(View itemView) {
            super(itemView);
        }
    }

    public boolean isSearchTitle() {
        return isSearchTitle;
    }

    public void setSearchTitle(boolean searchMeaning) {
        isSearchTitle = searchMeaning;
    }

    public boolean isSearchMeaning() {
        return isSearchMeaning;
    }

    public void setSearchMeaning(boolean searchMeaning) {
        isSearchMeaning = searchMeaning;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue.toLowerCase();
    }

    public String getSearchValue() {
        return searchValue;
    }
}
