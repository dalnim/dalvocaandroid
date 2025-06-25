package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.databinding.ItemUserVocaBookLocalBinding;
import com.dalread.interfaces.IVocabooksCommon;
import com.dalread.listener.OnDoubleClickListener;

import java.util.ArrayList;
import java.util.List;

public class WorkbookListUserVocaBookLocalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<IVocabooksCommon> books = new ArrayList<>();
    private OnDoubleClickListener onDoubleClickListener;
    private Context context;
    public WorkbookListUserVocaBookLocalAdapter(Context context, OnDoubleClickListener onDoubleClickListener) {
        this.context = context;
        this.onDoubleClickListener = onDoubleClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkbookHolder(ItemUserVocaBookLocalBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WorkbookHolder) {
            ((WorkbookHolder) holder).bind(books.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void setBooks(List<? extends IVocabooksCommon> books) {
        this.books.clear();
        this.books.addAll(books);
    }

    class WorkbookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private IVocabooksCommon book;
        private ItemUserVocaBookLocalBinding binding;
        WorkbookHolder(ItemUserVocaBookLocalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.ivDelete.setOnClickListener(this);
            binding.ivInformation.setOnClickListener(this);
            binding.vItem.setOnClickListener(this);
        }

        public void bind(IVocabooksCommon book) {
            this.book = book;
            showBookTitle();
        }

        private void showBookTitle() {
            binding.tvTitle.setText(book.getIBookName(null));
        }

        public void onClick(View view) {
            if (onDoubleClickListener != null) {
                onDoubleClickListener.onClick(view, book);
            }
        }
    }
}
