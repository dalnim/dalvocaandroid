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
import com.dalread.model.VOCABOOKS_HANJA;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;
//이건 홈 화면의 단어장 메뉴를 누르면 나온다.
@SuppressLint("NonConstantResourceId")
public class WorkbooksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<VOCABOOKS_HANJA> books = new ArrayList<>();
    private OnClickListener onClickListener;
    private Context context;
    public WorkbooksAdapter(Context context) {
        this.context = context;
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

    public void setBooks(List<VOCABOOKS_HANJA> books) {
        this.books.clear();
        this.books.addAll(books);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    class WorkbookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private VOCABOOKS_HANJA book;
        private ItemVocaBooksBinding binding;
        WorkbookHolder(ItemVocaBooksBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.vItem.setOnClickListener(this);
        }

        public void bind(VOCABOOKS_HANJA book) {
            this.book = book;

            showIndex();
            showBookName();
            showInAppBuyIcon();

//            itemView.setBackgroundColor(0);
            showCount();
        }

        private void showIndex() {
            String strIndex = String.valueOf(getAbsoluteAdapterPosition() + 1);
            binding.tvIndex.setText(strIndex);
        }
        private void showBookName() {
            String strName = book.getName(context);
            binding.tvName.setText(strName);
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

        private void showCount() {
            if (Utils.isDebugOrAdminUser(context)) {
                long vocaCount = book.getVOCA_COUNT();
                if (vocaCount > 0) {
                    String strVocaCount = String.format(context.getString(R.string.tpl_wb_word_count), book.getVOCA_COUNT().toString());
                    Long vocaKnowCount = book.getCOUNT_OF_VOCA_KNOW();
                    if ((vocaKnowCount == null) || (vocaKnowCount == 0)) {
                        binding.tvCount.setText(strVocaCount);
                    } else {
                        binding.tvCount.setText(book.getCOUNT_OF_VOCA_KNOW() + "/" + strVocaCount);
                        if (vocaKnowCount == vocaCount)
                            itemView.setBackgroundResource(R.color.colorAppTint);
                    }
                    binding.tvCount.setVisibility(View.VISIBLE);
                } else {
                    binding.tvCount.setVisibility(View.GONE);
                }
            } else {
                binding.tvCount.setVisibility(View.GONE);
            }
        }

        public void onClick(View view) {
            if (onClickListener != null) {
                onClickListener.onClick(view, book);
            }
        }
    }
}
