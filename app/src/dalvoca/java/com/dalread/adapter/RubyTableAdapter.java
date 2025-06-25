package com.dalread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.component.FuriganaView;
import com.dalread.listener.OnRubyTableListener;
import com.dalread.model.RubyListModel;
import com.dalread.model.RubyTextModel;
import com.dalread.util.DLog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RubyTableAdapter extends RecyclerView.Adapter {
    private final String TAG = "RubyTableAdapter";
    private RubyListModel data;
    private OnRubyTableListener listener;
    public RubyTableAdapter(RubyListModel data, OnRubyTableListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ruby_table, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bind(position, data.getRubyTextModels().get(position));
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.getRubyTextModels().size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_content) FuriganaView tvContent;
        private int currentPosition;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tvContent.setOnTextSelectedListener(onTextSelectedListener);
        }

        public FuriganaView.OnTextSelectedListener onTextSelectedListener = new FuriganaView.OnTextSelectedListener() {
            @Override
            public void onTextSelected(String text, RubyTextModel rubyTextModel) {
                if (listener != null) {
                    listener.onTextSelected(text, rubyTextModel);
                }
            }

            @Override
            public void onDoubleClick(String text, RubyTextModel rubyTextModel) {
                data.getRubyTextModels().set(currentPosition, rubyTextModel);
            }
        };

        public void bind(int position, RubyTextModel rubyTextModel) {
            DLog.d(TAG, "bind - position=" + position + " - isShowMeaning=" + rubyTextModel.isShowMeaning());
            this.currentPosition = position;
            tvContent.resetText();
            tvContent.setJText(rubyTextModel.getContent());
        }
    }
}
