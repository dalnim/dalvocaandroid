package com.dalread.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.DownloadPlayerAdapter;
import com.dalread.base.BasePlayerFragment;
import com.dalread.component.SeparatorDecoration;
import com.dalread.database.DownloadModelQuery;
import com.dalread.databinding.FragmentDownloadPlayerBinding;
import com.dalread.dialog.YesNoDialog;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.DownloadModel;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.DownloadUtil;
import com.dalread.util.StorageUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MultiPlayerDownloadFragment extends BasePlayerFragment {
    private PlayerNetworkActivity activity;
    private LinearLayoutManager layoutManager;
    private DownloadPlayerAdapter adapter;
    private ArrayList<DownloadModel> list = new ArrayList<>();

    private FragmentDownloadPlayerBinding binding;

    @Override
    protected View getContentView() {
        binding = FragmentDownloadPlayerBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        activity = (PlayerNetworkActivity)getActivity();
        return binding.getRoot();
    }

    @Override
    public void initView() {
        initEventBus();
        adapter = new DownloadPlayerAdapter(getContext(), list, onDownloadClickListener);
        binding.rvContent.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getContext());
        binding.rvContent.setLayoutManager(layoutManager);
        binding.rvContent.addItemDecoration(new SeparatorDecoration(getContext(), BaseBindUtils.getDividerColor(), BaseBindUtils.getDividerHeight(getContext())));
        showDownloadedFilePath();
    }

    private void showDownloadedFilePath() {
        binding.tvDownloadMediaPath.setText(getString(R.string.msg_downloaded_file_path, StorageUtil.getMediaAbsoluteFolder(getContext())));
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        activity.setClearButtonEnable(true);
    }

    @Override
    public void initData() {
        final List<DownloadModel> tmp = DownloadModelQuery.getAll(Voca.getRealm());
        list.clear();
        if (tmp != null && !tmp.isEmpty()) {
            for (DownloadModel file : tmp) {
//                if (!file.isCompleted()) {
//                    if ((file.isDownload() || file.isPause())) {
//                        file.setStatus(Constant.PLAYER.SERVER.DOWNLOAD.STATUS.PAUSE);
//                    } else {
//                        file.setStatus(Constant.PLAYER.SERVER.DOWNLOAD.STATUS.WAIT);
//                    }
//                }
//                DownloadModelQuery.update(Voca.getRealm(), file);
                list.add(new DownloadModel(file));
            }
        }
//        activity.setClearButtonEnable(!list.isEmpty());
        adapter.setDatas(list);
        adapter.notifyDataSetChanged();
        updateVisibilityDownloadingListAndMessage();
    }

    private void updateVisibilityDownloadingListAndMessage() {
        if (list.size() > 0) {
            binding.rvContent.setVisibility(View.VISIBLE);
            binding.tvNoDownloadData.setVisibility(View.GONE);
        } else {
            binding.rvContent.setVisibility(View.GONE);
            binding.tvNoDownloadData.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void onEvent(SuccessEvent event) {
        if (event.getScreen() != BaseEvent.Screen.PLAYER_DOWNLOAD) return;
        switch (event.getEventType()) {
            case PLAYER_DOWNLOAD_INIT:
                initData();
                break;
            case PLAYER_DOWNLOAD_UPDATE:
                final DownloadModel file = (DownloadModel) event.getModel();
                updateDownloadPosition(file);
                if (file.getPosition() >= 0 && file.getPosition() < list.size()) {
                    list.set(file.getPosition(), file);
                }
                adapter.notifyItemChanged(file.getPosition());
                break;
            case PLAYER_DOWNLOAD_PROGRESS:
                onProgressUpdate((DownloadModel) event.getModel());
                break;
            case PLAYER_DOWNLOAD_COMPLETED:
                onDownloadCompleted((DownloadModel) event.getModel());
                break;
        }
    }

    private void updateRow(final DownloadModel file, View v) {
        TextView tvDescription = v.findViewById(R.id.tvDescription);
        String downloadingInfo = DownloadUtil.getDownloadInfo(file);
        String status = getContext().getString(R.string.download_msg_downloading)  + " " + downloadingInfo;
        if (Utils.isEmpty(downloadingInfo)) {
            status = getContext().getString(R.string.download_msg_download_fail);
        }

        tvDescription.setText(status);
//        if (!file.isCompleted() && file.isDownload()) {
//            file.setStatus(Constant.PLAYER.SERVER.DOWNLOAD.STATUS.DOWNLOAD);
//            DownloadModelQuery.update(Voca.getRealm(), file);
//        }
    }

    protected void onProgressUpdate(DownloadModel file) {
        if (list == null || list.isEmpty()) return;
        updateDownloadPosition(file);
        if (file.getPosition() < 0 || file.getPosition() >= list.size()) return;
//        DLog.d(getLogTag(), "onProgressUpdate - file=" + file.toString());
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        adapter.getItem(file.getPosition()).setCurrentSize(file.getCurrentSize());
        if (file.getPosition() < first || file.getPosition() > last) {
            // just update your data set, UI will be updated automatically in next
            // getView() call
        } else {
            RecyclerView.ViewHolder holder = binding.rvContent.findViewHolderForAdapterPosition(file.getPosition());
            updateRow(adapter.getItem(file.getPosition()), holder.itemView);
        }
    }

    private void onDownloadCompleted(DownloadModel file) {
        DLog.d(getLogTag(), "onDownloadCompleted - file=" + file.toString());
        updateDownloadPosition(file);
        if (file.getPosition() >= 0 && file.getPosition() < list.size()) {
            list.set(file.getPosition(), file);
        }
        adapter.notifyItemChanged(file.getPosition());
    }

    private OnClickListener onDownloadClickListener = (view, object) -> {
        DLog.d(getLogTag(), "onDownloadClickListener");
        final DownloadModel model = (DownloadModel) object;
        switch (model.getStatus()) {
            case Constant.PLAYER.SERVER.DOWNLOAD.STATUS.DOWNLOAD:
                handleDownloadToPauseStatus(model);
                break;
            default:
                confirmDownloadNetwork(model);
                break;
        }
    };

    private void handleDownloadToPauseStatus(DownloadModel model) {
        model.setStatus(Constant.PLAYER.SERVER.DOWNLOAD.STATUS.PAUSE);
        activity.cancelDownload(model);
        DownloadModelQuery.update(Voca.getRealm(), model);
        adapter.notifyItemChanged(model.getPosition());
    }

    private void handlePauseToWaitStatus(DownloadModel model) {
        model.setStatus(Constant.PLAYER.SERVER.DOWNLOAD.STATUS.WAIT);
        activity.addDownload(model);
        DownloadModelQuery.update(Voca.getRealm(), model);
        adapter.notifyItemChanged(model.getPosition());
    }

    private void confirmDownloadNetwork(DownloadModel data) {
        if (!Utils.hasWifiConnected(getContext())) {
            final YesNoDialog dialog = new YesNoDialog(getContext(),
                    R.string.warning,
                    R.string.msg_download_warning_mobile,
                    data,
                    onConfirmDownloadNetwork);
            dialog.show();
        } else {
            handlePauseToWaitStatus(data);
        }
    }

    private OnYesNoClickListener onConfirmDownloadNetwork = new OnYesNoClickListener() {
        @Override
        public void onYesClick(View view, Object object) {
            handlePauseToWaitStatus((DownloadModel) object);
        }

        @Override
        public void onNoClick(View view, Object object) {

        }
    };

    private void updateDownloadPosition(DownloadModel file) {
        if (file.getPosition() <= 0 || file.getPosition() >= list.size()) {
            DLog.d(getLogTag(), "updateDownloadPosition - file=" + file.toString());
            for (DownloadModel d : list) {
                if (d.getId().equals(file.getId())) {
                    file.setPosition(d.getPosition());
                    break;
                }
            }
        }
    }
}
