package com.dalread.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.DalPlayerAdapter;
import com.dalread.adapter.SelectVideoAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.BaseActivity;
import com.dalread.component.Toolbar;
import com.dalread.database.VideoModelQuery;
import com.dalread.database.sqlite.MultiPlayerDatabase;
import com.dalread.databinding.ActivityMulitPlayerSelectVideoBinding;
import com.dalread.dialog.AraMultiChoiceDialog;
import com.dalread.dialog.MultiPlayerShowSortDialog;
import com.dalread.dialog.MultiPlayerVideoListMoreDialog;
import com.dalread.dialog.PlayerShowSortMenuDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.helper.MultiPlayerFileListHelper;
import com.dalread.helper.MultiplePlayerDbHelper;
import com.dalread.helper.PlaylistHelper;
import com.dalread.helper.ScrollToItemHelper;
import com.dalread.listener.OnAsyncTaskListenerWithType;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnLongClickListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.PlaylistModel;
import com.dalread.model.VideoModel;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraScreenSecureUtils;
import com.dalread.util.AraThemeUtil;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.GuideUtil;
import com.dalread.util.Loading;
import com.dalread.util.MediaFileListUtil;
import com.dalread.util.MediaListUtil;
import com.dalread.util.PlaylistBackupHelper;
import com.dalread.util.StorageUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.Voca;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

//This is for MultiPlayer only
public class MultiPlayerSelectVideoActivity extends BaseActivity implements OnClickListener, View.OnClickListener, OnAsyncTaskListener, OnAsyncTaskListenerWithType {
    protected final int TYPE_INIT_DATA = 1; // 비디오 리스트를 폰에서 다시 읽어올때.
    protected final int TYPE_RELOAD_DATA_FROM_DB = TYPE_INIT_DATA + 1; //비디오 리스트를 DB에서 읽어올때.
    protected final int TYPE_RELOAD_FROM_LIST = TYPE_RELOAD_DATA_FROM_DB + 1; //이미 fileListTotal를 읽어왔으면 여기서 필터링을 한다.
    protected final int TYPE_LOAD_VIDEO_FOR_STUDY_LANGUAGE_FOLDER_LIST = TYPE_RELOAD_FROM_LIST + 1;
    protected final int TYPE_SEARCH = TYPE_LOAD_VIDEO_FOR_STUDY_LANGUAGE_FOLDER_LIST + 1;
    protected final int TYPE_DELETE_SELECTED_VIDEOS = TYPE_SEARCH + 1;
    protected final int TYPE_CONVERT_TO_HIDDEN_FILES = TYPE_DELETE_SELECTED_VIDEOS + 1;
    protected final int TYPE_CONVERT_TO_NORMAL_FILES = TYPE_CONVERT_TO_HIDDEN_FILES + 1;

    private ActivityMulitPlayerSelectVideoBinding binding;
    private SelectVideoAdapter selectVideoAdapter;
    private DalPlayerAdapter dalPlayerAdapter;
    private Stack<String> paths = new Stack<>();
    protected VideoModel currentVideoModel;
    private boolean isSmallGroupVideoListLoaded_StudyLanguageVideoFolder; // When study language's video folder is displaying.
    protected List<PlayerFileModel> fileListTotal = new ArrayList<>();
    protected List<PlayerFileModel> currentFilesList = new ArrayList<>();
    protected List<PlayerFileModel> filteredFileList = new ArrayList<>();
    private String searchValue = "";
    private PlaylistHelper playlistHelper;
    private boolean isShowingPlaylistVideos = false;
    List<PlaylistModel> selectedPlaylists = new ArrayList<>();
    private ActivityResultLauncher<IntentSenderRequest> intentSenderLauncher;
    private List<PlayerFileModel> deletedFilesList = new ArrayList<>();
    private int deletedFilePos = 0;
    private MultiplePlayerDbHelper dbHelper;
    private MultiPlayerDatabase multiPlayerDatabase;
    private String previousSelectedFileName = Constant.BASE_BLANK;

    @Override
    protected View getContentView() {
        binding = ActivityMulitPlayerSelectVideoBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Nullable
    public Toolbar getToolbar() {
        return binding.header;
    }
    @Override
    public void onHeaderLeftClick() {
//        closeEditView();
        if (isSmallGroupVideoListLoaded_StudyLanguageVideoFolder()) {
            backToMainVideoListView();
            refreshVideoListOnBackPressed();
        } else {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.mediaListLayoutEdit.getVisibility() == View.VISIBLE) {
            closeEditView();
            return;
        }
        if (isSmallGroupVideoListLoaded_StudyLanguageVideoFolder()) {
            backToMainVideoListView();
            refreshVideoListOnBackPressed();
        } else if ((binding.header != null) && (binding.header.isSearchStarted())) {
            binding.header.closeSearchView();
//        } else if (!sharedPreferences.getShowNormalVideoFileList() || isShowingPlaylistVideos) {
//            isShowingPlaylistVideos = false;
//            backToMainVideoListView();
//            reloadNormalOrHidedVideoList();
//            refreshVideoListOnBackPressed();
        } else {
            super.onBackPressed();
        }
    }
    private void refreshVideoListOnBackPressed() {
        exitEditMode();
        if (isSmallGroupVideoListLoaded_StudyLanguageVideoFolder()) {
            callAsyncTask(MultiPlayerSelectVideoActivity.this, null, TYPE_LOAD_VIDEO_FOR_STUDY_LANGUAGE_FOLDER_LIST, true);
        } else {
            callAsyncTask(MultiPlayerSelectVideoActivity.this, "", TYPE_RELOAD_FROM_LIST, false);
        }
    }
//    @Override
//    public void onHeaderLeft2Click() {
//
//    }
//
//    @Override
//    public void onHeaderRightClick() {
//
//    }
//
//    @Override
//    public void onHeaderIconRightClick() {
//
//    }

    @Override
    public void onHeaderTextRightClick() {
        showSortDialog();
    }
    private void showMoreDialog() {
        MultiPlayerVideoListMoreDialog dialog = new MultiPlayerVideoListMoreDialog(this, isShowingPlaylistVideos, (dialogInterface, i) -> {
            switch (i) {
                case R.id.llSelectAll:
                    dalPlayerAdapter.selectAllVideo(true);
                    break;
                case R.id.llUnSelectAll:
                    dalPlayerAdapter.selectAllVideo(false);
                    break;
                case R.id.llRemoveFromPlaylist:
                    deleteSelectedItemsFromPlaylist();
                    break;
                case R.id.llConvertToHiddenFiles:
                    showDialogConfirmConvertToHiddenFiles(true);
                    break;
                case R.id.llConvertToNormalFiles:
                    showDialogConfirmConvertToHiddenFiles(false);
                    break;
                case R.id.llDeleteFile:
                    showDialogConfirmDeleteSelectedVideos();
                    break;
            }
        });
        dialog.show();
    }
    private void deleteSelectedItemsFromPlaylist() {
        List<PlayerFileModel> selectedVideoList = new ArrayList<>(dalPlayerAdapter.getSelectedVideoList());
        int selectedCount = selectedVideoList.size();
        if (selectedCount == 0) {
            showToastNoSelectedMedia();
            return;
        }
        if (selectedPlaylists.isEmpty()) {
            return;
        }
        playlistHelper.deleteSelectedItemsFromPlaylist(selectedVideoList, selectedPlaylists);
        callAsyncTask("", TYPE_RELOAD_FROM_LIST, true);

        String message = this.getResources().getQuantityString(
                R.plurals.msg_video_files_removed_from_playlist, selectedCount, selectedCount);

        ToastUtil.getInstance(this).show(message);
    }

    private void showSortDialog() {
        MultiPlayerShowSortDialog dialog = new MultiPlayerShowSortDialog(this, sharedPreferences, isShowingPlaylistVideos,(dialogInterface, i) -> {
            switch (i) {
                case R.id.llRestorePlaylistBackup:
                    PlaylistBackupHelper helper = new PlaylistBackupHelper(MultiPlayerSelectVideoActivity.this);
                    helper.restoreAll();
                    break;
                case R.id.llSort:
                    showSortMenuDialog();
                    break;
                case R.id.llEdit:
                    showEditView();
                    break;
                case R.id.llShowNormalVideoFiles:
                    sharedPreferences.setShowNormalVideoFileList(false);
                    closeEditView();
                    showLoginPopForHiddenFiles();
                    break;
                case R.id.llShowHiddenVideoFiles:
                    sharedPreferences.setShowNormalVideoFileList(true);
                    closeEditView();
                    showLoginPopForHiddenFiles();
                    break;
                case R.id.llRefreshMediaList:
                    AraMultiChoiceDialog.showMediaFolderSelectionDialog(context, selectedFolders -> {
                        callRefreshMethod();
                    });
                    break;
                case R.id.llCreatePlaylist:
                    playlistHelper.createPlaylist();
                    break;
                case R.id.llDeletePlaylist:
                    playlistHelper.showDeletePlaylistDialog();
                    break;
                case R.id.llPlaylist:
                    closeEditView();
                    showPlaylistVideos();
                    break;
            }
        });
        dialog.show();
    }
    private void showSortMenuDialog() {
        PlayerShowSortMenuDialog dialog = new PlayerShowSortMenuDialog(this, (dialogInterface, i) -> {
            int sort = Constant.PLAYER.SORT.FILE_NAME_ASC;
            switch (i) {
//                @OnClick({R.id.tv_sort_created_date_asc, R.id.tv_sort_created_date_desc, R.id.tv_sort_file_size_asc, R.id.tv_sort_file_size_desc, R.id.tv_sort_file_name_asc, R.id., R.id.tv_sort_difficulty_asc, R.id.tv_sort_difficulty_desc, R.id.tv_cancel})
                case R.id.tv_sort_created_date_asc:
                    sort = Constant.PLAYER.SORT.CREATE_DATE_ASC;
                    break;
                case R.id.tv_sort_created_date_desc:
                    sort = Constant.PLAYER.SORT.CREATE_DATE_DESC;
                    break;
                case R.id.tv_sort_file_size_asc:
                    sort = Constant.PLAYER.SORT.FILE_SIZE_ASC;
                    break;
                case R.id.tv_sort_file_size_desc:
                    sort = Constant.PLAYER.SORT.FILE_SIZE_DESC;
                    break;
                case R.id.tv_sort_file_name_asc:
                    sort = Constant.PLAYER.SORT.FILE_NAME_ASC;
                    break;
                case R.id.tv_sort_file_name_desc:
                    sort = Constant.PLAYER.SORT.FILE_NAME_DESC;
                    break;
                case R.id.tv_sort_difficulty_asc:
                    sort = Constant.PLAYER.SORT.DIFFICULTY_ASC;
                    break;
                case R.id.tv_sort_difficulty_desc:
                    sort = Constant.PLAYER.SORT.DIFFICULTY_DESC;
                    break;
            }
            sharedPreferences.setPlayerFileSort(sort);
            sortFiles(currentFilesList);
            dalPlayerAdapter.setData(currentFilesList);
//            eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.PLAYER_SORT, sort));
        });
        dialog.show();
    }
    private void showEditView() {
        binding.mediaListLayoutEdit.setVisibility(View.VISIBLE);
        dalPlayerAdapter.setEditMode(true);
    }
    private void closeEditView() {
        binding.mediaListLayoutEdit.setVisibility(View.GONE);
        dalPlayerAdapter.setEditMode(false);
    }

    private void showPlaylistVideos() {
        playlistHelper.selectPlaylists(true, true,R.string.playlist_dialog_button_select, R.string.playlist_dialog_button_cancel, new PlaylistHelper.PlaylistSelectionCallback() {
            @Override
            public void onPlaylistsSelected(List<PlaylistModel> selectedPlaylists) {
                if (selectedPlaylists.isEmpty()) {
                    MultiPlayerSelectVideoActivity.this.selectedPlaylists = new ArrayList<>();
                    isShowingPlaylistVideos = false;
                    return;
                }
                MultiPlayerSelectVideoActivity.this.selectedPlaylists = selectedPlaylists;
                isShowingPlaylistVideos = true;
                animatePlaylistVideoList();
                exitEditMode();
                callAsyncTask(MultiPlayerSelectVideoActivity.this, "", TYPE_RELOAD_FROM_LIST, false);
            }
        });

    }
    private void showLoginPopForHiddenFiles() {
        isShowingPlaylistVideos = false;
        reloadNormalOrHidedVideoList();
        exitEditMode();
        callAsyncTask(MultiPlayerSelectVideoActivity.this, "", TYPE_RELOAD_FROM_LIST, false);

        //이부분은 나중에 숨김 비디오 리스트갈때 비번 넣기 위해서 필요. 아래꺼 쓰면 위는 주석처리할것
//        if (sharedPreferences.getShowNormalVideoFileList()) {
//            boolean isLoginMode = Utils.isEmpty(sharedPreferences.getPasswordHiddenFiles()) ? false : true;
//            startActivity(SignInUpHiddenFilesActivity.createIntent(this, isLoginMode));
//        } else {
//            reloadNormalOrHidedVideoList();
//        }
    }
    private void animatePlaylistVideoList() {
        updateToolBarTitle();
//        boolean showNormalVideoFileList = true;
//        showViewWithAnimation(toolbar.getIconLeft(), showNormalVideoFileList, null);
//        showViewWithAnimation(toolbar.getTvTitle(), showNormalVideoFileList, new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                if (!showNormalVideoFileList) {
//                    updateToolBarTitleForShowHideMode();
//                }
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                if (showNormalVideoFileList) {
//                    updateToolBarTitleForShowHideMode();
//                }
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        showViewWithAnimation(binding.rvVideo, showNormalVideoFileList, null);
    }

    private void reloadNormalOrHidedVideoList() {
//        boolean showNormalVideoFileList = !sharedPreferences.getShowNormalVideoFileList();
//        sharedPreferences.setShowNormalVideoFileList(showNormalVideoFileList);
//        setSecureView();
        updateToolBarTitle();
//        showViewWithAnimation(toolbar.getIconLeft(), showNormalVideoFileList, null);
//        showViewWithAnimation(toolbar.getTvTitle(), showNormalVideoFileList, new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                if (!showNormalVideoFileList) {
//                    updateToolBarTitleForShowHideMode();
//                }
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                if (showNormalVideoFileList) {
//                    updateToolBarTitleForShowHideMode();
//                }
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        showViewWithAnimation(binding.rvVideo, showNormalVideoFileList, null);
        if (!sharedPreferences.getShowNormalVideoFileList()) {
            ToastUtil.getInstance(getBaseContext()).show(R.string.messsage_enter_hidden_video_files);
        }
        binding.tvShowHideSelectedMedia.setText(sharedPreferences.getShowNormalVideoFileList() ? R.string.video_list_multi_select_menu_hide : R.string.video_list_multi_select_menu_show);
    }
//    private void setSecureView() {
//        if (sharedPreferences.getShowNormalVideoFileList()) {
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
//        } else {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//                    WindowManager.LayoutParams.FLAG_SECURE);
//        }
//    }
    private void showViewWithAnimation(View view, boolean showNormalVideoFileList, Animation.AnimationListener animationListener) {
        TranslateAnimation animation = new TranslateAnimation(view.getWidth(), 0, 0 , 0);
        if (showNormalVideoFileList) {
            animation = new TranslateAnimation(0, view.getWidth(), 0 , 0);
        }
        animation.setDuration(300);
        animation.setAnimationListener(animationListener);
        view.startAnimation(animation);
    }
    private void updateToolBarTitle() {
        boolean showNormalVideoFileList = sharedPreferences.getShowNormalVideoFileList();
        binding.tvPlaylistName.setVisibility(View.GONE);
        if (isShowingPlaylistVideos && !selectedPlaylists.isEmpty()) {
            binding.header.setTitle(R.string.view_title_playlist);
            if (selectedPlaylists.size() == 1) {
                handler.post(() -> {
                    binding.tvPlaylistName.setText(selectedPlaylists.get(0).getName() + " (" + selectedPlaylists.get(0).getFilePathCount() + ")");
                    binding.tvPlaylistName.setVisibility(View.VISIBLE);
                });
            }
        } else {
            if (showNormalVideoFileList) {
                binding.header.setTitle(R.string.title_toolbar_home_normal_video_mode);
            } else {
                binding.header.setTitle(R.string.title_toolbar_home_hidden_video_mode);
            }
        }
    }

    private void backToMainVideoListView() {
        setSmallGroupVideoListLoaded_StudyLanguageVideoFolder(false);
    }
    public void setSmallGroupVideoListLoaded_StudyLanguageVideoFolder(boolean smallGroupVideoListLoaded_StudyLanguageVideoFolder) {
        isSmallGroupVideoListLoaded_StudyLanguageVideoFolder = smallGroupVideoListLoaded_StudyLanguageVideoFolder;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        copySampleVideos();
        getIntentData();
        AraScreenSecureUtils.enableSecureFlag(this);
        initHelper();
        initOnClickListener();
        playlistHelper = new PlaylistHelper(this);
        sharedPreferences.setShowNormalVideoFileList(true);
        initView();
        initIntentLauncher();
        initColor();
    }
    private void copySampleVideos() {
        if(!sharedPreferences.isSampleVideoCopied()) {
            String destPathWithFileName = BaseStorageUtil.getDownloadFolderPath(this) + File.separator + Constant.PLAYER.ASSET_SAMPLE_FILE_MULTI_VIDEOS_ZIP;
            BaseStorageUtil.copyFileAssetsToExternalStorageOrOverwrite(context, Constant.PLAYER.ASSET_SAMPLE_FILE_MULTI_VIDEOS_ZIP, destPathWithFileName, true);
            StorageUtil.unzipFileAndDeleteZipFile(destPathWithFileName);
            sharedPreferences.setSampleVideoCopied();
        }
    }
    public void getIntentData() {
        previousSelectedFileName = getIntent().getStringExtra(Constant.PLAYER.INTENT.KEY_SUBPATH_INDEX);
    }

    private void initColor() {
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            AraThemeUtil.setBackgroundColor(this, binding.root, R.color.multiPlayerBackgroundLightBlackColor);
            AraThemeUtil.setTextColor(this, binding.tvPlaylistName, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setTextColor(this, binding.tvGuide, R.color.textPrimaryWhiteColor);

        }
    }

    private void initHelper() {
        dbHelper = new MultiplePlayerDbHelper(this);
        multiPlayerDatabase = dbHelper.initSubDatabase(multiPlayerDatabase);
    }
    private void initIntentLauncher() {
        // MANAGE_EXTERNAL_STORAGE권한이 없으면 한건 한건이 들어오기 때문에 하나씩 처리해야 한다.
        intentSenderLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
            if (!deletedFilesList.isEmpty()) {
                //삭제 거부한거는 삭제하면 안된다. 허용한거만 삭제해야 한다.
                if (result.getResultCode() == Activity.RESULT_OK) {
                        PlayerFileModel file = deletedFilesList.get(0);
                        deleteRelatedVideoFile(file, false);
                        binding.rvVideo.post(() -> dalPlayerAdapter.notifyDataSetChanged());
                }
                deletedFilesList.remove(0);
            }
        });
    }
    private void initOnClickListener() {
        binding.btnShowHideSelectedMedia.setOnClickListener(this);
        binding.btnPlaySelectedMedia.setOnClickListener(this);
        binding.btnAddItemToPlaylist.setOnClickListener(this);
        binding.btnMore.setOnClickListener(this);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (isSmallGroupVideoListLoaded_StudyLanguageVideoFolder()) {
            callAsyncTask(this, null, TYPE_LOAD_VIDEO_FOR_STUDY_LANGUAGE_FOLDER_LIST, true);
        } else {
            if (isPlayerFetchAllVideoFirstTime()) {
                callAsyncTask("", TYPE_INIT_DATA, true);
            } else {
                runOnUiThread(() -> {
                    binding.header.showSearchView();
                });
//                binding.header.showSearchView(); //이걸 하면 Loading이 사라지기 때문에 순서에 유의해야 한다.
                if (fileListTotal.isEmpty()) {
                    callAsyncTask("", TYPE_RELOAD_DATA_FROM_DB, true);
                } else {
                    callAsyncTask("", TYPE_RELOAD_FROM_LIST, true);
                }
            }
        }
    }
    private boolean isPlayerFetchAllVideoFirstTime() {
        return sharedPreferences.isPlayerFetchAllVideoFirstTime();
    }

    protected void exitEditMode() {
        if (dalPlayerAdapter.isEditMode()) {
            dalPlayerAdapter.setEditMode(false);
        }
    }
    private void initView() {
        showMediaFolderText();
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
        dalPlayerAdapter = new DalPlayerAdapter(this, new ArrayList<PlayerFileModel>(), Constant.AppMediaType.VIDEO, this, null, null, onVideoLongClickListener);
        binding.rvVideo.setAdapter(dalPlayerAdapter);
    }
    private OnLongClickListener onVideoLongClickListener = (view, object) -> {
        if (view.getId() == R.id.llItem) {
            showEditView();
            selectVideoItem();
            showGuideHowToUsePlayButtonAfterLongClick();
        }
    };
    private void selectVideoItem() {
//        eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.ALL_VIDEOS_SELECTED, dalPlayerAdapter.isSelectAllVideoFile()));
    }
    private void showMediaFolderText() {
        binding.llvideoFolderForStudyLanguageInfo.tvvideoFolderForStudyLanguageInfo.setText(getString(R.string.msg_video_folder, StorageUtil.getMediaFolderWithoutLangName(this)));
    }

    @NonNull
    private List<PlayerFileModel> sortFilesAndAddLangFolder(List<PlayerFileModel> list) {
        List<PlayerFileModel> sortedList = sortFiles(list);
        addLanguageFolderInFileList(sortedList);
        return sortedList;
    }

    protected void addLanguageFolderInFileList(List<PlayerFileModel> fileList) {
        if (!isExistLanguageFolderInFileListAlready(fileList)) {
            fileList.add(0, new PlayerFileModel(PlayerFileModel.FileType.LANGUAGE_FOLDER));
        }
    }

    private boolean isExistLanguageFolderInFileListAlready(@NonNull List<PlayerFileModel> fileList) {
        return fileList.stream().filter(e -> e.isLanguageFolder() == true).findFirst().isPresent();
//        for(PlayerFileModel playerFileModel : fileList) {
//            if (playerFileModel.isLanguageFolder())
//                return true;
//        }
//        return false;
    }

    private void initToolbar() {
        binding.header.setSearchListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchValue = s;
                DLog.d(getLogTag(), "onQueryTextChange - searchValue=" + searchValue);
//                callAsyncTask( MultiPlayerSelectVideoActivity.this, null, 901, true); //TODO : Can I call like this? No use eventBus?
//                eventBus.post(new SuccessEvent(BaseEvent.Screen.SELECT_VIDEO_ACTIVITY, BaseEvent.EventType.PLAYER_SEARCH, s));
                callAsyncTask(MultiPlayerSelectVideoActivity.this, s, TYPE_SEARCH, true);
                return true;
            }
        }, () -> {
            searchValue = "";
            DLog.d(getLogTag(), "searchValue=" + searchValue);
//            callAsyncTask(this, null, 901, true);
//            eventBus.post(new SuccessEvent(BaseEvent.Screen.SELECT_VIDEO_ACTIVITY, BaseEvent.EventType.PLAYER_SEARCH, Constant.BASE_BLANK));
            callAsyncTask(this, searchValue, TYPE_SEARCH, true);
            return true;
        });
    }

    @Subscribe
    public void onEvent(SuccessEvent event) {
        switch (event.getScreen()) {
            case SELECT_VIDEO_ACTIVITY:
                searchValue = (String) event.getModel();
                switch (event.getEventType()) {
                    case PLAYER_SEARCH:
//                        callAsyncTask(this, searchValue, TYPE_SEARCH, true);
                        break;
                }
                break;
            case SIGN_IN_UP_HIDDEN_FILES:
                reloadNormalOrHidedVideoList();
                break;
            default:
                switch (event.getEventType()) {
//                    case ALL_VIDEOS_SELECTED:
//                        allVideoSelected((boolean) event.getModel());
//                        break;
                    case ENABLE_EDIT_WHEN_LONG_CLICK_ITEM:
                        showEditView();
                        break;
                }
                break;

        }
    }
//    private void allVideoSelected(boolean isSelectAll) {
//        binding.tvSelectAll.setText(isSelectAll ? R.string.unselect_all : R.string.select_all);
//    }
//    public String getSearchValue() {
//        return searchValue;
//    }
//
//    public void setSearchValue(String searchValue) {
//        this.searchValue = searchValue;
//    }
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
                return loadData(true);
            case TYPE_LOAD_VIDEO_FOR_STUDY_LANGUAGE_FOLDER_LIST:
                runOnUiThread(() -> {
                    binding.header.hideSearchView();
                });
                return loadMediaFolderForStudyLanguageData();
            case TYPE_RELOAD_DATA_FROM_DB:
                List<PlayerFileModel> playerFileModelList = loadData(false);
                updateCurrentVideoModel();
                return playerFileModelList;
            case TYPE_RELOAD_FROM_LIST:
                return fileListTotal;
            case TYPE_DELETE_SELECTED_VIDEOS:
                showDialogConfirmDeleteSelectedVideos((List<PlayerFileModel>) data);
                break;
            case TYPE_CONVERT_TO_HIDDEN_FILES:
                convertToHiddenFiles((List<PlayerFileModel>) data, true);
                break;
            case TYPE_CONVERT_TO_NORMAL_FILES:
                convertToHiddenFiles((List<PlayerFileModel>) data, false);
                break;
            case TYPE_SEARCH:
                filteredFileList = MediaListUtil.generateSearchData((String) data, currentFilesList);
                break;
        }
        return null;
    }
    private void updateCurrentVideoModel() {
        if (currentVideoModel == null)
            return;

        int index = 0;
        for (PlayerFileModel f : currentFilesList) {
            if (f.getVideoModel().getPath().equalsIgnoreCase(currentVideoModel.getPath())) {
                f.setVideoModel(currentVideoModel);
                index = f.getIndex();
                break;
            }
        }
        dalPlayerAdapter.notifyItemChanged(index);
        currentVideoModel = null;
    }
    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
                fileListTotal = ((List<PlayerFileModel>) resultData);
                finishLoadData();
                runOnUiThread(() -> {
                    binding.header.showSearchView();
                });
                sharedPreferences.setPlayerFetchAllVideoFirstTime(false);
                break;
            case TYPE_RELOAD_DATA_FROM_DB:
            case TYPE_RELOAD_FROM_LIST:
                fileListTotal = ((List<PlayerFileModel>) resultData);
                finishLoadData();
                break;
            case TYPE_LOAD_VIDEO_FOR_STUDY_LANGUAGE_FOLDER_LIST:
                finishLoadStudyLanguageVideoFolderData(resultData);
                break;
            case TYPE_CONVERT_TO_HIDDEN_FILES:
            case TYPE_CONVERT_TO_NORMAL_FILES:
//                fileListTotal = ((List<PlayerFileModel>) resultData);
                finishLoadData();
                break;
            case TYPE_SEARCH:
                dalPlayerAdapter.setData(filteredFileList);
                break;
            default:
                break;
        }
        Loading.hide();
    }

    private void showGuideHowToUse() {
        if (sharedPreferences.isFirstShowGuideHowToUseMultiPlayerSelectVideo()) {
            sharedPreferences.setFirstShowGuideHowToUseMultiPlayerSelectVideo();
            showGuideHowToUseFoldersLocations();
        }
        updateFoldersLocationsTextViewVisibility();
    }
    private void showGuideHowToUseFoldersLocations() {
//        if (sharedPreferences.isFirstShowGuideHowToUseSelectVideoFoldersLocationsInMultiPlayer()) {
//            sharedPreferences.setFirstShowGuideHowToUseSelectVideoFoldersLocationsInMultiPlayer();
            String title = getString(R.string.guide_multi_player_how_to_use_select_video_folders);
            GuideUtil.showGuideView(this, title, binding.tvGuide, view -> showGuideHowToUseTopMenu());
//        }
    }
    //다운로드 폴더안에 비디오파일이 있어야 한다는 안내는 3번까지만 하고 숨긴다.
    private void updateFoldersLocationsTextViewVisibility() {
        int minCountToShowTextView = 3;
        int count = sharedPreferences.getCountShowTextViewVideoFoldersLocationsInMultiPlayer();
        if (sharedPreferences.getCountShowTextViewVideoFoldersLocationsInMultiPlayer() < minCountToShowTextView) {
            sharedPreferences.setCountShowTextViewVideoFoldersLocationsInMultiPlayer(++count);
            binding.tvGuide.setVisibility(View.VISIBLE);
        } else {
            binding.tvGuide.setVisibility(View.GONE);
        }
    }

    private void showGuideHowToUseTopMenu() {
//        if (sharedPreferences.isFirstShowGuideHowToUseSelectVideoRefreshInMultiPlayer()) {
//            sharedPreferences.setFirstShowGuideHowToUseSelectVideoRefreshInMultiPlayer();
            String title = getString(R.string.guide_multi_player_how_to_use_select_video_top_menu);
            GuideUtil.showGuideView(this, title, binding.header.getIconRight2(), view -> showGuideHowToUseSelectMultipleMedias());
//        }
    }

    private void showGuideHowToUseSelectMultipleMedias() {
        // 리사이클 뷰의 첫 번째 아이템 뷰를 가져옵니다.
        RecyclerView.ViewHolder itemViewHolder = binding.rvVideo.findViewHolderForAdapterPosition(1);
        if (itemViewHolder != null) {
            View itemView = itemViewHolder.itemView;
            String title = getString(R.string.guide_multi_player_how_to_use_select_multi_videos_by_long_click);
            GuideUtil.showGuideView(this, title, itemView, view -> {});
        }
    }

    private void showGuideHowToUsePlayButtonAfterLongClick() {
        if (sharedPreferences.isFirstShowGuideHowToUsePlayButtonAfterLongClickInMultiPlayer()) {
            sharedPreferences.setFirstShowGuideHowToUsePlayButtonAfterLongClickInMultiPlayer();
            String title = getString(R.string.dialog_message_first_select_multi_videos_from_menu);
            GuideUtil.showGuideView(this, title, binding.btnPlaySelectedMedia, view -> {});
        }
    }

    private List<PlayerFileModel> loadMediaFolderForStudyLanguageData() {
        List<PlayerFileModel> list = new ArrayList<>();
        String mediaFolderPath = StorageUtil.getMediaAbsoluteFolder(this).toLowerCase();
        for(PlayerFileModel f : fileListTotal) {
            if (f.getVideoModel().getPath().toLowerCase().startsWith(mediaFolderPath)) {
                list.add(f);
            }
        }
        return sortFiles(list);
    }

    private void finishLoadStudyLanguageVideoFolderData(Object resultData) {
        DLog.d(getLogTag(), "finishLoadSeasonVideoList");
        setSmallGroupVideoListLoaded_StudyLanguageVideoFolder(true);
        dalPlayerAdapter.setData((List<PlayerFileModel>) resultData);

//        updateHeaderNaviView();
    }
//    public void updateHeaderNaviView() {
////        DLog.d(getLogTag(), "updateHeaderSeasonView");
//        if (isSmallGroupVideoListLoaded_StudyLanguageVideoFolder()) {
//            toolbar.invisibleRight();
//            toolbar.setIconLeft(R.drawable.ic_back);
//            setTitle(PlayerLanguageUtil.getStudyLanguageFolderText(Constant.AppMediaType.VIDEO));
//        } else {
//            toolbar.visibleRight();
//            toolbar.setIconLeft(R.drawable.ic_drawer_menu);
//            toolbar.setTitle("");
//        }
//    }
    private boolean isSmallGroupVideoListLoaded_StudyLanguageVideoFolder() {
        return isSmallGroupVideoListLoaded_StudyLanguageVideoFolder;
    }
    private void callRefreshMethod() {
        VideoModelQuery.updateAllNewFileToFalse(Voca.getRealm());
        VideoModelQuery.updateAllByTrashToTrue(Voca.getRealm());
        previousSelectedFileName = "";
        callAsyncTask(this, null, TYPE_INIT_DATA, true);
    }

    private List<PlayerFileModel> loadData(boolean isLoadAgain) {
        List<PlayerFileModel> list = new ArrayList<>();
        if (isLoadAgain) {
            list.addAll(MediaFileListUtil.addAllVideosFromRootFolder(this,true, false, Constant.AppMediaType.VIDEO));
        } else {
//            list.addAll(MediaFileListUtil.fetchAllVideosFromDBForMultiPlayer());
            List<PlayerFileModel> listFromDb = MediaFileListUtil.fetchAllVideosFromDBForMultiPlayer();
            for (PlayerFileModel model : listFromDb) {
                if (StorageUtil.isFileExist(model.getPath())) {
                    list.add(model);
                } else {
                    VideoModelQuery.deleteByPath(Voca.getRealm(), model.getPath());
                }
            }
        }

        playlistHelper.refreshFilePathsInPlaylist();
        multiPlayerDatabase.refreshFilePathsInTables();

        return sortFilesAndAddLangFolder(list); //StorageUtil.sortFiles(list, sharedPreferences.getPlayerFileSort());
    }

    private List<PlayerFileModel> sortFiles(List<PlayerFileModel> list) {
        return StorageUtil.sortFiles(list, sharedPreferences.getPlayerFileSort());
    }

    @Override
    public void onClick(View view, Object object) {
        PlayerFileModel playerFileModel = (PlayerFileModel) object;
        switch (view.getId()) {
            case R.id.llItem:
                if (dalPlayerAdapter.isEditMode()) {
                    selectVideoItem();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(Constant.BUNDLE.KEY_SELECTED_VIDEO_FILE, playerFileModel);
                    if (!filteredFileList.isEmpty() && (searchValue != null && !searchValue.isEmpty())) {
                        intent.putExtra(Constant.BUNDLE.KEY_ALL_VIDEO_FILES_IN_LIST, (Serializable)MultiPlayerFileListHelper.convertFileListToFilePathList(filteredFileList));
                    } else {
                        intent.putExtra(Constant.BUNDLE.KEY_ALL_VIDEO_FILES_IN_LIST, (Serializable)MultiPlayerFileListHelper.convertFileListToFilePathList(currentFilesList));
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            case R.id.root:
                if (playerFileModel.isLanguageFolder()) {
                    this.callAsyncTask(this, null, TYPE_LOAD_VIDEO_FOR_STUDY_LANGUAGE_FOLDER_LIST, true);
                }
        }
    }

    private void handleShowOrHideSelectedVideos() {
        List<PlayerFileModel> selectedVideoList = dalPlayerAdapter.getSelectedVideoList();
        if (selectedVideoList.isEmpty()) {
            showToastNoSelectedMedia();
            return;
        }
        int selectedCount = selectedVideoList.size();
        selectedVideoList.forEach(item -> onUpdateShowOrHideItem(item));
        selectedVideoList.clear();
        if (binding.header.isSearchStarted()) {
            dalPlayerAdapter.setData(filteredFileList);
        } else {
            dalPlayerAdapter.setData(currentFilesList);
        }
        selectVideoItem();

        // 선택된 파일 수에 따라 올바른 메시지 가져오기
        int toastMessageResId = sharedPreferences.getShowNormalVideoFileList()
                ? R.plurals.toast_moved_selected_videos_to_hidden_list
                : R.plurals.toast_moved_selected_videos_to_normal_list;

        ToastUtil.getInstance(this).show(getResources().getQuantityString(toastMessageResId, selectedCount, selectedCount));
//        ToastUtil.getInstance(this).show(sharedPreferences.getShowNormalVideoFileList() ? R.string.toast_moved_selected_videos_to_hidden_list : R.string.toast_moved_selected_videos_to_normal_list);
    }

    private void onUpdateShowOrHideItem(PlayerFileModel file) {
        DLog.d(getLogTag(), "onUpdateShowHideItem - file=" + file.toString());
        file.getVideoModel().swapHide();
        //비디오디스트에서 숨김비디오나 일반비디오로 갈때는 fileListTotal에서는 제거하면 안된다.
//        final int indexPosition = currentFilesList.indexOf(file);
//        fileListTotal.remove(file);
        filteredFileList.remove(file);
        currentFilesList.remove(file);
        updateVideoModel(file.getVideoModel());
    }
    public void updateVideoModel(VideoModel videoModel) {
        VideoModelQuery.update(Voca.getRealm(), videoModel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnShowHideSelectedMedia:
                handleShowOrHideSelectedVideos();
                break;
            case R.id.btnPlaySelectedMedia:
                playSelectedMedia();
                break;
            case R.id.btnAddItemToPlaylist:
                addSelectedItemsInPlaylist();
                break;
            case R.id.btnMore:
                showMoreDialog();
                break;
//            case R.id.btnSelectAllMedia:
//                boolean isSelectAll = binding.tvSelectAll.getText().equals(getString(R.string.select_all));
//                dalPlayerAdapter.selectAllVideo(isSelectAll);
//                allVideoSelected(isSelectAll);
//                break;
        }
    }
    private void showDialogConfirmDeleteSelectedVideos() {
        List<PlayerFileModel> selectedVideoList = dalPlayerAdapter.getSelectedVideoList();
        int selectedCount = selectedVideoList.size();

        if (selectedCount == 0) {
            ToastUtil.getInstance(this).show(R.string.toast_select_file_first);
            return;
        }
        String message = this.getResources().getQuantityString(
                R.plurals.msg_video_files_confirm_delete, selectedCount, selectedCount);

        YesNoDialog dialog = new YesNoDialog(
                this,
                R.string.confirm,
                message,
                selectedVideoList,
                new OnYesNoClickListener() {
                    @Override
                    public void onYesClick(View view, Object object) {
                        callAsyncTask(MultiPlayerSelectVideoActivity.this, object, TYPE_DELETE_SELECTED_VIDEOS);
                    }

                    @Override
                    public void onNoClick(View view, Object object) {

                    }
                });
        dialog.show();
    }

    private void showDialogConfirmConvertToHiddenFiles(boolean isConvertToHidden) {
        List<PlayerFileModel> selectedVideoList = dalPlayerAdapter.getSelectedVideoList();
        int selectedCount = selectedVideoList.size();

        if (selectedCount == 0) {
            ToastUtil.getInstance(this).show(R.string.toast_select_file_first);
            return;
        }
        @PluralsRes int messageId = isConvertToHidden ? R.plurals.msg_video_files_confirm_convert_to_hidden_files : R.plurals.msg_video_files_confirm_convert_to_normal_files;
        String message = this.getResources().getQuantityString(
                messageId, selectedCount, selectedCount);

        YesNoDialog dialog = new YesNoDialog(
                this,
                R.string.confirm,
                message,
                selectedVideoList,
                new OnYesNoClickListener() {
                    @Override
                    public void onYesClick(View view, Object object) {
                        if (isConvertToHidden) {
                            callAsyncTask(MultiPlayerSelectVideoActivity.this, object, TYPE_CONVERT_TO_HIDDEN_FILES);
                        } else {
                            callAsyncTask(MultiPlayerSelectVideoActivity.this, object, TYPE_CONVERT_TO_NORMAL_FILES);
                        }
                    }

                    @Override
                    public void onNoClick(View view, Object object) {

                    }
                });
        dialog.show();
    }
    private void convertToHiddenFiles(List<PlayerFileModel> data, boolean isToHidden) {
        data.forEach(item -> {
            String oldFilePath = StorageUtil.convertToHiddenFiles(item, isToHidden);
            //convertToHiddenFiles을 하고나면 item.getPath()에는 최신께 들어간다. (실패하거나 선택한 파일이 이미 컨버트된걸수도 있다)
            if ((!oldFilePath.isEmpty()) && (item.getPath() != oldFilePath)) {
                renameRelatedVideoFile(item, oldFilePath);
            }
        });

        ToastUtil.getInstance(this).show(isToHidden ? R.string.toast_converted_to_hidden_files : R.string.toast_converted_to_normal_files);
    }
    private void renameRelatedVideoFile(PlayerFileModel file, String oldFilePath) {
        VideoModelQuery.updateByPath(Voca.getRealm(), oldFilePath, file.getPath());
        VideoModel videoModel = VideoModelQuery.getByPath(Voca.getRealm(), file.getPath());
        if (videoModel != null) {
            updateVideoModel(videoModel);
            file.setVideoModel(videoModel);
            file.setName(videoModel.getName());
        }
        dbHelper.handleFilePathRename(oldFilePath, file.getPath());
    }
    private void showDialogConfirmDeleteSelectedVideos(List<PlayerFileModel> data) {
        deletedFilePos = 0;
        deletedFilesList.clear();
        deletedFilesList.addAll(data);
        data.forEach(item -> {
            deletePlayerFileModelItem(item);
        });
    }
    private void deletePlayerFileModelItem(PlayerFileModel file) {
        boolean isRemoved = StorageUtil.removeVideoFile(this, file.getPath(), intentSenderLauncher);
        //파일을 바로 지울수 있는 권한이 있으면. 권한이 없으면 intentSenderLauncher로 간다.
        if (isRemoved) {
            deleteRelatedVideoFile(file, true);
        }
    }

    private void deleteRelatedVideoFile(PlayerFileModel file, boolean notifyItem) {
        VideoModelQuery.deleteByPath(Voca.getRealm(), file.getPath());
        dbHelper.handleFileDelete(file.getPath());
        boolean deletedInPlaylist = playlistHelper.deleteSelectedItemFromAllPlaylists(file.getPath());
        if (deletedInPlaylist && isShowingPlaylistVideos) {
            updateToolBarTitle();
        }
        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.SELECT_VIDEO_ACTIVITY, BaseEvent.EventType.DELETE_VIDEOS, file.getPath()));
        this.runOnUiThread(() -> updateUIDeletePlayerFileModelItem(file, notifyItem));
    }
    private void updateUIDeletePlayerFileModelItem(PlayerFileModel file, boolean notifyItem) {
        final int indexPosition = currentFilesList.indexOf(file);
        fileListTotal.remove(file);
        currentFilesList.remove(file);
        filteredFileList.remove(file);
//        getLastPlayedVideo();
        if (notifyItem) {
            binding.rvVideo.post(() -> dalPlayerAdapter.notifyItemRemoved(indexPosition));
        }
        Loading.hide();
    }

    public void addSelectedItemsInPlaylist() {
        List<PlayerFileModel> selectedVideos = new ArrayList<>(dalPlayerAdapter.getSelectedVideoList());
        if (selectedVideos.isEmpty()) {
            showToastNoSelectedMedia();
            return;
        }

        if (playlistHelper.hasPlaylist()) {
            playlistHelper.addSelectedItemsInPlaylist(selectedVideos);
        } else {
            playlistHelper.createPlaylist();
        }
    }

    private void playSelectedMedia() {
        ArrayList<PlayerFileModel> selectedVideos = new ArrayList<>(dalPlayerAdapter.getSelectedVideoList());
        if (selectedVideos.isEmpty()) {
            showToastNoSelectedMedia();
            return;
        }
        sharedPreferences.setShowNormalVideoFileList(true);
        Intent intent = new Intent();
        intent.putExtra(Constant.BUNDLE.KEY_SELECTED_VIDEO_FILES, selectedVideos);
        if (!filteredFileList.isEmpty() && (searchValue != null && !searchValue.isEmpty())) {
            intent.putExtra(Constant.BUNDLE.KEY_ALL_VIDEO_FILES_IN_LIST, (Serializable)MultiPlayerFileListHelper.convertFileListToFilePathList(filteredFileList));
        } else {
            intent.putExtra(Constant.BUNDLE.KEY_ALL_VIDEO_FILES_IN_LIST, (Serializable)MultiPlayerFileListHelper.convertFileListToFilePathList(currentFilesList));
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    private void showToastNoSelectedMedia() {
        ToastUtil.getInstance(this).show(R.string.toast_select_videos_first);
    }

    protected void finishLoadData() {
        if (isShowingPlaylistVideos && !selectedPlaylists.isEmpty()) {
            Map<String, Boolean> videosInPlaylist = new HashMap<>();
            for (PlaylistModel model : selectedPlaylists) {
                videosInPlaylist.putAll(model.getFilePathsAsMap());
            }
            currentFilesList.clear();
            for (PlayerFileModel mode : fileListTotal) {
                if (videosInPlaylist.containsKey(mode.getPath())) {
                    currentFilesList.add(mode);
                }
            }
        } else {
            if (!sharedPreferences.getShowNormalVideoFileList()) {
                currentFilesList = fileListTotal.stream()
                        .filter(model -> model.getVideoModel().isHide())
                        .collect(Collectors.toList());
            } else {
                currentFilesList = fileListTotal.stream()
                        .filter(model -> !model.getVideoModel().isHide())
                        .collect(Collectors.toList());
            }
        }
        dalPlayerAdapter.setData(currentFilesList);
        if (binding.rvVideo != null && binding.rvVideo.getAdapter() != null && binding.rvVideo.getAdapter().getItemCount() > 0) {
            binding.rvVideo.scrollToPosition(0);
            ScrollToItemHelper.scrollToFile(previousSelectedFileName, currentFilesList, binding.rvVideo);
        }
        showGuideHowToUse();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.setShowNormalVideoFileList(true);
        new PlaylistBackupHelper(this).backupPlaylist();
    }
}
