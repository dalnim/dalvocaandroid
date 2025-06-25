package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.ThumbnailListPlayerAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.BasePlayerActivity;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityThumbnailListPlayerBinding;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.dialog.ThumbnailListPlayerDialog;
import com.dalread.dialog.ZoomedPhotoDialog;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnClickListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.ToastUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindColor;
import butterknife.BindDimen;

public class ThumbnailListPlayerActivity extends BasePlayerActivity implements OnAsyncTaskListener {

    //    @BindView(R.id.rvList) RecyclerView rvList;
//    @BindView(R.id.nvBottom) BottomNavigationView nvBottom;
    @BindColor(R.color.color_divider)
    int clDivider;
    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    private final String[] thumbnailCountInARowValues = new String[]{"1", "2", "3", "4"};
    private final String[] readThumbnailIntervalValues = new String[]{"1", "2", "3", "4", "5", "7", "10", "20", "30", "60", "120", "180", "360", "600"};

    private ArrayList<Long> itemsThumbnail = new ArrayList<>();
    private int currentBottomNavigationId;
    private ThumbnailListPlayerAdapter adapter;
    private ThumbnailListPlayerDialog thumbnailListPlayerDialog;
    private SingleChoiceDialog singleChoiceDialog;

    private int selectedThumbnailIntervalCountValue;
    private int selectedThumbnailCountInARow;
    private int thumbnailInterval; //milliseconds
    private int numberOfThunbnailInOneItem = 1;
    private boolean blnOpenVideo = false;
    private final int TYPE_GET_ALL = 0;
    private final int TYPE_SYNC_DATA_BEFORE_EXIT = TYPE_GET_ALL + 1;

    private boolean dataChanged;

    private ActivityThumbnailListPlayerBinding binding;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected View getContentView() {
        binding = ActivityThumbnailListPlayerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void setFullscreen() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDialog();

    }
    private void initDialog() {
        singleChoiceDialog = new SingleChoiceDialog(this);
    }
    @Override
    public void initView() {
        playerFileModel = getIntent().getParcelableExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE);
        if (playerFileModel == null) {
            DLog.e(getLogTag(), "playerFileModel is null");
            return;
        }
        createSubDatabase(playerFileModel);
        initThumbnailIntervalValue();
        initNumberOfThumbnailInOneItem();
        initAutoScrollIntervalCountValue();
        updateAutoScrollIconToPlay();
        initEventBus();
        initToolbar();
        initBottomNavigation();
        initRecycleView();
        binding.nvBottom.setVisibility(View.GONE);
        initData();
    }

    private void getNumberOfThumbnailInOneItem() {
        numberOfThunbnailInOneItem = Integer.parseInt(thumbnailCountInARowValues[selectedThumbnailCountInARow]);
    }

    @Override
    public void initData() {
        callAsyncTask(TYPE_GET_ALL);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        callAsyncTask(this, null, TYPE_SYNC_DATA_BEFORE_EXIT, true);
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
        onHeaderAutoScrollIconClick();
    }

    @Override
    public void onHeaderTextRightClick() {
        stopAutoScrolling();
        openMenuDialog();
    }

    private void initThumbnailIntervalValue() {
        thumbnailInterval = sharedPreferences.getVideoThumbnailInterval();
        for (int i = 0; i < readThumbnailIntervalValues.length; i++) {
            if (Integer.parseInt(readThumbnailIntervalValues[i]) == thumbnailInterval / 1000) {
                selectedThumbnailIntervalCountValue = i;
                break;
            }
        }
    }

    private void initNumberOfThumbnailInOneItem() {
        int thumbnailCountInARow = sharedPreferences.getVideoThumbnailCountInARow();
        for (int i = 0; i < thumbnailCountInARowValues.length; i++) {
            if (Integer.parseInt(thumbnailCountInARowValues[i]) == thumbnailCountInARow) {
                selectedThumbnailCountInARow = i;
                break;
            }
        }
        getNumberOfThumbnailInOneItem();
    }

    @Override
    protected Object getIconTag() {
        return binding.header.getIconRight().getTag();
    }

    @Override
    protected void updateAutoScrollIconToPause() {
        binding.header.setIconRight(R.drawable.ic_new_pause);
        binding.header.getIconRight().setTag(R.drawable.ic_new_pause);
        binding.fsList.getHandleDrawable().setAlpha(0);
    }

    @Override
    protected void updateAutoScrollIconToPlay() {
        binding.header.setIconRight(R.drawable.ic_new_play);
        binding.header.getIconRight2().setTag(R.drawable.ic_new_play);
        binding.fsList.getHandleDrawable().setAlpha(255);
    }

    @Override
    protected RecyclerView getAutoScrollRecyclerView() {
        return binding.rvList;
    }

    @Override
    protected RecyclerView.Adapter getAutoScrollRecyclerViewAdapter() {
        return adapter;
    }

    @Override
    protected RecyclerView.LayoutManager getAutoScrollLayoutManager() {
        return layoutManager;
    }

    private void openMenuDialog() {
        final ThumbnailListPlayerDialog dialog = new ThumbnailListPlayerDialog(this, false, (view, object) -> {
            switch (view.getId()) {
                case R.id.llRegenerateThumbnailList:
                    showThumbnailIntervalDialog();
                    break;
                case R.id.llAutoScrolling:
                    showAutoScrollIntervalDialog();
                    break;
                case R.id.llKnownPhrases:
                    ToastUtil.getInstance(ThumbnailListPlayerActivity.this).show(R.string.msg_under_development);
                    break;
            }
        });
        dialog.show();
    }

    private void showThumbnailIntervalDialog() {
        singleChoiceDialog.show(
                R.string.thumbnail_interval,
                readThumbnailIntervalValues,
                selectedThumbnailIntervalCountValue,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        boolean isChangeInterval = false;
                        if (which != selectedThumbnailIntervalCountValue) {
                            isChangeInterval = true;
                            String strInterval = readThumbnailIntervalValues[selectedThumbnailIntervalCountValue = which];
                            thumbnailInterval = Integer.parseInt(strInterval) * 1000; //to make millisecond
                            sharedPreferences.setVideoThumbnailInterval(thumbnailInterval);
                        }
                        showThumbnailCountInARowDialog(isChangeInterval);
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    private void showThumbnailCountInARowDialog(boolean isChangeInterval) {
        singleChoiceDialog.showWrapContentHeight(
                R.string.thumbnail_count_in_a_row,
                thumbnailCountInARowValues,
                selectedThumbnailCountInARow,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        boolean isChangeCountInARow = false;
                        if (which != selectedThumbnailCountInARow) {
                            isChangeCountInARow = true;
                            selectedThumbnailCountInARow = which;
                            getNumberOfThumbnailInOneItem();
                            sharedPreferences.setVideoThumbnailCountInARow(numberOfThunbnailInOneItem);
                        }
                        if (isChangeInterval || isChangeCountInARow) {
                            callAsyncTask(TYPE_GET_ALL);
                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
            BaseEvent.EventType type = successEvent.getEventType();
            if (type == BaseEvent.EventType.DATA_CHANGED) {
                dataChanged = true;
            }
        }
    }

    private void callAsyncTask(int type) {
        callAsyncTask(type, null);
    }

    private void callAsyncTask(int type, Object data) {
        callAsyncTask(type, data, true);
    }

    private void callAsyncTask(int type, Object data, boolean isLoading) {
        new CustomAsyncTask(this, this, data, type, isLoading).execute();
    }

    private void initToolbar() {
        binding.header.setTitle(R.string.thumbnail_list);
    }

    private void initBottomNavigation() {
        binding.nvBottom.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id != currentBottomNavigationId) {
                currentBottomNavigationId = id;
                callAsyncTask(TYPE_GET_ALL);
                return true;
            }
            return false;
        });
        currentBottomNavigationId = R.id.nav_grade;
    }


    private void initRecycleView() {
        layoutManager = new GridLayoutManager(this, numberOfThunbnailInOneItem);
        binding.rvList.setLayoutManager(layoutManager);
        binding.rvList.addItemDecoration(new SeparatorDecoration(this, clDivider, dividerHeight));
        adapter = new ThumbnailListPlayerAdapter(this, playerFileModel, onItemClickListener);
        binding.rvList.setAdapter(adapter);
    }

    private void generateData() {
        long videoDuration = playerFileModel.getDuration();
        if (videoDuration <= 0)
            return;

        itemsThumbnail = new ArrayList<>();
        for (long i = thumbnailInterval; i < videoDuration; i = i + thumbnailInterval) {
            itemsThumbnail.add(i);
        }
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
            case TYPE_GET_ALL:
                generateData();
                break;
            case TYPE_SYNC_DATA_BEFORE_EXIT:
                break;
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_GET_ALL:
                binding.rvList.setAdapter(null);
                initRecycleView();
                adapter.setThumbnailIntervalList(itemsThumbnail);
                if (data == null) {
                    binding.rvList.post(() -> binding.rvList.scrollToPosition(0));
                }
                break;
            case TYPE_SYNC_DATA_BEFORE_EXIT:
                onFinishActivity();
                break;
        }
        Loading.hide();
    }

    private void onFinishActivity() {
        int resultValue = blnOpenVideo == true ? RESULT_OK : RESULT_CANCELED;
        Intent intent = getIntent();
        setResult(resultValue, intent);
        finish();
    }

    private OnClickListener onItemClickListener = (view, object) -> {
        switch (view.getId()) {
            case R.id.izbVideoThumbnail:
                int index = (int) object;
                zoomImage(index, view);
                break;
        }
    };

    private void zoomImage(int index, View view) {
        ZoomedPhotoDialog zoomedPhotoDialog = new ZoomedPhotoDialog(this, playerFileModel);
        zoomedPhotoDialog.loadThumbnailFromTimeAndShow(index * thumbnailInterval + thumbnailInterval, ((ImageView) view).getDrawable());
    }

    //Don't delete this. May use this later.
    private void openDalPlayer(int index) {
        blnOpenVideo = true;
        Long thumbnailTime = (long) (index * thumbnailInterval + thumbnailInterval);
        DLog.d(getLogTag(), "openDalPlayer");
        Loading.hide();
        playerFileModel.getVideoModel().setLastDuration(thumbnailTime);
        playerFileModel.getVideoModel().setNewFile(Constant.INT_BOOLEAN.FASLE);
        updateVideoModel(playerFileModel.getVideoModel());
        eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.PLAYER_RELOAD_VIDEO, playerFileModel.getVideoModel()));
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_TIME_TO_START, thumbnailTime);
        startActivity(intent);
    }

}
