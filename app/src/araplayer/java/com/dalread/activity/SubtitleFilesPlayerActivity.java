package com.dalread.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.SubtitleFilesPlayerAdapter;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.BasePlayerActivity;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivitySubtitleFilesPlayerBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.StorageUtil;
import com.dalread.util.SubtitleFileNameUtils;
import com.dalread.util.Utils;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SubtitleFilesPlayerActivity extends BasePlayerActivity implements OnAsyncTaskListener {
    private List<PlayerFileModel> playerFileModels = new ArrayList<>();
    private ArrayList<PlayerFileModel> displayItems = new ArrayList<>();
    private SubtitleFilesPlayerAdapter adapter;
    private String currentPath = Constant.BASE_BLANK;
    private Stack<String> paths = new Stack<>();
    private Stack<String> titles = new Stack<>();
    private String fileName = Constant.BASE_BLANK;
    private int subtitleIndex = -1;
    private int subPathIndex = Constant.PLAYER.INTENT.SUBPATH_INDEX_1;
    final Handler mHandler = new Handler();

    private final int COUNT_OF_ITEMS_TO_SCROLL_SMOOTH = 20;
    private final int TYPE_INIT_DATA = 0;
    private final int TYPE_LOAD_NEXT_DATA = TYPE_INIT_DATA + 1;
    private final int TYPE_LOAD_PREV_DATA = TYPE_LOAD_NEXT_DATA + 1;
    private final int TYPE_SYNC_DATA_BEFORE_EXIT = TYPE_LOAD_PREV_DATA + 1;
    protected final int TYPE_SEARCH = TYPE_SYNC_DATA_BEFORE_EXIT + 1;

    private ActivitySubtitleFilesPlayerBinding binding;

    protected String searchValue;

    @Override
    protected View getContentView() {
        binding = ActivitySubtitleFilesPlayerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.tvVideoFileName.setText(playerFileModel.getVideoModel().getName());
    }

    @Override
    protected void setFullscreen() {

    }

    @Override
    public void initView() {
        adapter = new SubtitleFilesPlayerAdapter(this, displayItems, OnPlayerItemClickListener);
        binding.rvContent.setAdapter(adapter);
        binding.rvContent.setLayoutManager(new CenterLayoutManager(this));
        binding.rvContent.addItemDecoration(new SeparatorDecoration(this, BaseBindUtils.getDividerColor(), BaseBindUtils.getDividerHeight(this)));
        binding.header.getTvRight().setVisibility(View.INVISIBLE);
        initToolbar();
        initData();
    }

    private void initToolbar() {
        binding.header.showSearchView();
        binding.header.setSearchListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchValue = s;
                callAsyncTask(searchValue, TYPE_SEARCH);
                return true;
            }
        }, () -> {
            searchValue = Constant.BASE_BLANK;
            callAsyncTask(searchValue, TYPE_SEARCH);
            return true;
        });
    }

    @Override
    public void initData() {
        playerFileModel = getIntent().getExtras().getParcelable(Constant.PLAYER.INTENT.KEY_DATA);
        subPathIndex = getIntent().getIntExtra(Constant.PLAYER.INTENT.KEY_SUBPATH_INDEX, Constant.PLAYER.INTENT.SUBPATH_INDEX_1);
        if (subPathIndex == Constant.PLAYER.INTENT.SUBPATH_INDEX_2) {
            fileName = playerFileModel.getSubPath2();
        } else {
            fileName = playerFileModel.getSubPath1();
        }


        callAsyncTask(currentPath, TYPE_INIT_DATA);
    }

    @Override
    public void onHeaderLeftClick() {
        checkBackFolder();
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
        callAsyncTask(this, TYPE_SYNC_DATA_BEFORE_EXIT);
    }

    private OnClickListener OnPlayerItemClickListener = (view, object) -> {
        switch (view.getId()) {
            case R.id.llFolder:
                callAsyncTaskPlayerFile((PlayerFileModel) object, TYPE_LOAD_NEXT_DATA);
                break;
            case R.id.llItem:
                updateSelectItem(object);
                break;
        }
    };

    @Override
    public void onBackPressed() {
        checkBackFolder();
    }

    private void callAsyncTask(String data, int type) {
        callAsyncTaskPlayerFile( new PlayerFileModel(data), type);
    }

    private void callAsyncTaskPlayerFile(PlayerFileModel data, int type) {
        if (!isNetwork()) {
            alertDialog.showNoInternet();
            return;
        }
        callAsyncTask(this, data, type);
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
            case TYPE_INIT_DATA:
            case TYPE_LOAD_NEXT_DATA:
            case TYPE_LOAD_PREV_DATA:
                loadData(data);
                break;
            case TYPE_SYNC_DATA_BEFORE_EXIT:
                updateSubtitleInfoWithSelected();
                break;
            case TYPE_SEARCH:
                displayItems.clear();
                displayItems.addAll(searchData(playerFileModels));
                break;
        }
        return null;
    }

    private void updateSubtitleInfoWithSelected() {
        resetSubtitleFileAndDatabase(playerFileModel, fileName, subPathIndex);
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
            case TYPE_LOAD_NEXT_DATA:
            case TYPE_LOAD_PREV_DATA:
                finishLoadData(searchType, data);
                break;
            case TYPE_SYNC_DATA_BEFORE_EXIT:
                updateVideoModel(playerFileModel.getVideoModel());
                finish();
                Loading.hide();
                break;
            case TYPE_SEARCH:
                adapter.setKeyword(searchValue);
                adapter.setData(displayItems);
                binding.rvContent.post(() -> binding.rvContent.scrollToPosition(0));
                Loading.hide();
                break;
        }
    }

    private void loadData(Object data) {
        final PlayerFileModel file = (PlayerFileModel) data;
        DLog.d(getLogTag(), "loadData - file=" + file.toString());
        List<PlayerFileModel> list = new ArrayList<>();
        if (Utils.isEmpty(file.getPath())) {
            //Don't add System folder When I choose a subtitle file, Now AraPlayer shows subtitles in th 3 folders only.
//            list.add(new PlayerFileModel(getString(R.string.system_folder), 0, 0, StorageUtil.getRootFolder(), PlayerFileModel.DirectoryType.BLACK));
            list.addAll(StorageUtil.getAllSubtitlesOrLyrics(
                    this,
                    null, true));
        } else {
            list.addAll(StorageUtil.getAllSubtitlesOrLyrics(
                    this,
                    file.getPath(), true,false));
        }

        playerFileModels = (ArrayList<PlayerFileModel>) StorageUtil.sortFiles(list, Constant.PLAYER.SORT.FILE_NAME_ASC);
        displayItems.clear();
        displayItems.addAll(playerFileModels);
        findAndSetSubtitleIndex();
        showSelectMenu();
    }

    private void findAndSetSubtitleIndex() {
        subtitleIndex = SubtitleFileNameUtils.findLinkedSubtitleIndex(fileName, displayItems);

        if (subtitleIndex != -1) {
            displayItems.get(subtitleIndex).setCheck(true);
        } else {
            subtitleIndex = SubtitleFileNameUtils.findBestSubtitleMatch(playerFileModel.getName(), displayItems);
        }
    }

    private void finishLoadData(int type, Object data) {
        final PlayerFileModel file = (PlayerFileModel) data;
        if (type == TYPE_LOAD_NEXT_DATA) {
            setCurrentNextPath(file.getName(), file.getPath());
        } else if (type == TYPE_LOAD_PREV_DATA) {
            setCurrentPrevPath();
        }
        DLog.d(getLogTag(), "finishLoadData");
        adapter.setData(displayItems);
        scrollToPositionSubtitleDialog();
        Loading.hide();
    }

    public void setCurrentNextPath(String name, String path) {
        setTitle(name);
        paths.push(currentPath);
        currentPath = path;
        DLog.d(getLogTag(), "setCurrentPath - currentPath=" + this.currentPath);
    }

    public void setCurrentPrevPath() {
        binding.header.setTitle(titles.pop());
        DLog.d(getLogTag(), "setCurrentPath - currentPath=" + this.currentPath);
    }

    private boolean isRootPath() {
        return paths.size() <= 0;
    }

    private void updateSelectItem(Object object) {
        final PlayerFileModel item = (PlayerFileModel) object;
        fileName = item.isCheck() ? item.getPath() : Constant.BASE_BLANK;
        showSelectMenu();
    }

    private void showSelectMenu() {
        runOnUiThread(() -> {
            if (Utils.isEmpty(fileName)) {
                binding.header.getTvRight().setVisibility(View.INVISIBLE);
            } else {
                binding.header.getTvRight().setVisibility(View.VISIBLE);
            }
        });
    }

    private void checkBackFolder() {
        if (isRootPath()) {
            super.onBackPressed();
        } else {
            sendCurrentPath();
        }
    }

    public void setTitle(String msg) {
        titles.push(binding.header.getTitle());
        binding.header.setTitle(msg);
    }

    private void sendCurrentPath() {
        DLog.d(getLogTag(), "sendCurrentPath - currentPath=" + currentPath);
        currentPath = paths.pop();
        callAsyncTask(currentPath, TYPE_LOAD_PREV_DATA);
    }

    private void scrollToPositionSubtitleDialog() {
        if (subtitleIndex < 0 || subtitleIndex > displayItems.size()) return;
        mHandler.postDelayed(() -> {
            binding.rvContent.post(() -> {
                if (subtitleIndex <= COUNT_OF_ITEMS_TO_SCROLL_SMOOTH)
                    binding.rvContent.smoothScrollToPosition(subtitleIndex);
                else
                    scrollPositionInSubtitleTableView(binding.rvContent, subtitleIndex);//rvContent.scrollToPosition(subtitleIndex);
                subtitleIndex = -1;
            });
        }, 200);
    }
    private void scrollPositionInSubtitleTableView(RecyclerView list, int pos) {
        try {
            if (list.getLayoutManager() instanceof LinearLayoutManager) {
                // Centering item in its parent
                final LinearLayoutManager manager = (LinearLayoutManager) list.getLayoutManager();
                final boolean isHorizontal = manager.getOrientation() == LinearLayoutManager.HORIZONTAL;
                int offset = isHorizontal
                        ? (list.getWidth() - list.getPaddingLeft() - list.getPaddingRight()) / 2
                        : (list.getHeight() - list.getPaddingTop() - list.getPaddingBottom()) / 2;
                final RecyclerView.ViewHolder holder = list.findViewHolderForAdapterPosition(pos);
                if (holder != null) {
                    final View view = holder.itemView;
                    offset -= isHorizontal ? view.getWidth() / 2 : view.getHeight() / 2;
                }
                manager.scrollToPositionWithOffset(pos, offset);
            }
        } catch (Exception ex) {
            DLog.e(getLogTag(), "error=" + ex.getMessage());
        }
    }

    private List<PlayerFileModel> searchData(List<PlayerFileModel> totalData) {
        if (Utils.isEmpty(searchValue) || Utils.isEmpty(totalData))
            return totalData;
        final ArrayList<PlayerFileModel> temp = new ArrayList<>();
        for (PlayerFileModel playerFileModel : totalData) {
            if (playerFileModel.isDirectory()) continue;
            String filePath = FilenameUtils.getPath(playerFileModel.getPath()).toLowerCase();
            String fileName = playerFileModel.getName().toLowerCase();
            if (filePath.contains(searchValue.toLowerCase()) || fileName.contains(searchValue)) {
                temp.add(playerFileModel);
            }
        }
        return temp;
    }

}
