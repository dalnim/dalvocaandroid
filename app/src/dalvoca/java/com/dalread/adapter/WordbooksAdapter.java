package com.dalread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnWordbookClickListener;
import com.dalread.model.VocaBook;
import com.dalread.model.VocaBookNativeSpeaker;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WordbooksAdapter extends RecyclerView.Adapter {

    private boolean selectMode;
    private List<VocaBook> serverBooks;
    private OnWordbookClickListener listener;

    public WordbooksAdapter() {
    }

    public WordbooksAdapter(boolean selectMode) {
        this.selectMode = selectMode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voca_books, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ItemHolder) holder).bind(selectMode, serverBooks.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return serverBooks == null ? 0 : serverBooks.size();
    }

    public int notifyItemChanged(VocaBook book) {
        int pos = -1;
        if (serverBooks != null) {
            notifyItemChanged(pos = serverBooks.indexOf(book));
        }
        return pos;
    }

    public void setData(List<VocaBook> serverBooks) {
        this.serverBooks = serverBooks;
    }

    public void setListener(OnWordbookClickListener listener) {
        this.listener = listener;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.v_foreground)
        View vForeground;
        @BindView(R.id.tv_index)
        TextView tvIndex;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_count)
        TextView tvCount;
        @BindView(R.id.ic_check)
        ImageView icCheck;

        @BindString(R.string.tpl_wb_word_count)
        String tplWordCount;

        private VocaBook book;
        private OnWordbookClickListener listener;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(boolean selectMode, VocaBook vocaBook, OnWordbookClickListener listener) {
            book = vocaBook;
            this.listener = listener;

            if (book instanceof VocaBookNativeSpeaker
                    && Math.abs(((VocaBookNativeSpeaker) book).getVocaRecordedTotal() - book.getWordCount()) <= 2) {
                vForeground.setBackgroundResource(R.color.colorBackgroundSignUp);
            } else if (book.isParentChecked()) {
                vForeground.setBackgroundResource(R.color.color_blink);
            } else {
                vForeground.setBackgroundResource(R.color.colorBackground);
            }

            String text = String.valueOf(book.getIndex());
            tvIndex.setText(text);
            text = book.getName();
            tvName.setText(text);
            if (book.getWordCount() > 0) {
                text = String.format(tplWordCount, book.getWordCount());
                if (book instanceof VocaBookNativeSpeaker) {
                    text = ((VocaBookNativeSpeaker) book).getVocaRecordedTotal() + "/" + text;
                }
                tvCount.setText(text);
                tvCount.setVisibility(View.VISIBLE);
            } else {
                tvCount.setText("");
                tvCount.setVisibility(View.GONE);
            }

            if (selectMode) {
                icCheck.setImageResource(R.drawable.ic_check_box_black_24dp);
                icCheck.setVisibility(book.isChecked() ? View.VISIBLE : View.GONE);
            } else {
                icCheck.setImageResource(R.drawable.ic_keyboard_arrow_right_36dp);
            }
        }

        @OnClick({R.id.v_foreground})
        void onClick(View view) {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.v_foreground:
                    if (listener != null) {
                        listener.onDetailsClick(book);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
