package com.dalread.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.HanjaWordlistAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityHanjaWordListBinding;
import com.dalread.dialog.HanjaWordListDialog;
import com.dalread.listener.OnClickListener;
import com.dalread.model.HanjaItem;
import com.dalread.model.VOCABOOK_HANJA;
import com.dalread.model.VocaTypeId;
import com.dalread.model.WordListType;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

import butterknife.BindString;
import me.zhanghai.android.fastscroll.FastScrollerBuilder;

@SuppressLint("NonConstantResourceId")
public class HanjaWordListInfoActivity extends BaseHanjaInfoActivity implements OnAsyncTaskListener  {
    @BindString(R.string.tpl_wb_known_word_count)
    String tplKnownWordCount;

    private List<HanjaItem> hanjaItemList;
    private HanjaWordlistAdapter hanjaWordlistAdapter;
    private HanjaWordListDialog hanjaWordListDialog;
    private boolean show4Buttons;
    private String vocaTypeListWithComma;
    private String vocaIDListWithComma;

    private final int TYPE_INIT_DATA = 0;
    private ActivityHanjaWordListBinding binding;
    private WordListType wordListType;

    public static Intent createIntentVocaTypeId(Context context, List<VocaTypeId> vocaTypeIdList, boolean show4Buttons) {
        Intent intent = new Intent(context, HanjaWordListInfoActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE_ID, (Serializable) vocaTypeIdList);
        intent.putExtra(Constant.BUNDLE.KEY_SHOW_4_BUTTONS, show4Buttons);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.VOCA_TYPE_ID_LIST);
        return intent;
    }

    public static Intent createIntentByBookId(Context context, long bookId, String workbookName) {
        Intent intent = new Intent(context, HanjaWordListInfoActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, bookId);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_NAME, workbookName);
        intent.putExtra(Constant.BUNDLE.KEY_SHOW_4_BUTTONS, false);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.BOOK);
        return intent;
    }

    public static Intent createIntentByContent(Context context, String content, boolean show4Buttons) {
        Intent intent = new Intent(context, HanjaWordListInfoActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA, content);
        intent.putExtra(Constant.BUNDLE.KEY_SHOW_4_BUTTONS, show4Buttons);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.VOCA);
        return intent;
    }

    public static Intent createIntentByWordListTypeOnly(Context context, WordListType wordListType, boolean show4Buttons) {
        Intent intent = new Intent(context, HanjaWordListInfoActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, wordListType);
        intent.putExtra(Constant.BUNDLE.KEY_SHOW_4_BUTTONS, show4Buttons);
        return intent;
    }

    protected View getContentView() {
        binding = ActivityHanjaWordListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected RecyclerView getRvSearch() {
        return binding.rvSearch;
    }

    @Override
    public void onHeaderTextRightClick() {
        hanjaWordListDialog.show();
    }

//    @Override
//    protected RecyclerView.Adapter<RecyclerView.ViewHolder> getAdapter() {
//        return hanjaWordlistAdapter;
//    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        switch (successEvent.getScreen()) {
            case MAIN:
                switch (successEvent.getEventType()) {
                    case VOCA_KNOW_CHANGED:
                    case BOOKMARK_CHANGED:
                        reloadData((HanjaItem) successEvent.getModel());
                        break;
                }
                break;
            case EDIT_HANJA_WORD:
            case EDIT_HANJA_SENTENCE:
                BaseEvent.EventType type = successEvent.getEventType();
                if (type == BaseEvent.EventType.DATA_CHANGED) {
                    reloadData((HanjaItem) successEvent.getModel());
                }
                break;
        }
    }
    private void reloadData(HanjaItem hanjaItem) {
        if (hanjaItem == null)
            return;

        for (int i = 0; i < hanjaItemList.size(); i++) {
            HanjaItem hanjaItemInList = hanjaItemList.get(i);
            if ((hanjaItem.getHI_VOCA_TYPE() == hanjaItemInList.getHI_VOCA_TYPE()) && (hanjaItem.getHI_ID().equals(hanjaItemInList.getHI_ID()))) {
                hanjaItem.setHI_INDEX(hanjaItemInList.getHI_INDEX());
                hanjaItemList.set(i, hanjaItem);
            }
        }
        bindData(hanjaItemList);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addMobileAdsView();
        loadBanner();
    }

    private void initAdapter() {
        hanjaWordlistAdapter = new HanjaWordlistAdapter(this, vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, onDoubleClickListenerForCommon, onSearchClickListener);
    }

    protected void initData() {
        super.initData();
        show4Buttons = false;
        initAdapter();
        binding.layoutItemContentBookTitle.vItem.setVisibility(View.GONE);
        wordListType = (WordListType) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE);
        show4Buttons = getIntent().getBooleanExtra(Constant.BUNDLE.KEY_SHOW_4_BUTTONS, false);
        if (getToolbar() != null) {
            if (wordListType.equals(WordListType.BOOKMARK)) {
                getToolbar().setTitle(R.string.bookmarks);
            } else if (wordListType.equals(WordListType.BOOK)) {
                String bookTitle = getIntent().getStringExtra(Constant.BUNDLE.KEY_VOCA_BOOK_NAME);
                if (!Utils.isEmpty(bookTitle)) {
                    int maxBookTitleLengthToDisplayInToolBar = 6;
                    if (bookTitle.length() < maxBookTitleLengthToDisplayInToolBar) {
                        getToolbar().setTitle(bookTitle);
                    } else {
                        binding.layoutItemContentBookTitle.tvBookTitle.setText(bookTitle);
                        binding.layoutItemContentBookTitle.vItem.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        callAsyncTask(TYPE_INIT_DATA, null);
    }

    private void resetIndexInList() {
        int i = 1;
        for(HanjaItem hanjaItem : hanjaItemList) {
            hanjaItem.setHI_INDEX((long) i++);
        }
    }

    protected void initLayout() {
        super.initLayout();
        new FastScrollerBuilder(binding.rvInfo).build();

        binding.rvInfo.setLayoutManager(new LinearLayoutManager(context));
        binding.rvInfo.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
        binding.rvInfo.setAdapter(hanjaWordlistAdapter);

        hanjaWordListDialog = new HanjaWordListDialog(this, show4Buttons, onDialogItemClickListener);
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

    @SuppressWarnings("unchecked")
    @Override
    protected void getData() {

    }

//    @Override
//    protected List<HanjaQuizItem> getQuizHanjaList() {
//        return new ArrayList<>();
//    }

//    @Override
//    protected void updateVocaKnowInDB(IVocaBasicItem voca, int newVocaKnow) {
//        super.updateVocaKnowInDB(voca, newVocaKnow);
//        hanjaWordlistAdapter.notifyDataSetChanged();
//    }
//
//    @Override
//    protected void updateVocaKnowPronounceInDB(IVocaBasicItem voca, int newVocaKnowPronounce) {
//        super.updateVocaKnowPronounceInDB(voca, newVocaKnowPronounce);
//    }

    protected final DialogInterface.OnClickListener onDialogItemClickListener = (dialog, which) -> {
        switch (which) {
            case R.id.tvShow4Buttons:
                refresh4Buttons();
                break;
            case R.id.tvAllToKnown:
                getVocaTypeAndID();
                changeMultipleVocaKnow(vocaTypeListWithComma, vocaIDListWithComma, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
                break;
            case R.id.tvAllToUnknown:
                getVocaTypeAndID();
                changeMultipleVocaKnow(vocaTypeListWithComma, vocaIDListWithComma, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
                break;
            case R.id.tvBackToHome:
                backToHome();
                break;
            default:
                break;
        }
    };

    private void bindData(List<HanjaItem> hanjaItemListToBind) {
        hanjaWordlistAdapter.setData(hanjaItemListToBind);
        hanjaWordlistAdapter.setShow4Buttons(show4Buttons);
        hanjaWordlistAdapter.notifyDataSetChanged();
    }

    private final OnClickListener onSearchClickListener = (view, object) -> {
        if ((view.getId() == R.id.etSearchKeyword)) {
            String keyword = (String) object;
            List<HanjaItem> hanjaItemListFiltered = new ArrayList<>();
            for(HanjaItem hanjaItem : hanjaItemList) {
                if ((hanjaItem.getHI_VOCA().contains(keyword))
                    || (hanjaItem.getHI_PRONOUNCE1_FIRST().contains(keyword))) {
                    hanjaItemListFiltered.add(hanjaItem);
                }
            }
            Collections.sort(hanjaItemListFiltered, Comparator.nullsLast(Comparator.comparingInt(HanjaItem::getHI_VOCA_TYPE)
                .thenComparing(HanjaItem::getHI_PRONOUNCE1_FIRST)
                .thenComparing(HanjaItem::getHI_MEANING1)));

            bindData(hanjaItemListFiltered);
        } else if ((view.getId() == R.id.btnResetSearch)) {
            bindData(hanjaItemList);
        }
    };

    private void refresh4Buttons() {
        show4Buttons = !show4Buttons;
        hanjaWordlistAdapter.setShow4Buttons(show4Buttons);
        hanjaWordlistAdapter.notifyDataSetChanged();
        hanjaWordListDialog.update4ButtonName(show4Buttons);
    }

    private void getVocaTypeAndID() {
        StringJoiner sjVocaType = new StringJoiner(",");
        StringJoiner sjVocaID = new StringJoiner(",");
        for(HanjaItem hanjaItem : hanjaItemList) {
            sjVocaType.add(String.valueOf(hanjaItem.getHI_VOCA_TYPE()));
            sjVocaID.add(String.valueOf(hanjaItem.getHI_ID()));
        }

        vocaTypeListWithComma = sjVocaType.toString();
        vocaIDListWithComma = sjVocaID.toString();
    }
    private void changeMultipleVocaKnow(String vocaTypeListWithComma, String vocaIDListWithComma, int vocaKnow) {
        if (Utils.isConnected(context)) {
            Loading.show(this);
            application.getDalAiImpl().changeMultipleVocaKnow(
                String.valueOf(vocaKnow),
                String.valueOf(vocaKnow),
                vocaIDListWithComma,
                vocaTypeListWithComma,
                new DalApiListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        if (response == true) {
                            long vocaKnowPronounce = VocaKnow.getVocaKnowPronounceByVocaKnow(vocaKnow);
                            hanjaItemList.stream().forEach(e -> { e.setHI_VOCA_KNOW((long) vocaKnow); e.setHI_VOCA_KNOWPRONOUNCE(vocaKnowPronounce);});
                            bindData(hanjaItemList);
                            Voca.updateMultipleVocaKnow(hanjaItemList, vocaKnow);
                            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.MULTIPLE_VOCA_KNOW_CHANGED, vocaKnow));
                        }
                        Loading.hide();
                    }

                    @Override
                    public void onFailure(String error) {
                        Loading.hide();
                    }
                }
            );
        } else {
            alertDialog.showNoInternet();
        }
    }
    private void callAsyncTask(int type, Object data) {
        new CustomAsyncTask(this, this, data, type, true).execute();
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
                if (wordListType.equals(WordListType.UNICODE)) {
                    return Voca.getUnicodeHanjaList();
                } else if (wordListType.equals(WordListType.BOOKMARK)) {
                    return Voca.getVocaBookmarkList();
                } else if (wordListType.equals(WordListType.BOOK)) {
                    return getHanjaListFromBookID((int)getIntent().getLongExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, 0L));
                } else if (wordListType.equals(WordListType.VOCA)) {
                    return Voca.getHanjaWordItemListFromContent(getIntent().getStringExtra(Constant.BUNDLE.KEY_VOCA), false);
                } else if (wordListType.equals(WordListType.VOCA_TYPE_ID_LIST)) {
                    return Voca.getHanjaWordItemListFromVocaTypeIdList((List<VocaTypeId>) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_TYPE_ID));
                }
                return hanjaItemList;
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
                hanjaItemList = (List<HanjaItem>)resultData;
                resetIndexInList();
                bindData(hanjaItemList);
                break;
        }
        Loading.hide();
    }

    private List<HanjaItem> getHanjaListFromBookID(int bookId) {
        List<VOCABOOK_HANJA> vocabookHanjaList = Voca.getVocabookHanjaListByID(bookId);
        return Voca.getSortedHanjaItemsByDispOrder(vocabookHanjaList);
    }


}
