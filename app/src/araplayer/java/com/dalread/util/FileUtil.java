package com.dalread.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.dalread.BuildConfig;
import com.dalread.util.arasubtitle.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FileUtil extends BaseFileUtil {

    private static final String TAG = "FileUtil";

    public static String getFileContentsFromFile(File file) {
        String strContents = "";
        try {
            final String encodingStr = FileUtils.guessEncoding(file);
            InputStream inputStream = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(inputStream, encodingStr);
            BufferedReader reader = new BufferedReader(isr);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
            DLog.d(TAG, "File content in UTF-8: " + stringBuilder.toString());
            strContents = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strContents;
    }
    public static SupportVideoFormat getVideoExtension(String fileName) {
        String ext = getFileExtension(fileName);
//        DLog.d(TAG, "getVideoExtension - ext=" + ext);
        if (Utils.isEmpty(ext)) return SupportVideoFormat.NONE;
        try {
            SupportVideoFormat value = SupportVideoFormat.valueOf(ext.toUpperCase());
            if (!Utils.isEmpty(ext)) {
                return value;
            }
        } catch (IllegalArgumentException e) {
            //Not known enum value
        }
        return SupportVideoFormat.NONE;
    }

    public static boolean isValidVideoExtension(String filename) {
        return SupportVideoFormat.isVideoFormat(FileUtil.getVideoExtension(filename));
//        return getVideoExtension(filename) != SupportVideoFormat.NONE;
    }
    public static boolean isValidMediaExtension(String filename) {
        return getVideoExtension(filename) != SupportVideoFormat.NONE;
    }

    public static boolean isValidMusicExtension(String filename) {
        return SupportVideoFormat.isAudioFormat(FileUtil.getVideoExtension(filename));
//        return getVideoExtension(filename) != SupportVideoFormat.NONE;
    }

    public static SupportSubtitleFormat getSubtitleExtension(String fileName) {
        String ext = getFileExtension(fileName);
//        DLog.d(TAG, "getSubtitleExtension - ext=" + ext);
        if (Utils.isEmpty(ext)) return SupportSubtitleFormat.NONE;
        try {
            SupportSubtitleFormat value = SupportSubtitleFormat.valueOf(ext.toUpperCase());
            if (!Utils.isEmpty(ext)) {
                return value;
            }
        } catch (IllegalArgumentException e) {
            //Not known enum value
        }
        return SupportSubtitleFormat.NONE;
    }

    public static boolean checkSubtitleExtension(String filename) {
        return SupportSubtitleFormat.isSubtitleFormat(FileUtil.getSubtitleExtension(filename));
//        return getSubtitleExtension(filename) != SupportSubtitleFormat.NONE;
    }

    public static boolean checkLyricExtension(String filename) {
        return SupportSubtitleFormat.isLyricFormat(FileUtil.getSubtitleExtension(filename));
    }

    public static boolean isTxtFormat(String filename) {
        return SupportSubtitleFormat.isTxtFormat(FileUtil.getSubtitleExtension(filename));
    }

    public static boolean isAudioFormat(String filename) {
        return SupportVideoFormat.isAudioFormat(FileUtil.getVideoExtension(filename));
    }

    public static boolean isVideoApp() {
        return BuildConfig.MEDIA_TYPE == Constant.AppMediaType.VIDEO;
    }

    public static boolean isMusicApp() {
        return BuildConfig.MEDIA_TYPE == Constant.AppMediaType.MUSIC;
    }

    //This is NOT working at all. It gets only Video files.
    public static List<MediaAraPlayer> getSubtiteListFromMediaStore(Context context) {
        List<MediaAraPlayer> resultList = new ArrayList<MediaAraPlayer>();
        String[] projection = null;
//        String[] projection = new String[] {
//                MediaStore.Files.FileColumns._ID,
//                MediaStore.Files.FileColumns.DISPLAY_NAME,
//                MediaStore.Files.FileColumns.ARTIST,
//                MediaStore.Files.FileColumns.GENRE,
//                MediaStore.Files.FileColumns.SIZE,
//                MediaStore.Files.FileColumns.DATE_MODIFIED,
//                MediaStore.Files.FileColumns.DATE_EXPIRES,
//                MediaStore.Files.FileColumns.MIME_TYPE,
//                MediaStore.Files.FileColumns.DATA
//        };

        List<String> subtitleExtensions = SupportSubtitleFormat.getSubtitles();
        String selection = null;
//        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;

//        StringJoiner sbSelection = new StringJoiner(" OR ");

//        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
//        String[] selectionArgs = new String[]{ mimeType };
//        String[] selectionArgs = subtitleExtensions.stream().map(e->MimeTypeMap.getSingleton().getMimeTypeFromExtension(e)).toArray(String[]::new);

//        String[] selectionArgs = new String[subtitleExtensions.size()];
//        List<String> mimes = new ArrayList<>();
//        int index = 0;
//        for (String ext : subtitleExtensions) {
//            sbSelection.add(MediaStore.Files.FileColumns.MIME_TYPE + "=?");
//            selectionArgs[index++] = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
//            mimes.add(MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext));
//        }

        String[] selectionArgs = null;
        String sortOrder  = null; // unordered // MediaStore.Video.Media.DISPLAY_NAME + " ASC";

        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(getUri(), projection, selection, selectionArgs, sortOrder);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);
            int dataModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED);
            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int size = cursor.getInt(sizeColumn);
                long dataModified = cursor.getLong(dataModifiedColumn);
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                DLog.d("getSubtiteListFromMediaStore", "path = " + path);
                if (subtitleExtensions.contains(FileUtil.getFileExtension(name))) {
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                    resultList.add(new MediaAraPlayer(contentUri, name, path, dataModified, 0, size));
                }
            }
            cursor.moveToNext();
        }
        return resultList;
    }

    private static Uri getUri() {
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }
        return collection;
    }

    public static List<MediaAraPlayer> getMediaListFromMediaStore(Context context) {
        List<MediaAraPlayer> resultList = new ArrayList<MediaAraPlayer>();

        String[] projection = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.SIZE
        };

        String selection = MediaStore.Video.Media.DURATION +
                " >= ?";
        int minSecondsToGetMedia = 30;
        String[] selectionArgs = new String[] {
                String.valueOf(TimeUnit.MILLISECONDS.convert(minSecondsToGetMedia, TimeUnit.SECONDS))
        };


        String sortOrder  = null; // unordered // MediaStore.Video.Media.DISPLAY_NAME + " ASC";

        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(getUri(), projection, selection, selectionArgs, sortOrder);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
            int dataModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED);
            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);
                long dataModified = cursor.getLong(dataModifiedColumn) * 1000; //DATE_MODIFIED is in seconds since 1970, so just multiply it by 1000
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                if (size == 0) {
                    size = 0;
                }
                // Stores column values and the contentUri in a local object
                // that represents the media file.
                resultList.add(new MediaAraPlayer(contentUri, name, path, dataModified, duration, size));
            }
            cursor.moveToNext();
        }
        return resultList;
    }

    public static String uri2path(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
//        Uri uri = Uri.fromFile(new File(path));
        cursor.close();
        return path;
    }

    public static Uri path2uri(Context context, String filePath) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, "_data = '" + filePath + "'", null, null);
        cursor.moveToNext();
        int id = cursor.getInt(cursor.getColumnIndex("_id"));
        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        return uri;
    }

//    public static boolean copyFile(String inputPath, String outputPath) {
//        File sourceFile = new File(inputPath);
//        File destFile = new File(outputPath);
//
//        try {
//            FileUtils.copyFile(sourceFile, destFile);
//        } catch (IOException e) {
//            return false;
//        }
//        return true;
//    }
}

class MediaAraPlayer {
    private final Uri uri;
    private final String name;
    private final String path;
    private final long dataModified;
    private final int duration;
    private final int size;

    public MediaAraPlayer(Uri uri, String name, String path, long dataModified, int duration, int size) {
        this.uri = uri;
        this.name = name;
        this.path = path;
        this.dataModified = dataModified;
        this.duration = duration;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public long getDataModified() {
        return dataModified;
    }

    public int getDuration() {
        return duration;
    }

    public int getSize() {
        return size;
    }
}
