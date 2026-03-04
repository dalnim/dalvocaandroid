package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import com.dalread.R;
import com.dalread.adapter.StoredVideosAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.BaseActivity;
import com.dalread.component.Toolbar;
import com.dalread.database.sqlite.MultiPlayerDatabase;
import com.dalread.database.sqlite.model.MultiPlayerVideoModel;
import com.dalread.database.sqlite.model.MultiPlayerVideoStoredModel;
import com.dalread.databinding.ActivityMulitPlayerStoredVideoBinding;
import com.dalread.dialog.YesNoDialog;
import com.dalread.helper.MultiplePlayerDbHelper;
import com.dalread.helper.MultiPlayerPlaylistHelper;
import com.dalread.listener.OnAsyncTaskListenerWithType;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraScreenSecureUtils;
import com.dalread.util.AraThemeUtil;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.GuideUtil;
import com.dalread.util.Loading;
import com.dalread.util.ToastUtil;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultiPlayerStoredVideosActivity extends BaseActivity implements OnAsyncTaskListenerWithType {
    protected final int TYPE_INIT_DATA = 1; // 비디오 리스트를 폰에서 다시 읽어올때.
    protected final int TYPE_RELOAD_FROM_LIST = TYPE_INIT_DATA + 1;
    protected final int TYPE_SEARCH = TYPE_RELOAD_FROM_LIST + 1;
    protected final int TYPE_DELETE_SELECTED_ITEMS = TYPE_SEARCH + 1;

    private ActivityMulitPlayerStoredVideoBinding binding;
    private StoredVideosAdapter adapter;
    private List<MultiPlayerVideoModel> modelList = new ArrayList<>();
    private List<MultiPlayerVideoStoredModel> allModelList = new ArrayList<>();
    private String searchValue;
    private MultiPlayerPlaylistHelper multiPlayerPlaylistHelper;
    private boolean isShowingPlaylistVideos = false;
    private MultiplePlayerDbHelper dbHelper;
    private MultiPlayerDatabase multiPlayerDatabase;

    public static Intent createIntent(Context context, List<MultiPlayerVideoModel> modelList) {
        Intent intent = new Intent(context, MultiPlayerStoredVideosActivity.class);
        intent.putParcelableArrayListExtra(Constant.BUNDLE.KEY_DATA, new ArrayList<>(modelList));
        return intent;
    }

    @Override
    protected View getContentView() {
        binding = ActivityMulitPlayerStoredVideoBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Nullable
    public Toolbar getToolbar() {
        return binding.header;
    }
    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if ((binding.header != null) && (binding.header.isSearchStarted())) {
            binding.header.closeSearchView();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modelList = getIntent().getParcelableArrayListExtra(Constant.BUNDLE.KEY_DATA);
        if (modelList.isEmpty()) {
            binding.btnSave.setVisibility(View.INVISIBLE);
        }
        AraScreenSecureUtils.enableSecureFlag(this);
        initHelper();
        initOnClickListener();
        multiPlayerPlaylistHelper = new MultiPlayerPlaylistHelper(this, multiPlayerDatabase);
        sharedPreferences.setShowNormalVideoFileList(true);
        initView();
        initColor();
    }
    private void initColor() {
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            AraThemeUtil.setBackgroundColor(this, binding.root, R.color.multiPlayerBackgroundLightBlackColor);
        }
    }

    private void initHelper() {
        dbHelper = new MultiplePlayerDbHelper(this);
        multiPlayerDatabase = dbHelper.initSubDatabase(multiPlayerDatabase);
    }

    private void initOnClickListener() {

    }
    @Override
    public void onResume() {
        super.onResume();
        callAsyncTask("", TYPE_INIT_DATA, true);
    }

    private void initView() {
        initAdapter();
        initToolbar();
    }
    private void callAsyncTask(String data, int type, boolean isLoading) {
        callAsyncTaskPlayerFile( new PlayerFileModel(data), type, isLoading);
    }
    private void callAsyncTaskPlayerFile(PlayerFileModel data, int type, boolean isLoading) {
        callAsyncTask(this, data, type, isLoading);
    }
    public void callAsyncTask(OnAsyncTaskListener listener, Object data, int type, boolean isLoading) {
        new CustomAsyncTask(this, listener, data, type, isLoading).execute();
    }
    public void callAsyncTask(OnAsyncTaskListener listener, Object data, int type) {
        callAsyncTask(listener, data, type, true);
    }
    private void initAdapter() {
        adapter = new StoredVideosAdapter(this, (View view, Object object) -> {
            switch (view.getId()) {
                case R.id.ivEdit:
                    handleEdit(object);
                    break;
                default:
                    loadStoredLayout(object);
                    break;
            }
        });
        binding.rvVideo.setAdapter(adapter);
    }
    //TODO : 에디터는 아직 기능이 안되어 있다.
    private void handleEdit(Object object) {
        ArrayList<MultiPlayerVideoStoredModel> list = (ArrayList<MultiPlayerVideoStoredModel>) object;
        if (list.isEmpty()) {
            return;
        }

        List<Integer> storedIds = list.stream()
                .map(MultiPlayerVideoStoredModel::getSTORED_ID)
                .collect(Collectors.toList());
        if (sharedPreferences.isShowConfirmPopupToDeleteItem()) {
            sharedPreferences.setShowConfirmPopupToDeleteItem();
            showDialogConfirmDeleteSelectedVideos(storedIds);
        } else {
            callAsyncTask(MultiPlayerStoredVideosActivity.this, storedIds, TYPE_DELETE_SELECTED_ITEMS);
        }
    }
    // 저장된 레이아웃 로드 시 current_screens를 비운 뒤 screens_in_stored_layout 내용으로 채움.
    private void loadStoredLayout(Object object) {
        ArrayList<MultiPlayerVideoStoredModel> list = (ArrayList<MultiPlayerVideoStoredModel>) object;
        if (list.isEmpty()) {
            return;
        }
        multiPlayerDatabase.deleteMultiScreenAllRecords();
        for(MultiPlayerVideoStoredModel storedModel : list) {
            MultiPlayerVideoModel model = new MultiPlayerVideoModel();
            model.initializeFromStoredModel(storedModel);
            multiPlayerDatabase.insertOrUpdateCurrentScreenOnly(model);
        }

        Intent intent = new Intent();
        intent.putExtra(Constant.BUNDLE.KEY_MULTI_PLAYER_SCREEN_STORED_LAYOUT_ID, list.get(0).getSTORED_ID());
        intent.putExtra(Constant.BUNDLE.KEY_MULTI_PLAYER_SCREEN_STORED_LAYOUT_ROTATE_LAYOUT, list.get(0).getROTATE_LAYOUT());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initToolbar() {
        binding.header.setSearchListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                DLog.d(getLogTag(), "onQueryTextChange - searchValue=" + searchValue);
                callAsyncTask(MultiPlayerStoredVideosActivity.this, s, TYPE_SEARCH, true);
                return true;
            }
        }, () -> {
            DLog.d(getLogTag(), "searchValue=" + searchValue);
            callAsyncTask(this, searchValue, TYPE_SEARCH, true);
            return true;
        });
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }
    @Override
    public void onInitAsyncTask(int searchType) {
        DLog.d(getLogTag(), "onInitAsyncTaskWithType");
        switch (searchType) {
            case TYPE_INIT_DATA:
                Loading.show(this, R.string.msg_indexing_video_files);
                break;
            default:
                Loading.showDelay(this);
        }
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
            case TYPE_RELOAD_FROM_LIST:
                return loadData();
            case TYPE_DELETE_SELECTED_ITEMS:
                List<Integer> storedIdList = (List<Integer>) data;
                deleteRecordsByStoredIdList(storedIdList);
                return loadData();
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
            case TYPE_RELOAD_FROM_LIST:
            case TYPE_DELETE_SELECTED_ITEMS:
                allModelList = ((List<MultiPlayerVideoStoredModel>) resultData);
                finishLoadData();
                break;
            default:
                break;
        }
        Loading.hide();
    }

    private void showGuideHowToUse() {
        if (sharedPreferences.isFirstShowGuideHowToUseMultiPlayerScreenStoredLayout()) {
            sharedPreferences.setFirstShowGuideHowToUseMultiPlayerScreenStoredLayout();
            showGuideHowToUseScreenStoredLayout();
        }
    }
    private void showGuideHowToUseScreenStoredLayout() {
            String title = getString(R.string.guide_multi_player_how_to_use_screen_stored_layouer);
            GuideUtil.showGuideView(this, title, binding.btnSave, view -> {});
    }

    private List<MultiPlayerVideoStoredModel> loadData() {
        return multiPlayerDatabase.getAllScreenStoredLayout();
    }

    private void deleteRecordsByStoredIdList(List<Integer> storedIds) {
        multiPlayerDatabase.deleteRecordsByStoredIdList(storedIds);
    }


    private void showDialogConfirmDeleteSelectedVideos(List<Integer> storedIdList) {
        YesNoDialog dialog = new YesNoDialog(
                this,
                R.string.confirm,
                R.string.msg_video_items_confirm_delete,
                storedIdList,
                new OnYesNoClickListener() {
                    @Override
                    public void onYesClick(View view, Object object) {
                        callAsyncTask(MultiPlayerStoredVideosActivity.this, object, TYPE_DELETE_SELECTED_ITEMS);
                    }

                    @Override
                    public void onNoClick(View view, Object object) {

                    }
                });
        dialog.show();
    }

    protected void finishLoadData() {
        adapter.setData(allModelList);
        showGuideHowToUse();
    }

    public void onMenuButtonClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave:
                handleSaveButton();
                break;
        }
    }

    private void handleSaveButton() {
        //이름은 나중에 입력받아야 한다.
        List<MultiPlayerVideoStoredModel> storedModelList = new ArrayList<>();
        String layoutName = RandomStringUtils.randomAlphabetic(4);
        for(MultiPlayerVideoModel model : modelList) {
            MultiPlayerVideoStoredModel storedModel = new MultiPlayerVideoStoredModel();
            storedModel.initializeFromBaseModel(model, -1, layoutName, 0, sharedPreferences.getMultiPlayerScreenOrientation());
            storedModelList.add(storedModel);
        }
        int result = multiPlayerDatabase.saveStoredLayout(storedModelList);
        switch (result) {
            case MultiPlayerDatabase.STORED_SCREEN_REPLACED:
                ToastUtil.getInstance(this).show(R.string.toast_current_videos_replaced);
                break;
            case MultiPlayerDatabase.STORED_SCREEN_STORED:
                ToastUtil.getInstance(this).show(R.string.toast_current_videos_stored);
                break;
            case MultiPlayerDatabase.STORED_SCREEN_NOTHING_HAPPEN:
            default:
                ToastUtil.getInstance(this).show(R.string.toast_current_videos_stored_nothing_happen);
                break;
        }
        binding.btnSave.setVisibility(View.INVISIBLE);
        callAsyncTask("", TYPE_RELOAD_FROM_LIST, true);
    }
}
