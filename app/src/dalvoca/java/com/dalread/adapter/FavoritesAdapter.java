package com.dalread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnWordbookClickListener;
import com.dalread.model.VocaBook;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FavoritesAdapter extends RecyclerView.Adapter {

    private static final int TYPE_USER_HEADER = 0;
    private static final int TYPE_USER_ITEM = TYPE_USER_HEADER + 1;

    private List<VocaBook> userBooks;
    private OnWordbookClickListener listener;

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_USER_HEADER;
        return TYPE_USER_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_USER_HEADER)
            return new HeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_voca_books, parent, false));
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voca_books, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).bind(listener);
        } else if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind(userBooks.get(position - 1), listener);
        }
    }

    @Override
    public int getItemCount() {
        return 1 + (userBooks == null ? 0 : userBooks.size());
    }

    public void setData(List<VocaBook> userBooks) {
        this.userBooks = userBooks;
    }

    public void setListener(OnWordbookClickListener listener) {
        this.listener = listener;
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_type)
        TextView tvType;
        @BindView(R.id.ic_add)
        View icAdd;

        private OnWordbookClickListener listener;

        public HeaderHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(OnWordbookClickListener listener) {
            this.listener = listener;

            tvType.setText(R.string.nav_title_favorites);
            icAdd.setVisibility(View.VISIBLE);
        }

        @OnClick({R.id.ic_add})
        void onClick() {
            if (listener != null) {
                listener.onAddClick();
            }
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_count)
        TextView tvCount;

        @BindString(R.string.tpl_wb_word_count)
        String tplWordCount;

        private VocaBook book;
        private OnWordbookClickListener listener;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(VocaBook vocaBook, OnWordbookClickListener listener) {
            book = vocaBook;
            this.listener = listener;

            String text = book.getName();
            tvName.setText(text);
            text = String.format(tplWordCount, book.getWordCount());
            tvCount.setText(text);
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
