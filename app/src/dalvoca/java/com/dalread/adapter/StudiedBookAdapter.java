package com.dalread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnClickListener;
import com.dalread.model.VocaBookInChat;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StudiedBookAdapter extends RecyclerView.Adapter {

    private ArrayList<VocaBookInChat> books;
    private OnClickListener listener;

    public StudiedBookAdapter(ArrayList<VocaBookInChat> books, OnClickListener listener) {
        this.books = books;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_studied_book, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind(books.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void notifyItemChanged(VocaBookInChat book) {
        int pos = books.indexOf(book);
        if (pos > -1) {
            notifyItemChanged(pos);
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_book_name)
        TextView tvBookName;
        @BindView(R.id.ic_check)
        ImageView icCheck;

        private VocaBookInChat book;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(VocaBookInChat book) {
            this.book = book;

            tvBookName.setText(book.getBookName());
            icCheck.setVisibility(book.isChecked() ? View.VISIBLE : View.INVISIBLE);
        }

        @OnClick(R.id.v_item)
        void onClick() {
            if (listener != null) {
                listener.onClick(itemView, book);
            }
        }
    }
}
