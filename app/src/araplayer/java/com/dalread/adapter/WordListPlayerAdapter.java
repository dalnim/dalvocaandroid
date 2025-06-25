package com.dalread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.component.FuriganaView;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.WordListHeaderModel;
import com.dalread.util.Constant;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WordListPlayerAdapter extends VocaListPlayerAdapter {

    public WordListPlayerAdapter(Context context, SubDatabase subDatabase, boolean displayPronunciation, OnClickListener listener, OnDoubleClickListener onDoubleClickListener) {
        super(context, subDatabase, displayPronunciation, listener, onDoubleClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        Object object = data.get(position);
        if (object instanceof WordListHeaderModel) {
            return VIEW_TYPE_HEADER;
        } else if (object instanceof DicModel) {
            //Dalnim : Don't show ITEM_FULL, now VIEW_TYPE_ITEM has FULL 4 buttons now
            return VIEW_TYPE_ITEM;

//            DicModel data = (DicModel) object;
//            if (Voca.isShow4Buttons(data.getVocaKnow())) {
//                return VIEW_TYPE_ITEM_FULL;
//            }
//            return VIEW_TYPE_ITEM;
        }
        return VIEW_TYPE_LOAD_MORE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(viewType, parent, false);
        if (viewType == VIEW_TYPE_HEADER)
            return new HeaderViewHolder(view);
        else if (viewType == VIEW_TYPE_ITEM)
            return new ItemViewHolder(view);
        else if (viewType == VIEW_TYPE_ITEM_FULL) {
            return new ItemFullViewHolder(view);
        } else if (viewType == VIEW_TYPE_DIALOG_ITEM) {
            return new ItemRubyViewHolder(view);
        }
        return new LoadMoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Object object = data.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind((WordListHeaderModel) object);
        } else if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bind((DicModel) object, position);
        } else if (holder instanceof ItemFullViewHolder) {
            ((ItemFullViewHolder) holder).bind((DicModel) object, position);
        } else if (holder instanceof ItemRubyViewHolder) {
            ((ItemRubyViewHolder) holder).bind((DicModel) object, position);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(ArrayList<Object> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivWordList) ImageView ivWordList;
        @BindView(R.id.ivKnow) ImageView ivKnow;
        @BindView(R.id.tvItemTitle) TextView tvTitle;
        @BindView(R.id.icPlayAll) View icPlayAll;
        private WordListHeaderModel item;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(WordListHeaderModel item) {
            this.item = item;
            ivWordList.setVisibility(View.GONE);
            icPlayAll.setVisibility(View.VISIBLE);
            if (item.isHideType()) {
                ivKnow.setVisibility(View.GONE);
            } else {
                VocaKnow.updateIconVocaKnow(ivKnow, item.getKnow());
                ivKnow.setVisibility(View.VISIBLE);
            }
            tvTitle.setText(item.getName());
        }

        @OnClick(R.id.icPlayAll)
        void onClick(View v) {
            if (listener != null) {
                listener.onClick(v, item);
            }
        }
    }

    public class BaseItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvIndex) TextView tvIndex;
        @BindView(R.id.tvFrequency) TextView tvFrequency;
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.tvMeaning) TextView tvMeaning;
        @BindView(R.id.icPlay) ImageView icPlay;
        @BindView(R.id.vEntireItem) RelativeLayout vEntireItem;
        public DicModel item;
        public int position;

        public BaseItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(DicModel item, int position) {
            this.item = item;
            vEntireItem.setOnClickListener(new DoubleClick(onDoubleClickListener, item));
//            this.item.setPosition(position);
            this.position = position;
            tvIndex.setText(String.valueOf(item.getIndex()));
            tvFrequency.setText(context.getString(R.string.format_frequency, item.getFrequency()));
            if (item.getVocaType() == 1) {
                if (item.getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                    if (item.getVocaKnowPronounce() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                        tvName.setTextColor(ContextCompat.getColor(context, R.color.color_ruby_text_normal));
                    } else {
                        tvName.setTextColor(ContextCompat.getColor(context, R.color.color_ruby_text_unknown_pronounce));
                    }
                } else {
                    tvName.setTextColor(ContextCompat.getColor(context, R.color.color_ruby_text_unknown_meaning));
                }
            } else {
                tvName.setTextColor(ContextCompat.getColor(context, R.color.color_ruby_text_normal));
            }
            tvName.setText(combinePronounce(Voca.getVocaDisplay(item)));
            tvMeaning.setText(item.getMeaning());
            Voca.updateIconSpeaker(icPlay, item);
        }

        public String combinePronounce(String strWord) {
            if (displayPronunciation && !TextUtils.isEmpty(item.getPronounce())
                    && (item.getVocaKnow() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN || item.getVocaKnowPronounce() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)) {
                strWord += " [" + item.getPronounce() + "]";
            }
            return strWord;
        }

        @OnClick({R.id.icPlay})
        void onClick(View v) {
            if (listener != null) {
                listener.onClick(v, item);
            }
        }
    }

    public class ItemViewHolder extends BaseItemViewHolder {

        @BindView(R.id.llVocaKnow) LinearLayout llVocaKnow;
        @BindView(R.id.tvVocaKnowValue) TextView tvVocaKnowValue;
        @BindView(R.id.ivVocaBookmark) ImageView ivVocaBookmark;
        @BindView(R.id.ivVocaKnowPronounce) ImageView ivVocaKnowPronounce;

        public ItemViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(DicModel item, int position) {
            super.bind(item, position);
            llVocaKnow.setOnClickListener(new DoubleClick(onDoubleClickListener, item));
            tvVocaKnowValue.setOnClickListener(new DoubleClick(onDoubleClickListener, item));

            VocaKnow.updateIconVocaKnow(context, tvVocaKnowValue, item.getVocaKnow());
            VocaKnow.updateIconVocaKnowPronounce(context, ivVocaKnowPronounce, item);
            VocaKnow.updateIconVocaBookmark(ivVocaBookmark, item.isVIBookmark(), false);
        }

        @OnClick({R.id.btnKnown, R.id.btnGrade1, R.id.btnGrade2, R.id.btnUnknown})
        void onClick(View v) {
            if (listener != null) {
                listener.onClick(v, item);
            }
        }
    }
    public class ItemFullViewHolder extends BaseItemViewHolder {

        public ItemFullViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(DicModel item, int position) {
            super.bind(item, position);
        }

        @OnClick({R.id.btnKnown, R.id.btnGrade1, R.id.btnGrade2, R.id.btnUnknown})
        void onClick(View v) {
            if (listener != null) {
                listener.onClick(v, item);
            }
        }
    }

    public class LoadMoreViewHolder extends RecyclerView.ViewHolder {

        public LoadMoreViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class ItemRubyViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        @BindView(R.id.tvIndex) TextView tvIndex;
        @BindView(R.id.tvFrequency) TextView tvFrequency;
        @BindView(R.id.tvName) FuriganaView tvName;
        @BindView(R.id.tvMeaning) TextView tvMeaning;
        @BindView(R.id.icPlay) ImageView icPlay;
        public DicModel item;
        public int position;

        public ItemRubyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tvName.setOnTouchListener(this);
        }

        public void bind(DicModel dic, int position) {
            this.item = dic;
//            this.item.setPosition(position);
            this.position = position;
            tvIndex.setText(String.valueOf(item.getIndex()));
            tvFrequency.setText(context.getString(R.string.format_frequency, item.getFrequency()));
            tvName.setTutor(true);
            tvName.resetText();
            tvName.setJText(item.getVocaDisplayRuby());
            tvMeaning.setText(item.getMeaningWords());
            Voca.updateIconSpeaker(icPlay, item);
        }

        @OnClick({R.id.icPlay})
        void onClick(View v) {
            if (listener != null) {
                listener.onClick(v, item);
            }
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (listener != null) {
                    listener.onClick(view, item);
                }
                return true;
            }
            return false;
        }
    }
}
