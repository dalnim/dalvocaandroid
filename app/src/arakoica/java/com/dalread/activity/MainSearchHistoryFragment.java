package com.dalread.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dalread.R;
import com.dalread.adapter.IctTermListAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.BaseIctTermFragment;
import com.dalread.component.SeparatorDecoration;
import com.dalread.database.sqlite.model.DIC_ICT_TERM;
import com.dalread.databinding.ActivityIctTermListBinding;
import com.dalread.dialog.YesNoDialog;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Loading;
import com.dalread.util.UtilIctTerm;
import com.dalread.util.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import me.zhanghai.android.fastscroll.FastScrollerBuilder;

public class MainSearchHistoryFragment extends BaseIctTermFragment implements OnAsyncTaskListener {
    private List<DIC_ICT_TERM> itemList;
    private IctTermListAdapter termlistAdapter;
    private MainHomeActivity activity;

    private final int TYPE_INIT_DATA = 0;
    private final int TYPE_CLEAR_SEARCH_HISTORY = 1;

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
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initAdapter() {
        termlistAdapter = new IctTermListAdapter(activity, activity.vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, activity.onDoubleClickListenerForCommon, onClickListener);
        termlistAdapter.setSwipeToDelete(true);
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
                    case R.id.right_view:
                        resetSearchHistory((DIC_ICT_TERM) item);
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
    private void resetSearchHistory(DIC_ICT_TERM item) {
        itemList.remove(item);
        activity.subDatabase.resetSearchHistory(item.getVIId());
    }
    private void bindData(List<DIC_ICT_TERM> itemListToBind) {
        termlistAdapter.setItemList(itemListToBind);
        termlistAdapter.notifyDataSetChanged();
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
                        updateItemInList((DIC_ICT_TERM) successEvent.getModel());
                        break;
                }
                break;
            case EDIT_ICT_TERM:
                BaseEvent.EventType type = successEvent.getEventType();
                if (type == BaseEvent.EventType.DATA_CHANGED) {
                    updateItemInList((DIC_ICT_TERM) successEvent.getModel());
                }
                break;
        }
    }
    public void clearSearchHistory() {
        final YesNoDialog dialog = new YesNoDialog(activity, R.string.warning, R.string.dialog_title_clear_search_history, null, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                callAsyncTask(TYPE_CLEAR_SEARCH_HISTORY, null);
            }

            @Override
            public void onNoClick(View view, Object object) {

            }
        });
        dialog.show();

    }
    private void updateItemInList(DIC_ICT_TERM item) {
        itemList = UtilIctTerm.addItemInList(itemList, item);
        bindData(itemList);
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
                return activity.subDatabase.getSearchHistoryIctTermList();
            case TYPE_CLEAR_SEARCH_HISTORY:
                activity.subDatabase.clearSearchHistory();
                return activity.subDatabase.getSearchHistoryIctTermList();
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
            case TYPE_CLEAR_SEARCH_HISTORY:
                itemList = UtilIctTerm.resetIndex((List<DIC_ICT_TERM>)resultData);
                showOrHideDeleteSearchHistoryIcon();
                bindData(itemList);
                break;
        }
        Loading.hide();
    }

    private void showOrHideDeleteSearchHistoryIcon() {
        if (Utils.isEmpty(itemList)) {
            activity.getToolbar().hideIconRight();
        } else {
            activity.getToolbar().showIconRight();
            activity.getToolbar().setIconRight(R.drawable.ic_refresh_white);
        }
    }
}
