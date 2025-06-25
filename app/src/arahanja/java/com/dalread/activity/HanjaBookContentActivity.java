package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.HanjaBookDetailPagerAdapter;
import com.dalread.adapter.VocaHanjaBookContentAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.component.FuriganaView;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.component.page_curl.PageTransform;
import com.dalread.databinding.ActivityHanjaBookContentBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.BookStyle;
import com.dalread.dialog.HanjaBookMenuDialog;
import com.dalread.helper.HanjaBookContentHelper;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.listener.OnClickListener;
import com.dalread.model.DIC_HANJA;
import com.dalread.model.DIC_HANJA_BOOK;
import com.dalread.model.HanjaItem;
import com.dalread.model.VOCABOOKS_HANJA_CLASSICS;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BannerAdUtil;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.HanjaVoca;
import com.dalread.util.Loading;
import com.dalread.util.StringUtils;
import com.dalread.util.ToastUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.stream.Collectors;

import io.realm.Realm;
import me.zhanghai.android.fastscroll.FastScrollerBuilder;

public class HanjaBookContentActivity extends BaseHanjaInfoActivity implements OnAsyncTaskListener {
    protected AlertDialog alertDialog;
    protected VOCABOOKS_HANJA_CLASSICS vocabooksHanjaClassics;
    protected List<DIC_HANJA_BOOK> mHanjaBookItemList;
    protected DIC_HANJA_BOOK mDicHanjaBook;
    protected Context context;
    protected boolean isOpenEditView_;
    private int bookPageIndex;
    private int heightReading;
    private LinearLayoutManager linearLayoutManager;
    private HanjaBookDetailPagerAdapter hanjaBookDetailPagerAdapter;
    private VocaHanjaBookContentAdapter adapter;
    private HanjaBookContentHelper helper;
    protected final int TYPE_INIT_DATA = 0;
    protected final int TYPE_SEARCH_DATA = TYPE_INIT_DATA + 1;
    protected final int TYPE_REFRESH_VOCA_AND_OPEN_EDIT_VIEW = TYPE_SEARCH_DATA + 1;
//    protected final int TYPE_BACK_FROM_BOOK_DETAIL_VIEW = TYPE_REFRESH_VOCA_AND_OPEN_EDIT_VIEW + 1;
    protected final int TYPE_REFRESH_DATA_FROM_DB = TYPE_REFRESH_VOCA_AND_OPEN_EDIT_VIEW + 1;

    protected boolean isResume;

    private ActivityHanjaBookContentBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityHanjaBookContentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    public static Intent createIntent(Context context, VOCABOOKS_HANJA_CLASSICS vocabooksHanjaClassics) {
        Intent intent = new Intent(context, HanjaBookContentActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, vocabooksHanjaClassics);
        return intent;
    }

    @Override
    protected RecyclerView getRvSearch() {
        return binding.rvSearch;
    }

    @Override
    protected void getData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initData();
        addMobileAdsView();
        loadBanner();
        updateUIForBookStyle();
        saveLastReadBookId();
        callAsyncTask(TYPE_INIT_DATA);
        showToLogInToSeeMeaning();
    }

    private void saveLastReadBookId() {
        sharedPreferences.setLastReadBookId(vocabooksHanjaClassics.getID());
    }

    private void showToLogInToSeeMeaning() {
//        if ((!UserUtil.isLoggedIn(context, false)) && (!HanjaVoca.isBookUsedType_Free(vocabooksHanjaClassics.getUSED()))) {
//            ToastUtil.getInstance(this).show("책 해석부분에 흐림 처리를 없애고 볼려면 로그인하세요.");
//        }
    }
    @Override
    public void onResume() {
        super.onResume();
        udpateShowPronounceEyeIcon();
        if (isResume) {
//            if (isOpenBookDetailView_) {
//                callAsyncTask(TYPE_BACK_FROM_BOOK_DETAIL_VIEW);
//                isOpenBookDetailView_ = false;
//            } else {
                callAsyncTask(TYPE_REFRESH_DATA_FROM_DB);
//            }
        }
    }

    @Override
    protected void onDestroy() {
        unRegisterEventBus();

        super.onDestroy();
    }

    protected void initLayout() {
        super.initLayout();
        context = this;
        new FastScrollerBuilder(binding.rvBooks).build();
        DLog.d(getLogTag(), "initLayout");
        adapter = new VocaHanjaBookContentAdapter(this, vocabooksHanjaClassics.getUSED(), onBookClickListener);
        adapter.setOnRubyWordClickListener(onRubyWordClickListener);
        binding.rvBooks.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(context);
        binding.rvBooks.setLayoutManager(linearLayoutManager);
        binding.rvBooks.addItemDecoration(new SeparatorDecoration(context, BaseBindUtils.getDividerColor(), BaseBindUtils.getDividerHeight(this)));
    }

    protected final OnClickListener onBookClickListener = (view, object) -> {
        if ((view.getId() == R.id.etSearchKeyword)) {
            String keyword = (String) object;
            callAsyncTask(TYPE_SEARCH_DATA, keyword);
        } else if ((view.getId() == R.id.btnResetSearch)) {
            callAsyncTask(TYPE_SEARCH_DATA, "");
        } else {
            mDicHanjaBook = (DIC_HANJA_BOOK) object;
            switch (view.getId()) {
                case R.id.iv_unknown_word_list:
                    openHanjaWordListInfoView(mDicHanjaBook);
                    break;
                case R.id.iv_bookmark:
                    updateBookmarkInDB(mDicHanjaBook);
                    break;
                case R.id.ivEditView:
                    openEditHanjaBookContentScreen(mDicHanjaBook);
                    break;
                case R.id.ivCopy:
                    Voca.openCopyDialog(this, mDicHanjaBook);
                    break;
                case R.id.ivWebSearch:
                    Utils.openWebSearchForHanja(context, mDicHanjaBook.getHI_VOCA());
                    break;
            }
        }
    };

    public void openEditHanjaBookContentScreen(DIC_HANJA_BOOK dicHanjaBook) {
//        if (Utils.isConnected(this)) {
//            isOpenEditView_ = true;
//            HanjaVoca.refreshHanjaBookAndOpenEditView(this, dicHanjaBook, isOpenEditView_);
//        } else {
            openEditHanjaBookScreen(dicHanjaBook);
//        }
    }

    private void openEditHanjaBookScreen(DIC_HANJA_BOOK hanjaItem) {
        openNewScreen(
                EditHanjaBookActivity.createIntent(this, hanjaItem)
        );
    }

    private void updateBookmarkInDB(HanjaItem hanjaItem) {
        //If a user is log in then Need to call API first to update in Server.
        Voca.updateItemInDic(hanjaItem);
    }

    @Override
    public void onHeaderLeftClick() {
//        saveLastReadBookRowNo();
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        saveLastReadBookPageNo();
        super.onBackPressed();
    }

    private void saveLastReadBookPageNo() {
        int lastReadBookPageNoIndex = getLastReadBookPageNo() - 1;
        if (Voca.isIndexInsideList(mHanjaBookItemList, lastReadBookPageNoIndex)) {
            Voca.saveLastReadBookPageNo(vocabooksHanjaClassics.getID(), lastReadBookPageNoIndex);
        }
    }
    protected int getLastReadBookPageNo() {
        if (isBookStylePage()) {
            return bookPageIndex + 1;
        } else {
            return linearLayoutManager.findFirstVisibleItemPosition();
        }

    }
    protected int getLastReadBookPageNoFromDB() {
        Realm realm = BaseVoca.getRealm();
        realm.refresh();
        int lastReadBookPageNo = Voca.getLastReadBookPageNo(realm, vocabooksHanjaClassics.getID());
        return lastReadBookPageNo;
    }

    @Override
    public void onHeaderLeft2Click() {
        if (sharedPreferences.getShowHuriganaForHanja() == Constant.SHOW_FURIGANA_DIFFICULT_WORDS_ONLY) {
            sharedPreferences.setShowHuriganaForHanja(Constant.SHOW_FURIGANA_ALL);
            ToastUtil.getInstance(context).show(R.string.show_ruby_pronounce_for_all_hanja);
        } else {
            sharedPreferences.setShowHuriganaForHanja(Constant.SHOW_FURIGANA_DIFFICULT_WORDS_ONLY);
            ToastUtil.getInstance(context).show(R.string.show_ruby_pronounce_for_difficult_words_hanja);
        }
        handleEyeClick();
        udpateShowPronounceEyeIcon();
    }

    private void udpateShowPronounceEyeIcon() {
        if (sharedPreferences.getShowHuriganaForHanja() == Constant.SHOW_FURIGANA_DIFFICULT_WORDS_ONLY) {
            getToolbar().setIconLeft2(R.drawable.ic_new_eye_close2);
        } else {
            getToolbar().setIconLeft2(R.drawable.ic_new_eye_open2);
        }
    }

    @Override
    public void onHeaderRightClick() {

    }

    @Override
    public void onHeaderIconRightClick() {

    }

    @Override
    public void onHeaderTextRightClick() {
        openHanjaBookMenuDialog();
    }


    protected void initData() {
        super.initData();
        vocabooksHanjaClassics = (VOCABOOKS_HANJA_CLASSICS) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_BOOK);
        helper = new HanjaBookContentHelper(vocabooksHanjaClassics, sharedPreferences);
        getToolbar().setTitle(vocabooksHanjaClassics.getName(this));
//        isOpenBookDetailView_ = false;
    }

    private void callAsyncTask(int type) {
        new CustomAsyncTask(this, this, null, type, true).execute();
    }

    private void callAsyncTask(int type, String string) {
        new CustomAsyncTask(this, this, string, type, true).execute();
    }


    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        switch (successEvent.getScreen()) {
            case EDIT_HANJA_BOOK:
                switch (successEvent.getEventType()) {
                    case DATA_CHANGED:
                        callAsyncTask(TYPE_REFRESH_DATA_FROM_DB);
                }
                break;
            case HANJA_VOCA:
                DIC_HANJA_BOOK dicHanjaBook = (DIC_HANJA_BOOK) successEvent.getModel();
                switch (successEvent.getEventType()) {
                    case UPDATE_LOCAL_HANJA_BOOK_MODEL_WITH_SERVER_MODEL_AND_OPEN_EDIT_SCREEN:
                        if (isOpenEditView_) {
                            openEditHanjaBookScreen(dicHanjaBook);
                            isOpenEditView_ = false;
                        }
                        callAsyncTask(TYPE_REFRESH_DATA_FROM_DB);
                        break;
                    case OPEN_EDIT_HANJA_BOOK_SCREEN:
//                        if ((isOpenEditView_) && (Voca.isSameVoca((HanjaItem)dicHanjaBook, mDicHanjaBook))) {
//                            openEditHanjaBookScreen(mDicHanjaBook);
//                        }
                        openEditHanjaBookScreen(dicHanjaBook);
                        break;
                }
                break;
            case MAIN:
                switch (successEvent.getEventType()) {
                    case VOCA_KNOW_CHANGED:
                        callAsyncTask(TYPE_REFRESH_DATA_FROM_DB);
                }
                break;
        }
    }

    protected void refreshVocaFromServer() {
        //TODO : Need to udpate books all contents or make to update only one content in the book.
        isOpenEditView_ = false;
        if (mDicHanjaBook == null) {
            HanjaVoca.refreshHanjaBookAndOpenEditView(context, mHanjaBookItemList.get(0), isOpenEditView_);
        } else {
            HanjaVoca.refreshHanjaBookAndOpenEditView(context, mDicHanjaBook, isOpenEditView_);
        }
    }

    private void openHanjaBookMenuDialog() {
        final HanjaBookMenuDialog dialog = new HanjaBookMenuDialog(this, (view, object) -> {
            switch (view.getId()) {
                case R.id.llRefreshVocaFromServer:
                    refreshVocaFromServer();
                    break;
                case R.id.llWordList:
                    openHanjaWordListInfoView(mHanjaBookItemList.stream().map(e -> e.getHI_VOCA()).collect(Collectors.joining("")));
                    break;
                case R.id.llCopyBookAllContents:
                    copyBookAllContents();
                    break;
                case R.id.llOpenNormalTextViewForHurigana:
                    openNormalTextViewForHuriganaScreen();
                    break;
                case R.id.llBookStyle:
                    saveLastReadBookPageNo();
                    updateUIForBookStyle();
                    callAsyncTask(TYPE_INIT_DATA);
                    break;
                case R.id.ll_home:
                    backToHome();
                    break;
            }
        });
        dialog.show();
    }

    //TODO : need to replace "carrage return" in meaning or meaning detailed with other
    private void copyBookAllContents() {
        String strToCopy = mHanjaBookItemList.stream()
                .map(e -> StringUtils.replaceLineBreaksToBRtag(e.getHI_VOCA() + "\t" + e.getHI_PRONOUNCE1_FIRST() + "\t" + e.getHI_MEANING(this) + "\t" + e.getHI_MEANING_DETAILED(this))).collect(Collectors.joining("\n"));
        String toastText = Voca.getMessageInToastToShow(this, strToCopy);
        Utils.copyToClipboard(context, strToCopy, toastText);
    }
    protected void openNormalTextViewForHuriganaScreen() {
        if (Utils.isIndexInsideRange(mHanjaBookItemList, bookPageIndex)) {
            DIC_HANJA_BOOK dicHanjaBook = mHanjaBookItemList.get(bookPageIndex);
            openNewScreen(
                    NormalTextViewForHuriganaActivity.createIntent(this, dicHanjaBook)
            );
        }
    }

    @Override
    public void onInitAsyncTask() {
        Loading.showDelay(this);
    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }


    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
                if (isBookStylePage()) {
                    getChapterListFromDB();
                    updateChapterIndexAtInitData();
                } else {
                    return searchHanjaBookByKeyword("");
                }
                break;
            case TYPE_REFRESH_DATA_FROM_DB:
                if (isBookStylePage()) {
                    getChapterListFromDB();
                } else {
                    return searchHanjaBookByKeyword("");
                }
            case TYPE_SEARCH_DATA:
                return searchHanjaBookByKeyword((String) data);
        }
        return null;
    }


    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
                if (isBookStylePage()) {
                    initSeekBar();
                    initPagerAdapter();
                } else {
                    onFinishAsyncTaskForBookStylePage(searchType, resultData);
                }
                break;
            case TYPE_REFRESH_DATA_FROM_DB:
                if (isBookStylePage()) {
                    hanjaBookDetailPagerAdapter.notifyDataSetChanged();
                } else {
                    onFinishAsyncTaskForBookStylePage(searchType, resultData);
                }
                break;

        }
        Loading.hide();
    }

    private List<DIC_HANJA_BOOK> searchHanjaBookByKeyword(String keyword) {
        return Voca.getHanjaBookContentListByBookIdAndKeyword(vocabooksHanjaClassics.getID(), keyword);
    }

    private void onFinishAsyncTaskForBookStylePage(int searchType, Object resultData) {
        isResume = true;
        mHanjaBookItemList = (List<DIC_HANJA_BOOK>) resultData;
        adapter.setData(mHanjaBookItemList);
        if (searchType != TYPE_REFRESH_DATA_FROM_DB) {
            int lastReadBookPageNo = getLastReadBookPageNoFromDB();//Voca.getLastReadBookPageNo(BaseVoca.getRealm(), vocabooksHanjaClassics.getID());
            if (Voca.isIndexInsideList(mHanjaBookItemList, lastReadBookPageNo)) {
                this.runOnUiThread(() -> {
                    linearLayoutManager.scrollToPositionWithOffset(lastReadBookPageNo, 0);
                });
            }
        }
    }

    protected void checkAndChangeVocaKnow(IVocaBasicItem voca, int vocaKnow) {
        if (Utils.isConnected(context)) {
            application.getDalAiImpl().checkAndChangeVocaKnow(
                    context,
                    vocaKnow,
                    VocaKnow.getVocaKnowPronounceByVocaKnow(vocaKnow),
                    String.valueOf(voca.getVIVocaId()),
                    voca.getVIVocaType(),
                    new DalApiListener<Integer>() {

                        @Override
                        public void onSuccess(Integer newVocaKnow) {
                            onVocaKnowChanged(voca.getVIVocaId(), newVocaKnow);
                            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.VOCA_KNOW_CHANGED, true));
//                            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DATA_CHANGED, true));
                        }

                        @Override
                        public void onFailure(String error) {
                        }
                    }
            );
        } else {
            alertDialog.showNoInternet();
        }
    }

    protected void onVocaKnowChanged(int vocaID, int vocaKnow) {
        long vocaKnowPronounce = VocaKnow.getVocaKnowPronounceByVocaKnow(vocaKnow);
        Voca.executeRealmTransaction(realm -> {
            DIC_HANJA hanja1 = realm.where(DIC_HANJA.class)
                    .equalTo(Constant.REALMDB.KEY_ID, vocaID)
                    .findFirst();
            if (hanja1 != null) {
                hanja1.setVOCA_KNOW((long) vocaKnow);
                hanja1.setVOCA_KNOWPRONOUNCE(vocaKnowPronounce);
            }
        });
//        callAsyncTask(TYPE_PARSE_CONTENT_AND_DISPLAY_RUBY_TEXT);
    }

    private boolean isBookStylePage() {
        return sharedPreferences.getBookStyle() == BookStyle.PAGE.getValue();
    }

    private void updateUIForBookStyle() {
        if (isBookStylePage()) {
            binding.fssvPreview.setVisibility(View.GONE);
            binding.vpReading.setVisibility(View.VISIBLE);
            handleBbBookPageVisibility(View.VISIBLE);
        } else {
            binding.fssvPreview.setVisibility(View.VISIBLE);
            binding.vpReading.setVisibility(View.GONE);
            handleBbBookPageVisibility(View.GONE);
        }
    }
    private void initSeekBar() {
        binding.sbBookPage.setRange(0, mHanjaBookItemList.size()-1);
        binding.sbBookPage.setIndicatorTextDecimalFormat("0");
        binding.sbBookPage.setProgress(bookPageIndex);
        binding.sbBookPage.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                float leftProgress = view.getLeftSeekBar().getProgress();
                eventBus.post(new SuccessEvent(BaseEvent.Screen.HANJA_BOOK_DETAIL, BaseEvent.EventType.PAGE_INDEX, (int) leftProgress));
                //Don't delete this. Need to set this in the XML to display Indicator
//                view.setIndicatorText(String.valueOf((int)leftProgress));
//                app:rsb_indicator_show_mode="showWhenTouch"
//                app:rsb_indicator_padding_left="10dp"
//                app:rsb_indicator_padding_right="10dp"
//                app:rsb_indicator_padding_top="10dp"
//                app:rsb_indicator_padding_bottom="10dp"
//                app:rsb_indicator_text_size="@dimen/font_value"
//                app:rsb_indicator_background_color="@color/primaryColor"
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                float leftProgress = view.getLeftSeekBar().getProgress();
                movePage((int)leftProgress);
            }
        });
    }

    private void movePage(int page) {
        if (Utils.isIndexInsideRange(mHanjaBookItemList, page)) {
            bookPageIndex = page;
            binding.vpReading.setCurrentItem(bookPageIndex);
        }
    }

    private void getChapterListFromDB() {
        mHanjaBookItemList = Voca.getHanjaItemListFromBookContent(BaseVoca.getRealm(), vocabooksHanjaClassics.getID());
    }
    private void updateChapterIndexAtInitData() {
        bookPageIndex = getLastReadBookPageNoFromDB();
        if (!Utils.isIndexInsideRange(mHanjaBookItemList, bookPageIndex)) {
            bookPageIndex = 0;
        }
    }
    //    private void getHanjaItemFromBook(int index) {
//        if (Utils.isIndexInsideRange(mHanjaBookItemList, index)) {
//            hanjaItem = mHanjaBookItemList.get(index);
//        }
//    }
    public void movePreviousChapter() {
        bookPageIndex--;
        if (bookPageIndex < 0) {
            bookPageIndex = mHanjaBookItemList.size() - 1;
            ToastUtil.getInstance(this).show("마지막 페이지로 이동합니다.");
        }
        binding.vpReading.setCurrentItem(bookPageIndex);
    }

    public void moveNextChapter() {
        bookPageIndex++;
        if (bookPageIndex >= mHanjaBookItemList.size()) {
            bookPageIndex = 0;
            ToastUtil.getInstance(this).show("첫 페이지로 이동합니다.");
        }
        binding.vpReading.setCurrentItem(bookPageIndex);
    }
    protected void showHanjaSearchResultView() {
        super.showHanjaSearchResultView();
        binding.llMain.setVisibility(View.GONE);
        handleAdViewVisibility(View.GONE);
        handleBbBookPageVisibility(View.GONE);
    }

    protected void hideHanjaSearchResultView() {
        super.hideHanjaSearchResultView();
        binding.llMain.setVisibility(View.VISIBLE);
        handleAdViewVisibility(View.VISIBLE);
        handleBbBookPageVisibility(View.VISIBLE);
    }

    private void handleAdViewVisibility(int visibility) {
        if (helper.isPurchasedBook()) {
            BannerAdUtil.handleVisibility(binding.adViewContainer, View.GONE);
        } else {
            BannerAdUtil.handleVisibility(binding.adViewContainer, visibility);
        }
    }

    private void handleBbBookPageVisibility(int visibility) {
        if (UserUtil.isDebugOrAdminUser(this)) {
            binding.sbBookPage.setVisibility(visibility);
        } else {
            binding.sbBookPage.setVisibility(View.GONE);
        }
    }

    public FuriganaView.OnTextSelectedListener getRubyWordClickListener() {
        return onRubyWordClickListener;
    }

    public void changeHeightReading(int height) {
        heightReading = height;
        eventBus.post(new SuccessEvent(BaseEvent.Screen.HANJA_BOOK_DETAIL, BaseEvent.EventType.CHANGE_READING_HEIGHT, heightReading));
    }

    public int getHeightReading() {
        return heightReading;
    }

    public int getPageListSize() {
        return mHanjaBookItemList.size();
    }

    protected void handleEyeClick() {
        if (isBookStylePage()) {
            if (hanjaBookDetailPagerAdapter != null) {
                hanjaBookDetailPagerAdapter.notifyDataSetChanged();
            }
        } else {
            adapter.switchShowFurigana();
            adapter.notifyDataSetChanged();
        }
    }
    protected void initPagerAdapter() {
        hanjaBookDetailPagerAdapter = new HanjaBookDetailPagerAdapter(this, mHanjaBookItemList, vocabooksHanjaClassics.getUSED());
        binding.vpReading.setAdapter(hanjaBookDetailPagerAdapter);
        binding.vpReading.setPageTransformer(new PageTransform());
        binding.vpReading.setOffscreenPageLimit(1);
        binding.vpReading.setCurrentItem(bookPageIndex, false);
        //이건 없어도 차이를 못느끼겠다.
//        binding.vpReading.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                binding.sbBookPage.setProgress(position);
//                bookPageIndex = position;
//            }
//        });
    }
}