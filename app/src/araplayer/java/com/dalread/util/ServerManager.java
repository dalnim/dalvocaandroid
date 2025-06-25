package com.dalread.util;

import com.dalread.database.VideoModelQuery;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.ServerModel;
import com.dalread.model.VideoModel;
import com.dalread.util.sardine.DavResource;
import com.dalread.util.sardine.Sardine;
import com.dalread.util.sardine.impl.OkHttpSardine;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class ServerManager {

    private final String TAG = "FTPClientUtil";

    private static ServerManager instance;

    private FTPClient ftpClient;
    private Sardine webDAV;
    private ServerModel serverModel;

    private ServerManager() {
        initFTPClient();
        this.webDAV = new OkHttpSardine();
    }

    public static ServerManager getInstance() {
        if (instance == null) {
            instance = new ServerManager();
        }
        return instance;
    }

    private void initFTPClient() {
        this.ftpClient = new FTPClient();
        ftpClient.setAutodetectUTF8(true);
    }

    public List<PlayerFileModel> getListFileFromFTPServer(String path) {
        List<PlayerFileModel> listFiles = new ArrayList<>();
        try {
            // connect ftp server
            if (!ftpClient.isConnected()) {
                connectFTPServer();
            }
            if (path.isEmpty()) {
                ftpClient.changeToParentDirectory();
            }
            ftpClient.changeWorkingDirectory(path);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile f : ftpFiles) {
                PlayerFileModel file = new PlayerFileModel(f.getName(), f.isDirectory());
                file.setSize(f.getSize());
                file.setPath(path + File.separator + f.getName());
                listFiles.add(file);
                DLog.d(TAG, "file=" + file.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            initFTPClient();
            return null;
        }
        return listFiles;
    }

    private void connectFTPServer() throws IOException {
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

    public void abortConnection() {
        DLog.d(TAG, "abortConnection");
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                if (ftpClient.abort()) {
                    // Connection aborted!
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnectFTPServer() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                DLog.d(TAG, "disconnectFTPServer");
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException ex) {
                ex.printStackTrace();
                initFTPClient();
            }
        }
    }

    public ServerModel getServerModel() {
        return serverModel;
    }

    public void setServerModel(ServerModel model) {
        if (serverModel != null && serverModel.getId() == model.getId()) return;
        serverModel = model;
    }

    public List<PlayerFileModel> getListFileFromWebDAVServer(String path, int mediaType) {
        DLog.d(TAG, "getListFileFromWebDAVServer - path=" + path);
        List<PlayerFileModel> listFiles = new ArrayList<>();
        // connect ftp server
        webDAV.setCredentials(serverModel.getAccount(), serverModel.getPassword());
        try {
            List<PlayerFileModel> playerFileInDBList = fetchAllVideosFromDB();
            List<DavResource> resources = webDAV.list(serverModel.getPath() + path);
            for (DavResource f : resources) {
                if (!Utils.isEmpty(f.getName()) && f.getPath().replace(path, "").length() > 1) {
                    DLog.d(TAG, f.getPath() + " - lenght=" + f.getPath().replace(path, "").length());
                    PlayerFileModel file = new PlayerFileModel(f.getName(), f.isDirectory());
                    file.setSize(f.getContentLength());
                    file.setPath(path + File.separator + f.getName());
                    file.getVideoModel().setVideoFromNetwork(Constant.INT_BOOLEAN.TRUE);
                    file.setServerModel(serverModel);
                    for (PlayerFileModel pfm : playerFileInDBList) {
                        if (pfm.getPath().equals(file.getPath())) {
                            file.setVideoModel(pfm.getVideoModel());
                            break;
                        }
                    }
                    listFiles.add(file);
                    DLog.d(TAG, "file=" + file.toString());
                }
            }
            // sync subtitle
            if (listFiles.size() > 0) {
                for (PlayerFileModel fileModel : listFiles) {
                    for (DavResource f : resources) {
                        if (!f.isDirectory() && FileUtil.checkSubtitleExtension(f.getName())) {
                            if (StorageUtil.checkVideoAndSubName(fileModel.getName(), f.getName())) {
                                DLog.d(TAG, "sub file=" + f.getPath());
                                fileModel.setSubPath(f.getPath());
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return listFiles;
    }

    public static InputStream getInputStream(String path, String accountPassword) throws IOException {
        URL url = new URL(path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", NetworkUtil.generateBasicAuth(accountPassword));
        connection.connect();
        return connection.getInputStream();
    }

    private List<PlayerFileModel> fetchAllVideosFromDB() {
        List<PlayerFileModel> list = new ArrayList<>();
        try (Realm realm = Voca.getRealm()) {
            final List<VideoModel> videos = VideoModelQuery.getAll(realm, true, true);
            if (videos != null) {
                for (VideoModel v : videos) {
                    DLog.d(TAG, "fetchAllVideo: " + v);
                    list.add(new PlayerFileModel(v));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
