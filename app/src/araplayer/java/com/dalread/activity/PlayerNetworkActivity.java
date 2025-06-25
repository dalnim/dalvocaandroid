package com.dalread.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dalread.R;
import com.dalread.base.BasePlayerActivity;
import com.dalread.component.Toolbar;
import com.dalread.database.DownloadModelQuery;
import com.dalread.database.ServerModelQuery;
import com.dalread.databinding.ActivityPlayerNetworkBinding;
import com.dalread.dialog.PlayerDownloadClearDialog;
import com.dalread.model.DownloadModel;
import com.dalread.model.ServerModel;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.service.DownloadingService;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.ServerManager;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import java.util.Stack;

public class PlayerNetworkActivity extends BasePlayerActivity {
  private ActivityPlayerNetworkBinding binding;
  private String currentPath = Constant.BASE_BLANK;
  private Stack<String> paths = new Stack<>();
  private Stack<String> titles = new Stack<>();
  @Override
  protected View getContentView() {
    binding = ActivityPlayerNetworkBinding.inflate(getLayoutInflater());
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
  public void onBackPressed() {
    if (isRootPath()) {
      super.onBackPressed();
    } else {
      sendCurrentPath();
    }
  }
  public String getCurrentPath() {
    return currentPath;
  }
  public void resetCurrentPath() {
    currentPath = Constant.BASE_BLANK;
  }
  private void sendCurrentPath() {
    DLog.d(getLogTag(), "sendCurrentPath - currentPath=" + currentPath);
    currentPath = paths.pop();
    DLog.d(getLogTag(), "sendCurrentPath - currentPath edited=" + currentPath);
    eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.PLAYER_BACK_FOLDER_MULTIPLAYER, currentPath));
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
  public void setTitle(String msg) {
    titles.push(binding.header.getTitle());
    binding.header.setTitle(msg);
  }

  private boolean isRootPath() {
    return paths.size() <= 0;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
              .replace(binding.fragmentContainer.getId(), new PlayerNetworkFragment())
              .commit();
    }
  }

  @Override
  public void initView() {

  }

  @Override
  public void initData() {

  }

  @Override
  public void onHeaderLeftClick() {
    onBackPressed();
  }

  @Override
  public void onHeaderLeft2Click() {
    ToastUtil.getInstance(getBaseContext()).show("onHeaderLeft2Click");
  }

  @Override
  public void onHeaderRightClick() {
    ToastUtil.getInstance(getBaseContext()).show("onHeaderRightClick");
  }

  @Override
  public void onHeaderIconRightClick() {
    ToastUtil.getInstance(getBaseContext()).show("onHeaderIconRightClick");
  }

  @Override
  public void onHeaderTextRightClick() {
    showDownloadClearDialog();
  }
  private void callDownloadClear(boolean isAll) {
    Loading.show(this);
    if (isAll) {
      final DownloadModel file = DownloadModelQuery.getByDownload(Voca.getRealm());
      if (file != null) {
        cancelDownload(file);
      }
      DownloadModelQuery.deleteAll(Voca.getRealm());
    } else {
      DownloadModelQuery.deleteByComplete(Voca.getRealm());
    }
    Loading.hide();
    eventBus.post(new SuccessEvent(BaseEvent.Screen.PLAYER_DOWNLOAD, BaseEvent.EventType.PLAYER_DOWNLOAD_INIT, null));
  }
  private void showDownloadClearDialog() {
    final PlayerDownloadClearDialog dialog = new PlayerDownloadClearDialog(this, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        switch (i) {
          case R.id.tv_clear_all:
            callDownloadClear(true);
            break;
          case R.id.tv_clear_completed:
            callDownloadClear(false);
            break;
        }
      }
    });
    dialog.show();
  }

  public void openMainNetworkItemFragment(ServerModel serverModel) {
    if(!isNetwork()) {
      getAlertDialog().showNoInternet();
      return;
    }
    Loading.show(this);
    DLog.d(getLogTag(), "openMainPlayerFragment");
    ServerManager.getInstance().setServerModel(serverModel);
    binding.header.setIconLeft(R.drawable.ic_back);
//        toolbar.setIconRight(R.drawable.ic_search_white_24dp);
//        toolbar.setIconRight2(R.drawable.ic_toolbar_sort);
    binding.header.getIconRight2().setVisibility(View.GONE);
    binding.header.getIconRight().setVisibility(View.GONE);
    binding.header.getTvRight().setVisibility(View.GONE);
    setTitle(serverModel.getTitle());
    binding.header.showTitle();
    Fragment fragment = new PlayerNetworkFragmentSub();
    final Bundle bundle = new Bundle();
    bundle.putParcelable(Constant.PLAYER.INTENT.KEY_DATA, serverModel);
    fragment.setArguments(bundle);
    Utils.loadFragment(PlayerNetworkActivity.this, fragment, getFragmentContainerId());
  }

  public void openMainNetworkDownloadFragment(ServerModel serverModel) {
    DLog.d(getLogTag(), "openMainPlayerDownloadFragment");
    binding.header.setIconLeft(R.drawable.ic_back);
    binding.header.getIconRight().setVisibility(View.GONE);
    binding.header.getIconRight2().setVisibility(View.GONE);
    binding.header.setTextRight(R.string.clear);
    binding.header.getTvRight().setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
    binding.header.hideSearchView();
    setTitle(serverModel.getTitle());
    binding.header.showTitle();
    Fragment fragment = new MultiPlayerDownloadFragment();
    Utils.loadFragment(this, fragment, getFragmentContainerId());
  }
  public void addDownload(DownloadModel model) {
    model.setServerModel(ServerModelQuery.getById(Voca.getRealm(), model.getIdServer()));
    DLog.d(getLogTag(), "addDownload - model=" + model.toString());
    Intent intent = new Intent(this, DownloadingService.class);
    intent.putExtra(DownloadingService.FILE, model);
    startService(intent);
  }

  public void cancelDownload(DownloadModel model) {
    DLog.d(getLogTag(), "cancelDownload - model=" + model.toString());
    Intent i = new Intent();
    i.setAction(DownloadingService.ACTION_CANCEL_DOWNLOAD);
    i.putExtra(DownloadingService.ID, model.getId());
    LocalBroadcastManager.getInstance(this).sendBroadcast(i);
  }
}