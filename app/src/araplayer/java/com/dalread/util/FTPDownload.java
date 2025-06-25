package com.dalread.util;

import com.dalread.model.ServerModel;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;

public class FTPDownload {
    private final String TAG = "FTPDownload";

    private FTPClient ftpClient;
    private ServerModel serverModel;
    private boolean isDownloadCancel = false;
    private static FTPDownload instance;

    private FTPDownload() {
        this.ftpClient = new FTPClient();
        ftpClient.setAutodetectUTF8(true);
        ftpClient.setControlEncoding("UTF-8");
    }

    public static FTPDownload getInstance() {
        if (instance == null) {
            instance = new FTPDownload();
        }
        return instance;
    }

    public void connectFTPServer() throws IOException {
        DLog.d(TAG, "connectFTPServer");
        // connect to ftp server
        ftpClient.setDefaultTimeout(Constant.PLAYER.SERVER.TIMEOUT);
        ftpClient.connect(serverModel.getHost(), Utils.parseInt(serverModel.getPort()));
        // run the passive mode command
        ftpClient.enterLocalPassiveMode();
        // check reply code
        if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            disconnectFTPServer();
            throw new IOException("FTP server not respond!");
        } else {
            ftpClient.setSoTimeout(Constant.PLAYER.SERVER.TIMEOUT);
            ftpClient.setControlKeepAliveTimeout(1);//Send An Keep Alive Message every second
            ftpClient.setControlKeepAliveReplyTimeout(5000);//Wait atleast 5 seconds to respond to my KeepAlive messages
            // login ftp server
            if (!ftpClient.login(serverModel.getAccount(), serverModel.getPassword())) {
                throw new IOException("Username or password is incorrect!");
            }
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setDataTimeout(Constant.PLAYER.SERVER.TIMEOUT);
            ftpClient.sendCommand("OPTS UTF8 ON");
            DLog.d(TAG, "connectFTPServer connected");
        }
    }

//    public void downloadFile(Context context, DownloadModel downloadModel, OnDownloadListener listener) {
//        DLog.d(TAG, "downloadFile - filename=" + downloadModel.getName() + " - remoteFilePath=" + downloadModel.getPath());
//        if (!ftpClient.isConnected()) {
//            connectFTPServer();
//        }
//        boolean success = false;
//        OutputStream fos = null;
//        try {
//            File localFile = new File(StorageUtil.getVideoPath(context, downloadModel.getPath()));
//            long currentSize = 0;
//            if (localFile.exists()) {
//                // If file exist set append=true, set ofset localFile size and resume
//                fos = new FileOutputStream(localFile, true);
//                ftpClient.setRestartOffset(localFile.length());
//                currentSize = localFile.length();
//            } else {
//                // Create file with directories if necessary(safer) and start download
//                localFile.getParentFile().mkdirs();
//                localFile.createNewFile();
//                fos = new FileOutputStream(localFile);
//            }
//            long finalLenghtOfFile = downloadModel.getSize();
//            final long[] currentProgress = {-1};
//            long finalCurrentSize = currentSize;
//            CountingOutputStream cos = new CountingOutputStream(fos) {
//                protected void beforeWrite(int n) {
//                    super.beforeWrite(n);
//                    final long pos = getCount() + finalCurrentSize;
//                    if (currentProgress[0] != pos) {
//                        currentProgress[0] = pos;
//                        if (listener != null) {
//                            listener.onProgressUpdate(currentProgress[0]);
//                        }
//                    }
//                }
//            };
//            ftpClient.setBufferSize(2024 * 2048);//To increase the  download speed
//            success = ftpClient.retrieveFile(downloadModel.getPath(), cos);
//        } catch (Exception ex) {
//            DLog.e(TAG, "Could not download file " + ex.getMessage());
//            success = false;
//        } finally {
//            DLog.d(TAG, "filename=" + downloadModel.getName() + " - success=" + success);
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    DLog.e(TAG, e.getMessage());
//                }
//            }
//            if (listener != null) {
//                if (success) {
//                    listener.onCompleted();
//                } else {
//                    listener.onFailed();
//                }
//            }
//        }
//    }

//    public void downloadFile(Context context, DownloadModel downloadModel, OnDownloadListener listener) {
//        try {
//            if (!ftpClient.isConnected()) {
//                connectFTPServer();
//            }
//            File localFile = new File(StorageUtil.getVideoPath(context), downloadModel.getPath());
//            // get file size if the file exists
//            long localFileSize = localFile.length();
//            //retrieve file from server
//            if (!localFile.exists()) {
//                localFile.getParentFile().mkdirs();
//                localFile.createNewFile();
//            }
//            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile, true));
//            byte[] bytesArray = new byte[4096];
//            int bytesRead;
//            long totalRead = localFileSize;
//            ftpClient.setRestartOffset(localFileSize);
//            ftpClient.setBufferSize(2024 * 2048);
//            InputStream inputStream = ftpClient.retrieveFileStream(downloadModel.getPath());
//            while ((bytesRead = inputStream.read(bytesArray)) != -1) {
//                totalRead += bytesRead;
//                outputStream.write(bytesArray, 0, bytesRead);
//                downloadModel.setCurrentSize(totalRead);
//                if (listener != null) {
//                    listener.onProgressUpdate(downloadModel);
//                }
//                if(isDownloadCancel()) {
//                    downloadModel.setStatus(Constant.PLAYER.SERVER.DOWNLOAD.STATUS.PAUSE);
//                    if (listener != null) {
//                        listener.onFailed(downloadModel);
//                    }
//                    abortConnection();
//                    break;
//                }
//            }
//            if (ftpClient.completePendingCommand()) {
//            }
//            if(!isDownloadCancel()) {
//                downloadModel.setStatus(Constant.PLAYER.SERVER.DOWNLOAD.STATUS.COMPLETE);
//                if (listener != null) {
//                    listener.onCompleted(downloadModel);
//                }
//            }
//            outputStream.flush();
//            inputStream.close();
//            outputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            downloadModel.setStatus(Constant.PLAYER.SERVER.DOWNLOAD.STATUS.PAUSE);
//            FTPDownload.getInstance().abortConnection();
//            setDownloadCancel(true);
//            if (listener != null) {
//                listener.onFailed(downloadModel);
//            }
//        }
//    }

    public void abortConnection() {
        DLog.d(TAG, "abortConnection");
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                if (ftpClient.abort()) {
                    // Connection aborted!
                }
            } catch (IOException e) {
                e.printStackTrace();
                this.ftpClient = new FTPClient();
            }
        }
    }

    public void disconnectFTPServer() {
        DLog.d(TAG, "disconnectFTPServer");
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                setDownloadCancel(false);
                serverModel = null;
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException ex) {
                ex.printStackTrace();
                this.ftpClient = new FTPClient();
            }
        }
    }

    public ServerModel getServerModel() {
        return serverModel;
    }

    public void setServerModel(ServerModel model) {
        if (serverModel != null && serverModel.getId() == model.getId()) return;
        disconnectFTPServer();
        serverModel = model;
    }

    public boolean isDownloadCancel() {
        return isDownloadCancel;
    }

    public void setDownloadCancel(boolean cancel) {
        isDownloadCancel = cancel;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
