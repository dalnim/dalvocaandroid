package com.dalread.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dalread.R;
import com.dalread.base.BasePlayerActivity;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityPlayerBinding;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.FileUtil;
import com.dalread.util.Utils;

import org.greenrobot.eventbus.Subscribe;

public class PlayerActivity extends BasePlayerActivity {
    public boolean isMusicPlaylist = false;
    private PowerManager.WakeLock wakeLock;
    private ActivityPlayerBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }

    @Override
    protected void setFullscreen() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isMusicPlaylist = getIntent().getBooleanExtra(Constant.PLAYER.INTENT.KEY_MUSIC_PLAYLIST, false);
//        acquireWakeLock();
    }

//    private void acquireWakeLock() {
//        PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
//        if (powerManager.isInteractive()) {
//
//            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                    "MyApp::MyWakelockTag");
//            wakeLock.acquire();
//
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
//        PermissionUtils.checkSystemWritePermission(this);
    }

    @Override
    public void finish() {
        sharedPreferences.setList(Constant.SHARE_PREF.KEY_MUSIC_PLAYLIST, null);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        if (!FileUtil.isMusicApp()) {
            Utils.toggleFullscreen(this, false);
        }
        super.onDestroy();
//        releaseWakeLock();
    }

//    private void releaseWakeLock() {
//        if (wakeLock != null) {
//            wakeLock.release();
//        }
//    }

    @Override
    public void onHeaderLeftClick() {

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

    }

    @Subscribe
    public void onEvent(SuccessEvent event) {

    }

    @Override
    public void initView() {
//        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
//            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//                DLog.d(getLogTag(), "not full screen");
//                Utils.toggleFullscreen(this);
////                getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(null);
//            }
//        });
        setTransparentStatusBarAndNavigationBar();
        initEventBus();
        screen = BaseEvent.Screen.DAL_PLAYER;
        initData();
    }

    public void setTransparentStatusBarAndNavigationBar() {
        if (FileUtil.isMusicApp()) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
            return;
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.status_and_navigation_bar_transparent_color));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.status_and_navigation_bar_transparent_color));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void initData() {
//        initPlayVocaHelper();
        openPlayerFragment();
    }

    DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
    };

    private void openPlayerFragment() {
        Bundle b = getIntent().getExtras();
        if (b == null) {
            finish();
            return;
        }
        Fragment fragment = new PlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(Constant.PLAYER.INTENT.KEY_VIDEO_TIME_TO_START, b.getLong(Constant.PLAYER.INTENT.KEY_VIDEO_TIME_TO_START));
        bundle.putParcelable(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, b.getParcelable(Constant.PLAYER.INTENT.KEY_VIDEO_FILE));
        fragment.setArguments(bundle);
        Utils.loadFragment(this, fragment, getFragmentContainerId(), false);
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_UP) {
//            final int keyCode = event.getKeyCode();
//            DLog.d(getLogTag(), "dispatchKeyEvent keyCode=" + keyCode);
//            ToastUtil.getInstance(this).show("PlayerActivity dispatchKeyEvent keyCode=" + keyCode);
//            eventBus.post(new SuccessEvent(BaseEvent.Screen.DAL_PLAYER, BaseEvent.EventType.KEYBOARD_EVENT, keyCode));
//            return true;
//        }
//        return super.dispatchKeyEvent(event);
//    }

    //Dalnim : At first I moved this to VocaActivity but when I click the Hardware back button, AraPlayer doesn't go back. So I moved the code back here.
    // ear phone
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            final int keyCode = event.getKeyCode();
            DLog.d(getLogTag(), "dispatchKeyEvent keyCode=" + keyCode);
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
                eventBus.post(new SuccessEvent(BaseEvent.Screen.MEDIA_BUTTON, BaseEvent.EventType.MEDIA_BUTTON_HEADSETHOOK, true));
//                ToastUtil.getInstance(this).show("dispatchKeyEvent KEYCODE_HEADSETHOOK keyCode=" + keyCode);
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                eventBus.post(new SuccessEvent(BaseEvent.Screen.MEDIA_BUTTON, BaseEvent.EventType.MEDIA_BUTTON_PLAY, true));
//                ToastUtil.getInstance(this).show("dispatchKeyEvent KEYCODE_MEDIA_PAUSE keyCode=" + keyCode);
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                eventBus.post(new SuccessEvent(BaseEvent.Screen.MEDIA_BUTTON, BaseEvent.EventType.MEDIA_BUTTON_PAUSE, true));
//                ToastUtil.getInstance(this).show("dispatchKeyEvent KEYCODE_MEDIA_PLAY keyCode=" + keyCode);
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                //TODO : Dalnim - When I first open a PlayerFragment, dispatchKeyEvent is called. but if I open a word list view and back to the PlayerFragment then notthing is called.
                //TODO : This is called when I'm only in the PlayerFragment
                eventBus.post(new SuccessEvent(BaseEvent.Screen.MEDIA_BUTTON, BaseEvent.EventType.MEDIA_MEDIA_PLAY_OR_PAUSE, true));
//                ToastUtil.getInstance(this).show("dispatchKeyEvent KEYCODE_MEDIA_PLAY_PAUSE keyCode=" + keyCode);
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
                eventBus.post(new SuccessEvent(BaseEvent.Screen.MEDIA_BUTTON, BaseEvent.EventType.MEDIA_MEDIA_STOP, true));
//                ToastUtil.getInstance(this).show("dispatchKeyEvent KEYCODE_MEDIA_STOP keyCode=" + keyCode);
            } else {
                eventBus.post(new SuccessEvent(BaseEvent.Screen.DAL_PLAYER, BaseEvent.EventType.KEYBOARD_EVENT, keyCode));
//                ToastUtil.getInstance(this).show("dispatchKeyEvent else keyCode=" + keyCode);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
