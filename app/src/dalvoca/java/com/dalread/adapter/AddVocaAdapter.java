package com.dalread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnAddVocaListener;
import com.dalread.model.VocaSearch;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddVocaAdapter extends RecyclerView.Adapter {

    private static final int TYPE_SUM = 0;
    private static final int TYPE_ITEM = TYPE_SUM + 1;
    private static final int TYPE_LOAD_MORE = TYPE_ITEM + 1;

    private ArrayList<Object> data;
    private boolean displayPronunciation;
    private OnAddVocaListener listener;
    private Context context;

    public AddVocaAdapter(Context context, boolean displayPronunciation) {
        data = new ArrayList<>();
        this.context = context;
        this.displayPronunciation = displayPronunciation;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) instanceof Integer)
            return TYPE_SUM;
        if (data.get(position) instanceof VocaSearch)
            return TYPE_ITEM;
        return TYPE_LOAD_MORE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SUM)
            return new SumHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_add_voca, parent, false));
        if (viewType == TYPE_ITEM)
            return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_voca, parent, false));
        return new LoadMoreHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SumHolder) {
            ((SumHolder) holder).bind((Integer) data.get(position), listener);
        } else if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind((VocaSearch) data.get(position), displayPronunciation, listener);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<VocaSearch> vocas) {
        data.clear();
        if (vocas == null) {
            data.add(0);
        } else {
            data.add(vocas.size());
            data.addAll(vocas);
        }
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

    public void setListener(OnAddVocaListener listener) {
        this.listener = listener;
    }

    public void notifyRegisteredOrRemoved(VocaSearch voca) {
        notifyItemChanged(data.indexOf(voca));
    }

    public static class SumHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_count)
        TextView tvCount;

        @BindString(R.string.tpl_aw_word_count)
        String tplCount;

        private OnAddVocaListener listener;

        public SumHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(int count, OnAddVocaListener listener) {
            this.listener = listener;

            String text = String.format(tplCount, count);
            tvCount.setText(text);
        }

        @OnClick({R.id.ic_play_all})
        void onClick(View view) {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.ic_play_all:
                    if (listener != null) {
                        listener.onPlayAllClick();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_meaning)
        TextView tvMeaning;
        @BindView(R.id.tv_grade_number)
        TextView tvGradeNumber;
        @BindView(R.id.btn_action)
        Button btnAction;
        @BindView(R.id.ic_play)
        ImageView icPlay;

        private VocaSearch voca;
        private OnAddVocaListener listener;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(VocaSearch voca, boolean displayPronunciation, OnAddVocaListener listener) {
            this.voca = voca;
            this.listener = listener;

            String text = Voca.getVocaDisplay(voca);
            if (displayPronunciation && !TextUtils.isEmpty(voca.getPronounce())) {
                text += " [" + voca.getPronounce() + "]";
            }
            tvName.setText(text);
            text = voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context));
            tvMeaning.setText(text);
            VocaKnow.updateIconVocaKnow(context, tvGradeNumber, voca.getVocaKnow());
            if (voca.isBelongToBook()) {
                btnAction.setText(R.string.remove);
                btnAction.setBackgroundResource(R.color.color_red);
            } else {
                btnAction.setText(R.string.register);
                btnAction.setBackgroundResource(R.color.colorBlue);
            }
            Voca.updateIconSpeaker(icPlay, voca);
        }

        @OnClick({R.id.ic_play, R.id.btn_action, R.id.v_item})
        void onClick(View view) {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.ic_play:
                    if (listener != null) {
                        listener.onPlayClick(voca);
                    }
                    break;
                case R.id.btn_action:
                    if (listener != null) {
                        listener.onRegisterClick(voca);
                    }
                    break;
                case R.id.v_item:
                    if (listener != null) {
                        listener.onInfoClick(voca);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static class LoadMoreHolder extends RecyclerView.ViewHolder {

        public LoadMoreHolder(View itemView) {
            super(itemView);
        }
    }
}
