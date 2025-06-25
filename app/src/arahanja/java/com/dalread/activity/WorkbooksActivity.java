package com.dalread.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.WorkbooksAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityWorkbooksBinding;
import com.dalread.dialog.HanjaWorkbooksMenuDialog;
import com.dalread.listener.OnClickListener;
import com.dalread.model.HanjaGroupType;
import com.dalread.model.HanjaGroupTypeModel;
import com.dalread.model.VOCABOOKS_HANJA;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindColor;
import butterknife.BindDimen;
//For Debug mode only
@SuppressLint("NonConstantResourceId")
public class WorkbooksActivity extends BaseHanjaInfoActivity implements OnAsyncTaskListener {
    private VOCABOOKS_HANJA parentBook;
    private List<VOCABOOKS_HANJA> books;
    private Context context;
    private WorkbooksAdapter adapter;
    private List<String> workbookNameWithParent;

    private final int TYPE_INIT_DATA = 0;
    private final int TYPE_SHOW_KNOWN_WORD_COUNT = TYPE_INIT_DATA + 1;
    private final String booknameDelimiter = " - ";
    private ActivityWorkbooksBinding binding;

    protected View getContentView() {
        binding = ActivityWorkbooksBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

//    @Override
//    protected RecyclerView.Adapter<RecyclerView.ViewHolder> getAdapter() {
//        return null;
//    }

    @Override
    protected void getData() {

    }

//    @Override
//    protected List<HanjaQuizItem> getQuizHanjaList() {
//        return null;
//    }

    @Override
    protected RecyclerView getRvSearch() {
        return binding.rvSearch;
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
        openMenuDialog();
    }
    private void openMenuDialog() {
        final HanjaWorkbooksMenuDialog dialog = new HanjaWorkbooksMenuDialog(this, (view, object) -> {
            switch (view.getId()) {
                case R.id.ll_show_known_word_count:
                    callAsyncTask(TYPE_SHOW_KNOWN_WORD_COUNT, null);
                    break;
                case R.id.llBackToHome:
                    backToHome();
                    break;
                default:
                    break;
            }
        });
        dialog.show();
    }

//    private void backToHome() {
//        openNewScreenWithRightToLeftInAnimation(MainHanjaActivity.class);
//        finishAffinity();
//    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addMobileAdsView();
        loadBanner();

        bindData();
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
            BaseEvent.EventType type = successEvent.getEventType();
        }
    }


    protected void initData() {
        super.initData();
        parentBook = (VOCABOOKS_HANJA) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_BOOK);
        setWorkbookNameWithParent(parentBook);
        List<VOCABOOKS_HANJA> booksTemp = Voca.getWorkbooksInfo(parentBook, false, UserUtil.getVocabooksUsedByUserType(this));
        if (Utils.isDebugOrAdminUser(this)) {
            books = booksTemp;
        } else {
            books = booksTemp.stream().filter(e -> e.getUSED() >= Constant.VOCABOOKS.USED.USE_FOR_LOGIN_USER).collect(Collectors.toList());
        }

    }

    private void setWorkbookNameWithParent(VOCABOOKS_HANJA book) {
        if (book != null) {
            if (workbookNameWithParent == null) {
                workbookNameWithParent = new ArrayList<>();
            }
            workbookNameWithParent.add(book.getName(this));
        }
    }

    private String getFinalWorkbookNameWithParent(VOCABOOKS_HANJA book) {
        String result = "";
        if (book != null) {
            if (workbookNameWithParent == null) {
                result = book.getName(this);
            } else {
                result = workbookNameWithParent.stream().collect(Collectors.joining(booknameDelimiter)) + booknameDelimiter + book.getName(context);
            }
        }
        return result;
    }

    protected void initLayout() {
        super.initLayout();
        context = this;
        adapter = new WorkbooksAdapter(this);
        adapter.setOnClickListener(onBookClickListener);
        binding.rvBooks.setAdapter(adapter);
        binding.rvBooks.setLayoutManager(new LinearLayoutManager(context));
        binding.rvBooks.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
    }

    private void bindData() {
        binding.layoutItemContentBookTitle.vItem.setVisibility(View.GONE);
        if (parentBook != null && getToolbar() != null) {
            String bookTitle = parentBook.getName(this);
            if (!Utils.isEmpty(bookTitle)) {
                int maxBookTitleLengthToDisplayInToolBar = 8;
                if (bookTitle.length() < maxBookTitleLengthToDisplayInToolBar) {
                    getToolbar().setTitle(bookTitle);
                } else {
                    binding.layoutItemContentBookTitle.tvBookTitle.setText(bookTitle);
                    binding.layoutItemContentBookTitle.vItem.setVisibility(View.VISIBLE);
                }
            }
//            toolbar.setTitle(parentBook.getNAME_KO());
        }
        if (books != null) {
            adapter.setBooks(books);
            adapter.notifyDataSetChanged();
        }
    }

    private final OnClickListener onBookClickListener = (view, object) -> {
        if (object instanceof VOCABOOKS_HANJA) {
            VOCABOOKS_HANJA book = (VOCABOOKS_HANJA) object;
            if (book.getHAS_SUB_LIST() == 1) {
                if (book.getID() == Constant.HANJA.WORKBOOK_ID.level) {
                    HanjaGroupTypeModel hanjaGroupTypeModel = new HanjaGroupTypeModel(null, HanjaGroupType.VOCA_LEVEL);
                    openNewScreen(
                            StrokesActivity.createIntent(this, hanjaGroupTypeModel)
                    );
                } else if (book.getID() == Constant.HANJA.WORKBOOK_ID.bookIdLevelKoreaType1) {
                    openGroupViewLevelKoreanTypeScreen(HanjaGroupType.LEVEL_KOREA_TYPE_1);
                } else if (book.getID() == Constant.HANJA.WORKBOOK_ID.bookIdLevelKoreaType2) {
                    openGroupViewLevelKoreanTypeScreen(HanjaGroupType.LEVEL_KOREA_TYPE_2);
                } else if (book.getID() == Constant.HANJA.WORKBOOK_ID.bookIdLevelKoreaType3) {
                    openGroupViewLevelKoreanTypeScreen(HanjaGroupType.LEVEL_KOREA_TYPE_3);
                    //Don't delete it.
//                    openNewScreen(
//                            HanjaWordListGroupInfoActivity.createIntent(this, book)
//                    );
                } else {
                    openChildBook(book);
                }
            } else {
                if (book.getID() == Constant.HANJA.WORKBOOK_ID.commonMiddleSchool) {
                    openStrokesScreen(HanjaGroupType.COMMON_MIDDLE_SCHOOL);
                } else if (book.getID() == Constant.HANJA.WORKBOOK_ID.commonHighSchool) {
                    openStrokesScreen(HanjaGroupType.COMMON_HIGH_SCHOOL);
                } else {
                    openNewScreen(
                            HanjaWordListInfoActivity.createIntentByBookId(this, book.getID(), getFinalWorkbookNameWithParent(book))
                    );
                }
            }
        }
    };

    private void openStrokesScreen(HanjaGroupType hanjaGroupType) {
        HanjaGroupTypeModel hanjaGroupTypeModel = new HanjaGroupTypeModel(null, hanjaGroupType);
        openNewScreen(
                StrokesActivity.createIntent(this, hanjaGroupTypeModel)
        );
    }

    private void openGroupViewLevelKoreanTypeScreen(HanjaGroupType hanjaGroupType) {
        int bookId = Constant.HANJA.WORKBOOK_ID.bookIdLevelKoreaType1;
        if (hanjaGroupType == HanjaGroupType.LEVEL_KOREA_TYPE_2) {
            bookId = Constant.HANJA.WORKBOOK_ID.bookIdLevelKoreaType2;
        } else if (hanjaGroupType == HanjaGroupType.LEVEL_KOREA_TYPE_3) {
            bookId = Constant.HANJA.WORKBOOK_ID.bookIdLevelKoreaType3;
        }
        HanjaGroupTypeModel hanjaGroupTypeModel = new HanjaGroupTypeModel(null, hanjaGroupType);
        openNewScreen(
                LevelKoreaTypeActivity.createIntent(this, hanjaGroupTypeModel, bookId, "")
        );
    }

    private void openChildBook(VOCABOOKS_HANJA parentBook) {
        Intent intent = new Intent(context, WorkbooksActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, parentBook);
        openNewScreen(intent);
    }

    private void callAsyncTask(int type, Object data) {
        new CustomAsyncTask(this, this, data, type, true).execute();
    }
    @Override
    public void onInitAsyncTask() {
        Loading.show(this);
    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        switch (searchType) {
            case TYPE_SHOW_KNOWN_WORD_COUNT:
                books = Voca.getWorkbooksInfo(parentBook, true, UserUtil.getVocabooksUsedByUserType(this));
                return null;
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_SHOW_KNOWN_WORD_COUNT:
                bindData();
                break;
        }
        Loading.hide();
    }

    protected void showHanjaSearchResultView() {
        super.showHanjaSearchResultView();
        binding.llMain.setVisibility(View.GONE);
        binding.adViewContainer.setVisibility(View.GONE);
    }

    protected void hideHanjaSearchResultView() {
        super.hideHanjaSearchResultView();
        binding.llMain.setVisibility(View.VISIBLE);
        binding.adViewContainer.setVisibility(View.VISIBLE);
    }
}
