package com.dalread.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.dalread.BaseApplication;
import com.dalread.R;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.ServerModel;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.NetworkUtil;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;

public class PlayerService extends Service {
    public static final String PLAYER_SERVICE_BUNDLE_KEY = "com.dalread.service.PlayerService.PLAYER_SERVICE_BUNDLE_KEY";
    public static final String PLAYER_FILE_KEY = "com.dalread.service.PlayerService.PLAYER_FILE_KEY";
    public static final String PLAYBACK_CHANNEL_ID = "playback_channel";
    public static final String PLAYER_RECEIVER_ACTION = "player_receiver_action";
    public static final int PLAYBACK_NOTIFICATION_ID = 9999;

    private final IBinder mBinder = new LocalBinder();
    private SimpleExoPlayer player;
    private PlayerFileModel playerFileModel;
    private PlayerNotificationManager playerNotificationManager;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PLAYER_RECEIVER_ACTION);
        filter.addAction(PlayerNotificationManager.ACTION_PLAY);
        filter.addAction(PlayerNotificationManager.ACTION_PAUSE);
        mReceiver = new PlayerReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void releasePlayer() {
        if (player != null) {
            playerNotificationManager.setPlayer(null);
            player.release();
            player = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public SimpleExoPlayer getPlayerInstance() {
        if (player == null) {
            startPlayer();
        }

        return player;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            getPlayerFileFromIntent(intent);
        }
        return START_STICKY;
    }

    private void getPlayerFileFromIntent(Intent intent) {
        Bundle b = intent.getBundleExtra(PLAYER_SERVICE_BUNDLE_KEY);
        if (b != null) {
            playerFileModel = b.getParcelable(PLAYER_FILE_KEY);
        }
        if (player == null) {
            startPlayer();
        } else {
            setMediaSourceForPlayer();
        }
    }

    private void startPlayer() {
        try {
            final Context context = this;
            player = new SimpleExoPlayer.Builder(context).build();
            player.setRepeatMode(Player.REPEAT_MODE_OFF);
            setMediaSourceForPlayer();

            PlayerNotificationManager.Builder playerNotificationManagerBuilder =
                    new PlayerNotificationManager.Builder(context, PLAYBACK_NOTIFICATION_ID, PLAYBACK_CHANNEL_ID,
                            new PlayerNotificationManager.MediaDescriptionAdapter() {
                                @Override
                                public String getCurrentContentTitle(Player player) {
                                    return playerFileModel.getName();
                                }

                                @SuppressLint("UnspecifiedImmutableFlag")
                                @Override
                                public PendingIntent createCurrentContentIntent(Player player) {
                                    return null;
                                }

                                @Override
                                public String getCurrentContentText(Player player) {
                                    return "";
                                }

                                @Override
                                public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                                    return null;
                                }
                            }
                    );
            playerNotificationManagerBuilder.setChannelNameResourceId(R.string.default_notification_channel_id);
            playerNotificationManagerBuilder.setNotificationListener(new PlayerNotificationManager.NotificationListener() {
                @Override
                public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                    if (ongoing) {
                        startForeground(notificationId, notification);
                    } else {
                        stopForeground(false);
                    }
                }

                @Override
                public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                    stopSelf();
                }
            });

            playerNotificationManager = playerNotificationManagerBuilder.build();
            playerNotificationManager.setPlayer(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMediaSourceForPlayer() {
        String userAgent = NetworkUtil.getDefaultUserAgent();
        ProgressiveMediaSource mediaSource;
        if (playerFileModel.isWebDAV()) {
            Uri videoUri = Uri.parse(playerFileModel.getServerModel().getPath(playerFileModel.getPath()));
            mediaSource = buildMediaSource(playerFileModel.getServerModel(), videoUri, userAgent);
        } else {
            Uri videoUri = (new Uri.Builder()).path(playerFileModel.getPath()).build();
            mediaSource = new ProgressiveMediaSource.Factory(
                    new DefaultDataSourceFactory(this, userAgent),
                    new DefaultExtractorsFactory()
            ).createMediaSource(MediaItem.fromUri(videoUri));
        }
        player.setMediaSource(mediaSource);
        player.prepare();
        player.setPlayWhenReady(false);
    }

    private ProgressiveMediaSource buildMediaSource(ServerModel serverModel, final Uri uri, String userAgent) {
        HttpDataSource.BaseFactory myDSFactory = new HttpDataSource.BaseFactory() {
            @Override
            protected HttpDataSource createDataSourceInternal(@NonNull HttpDataSource.RequestProperties defaultRequestProperties) {
                final String auth = NetworkUtil.generateBasicAuth(serverModel.getAccountPassword());
                DefaultHttpDataSource.Factory dsf = new DefaultHttpDataSource.Factory();
                dsf.setUserAgent(userAgent);
                HttpDataSource ds = dsf.createDataSource();
                ds.setRequestProperty("Authorization", auth);
                return ds;
            }
        };

        ProgressiveMediaSource.Factory emf = new ProgressiveMediaSource.Factory(myDSFactory);
        return emf.createMediaSource(MediaItem.fromUri(uri));
    }

    public class LocalBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    public class PlayerReceiver  extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case PLAYER_RECEIVER_ACTION:
                    getPlayerFileFromIntent(intent);
                    break;
                case PlayerNotificationManager.ACTION_PLAY:
                    BaseApplication.getInstance().getEventBus().post(new SuccessEvent(BaseEvent.Screen.NOTIFICATION, BaseEvent.EventType.MEDIA_BUTTON_PLAY, null));
                    break;
                case PlayerNotificationManager.ACTION_PAUSE:
                    BaseApplication.getInstance().getEventBus().post(new SuccessEvent(BaseEvent.Screen.NOTIFICATION, BaseEvent.EventType.MEDIA_BUTTON_PAUSE, null));
                    break;
            }

        }

        public PlayerReceiver(){

        }
    }
}
