package com.dalread.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;

import com.dalread.R;
import com.dalread.base.BaseActivity;
import com.dalread.component.Toolbar;
import com.dalread.database.sqlite.MultiPlayerDatabase;
import com.dalread.database.sqlite.model.MultiPlayerVideoModel;
import com.dalread.databinding.ActivityMultiplePlayerBinding;
import com.dalread.dialog.InfoDialog;
import com.dalread.dialog.MultiPlayerAllVideosDialog;
import com.dalread.dialog.MultiPlayerSettingDialog;
import com.dalread.dialog.SelectRowColumnDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.helper.MultiPlayerFileListHelper;
import com.dalread.helper.MultiPlayerFragmentListHelper;
import com.dalread.helper.MultiplePlayerDbHelper;
import com.dalread.helper.MultiplePlayerScreenHelper;
import com.dalread.database.sqlite.model.MultiPlayerPlaylistModel;
import com.dalread.helper.MultiPlayerPlaylistHelper;
import com.dalread.helper.point.MultiPlayerPointHelper;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.VideoModel;
import com.dalread.util.AraRandomUtil;
import com.dalread.util.AraScreenSecureUtils;
import com.dalread.util.CollectionUtil;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.DialogUtil;
import com.dalread.util.DoubleBackPressHandler;
import com.dalread.util.GuideUtil;
import com.dalread.util.Loading;
import com.dalread.util.MediaFileListUtil;
import com.dalread.util.MultiPlayerSqliteExportHelper;
import com.dalread.util.PlayerFileModelUtil;
import com.dalread.util.PlaylistBackupHelper;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MultiplePlayerActivity extends BaseActivity {
    public ActivityMultiplePlayerBinding binding;
    MultiplePlayerDbHelper dbHelper;
    private MultiplePlayerScreenHelper screenHelper;
    private MultiPlayerFragmentListHelper fragmentListHelper;
    public MultiPlayerPointHelper pointHelper;
    private MultiPlayerDatabase multiPlayerDatabase;
    int numberOfScreens;
    private int row;
    private int column;
    boolean isLoadLastWatchedVideos;
    int myOrientation;
    private DoubleBackPressHandler doubleBackPressHandler;
    private boolean isTabLayoutVisible = true;
    public MultiPlayerPlaylistHelper multiPlayerPlaylistHelper;
    private boolean isAllVideosPlay = true;
    private boolean isAllVideosPause = true;
    private boolean isSomeVideosPlay = false;

    private boolean isAllVideosUnmuted = false;
    private boolean isAllVideosMuted = true;
    private boolean isSomeVideosMuted = false;
    List<Integer> fixedScreenIdListForRandomPlay = new ArrayList<>();
    public List<MultiplePlayerFragment> fragmentList = new ArrayList<>();
    private List<MultiplePlayerFragment> duplicatedFragments = new ArrayList<>();
    private int fullScreenFragmentIndex = -1;

    //    protected List<PlayerFileModel> allRandomFileList = new ArrayList<>();
    protected List<String> listAllRandomFilePath = new ArrayList<>();
    private boolean isAutoRandomPlay = false;
    private boolean isFirstToastForAllVideosMuted = true;
    /** 저장된 레이아웃에서 로드된 상태일 때만 양수. AB/재생위치 저장 시 screens_in_stored_layout 갱신에 사용 */
    private int loadedLayoutId = -1;

    public int getLoadedLayoutId() {
        return loadedLayoutId;
    }

    @Override
    protected View getContentView() {
        binding = ActivityMultiplePlayerBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }

//    @Override
//    public void onHeaderLeftClick() {
//
//    }
//
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
//
//    @Override
//    public void onHeaderTextRightClick() {
//
//    }

    void showGuideHowToUse() {
        if (sharedPreferences.isFirstShowGuideHowToUseMultiPlayerActivity()) {
            sharedPreferences.setFirstShowGuideHowToUseMultiPlayerActivity();
            showGuideHowToUseMultiPlayMenu();
        }
    }
    public void showGuideHowToUseMultiPlayMenu() {
        //이건 binding.llTopMenu를 정확하게 가르키지 못한다.
        String title = getString(R.string.guide_multi_player_how_to_use_multi_play_top_menu);
        GuideUtil.showGuideView(this, title, binding.ivAllVideosMenu, view -> showGuideHowToUseMultiPlayHideMenu());
    }

    public void showGuideHowToUseMultiPlayHideMenu() {
        String title = getString(R.string.guide_multi_player_how_to_use_multi_play_top_menu_hide);
        GuideUtil.showGuideView(this, title, binding.hideTopMenu, view -> {});
    }

    private void drawFragment() {
        binding.gridLayout.removeAllViews();
        Pair<Integer, Integer> rowsAndColumns = screenHelper.calculateRowsAndColumns();
        int rows = rowsAndColumns.first;
        int columns = rowsAndColumns.second;

        binding.gridLayout.setColumnCount(columns);
        binding.gridLayout.setRowCount(rows);

        for (int i = 0; i < numberOfScreens; i++) {
            int containerId = addFragmentContainerView(rows, columns);
            initPlayerFragment(containerId, i, isLoadLastWatchedVideos);
        }
    }

    private void rotateFragment() {
        screenHelper.getScreenSize(); //이게 없으면 로테이트 해도 원래 자리로 다시 돌아온다.
        updateGridLayoutParams();
        for (MultiplePlayerFragment fragment : fragmentList) {
            fragment.setOrientation(screenHelper.isOrientationVertical);
        }
    }

    // 여러 비디오를 선택하면 맨 처음 프래그먼트부터 선택된 비디오들로 다시 채워준다.
    public void updateVideosAllFragments(List<PlayerFileModel> selectedVideosFromIntent, List<String> currentVideoFilePathList) {
        int videoIndex = 0;

        int limit = Math.min(fragmentList.size(), selectedVideosFromIntent.size());
        List<PlayerFileModel> limitedSelectedVideos = selectedVideosFromIntent.subList(0, limit);
        int numVideos = limitedSelectedVideos.size();

        // 먼저, isVideoLoaded가 false인 프래그먼트부터 밑으로 쭉 채운다. (기존에 있어도 채운다)
        boolean isForceLoad = false;
        int fragmentIndex = 0;
        for (MultiplePlayerFragment fragment : fragmentList) {
            if (isForceLoad || (!fragment.isVideoLoaded && videoIndex < numVideos)) {
                isForceLoad = true;
                if (videoIndex < numVideos) {
                    PlayerFileModel playerFileModel = limitedSelectedVideos.get(videoIndex++);
                    loadVideoInFragment(fragment, fragmentIndex, playerFileModel.getPath());
                    fragment.setPinScreen(true);
                    fragment.setCurrentVideoFilePathList(currentVideoFilePathList);
                }
            }
            fragmentIndex++;
        }

        // 다 채운 후, 처음부터 다시 위에서 채운 Fragment전까지 채운다.
        if (videoIndex < numVideos) {
            fragmentIndex = 0;
            for (MultiplePlayerFragment fragment : fragmentList) {
                if (videoIndex >= numVideos) break;
                PlayerFileModel playerFileModel = limitedSelectedVideos.get(videoIndex++);
                loadVideoInFragment(fragment, fragmentIndex, playerFileModel.getPath());
                fragment.setPinScreen(true);
                fragment.setCurrentVideoFilePathList(currentVideoFilePathList);
                fragmentIndex++;
            }
        }
        // 비디오 선택 후 복귀 시 멈춘 상태 유지 (자동 재생하지 않음)
        new Handler().postDelayed(() -> {
            pauseAllVideos();
            refreshAllSpeakerIcon();
        }, 1000);
    }

    public void updateVideosAllFragmentsByStoredLayout(int rotateLayout) {
        List<MultiPlayerVideoModel> list = multiPlayerDatabase.getCurrentScreenModels();
        if (list.isEmpty()) {
            return;
        }
        closeAllVideosWithoutClearDb();
        myOrientation = rotateLayout;
        rotateOrientationMain();
        int fragmentIndex = 0;
        numberOfScreens = list.size();
        screenCountChanged();

        //자동 실행을 하면 전제 플레이 아이콘이 정지가 아니고 플레이 아이콘으로 보여서 1초 뒤에 한다.
        new Handler().postDelayed(() -> {
            //각 스크린별 currentVideoFilePathList를 업데이트 해준다.
            setCurrentVideoFilePathList(list.stream()
                    .map(MultiPlayerVideoModel::getFILE_PATH)
                    .collect(Collectors.toList()));
            playAllVideos();
            refeshAllIconsForcedPlay();
        }, 1000);
    }
    private void refeshAllIconsForcedPlay() {
        updateAllVideosPlayImage(true, false);
//        updateAllPlayImageToPause();
        refreshAllSpeakerIcon();
    }

    private void updateAllVideosPlayImage(boolean isAllVideosPlayLocal, boolean isAllVideosPauseLocal) {
        isAllVideosPlay = isAllVideosPlayLocal;
        isAllVideosPause = isAllVideosPauseLocal;
        isSomeVideosPlay = !isAllVideosPlay && !isAllVideosPause;
        updateAllPlayPauseImage();
    }

    private void updateGridLayoutParams() {
        Pair<Integer, Integer> rowsAndColumns = screenHelper.calculateRowsAndColumns();
        int rows = rowsAndColumns.first;
        int columns = rowsAndColumns.second;

        for (int i = 0; i < numberOfScreens; i++) {
            View view = binding.gridLayout.getChildAt(i);
            if (view instanceof FragmentContainerView) {
                view.setLayoutParams(getLayoutParams(rows, columns));
            }
        }

        binding.gridLayout.setColumnCount(columns);
        binding.gridLayout.setRowCount(rows);
    }

    private void rotateOrientation() {
        int currentOrientation = getRequestedOrientation();

        switch (currentOrientation) {
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
            case ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT:
                myOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                myOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                myOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
            default:
                myOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
        }
        rotateOrientationMain();
    }

    private void rotateOrientationMain() {
        //updateVideosAllFragmentsByStoredLayout에서는 그냥 하면 한번은 제대로 회전이 되는데, 두번째부터는 계속 가로모드라서 postDelayed로 한다.
        new Handler().postDelayed(() -> setRequestedOrientation(myOrientation), 500);
//        setRequestedOrientation(myOrientation);
        sharedPreferences.setMultiPlayerScreenOrientation(myOrientation);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        rotateFragment();
    }
    private void hideTopMenu() {
//        if (isAnyVideoLoaded()) {
        Utils.toggleFullscreenMultiPlayer(this, true);
            binding.llTopMenu.setVisibility(View.GONE);

            isTabLayoutVisible = false;
        updateGridLayoutParams();
        // 네비 숨김 후 insets 재적용되어 하단/우측 패딩 0으로 갱신되도록 요청 (s/w 네비 시 회색 영역 방지)
        binding.getRoot().post(() -> ViewCompat.requestApplyInsets(binding.getRoot()));
            if (sharedPreferences.isFirstHideMultiPlayerScreenTabLayout()) {
                DialogUtil.showPositiveDialog(context, context.getString(R.string.info), context.getString(R.string.dialog_message_first_hide_multi_player_tab_layout), context.getString(R.string.ok), () -> {
                    sharedPreferences.setFirstHideMultiPlayerScreenTabLayout();
                });
            }
//        } else {
//            ToastUtil.getInstance(this).show(R.string.toast_to_hide_top_menu_open_video);
//        }
    }
    private void showTopMenu() {
        Utils.toggleFullscreenMultiPlayer(this, false);
        binding.llTopMenu.setVisibility(View.VISIBLE);
        isTabLayoutVisible = true;
        updateGridLayoutParams();
        ViewCompat.requestApplyInsets(binding.getRoot());
        ToastUtil.getInstance(this).show(R.string.toast_show_top_menu);
    }
    @Override
    public void onBackPressed() {
        if (fullScreenFragmentIndex >= 0) {
            exitFullScreen();
            return;
        }
        if (!isTabLayoutVisible) {
            showTopMenu();
        } else {
            if (isAnyVideoLoaded()) {
                doubleBackPressHandler.onBackPressed();
            } else {
                super.onBackPressed();
            }
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AraScreenSecureUtils.enableSecureFlag(this);
        int intentRow = getIntent().getIntExtra(Constant.BUNDLE.KEY_ROW, -1);
        int intentColumn = getIntent().getIntExtra(Constant.BUNDLE.KEY_COLUMN, -1);
        if (intentRow >= 1 && intentRow <= 5 && intentColumn >= 1 && intentColumn <= 5) {
            row = intentRow;
            column = intentColumn;
        } else {
            row = sharedPreferences.getMultiPlayerRow();
            column = sharedPreferences.getMultiPlayerColumn();
        }
        numberOfScreens = row * column;
        isLoadLastWatchedVideos = getIntent().getBooleanExtra(Constant.BUNDLE.KEY_LOAD_LAST_WATCHED_VIDEO_MULTIPLE_PLAYER, false);
        initHelper();
        myOrientation = sharedPreferences.getMultiPlayerScreenOrientation();
        // 앱 설치 후 처음 그리드 뷰 진입 시 저장된 값이 없으면 가로(랜드스케이프)를 기본으로 사용
        if (myOrientation == -1) {
            myOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }
        setRequestedOrientation(myOrientation);

        // s/w 네비게이션 바가 있을 때 전체 화면에서 하단(또는 가로 시 우측) 회색 영역 방지: 엣지투엣지로 그린 뒤 WindowInsets로 패딩만 적용
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(binding.getRoot());

        binding.llTopMenu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.llTopMenu.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // llTopMenu의 측정이 완료되면 drawFragment 호출, 그냥 drawFragment를 호출하면 binding.llTopMenu.getHeight()이 0으로 나와서 getViewTreeObserver를 먼저 호출한다.
                drawFragment();
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void initHelper() {
        pointHelper = new MultiPlayerPointHelper(this);
        doubleBackPressHandler = new DoubleBackPressHandler(this, getString(R.string.toast_double_press_back_button_to_move_previous_screen));
        dbHelper = new MultiplePlayerDbHelper(this);
        screenHelper = new MultiplePlayerScreenHelper(this, row, column);
        fragmentListHelper = new MultiPlayerFragmentListHelper(this);
        initSubDatabase();
        multiPlayerPlaylistHelper = new MultiPlayerPlaylistHelper(this, multiPlayerDatabase);
    }

    private void initSubDatabase() {
        multiPlayerDatabase = dbHelper.initSubDatabase(multiPlayerDatabase);
    }

    public MultiPlayerDatabase getMultiPlayerDatabase() {
        return multiPlayerDatabase;
    }

    public void onMenuButtonClick(View view) {
        switch (view.getId()) {
            case R.id.hideTopMenu:
                hideTopMenu();
                break;
            case R.id.ivRotateVideo:
                rotateOrientation();
                break;
            case R.id.ibAllSpeaker:
                onAllSpeaker();
                break;
            case R.id.ibRandomPlay:
                List<PlayerFileModel> fileListTotal = MediaFileListUtil.fetchAllVideosFromVideoMetaForMultiPlayer(this, multiPlayerDatabase);
                if (multiPlayerPlaylistHelper.isRandomPlayPossible(fileListTotal)) {
                    pauseAllVideos();
                    getFixedScreenIdList();
                    resetAutoRandom();
                    if (isAnyVideoLoaded()) {
                        showFixedScreenIdSelectionDialog(numberOfScreens, fixedScreenIdListForRandomPlay, fileListTotal);
                    } else {
                        choosePlaylistToPlayRandomVideos(fixedScreenIdListForRandomPlay, fileListTotal);
                    }
                } else {
                    DialogUtil.showPositiveDialog(context, context.getString(R.string.info), context.getString(R.string.msg_no_videos_for_random_play), context.getString(R.string.ok), () -> {});
                }
                break;
            case R.id.ibAllPlayPause:
                onAllPlayPause();
                break;
            case R.id.ivAllVideosMenu:
                pauseAllVideos();
                showAllVideosMenu();
                break;
        }
    }

    public void setAllPinScreen(boolean value) {
        isAutoRandomPlay = false;
        for (MultiplePlayerFragment fragment : fragmentList) {
            fragment.setPinScreen(value);
        }
    }

    private void resetAutoRandom() {
        isAutoRandomPlay = false;
        for (MultiplePlayerFragment fragment : fragmentList) {
            fragment.setAutoRandomPlay(false);
//            fragment.setPinScreen(false);
        }
    }

    private void undoResetAutoRandom() {
        isAutoRandomPlay = true;
        for (MultiplePlayerFragment fragment : fragmentList) {
            fragment.setAutoRandomPlay(true);
            //fragment.setPinScreen(false); //이건 팝업에서 다시 지정해주기 때문에 괜찮다.
        }
    }

    private void onAllSpeaker() {
        if (isAllVideosMuted) {
            unmuteAllVideos();
        } else {
            muteAllVideos();
        }
    }

    private void onAllPlayPause() {
        if (isAllVideosPause) {
            playAllVideos();
        } else {
            pauseAllVideos();
        }
    }
    public void showAllVideosMenu() {
        MultiPlayerAllVideosDialog dialog = new MultiPlayerAllVideosDialog(this, new OnClickDialogListener() {
            @Override
            public void onClick(View view, Object object) {
                switch (view.getId()) {
                    case R.id.llSettings:
                        MultiPlayerSettingDialog dialog = new MultiPlayerSettingDialog(MultiplePlayerActivity.this, null);
                        dialog.show();
                        break;
                    case R.id.llChooseScreenNumberOfScreens:
                        showChooseNumberOfMultiplePlayerDialog();
                        break;
                    case R.id.tvHideAllVideosUI:
                        hideAllVideosUI();
                        break;
                    case R.id.tvCloseAllVideos:
                        askAndCloseAllVideos();
                        break;
                    case R.id.tvResizeAllVideos:
                        resizeAllVideos();
                        break;
                    case R.id.tvSelectVideosToPlay:
                        addVideos();
                        break;
                    case R.id.tvPlayAllVideosABRepeat:
                        playAbRepeatAllVideos();
                        break;
                    case R.id.tvMuteAllVideos:
                        muteAllVideos();
                        break;
                    case R.id.tvUnmuteAllVideos:
                        unmuteAllVideos();
                        break;
                    case R.id.tvPlayAllVideosIntervals:
                        playAllVideosIntervals();
                        break;
                    case R.id.tvMoveAllVideosToStart:
                        moveAllVideosToStart();
                        break;
                    case R.id.llSwapScreens:
                        fragmentListHelper.showIndexInputDialog();
                        break;
                    case R.id.llScreenStoreLayout:
                        handleScreenStoredLayout();
                        break;
                    case R.id.llExportSqlite:
                        MultiPlayerSqliteExportHelper.exportToDownloads(MultiplePlayerActivity.this);
                        break;
                }
            }

            @Override
            public void onDismiss(View view, Object object) {

            }
        });
        dialog.show();
    }

    private void handleScreenStoredLayout() {
        List<MultiPlayerVideoModel> modelList = new ArrayList<>();
        for (MultiplePlayerFragment fragment : fragmentList) {
            if (fragment != null) {
                MultiPlayerVideoModel model = fragment.getModel();
                if (model != null) {
                    //경로명이 없는거도 넣어줘야 다시 저장된 레이아웃을 불러올때 원래랑 동일하게 불러온다.
                    modelList.add(model);
                }
            }
        }
        Intent intent = MultiPlayerStoredVideosActivity.createIntent(MultiplePlayerActivity.this, modelList);
        launchMultiPlayerStoredVideosActivity.launch(intent);
    }

    private void showChooseNumberOfMultiplePlayerDialog() {
        SelectRowColumnDialog dialog = new SelectRowColumnDialog(this, row, column, new OnClickDialogListener() {
            @Override
            public void onClick(View view, Object object) {
                if (object instanceof Pair) {
                    @SuppressWarnings("unchecked")
                    Pair<Integer, Integer> pair = (Pair<Integer, Integer>) object;
                    row = pair.first;
                    column = pair.second;
                    numberOfScreens = row * column;
                    sharedPreferences.setMultiPlayerRow(row);
                    sharedPreferences.setMultiPlayerColumn(column);
                    screenHelper.setRowColumn(row, column);
                    closeAllVideosWithoutClearDb();
                    isLoadLastWatchedVideos = true;
                    fragmentList.clear();
                    drawFragment();
                }
            }

            @Override
            public void onDismiss(View view, Object object) {

            }
        });
        dialog.show();
    }

    private void screenCountChanged() {
        row = sharedPreferences.getMultiPlayerRow();
        column = sharedPreferences.getMultiPlayerColumn();
        numberOfScreens = row * column;
        screenHelper.setRowColumn(row, column);
        closeAllVideosWithoutClearDb();
        isLoadLastWatchedVideos = true;
        fragmentList.clear();
        drawFragment();
    }

    private void addVideos() {
        if (sharedPreferences.isFirstSelectMultiVideosFromMenu()) {
            sharedPreferences.setFirstSelectMultiVideosFromMenu();
            DialogUtil.showPositiveDialog(context, context.getString(R.string.info), context.getString(R.string.dialog_message_first_select_multi_videos_from_menu), context.getString(R.string.ok), () -> launchVideoPicker());
        } else {
            launchVideoPicker();
        }
    }

    private void launchVideoPicker() {
        Intent intent = new Intent(this, MultiPlayerSelectVideoActivity.class);
        videoPickerLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> videoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            List<PlayerFileModel> selectedVideosFromIntent = new ArrayList<>();
                            if (data.hasExtra(Constant.BUNDLE.KEY_SELECTED_VIDEO_FILE)) {
                                PlayerFileModel playerFileModel = data.getParcelableExtra(Constant.BUNDLE.KEY_SELECTED_VIDEO_FILE);
                                selectedVideosFromIntent.add(playerFileModel);
                            } else if (data.hasExtra(Constant.BUNDLE.KEY_SELECTED_VIDEO_FILES)) {
                                selectedVideosFromIntent = data.getParcelableArrayListExtra(Constant.BUNDLE.KEY_SELECTED_VIDEO_FILES);
                            }
                            if (selectedVideosFromIntent.size() > 0) {
                                PlayerFileModelUtil.setNewFileToFalse(selectedVideosFromIntent);
                                List<String> currentFilesList = (ArrayList<String>) data.getSerializableExtra(Constant.BUNDLE.KEY_ALL_VIDEO_FILES_IN_LIST);
                                updateVideosAllFragments(selectedVideosFromIntent, currentFilesList);
                            }
                        }
                        // 처리할 로직
                    }
                }
            }
    );

    private ActivityResultLauncher<Intent> launchMultiPlayerStoredVideosActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            loadedLayoutId = data.getIntExtra(Constant.BUNDLE.KEY_MULTI_PLAYER_SCREEN_STORED_LAYOUT_ID, -1);
                            if (data.hasExtra(Constant.BUNDLE.KEY_MULTI_PLAYER_SCREEN_STORED_LAYOUT_ROTATE_LAYOUT)) {
                                int rotateLayout = data.getIntExtra(Constant.BUNDLE.KEY_MULTI_PLAYER_SCREEN_STORED_LAYOUT_ROTATE_LAYOUT, -1);
                                updateVideosAllFragmentsByStoredLayout(rotateLayout);
                            }
                        }
                        // 처리할 로직
                    }
                }
            }
    );

    private void moveAllVideosToStart() {
        fragmentList.forEach(fragment -> fragment.moveToStart());
    }

    private void playAllVideosIntervals() {
        if (isAnyVideoLoaded()) {
            SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(MultiplePlayerActivity.this);
            singleChoiceDialog.show(
                    R.string.dialog_title_play_all_interval,
                    getVideosIntervals(),
                    10,
                    R.string.ok,
                    R.string.cancel,
                    new OnClickDialogListener() {
                        @Override
                        public void onClick(View view, Object object) {
                            int which = (int) object;
                            final float seconds = which * 0.1f;
                            final int delayMilliSecond = (int) (seconds * 1000);
                            hideAllVideosUI();
                            pauseAllVideos();
                            playAllVideosIntervals(delayMilliSecond);
                        }

                        @Override
                        public void onDismiss(View view, Object object) {
                        }
                    });
        } else {
            showToastNoVideosLoaded();
        }
    }

    @NonNull
    private CharSequence[] getVideosIntervals() {
        final int numberOfItems = 51; // (5.0 - 0.0) / 0.1 + 1
        final CharSequence[] items = new CharSequence[numberOfItems];
        for (int i = 0; i < numberOfItems; i++) {
            float seconds = i * 0.1f;
            items[i] = getString(R.string.second_format_float, seconds);
        }
        return items;
    }

    void refreshAllSpeakerIcon() {
        checkAllFragmentsMuted();
        updateAllSpeakerImage();
    }
    private void updateisAllVideosUnmuted(boolean value) {
        isAllVideosUnmuted = value;
    }
    private void updateisAllVideosMuted(boolean value) {
        isAllVideosMuted = value;
    }
    private void updateisSomeVideosMuted(boolean value) {
        isSomeVideosMuted = value;
    }
    private void checkAllFragmentsMuted() {
        int countVideoLoaded = 0;
        int countVideoUnmuted = 0;
        int countVideoMuted = 0;
        for (MultiplePlayerFragment fragment : fragmentList) {
            if ((fragment != null) && (fragment.exoPlayer != null) && (!fragment.getModel().isFilePathEmpty())) {
                countVideoLoaded++;
                if (fragment.isMuted()) {
                    countVideoMuted++;
                } else {
                    countVideoUnmuted++;
                }
            }
        }
        updateisAllVideosUnmuted(countVideoLoaded == countVideoUnmuted);
        updateisAllVideosMuted(countVideoLoaded == countVideoMuted);
        updateisSomeVideosMuted(!isAllVideosMuted && !isAllVideosUnmuted);
    }

    private void updateAllSpeakerImage() {
        int drawableId = isAllVideosMuted ? R.drawable.baseline_volume_off_24 : (isSomeVideosMuted ? R.drawable.outline_volume_down_24 : R.drawable.baseline_volume_up_24);
        //이 아이콘들은 형태가 동일하지 않아서 쓰지를 못한다.
//        int drawableId = isAllVideosMuted ? R.drawable.ic_speaker_mute : (isSomeVideosMuted ? R.drawable.ic_speaker_part : R.drawable.ic_speaker_all);
        binding.ibAllSpeaker.setImageDrawable(AppCompatResources.getDrawable(this, drawableId));
    }

    void refreshAllPlayPauseIcon() {
        checkAnyVideoPlaying();
        updateAllPlayPauseImage();
    }

    boolean checkAnyVideoPlaying() {
        int countVideoLoaded = 0;
        int countVideosPlay = 0;
        int countVideosPause = 0;
        for (MultiplePlayerFragment fragment : fragmentList) {
            if ((fragment != null) && (fragment.exoPlayer != null) && (!fragment.getModel().isFilePathEmpty())) {
                countVideoLoaded++;
                if (fragment.exoPlayer.isPlaying()) {
                    countVideosPlay++;
                } else {
                    countVideosPause++;
                }
            }
        }
        updateAllVideosPlayImage(countVideoLoaded == countVideosPlay, countVideoLoaded == countVideosPause);
        return isSomeVideosPlay || isAllVideosPlay;
    }

    void updateAllPlayPauseImage() {
        int drawableId = isAllVideosPlay ? R.drawable.baseline_pause_36 : (isSomeVideosPlay ? R.drawable.outline_play_arrow_36 : R.drawable.baseline_play_arrow_36);
        binding.ibAllPlayPause.setImageDrawable(AppCompatResources.getDrawable(this, drawableId));
    }

    void duplicateToSelectedScreens(MultiPlayerVideoModel model) {
        List<String> currentVideoFilePathList = MultiPlayerFileListHelper.getCurrentVideoFilePathListFromModel(model, fragmentList);

        String[] items = IntStream.range(0, fragmentList.size())
                .mapToObj(i -> String.valueOf(i + 1))
                .toArray(String[]::new);

        boolean[] checkedItems = new boolean[fragmentList.size()];
        Arrays.fill(checkedItems, true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MultiMultiChoiceDialog);
        builder.setTitle(R.string.dialog_multi_player_choose_screen_to_load_same_videos)
                .setMultiChoiceItems(items, checkedItems, (dialog, indexSelected, isChecked) -> {
                    checkedItems[indexSelected] = isChecked;
                })
                .setPositiveButton(R.string.select, (dialog, id) -> {
                    Loading.show(this);

                                            new Handler().postDelayed(() -> {
                            int minSize = Math.min(fragmentList.size(), checkedItems.length);
                            clearDuplicatedFragments();
                            for (int i = 0; i < minSize; i++) {
                                // checkedItems[i]가 true인 것만 함.
                                //원래는 선택한 화면의 비디오는 그냥 쓸려고 했는데, 가끔 화면에 로드한 비디오와 화면동기화가 안될때가 있어서 전부 다함.
                                if (checkedItems[i]) {
                                    MultiplePlayerFragment fragment = fragmentList.get(i);
                                    duplicatedFragments.add(fragment);
                                    MultiPlayerVideoModel modelTemp = model.clone();
                                    modelTemp.setSCREEN_ID(fragment.getModel().getSCREEN_ID());
                                    multiPlayerDatabase.insertOrUpdateCurrentScreenOnly(modelTemp);
                                    fragment.setDuplicatedFragment(true);
                                    fragment.setModel(modelTemp);
                                    fragment.setCurrentVideoFilePathList(currentVideoFilePathList);
                                    fragment.initExoPlayer();
                                }
                            }
                            Loading.hide();
                        }, 100);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    int i = 0;
                })
                .setOnCancelListener(dialog -> {
                    int i = 0;
                })
                .create()
                .show();
    }

    void playAllVideosIntervals(int delayMilliSecond) {
        for (int i = 0; i < fragmentList.size(); i++) {
            updateAllVideosPlayImage(true, false);
            final int index = i;
            handler.postDelayed(() -> {
                fragmentList.get(index).forcePlayFromAllVideos();
            }, delayMilliSecond * i);
        }

        if (isAllVideosMuted && isFirstToastForAllVideosMuted) {
            isFirstToastForAllVideosMuted = false;
            ToastUtil.getInstance(this).show(R.string.toast_all_videos_muted);
        }
    }

    void playAllVideos() {
        if (isAnyVideoLoaded()) {
            hideAllVideosUI();
            playAllVideosIntervals(0);
        } else {
            showToastNoVideosLoaded();
        }
    }
    void pauseAllVideos() {
        updateAllVideosPlayImage(false, true);
        fragmentList.forEach(fragment -> fragment.pausePlayerFromAllVideos());
    }

    void clearDuplicatedFragments() {
        fragmentList.forEach(fragment -> fragment.setDuplicatedFragment(false));
        duplicatedFragments.clear();
    }

    void playDuplicatedFragments() {
        //duplicatedFragments는 duplicatedFragments모두가 다 로드되었을때 재생한다. 안그러면 동기화가 안된다.
        for (int i = 0; i < duplicatedFragments.size(); i++) {
            if (!duplicatedFragments.get(i).isVideoLoaded) {
                return;
            }
        }
        new Handler().postDelayed(() -> {
            for (int i = 0; i < duplicatedFragments.size(); i++) {
                updateAllVideosPlayImage(true, false);
                final int index = i;
                handler.postDelayed(() -> {
                    duplicatedFragments.get(index).forcePlayFromAllVideos();
                }, 0);
            }
        }, 100);
    }
    void setCurrentVideoFilePathList(List<String> list) {
        fragmentList.forEach(fragment -> fragment.setCurrentVideoFilePathList(list));
    }
    void hideAllVideosUI() {
        fragmentList.forEach(fragment -> fragment.hideAllControl());
    }
    void resizeAllVideos() {
        fragmentList.forEach(fragment -> fragment.resizeMode());

        //아래부분은 ok일때 다이얼로그가 사라진다. 안사라져야함. 그리고 선택한거랑 resize mode가 맞는지 봐야함.
//        SelectResizeDialog dialog = new SelectResizeDialog(this, new OnClickDialogListener() {
//            @Override
//            public void onClick(View view, Object object) {
//                final int pos = (int) object;
//                fragmentList.forEach(fragment -> fragment.resizeMode(pos));
//            }
//
//            @Override
//            public void onDismiss(View view, Object object) {
//
//            }
//        });
//        dialog.show();
    }
    void muteAllVideos() {
        updateisAllVideosUnmuted(false);
        updateisAllVideosMuted(true);
        updateisSomeVideosMuted(false);
        fragmentList.forEach(fragment -> fragment.muteVolume(false));
//        ToastUtil.getInstance(this).show(R.string.toast_all_videos_muted);
        updateAllSpeakerImage();
    }
    void unmuteAllVideos() {
        updateisAllVideosUnmuted(true);
        updateisAllVideosMuted(false);
        updateisSomeVideosMuted(false);
        fragmentList.forEach(fragment -> fragment.unmuteVolume(false));
        isFirstToastForAllVideosMuted = false;
//        ToastUtil.getInstance(this).show(R.string.toast_all_videos_unmuted);
        updateAllSpeakerImage();
    }
    public void refresh4ButtonsTransparency() {
        fragmentList.forEach(fragment -> fragment.show4Buttons(false));
    }
    public void hideAllVideos4Buttons() {
        fragmentList.forEach(fragment -> {
            fragment.hide4Buttons();
        });
    }
    void closeAllVideosWithoutClearDb() {
        fragmentList.forEach(fragment -> fragment.closeVideo(false));
    }

    private void askAndCloseAllVideos() {
        final YesNoDialog dialog = new YesNoDialog(this, R.string.warning, R.string.msg_ask_multiplayer_close_all_videos, null, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                closeAllVideosWithoutClearDb();
                multiPlayerDatabase.clearMultiScreenHistory();
                clearDuplicatedFragments();
                refreshAllPlayPauseIcon();
                checkAllFragmentsMuted();
                updateAllSpeakerImage();
                ToastUtil.getInstance(MultiplePlayerActivity.this).show(R.string.msg_ok_multiplayer_close_all_videos);
            }

            @Override
            public void onNoClick(View view, Object object) {

            }
        });
        dialog.show();
    }

    void closeAllVideos(List<Integer> skipList) {
        int i = -1;
        for (MultiplePlayerFragment fragment : fragmentList) {
            i++;
            if (skipList.contains(i)) {
                continue;
            }
           fragment.closeVideo(true);
        }
    }

    private void getFixedScreenIdList() {
        fixedScreenIdListForRandomPlay = fragmentList.stream()
                .filter(fragment -> fragment.isVideoLoaded && fragment.isPinScreen())
                .map(MultiplePlayerFragment::getScreenId)
                .collect(Collectors.toList());

        if (fixedScreenIdListForRandomPlay.size() == fragmentList.size()) {
            fixedScreenIdListForRandomPlay.clear();
        }
//
//        fixedScreenIdListForRandomPlay.clear();
////        int loadedCount = 0;
//        for (MultiplePlayerFragment fragment : fragmentList) {
//            if (fragment.isVideoLoaded && fragment.isPinScreen()) {
//                fixedScreenIdListForRandomPlay.add(fragment.getScreenId());
//            }
////            if (fragment.isVideoLoaded) {
////                loadedCount++;
////            }
//        }
//
////        if (loadedCount == fragmentList.size()) {
//            if (fixedScreenIdListForRandomPlay.size() == fragmentList.size()) {
//                fixedScreenIdListForRandomPlay.clear();
//            }
////        }
//        fixedScreenIdListForRandomPlay = fragmentList.stream()
//                .filter(MultiplePlayerFragment::isPinScreen)
//                .map(MultiplePlayerFragment::getScreenId)
//                .collect(Collectors.toList());
////
////        if (fixedScreenIdListForRandomPlay.size() == fragmentList.size()) {
////            fixedScreenIdListForRandomPlay.clear();
////        }
    }

    //랜덤 제외 스크린을 선택하는 코드
    public void showFixedScreenIdSelectionDialog(int maxValues, List<Integer> preCheckedIndexes, List<PlayerFileModel> fileListTotal) {
        String[] items = new String[maxValues];
        boolean[] checkedItems = new boolean[maxValues];

        for (int i = 0; i < maxValues; i++) {
            items[i] = String.valueOf(i + 1);
            checkedItems[i] = preCheckedIndexes.contains(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MultiMultiChoiceDialog);
        builder.setTitle(R.string.dialog_title_select_fixed_screen_id_for_random_video_play)
                .setMultiChoiceItems(items, checkedItems, (dialog, indexSelected, isChecked) -> {
                    checkedItems[indexSelected] = isChecked;
                })
                .setPositiveButton(R.string.next, (dialog, id) -> {
                    fixedScreenIdListForRandomPlay.clear();
                    for (int i = 0; i < checkedItems.length; i++) {
                        if (checkedItems[i]) {
                            fixedScreenIdListForRandomPlay.add(i); // 실제 값은 0부터 시작
                        }
                    }
                    choosePlaylistToPlayRandomVideos(fixedScreenIdListForRandomPlay, fileListTotal);
//    playRandomVideos(fixedScreenIdListForRandomPlay);
                })
                .setNegativeButton(R.string.dialog_button_cancel_auto_random_for_random_video_play, (dialog, which) -> {
                    cancelAutoRandomPlay();
                })
                .setOnCancelListener(dialog -> {
                    cancelAutoRandomPlay();
                })

                .create()
                .show();
    }
    private void cancelAutoRandomPlay() {
        ToastUtil.getInstance(this).show(R.string.toast_cancelled_auto_random_for_random_video_play);
    }
    void choosePlaylistToPlayRandomVideos(List<Integer> skipList, List<PlayerFileModel> fileListTotal) {
        multiPlayerPlaylistHelper.selectPlaylists(false, false, R.string.playlist_dialog_button_select, R.string.playlist_dialog_button_from_hidden_folder, new MultiPlayerPlaylistHelper.PlaylistSelectionCallback() {
            @Override
            public void onPlaylistsSelected(List<MultiPlayerPlaylistModel> selected) {
                if (selected.isEmpty()) {
                    final YesNoDialog dialog = new YesNoDialog(MultiplePlayerActivity.this, R.string.info, R.string.msg_no_videos_for_random_play_in_play_list, null, new OnYesNoClickListener() {
                        @Override
                        public void onYesClick(View view, Object object) {
                            playRandomVideos(skipList, selected, fileListTotal);
                        }
                        @Override
                        public void onNoClick(View view, Object object) {}
                    });
                    dialog.show();
                } else {
                    playRandomVideos(skipList, selected, fileListTotal);
                }
            }
        });
    }

    void playRandomVideos(List<Integer> skipList, List<MultiPlayerPlaylistModel> selectedPlaylists, List<PlayerFileModel> fileListTotal) {
        closeAllVideos(skipList);
        List<PlayerFileModel> list = selectRandomVideos(skipList, selectedPlaylists, fileListTotal);
        if (list.size() == 0) {
            InfoDialog infoDialog = new InfoDialog(this, R.string.info, R.string.msg_no_videos_for_random_play, R.string.close, null);
            infoDialog.setCancelable(true);
            infoDialog.setCanceledOnTouchOutside(true);
            infoDialog.show();
            undoResetAutoRandom();
            return;
        }
        Loading.show(this);
        isAutoRandomPlay = true;
        int fragmentIndex = -1;
        int listIndex = 0;
        for (MultiplePlayerFragment fragment : fragmentList) {
            // skipList에 있는 인덱스는 건너뛰기
            fragmentIndex++;
            fragment.setAutoRandomPlay(true);
            fragment.setPinScreen(false);
            if (skipList.contains(fragmentIndex)) {
                fragment.setPinScreen(true);
                continue;
            }
            if (listIndex >= list.size()) {
                listIndex = 0;
                Collections.shuffle(list);
            }
            if (listIndex < list.size()) {
                fragment.handleRepeatCloseClick();
                PlayerFileModel playerFileModel = list.get(listIndex);
                loadVideoInFragment(fragment,fragmentIndex, playerFileModel.getPath());
                fragment.setAutoRandomPlay(isAutoRandomPlay);
                listIndex++;
            }
        }
        //자동 실행을 하면 전제 플레이 아이콘이 정지가 아니고 플레이 아이콘으로 보여서 1초 뒤에 한다.
        new Handler().postDelayed(() -> {
            playAllVideos();
        }, 1000);
        refreshAllSpeakerIcon();
        checkAnyVideoPlaying();
        Loading.hide();
    }

    @NonNull
    private List<MultiPlayerVideoModel> getLoadedModels() {
        List<MultiPlayerVideoModel> currentModels = new ArrayList<>();
        for (MultiplePlayerFragment fragment : fragmentList) {
            currentModels.add(fragment.getModel());
        }
        return currentModels;
    }

    private void loadVideoInFragment(MultiplePlayerFragment fragment, int screenId, String filePath) {
        fragment.loadVideoInFragment(filePath);
    }

    public MultiPlayerVideoModel restoreHistoryModel(String filePath, MultiPlayerVideoModel modelNew) {
        MultiPlayerVideoModel modelOld = multiPlayerDatabase.getMultiPlayerVideoModelByFilePathInBackUp(filePath);
        if (modelOld.getFILE_PATH().contains(filePath)) {
            modelNew.setAB_A(modelOld.getAB_A());
            modelNew.setAB_B(modelOld.getAB_B());
            modelNew.setAb_loop_json(modelOld.getAb_loop_json());
            modelNew.setUSE_AB(modelOld.getUSE_AB());
            modelNew.setLAST_TIME(modelOld.getLAST_TIME());
            modelNew.setVOLUME(modelOld.getVOLUME());
            modelNew.setROTATE(modelOld.getROTATE());
            modelNew.setRESIZE_MODE(modelOld.getRESIZE_MODE());
            modelNew.setSpeed(modelOld.getSpeed());
        }
        return modelNew;
    }

    private List<PlayerFileModel> selectRandomVideos(List<Integer> skipList, List<MultiPlayerPlaylistModel> selectedPlaylists, List<PlayerFileModel> fileListTotal) {
        List<PlayerFileModel> allList = new ArrayList<>();
        if (selectedPlaylists == null || selectedPlaylists.isEmpty()) {
            allList = fileListTotal.stream()
                    .filter(model -> model.getVideoModel().isHide())
                    .collect(Collectors.toList());
        } else if (multiPlayerDatabase != null) {
            Map<String, Boolean> videosInPlaylist = new HashMap<>();
            for (MultiPlayerPlaylistModel model : selectedPlaylists) {
                List<String> paths = multiPlayerDatabase.getPlaylistItemFilePaths(model.getId());
                for (String path : paths) {
                    videosInPlaylist.put(path, true);
                }
            }
            for (PlayerFileModel mode : fileListTotal) {
                if (videosInPlaylist.containsKey(mode.getPath())) {
                    allList.add(mode);
                }
            }
        }
//        allList.addAll(MediaFileListUtil.fetchAllVideosFromDBForMultiPlayer());
        List<PlayerFileModel> filteredList = new ArrayList<>();


        // skip리스트에 있는 파일 경로들을 모은다
        Set<String> fragmentFilePaths = new HashSet<>();
        int i = -1;
        for (MultiplePlayerFragment fragment : fragmentList) {
            i++;
            if (skipList.contains(i)) {
                MultiPlayerVideoModel multiPlayerVideoModel = fragment.getMultiPlayerVideoModel();
                if ((multiPlayerVideoModel != null) && !multiPlayerVideoModel.getFILE_PATH().equals("")) {
                    fragmentFilePaths.add(multiPlayerVideoModel.getFILE_PATH());
                }
            }
        }

        // fragmentFilePaths에 없는 파일들만 필터링해서 selectedList에 추가
        for (PlayerFileModel playerFileModel : allList) {
            if (!fragmentFilePaths.contains(playerFileModel.getPath())) {
                filteredList.add(playerFileModel);
            }
        }

        //파일 갯수를 fragmentList.size() - skipList.size()를 해야하지만 파일수를 여유있게 한다.
        refreshListAllRandomFilePath(filteredList);
        // 프레그먼트에서도 리스트를 업데이트 해주면 이전/이후 비디오가 플레이리스트에서 가능하다.
        fragmentList.forEach(fragment -> fragment.setCurrentVideoFilePathList(listAllRandomFilePath));

        List<PlayerFileModel> randomList = CollectionUtil.getRandomElements(filteredList, numberOfScreens);
        Collections.shuffle(randomList);
        return randomList;
    }

    private void refreshListAllRandomFilePath(List<PlayerFileModel> list) {
        listAllRandomFilePath = list.stream()
                .map(fileModel -> fileModel != null ? fileModel.getVideoModel() : null)
                .filter(videoModel -> videoModel != null)
                .map(VideoModel::getPath)
                .filter(path -> path != null)
                .collect(Collectors.toList());
    }

    void playAbRepeatAllVideos() {
        if (isAnyVideoLoaded()) {
            hideAllVideosUI();
            int count = (int) fragmentList.stream().filter(MultiplePlayerFragment::hasSavedAbRepeatTime).count();
            String message = count > 0 ? getString(R.string.toast_all_videos_play_ab_repeat_time, count) : getString(R.string.toast_play_no_saved_ab_repeat_time);
            ToastUtil.getInstance(this).show(message);
            fragmentList.forEach(fragment -> fragment.playSavedABRepeatTimeInTable(true));

            if (count == fragmentList.size()) {
                updateAllPlayPauseImage();
            }
        } else {
            showToastNoVideosLoaded();
        }
    }

    void showToastNoVideosLoaded() {
        ToastUtil.getInstance(this).show(R.string.toast_load_video_first);
    }

    boolean isAnyVideoLoaded() {
        int count = (int) fragmentList.stream().filter(fragment -> fragment.isVideoLoaded).count();
        return count > 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        new PlaylistBackupHelper(this).backupTables();
    }

    private int addFragmentContainerView(int rows, int columns) {
        FragmentContainerView fragmentContainerView = new FragmentContainerView(this);
        fragmentContainerView.setId(View.generateViewId());

        GridLayout.LayoutParams layoutParams = getLayoutParams(rows, columns);
        fragmentContainerView.setLayoutParams(layoutParams);

        binding.gridLayout.addView(fragmentContainerView);
        return fragmentContainerView.getId();
    }

    private GridLayout.LayoutParams getLayoutParams(int rows, int columns) {
        DLog.d("getLayoutParams", "screenWidth : " + screenHelper.screenWidth);
        DLog.d("getLayoutParams", "screenHeight : " + screenHelper.screenHeight);

        boolean isLandscape = (myOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                || myOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                || myOrientation == ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        int topLayoutHeight = isTabLayoutVisible ? binding.llTopMenu.getHeight() : 0;
        int navigationBarHeight = isTabLayoutVisible ? 0 : Utils.getNavigationBarHeight(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.llTopMenu.getLayoutParams();
        ConstraintLayout.LayoutParams hideTopMenuParams = (ConstraintLayout.LayoutParams) binding.hideTopMenu.getLayoutParams();

        if (isLandscape) {
            layoutParams.width = (screenHelper.screenWidth  + navigationBarHeight) / columns;
            layoutParams.height = (screenHelper.screenHeight - topLayoutHeight) / rows;
            params.width = screenHelper.screenWidth - navigationBarHeight;
            hideTopMenuParams.setMarginStart(Utils.convertDpToPx(this, navigationBarHeight == 0 ? 56 : 8));
        } else {
            layoutParams.width = screenHelper.screenWidth / columns;
            layoutParams.height = (screenHelper.screenHeight - topLayoutHeight + navigationBarHeight) / rows;
            params.width = screenHelper.screenWidth;
            hideTopMenuParams.setMarginStart(Utils.convertDpToPx(this, 8));
        }
        binding.llTopMenu.setLayoutParams(params); //이걸 안하면 가로모드에서 전체화면 갔다가 상단메뉴 보이면 화면이 아레가 짤린다.
        binding.hideTopMenu.setLayoutParams(hideTopMenuParams); //가로모드에서 전체화면 갔다가 상단메뉴보이면 좌측 메뉴가 숨겨진다. 이건 화면이 좌측으로 옮겨지서 그렀다. 지금은 임시로 시작 마진을 조절했다.
        return layoutParams;
    }

    private GridLayout.LayoutParams getFullScreenLayoutParams(int rows, int columns) {
        boolean isLandscape = (myOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                || myOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                || myOrientation == ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        int topLayoutHeight = isTabLayoutVisible ? binding.llTopMenu.getHeight() : 0;
        int navigationBarHeight = isTabLayoutVisible ? 0 : Utils.getNavigationBarHeight(this);

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.rowSpec = GridLayout.spec(0, rows);
        layoutParams.columnSpec = GridLayout.spec(0, columns);
        if (isLandscape) {
            layoutParams.width = (screenHelper.screenWidth + navigationBarHeight);
            layoutParams.height = (screenHelper.screenHeight - topLayoutHeight);
        } else {
            layoutParams.width = screenHelper.screenWidth;
            layoutParams.height = (screenHelper.screenHeight - topLayoutHeight + navigationBarHeight);
        }
        return layoutParams;
    }

    public boolean isFullScreenForFragment(MultiplePlayerFragment fragment) {
        return fullScreenFragmentIndex >= 0 && fullScreenFragmentIndex < fragmentList.size()
                && fragmentList.get(fullScreenFragmentIndex) == fragment;
    }

    public void enterFullScreenForFragment(MultiplePlayerFragment fragment) {
        int idx = fragmentList.indexOf(fragment);
        if (idx < 0) return;
        for (int i = 0; i < fragmentList.size(); i++) {
            if (i != idx) {
                fragmentList.get(i).pausePlayerFromAllVideos();
            }
        }
        fullScreenFragmentIndex = idx;
        // 전체 화면일 때는 상단 메뉴바(전체 플레이/음소거 등) 숨김
        isTabLayoutVisible = false;
        binding.llTopMenu.setVisibility(View.GONE);
        Pair<Integer, Integer> rowsAndColumns = screenHelper.calculateRowsAndColumns();
        int rows = rowsAndColumns.first;
        int columns = rowsAndColumns.second;
        for (int i = 0; i < numberOfScreens; i++) {
            View view = binding.gridLayout.getChildAt(i);
            if (view instanceof FragmentContainerView) {
                view.setVisibility(i == idx ? View.VISIBLE : View.GONE);
                if (i == idx) {
                    view.setLayoutParams(getFullScreenLayoutParams(rows, columns));
                }
            }
        }
        binding.gridLayout.requestLayout();
        View fullScreenContainer = binding.gridLayout.getChildAt(idx);
        if (fullScreenContainer != null) {
            fullScreenContainer.requestLayout();
        }
        // 그리드 레이아웃 완료 후 컨테이너를 그리드 크기에 맞춰 재조정(하단 UI가 잘리지 않도록), 그 다음 하단 메뉴 표시
        binding.gridLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.gridLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (fullScreenFragmentIndex != idx) return;
                int w = binding.gridLayout.getWidth();
                int h = binding.gridLayout.getHeight();
                if (w > 0 && h > 0 && fullScreenContainer != null && fullScreenContainer.getLayoutParams() instanceof GridLayout.LayoutParams) {
                    GridLayout.LayoutParams lp = (GridLayout.LayoutParams) fullScreenContainer.getLayoutParams();
                    lp.width = w;
                    lp.height = h;
                    fullScreenContainer.setLayoutParams(lp);
                }
                fragment.setFullScreenBottomPadding(true);
                fullScreenContainer.postDelayed(() -> {
                    if (fullScreenFragmentIndex == idx && fragmentList.get(idx) == fragment) {
                        fragment.showMenuControlForFullScreen();
                    }
                }, 100);
            }
        });
    }

    public void exitFullScreen() {
        if (fullScreenFragmentIndex < 0) return;
        MultiplePlayerFragment wasFullScreen = fragmentList.get(fullScreenFragmentIndex);
        fullScreenFragmentIndex = -1;
        wasFullScreen.setFullScreenBottomPadding(false);
        // 상단 메뉴바 다시 표시
        isTabLayoutVisible = true;
        binding.llTopMenu.setVisibility(View.VISIBLE);
        for (int i = 0; i < numberOfScreens; i++) {
            View view = binding.gridLayout.getChildAt(i);
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
        }
        // 전체 화면 해제 후 우측/하단 회색 영역 대신 s/w 네비게이션 바가 보이도록
        Utils.toggleFullscreenMultiPlayer(this, false);
        updateGridLayoutParams();
        binding.getRoot().post(() -> ViewCompat.requestApplyInsets(binding.getRoot()));
    }

    private void initPlayerFragment(int containerId, int screenId, boolean isLoadLastWatchedVideos) {
        MultiplePlayerFragment fragment = new MultiplePlayerFragment(screenId, screenHelper.isOrientationVertical,isLoadLastWatchedVideos, multiPlayerDatabase);
        getSupportFragmentManager().beginTransaction()
                .add(containerId, fragment, String.valueOf(containerId)).commit();
        fragmentList.add(fragment);
    }
}
