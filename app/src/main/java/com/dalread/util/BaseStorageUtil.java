package com.dalread.util;

import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;

import com.dalread.BuildConfig;
import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.ResponseBody;
import wseemann.media.FFmpegMediaMetadataRetriever;

//BaseFileUtil에도 비슷한 기능이 있음.
public class BaseStorageUtil {

    protected static final String TAG = "StorageUtil";
    private static final Executor executor = Executors.newSingleThreadExecutor();

    protected static String getAbsoluteDir(Context ctx, String optionalPath) {
        String rootPath;
        if (optionalPath != null && !optionalPath.equals(Constant.BASE_BLANK)) {
            rootPath = ctx.getExternalFilesDir(optionalPath).getAbsolutePath();
        } else {
            rootPath = ctx.getExternalFilesDir(null).getAbsolutePath();
        }
        // extraPortion is extra part of file path
        String extraPortion = "Android/data/" + BuildConfig.APPLICATION_ID
                + File.separator + "files" + File.separator;
        // Remove extraPortion
        rootPath = rootPath.replace(extraPortion, Constant.BASE_BLANK);
        DLog.d(TAG, "getAbsoluteDir - rootPath=" + rootPath);
        return rootPath;
    }

    public static String getPath(Context context, String name) {
        return getAraOneRootPath(context) + File.separator + name;
    }

    public static String getVideoPath(Context context, String name) {
        return getVideoPath(context) + File.separator + name;
    }

//    public static String getTempPath(Context context, String name) {
//        return getTempPath(context) + File.separator + name;
//    }

    public static String getAraOneRootPath(Context context, String folder) {
        return getRootFolder() + File.separator + folder;
//        return getAbsoluteDir(context, folder);
    }

    public static String getAraOneRootPathInApp(Context context, String folder) {
        return getAppPath(context) + File.separator + folder;
//        return getAbsoluteDir(context, folder);
    }

    public static String getAraOneRootPath(Context context) {
        return getAraOneRootPath(context, Constant.FOLDER_ARAONE.ROOT);
    }

    public static String getRootFolder() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    //아마 외부SD카드에 있는 동영상을 읽어오는 코드를 만든거 같은데. 일단 쓰지 말자.
    // Failed to find storage device at /data/local/tmp/external 에러가 난다.
    public static String getRootFolderFromSDCard(Context context) {
        return null;
//        List<String> results = new ArrayList<>();
//
//        File[] externalDirs = context.getExternalFilesDirs(null);
//        String internalRoot = Environment.getExternalStorageDirectory().getAbsolutePath().toLowerCase();
//
//        for (File file : externalDirs) {
//            if(file==null)
//                continue;
//            String path = file.getPath().split("/Android")[0];
//
//            if(path.toLowerCase().startsWith(internalRoot))
//                continue;
//
//            boolean addPath = Environment.isExternalStorageRemovable(file);
//
//            if(addPath){
//                results.add(path);
//            }
//        }
//
//        if(results.isEmpty()) {
//            String output = "";
//            try {
//                final Process process = new ProcessBuilder().command("mount | grep /dev/block/vold")
//                        .redirectErrorStream(true).start();
//                process.waitFor();
//                final InputStream is = process.getInputStream();
//                final byte[] buffer = new byte[1024];
//                while (is.read(buffer) != -1) {
//                    output = output + new String(buffer);
//                }
//                is.close();
//            } catch (final Exception e) {
//                e.printStackTrace();
//            }
//            if(!output.trim().isEmpty()) {
//                String devicePoints[] = output.split("\n");
//                for(String voldPoint: devicePoints) {
//                    results.add(voldPoint.split(" ")[2]);
//                }
//            }
//        }
//
//        for (int i = 0; i < results.size(); i++) {
//            if (!results.get(i).toLowerCase().matches(".*[0-9a-f]{4}[-][0-9a-f]{4}")) {
//                results.remove(i--);
//            }
//        }
//
//        String[] storageDirectories = new String[results.size()];
//        for(int i=0; i<results.size(); ++i) {
//            storageDirectories[i] = results.get(i);
//        }
//
//        String path = null;
//        if (storageDirectories.length > 0) {
//            path = storageDirectories[0];
//        }
//        return path;
    }

    public static String getVideoPath(Context context) {
        return getAraOneRootPath(context, Constant.FOLDER_ARAONE.VIDEO_PATH);
    }

    public static String getTempPath(Context context) {
        return getAraOneRootPath(context, Constant.FOLDER_APP.TEMP);
    }

    public static String getAppTempPath(Context context) {
        return getAppPath(context) + File.separator + Constant.FOLDER_APP.TEMP;
    }

    public static String getVoicePath(Context context) {
        return getAraOneRootPath(context, Constant.FOLDER_ARAONE.VOICE_PATH);
    }

    public static String getVoicePathInApp(Context context) {
        return getAraOneRootPathInApp(context, Constant.FOLDER_ARAONE.VOICE_PATH);
    }

    public static String getVoicePath(Context context, String name) {
        return getVoicePath(context) + File.separator + name;
    }

    /*
     * @deprecated Replaced by {@link #getVoiceFolderInApp(Context context)} from BaseVoca
     */
    @Deprecated
    public static File getVoiceFolder(Context context) {
        return new File(getVoicePath(context));
    }

    public static long getCountOfRecordedVoiceFile(Context context) {
        return getCountOfRecordedVoiceFileByUid(context, UserUtil.getUserID(context));
    }

    public static long getCountOfRecordedVoiceFileByUid(Context context, int uid) {
        List<File> fileList = getRecordedVoiceFileListByUid(context, uid);
        return fileList.stream()
                .filter(e -> FilenameUtils.getExtension(e.getName()).toLowerCase().equals(Constant.FILE.EXTENTION_SPEAKING))
                .count();
    }
    public static List<File> getRecordedVoiceFileList(Context context) {
        return getRecordedVoiceFileListByUid(context, UserUtil.getUserID(context));
    }

    public static List<File> getRecordedVoiceFileListByUid(Context context, int uid) {
        List<File> result = new ArrayList<>();
        File userVoiceFolder = new File(BaseVoca.getVoiceFolderInApp(context), SharedPreferencesDB.getInstance(context).getLangStudyCode() + File.separator + uid);
        if (userVoiceFolder.exists() && userVoiceFolder.isDirectory()) {
            result = Arrays.stream(userVoiceFolder.listFiles())
                    .filter(e -> FilenameUtils.getExtension(e.getName()).toLowerCase().equals(Constant.FILE.EXTENTION_SPEAKING))
                    .collect(Collectors.toList());
        }
        return result;
    }
    public static boolean isRootPath(Context context, String path) {
        return path.equals(getAraOneRootPath(context));
    }

    public static boolean isVideoPath(Context context, String path) {
        return path.equals(getVideoPath(context));
    }

    public static String getCurrentPath(String name, String path) {
        if (Utils.isEmpty(path) || Utils.isEmpty(name))
            return Constant.BASE_BLANK;
        return path.replace(File.separator + name, Constant.BASE_BLANK);
    }

    public static String getPrevPath(String path) {
        if (Utils.isEmpty(path) || path.equals(getRootFolder()))
            return Constant.BASE_BLANK;
        return path.substring(0, path.lastIndexOf(File.separator));
    }

    public static void createFolderInApp(Context context, String folderName) {
        String path = getAppPath(context) + File.separator + folderName;
        createFolder(path);
    }

    public static void createFolder(Context context, String folderName) {
        String path = getAbsoluteDir(context, folderName);
        createFolder(path);
    }

    public static void createFolder(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.getParentFile().mkdirs();
            if (dir.mkdir()) {
                DLog.d(TAG, "created dir" + dir.getAbsolutePath());
            } else {
                DLog.d(TAG, "fail to create dir" + dir.getAbsolutePath());
            }
        }
    }

    public static File getFile(String path) {
        return new File(path);
    }
    //이건 폰내 내장메모리에 있는 외부저장소(빌트인 외부저장소)에 있는 앱의 경로를 가져온다. 내부저장소의 경로는 getFilesDir로 가져온다.
    //앱을 꾹 눌러서 삭제하면 내부저장소의 앱 경로의 데이타만 삭제되고 빌트인 외부저장소의 폴더는 삭제 되지 않는다. 수동으로 삭제해야 한다.
    //그래서 사전데이타나 자막sqlite등은 빌트인 외부저장소에 계속 남아서 용량을 차지할수 있다.
    public static String getAppPath(Context context) {
        return context.getExternalFilesDir(null).getAbsolutePath();
    }

    public static void createAllFolderInAppFolder(Context context) {
        if (!AppFlavorUtil.isAraHanjaApp()) {
//            createFolderInApp(context, Constant.FOLDER_IN_APP.ROOT); // I think don't need to make this VOICE_PATH will make ROOT folder too.
            createFolderInApp(context, Constant.FOLDER_IN_APP.VOICE_PATH);
        }
    }

    public static void createAllBaseFolder(Context context) {
        createAppTempFolder(context);
        createAllFolderInAppFolder(context);
        //These codes create folder under Document and its app name. But don't need this because Folder Sharing issue
//        createFolder(context, Constant.FOLDER_ARAONE.APP_ROOT);
//        createFolder(context, Constant.FOLDER_ARAONE.APP_ROOT_VOICE_PATH);

        createBaseFolderAraOne(context);
    }

    //These folders can not be shared between apps over OS 11. So may don't use this later.
    public static void createBaseFolderAraOne(Context context) {
        if (AppFlavorUtil.isAraPlayerApp()) {
            createFolder(context, Constant.FOLDER_ARAONE.ROOT);
            createFolder(context, Constant.FOLDER_ARAONE.CHANNELS_PATH);
            createFolder(context, Constant.FOLDER_ARAONE.IMAGE_PATH);
            createFolder(context, Constant.FOLDER_ARAONE.SOUND_PATH);
//        createFolder(context, Constant.FOLDER_ARAONE.TEMP_PATH);
//        createFolder(context, Constant.FOLDER_ARAONE.TMDB_PATH);
            createFolder(context, Constant.FOLDER_ARAONE.VIDEO_PATH);
            createFolder(context, Constant.FOLDER_ARAONE.VOICE_PATH);
        }
    }

    public static void createAppTempFolder(Context context) {
        createFolder(getAppTempPath(context));
    }

    public static void copyFileToExternalStorage(Context context, int resourceId, String path) {
        DLog.d(TAG, "copyFileToExternalStorage");
        DLog.d(TAG, "path=" + path);
        File file = new File(path);
        if (file.exists()) return;
        try {
            InputStream in = context.getResources().openRawResource(resourceId);
            FileOutputStream out;
            File dir = file.getParentFile();
            if (!dir.isDirectory() && !dir.mkdirs())
                throw new FileNotFoundException("Failed to ensure directory: " + dir.getAbsolutePath());

            out = new FileOutputStream(path);
            byte[] buff = new byte[1024];
            int read = 0;
            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }
        } catch (FileNotFoundException e) {
            DLog.e(TAG, "FileNotFoundException", e);
        } catch (IOException e) {
            DLog.e(TAG, "IOException", e);
        }
    }

    private static boolean createFileWithContents(InputStream in, File destFile) {
        try (OutputStream out = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
            return true;
        } catch (IOException e) {
            DLog.e(TAG, "Failed to write file: " + destFile.getAbsolutePath(), e);
            return false;
        }
    }

    public static void copyFileAssetsToExternalStorageOrOverwrite(Context context, String fileNameInAsset, String destPathWithFileName, boolean overwrite) {
        AssetManager assetManager = context.getAssets();
        try (InputStream in = assetManager.open(fileNameInAsset)) {
            File newFile = new File(destPathWithFileName);
            if (!newFile.getParentFile().exists()) {
                newFile.getParentFile().mkdirs();
            }

            if (!newFile.exists() || overwrite) {
                createFileWithContents(in, newFile);
            }
        } catch (IOException e) {
            DLog.e(TAG, "Exception occurred while copying file", e);
        }
    }

    public static void copyFileAssetsToExternalStorageModifyFileName(Context context, String fileNameInAsset, String destPathWithFileName) {
        AssetManager assetManager = context.getAssets();
        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open(fileNameInAsset);
            File newFile = new File(destPathWithFileName);
            if (!newFile.getParentFile().exists()) {
                newFile.getParentFile().mkdirs();
            }
            if (!newFile.exists()) {
                out = new FileOutputStream(destPathWithFileName);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                out.flush();
                out.close();
            }
            in.close();
        } catch (Exception e) {
            DLog.e(TAG, "Exception", e);
        }
    }

    public static void copyFileAssetsToExternalStorage(Context context, String fileName, String toPath) {
        AssetManager assetManager = context.getAssets();
        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open(fileName);
            String newFileName = toPath + File.separator + fileName;
            File newFile = new File(newFileName);
            if (!newFile.exists()) {
                out = new FileOutputStream(newFileName);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                out.flush();
                out.close();
            }
            in.close();
//            out.flush();
//            out.close();
        } catch (Exception e) {
            DLog.e(TAG, "Exception", e);
        }
    }

    public static void copyFileOrDirAssetsToExternalStorage(Context context, String path, String toPath) {
        AssetManager assetManager = context.getAssets();
        String assets[];
        try {
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFileAssetsToExternalStorage(context, path, toPath);
            } else {
                String fullPath = toPath + File.separator + path;
                File dir = new File(fullPath);
                if (!dir.exists()) {
                    dir.getParentFile().mkdirs();
                    dir.mkdir();
                }
                for (String asset : assets) {
                    copyFileOrDirAssetsToExternalStorage(context,path + File.separator + asset, toPath);
                }
            }
        } catch (IOException ex) {
            DLog.e(TAG, "I/O Exception", ex);
        }
    }

    public static void copyFileToVideoFolder(Context context, int resourceId, String name) {
        copyFileToExternalStorage(context, resourceId, getVideoPath(context, name));
    }

    public static void copySubtitleDatabseFileToVideoFolder(Context context, int resourceId, String path) {
        copyFileToExternalStorage(context, resourceId, path);
    }


    public static void copyVoicesFolderAsset(Context context, String path) {
        copyFileOrDirAssetsToExternalStorage(context, path, getAraOneRootPath(context));
    }

    public static void copy(File src, File dst) {
        try {
            FileInputStream inStream = new FileInputStream(src);
            FileOutputStream outStream = new FileOutputStream(dst);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getDynamicSpace(long diskSpaceUsed) {
        if (diskSpaceUsed <= 0) {
            return "0";
        }

        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(diskSpaceUsed) / Math.log10(1024));
        String pattern = "#,##0.#";

        if (digitGroups < 3) { // B ~ MB
            pattern = "#,##0";
        }
        return new DecimalFormat(pattern).format(diskSpaceUsed / Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }

//    public static void getThumbnail(Context context, ImageView imageView, String path) {
//        getThumbnail(context, imageView, path, R.drawable.ic_video, new RequestOptions());
//    }
//
//    public static void getThumbnailCircle(Context context, ImageView imageView, int defaultImage, String path) {
//        RequestOptions options = new RequestOptions();
//        options.circleCrop();
//        getThumbnail(context, imageView, path, defaultImage, options);
//    }
//
//    public static void getThumbnail(Context context, ImageView imageView, int defaultImage, String path) {
//        getThumbnail(context, imageView, path, defaultImage, new RequestOptions());
//    }
//
//    public static void setBitmapThumbnailFromVideoFile(Context context, PlayerFileModel file) {
//        try {
//            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
//            mediaMetadataRetriever.setDataSource(context, Uri.parse(file.getPath()));
//            long frameTime = 0;
//            long duration = file.getDuration();
//            long time = file.getVideoModel().getLastDuration();
//            if (duration > 0) {
//                if (time <= 0) {
//                    frameTime = duration / 10;
//                } else {
//                    frameTime = time;
//                }
//            }
//            Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(frameTime * Constant.PLAYER.TIMER.SECOND, MediaMetadataRetriever.OPTION_CLOSEST);
//            file.setThumbnail(bitmap);
//            UtilImage.saveImage(generateLastPositionVideoScreenFilepath(context, file.getPath()), bitmap);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public static void getThumbnailFromSavedVideoImageFile(Context context, ImageView imageView, PlayerFileModel playerFileModel, boolean isFullSize, OnLoadImageFromGlideListener listener) {
//        if (playerFileModel == null) {
//            return;
//        }
//        String videoPath = playerFileModel.getPath();
//        long time = playerFileModel.getVideoModel().getLastDuration();
//        long duration = playerFileModel.getVideoModel().getDuration();
//
//        String imageFilePath = generateLastPositionVideoScreenFilepath(context, videoPath);
//        if (StorageUtil.isFileExist(imageFilePath)) {
//            RequestOptions options = new RequestOptions();
//            options.diskCacheStrategy(DiskCacheStrategy.NONE);
//            options.skipMemoryCache(true);
//            if (isFullSize) {
//                options.override(4000,4000);
//            }
//            getThumbnail(context, imageView, imageFilePath, 0, options);
//        } else {
//            getThumbnailVideoFile(context, imageView, videoPath, time, duration, isFullSize);
//
//        }
//    }

//    public static void getThumbnailVideoFile(Context context, ImageView imageView, String path, long time, long duration, boolean isFullSize) {
//        RequestOptions options = new RequestOptions();
//        if (isFullSize) {
//            options.override(4000,4000);
//        }
//        if (duration > 0) {
//            if (time <= 0) {
//                long defaultTime = duration / 10;
//                options.frame(defaultTime * Constant.PLAYER.TIMER.SECOND);
//            } else {
//                options.frame(time * Constant.PLAYER.TIMER.SECOND);
//            }
//        }
//
//        getThumbnail(context, imageView, path, R.drawable.btn_no_border_black_background, options);
//    }

//    public static void getThumbnailFullsize(Context context, ImageView imageView, String path) {
//        if (context == null)
//            return;
//
//        RequestOptions options = new RequestOptions();
//        options.centerInside();
//        options.override(4000,4000);
//
//        Glide.with(context)
//                .load(path)
////                .transition(DrawableTransitionOptions.withCrossFade(1000))
//                .apply(options)
//                .into(imageView);
//    }
//
//    public static void getThumbnail(Context context, ImageView imageView, String path, int defaultImage, RequestOptions options) {
//        if (context == null)
//            return;
//
//        Glide.with(context)
//                .load(path)
//                .placeholder(defaultImage)
//                .apply(options)
//                .into(imageView);
//    }

    public static String getZipPath(Context context) {
        return context.getExternalFilesDir(null) + File.separator + Constant.PLAYER.ZIP_NAME;
    }

    public static String getFolderZip(Context context) {
        return context.getExternalFilesDir(null).toString();
    }

//    public static String getFolderNetworkVideo(Context context, PlayerFileModel playerFileModel) {
//        return getFolderZip(context) + getNetworkPath(playerFileModel);
//    }
//
//    public static String getNetworkPath(PlayerFileModel playerFileModel) {
//        String path = "";
//        ServerModel serverModel = playerFileModel.getServerModel();
//        if (!playerFileModel.isLocal() && serverModel != null) {
//            path += File.separator + Constant.FILE.FOLDER_NETWORK + File.separator + serverModel.getHost() + "_" + serverModel.getPort() + "_" + serverModel.getTitle();
//        }
//        return path;
//    }

    public static String getFolderLocalVideo(Context context) {
        return getFolderZip(context) + getLocalPath();
    }

    public static String getLocalPath() {
        return File.separator + Constant.FILE.FOLDER_LOCAL;
    }

//    public static String getFilesStoragePath(Context context, PlayerFileModel playerFileModel) {
//        String path;
//        if (playerFileModel.isLocal()) {
//            path = getFolderLocalVideo(context);
//        } else {
//            path = getFolderNetworkVideo(context, playerFileModel);
//        }
//        return path;
//    }
//
//    public static String generateLastPositionVideoScreenFilepath(Context context, String videoFileName, PlayerFileModel playerFileModel) {
//        return getFilesStoragePath(context, playerFileModel) + videoFileName + Constant.PLAYER.FILE_EXT_PNG;
//    }
//
//    public static String generateSubtitleSQLitePathUnderAndroidFolder(Context context, String videoFileName, PlayerFileModel playerFileModel) {
//        return getFilesStoragePath(context, playerFileModel) + videoFileName + Constant.PLAYER.DATABASE.SQLITE;
//    }

    public static File writeResponseBodyToDisk(Context context, ResponseBody body) {
        try {
            File futureStudioIconFile = new File(getZipPath(context));
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    DLog.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return futureStudioIconFile;
            } catch (IOException e) {
                return null;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return null;
        }
    }

    //Dalnim Create
    public static void unzip(String zipFile, String targetDir) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry;
            byte[] buffer = new byte[4096];
            while ((zipEntry = zis.getNextEntry()) != null) {
                File file = new File(targetDir, zipEntry.getName());
                String canonicalPath = file.getCanonicalPath();
                if (canonicalPath.startsWith(targetDir)) {
                    if (zipEntry.isDirectory()) {
                        file.mkdirs();
                        continue;
                    }
                    file.getParentFile().mkdirs();
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                    int count;
                    while ((count = zis.read(buffer)) != -1) {
                        out.write(buffer, 0, count);
                    }
                    out.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            removeFile(zipFile);
        }
    }
    //asset 압축파일이 unzip은 안되어서 따로 만들어서 사용함.
    public static boolean unzipFileAndDeleteZipFile(String zipFilePath) {
        File zipFile = new File(zipFilePath);
        File outputDir = zipFile.getParentFile();

        try (InputStream fis = new FileInputStream(zipFilePath);
             ZipInputStream zis = new ZipInputStream(fis)) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                // Skip __MACOSX directory and files starting with ._
                if (zipEntry.getName().startsWith("__MACOSX") || zipEntry.getName().startsWith("._")) {
                    zis.closeEntry();
                    continue;
                }

                File outputFile = new File(outputDir, zipEntry.getName());

                if (zipEntry.isDirectory()) {
                    if (!outputFile.exists()) {
                        outputFile.mkdirs();
                    }
                } else {
                    File parentDir = outputFile.getParentFile();
                    if (!parentDir.exists()) {
                        parentDir.mkdirs();
                    }
                    try (OutputStream os = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            os.write(buffer, 0, length);
                        }
                    }
                }
                zis.closeEntry();
            }
            return true;
        } catch (IOException e) {
            DLog.e(TAG, "Error unzipping file", e);
            return false;
        } finally {
            removeFile(zipFilePath);
        }
    }

    public static void unzip(Context context, File zipFile, File targetDirectory, String filePath) {
        try (FileInputStream fis = new FileInputStream(zipFile)) {
            try (BufferedInputStream bis = new BufferedInputStream(fis)) {
                try (ZipInputStream zis = new ZipInputStream(bis)) {
                    ZipEntry ze;
                    int count;
                    byte[] buffer = new byte[4096];
                    while ((ze = zis.getNextEntry()) != null) {
//                        String name = ze.getName();
//                        if (!Utils.isEmpty(name) && name.toLowerCase().endsWith(Constant.PLAYER.DATABASE.SQLITE)) {
//                            name = videoFileName + Constant.PLAYER.DATABASE.SQLITE;
//                        }
//                        String subtitleDatabasePath = StorageUtil.generateSubtitleSQLitePathUnderAndroidFolder(context, videoFileName);
                        File file = new File(filePath);
                        String canonicalPath = file.getCanonicalPath();
                        if (canonicalPath.startsWith(filePath)) {
//                        File file1 = new File(targetDirectory, name);
                            File dir = ze.isDirectory() ? file : file.getParentFile();
                            if (!dir.isDirectory() && !dir.mkdirs())
                                throw new FileNotFoundException("Failed to ensure directory: " + dir.getAbsolutePath());
                            if (ze.isDirectory())
                                continue;
                            try (FileOutputStream fout = new FileOutputStream(file)) {
                                while ((count = zis.read(buffer)) != -1)
                                    fout.write(buffer, 0, count);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            //handle exception
        } finally {
            removeFile(getZipPath(context));
        }
    }

    public static void removeFile(String path) {
        DLog.d(TAG, "removeFile - path=" + path);
        if (Utils.isEmpty(path))
            return;

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean removeVideoFile(Context context, String filePath, ActivityResultLauncher intentSenderLauncher) {
        boolean isDeleted;
        File file = new File(filePath);
        Uri uri = getFileContentUriId(context, Uri.parse(filePath), MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        ContentResolver resolver = context.getContentResolver();
        try {
            isDeleted = resolver.delete(uri, null, null) > 0;
            if (file.exists()) {
                isDeleted = file.delete();
            }
        }catch (Exception e){
            e.printStackTrace();
            IntentSender intentSender = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                List<Uri> uriList = new ArrayList<>();
                Collections.addAll(uriList, uri);
                intentSender = MediaStore.createDeleteRequest(resolver, uriList).getIntentSender();
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                if (e instanceof RecoverableSecurityException) {
                    intentSender = ((RecoverableSecurityException) e).getUserAction().getActionIntent().getIntentSender();
                }
            }
            if (intentSender != null) {
                // Intent에 filePath 추가
                Intent intent = new Intent();
                intent.putExtra("filePath", filePath);
                IntentSenderRequest request = new IntentSenderRequest.Builder(intentSender)
                        .setFillInIntent(intent)
                        .build();
                intentSenderLauncher.launch(request);
//                intentSenderLauncher.launch(new IntentSenderRequest.Builder(intentSender).build());
            }
            isDeleted = false;
        }
        return isDeleted;
    }

    public static Uri getFileContentUriId(Context context, Uri fileUri, Uri contentUri) {
        String[] projections = {BaseColumns._ID};
        Cursor cursor = context.getContentResolver().query(
                contentUri,
                projections,
                MediaStore.MediaColumns.DATA + "=?",
                new String[]{fileUri.getPath()}, null);
        long id = 0;
        if (cursor != null){
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                id  = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID));
            }
            cursor.close();
        }

        return Uri.withAppendedPath(contentUri, String.valueOf((int)id));
    }

    public static void deleteFolder(String path) {
        if (Utils.isEmpty(path))
            return;
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isFileExist(String path) {
        DLog.d(TAG, "isFileExist - path=" + path);
        File file = new File(path);
        return file.exists();
    }

    public static boolean isSubDatabaseFileExist(String path) {
        if (Utils.isEmpty(path))
            return false;

        return isFileExist(path);
    }

    public static String getNameFromPath(String path) {
        if (Utils.isEmpty(path))
            return Constant.BASE_BLANK;
        return path.substring(path.lastIndexOf(File.separator) + 1);
    }

    public static boolean writeToFile(String path, String data) {
        DLog.d(TAG, "writeToFile - path=" + path);
        try {
            FileWriter out = new FileWriter(new File(path));
            out.write(data);
            out.close();
        } catch (IOException e) {
            DLog.d(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    public static String getName(String name) {
        if (Utils.isEmpty(name))
            return Constant.BASE_BLANK;
        return name.substring(0, name.lastIndexOf("."));
    }

    public static String generateSubtitleFileName(String name, String subtitleFile) {
        return name + "." + subtitleFile;
    }

    public static String getVideoInfo(FFmpegMediaMetadataRetriever mmr) {
        return mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT) +
                " x " + mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) +
                ", " + mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_CODEC);
    }

    public static String getAudioInfo(FFmpegMediaMetadataRetriever mmr) {
        return mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_AUDIO_CODEC);
    }

    public static long getDuration(FFmpegMediaMetadataRetriever mmr, String path) {
        long duration = 0;
        try {
            mmr.setDataSource(path);
            duration = Utils.parseLong(mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception ex) {
            DLog.d(TAG, "error=" + ex.getMessage());
        }
        return duration;
    }

    public static long getDuration(MediaMetadataRetriever mmr) {
        long duration = 0;
        try {
            duration = Utils.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception ex) {
            DLog.d(TAG, "error=" + ex.getMessage());
        }
        return duration;
    }

//    public static void saveImage(String path, Bitmap image, String imageName) {
//        File storageDir = new File(path);
//        boolean success = true;
//        if (!storageDir.exists()) {
//            success = storageDir.getParentFile().mkdirs();
//        }
//        if (success) {
//            File imageFile = new File(storageDir, imageName);
//            try {
//                OutputStream fOut = new FileOutputStream(imageFile);
//                image.compress(
//                        FilenameUtils.getExtension(imageName).equalsIgnoreCase("png") ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG,
//                        100, fOut);
//                fOut.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public static File createCacheFile(Context context, String fileName, String json) {
        File cacheFile = new File(context.getFilesDir(), fileName);
        try {
            FileWriter fw = new FileWriter(cacheFile);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(json);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();

            // on exception null will be returned
            cacheFile = null;
        }

        return cacheFile;
    }

    public static void writeJsonFile(String path, String content) {
        byte[] jsonArray = content.getBytes();
        File fileToSaveJson = new File(path);
        BufferedOutputStream bos;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(fileToSaveJson));
            bos.write(jsonArray);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            DLog.e(TAG, "writeJsonFile error=" + e.getMessage());
        } finally {
            jsonArray = null;
            System.gc();
        }
    }

    public static String readJsonFile(String path) {
        String jsonStr = null;
        try {
            File yourFile = new File(path);
            FileInputStream stream = new FileInputStream(yourFile);
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                jsonStr = Charset.defaultCharset().decode(bb).toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonStr;
    }

    public static String convertStreamToString(InputStream is, String encoding) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, encoding));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (String filePath, String encoding) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin, encoding);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public static String getStringFromFile(String filePath) {
        try {
            File fl = new File(filePath);
            FileInputStream fin = new FileInputStream(fl);
            String ret = convertStreamToString(fin);
            fin.close();
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 또는 예외 처리에 따라 적절히 처리할 수 있음
        }
    }


    public static String getPictureFolderPath(Context context) {
        return getRootFolder() + File.separator + "Pictures" + File.separator + context.getString(R.string.app_name);
    }

//    public static String getFileDownloadFolderFromNetworkPath(Context context) {
//        return getDownloadFolderPath(context);
//    }

    public static String getDownloadFolderPath(Context context) {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    }

    public static String getAraHanjaDBPath(Context context) {
        return getAppPath(context) + File.separator + Constant.ARAHANJA.DATABASE_FOLDER;
    }
    public static String getAraPlayerDicDBPath(Context context) {
        return getAppPath(context) + File.separator + Constant.PLAYER.DATABASE_NAME_ENG;
    }
    //이건 복사할때 한번 쓰고 만다. 압축해제헤면 이것 대신 getAraPlayerDicDBPath를 사용한다.
    public static String getAraPlayerZipDicDBPath(Context context) {
        return getAppPath(context) + File.separator + Constant.PLAYER.ASSET_DATABASE_NAME_ENG_ZIP;
    }
    public static String getAraConvDBPath(Context context) {
        return getAppPath(context) + File.separator + Constant.ARACONV.DATABASE_FOLDER;
    }

    public static String getAraKoicaDBPath(Context context) {
        return getAppPath(context) + File.separator + Constant.ARAKOICA.DATABASE_FOLDER;
    }

    public static String getAraHanjaTessDataathWithFileName(Context context, String fileName) {
        return getAraConvDBPath(context) + File.separator + Constant.ARAHANJA.TESSDATA_FOLDER + File.separator + fileName;
    }

    public static String getAraConvDBPathWithFileName(Context context) {
        return getAraConvDBPath(context) + File.separator + Constant.ARACONV.DATABASE_NAME;
    }

    public static String getAraConvAssetVoiceFilePathWithFileName(Context context) {
        return getAraConvDBPath(context) + File.separator + Constant.ARACONV.DATABASE_NAME;
    }

    public static String getAraMultiPlayerDBPathWithFileName(Context context) {
        return getAraConvDBPath(context) + File.separator + Constant.ARAMULTIPLAYER.DATABASE_NAME;
    }

    public static String getAraKoicaDBPathWithFileName(Context context) {
        return getAraKoicaDBPath(context) + File.separator + Constant.ARAKOICA.DATABASE_NAME;
    }

    public static String getTempRecordFilePathInCacheDir(Context context) {
        String fileName =  DateUtils.getDateFullNoSpace(new Date());
        return BaseFileUtil.getRecordInternalCacheDir(context).getAbsolutePath() + File.separator + fileName + "." + Constant.FILE.EXTENTION_SPEAKING;
    }

    public static void deleteFilesInInternalCacheAsync(final Context context, final List<String> fileExtensions) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                File dir = BaseFileUtil.getRecordInternalCacheDir(context);
                if (dir != null && dir.isDirectory()) {
                    for (File file : dir.listFiles()) {
                        if (file != null && file.isFile()) {
                            String fileExtension = BaseFileUtil.getFileExtension(file);
                            if (fileExtensions.contains(fileExtension)) {
                                file.delete();
                            }
                        }
                    }
                }
            }
        });
    }
}
