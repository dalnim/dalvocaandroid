package com.dalread.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dalread.R;
import com.dalread.adapter.IctTermListAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.BaseIctTermFragment;
import com.dalread.component.SeparatorDecoration;
import com.dalread.database.sqlite.model.DIC_ICT_TERM;
import com.dalread.databinding.ActivityIctTermListBinding;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.listener.OnClickListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Loading;
import com.dalread.util.StringUtils;
import com.dalread.util.UtilIctTerm;
import com.dalread.util.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import me.zhanghai.android.fastscroll.FastScrollerBuilder;

public class MainIctTermListFragment extends BaseIctTermFragment implements OnAsyncTaskListener {
    private List<DIC_ICT_TERM> termItemList;
    private IctTermListAdapter termlistAdapter;
    private MainHomeActivity activity;
    private String keyword = "";
    private final int TYPE_INIT_DATA = 0;
    private final int TYPE_INTENT_KEYWORD = TYPE_INIT_DATA + 1;
    private boolean isFilterMode = false;
    private ActivityIctTermListBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityIctTermListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        activity = (MainHomeActivity)getActivity();
        initAdapter();
        initLayout();
        initData();
        initSearch();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initAdapter() {
        termlistAdapter = new IctTermListAdapter(activity, activity.vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, activity.onDoubleClickListenerForCommon, onClickListener);
        termlistAdapter.setSwipeToDelete(false);
    }
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view, Object object) {
            if (object instanceof IVocaBasicItem) {
                IVocaBasicItem item = (IVocaBasicItem) object;
                switch (view.getId()) {
                    case R.id.ivBookmark:
                        activity.subDatabase.swapBookmark(item);
                        item.swapVIBookmark();
                        break;
                    default:
                        openNewScreen(
                                IctTermInfoActivity.createIntent(activity, (DIC_ICT_TERM) object)
                        );
                        break;
                }
            }
        }
    };
    private void bindData(List<DIC_ICT_TERM> itemListToBind) {
        termlistAdapter.setItemList(itemListToBind);
        termlistAdapter.setKeyword(keyword);
        termlistAdapter.setFilterMode(isFilterMode);
        termlistAdapter.notifyDataSetChanged();
    }

    protected void initSearch() {
        activity.getToolbar().setSearchListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                isFilterMode = false;
                keyword = StringUtils.trimAndNormalizeString(s.trim());
                if (keyword.isEmpty()) {
                    bindData(UtilIctTerm.sortList(termItemList));
                } else {
                    isFilterMode = true;
                    List<DIC_ICT_TERM> filteredList = UtilIctTerm.filterList(termItemList, keyword.toLowerCase());
//                    bindData(UtilIctTerm.sortList(filteredList));
                    bindData(filteredList);
                }
                return true;
            }
        }, () -> {
            isFilterMode = false;
            bindData(UtilIctTerm.sortList(termItemList));
            return true;
        });
        activity.getToolbar().showSearchView();
    }
    public void searchByIntentKeyword(String s) {
        callAsyncTask(TYPE_INTENT_KEYWORD, s);
    }

    private void initData() {
        callAsyncTask(TYPE_INIT_DATA, null);
    }

    private void callAsyncTask(int type, Object data) {
        new CustomAsyncTask(activity, this, data, type, true).execute();
    }

    protected void initLayout() {
        new FastScrollerBuilder(binding.rvInfo).build();

        binding.rvInfo.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvInfo.addItemDecoration(new SeparatorDecoration(activity, activity.clDivider, activity.dividerHeight));
        binding.rvInfo.setAdapter(termlistAdapter);
    }
    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        switch (successEvent.getScreen()) {
            case MAIN:
                switch (successEvent.getEventType()) {
                    case VOCA_KNOW_CHANGED:
                    case BOOKMARK_CHANGED:
                        addItemInList((DIC_ICT_TERM) successEvent.getModel());
                        break;
                }
                break;
            case EDIT_ICT_TERM:
                BaseEvent.EventType type = successEvent.getEventType();
                if (type == BaseEvent.EventType.DATA_ADDED) {
                    addItemInList((DIC_ICT_TERM) successEvent.getModel());
                } else if (type == BaseEvent.EventType.DATA_CHANGED) {
                    updateItemInList((DIC_ICT_TERM) successEvent.getModel());
                }
                break;
        }
    }
    public void refreshIctTermList() {
        activity.getToolbar().showSearchView();
        activity.getToolbar().showTitle();
        isFilterMode = false;
        callAsyncTask(TYPE_INIT_DATA, null);
    }
    private void updateItemInList(DIC_ICT_TERM item) {
        termItemList = UtilIctTerm.updateItemInList(termItemList, item);
        bindData(UtilIctTerm.sortList(termItemList));
    }
    private void addItemInList(DIC_ICT_TERM item) {
        termItemList.add(item);
        bindData(UtilIctTerm.sortList(termItemList));
    }
    @Override
    public void onInitAsyncTask() {

    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
            case TYPE_INTENT_KEYWORD:
                return activity.subDatabase.getIctTermList();
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
            case TYPE_INTENT_KEYWORD:
                termItemList = UtilIctTerm.resetIndex((List<DIC_ICT_TERM>)resultData);
                bindData(UtilIctTerm.sortList(termItemList));
                break;
        }
        if (searchType == TYPE_INTENT_KEYWORD) {
            if ((data != null) && (!Utils.isEmpty(data.toString()))) {
                activity.getToolbar().getViewSearch().onActionViewExpanded();
                activity.getToolbar().getViewSearch().post(() -> {
                    activity.getToolbar().getViewSearch().setQuery(data.toString(), true);
                });
            }
        }
        Loading.hide();
    }
}
