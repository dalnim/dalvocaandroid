package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dalread.adapter.WorkbooksAdapterCommon;
import com.dalread.base.EnumLanguage;
import com.dalread.databinding.ActivityWorkbooksBaseBinding;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.VocaBook;
import com.dalread.util.Constant;
import com.dalread.util.UserUtil;

import java.util.List;

public class WorkbookListActivity extends BaseConvActivity {
    private Context context;
    private VocaBook category;
    protected List<VocaBook> vocaBookList;
    protected WorkbooksAdapterCommon adapter;
    private ActivityWorkbooksBaseBinding binding;
    private String bookTitle;

    public static Intent createIntentByExpressionAndBookId(Context context, int expressionBookId, int bookId, String bookTitle) {
        Intent intent = new Intent(context, WorkbookListActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, bookId);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_EXPRESSION_BOOK_ID, expressionBookId);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_NAME, bookTitle);
        return intent;
    }

    @Override
    protected View getContentView() {
        binding = ActivityWorkbooksBaseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected int getContentViewId() {
        return 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
        bindData();
//        initData();
//        initLayout();
////        getData(category.getId());
    }

    private void bindData() {
        adapter.setBooks(vocaBookList);
        adapter.notifyDataSetChanged();
    }
    private void initToolbar() {
        // toolbar
        binding.header.setTitle(bookTitle);
        binding.header.getTvRight().setVisibility(View.GONE);
    }
    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }
    @Override
    public void onHeaderLeft2Click() {
    }
    @Override
    public void onHeaderRightClick() {

    }

    @Override
    public void onHeaderIconRightClick() {

    }

    @Override
    public void onHeaderTextRightClick() {

    }

    @Override
    protected void initData() {
        super.initData();
        context = this;
    }

    @Override
    protected void initLayout() {
        // recycler view
        binding.rvBooks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WorkbooksAdapterCommon(this, onDoubleClickListener);
        adapter.setShowCount(false);
        binding.rvBooks.setAdapter(adapter);
    }

    @Override
    protected void getData() {
        int bookId = getIntent().getIntExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, -1);
        int expressionBookId = getIntent().getIntExtra(Constant.BUNDLE.KEY_VOCA_EXPRESSION_BOOK_ID, -1);
        bookTitle = getIntent().getStringExtra(Constant.BUNDLE.KEY_VOCA_BOOK_NAME);
        vocaBookList = subDatabase.getServerVocaBookListByCategory(bookId);

        VocaBook expressionBook = subDatabase.getServerVocaBookListById(expressionBookId);
        if ((expressionBook != null) && (vocaBookList != null))
            vocaBookList.add(0, expressionBook);
    }

    private OnDoubleClickListener onDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object data) {
            VocaBook vocaBook = (VocaBook) data;
            if (vocaBook.isIBookHasSubList()) {
                openChildBook(vocaBook);
//                vocaBookList = activity.subDatabase.getServerVocaBookListByCategory((int) item.getIBookId());
//                bindData();
            } else {
                checkToOpenVocaListView(vocaBook);
            }

        }

        @Override
        public void onDoubleClick(View view, Object data) {

        }
    };
    private void openChildBook(VocaBook parentBook) {
        openNewScreen(
                WorkbookListActivity.createIntentByExpressionAndBookId(this, -1,parentBook.getId(), parentBook.getIBookName(EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage())))
        );
    }
    private void checkToOpenVocaListView(VocaBook vocaBook) {
        if (vocaBook.getIBookUsed() == Constant.VOCABOOKS.USED.USE_FOR_FREE) {
            openVocaListView(vocaBook);
        } else {
            if (UserUtil.isLoggedIn(this, true)) {
                openVocaListView(vocaBook);
            }
        }
    }
    private void openVocaListView(VocaBook vocaBook) {
        openNewScreen(
                ConvVocaListActivity.createIntentByBookId(this, Integer.parseInt(String.valueOf(vocaBook.getIBookId())), true)
        );
    }
}
