package com.dalread.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.dalread.R;
import com.dalread.adapter.HanjaWordGridGroupAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityDemoBinding;
import com.dalread.dialog.HanjaWordGridGroupDialog;
import com.dalread.listener.OnAsyncTaskListenerWithType;
import com.dalread.listener.OnClickListener;
import com.dalread.model.HanjaItem;
import com.dalread.model.ParentGroup;
import com.dalread.model.VOCABOOKS_HANJA;
import com.dalread.model.VOCABOOK_HANJA;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.HanjaVoca;
import com.dalread.util.Loading;
import com.dalread.util.UserUtil;
import com.dalread.util.Voca;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;

//For Debug mode only. Don't Delete it. (이건 어문회등이 레벨이 옆에 있는게 아니고 레벨이 단어랑 같은 테이블에 있는거다. 혹시 쓸지 몰라서 안지울려고 한다)
@SuppressLint("NonConstantResourceId")
public class HanjaWordListGroupInfoActivity extends BaseHanjaInfoActivity implements OnAsyncTaskListenerWithType {
    private Context context;
    VOCABOOKS_HANJA vocabooksHanjaParent;
    private List<ParentGroup> mParentGroupList;
    private HanjaWordGridGroupAdapter adapter;
    private RecyclerViewExpandableItemManager expMgr;
    private HanjaWordGridGroupDialog hanjaWordGridGroupDialog;
    private boolean isExpandAll;

    private final int TYPE_INIT_DATA = 0;
    private final int TYPE_RELOAD_CONTENT = TYPE_INIT_DATA + 1;
    private ActivityDemoBinding binding;

    public static Intent createIntent(Context context, VOCABOOKS_HANJA book) {
        Intent intent = new Intent(context, HanjaWordListGroupInfoActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, book);
        return intent;
    }

    protected View getContentView() {
        binding = ActivityDemoBinding.inflate(getLayoutInflater());
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

//    @Override
//    protected void updateVocaKnowInDB(IVocaBasicItem voca, int newVocaKnow) {
//        super.updateVocaKnowInDB(voca, newVocaKnow);
//    }
//
//    @Override
//    protected void updateVocaKnowPronounceInDB(IVocaBasicItem voca, int newVocaKnowPronounce) {
//        super.updateVocaKnowPronounceInDB(voca, newVocaKnowPronounce);
//    }

    @Override
    public void onHeaderTextRightClick() {
        hanjaWordGridGroupDialog.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _initData();
    }

    private void _initData() {
        context = this;
        isExpandAll = false;
        mParentGroupList = new ArrayList<>();
        vocabooksHanjaParent = (VOCABOOKS_HANJA) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_BOOK);
        getToolbar().setTitle(vocabooksHanjaParent.getName(this));
        callAsyncTask(TYPE_INIT_DATA, vocabooksHanjaParent.getID());
    }

    private void callAsyncTask(int type, Object data) {
        new CustomAsyncTask(this, this, data, type, true).execute();
    }

    private void _initLayout() {
        hanjaWordGridGroupDialog = new HanjaWordGridGroupDialog(this, onDialogItemClickListener);

        adapter = new HanjaWordGridGroupAdapter(this, vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow);
        adapter.setSentenceActivityListener(onSentenceActivityListener);
        binding.rvInfo.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        binding.rvInfo.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        int noOfColumns = Voca.calculateNoOfColumns(this, BaseBindUtils.getStudyHanjaSmallBoxWidth(this), BaseBindUtils.getStudyHanjaSmallBoxMargin(this));
        GridLayoutManager layoutManager = new GridLayoutManager(this, noOfColumns);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (RecyclerViewExpandableItemManager.getPackedPositionChild(expMgr.getExpandablePosition(position)) >= 0) {
                    return 1;
                }
                return noOfColumns;
            }
        });
        binding.rvInfo.setLayoutManager(layoutManager);
        expMgr = new RecyclerViewExpandableItemManager(null);
        binding.rvInfo.setAdapter(expMgr.createWrappedAdapter(adapter));
        ((SimpleItemAnimator) binding.rvInfo.getItemAnimator()).setSupportsChangeAnimations(false);
        expMgr.setDefaultGroupsExpandedState(isExpandAll);
        expMgr.attachRecyclerView(binding.rvInfo);
        adapter.setExpMgr(expMgr);
    }

    protected final DialogInterface.OnClickListener onDialogItemClickListener = (dialog, which) -> {
        switch (which) {
            case R.id.tv_expand_collapse_all:
                refresh4ExpandCollapseAll();
                break;
            default:
                break;
        }
    };

    private void refresh4ExpandCollapseAll() {

        isExpandAll = !isExpandAll;
        if (isExpandAll) {
            expMgr.expandAll();
        } else {
            expMgr.collapseAll();
        }
//        adapter.notifyDataSetChanged();
        hanjaWordGridGroupDialog.updateExpandAllMenuName(isExpandAll);
    }

    private void bindData() {
        adapter.setData(mParentGroupList);
        adapter.notifyDataSetChanged();
    }

    private OnClickListener onSentenceActivityListener = (view, object) -> {
        switch (view.getId()) {
            case R.id.ivWordList:
                List<HanjaItem> hanjas = (List<HanjaItem>) object;
                openNewScreen(
                        HanjaWordListInfoActivity.createIntentVocaTypeId(context, HanjaVoca.getVocaTypeIdFromHanjaItemList(hanjas), false)
                );
                break;
        }
    };



    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if ((successEvent.getScreen() == BaseEvent.Screen.EDIT_HANJA_WORD) ||
                (successEvent.getScreen() == BaseEvent.Screen.EDIT_HANJA_SENTENCE)) {
            BaseEvent.EventType type = successEvent.getEventType();
            if (type == BaseEvent.EventType.DATA_CHANGED) {
                refreshData((HanjaItem) successEvent.getModel());
            }
        } else if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
            BaseEvent.EventType type = successEvent.getEventType();
            if (type == BaseEvent.EventType.VOCA_KNOW_CHANGED) {
                HanjaItem hanjaItem = (HanjaItem) successEvent.getModel();
                if (hanjaItem == null) {
                    reloadData();
                } else {
                    refreshData(hanjaItem);
                }
            }
        }
    }

    private void reloadData() {
        callAsyncTask(TYPE_RELOAD_CONTENT, vocabooksHanjaParent.getID());
    }
    private void refreshData(HanjaItem hanjaItem) {
        for (ParentGroup parentGroup : mParentGroupList) {
            List<HanjaItem> childList = parentGroup.getChildList();
            for (int i = 0; i < childList.size(); i++) {
                HanjaItem hanjaItemInList = childList.get(i);
                if ((hanjaItem.getHI_VOCA_TYPE() == hanjaItemInList.getHI_VOCA_TYPE()) && (hanjaItem.getHI_ID().equals(hanjaItemInList.getHI_ID()))) {
                    childList.set(i, hanjaItem);
                    break;
                }
            }
        }
//        updateLayout();
        bindData();
    }

    @Override
    public void onInitAsyncTask() {
        Loading.show(this);
    }

    @Override
    public void onInitAsyncTask(int searchType) {
        // @dalnim please update here
    }

    @Override
    public void onPrevExecuteAsyncTask() {
    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
            case TYPE_RELOAD_CONTENT:
                Long bookID = (Long) data;
                return getHanjaListFromBookID(bookID);
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
                _initLayout();
                bindData();
                break;
            case TYPE_RELOAD_CONTENT:
                bindData();
                break;
        }
        Loading.hide();
    }

    private List<ParentGroup> getHanjaListFromBookID(Long bookId) {
        mParentGroupList.clear();
        List<VOCABOOKS_HANJA> mVocabooksHanjaList = Voca.getSubWorkbookListFromBookID(bookId, UserUtil.getVocabooksUsedByUserType(this));

        int index = 0;
        for (VOCABOOKS_HANJA vocabooksHanja : mVocabooksHanjaList) {
            List<VOCABOOK_HANJA> vocabookHanjaList = Voca.getVocabookHanjaListByID(vocabooksHanja.getID());
            List<HanjaItem> demoChildList = Voca.getSortedHanjaItemsByDispOrder(vocabookHanjaList);

            mParentGroupList.add(new ParentGroup(index++, vocabooksHanja.getNAME_KO(), demoChildList));
        }

        return mParentGroupList;
    }

    protected void showHanjaSearchResultView() {
        super.showHanjaSearchResultView();
        binding.rvInfo.setVisibility(View.GONE);
    }

    protected void hideHanjaSearchResultView() {
        super.hideHanjaSearchResultView();
        binding.rvInfo.setVisibility(View.VISIBLE);
    }
}
