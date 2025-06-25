package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dalread.R;
import com.dalread.adapter.WorkbooksAdapterCommon;
import com.dalread.databinding.ActivityWorkbooksBaseBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.interfaces.IVocabooksCommon;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.VocaBook;

import java.util.ArrayList;
import java.util.List;

public class WorkbookListToPracticeActivity extends BaseConvActivity {
    private Context context;
    private AlertDialog alertDialog;
    private VocaBook category;
    protected List<VocaBook> vocaBookList;
    protected WorkbooksAdapterCommon adapter;
    private ActivityWorkbooksBaseBinding binding;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, WorkbookListToPracticeActivity.class);
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
        bindData();
//        initData();
//        initLayout();
////        getData(category.getId());
    }

    private void bindData() {
        adapter.setBooks(vocaBookList);
        adapter.notifyDataSetChanged();
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
        // toolbar
        binding.header.setTitle(R.string.practice);
        binding.header.getTvRight().setVisibility(View.GONE);

        // recycler view
        binding.rvBooks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WorkbooksAdapterCommon(this, onDoubleClickListener);
        adapter.setShowCount(false);
        binding.rvBooks.setAdapter(adapter);
        // others
        alertDialog = new AlertDialog(context);
    }

    @Override
    protected void getData() {
        int bookId = 172; //Greeting book list's parent ID
        vocaBookList = new ArrayList<>();
        vocaBookList.addAll(subDatabase.getServerVocaBookListByKPopForHangulWriting());
        vocaBookList.addAll(subDatabase.getServerVocaBookListByCategoryForHangulWriting(bookId));
    }

    private OnDoubleClickListener onDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object data) {
            IVocabooksCommon iVocabooksCommon = (IVocabooksCommon) data;
            openStudyWritingScreen(iVocabooksCommon);
        }

        @Override
        public void onDoubleClick(View view, Object data) {

        }
    };

    protected void openStudyWritingScreen(IVocabooksCommon iVocabooksCommon) {
        openNewScreen(
                AraConvPracticeTypingHangulActivity.createIntentByBookId(this, (int) iVocabooksCommon.getIBookId())
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Get the current fragment being displayed
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        // If the current fragment is the WorkbookListToPracticeFragment,
        // navigate back to the previous fragment (i.e. mainFrMenuFragmentHangulWriting)
        if (currentFragment instanceof MenuFragmentHangulTyping) {
            getSupportFragmentManager().popBackStack();
        } else if (currentFragment instanceof MenuFragmentHangulWriting) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}
