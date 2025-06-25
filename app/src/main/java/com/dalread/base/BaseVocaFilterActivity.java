package com.dalread.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.SearchView;

import com.dalread.R;
import com.dalread.activity.PracticeConversationActivity;
import com.dalread.adapter.VocaFilterAdapter;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.Toolbar;
import com.dalread.composition.AbstractBaseVocaKnowActivity;
import com.dalread.composition.VocaKnowActivity;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.databinding.ActivityVocaFilterBinding;
import com.dalread.dialog.SelectRange2Dialog;
import com.dalread.dialog.VocaFilterDialog;
import com.dalread.interfaces.IVocaCoreItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.VocaKnowGroupSelect;
import com.dalread.model.VocaTypeId;
import com.dalread.model.WordListType;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseVoca;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.BaseVocaList;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.PlaylistUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
//BasePlayListByTtsActivity와 BaseVocaFilterActivity는 공통되는게 많다. BasePlayListByTtsActivity를 Abastrct로 하지 말고, 둘다의 공통 무모 클래스를 하나 만들것.
public class BaseVocaFilterActivity extends BaseActivity {// implements OnAsyncTaskListenerWithType {
    public enum NextView {
        PRACTICE_CONVERSATION, PLAY_ALL
    }
    protected WordListType wordListType;
    protected int bookId;
    protected static SubDatabase subDatabase;
    protected static String subDatabasePath;
    protected List<VocaTypeId> vocaTypeIdList;
    private String keyword = "";
    private VocaFilterAdapter adapter;
    protected List<IVocaFullPlayTTSItem> originalFullItems;
    private List<IVocaFullPlayTTSItem> displayItems;
    private List<IVocaFullPlayTTSItem> filteredByKeywordsItems;
    private int checkedCount;
    private SelectRange2Dialog selectRangeDialog;
    protected AbstractBaseVocaKnowActivity vocaKnowActivity;
    private ActivityVocaFilterBinding binding;
    private boolean isAllItemsSelected = true;
    protected VocaFilterDialog vocaFilterDialog;
    protected NextView nextView;

    @Override
    protected View getContentView() {
        binding = ActivityVocaFilterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBundle();
        initLocalDatabase();
        initVocaKnowActivity();
        initRecyclerView(); //Dalnim : switched line order with initToolbar(). (fullItems was null if initToolbar() run first)
        initDialog();
        initToolbar();
        setUpClickListeners();
    }

    private void initBundle() {
        bookId = getIntent().getIntExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, -1);
        wordListType = (WordListType) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE);
        vocaTypeIdList = (List<VocaTypeId>) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_TYPE_ID);
        nextView = (NextView) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_FILTER_TYPE);
        subDatabasePath = getIntent().getStringExtra(Constant.BUNDLE.KEY_SUB_DATABASE_PATH);
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
    //    private void openVocaFilterDialog() {
//        final BaseVocaFilterDialog dialog = new BaseVocaFilterDialog(this, new OnClickListener() {
//            @Override
//            public void onClick(View view, Object object) {
//                switch (view.getId()) {
//                    case R.id.llShowAllVocas:
//                        closeSearchView();
//                        filteredByKeywordsItems.clear();
//                        BaseVocaList.updateCheckedInVocaList(originalFullItems, true);
//                        displayItems = new ArrayList<>(originalFullItems);
//                        bindData(displayItems);
//                        ToastUtil.getInstance(BaseVocaFilterActivity.this).show(R.string.toast_item_all_items_displayed);
//                        isAllItemsSelected = true;
//                        updateSelectAllStatus();
//                        break;
//                    case R.id.llFilterByVocaKnow:
//                        if (vocaFilterDialog == null) {
//                            vocaFilterDialog = new VocaFilterDialog(BaseVocaFilterActivity.this, onVocaFilterDialogListener);
//                        }
//                        vocaFilterDialog.show();
//                        break;
//                }
//            }
//        });
//
//        dialog.show();
//    }
    @Override
    public void onResume() {
        super.onResume();
    }

    // ear phone
    @Subscribe
    public void onEvent(SuccessEvent event) {
        switch (event.getScreen()) {
            case COMMON_VOCA_DATA:
                IVocaFullPlayTTSItem item= (IVocaFullPlayTTSItem) event.getModel();
                switch (event.getEventType()) {
                    case COMMON_VOCA_KNOW_CHANGED:
                    case COMMON_VOCA_KNOWPRONOUNCE_CHANGED:
                    case COMMON_VOCA_BOOKMKARK_CHANGED:
                        refreshSearchViewAdapterVocaKnow(item);
                        break;
                }
                break;
        }
    }

    protected void initToolbar() {
//        toolbar.getIconRight2().setVisibility(View.GONE);
//        toolbar.getTvRight().setVisibility(View.GONE);
//        toolbar.getIconRight().setVisibility(View.GONE);
        binding.header.setSearchListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filteredByKeywordsItems.clear();
                String keywordLocal = s.toLowerCase();
                if (!keywordLocal.isEmpty()) {
                    for (IVocaFullPlayTTSItem item : displayItems) {
                        if (item.getVIVoca().toLowerCase().contains(keywordLocal)) {
                            filteredByKeywordsItems.add(item);
                        }
                    }
                }
                binding.rvVoca.post(new Runnable() {
                    @Override
                    public void run() {
                        keyword = keywordLocal;
                        if (filteredByKeywordsItems.size() == 0) {
                            bindData(displayItems);
                        } else {
                            bindData(filteredByKeywordsItems);
                        }
                    }
                });
                return true;
            }
        }, new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return true;
            }
        });
        binding.header.showSearchView();
    }

    private void initRecyclerView() {
        List<IVocaCoreItem> vocaCoreItemList = (List<IVocaCoreItem>) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST);
        displayItems = getFullItems(vocaCoreItemList);
        if (displayItems == null) {
            displayItems = new ArrayList<>();
        }
        originalFullItems = new ArrayList<>(displayItems);
        filteredByKeywordsItems = new ArrayList<>();
        updateSelectAllStatus();

        for (int i = 0; i < originalFullItems.size(); i++) {
            IVocaFullPlayTTSItem item = originalFullItems.get(i);
            item.setVIChecked(true);
            item.setVIIndex(i + 1);
        }
        adapter = new VocaFilterAdapter(this, sharedPreferences.getDisplayPronunciation(), vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, onDoubleClickListener);
        binding.rvVoca.setAdapter(adapter);
        binding.rvVoca.setLayoutManager(new CenterLayoutManager(this));
    }

    protected void initDialog() {
        selectRangeDialog = new SelectRange2Dialog(this, new SelectRange2Dialog.OnRangeSelectListener() {
            @Override
            public void onSelect(int from, int to) {
                updateStateRangeData(from, to);
            }

            @Override
            public void onDismiss(View v) {

            }
        });
//        selectRangeDialog.setTitle(R.string.select_range_filter_title);
//        selectRangeDialog.setDesc(R.string.select_range_filter_desc);
    }
    private void setUpClickListeners() {
        binding.ivSelectAll.setOnClickListener( v -> {
            selectOrUnselectAllInTable();
        });
        binding.ivDisplayAllItems.setOnClickListener( v -> {
            closeSearchView();
            filteredByKeywordsItems.clear();
            BaseVocaList.updateCheckedInVocaList(originalFullItems, true);
            displayItems = new ArrayList<>(originalFullItems);
            bindData(displayItems);
            ToastUtil.getInstance(BaseVocaFilterActivity.this).show(R.string.toast_item_all_items_displayed);
            isAllItemsSelected = true;
            updateSelectAllStatus();
        });
        binding.ivFilter.setOnClickListener( v -> {
            if (vocaFilterDialog == null) {
                vocaFilterDialog = new VocaFilterDialog(BaseVocaFilterActivity.this, onVocaFilterDialogListener);
            }
            vocaFilterDialog.show();
        });
        binding.fabNextView.setOnClickListener(view -> {
            int checkedCount = getCheckedCount();
            if (checkedCount < Constant.vocaFilterCountToCheck) {
                List<IVocaFullPlayTTSItem> listTemp = displayItems.stream().filter(e->e.isVIChecked()).collect(Collectors.toList());
                if (filteredByKeywordsItems.size() > 0)
                    listTemp = filteredByKeywordsItems;
                openRelatedActivity(listTemp);
            } else {
                int toCount = checkedCount > Constant.vocaFilterCountToCheck ? Constant.vocaFilterCountToCheck : checkedCount;
                selectRangeDialog.show(1, checkedCount);
            }
        });
    }
//    private void openPracticeConversation(List<IVocaFullPlayTTSItem> list) {
//        Intent intent = PracticeConversationActivity.createIntentByVocaTypeId(BaseVocaFilterActivity.this, BaseVocaList.convertToVocaTypeIdList(BaseVocaList.getVocaListByChecked(list)));
//        startActivity(intent);
//    }
    private void selectOrUnselectAllInTable() {
        isAllItemsSelected = !isAllItemsSelected;
        BaseVocaList.updateCheckedInVocaList(displayItems, isAllItemsSelected);
        updateSelectAllStatus();
        String message = isAllItemsSelected ?
                getString(R.string.toast_item_group_selected_all_displayed_items)
                : getString(R.string.toast_item_group_unselected_all_displayed_items);
        ToastUtil.getInstance(this).show(message);
        bindData(displayItems);
    }

    private void updateSelectAllStatus() {
        binding.ivSelectAll.setSelected(isAllItemsSelected);
    }

    private void updateSelectedItemCountStatus() {
        checkedCount = getCheckedCount();
        String status = getString(R.string.voca_filter_message_total_item_count) + " : " + displayItems.size() + ", " + getString(R.string.voca_filter_message_selected_item_count) + " : " + checkedCount;
        binding.tvStatus.setText(status);
    }

    protected OnDoubleClickListener onDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object data) {
            IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) data;
            switch (view.getId()) {
                case R.id.ivCheck:
                case R.id.llMain:
                    voca.setVIChecked(!voca.isVIChecked());
                    adapter.notifyPlaylistItemChanged(voca);
                    boolean checked = voca.isVIChecked();
                    if (checked) {
                        checkedCount++;
                    } else {
                        checkedCount--;
                    }
                    updateSelectedItemCountStatus();
                    break;
            }
        }

        @Override
        public void onDoubleClick(View view, Object data) {

        }
    };

    private void updateStateRangeData(int from, int to) {
        int displayItemsTempSize = 0;
        if (filteredByKeywordsItems.size() > 0) {
            displayItemsTempSize = filteredByKeywordsItems.size();
        } else {
            displayItemsTempSize = displayItems.size();
        }

        if (from < 1) {
            from = 1;
        }
        if (to < 1) {
            to = 1;
        }
        int size = displayItemsTempSize;
        if (from > size) {
            from = size;
        }
        if (to > size) {
            to = size;
        }
        if (to < from) {
            int temp = from;
            from = to;
            to = temp;
        }
        from--;
        to--;
        ArrayList<IVocaFullPlayTTSItem> listTemp = new ArrayList<>();
        for (int i = from; i < size; i++) {
            IVocaFullPlayTTSItem item;
            if (filteredByKeywordsItems.size() > 0) {
                item = filteredByKeywordsItems.get(i);
            } else {
                item = displayItems.get(i);
            }
            if (item.isVIChecked()) {
                listTemp.add(item);
            }
            if (listTemp.size() > to) {
                break;
            }
        }
        openRelatedActivity(listTemp);
    }

    //아직 이건 한번도 테스트 안됐다.
    private void refreshSearchViewAdapterVocaKnow(IVocaFullPlayTTSItem voca) {
        if (Utils.isEmpty(originalFullItems))
            return;

        for (IVocaFullPlayTTSItem iVocaFullItem : originalFullItems) {
            if (BaseVoca.isSameVoca(iVocaFullItem, voca)) {
                int newBookmark = voca.getVIBookmark();
                int newVocaKnow = voca.getVIVocaKnow();
                int newVocaKnowPronounce = BaseVocaKnow.getVocaKnowPronounceByVocaKnow(newVocaKnow);
                iVocaFullItem.setVIBookmark(newBookmark);
                iVocaFullItem.setVIVocaKnow(newVocaKnow);
                iVocaFullItem.setVIVocaKnowPronounce(newVocaKnowPronounce);
                break;
            }
        }
        if (filteredByKeywordsItems.size() > 0) {
            bindData(filteredByKeywordsItems);
        } else {
            bindData(displayItems);
        }
    }

    private void bindData(List<IVocaFullPlayTTSItem> displayItemsLocal) {
        binding.ivSelectAll.setVisibility(displayItemsLocal.size() > 0 ? View.VISIBLE:View.INVISIBLE);
        adapter.setData(displayItemsLocal);
        adapter.setKeyword(keyword);
        adapter.notifyDataSetChanged();
        updateSelectedItemCountStatus();
    }

    protected OnClickListener onVocaFilterDialogListener = (view, object) -> {
        DLog.d(getLogTag(), "onWordListPlayerPlayAllWordsDialogListener");
        if (view.getId() == R.id.btn_ok) {
            Map<String, IVocaFullPlayTTSItem> filteredData = BaseVocaList.getVocaTypeIdMapByFilter((VocaKnowGroupSelect) object, displayItems);
            List<IVocaFullPlayTTSItem> filteredDisplayItems = new ArrayList<>();
            for (IVocaFullPlayTTSItem item : displayItems) {
                String vocaTypeId = item.getVIVocaTypeId();
                if (filteredData.containsKey(vocaTypeId)) {
                    filteredDisplayItems.add(item);
                } else {
                    item.setVIChecked(false);
                }
            }
            displayItems = filteredDisplayItems;
            bindData(displayItems);
        }
    };

    private int getCheckedCount() {
        List<IVocaFullPlayTTSItem> listTemp = displayItems;
        if (filteredByKeywordsItems.size() > 0)
            listTemp = filteredByKeywordsItems;

        return (int) listTemp.stream()
                .filter(IVocaFullPlayTTSItem::isVIChecked)
                .count();
    }

    private void closeSearchView() {
        //            toolbar.getViewSearch().setIconified(true); //Don't delete this
        binding.header.getViewSearch().onActionViewCollapsed();
        binding.header.showTitle();
        if (filteredByKeywordsItems != null)
            filteredByKeywordsItems.clear();
    }

    public static Intent createIntentWithBookId(Context context, WordListType wordListType, NextView nextView, int bookId, String subDatabasePath) {
        Intent intent = new Intent(context, BaseVocaFilterActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, wordListType);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, bookId);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_FILTER_TYPE, nextView);
        intent.putExtra(Constant.BUNDLE.KEY_SUB_DATABASE_PATH, subDatabasePath);
        return intent;
    }

    public static Intent createIntentWithVocaTypeIdList(Context context, WordListType wordListType, NextView nextView, List<VocaTypeId> vocaTypeIdList, String subDatabasePath) {
        Intent intent = new Intent(context, BaseVocaFilterActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, wordListType);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE_ID, (Serializable) vocaTypeIdList);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_FILTER_TYPE, nextView);
        intent.putExtra(Constant.BUNDLE.KEY_SUB_DATABASE_PATH, subDatabasePath);
        return intent;
    }

    protected List<IVocaFullPlayTTSItem> getFullItems(List<IVocaCoreItem> vocaCoreItemList) {
        return BaseVocaList.getVocaList(wordListType, bookId, subDatabase, vocaTypeIdList);
    }

    protected void openRelatedActivity(List<IVocaFullPlayTTSItem> list) {
        if (nextView == NextView.PLAY_ALL) {
            PlaylistUtil.openPlaylistByVocaList(BaseVocaFilterActivity.this, list, wordListType, null, subDatabase.getDatabasePath());
        } else {
            Intent intent = PracticeConversationActivity.createIntentByVocaTypeId(BaseVocaFilterActivity.this, BaseVocaList.convertToVocaTypeIdList(BaseVocaList.getVocaListByChecked(list)));
            startActivity(intent);
        }
    }

    protected void initVocaKnowActivity() {
        vocaKnowActivity = new VocaKnowActivity(this, subDatabase);
    }

    protected void initLocalDatabase() {
        if (subDatabase != null) {
            subDatabase.close();
        }
        subDatabase = null;
        subDatabase = SubDatabase.getInstance(this, subDatabasePath);
    }
}

