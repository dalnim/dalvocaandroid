package com.dalread.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dalread.model.DownloadModel;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.FTPDownload;
import com.dalread.util.ServerManager;
import com.dalread.util.StorageUtil;
import com.dalread.util.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadingService extends IntentService {

    private final String TAG = "DownloadingService";
    public static final String ID = "id";
    public static final String PROGRESS_UPDATE_ACTION = DownloadingService.class.getName() + ".progress_update";
    public static final String ACTION_CANCEL_DOWNLOAD = DownloadingService.class.getName() + "action_cancel_download";
    public static final String PROGRESS_COMPLETED_ACTION = DownloadingService.class.getName() + ".progress_completed";
    public static final String PROGRESS_UPDATE_DATA_ACTION = DownloadingService.class.getName() + ".progress_update_data";
    public static final String FILE = "file";

    private boolean mIsAlreadyRunning;
    private boolean mReceiversRegistered;

    private ExecutorService mExec;
    private CompletionService<NoResultType> mEcs;
    private LocalBroadcastManager mBroadcastManager;
    private DownloadTask mTask;

    private static final long INTERVAL_BROADCAST = 800;
    private long mLastUpdate = 0;
    // only 1 at a time
    private final int DOWNLOAD_NUMBER_AT_TIME = 1;
    private Context context;

    public DownloadingService() {
        super("DownloadingService");
        DLog.d(TAG, "DownloadingService");
        this.context = DownloadingService.this;
        mExec = Executors.newFixedThreadPool(DOWNLOAD_NUMBER_AT_TIME);
        mEcs = new ExecutorCompletionService<>(mExec);
        mBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mIsAlreadyRunning) {
            publishCurrentProgressOneShot(true);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DLog.d(TAG, "onHandleIntent - mIsAlreadyRunning=" + mIsAlreadyRunning);
        if (mIsAlreadyRunning) {
            return;
        }
        mIsAlreadyRunning = true;

        DownloadModel file = intent.getParcelableExtra(FILE);
        file.setStatus(Constant.PLAYER.SERVER.DOWNLOAD.STATUS.DOWNLOAD);
        DLog.d(TAG, "onHandleIntent - file=" + file.toString());
        publishUpdate(file);
        mTask = new DownloadingService.DownloadTask(file);
        mEcs.submit(mTask);

        // wait for finish
        DownloadingService.NoResultType r;
        try {
            r = mEcs.take().get();
            if (r != null) {
                // use you result here
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        // send a last broadcast
        publishCurrentProgressOneShot(true);
        mExec.shutdown();
        publishCompleted(mTask.mFile);
        DLog.d(TAG, "FINISHED SERVICE");
    }

    private synchronized void publishUpdate(DownloadModel file) {
        DLog.d(TAG, "publishUpdate");
        Intent i = new Intent();
        i.putExtra(FILE, file);
        i.setAction(PROGRESS_UPDATE_DATA_ACTION);
        mBroadcastManager.sendBroadcast(i);
    }

    private synchronized void publishCompleted(DownloadModel file) {
        DLog.d(TAG, "publishCompleted");
        Intent i = new Intent();
        i.putExtra(FILE, file);
        i.setAction(PROGRESS_COMPLETED_ACTION);
        mBroadcastManager.sendBroadcast(i);
    }

    private void publishCurrentProgressOneShot(boolean forced) {
        if (forced
                || System.currentTimeMillis() - mLastUpdate > INTERVAL_BROADCAST) {
            mLastUpdate = System.currentTimeMillis();
            publishProgress(mTask.mFile);
        }
    }

    private synchronized void publishProgress(DownloadModel file) {
//        DLog.d(TAG, "publishProgress - size=" + file.getCurrentSize() + "/" + file.getSize());
        Intent i = new Intent();
        i.setAction(PROGRESS_UPDATE_ACTION);
        i.putExtra(FILE, file);
        mBroadcastManager.sendBroadcast(i);
    }

    class DownloadTask implements Callable<NoResultType> {
        private boolean mCancelled;
        private final DownloadModel mFile;

        public DownloadTask(DownloadModel file) {
            mFile = file;
        }

        private void downloadFile() {
            if (mFile.getServerModel().isFTP()) {
                FTPDownload.getInstance().setServerModel(mFile.getServerModel());
                if (!FTPDownload.getInstance().getFtpClient().isConnected()) {
                    try {
                        FTPDownload.getInstance().connectFTPServer();
                    } catch (IOException e) {
                        e.printStackTrace();
                        mFile.setStatus(Constant.PLAYER.SERVER.DOWNLOAD.STATUS.PAUSE);
                        FTPDownload.getInstance().disconnectFTPServer();
                        mCancelled = true;
                    }
                }
            }
            downloadFTPFile();
        }

        private void downloadFTPFile() {
            try {
                String downloadFolder = StorageUtil.getMediaAbsoluteFolder(context);
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH-mm-ss", Locale.getDefault());
                String currentTime = dateFormat.format(new Date());
                //그냥 mFile.getName()을 쓰면, exists는 false가 나는데, createNewFile이 안된다. 아마 무슨 권한 이슈 같다.
                File localFile1 = new File(downloadFolder, currentTime + mFile.getName());
                File localFile = new File(downloadFolder, mFile.getName());
                long localFileSize = localFile.length();
                if (!localFile.exists()) {
                    localFile.getParentFile().mkdirs();
                    localFile.createNewFile();
                }
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile, true));
                byte[] bytesArray = new byte[4096];
                int bytesRead;
                long totalRead = localFileSize;
                InputStream inputStream;
                if (mFile.getServerModel().isFTP()) {
                    FTPDownload.getInstance().getFtpClient().setRestartOffset(localFileSize);
                    FTPDownload.getInstance().getFtpClient().setBufferSize(2024 * 2048);
                    inputStream = FTPDownload.getInstance().getFtpClient().retrieveFileStream(mFile.getPath());
                } else {
                    String path = mFile.getServerModel().getPath(mFile.getPath());
                    inputStream = new BufferedInputStream(ServerManager.getInstance().getInputStream(path, mFile.getServerModel().getAccountPassword()));
                }
                while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                    totalRead += bytesRead;
                    outputStream.write(bytesArray, 0, bytesRead);
                    mFile.setCurrentSize(totalRead);
                    publishProgress(mTask.mFile);
                    if(mCancelled) {
                        mFile.setStatus(Constant.PLAYER.SERVER.DOWNLOAD.STATUS.PAUSE);
                        FTPDownload.getInstance().abortConnection();
                        break;
                    }
                }
                if (mFile.getServerModel().isFTP()) {
                    if (FTPDownload.getInstance().getFtpClient().completePendingCommand()) {
                        // task completed
                    }
                }
                if (!mCancelled) {
                    mFile.setStatus(Constant.PLAYER.SERVER.DOWNLOAD.STATUS.COMPLETE);
                }
                inputStream.close();
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                mFile.setStatus(Constant.PLAYER.SERVER.DOWNLOAD.STATUS.PAUSE);
                FTPDownload.getInstance().disconnectFTPServer();
                mCancelled = true;
            } finally {
//                FTPDownload.getInstance().disconnectFTPServer();
            }
        }

        @Override
        public DownloadingService.NoResultType call() {
            downloadFile();
            DLog.d(TAG, "end call NoResultType.");
            return new DownloadingService.NoResultType();
        }

        public DownloadModel getFile() {
            return mFile;
        }

        public void cancel() {
            mTask.mFile.setStatus(Constant.PLAYER.SERVER.DOWNLOAD.STATUS.PAUSE);
            mCancelled = true;
            FTPDownload.getInstance().setDownloadCancel(true);
        }

    }


    private void registerReceiver() {
        unregisterReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadingService.ACTION_CANCEL_DOWNLOAD);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mCommunicationReceiver, filter);
        mReceiversRegistered = true;
    }

    private void unregisterReceiver() {
        if (mReceiversRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(
                    mCommunicationReceiver);
            mReceiversRegistered = false;
        }
    }

    private final BroadcastReceiver mCommunicationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadingService.ACTION_CANCEL_DOWNLOAD)) {
                final String id = intent.getStringExtra(ID);
                if (!Utils.isEmpty(id)) {
                    if (mTask.mFile.getId().equalsIgnoreCase(id)) {
                        mTask.cancel();
                    }
                }
            }
        }
    };

    class NoResultType {
    }
}
