package com.dalread.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.adapter.NetworkSubAdapter;
import com.dalread.base.BasePlayerFragment;
import com.dalread.component.CenterLayoutManager;
import com.dalread.database.DownloadModelQuery;
import com.dalread.database.VideoModelQuery;
import com.dalread.databinding.FragmentMainPlayerBinding;
import com.dalread.dialog.PlayerShowMeaningDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.dialog.ZoomedPhotoDialog;
import com.dalread.listener.OnAsyncTaskListenerWithType;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.DownloadModel;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.ServerModel;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.FileUtil;
import com.dalread.util.Loading;
import com.dalread.util.MediaListUtil;
import com.dalread.util.ServerManager;
import com.dalread.util.StorageUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import org.apache.commons.io.FilenameUtils;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.fastscroll.FastScrollerBuilder;

public class PlayerNetworkFragmentSub extends BasePlayerFragment implements OnClickListener, OnAsyncTaskListenerWithType {
    private PlayerNetworkActivity activity;
    protected List<PlayerFileModel> fileListTotal = new ArrayList<>();
    protected List<PlayerFileModel> fileList = new ArrayList<>();
    protected NetworkSubAdapter networkSubAdapter;
    protected final int TYPE_INIT_DATA = 1;
    protected final int TYPE_LOAD_NEXT_FOLDER_DATA = TYPE_INIT_DATA + 1;  //Dalnim : Use when AraPlayre shows media in a folder.
    protected final int TYPE_LOAD_PREV_FOLDER_DATA = TYPE_LOAD_NEXT_FOLDER_DATA + 1;
    protected ServerModel serverModel;
//    private boolean isResume = false;  //앱을 열었을때 비디오리스트를 새로 읽을지 아님여부 결정
    private FragmentMainPlayerBinding binding;

    @Override
    protected View getContentView() {
        binding = FragmentMainPlayerBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return binding.getRoot();
    }

    protected String getLogTag() {
        return getClass().getSimpleName();
    }


    @Override
    public void initView() {
        initEventBus();
        activity = (PlayerNetworkActivity) getActivity();
        if (getArguments() != null) {
            serverModel = getArguments().getParcelable(Constant.PLAYER.INTENT.KEY_DATA);
            DLog.d(getLogTag(), "serverModel=" + serverModel.toString());
        }
        new FastScrollerBuilder(binding.rvContent).build();
        networkSubAdapter = new NetworkSubAdapter(getContext(), fileList, this);
        networkSubAdapter.setShowIconArrow(true);

        binding.rvContent.setAdapter(networkSubAdapter);
        binding.rvContent.setHasFixedSize(true);
        binding.rvContent.setLayoutManager(new CenterLayoutManager(activity));

        activity.resetCurrentPath();
    }

    @Override
    public void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        callAsyncTask(activity.getCurrentPath(), TYPE_INIT_DATA, true);
    }

    @Override
    public void onStop() {
        super.onStop();
        new Thread(() -> ServerManager.getInstance().disconnectFTPServer()).start();
    }

    @Subscribe
    public void onEvent(SuccessEvent event) {
        if (event.getScreen() == BaseEvent.Screen.MAIN) {
            switch (event.getEventType()) {
                case PLAYER_BACK_FOLDER_MULTIPLAYER:
                    callAsyncTask((String) event.getModel(), TYPE_LOAD_PREV_FOLDER_DATA);
                    break;
                case PLAYER_SORT:
                    sortFiles((int) event.getModel());
                    break;
                case PLAYER_REFRESH_VIDEO_LIST:
                    callRefreshMethod();
            }
        }

    }

    @Override
    public void onClick(View view, Object object) {
        PlayerFileModel playerFileModel = (PlayerFileModel) object;
        switch (view.getId()) {
            case R.id.llItem:
                    PlayerFileModel playerFile = (PlayerFileModel) object;
                    handleVideoItemClick(playerFile);
                break;
            case R.id.cb_select_item:
                selectVideoItem();
                break;
            case R.id.llFolder:

                callAsyncTaskPlayerFile((PlayerFileModel) object, TYPE_LOAD_NEXT_FOLDER_DATA);

                break;
            case R.id.izbVideoThumbnail:
                ZoomedPhotoDialog zoomedPhotoDialog = new ZoomedPhotoDialog(activity, playerFileModel);
                zoomedPhotoDialog.loadThumbnailFromFileAndShow(playerFileModel, ((ImageView) view).getDrawable());
                break;
        }
    }

    private void selectVideoItem() {
        eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.ALL_VIDEOS_SELECTED, false));
    }

    protected int getMsgIndexingMediaFiles() {
        return R.string.msg_indexing_video_files;
    }

    private void callRefreshMethod() {
        VideoModelQuery.updateAllNewFileToFalse(Voca.getRealm());
        VideoModelQuery.updateAllByTrashToTrue(Voca.getRealm());
        callAsyncTask(activity.getCurrentPath(), TYPE_INIT_DATA);
    }

    protected void handleVideoItemClick(PlayerFileModel playerFileModel) {
        playerFileModel.setServerModel(serverModel);
        boolean isShowDialog = true;
        int serverType = Constant.PLAYER.SERVER.TYPE.NONE;

        //여기서 setSubDatabase을 null을 안하면 어떤 오류가 생길까?
//            activity.setSubDatabase(null);
        serverType = serverModel.getType();
        switch (serverType) {
            case Constant.PLAYER.SERVER.TYPE.FTP:
            case Constant.PLAYER.SERVER.TYPE.FREE_FTP_DOWNLOAD:
                break;
            case Constant.PLAYER.SERVER.TYPE.WEBDAV:
                isShowDialog = checkVideoItemLocal(playerFileModel);
                break;
        }
        if (isShowDialog) {
            showMeaningDialog(playerFileModel, serverType);
        }
    }
    private void showMeaningDialog(PlayerFileModel playerFileModel, int serverType) {
        PlayerShowMeaningDialog dialog = new PlayerShowMeaningDialog(
                getContext(),
                serverType,
                FileUtil.isValidMediaExtension(playerFileModel.getPath()),
                false,
                (dialogInterface, i) -> {
                    switch (i) {
                        case R.id.llDownload:
                            confirmDownloadNetwork(playerFileModel);
                            break;
                    }
                });
        dialog.show();
    }
    private boolean checkVideoItemLocal(PlayerFileModel playerFileModel) {
        activity.createSubDatabase(playerFileModel);
        return true;
    }

    private void sortFiles(int sort) {
        DLog.d(getLogTag(), "sortFiles - sort=" + sort);
        Loading.show(activity);
        fileList = StorageUtil.sortFiles(fileList, sort);
        networkSubAdapter.setData(fileList);
        binding.rvContent.scrollToPosition(0);
        Loading.hide();
    }

    @Override
    public void onInitAsyncTask(int searchType) {
        DLog.d(getLogTag(), "onInitAsyncTaskWithType");
        switch (searchType) {
            case TYPE_INIT_DATA:
                Loading.show(activity);
                break;
            default:
                Loading.showDelay(activity);
        }
    }

    @Override
    public void onInitAsyncTask() {
        DLog.d(getLogTag(), "onInitAsyncTask");
    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }
    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
            case TYPE_LOAD_NEXT_FOLDER_DATA:
            case TYPE_LOAD_PREV_FOLDER_DATA:
                List<PlayerFileModel> playerFileModelList = loadData(searchType, data);
                return playerFileModelList;
            default:
                final PlayerFileModel playerFileModel = (PlayerFileModel) data;
                String encoding = null;
                if (playerFileModel.getVideoModel() != null) {
                    encoding = playerFileModel.getVideoModel().getEncoding();
                }
//                if (searchType == Constant.PLAYER.MEANING.WITHOUT) {
//                    return SubtitleUtil.parserSubTitle(playerFileModel, encoding);
//                }
                return "";//SubtitleUtil.parserContentSubTitle(playerFileModel, playerFileModel.getVideoModel().getSubPathIndex());
        }
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
            case TYPE_LOAD_NEXT_FOLDER_DATA:
            case TYPE_LOAD_PREV_FOLDER_DATA:
                finishLoadData(searchType, resultData, data);
                break;
        }
        Loading.hide();
    }

    private void callAsyncTask(String data, int type) {
        callAsyncTask(data, type, true);
    }

    private void callAsyncTask(String data, int type, boolean isLoading) {
        callAsyncTaskPlayerFile( new PlayerFileModel(data), type, isLoading);
    }

    private void callAsyncTaskPlayerFile(PlayerFileModel data, int type) {
        callAsyncTaskPlayerFile(data, type, true);
    }

    private void callAsyncTaskPlayerFile(PlayerFileModel data, int type, boolean isLoading) {
        if (!activity.isNetwork()) {
            activity.getAlertDialog().showNoInternet();
            return;
        }
        activity.callAsyncTask(this, data, type, isLoading);
    }

    private void confirmDownloadNetwork(PlayerFileModel data) {
        if (!Utils.hasWifiConnected(activity)) {
            final YesNoDialog dialog = new YesNoDialog(activity,
                    R.string.warning,
                    R.string.msg_download_warning_mobile,
                    data,
                    onConfirmDownloadNetwork);
            dialog.show();
        } else {
            checkDownloadFile(data);
        }
    }

    private OnYesNoClickListener onConfirmDownloadNetwork = new OnYesNoClickListener() {
        @Override
        public void onYesClick(View view, Object object) {
            checkDownloadFile((PlayerFileModel) object);
        }

        @Override
        public void onNoClick(View view, Object object) {

        }
    };

    private void checkDownloadFile(PlayerFileModel data) {
        DLog.d(getLogTag(), "checkDownloadFile - file=" + data.toString());
        File localFile = new File(StorageUtil.getMediaAbsoluteFolder(activity), FilenameUtils.getName(data.getPath()));
        final DownloadModel downloadModel = DownloadModelQuery.getById(Voca.getRealm(), FilenameUtils.getName(data.getPath()));
        downloadFile(data);
    }

    private void downloadFile(Object data) {
        final PlayerFileModel file = (PlayerFileModel) data;
        final DownloadModel checkSameModel = DownloadModelQuery.getById(Voca.getRealm(), file.getPath());
        // set currentSize is 0 when downloadModel is exist and isOverwrite is true
        if (checkSameModel != null) {
            if (!checkSameModel.isCompleted()) {
                checkSameModel.setStatus(Constant.PLAYER.SERVER.DOWNLOAD.STATUS.WAIT);
                DownloadModelQuery.update(Voca.getRealm(), checkSameModel);
                activity.addDownload(checkSameModel);
            }
            return;
        }
        final DownloadModel model = new DownloadModel();
        model.setId(file.getPath());
        model.setName(file.getName());
        model.setPath(file.getPath());
        model.setIdServer(serverModel.getId());
        model.setSize(file.getSize());
        DownloadModelQuery.add(Voca.getRealm(), model);
        activity.addDownload(model);
        ToastUtil.getInstance(activity).show(R.string.download_file_started);
    }

    private List<PlayerFileModel> loadData(int searchType, Object data) {
//        List<PlayerFileModel> lists = new ArrayList<>();
        final PlayerFileModel file = (PlayerFileModel) data;
        DLog.d(getLogTag(), "loadData - searchType=" + searchType + " - file=" + file.toString());
        List<PlayerFileModel> list = new ArrayList<>();

            switch (serverModel.getType()) {
                case Constant.PLAYER.SERVER.TYPE.FTP:
                case Constant.PLAYER.SERVER.TYPE.FREE_FTP_DOWNLOAD:
                    list = loadDataFTP(file.getPath());
                    break;
                case Constant.PLAYER.SERVER.TYPE.WEBDAV:
                    list = loadDataWebDAV(file.getPath());
                    break;
            }
            if (list == null) {
                list = new ArrayList<>();
//                ToastUtil.getInstance(activity).show(R.string.cant_connect_the_server);
            }

            return StorageUtil.sortFiles(list, sharedPreferences.getPlayerFileSort());
    }

    private List<PlayerFileModel> loadDataFTP(String path) {
        DLog.d(getLogTag(), "loadDataFTP");
        return ServerManager.getInstance().getListFileFromFTPServer(path);
    }

    private List<PlayerFileModel> loadDataWebDAV(String path) {
        DLog.d(getLogTag(), "loadDataWebDAV");
        return ServerManager.getInstance().getListFileFromWebDAVServer(path, Constant.AppMediaType.VIDEO);
    }

    protected void finishLoadData(int type, Object resultData, Object data) {
        fileListTotal = ((List<PlayerFileModel>) resultData);
        final PlayerFileModel file = (PlayerFileModel) data;
        if (type == TYPE_LOAD_NEXT_FOLDER_DATA) {
            activity.setCurrentNextPath(file.getName(), file.getPath());
        } else if (type == TYPE_LOAD_PREV_FOLDER_DATA) {
            activity.setCurrentPrevPath();
        }


        VideoModelQuery.deleteAllByTrash(Voca.getRealm());

        DLog.d(getLogTag(), "finishLoadData");

        fileList = MediaListUtil.generateSearchData("", fileListTotal);
        networkSubAdapter.setData(fileList);
    }
}