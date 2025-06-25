package com.dalread.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.component.FuriganaView;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.ItemContentSearchBinding;
import com.dalread.databinding.ItemVocaBookContentBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.DIC_HANJA_BOOK;
import com.dalread.model.RubyForBook;
import com.dalread.util.Constant;
import com.dalread.util.HanjaVoca;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.List;

//이건 어디에 쓰는거지?
@SuppressLint("NonConstantResourceId")
public class VocaHanjaBookContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SEARCH = 0;
    private static final int TYPE_CONTENT = TYPE_SEARCH + 1;

//    private List<HanjaItem> hanjaItems = new ArrayList<>();
    private List<DIC_HANJA_BOOK> hanjaItems = new ArrayList<>();
    private OnClickListener listener;
    private FuriganaView.OnTextSelectedListener rubyWordClickListener;
    private SharedPreferencesDB sharedPreferences;
    private int isShowFurigana = Constant.SHOW_FURIGANA_DIFFICULT_WORDS_ONLY;
    private Context context;
    private boolean isFirstTime;
    private long bookUsedType;

    public VocaHanjaBookContentAdapter(Context context, long bookUsedType, OnClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.bookUsedType = bookUsedType;
        isFirstTime = true;
        sharedPreferences = SharedPreferencesDB.getInstance(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_SEARCH;
        }
        return TYPE_CONTENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_SEARCH:
                viewHolder = new ContentSearchHolder(ItemContentSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
                break;
            case TYPE_CONTENT:
                viewHolder = new HanjaBookContentHolder(ItemVocaBookContentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
                break;
        }
        return viewHolder ;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_SEARCH:
                ((ContentSearchHolder) holder).bind(position);
                break;
            case TYPE_CONTENT:
                ((HanjaBookContentHolder) holder).bind(hanjaItems.get(position - 1), position);
                break;
        }
//        ((HanjaBookContentHolder) holder).bind(hanjaItems.get(position), position);
    }

    @Override
    public int getItemCount() {
        return hanjaItems.size() + 1;
    }

    public void setData(List<DIC_HANJA_BOOK> hanjaItems) {
        this.hanjaItems.clear();;
        this.hanjaItems = hanjaItems;
        notifyDataSetChanged();
    }

    public void setOnRubyWordClickListener(FuriganaView.OnTextSelectedListener rubyWordClickListener) {
        this.rubyWordClickListener = rubyWordClickListener;
    }

    public void switchShowFurigana() {
        if (getIsShowFurigana() == Constant.SHOW_FURIGANA_DIFFICULT_WORDS_ONLY) {
            isShowFurigana = Constant.SHOW_FURIGANA_ALL;
        } else {
            isShowFurigana = Constant.SHOW_FURIGANA_DIFFICULT_WORDS_ONLY;
        }
    }

    public int getIsShowFurigana() {
        return sharedPreferences.getShowHuriganaForHanja();
    }

    class ContentSearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemContentSearchBinding binding;
        ContentSearchHolder(ItemContentSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
//            ButterKnife.bind(this, binding.getRoot());
            setOnClickListeners();
            if (isFirstTime == true) {
                binding.btnResetSearch.setVisibility(View.GONE);
            }
            isFirstTime = false;
        }

        private void setOnClickListeners() {
            binding.btnResetSearch.setOnClickListener(this);
            binding.etSearchKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    boolean result = false;
                    if (i == EditorInfo.IME_ACTION_SEARCH) {
                        String strKeyword = binding.etSearchKeyword.getText().toString().trim();
                        binding.btnResetSearch.setVisibility(Utils.isEmpty(strKeyword) ? View.GONE : View.VISIBLE);
                        listener.onClick(textView, strKeyword);
                        Utils.hideSoftKeyboard(context, binding.etSearchKeyword);
                        result = true;
                    }
                    return result;
                }
            });
        }


        void bind(int position) {

        }

        @Override
        public void onClick(View view) {
            if ((view.getId() == R.id.btnResetSearch)) {
                binding.btnResetSearch.setVisibility(View.GONE);
                binding.etSearchKeyword.setText("");
            }
            String strKeyword = binding.etSearchKeyword.getText().toString().trim();
            listener.onClick(view, strKeyword);
        }
    }

    class HanjaBookContentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private DIC_HANJA_BOOK hanjaItem;

        private ItemVocaBookContentBinding binding;
        HanjaBookContentHolder(ItemVocaBookContentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
//            ButterKnife.bind(this, binding.getRoot());
            setOnClickListeners();
            binding.tvBookHanjaFurigana.setOnTextSelectedListener(rubyWordClickListener);
            binding.tvBookUnknownWordInfoFurigana.setOnTextSelectedListener(rubyWordClickListener);
            binding.tvBookMeaningDetailedFurigana.setOnTextSelectedListener(rubyWordClickListener);
            binding.tvBookMeaningFurigana.setOnTextSelectedListener(rubyWordClickListener);
        }

        private void setOnClickListeners() {
            binding.ivBookmark.setOnClickListener(this);
            binding.vItem.setOnClickListener(this);
            binding.tvBookMeaning.setOnClickListener(this);
            binding.ivUnknownWordList.setOnClickListener(this);
            binding.llBookMeaning.setOnClickListener(this);
            binding.ivEditView.setOnClickListener(this);
            binding.ivCopy.setOnClickListener(this);
            binding.ivWebSearch.setOnClickListener(this);
        }

        void bind(DIC_HANJA_BOOK hanjaItem, int position) {
            this.hanjaItem = hanjaItem;

            List<String> listRubyTextAndUnknownWord = Voca.getRubyTextAndUnknownWord(hanjaItem.getHI_VOCA(), hanjaItem.getHI_PRONOUNCE1_FIRST());
            List<String> listRubyTextDifficultWords = Voca.getRubyTextAndUnknownWord(listRubyTextAndUnknownWord.get(1),"");
            List<String> listRubyTextForMeaningDetailed = Voca.getRubyTextAndUnknownWord(hanjaItem.getMeaningDetailed(context),"");
            List<String> listRubyTextForMeaning = Voca.getRubyTextAndUnknownWord(hanjaItem.getMeaning(context),"");
            RubyForBook rubyForBook = new RubyForBook(listRubyTextAndUnknownWord.get(0), listRubyTextDifficultWords.get(0), listRubyTextForMeaning.get(0), listRubyTextForMeaningDetailed.get(0));
            updateLayout(rubyForBook, position);
        }

        private void updateLayout(RubyForBook rubyForBook, int position) {
//            if (hanjaItem == null)
//                return;
//            runOnUiThread(() -> {
            binding.ivBookmark.setImageResource(hanjaItem.getHI_BOOKMARK() == 0 ? R.drawable.ic_favorite_new_off : R.drawable.ic_favorite_new_on);
            binding.llBookMeaning.setVisibility(View.VISIBLE);
            binding.tvIndex.setText(hanjaItem.getHI_INDEX().toString());

            binding.tvBookHanjaFurigana.setFuriganViewForBookVoca(rubyForBook.getVoca(), getIsShowFurigana());
            hideMenusOnReleaseMode();
            updateDifficultWordsInfo(rubyForBook);
            updateMeaning(rubyForBook);
            updateMeaningDetailed(rubyForBook);
//            });
        }

        private void updateDifficultWordsInfo(RubyForBook rubyForBook) {
            binding.tvBookUnknownWordInfoFurigana.setFuriganViewForDifficultWords(rubyForBook.getDifficultWords());
        }

        private void updateMeaning(RubyForBook rubyForBook) {

//            if (Utils.isDebugOrAdminUser(context) || HanjaVoca.isBookUsedType_Free(bookUsedType)) {
            if (UserUtil.isLoggedIn(context, false) || HanjaVoca.isBookUsedType_Free(bookUsedType)) {
                binding.tvBookMeaningFurigana.setFuriganViewForMeaning(rubyForBook.getMeaning());
                binding.tvBookMeaning.setVisibility(View.GONE);
            } else {
                String meaning = hanjaItem.getMeaning(context);
                binding.tvBookMeaning.setVisibility(View.VISIBLE);
                binding.tvBookMeaning.setText(meaning);
//                binding.tvBookMeaning.setText(BlurTextUtil.getBlurText(meaning));
                binding.tvBookMeaning.setTextIsSelectable(false);
            }
        }

        private void updateMeaningDetailed(RubyForBook rubyForBook) {
            binding.tvBookMeaningDetailedFurigana.setFuriganViewForMeaning(rubyForBook.getMeaningDetailed());
//            if (Utils.isDebugOrAdminUser(context)) {
//                binding.tvBookMeaningDetailedFurigana.setFuriganViewForMeaning(rubyForBook.getMeaningDetailed());
//                //Don't delete this. tvBookMeaningDetailedFurigana의 줄바꿈이 아래처럼 자연스럽지 못하다. (단어사이에 줄바꿈이 일어난다.)
////                binding.tvBookMeaningDetailed.setText(hanjaItem.getMeaningDetailed(context));
//            } else {
//                binding.tvBookMeaningDetailed.setText("");
//            }
            //Don't Delete this
//            if (UserUtil.isNormalUser(context)) {
//                binding.tvBookMeaningDetailed.setText("");
//            } else {
//                binding.tvBookMeaningDetailed.setText(hanjaItem.getMeaningDetailed(context));
//            }
        }


        private void hideMenusOnReleaseMode() {
            if (UserUtil.isEditContentUser(context)) {
                binding.ivEditView.setVisibility(View.VISIBLE);
            } else {
                binding.ivEditView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_bookmark:
                    if (UserUtil.isLoggedIn(context, true)) {
                        if (hanjaItem.getHI_BOOKMARK() == 0) {
                            hanjaItem.setHI_BOOKMARK((long) 1);
                            binding.ivBookmark.setImageResource(R.drawable.ic_favorite_new_on);
                        } else {
                            hanjaItem.setHI_BOOKMARK((long) 0);
                            binding.ivBookmark.setImageResource(R.drawable.ic_favorite_new_off);
                        }
                        break;
                    } else {
                        return;
                    }
            }
            listener.onClick(view, hanjaItem);
        }
    }
}
