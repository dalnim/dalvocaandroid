package com.dalread.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.databinding.ItemVocaBooksCommonBinding;
import com.dalread.interfaces.IVocabooksCommon;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.Constant;

import java.util.ArrayList;
import java.util.List;
//Dalnim, Is this needed in AraHanja?
@SuppressLint("NonConstantResourceId")
public class WorkbooksAdapterCommon extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<IVocabooksCommon> books = new ArrayList<>();
    private OnDoubleClickListener onDoubleClickListener;
    private Context context;
    private boolean isShowCount;
    public WorkbooksAdapterCommon(Context context, OnDoubleClickListener onDoubleClickListener) {
        this.context = context;
        this.onDoubleClickListener = onDoubleClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkbookHolder(ItemVocaBooksCommonBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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

    public void setShowCount(boolean showCount) {
        isShowCount = showCount;
    }

    class WorkbookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private IVocabooksCommon book;
        private ItemVocaBooksCommonBinding binding;
        WorkbookHolder(ItemVocaBooksCommonBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.vItem.setOnClickListener(this);
        }

        public void bind(IVocabooksCommon book) {
            this.book = book;

            showIndex();
            showBookTitle();
            showInAppBuyIcon();

//            itemView.setBackgroundColor(0);
            showCount();
        }

        private void showIndex() {
            String strIndex = String.valueOf(getAbsoluteAdapterPosition() + 1);
            binding.tvIndex.setText(strIndex);
        }
        private void showBookTitle() {
            String bookTitleStudyLang = book.getIBookNameStudyLang();
            binding.tvTitleStudyLang.setText(bookTitleStudyLang);
            String bookTitleMotherTongue = book.getIBookName(EnumLanguage.getMotherTongueLanguage(context));
            if (!bookTitleMotherTongue.equals(bookTitleStudyLang) && !bookTitleMotherTongue.equals("")) {
                binding.tvTitleMotherTongue.setText(bookTitleMotherTongue);
            }
        }
        private void showInAppBuyIcon() {
            if (book.getIBookUsed() == Constant.VOCABOOKS.USED.USE_FOR_FREE) {
                binding.ivFreeBookCover.setVisibility(View.VISIBLE);
                binding.ivPaidBookCover.setVisibility(View.GONE);
            } else {
                binding.ivFreeBookCover.setVisibility(View.GONE);
                binding.ivPaidBookCover.setVisibility(View.VISIBLE);
//                binding.ivPaidBookCover.setBackgroundResource(R.color.primaryColor);
//                switch ((int) book.getIBookUsed()) {
//                    case Constant.VOCABOOKS.USED.USE_FOR_LOGIN_USER:
//                        if (UserUtil.isLoggedIn(context)) {
//                            binding.ivFreeBookCover.setVisibility(View.VISIBLE);
//                            binding.ivPaidBookCover.setVisibility(View.GONE);
//                        } else {
//                            binding.ivPaidBookCover.setText(R.string.inapp_buy_book_login_free);
//                            binding.ivPaidBookCover.setBackgroundResource(R.color.backgroundGrayColor);
//                        }
//                        break;
//                    case Constant.VOCABOOKS.USED.USE_FOR_BUY:
//                        //TODO : Change text to BUY later.
//                        binding.ivPaidBookCover.setText(R.string.inapp_buy_book_temp_free);
//                        break;
//                    default:
//                        binding.ivPaidBookCover.setText("   ");
//                        break;
//                }
            }
        }

        private void showCount() {
            if (!isShowCount)
                return;

            long vocaCount = book.getIBookVocaCount();
            if (vocaCount > 0) {
                String strVocaCount = String.format(context.getString(R.string.tpl_wb_item_count), String.valueOf(book.getIBookVocaCount()));
                Long vocaKnowCount = book.getIBookCountOfVocaKnow();

                if ((vocaKnowCount == null) || (vocaKnowCount == 0)) {
                    binding.tvCount.setText(strVocaCount);
                } else {
                    binding.tvCount.setText(book.getIBookCountOfVocaKnow() + "/" + strVocaCount);
                    if (vocaKnowCount == vocaCount)
                        itemView.setBackgroundResource(R.color.colorAppTint);
                }
                Long vocaRecordedCount = book.getIBookRecordedVocaCount();
                if (vocaRecordedCount > 0) {
                    binding.tvCount.setText(context.getString(R.string.tpl_wb_recorded_word_count, String.valueOf(vocaRecordedCount), String.valueOf(vocaRecordedCount)));
//                        binding.tvCount.setText(binding.tvCount.getText() + " (" + context.getString(R.string.tpl_total_recorded_word_count,vocaRecordedCount) + ")");
                }
                binding.tvCount.setVisibility(View.VISIBLE);
            } else {
                binding.tvCount.setVisibility(View.GONE);
            }
        }

        public void onClick(View view) {
            if (onDoubleClickListener != null) {
                onDoubleClickListener.onClick(view, book);
            }
        }
    }
}
