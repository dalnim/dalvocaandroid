package com.dalread.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemVocaBooksBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.VOCABOOKS_HANJA_CLASSICS;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

//이건 한자 고전 리스트를 표시한다.
@SuppressLint("NonConstantResourceId")
public class ClassicsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<VOCABOOKS_HANJA_CLASSICS> books = new ArrayList<>();
    private OnClickListener listener;
    private Context context;

    public ClassicsAdapter(Context context, OnClickListener listener) {
        this.context = context;
        this.listener = listener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkbookHolder(ItemVocaBooksBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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

    public void setBooks(List<VOCABOOKS_HANJA_CLASSICS> books) {
        this.books.clear();
        this.books.addAll(books);
    }

    class WorkbookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private VOCABOOKS_HANJA_CLASSICS book;
        private ItemVocaBooksBinding binding;
        WorkbookHolder(ItemVocaBooksBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.vItem.setOnClickListener(this);
            binding.ivInformation.setOnClickListener(this);
        }

        public void bind(VOCABOOKS_HANJA_CLASSICS book) {
            this.book = book;

            showIndex();
            showInAppBuyIcon();
            showBookName();
            showInformationIcon();
            showCount();
        }

        private void showInformationIcon() {
            binding.ivInformation.setVisibility(Utils.isEmpty(getBookInfo()) ? View.GONE : View.VISIBLE);
        }
        private String getBookInfo() {
            return book.getNameDetailed(context);
        }

        private void showIndex() {
            String strIndex = String.valueOf(getAbsoluteAdapterPosition() + 1);
            binding.tvIndex.setText(strIndex);
        }

        private void showCount() {
            long count = book.getVOCA_COUNT();
            if (count > 0) {
                String strCount = String.format(context.getString(R.string.tpl_wb_word_count), book.getVOCA_COUNT().toString());
                binding.tvCount.setText(strCount);
                binding.tvCount.setVisibility(View.VISIBLE);
            } else {
                binding.tvCount.setVisibility(View.GONE);
            }
        }

        private void showBookName() {
            String strHanjaName = book.getHANJA_KO();
            String strName = book.getName(context);
            if (Utils.isEmpty(strHanjaName) || strHanjaName.equals(strName)) {
                binding.tvName.setText(strName);
            } else {
                binding.tvName.setText(strName + "(" + strHanjaName + ")");
            }
        }

        private void showInAppBuyIcon() {
            if (book.getUSED() == Constant.VOCABOOKS.USED.USE_FOR_FREE) {
                binding.vBookCoverImage.setVisibility(View.VISIBLE);
                binding.tvInAppBuyBook.setVisibility(View.GONE);
            } else {
                binding.vBookCoverImage.setVisibility(View.GONE);
                binding.tvInAppBuyBook.setVisibility(View.VISIBLE);
            }
        }

//        @OnClick({R.id.v_item})
        public void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, book);
            }
        }
    }
}
