package com.dalread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnVocaDoYouKnowClickListener;
import com.dalread.model.VocaDoYouKnow;
import com.dalread.util.Constant;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VocaDoYouKnowAdapter extends RecyclerView.Adapter {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM_OLD_STYLE = TYPE_HEADER + 1;
    private static final int TYPE_ITEM_NEW_STYLE = TYPE_ITEM_OLD_STYLE + 1;

    private ArrayList<Object> dataList;
    private LinkedHashMap<String, ArrayList<VocaDoYouKnow>> dataMap;
    private String tplVocaFrom;
    private OnVocaDoYouKnowClickListener listener;
    private boolean displayPronunciation;
    private static Context context;

    public VocaDoYouKnowAdapter(Context context, boolean displayPronunciation) {
        dataList = new ArrayList<>();
        dataMap = new LinkedHashMap<>();
        this.context = context;
        this.displayPronunciation = displayPronunciation;
    }

    @Override
    public int getItemViewType(int position) {
        Object aData = dataList.get(position);
        if (aData instanceof VocaDoYouKnow) {
            VocaDoYouKnow voca = (VocaDoYouKnow) aData;
            int vocaKnow = voca.getAmkiKnow();
            if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN
                    || vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1
                    || vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2
                    || vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN)
                return TYPE_ITEM_OLD_STYLE;
            return TYPE_ITEM_NEW_STYLE;
        }
        return TYPE_HEADER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM_OLD_STYLE)
            return new ItemOldStyleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voca_book, parent, false));
        if (viewType == TYPE_ITEM_NEW_STYLE)
            return new ItemNewStyleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voca_book_new, parent, false));
        return new HeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_do_you_know, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).bind((String) dataList.get(position));
        } else if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind((VocaDoYouKnow) dataList.get(position), displayPronunciation, listener);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setData(LinkedHashMap<String, ArrayList<VocaDoYouKnow>> dataMap, String tplVocaFrom) {
        this.dataMap = dataMap;
        this.tplVocaFrom = tplVocaFrom;
        dataList.clear();
        for (String key : dataMap.keySet()) {
            ArrayList<VocaDoYouKnow> vocas = dataMap.get(key);
            if (vocas != null) {
                dataList.add(String.format(tplVocaFrom, key, vocas.size()));
                dataList.addAll(vocas);
            }
        }
        notifyDataSetChanged();
    }

    public void setListener(OnVocaDoYouKnowClickListener listener) {
        this.listener = listener;
    }

    public void refreshVoca(VocaDoYouKnow voca) {
        int pos = dataList.indexOf(voca);
        if (pos > -1) {
            notifyItemRemoved(pos);
        }
    }

    public void removeVoca(VocaDoYouKnow voca) {
        int pos = dataList.indexOf(voca);
        if (pos > -1) {
            dataList.remove(pos);
            String key = voca.getName();
            ArrayList<VocaDoYouKnow> vocas = dataMap.get(key);
            if (vocas == null) {
                notifyItemRemoved(pos);
            } else {
                vocas.remove(voca);
                if (vocas.isEmpty()) {
                    dataMap.remove(key);
                    dataList.remove(pos - 1);
                    notifyItemRangeRemoved(pos - 1, 2);
                } else {
                    for (int i = pos - 1; i >= 0; i--) {
                        Object aData = dataList.get(i);
                        if (aData instanceof String) {
                            dataList.set(i, String.format(tplVocaFrom, key, vocas.size()));
                            notifyItemChanged(i);
                            break;
                        }
                    }
                    notifyItemRemoved(pos);
                }
            }
        }
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_from)
        TextView tvFrom;

        public HeaderHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(String from) {
            tvFrom.setText(from);
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_meaning)
        TextView tvMeaning;
        @BindView(R.id.ic_play)
        ImageView icPlay;
        @BindView(R.id.ic_right)
        ImageView icRight;
        @BindView(R.id.ic_drag)
        ImageView icDrag;

        protected VocaDoYouKnow voca;
        protected OnVocaDoYouKnowClickListener listener;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        protected void innerBind(VocaDoYouKnow voca, boolean displayPronunciation, OnVocaDoYouKnowClickListener listener) {
            this.voca = voca;
            this.listener = listener;

            String text = Voca.getVocaDisplay(voca);
            if (TextUtils.isEmpty(text)) {
                text = voca.getVoca();
            }
            if (displayPronunciation && !TextUtils.isEmpty(voca.getPronounce())) {
                text += " [" + voca.getPronounce() + "]";
            }
            tvName.setText(text);
            text = voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context));
            tvMeaning.setText(text);
            Voca.updateIconSpeaker(icPlay, voca);
        }

        public void bind(final VocaDoYouKnow voca, boolean displayPronunciation, OnVocaDoYouKnowClickListener listener) {
            innerBind(voca, displayPronunciation, listener);

            icDrag.setVisibility(View.GONE);
            icRight.setImageResource(R.drawable.ic_keyboard_arrow_right_36dp);
            icRight.setVisibility(View.VISIBLE);
        }

        @OnClick({R.id.ic_play, R.id.v_foreground})
        void onClick(View view) {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.ic_play:
                    if (listener != null) {
                        listener.onPlayClick(voca);
                    }
                    break;
                case R.id.v_foreground:
                    if (listener != null) {
                        listener.onInfoClick(voca);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public class ItemOldStyleHolder extends ItemHolder {

        @BindView(R.id.tv_grade_number)
        TextView tvGradeNumber;

        public ItemOldStyleHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void innerBind(VocaDoYouKnow voca, boolean displayPronunciation, OnVocaDoYouKnowClickListener listener) {
            super.innerBind(voca, displayPronunciation, listener);

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
                    if (listener != null) {
                        listener.onGradeClick(voca);
                    }
                    break;
                default:
                    super.onClick(view);
                    break;
            }
        }
    }

    public static class ItemNewStyleHolder extends ItemHolder {

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
                    if (listener != null) {
                        listener.onGradeClick(voca, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
                    }
                    break;
                case R.id.btn_grade_1:
                    if (listener != null) {
                        listener.onGradeClick(voca, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1);
                    }
                    break;
                case R.id.btn_grade_2:
                    if (listener != null) {
                        listener.onGradeClick(voca, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2);
                    }
                    break;
                case R.id.btn_exclude:
                    if (listener != null) {
                        listener.onGradeClick(voca, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
                    }
                    break;
                default:
                    super.onClick(view);
                    break;
            }
        }
    }
}
