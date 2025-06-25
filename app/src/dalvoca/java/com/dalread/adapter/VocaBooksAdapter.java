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

public class VocaBooksAdapter extends RecyclerView.Adapter {

    private static final int TYPE_USER_HEADER = 0;
    private static final int TYPE_USER_ITEM = TYPE_USER_HEADER + 1;
    private static final int TYPE_SERVER_HEADER = TYPE_USER_ITEM + 1;
    private static final int TYPE_SERVER_ITEM = TYPE_SERVER_HEADER + 1;

    private List<VocaBook> userBooks;
    private List<VocaBook> serverBooks;
    private OnWordbookClickListener listener;
    private boolean selecting;

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_USER_HEADER;
        if (position - 1 < userBooks.size())
            return TYPE_USER_ITEM;
        if (position - 1 == userBooks.size())
            return TYPE_SERVER_HEADER;
        return TYPE_SERVER_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_USER_HEADER || viewType == TYPE_SERVER_HEADER)
            return new HeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_voca_books, parent, false));
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voca_books, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).bind(viewType == TYPE_USER_HEADER, selecting, listener);
        } else if (holder instanceof ItemHolder) {
            if (viewType == TYPE_USER_ITEM) {
                ((ItemHolder) holder).bind(userBooks.get(position - 1), listener);
            } else {
                ((ItemHolder) holder).bind(serverBooks.get(position - 2 - (userBooks == null ? 0 : userBooks.size())), listener);
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = 1;
        if (userBooks != null && !userBooks.isEmpty()) {
            count += userBooks.size();
        }
        if (serverBooks != null && !serverBooks.isEmpty()) {
            count += 1 + serverBooks.size();
        }
        return count;
    }

    public void setData(List<VocaBook> userBooks, List<VocaBook> serverBooks) {
        this.userBooks = userBooks;
        this.serverBooks = serverBooks;
    }

    public void setListener(OnWordbookClickListener listener) {
        this.listener = listener;
    }

    public boolean isSwipeable(int position) {
        return getItemViewType(position) == TYPE_USER_ITEM;
    }

    public VocaBook getUserBook(int position) {
        int type = getItemViewType(position);
        return type == TYPE_USER_ITEM ? userBooks.get(position - 1) : null;
    }

    public void setSelecting(boolean isAddMode) {
        this.selecting = isAddMode;
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

        public void bind(boolean isLocal, boolean selecting, OnWordbookClickListener listener) {
            this.listener = listener;

            tvType.setText(isLocal ? R.string.user_wordbooks : R.string.server_wordbooks);
            icAdd.setVisibility(isLocal && !selecting ? View.VISIBLE : View.INVISIBLE);
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
