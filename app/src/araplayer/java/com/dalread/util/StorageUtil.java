package com.dalread.util;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.Lyric;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.PlaylistModel;
import com.dalread.model.SeasonEpisodeInfo;
import com.dalread.model.ServerModel;
import com.dalread.model.VideoInformationModel;
import com.dalread.util.arasubtitle.Constants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class StorageUtil extends BaseStorageUtil {
    public static String getCurrentPath(PlayerFileModel playerFileModel) {
        return getCurrentPath(playerFileModel.getName(), playerFileModel.getPath());
    }

    public static boolean checkVideoAndSubName(String video, String sub) {
        SupportVideoFormat subVideo = FileUtil.getVideoExtension(video);
        if (subVideo == SupportVideoFormat.NONE) return false;
        SupportSubtitleFormat subType = FileUtil.getSubtitleExtension(sub);
        if (subType == SupportSubtitleFormat.NONE) return false;
        final String text1 = video.substring(0, video.toLowerCase().lastIndexOf(subVideo.getDotFileSuffix()));
        final String text2 = sub.substring(0, sub.toLowerCase().lastIndexOf(subType.getDotFileSuffix()));
        return text1.equals(text2);
    }

    private static void sortByNameAsc(List<PlayerFileModel> files) {
        Collections.sort(files, (s1, s2) -> SortStringUtil.sort(s1.getName(), s2.getName()));
    }

    private static void sortByNameDesc(List<PlayerFileModel> files){
        Collections.sort(files, (s1, s2) -> SortStringUtil.sort(s2.getName(), s1.getName()));
    }

    private static void sortByDateAsc(List<PlayerFileModel> files){
//        Collections.sort(files, (o1, o2) -> (int) (o1.getCreatedDate() - o2.getCreatedDate()));
        Collections.sort(files, (o1, o2) ->  Long.compare(o1.getCreatedDate(), o2.getCreatedDate()));
    }

    private static void sortByDateDesc(List<PlayerFileModel> files){
//        Collections.sort(files, (o1, o2) -> (int) (o2.getCreatedDate() - o1.getCreatedDate()));
        Collections.sort(files, (o1, o2) -> Long.compare(o2.getCreatedDate(), o1.getCreatedDate()));
    }

    private static void sortBySizeAsc(List<PlayerFileModel> files){
        Collections.sort(files, (o1, o2) -> Long.compare(o1.getSize(), o2.getSize()));
    }

    private static void sortBySizeDesc(List<PlayerFileModel> files){
        Collections.sort(files, (o1, o2) -> Long.compare(o2.getSize(), o1.getSize()));
    }

    private static void sortDifficultyAsc(List<PlayerFileModel> files){
        Collections.sort(files, (o1, o2) -> Long.compare(o1.getPercentVocaKnow(), o2.getPercentVocaKnow()));
    }

    private static void sortDifficultyDesc(List<PlayerFileModel> files){
        Collections.sort(files, (o1, o2) -> Long.compare(o2.getPercentVocaKnow(), o1.getPercentVocaKnow()));
    }

    public static List<PlayerFileModel> sortFiles(List<PlayerFileModel> files) {
        return sortFiles(files, Constant.PLAYER.SORT.FILE_NAME_ASC);
    }

    public static List<PlayerFileModel> sortFiles(List<PlayerFileModel> files, int sort) {
        switch (sort) {
            case Constant.PLAYER.SORT.CREATE_DATE_ASC:
                sortByDateAsc(files);
                break;
            case Constant.PLAYER.SORT.CREATE_DATE_DESC:
                sortByDateDesc(files);
                break;
            case Constant.PLAYER.SORT.FILE_SIZE_ASC:
                sortBySizeAsc(files);
                break;
            case Constant.PLAYER.SORT.FILE_SIZE_DESC:
                sortBySizeDesc(files);
                break;
            case Constant.PLAYER.SORT.FILE_NAME_ASC:
                sortByNameAsc(files);
                break;
            case Constant.PLAYER.SORT.FILE_NAME_DESC:
                sortByNameDesc(files);
                break;
            case Constant.PLAYER.SORT.DIFFICULTY_ASC:
                sortDifficultyAsc(files);
                break;
            case Constant.PLAYER.SORT.DIFFICULTY_DESC:
                sortDifficultyDesc(files);
                break;
        }
        return SortUtil.sortPlayerFileModelFolderTop(files);
    }

    public static void deleteSubtitleFiles(PlayerFileModel playerFileModel) {
        DLog.d(TAG, "deleteSubtitleFiles");
        File directory = new File(getCurrentPath(playerFileModel));
        File[] files = directory.listFiles();
        if (files == null || files.length <= 0) return;
        for (File file : files) {
            if (!file.isDirectory() && FileUtil.checkSubtitleExtension(file.getName())) {
                if (checkVideoAndSubName(playerFileModel.getName(), file.getName())) {
                    removeFile(file.getPath());
                }
            }
        }
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

    public static String generateExtension(String name, String subtitleFile) {
        return name + "." + subtitleFile;
    }

    public static String generateSRTFileName(String name) {
        return generateExtension(name, SupportSubtitleFormat.SRT.getFileSuffix());
    }

    public static String generateLRCFileName(String name) {
        return generateExtension(name, SupportSubtitleFormat.LRC.getFileSuffix());
    }

    public static String generatePNGFileName(String name) {
        return generateExtension(name, SupportImageFormat.PNG.getFileSuffix());
    }

    public static List<PlayerFileModel> getAllFiles(Context context, String path, boolean isAddSubtitle) {
        if (FileUtil.isVideoApp())
            return getAllFiles(context, path, true, true, isAddSubtitle, false, Constant.AppMediaType.VIDEO);// Constant.PLAYER.STORAGE.COUNT_OF_FILES.VIDEO);
        return getAllFiles(context, path, true, true, isAddSubtitle, false, Constant.AppMediaType.MUSIC);// Constant.PLAYER.STORAGE.COUNT_OF_FILES.VIDEO);
    }

    public static List<PlayerFileModel> getAllFiles(Context context, String path, boolean isAddSubtitle, boolean isRecursive) {
        if (FileUtil.isVideoApp())
            return getAllFiles(context, path, true, true, isAddSubtitle, isRecursive, Constant.AppMediaType.VIDEO);// Constant.PLAYER.STORAGE.COUNT_OF_FILES.VIDEO);
        return getAllFiles(context, path, true, true, isAddSubtitle, isRecursive, Constant.AppMediaType.MUSIC);// Constant.PLAYER.STORAGE.COUNT_OF_FILES.VIDEO);
    }

    public static List<PlayerFileModel> getAllSubtitlesOrLyrics(Context context, String path, boolean isRecursive) {
        if (FileUtil.isVideoApp())
            return getAllFiles(context, path, false, false, true, isRecursive, Constant.AppMediaType.SUBTITLE);// Constant.PLAYER.STORAGE.COUNT_OF_FILES.SUBTITLE);
        return getAllFiles(context, path, false, false, true, isRecursive, Constant.AppMediaType.LYRIC);// Constant.PLAYER.STORAGE.COUNT_OF_FILES.SUBTITLE);
    }

    public static List<PlayerFileModel> getAllSubtitlesOrLyrics(Context context, String path, boolean isAddFolder, boolean isRecursive) {
        if (FileUtil.isVideoApp())
            return getAllFiles(context, path, isAddFolder, false, true, isRecursive, Constant.AppMediaType.SUBTITLE);// Constant.PLAYER.STORAGE.COUNT_OF_FILES.SUBTITLE);
        return getAllFiles(context, path, isAddFolder, false, true, isRecursive, Constant.AppMediaType.LYRIC);// Constant.PLAYER.STORAGE.COUNT_OF_FILES.SUBTITLE);
    }

    public static List<PlayerFileModel> getAllSubtitlesOrLyrics(Context context, String path, boolean isAddFolder, boolean isAddSubtitle, boolean isRecursive) {
        if (FileUtil.isVideoApp())
            return getAllFiles(context, path, isAddFolder, false, isAddSubtitle, isRecursive, Constant.AppMediaType.SUBTITLE);// Constant.PLAYER.STORAGE.COUNT_OF_FILES.SUBTITLE);
        return getAllFiles(context, path, isAddFolder, false, isAddSubtitle, isRecursive, Constant.AppMediaType.LYRIC);// Constant.PLAYER.STORAGE.COUNT_OF_FILES.SUBTITLE);
    }

    public static List<PlayerFileModel> getAllFiles(Context context, String path,
                                                    boolean isAddFolder, boolean isAddMedia, boolean isAddSubtitle,
                                                    boolean isRecursive) {
        return getAllFiles(context, path, isAddFolder, isAddMedia, isAddSubtitle, isRecursive, Constant.AppMediaType.VIDEO);// Constant.PLAYER.STORAGE.COUNT_OF_FILES.VIDEO);
    }


    public static List<PlayerFileModel> getAllFiles(Context context, String path,
                                                    boolean isAddFolder, boolean isAddMedia, boolean isAddSubtitleLyric,
                                                    boolean isRecursive, int appMediaType) {
//        if ((FileUtil.isVideoApp()) && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
//
//            //For testing.
////            long start = System.currentTimeMillis();
////            getAllFilesVERSION_CODES_Q(context, path, isAddFolder, isAddMedia, isAddSubtitleLyric, isRecursive, mediaType);
////            long stop = System.currentTimeMillis();
////            DLog.d("getAllFiles getAllFilesVERSION_CODES_Q", " Elapsed : " + (stop - start) + " ms" );
////
////            start = System.currentTimeMillis();
////            getAllFilesVERSION_CODES_Q_Path(context, path, isAddFolder, isAddMedia, isAddSubtitleLyric, isRecursive, mediaType);
////            stop = System.currentTimeMillis();
////            DLog.d("getAllFiles getAllFilesVERSION_CODES_Q_Path", " Elapsed : " + (stop - start) + " ms" );
//
//            return getAllFilesVERSION_CODES_Q(context, path, isAddFolder, isAddMedia, isAddSubtitleLyric, isRecursive, appMediaType);
//        } else {
            return getAllFilesVERSION_CODES_OLD_VERSION(context, path, isAddFolder, isAddMedia, isAddSubtitleLyric, isRecursive, appMediaType);
//        }
    }

    //Can't get video files that are downloaded from network
//    public static List<PlayerFileModel> getAllFilesVERSION_CODES_Q(Context context, String path,
//                                                    boolean isAddFolder, boolean isAddMedia, boolean isAddSubtitleLyric,
//                                                    boolean isRecursive, int appMediaType) {
//
//        final List<PlayerFileModel> list = new ArrayList<>();
//        if (path != null) {
//            final File currentFile = new File(path);
//            // add folder
//            if (isAddFolder) {
//                File[] folders = currentFile.listFiles();
//                for (File f : folders) {
//                    final int count = getNumberOfFiles(f, appMediaType);
//                    if (f.isDirectory() && !f.isHidden() && count > 0) {
//                        PlayerFileModel folder = new PlayerFileModel(f.getName(), f.length(), f.lastModified(), f.getPath(), PlayerFileModel.DirectoryType.WHITE);
//                        folder.setCount(count);
//                        list.add(folder);
//                    }
//                }
//            }
//        }
//
////        String cameraDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
//        SharedPreferencesDB sharedPreferencesDB = SharedPreferencesDB.getInstance(context);
////        boolean isShowVideoInDCIM = sharedPreferencesDB.getShowVideoInDCIM();
////        String araoneTempDirectory = Environment.getExternalStorageDirectory().getPath() + "/" + Constant.FOLDER_ARAONE.TEMP_PATH;
//
//        final List<PlayerFileModel> listMedia = new ArrayList<>();
//        final Map<String, PlayerFileModel> mapMedia = new HashMap<>();
//        if (isAddMedia) {
//            List<MediaAraPlayer> mediaList = FileUtil.getMediaListFromMediaStore(context);
//            if (!Utils.isEmptyCollection(mediaList)) {
//                AudioFile audioFile = null; //move here due to java.lang.StackOverflowError: stack size 1043KB
//                for (MediaAraPlayer media : mediaList) {
////                    if (isTooSmallSizeMediaFile(file))
////                        continue;
////
//                    if (!isAddFolder) {
//                        if (doNotAddThisFile(araoneTempDirectory, media))
//                            continue;
//                    }
//                    if (FileUtil.isValidVideoExtension(media.getName())) {
////                        if (FileUtil.isValidMediaExtension(file.getName())) {
//                        SeasonEpisodeInfo seasonEpisodeInfo = new SeasonEpisodeInfo(media.getPath());
//                        PlayerFileModel t = new PlayerFileModel(media.getName(), media.getSize(), media.getDuration(), media.getDataModified(), media.getPath(), PlayerFileModel.DirectoryType.NONE, seasonEpisodeInfo, Constant.AppMediaType.VIDEO);
////                        t.setDuration(StorageUtil.getDuration(mmr, file.getPath())); //Dalnim : Don't get duration for all videos, it takes too long time.
//                        listMedia.add(t);
//
//                        //For Korean subtitle file name.(I met videon file name and subtitle file name were same, but in the code they were different)
//                        // "/storage/emulated/0/Download/나 남편을 쉐어했다 01화 " it with this name. There is a space at the end of file name. If there is no space, don't need to use NFD.
//                        String filenameNFD = Normalizer.normalize(FilenameUtils.removeExtension(media.getPath()), Normalizer.Form.NFD);
//                        mapMedia.put(filenameNFD, t);
////                        }
////                    } else if (mediaType == Constant.MediaType.MUSIC || mediaType == Constant.MediaType.LYRIC) {
//                    } else if (FileUtil.isValidMusicExtension(media.getName())) {
////                        if (FileUtil.isValidMediaExtension(file.getName())) {
//                        try {
//                            audioFile = AudioFileIO.read(new File(media.getPath()));
//                            PlayerFileModel t = new PlayerFileModel(media.getName(), media.getSize(), media.getDuration(), media.getDataModified(), media.getPath(), PlayerFileModel.DirectoryType.NONE, Constant.AppMediaType.MUSIC, audioFile.getTag());
//                            listMedia.add(t);
//                            String filenameNFD = Normalizer.normalize(FilenameUtils.removeExtension(media.getPath()), Normalizer.Form.NFD);
//                            mapMedia.put(filenameNFD, t);
//                        } catch (CannotReadException e) {
//                            e.printStackTrace();
//                        } catch (NoSuchMethodError e) {
//                            e.printStackTrace();
////                                PlayerFileModel t = new PlayerFileModel(file.getName(), file.length(), file.lastModified(), file.getPath(), PlayerFileModel.DirectoryType.NONE, Constant.MediaType.MUSIC, null);
////                                listMedia.add(t);
////                                String filenameNFD = Normalizer.normalize(FilenameUtils.removeExtension(file.getPath()), Normalizer.Form.NFD);
////                                mapMedia.put(filenameNFD, t);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } catch (TagException e) {
//                            e.printStackTrace();
//                        } catch (ReadOnlyFileException e) {
//                            e.printStackTrace();
//                        } catch (InvalidAudioFrameException e) {
//                            e.printStackTrace();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
////                        }
//                    }
//
//                }
////                mmr.release();
//            }
//        }
//
//        // get all subtitle files
//        final List<PlayerFileModel> listSubtitle = new ArrayList<>();
//        if (isAddSubtitleLyric || isAddMedia) {
//            final Map<String, PlayerFileModel> mapSubtitle = new HashMap<>();
//            List<MediaAraPlayer> mediaListFromMediaStore = FileUtil.getSubtiteListFromMediaStore(context);
//
//            List<String> subtitleExtensions = new ArrayList<>();
//            if (appMediaType == Constant.AppMediaType.VIDEO || appMediaType == Constant.AppMediaType.SUBTITLE)
//                subtitleExtensions.addAll(SupportSubtitleFormat.getSubtitles());
//            else if (appMediaType == Constant.AppMediaType.MUSIC || appMediaType == Constant.AppMediaType.LYRIC)
//                subtitleExtensions.addAll(SupportSubtitleFormat.getLyrics());
//
//            IOFileFilter subtitleFilter = new SuffixFileFilter(subtitleExtensions, IOCase.INSENSITIVE);
//            Collection<File> mediaList = new ArrayList<>();
//            if (path == null) { //get files only 4 main folders
//                mediaList = getMediaFilesAndSubtitleFiles(context, appMediaType, isRecursive);
//            } else {
//                final File currentFile = new File(path);
//                mediaList = FileUtils.listFiles(currentFile, subtitleFilter, isRecursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE);
//            }
//
//
//            if (!Utils.isEmptyCollection(mediaList)) { //subtitleFiles != null && !subtitleFiles.isEmpty()) {
//                for (File file : mediaList) {
//                    if (file.length() <= 0)
//                        continue;;
//
////                    if (!isAddFolder) {
////                        if (doNotAddThisFile(cameraDirectory, isShowVideoInDCIM, araoneTempDirectory, file))
////                            continue;
////                    }
//                    if (appMediaType == Constant.AppMediaType.VIDEO || appMediaType == Constant.AppMediaType.SUBTITLE) {
//                        if (FileUtil.checkSubtitleExtension(file.getName())) {
//                            PlayerFileModel t = new PlayerFileModel(file.getName(), file.length(), 0, file.getPath(), PlayerFileModel.DirectoryType.NONE);
////                        String key = FilenameUtils.removeExtension(file.getPath());
//                            listSubtitle.add(t);
//                            String filenameNFD = Normalizer.normalize(FilenameUtils.removeExtension(file.getPath()), Normalizer.Form.NFD);
//                            mapSubtitle.put(filenameNFD, t);
//                        }
//                    } else if (appMediaType == Constant.AppMediaType.MUSIC || appMediaType == Constant.AppMediaType.LYRIC) {
//                        if (FileUtil.checkLyricExtension(file.getName())) {
//                            PlayerFileModel t = new PlayerFileModel(file.getName(), file.length(), 0, file.getPath(), PlayerFileModel.DirectoryType.NONE);
////                        String key = FilenameUtils.removeExtension(file.getPath());
//                            listSubtitle.add(t);
//                            String filenameNFD = Normalizer.normalize(FilenameUtils.removeExtension(file.getPath()), Normalizer.Form.NFD);
//                            mapSubtitle.put(filenameNFD, t);
//                        }
//                    }
//                }
//            }
//
//            //If AraPlayer refresh (Get All video files from root folder) video files, then set the default subtitle file to the video file(If they have same file name)
//            for (String key : mapMedia.keySet()) {
//                if (mapSubtitle.containsKey(key)) {
//                    PlayerFileModel model = mapMedia.get(key);
//                    PlayerFileModel subtitle = mapSubtitle.get(key);
//                    model.setSubPath1(subtitle.getPath());
//                }
//            }
//        }
//
//        if (isAddMedia) {
//            list.addAll(listMedia);
//        }
//        if (isAddSubtitleLyric) {
//            list.addAll(listSubtitle);
//        }
//
//        return list;
//    }


    //This is for testing. Using nio.Path instead of io.File, but this is slower.
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public static List<PlayerFileModel> getAllFilesVERSION_CODES_Q_Path(Context context, String path,
//                                                                        boolean isAddFolder, boolean isAddMedia, boolean isAddSubtitleLyric,
//                                                                        boolean isRecursive, int mediaType) {
//
//        final List<PlayerFileModel> list = new ArrayList<>();
//        if (path != null) {
//            final File currentFile = new File(path);
//            // add folder
//            if (isAddFolder) {
//                File[] folders = currentFile.listFiles();
//                for (File f : folders) {
//                    final int count = getNumberOfFiles(f, mediaType);
//                    if (f.isDirectory() && !f.isHidden() && count > 0) {
//                        PlayerFileModel folder = new PlayerFileModel(f.getName(), f.length(), f.lastModified(), f.getPath(), PlayerFileModel.DirectoryType.WHITE);
//                        folder.setCount(count);
//                        list.add(folder);
//                    }
//                }
//            }
//        }
//
//        String cameraDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
//        SharedPreferencesDB sharedPreferencesDB = SharedPreferencesDB.getInstance(context);
////        boolean isShowVideoInDCIM = sharedPreferencesDB.getShowVideoInDCIM();
//        String araoneTempDirectory = Environment.getExternalStorageDirectory().getPath() + "/" + Constant.FOLDER_ARAONE.TEMP_PATH;
//
//        final List<PlayerFileModel> listMedia = new ArrayList<>();
//        final Map<String, PlayerFileModel> mapMedia = new HashMap<>();
//        if (isAddMedia) {
//            List<MediaAraPlayer> mediaList = FileUtil.getMediaListFromMediaStore(context);
//            if (!Utils.isEmptyCollection(mediaList)) {
//                AudioFile audioFile = null; //move here due to java.lang.StackOverflowError: stack size 1043KB
//                for (MediaAraPlayer media : mediaList) {
////                    if (isTooSmallSizeMediaFile(file))
////                        continue;
////
//                    if (!isAddFolder) {
//                        if (doNotAddThisFile(cameraDirectory, isShowVideoInDCIM, araoneTempDirectory, media))
//                            continue;
//                    }
//                    if (FileUtil.isValidVideoExtension(media.getName())) {
////                        if (FileUtil.isValidMediaExtension(file.getName())) {
//                        SeasonEpisodeInfo seasonEpisodeInfo = new SeasonEpisodeInfo(media.getPath());
//                        PlayerFileModel t = new PlayerFileModel(media.getName(), media.getSize(), media.getDuration(), media.getDataModified(), media.getPath(), PlayerFileModel.DirectoryType.NONE, seasonEpisodeInfo, Constant.AppMediaType.VIDEO);
////                        t.setDuration(StorageUtil.getDuration(mmr, file.getPath())); //Dalnim : Don't get duration for all videos, it takes too long time.
//                        listMedia.add(t);
//
//                        //For Korean subtitle file name.(I met videon file name and subtitle file name were same, but in the code they were different)
//                        // "/storage/emulated/0/Download/나 남편을 쉐어했다 01화 " it with this name. There is a space at the end of file name. If there is no space, don't need to use NFD.
//                        String filenameNFD = Normalizer.normalize(FilenameUtils.removeExtension(media.getPath()), Normalizer.Form.NFD);
//                        mapMedia.put(filenameNFD, t);
////                        }
////                    } else if (mediaType == Constant.MediaType.MUSIC || mediaType == Constant.MediaType.LYRIC) {
//                    } else if (FileUtil.isValidMusicExtension(media.getName())) {
////                        if (FileUtil.isValidMediaExtension(file.getName())) {
//                        try {
//                            audioFile = AudioFileIO.read(new File(media.getPath()));
//                            PlayerFileModel t = new PlayerFileModel(media.getName(), media.getSize(), media.getDuration(), media.getDataModified(), media.getPath(), PlayerFileModel.DirectoryType.NONE, Constant.AppMediaType.MUSIC, audioFile.getTag());
//                            listMedia.add(t);
//                            String filenameNFD = Normalizer.normalize(FilenameUtils.removeExtension(media.getPath()), Normalizer.Form.NFD);
//                            mapMedia.put(filenameNFD, t);
//                        } catch (CannotReadException e) {
//                            e.printStackTrace();
//                        } catch (NoSuchMethodError e) {
//                            e.printStackTrace();
////                                PlayerFileModel t = new PlayerFileModel(file.getName(), file.length(), file.lastModified(), file.getPath(), PlayerFileModel.DirectoryType.NONE, Constant.MediaType.MUSIC, null);
////                                listMedia.add(t);
////                                String filenameNFD = Normalizer.normalize(FilenameUtils.removeExtension(file.getPath()), Normalizer.Form.NFD);
////                                mapMedia.put(filenameNFD, t);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } catch (TagException e) {
//                            e.printStackTrace();
//                        } catch (ReadOnlyFileException e) {
//                            e.printStackTrace();
//                        } catch (InvalidAudioFrameException e) {
//                            e.printStackTrace();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
////                        }
//                    }
//
//                }
////                mmr.release();
//            }
//        }
//
//        // get all subtitle files
//        final List<PlayerFileModel> listSubtitle = new ArrayList<>();
//        if (isAddSubtitleLyric || isAddMedia) {
//            final Map<String, PlayerFileModel> mapSubtitle = new HashMap<>();
//            List<MediaAraPlayer> mediaListFromMediaStore = FileUtil.getSubtiteListFromMediaStore(context);
//
//            List<String> subtitleExtensions = new ArrayList<>();
//            if (mediaType == Constant.AppMediaType.VIDEO || mediaType == Constant.AppMediaType.SUBTITLE)
//                subtitleExtensions.addAll(SupportSubtitleFormat.getSubtitles());
//            else if (mediaType == Constant.AppMediaType.MUSIC || mediaType == Constant.AppMediaType.LYRIC)
//                subtitleExtensions.addAll(SupportSubtitleFormat.getLyrics());
//
//            IOFileFilter subtitleFilter = new SuffixFileFilter(subtitleExtensions, IOCase.INSENSITIVE);
////            Collection<File> mediaList = new ArrayList<>();
////            if (path == null) { //get files only 4 main folders
////                mediaList = getMediaFilesAndSubtitleFiles(context, mediaType, isRecursive);
////            } else {
////                final File currentFile = new File(path);
////                mediaList = FileUtils.listFiles(currentFile, subtitleFilter, isRecursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE);
////            }
//
//            List<Path> mediaList = new ArrayList<>();
//            if (path == null) { //get files only 4 main folders
//                mediaList = getMediaFilesAndSubtitleFilesByPath(context, mediaType, isRecursive);
////            } else {
////                final File currentFile = new File(path);
////                mediaList = FileUtils.listFiles(currentFile, subtitleFilter, isRecursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE);
//            }
//
//            try {
//                if (!Utils.isEmptyCollection(mediaList)) { //subtitleFiles != null && !subtitleFiles.isEmpty()) {
//                    for (Path file : mediaList) {
//                        long size = Files.size(file);
//                        if (size <= 0)
//                            continue;
//
//                        String fileName = file.getFileName().toString();
//                        String filePath = file.getParent().toString() + File.separator + fileName;
//                        if (mediaType == Constant.AppMediaType.VIDEO || mediaType == Constant.AppMediaType.SUBTITLE) {
//                            if (FileUtil.checkSubtitleExtension(fileName)) {
//                                PlayerFileModel t = new PlayerFileModel(fileName, size, 0, filePath, PlayerFileModel.DirectoryType.NONE);
////                        String key = FilenameUtils.removeExtension(file.getPath());
//                                listSubtitle.add(t);
//                                String filenameNFD = Normalizer.normalize(FilenameUtils.removeExtension(filePath), Normalizer.Form.NFD);
//                                mapSubtitle.put(filenameNFD, t);
//                            }
//                        } else if (mediaType == Constant.AppMediaType.MUSIC || mediaType == Constant.AppMediaType.LYRIC) {
//                            if (FileUtil.checkLyricExtension(fileName)) {
//                                PlayerFileModel t = new PlayerFileModel(fileName, size, 0, filePath, PlayerFileModel.DirectoryType.NONE);
////                        String key = FilenameUtils.removeExtension(file.getPath());
//                                listSubtitle.add(t);
//                                String filenameNFD = Normalizer.normalize(FilenameUtils.removeExtension(filePath), Normalizer.Form.NFD);
//                                mapSubtitle.put(filenameNFD, t);
//                            }
//                        }
//
//
////                    if (file.length() <= 0)
////                        continue;;
//
//////                    if (!isAddFolder) {
//////                        if (doNotAddThisFile(cameraDirectory, isShowVideoInDCIM, araoneTempDirectory, file))
//////                            continue;
//////                    }
////                    if (mediaType == Constant.MediaType.VIDEO || mediaType == Constant.MediaType.SUBTITLE) {
////                        if (FileUtil.checkSubtitleExtension(file.getName())) {
////                            PlayerFileModel t = new PlayerFileModel(file.getName(), file.length(), 0, file.getPath(), PlayerFileModel.DirectoryType.NONE);
//////                        String key = FilenameUtils.removeExtension(file.getPath());
////                            listSubtitle.add(t);
////                            String filenameNFD = Normalizer.normalize(FilenameUtils.removeExtension(file.getPath()), Normalizer.Form.NFD);
////                            mapSubtitle.put(filenameNFD, t);
////                        }
////                    } else if (mediaType == Constant.MediaType.MUSIC || mediaType == Constant.MediaType.LYRIC) {
////                        if (FileUtil.checkLyricExtension(file.getName())) {
////                            PlayerFileModel t = new PlayerFileModel(file.getName(), file.length(), 0, file.getPath(), PlayerFileModel.DirectoryType.NONE);
//////                        String key = FilenameUtils.removeExtension(file.getPath());
////                            listSubtitle.add(t);
////                            String filenameNFD = Normalizer.normalize(FilenameUtils.removeExtension(file.getPath()), Normalizer.Form.NFD);
////                            mapSubtitle.put(filenameNFD, t);
////                        }
////                    }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            //If AraPlayer refresh (Get All video files from root folder) video files, then set the default subtitle file to the video file(If they have same file name)
//            for (String key : mapMedia.keySet()) {
//                if (mapSubtitle.containsKey(key)) {
//                    PlayerFileModel model = mapMedia.get(key);
//                    PlayerFileModel subtitle = mapSubtitle.get(key);
//                    model.setSubPath1(subtitle.getPath());
//                }
//            }
//        }
//
//        if (isAddMedia) {
//            list.addAll(listMedia);
//        }
//        if (isAddSubtitleLyric) {
//            list.addAll(listSubtitle);
//        }
//
//        return list;
//    }

//    //This is for testing.
//    private static void aaa(Context context) {
//        ContentResolver cr = context.getContentResolver();
//        Uri uri = MediaStore.Files.getContentUri("external");
//        String[] projection = {MediaStore.Files.FileColumns.TITLE,
//                MediaStore.Files.FileColumns._ID,
//                MediaStore.Files.FileColumns._ID,
//                MediaStore.Files.FileColumns.PARENT,
//                MediaStore.Files.FileColumns.DATA
//        };// Can include more data for more details and check it.
//
//// exclude media files, they would be here also.
//        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
//                + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;
//        String[] selectionArgs = null; // there is no ? in selection so null here
//
//        String sortOrder = null; // unordered
//        Cursor cursorAllNonMediaFiles = cr.query(uri, projection, selection, selectionArgs, sortOrder);
//        cursorAllNonMediaFiles.moveToFirst();
//        while (!cursorAllNonMediaFiles.isAfterLast()){
//            String path = cursorAllNonMediaFiles.getString(cursorAllNonMediaFiles.getColumnIndex(MediaStore.Images.Media.DATA));
//            int lastPoint = path.lastIndexOf(".");
//            path = path.substring(0, lastPoint) + path.substring(lastPoint).toLowerCase();
//            cursorAllNonMediaFiles.moveToNext();
//        }
//
//        // only pdf
//        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
//        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("srt");
//        String[] selectionArgsPdf = new String[]{ mimeType };
//        Cursor cursorSrt = cr.query(uri, projection, selectionMimeType, selectionArgsPdf, sortOrder);
//        cursorSrt.moveToFirst();
//        while (!cursorSrt.isAfterLast()){
//            String path = cursorSrt.getString(cursorSrt.getColumnIndex(MediaStore.Images.Media.DATA));
//            cursorSrt.moveToNext();
//        }
//
//        List<File> files = new ArrayList<>();
//        try {
//
//            final String[] columns = { MediaStore.Images.Media.DATA,
//                    MediaStore.Images.Media.DATE_ADDED,
//                    MediaStore.Images.Media.BUCKET_ID,
//                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
//
//            MergeCursor cursor = new MergeCursor(new Cursor[]{context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null),
//                    context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null, null, null),
//                    context.getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, columns, null, null, null),
//                    context.getContentResolver().query(MediaStore.Video.Media.INTERNAL_CONTENT_URI, columns, null, null, null)
//            });
//            cursor.moveToFirst();
//            files.clear();
//            while (!cursor.isAfterLast()){
//                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                int lastPoint = path.lastIndexOf(".");
//                path = path.substring(0, lastPoint) + path.substring(lastPoint).toLowerCase();
//                files.add(new File(path));
//                cursor.moveToNext();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        int i = 0;
//
//
//    }

    private static Collection<File> getMediaFilesAndSubtitleFiles(Context context, int mediaType, boolean isRecursive) {
        List<String> videoExtensions = new ArrayList<>();
        if (mediaType == Constant.AppMediaType.VIDEO)
            videoExtensions.addAll(SupportVideoFormat.getVideos());
        else if (mediaType == Constant.AppMediaType.MUSIC)
            videoExtensions.addAll(SupportVideoFormat.getMusics());

        List<String> subtitleExtensions = new ArrayList<>();
        if (mediaType == Constant.AppMediaType.VIDEO || mediaType == Constant.AppMediaType.SUBTITLE)
            subtitleExtensions.addAll(SupportSubtitleFormat.getSubtitles());
        else if (mediaType == Constant.AppMediaType.MUSIC || mediaType == Constant.AppMediaType.LYRIC)
            subtitleExtensions.addAll(SupportSubtitleFormat.getLyrics());

        List<String> allExtensions = new ArrayList<>();
        allExtensions.addAll(videoExtensions);
        allExtensions.addAll(subtitleExtensions);

        IOFileFilter filter = new SuffixFileFilter(allExtensions, IOCase.INSENSITIVE);

        Collection<File> mediaFiles = new LinkedList();
        List<String> pathList = MediaFolderUtil.getMediaFolderPathForAraMedia(context);
        for(String pathInList : pathList) {
            File aFile = new File(pathInList);
            if (aFile.exists() && aFile.isDirectory()) {
                String[] types = allExtensions.toArray(new String[allExtensions.size()]);
                long start = System.currentTimeMillis();
//                Collection<File> files11 = FileUtils.listFiles(aFile, types , isRecursive); //Don't use this, it's alwasy case sensitive
                Collection<File> files2 = FileUtils.listFiles(aFile, (IOFileFilter)filter, isRecursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE);
                long stop = System.currentTimeMillis();
                DLog.d("getAllFiles in " + pathInList, "Elapsed: " + (stop - start) + " ms" );
                mediaFiles.addAll(files2);
            } else {
                DLog.d("getAllFiles in " + pathInList, " is not a directory");
                if (UserUtil.isDebugOrAdminUser(context)) {
                    ToastUtil.getInstance(context).show(pathInList + " is not a directory");
                }
            }
        }
        return mediaFiles;
    }

    //This is for testing. Using nio.Path instead of io.File, but this is slower.
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private static List<Path> getMediaFilesAndSubtitleFilesByPath(Context context, int mediaType, boolean isRecursive) {
//
//        List<String> pathList = MediaFolderUtil.getMediaFolderPathForAraMedia(context);
//
//        long start = System.currentTimeMillis();
//        List<Path> mediaFiles = pathList.stream().parallel()
//                .flatMap(path -> {
//                    try { return Files.find(Paths.get(path), Integer.MAX_VALUE,
//                            (p, attrs) -> attrs.isRegularFile()); }
//                    catch (IOException ex) { throw new UncheckedIOException(ex); }
//                })
//                .collect(Collectors.toList());
////        mediaFiles.addAll();
//        long stop = System.currentTimeMillis();
//        DLog.d("getAllFiles", " Elapsed by Path: " + (stop - start) + " ms" );
////        for(String pathInList : pathList) {
////            long start = System.currentTimeMillis();
//////            try (Stream<Path> paths = Files.find(Paths.get(pathInList),
//////                    Integer.MAX_VALUE,
//////                    (filePath, fileAttr) -> fileAttr.isRegularFile())) {
//////                mediaFiles.addAll(paths.collect(Collectors.toList()));
//////            } catch (Exception e) {
//////                e.printStackTrace();
//////            }
////            try {
////                mediaFiles.addAll(Files.walk(Paths.get(pathInList)).parallel().collect(Collectors.toList()));
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////
//////            try (Stream<Path> paths = java.nio.file.Files.walk(Paths.get(pathInList))) {
//////                mediaFiles.addAll(paths.collect(Collectors.toList()));
////////
////////                paths.filter(java.nio.file.Files::isRegularFile)
////////                        .forEach(System.out::println);
//////            } catch (Exception e) {
//////                e.printStackTrace();
//////            }
////            long stop = System.currentTimeMillis();
////            DLog.d("getAllFiles", pathInList + " Elapsed by Path: " + (stop - start) + " ms" );
////        }
//        return mediaFiles;
//    }

    public static List<PlayerFileModel> getAllFilesVERSION_CODES_OLD_VERSION(Context context, String path,
                                                    boolean isAddFolder, boolean isAddMedia, boolean isAddSubtitleLyric,
                                                    boolean isRecursive, int appMediaType) {

        long totalStartTime = System.currentTimeMillis();
        long stepStartTime, stepEndTime;
        long folderTime = 0, mediaTime = 0, subtitleTime = 0, mergeTime = 0;
        
        final List<PlayerFileModel> list = new ArrayList<>();
        
        // 1. 폴더 정보 추가
        if (path != null) {
            stepStartTime = System.currentTimeMillis();
            
            final File currentFile = new File(path);
            // add folder
            if (isAddFolder) {
                File[] folders = currentFile.listFiles();
                for (File f : folders) {
                    final int count = getNumberOfFiles(f, appMediaType);
                    if (f.isDirectory() && !f.isHidden() && count > 0) {
                        PlayerFileModel folder = new PlayerFileModel(f.getName(), f.length(), f.lastModified(), f.getPath(), PlayerFileModel.DirectoryType.WHITE);
                        folder.setCount(count);
                        list.add(folder);
                    }
                }
            }
            
            stepEndTime = System.currentTimeMillis();
            folderTime = stepEndTime - stepStartTime;
            Log.d("DalnimTag", "1단계 - 폴더 처리: " + folderTime + "ms");
        }
        
//        String cameraDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
        SharedPreferencesDB sharedPreferencesDB = SharedPreferencesDB.getInstance(context);
//        boolean isShowVideoInDCIM = sharedPreferencesDB.getShowVideoInDCIM();
//        String araoneTempDirectory = Environment.getExternalStorageDirectory().getPath() + "/" + Constant.FOLDER_ARAONE.TEMP_PATH;
        // get all video files
        final List<PlayerFileModel> listMedia = new ArrayList<>();
        final Map<String, PlayerFileModel> mapMedia = new HashMap<>();
        if (isAddMedia) {
            stepStartTime = System.currentTimeMillis();
            
            List<String> videoExtensions = new ArrayList<>();
            if (appMediaType == Constant.AppMediaType.VIDEO)
                videoExtensions.addAll(SupportVideoFormat.getVideos());
            else if (appMediaType == Constant.AppMediaType.MUSIC)
                videoExtensions.addAll(SupportVideoFormat.getMusics());
            IOFileFilter filter = new SuffixFileFilter(videoExtensions, IOCase.INSENSITIVE);
            Collection<File> mediaFiles = new ArrayList<>();
            if (path == null) {
                mediaFiles = getMediaFilesAndSubtitleFiles(context, appMediaType, isRecursive);
            } else {
                final File currentFile = new File(path);
                mediaFiles = FileUtils.listFiles(currentFile, filter, isRecursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE);
            }


            if (!Utils.isEmptyCollection(mediaFiles)) {
//                FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
                AudioFile audioFile = null; //move here due to java.lang.StackOverflowError: stack size 1043KB
                for (File file : mediaFiles) {
                    if (MediaFolderUtil.isTooSmallSizeMediaFile(file))
                        continue;

//                    if (!isAddFolder) {
//                        if (doNotAddThisFile(cameraDirectory, araoneTempDirectory, file))
//                            continue;
//                    }
//                    if (mediaType == Constant.MediaType.VIDEO || mediaType == Constant.MediaType.SUBTITLE) {
                    if (FileUtil.isValidVideoExtension(file.getName())) {
//                        if (FileUtil.isValidMediaExtension(file.getName())) {
                            SeasonEpisodeInfo seasonEpisodeInfo = new SeasonEpisodeInfo(file.getPath());
                            PlayerFileModel t = new PlayerFileModel(file.getName(), file.length(), 0, file.lastModified(), file.getPath(), PlayerFileModel.DirectoryType.NONE, seasonEpisodeInfo, Constant.AppMediaType.VIDEO);
//                        String key = FilenameUtils.removeExtension(file.getPath());
//                        t.setDuration(StorageUtil.getDuration(mmr, file.getPath())); //Dalnim : Don't get duration for all videos, it takes too long time.
                            listMedia.add(t);

                            //For Korean subtitle file name.(I met videon file name and subtitle file name were same, but in the code they were different)
                            // "/storage/emulated/0/Download/나 남편을 쉐어했다 01화 " it with this name. There is a space at the end of file name. If there is no space, don't need to use NFD.
                            String filenameNFD = Normalizer.normalize(FilenameUtils.removeExtension(file.getPath()), Normalizer.Form.NFD);
                            mapMedia.put(filenameNFD, t);
//                        }
//                    } else if (mediaType == Constant.MediaType.MUSIC || mediaType == Constant.MediaType.LYRIC) {
                    } else if (FileUtil.isValidMusicExtension(file.getName())) {
//                        if (FileUtil.isValidMediaExtension(file.getName())) {
                            try {
                                audioFile = AudioFileIO.read(file);
                                PlayerFileModel t = new PlayerFileModel(file.getName(), file.length(), 0, file.lastModified(), file.getPath(), PlayerFileModel.DirectoryType.NONE, Constant.AppMediaType.MUSIC, audioFile.getTag());
                                listMedia.add(t);
                                String filenameNFD = Normalizer.normalize(FilenameUtils.removeExtension(file.getPath()), Normalizer.Form.NFD);
                                mapMedia.put(filenameNFD, t);
                            } catch (CannotReadException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodError e) {
                                e.printStackTrace();
//                                PlayerFileModel t = new PlayerFileModel(file.getName(), file.length(), file.lastModified(), file.getPath(), PlayerFileModel.DirectoryType.NONE, Constant.MediaType.MUSIC, null);
//                                listMedia.add(t);
//                                String filenameNFD = Normalizer.normalize(FilenameUtils.removeExtension(file.getPath()), Normalizer.Form.NFD);
//                                mapMedia.put(filenameNFD, t);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (TagException e) {
                                e.printStackTrace();
                            } catch (ReadOnlyFileException e) {
                                e.printStackTrace();
                            } catch (InvalidAudioFrameException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                        }
                    }

                }
//                mmr.release();
            }
            
            stepEndTime = System.currentTimeMillis();
            mediaTime = stepEndTime - stepStartTime;
            Log.d("DalnimTag", "2단계 - 미디어 파일 처리: " + mediaTime + "ms, 파일 수: " + listMedia.size());
        }

        // 3. 자막 파일 처리
        final List<PlayerFileModel> listSubtitle = new ArrayList<>();
        if (isAddSubtitleLyric || isAddMedia) {
            stepStartTime = System.currentTimeMillis();
            
            final Map<String, PlayerFileModel> mapSubtitle = new HashMap<>();
            List<String> subtitleExtensions = new ArrayList<>();
            if (appMediaType == Constant.AppMediaType.VIDEO || appMediaType == Constant.AppMediaType.SUBTITLE)
                subtitleExtensions.addAll(SupportSubtitleFormat.getSubtitles());
            else if (appMediaType == Constant.AppMediaType.MUSIC || appMediaType == Constant.AppMediaType.LYRIC)
                subtitleExtensions.addAll(SupportSubtitleFormat.getLyrics());

            IOFileFilter subtitleFilter = new SuffixFileFilter(subtitleExtensions, IOCase.INSENSITIVE);
            Collection<File> subtitleLyricFiles = new ArrayList<>();
            if (path == null) {
                subtitleLyricFiles = getMediaFilesAndSubtitleFiles(context, appMediaType, isRecursive);
            } else {
                final File currentFile = new File(path);
                subtitleLyricFiles = FileUtils.listFiles(currentFile, subtitleFilter, isRecursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE);
            }
//            Collection<File> subtitleLyricFiles = FileUtils.listFiles(currentFile, subtitleFilter, isRecursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE);
            if (!Utils.isEmptyCollection(subtitleLyricFiles)) { //subtitleFiles != null && !subtitleFiles.isEmpty()) {
                for (File file : subtitleLyricFiles) {
                    if (file.length() <= 0)
                        continue;;

//                    if (!isAddFolder) {
//                        if (doNotAddThisFile(cameraDirectory, araoneTempDirectory, file))
//                            continue;
//                    }
                    if (appMediaType == Constant.AppMediaType.VIDEO || appMediaType == Constant.AppMediaType.SUBTITLE) {
                        if (FileUtil.checkSubtitleExtension(file.getName())) {
                            PlayerFileModel t = new PlayerFileModel(file.getName(), file.length(), file.lastModified(), file.getPath(), PlayerFileModel.DirectoryType.NONE);
//                        String key = FilenameUtils.removeExtension(file.getPath());
                            listSubtitle.add(t);
                            String filenameNFD = Normalizer.normalize(FilenameUtils.removeExtension(file.getPath()), Normalizer.Form.NFD);
                            mapSubtitle.put(filenameNFD, t);
                        }
                    } else if (appMediaType == Constant.AppMediaType.MUSIC || appMediaType == Constant.AppMediaType.LYRIC) {
                        if (FileUtil.checkLyricExtension(file.getName())) {
                            PlayerFileModel t = new PlayerFileModel(file.getName(), file.length(), file.lastModified(), file.getPath(), PlayerFileModel.DirectoryType.NONE);
//                        String key = FilenameUtils.removeExtension(file.getPath());
                            listSubtitle.add(t);
                            String filenameNFD = Normalizer.normalize(FilenameUtils.removeExtension(file.getPath()), Normalizer.Form.NFD);
                            mapSubtitle.put(filenameNFD, t);
                        }
                    }
                }
            }

            //If AraPlayer refresh (Get All video files from root folder) video files, then set the default subtitle file to the video file(If they have same file name)
            for (String key : mapMedia.keySet()) {
                if (mapSubtitle.containsKey(key)) {
                    PlayerFileModel model = mapMedia.get(key);
                    PlayerFileModel subtitle = mapSubtitle.get(key);
                    model.setSubPath1(subtitle.getPath());
                }
            }
            
            stepEndTime = System.currentTimeMillis();
            subtitleTime = stepEndTime - stepStartTime;
            Log.d("DalnimTag", "3단계 - 자막 파일 처리: " + subtitleTime + "ms, 파일 수: " + listSubtitle.size());
        }

        // 4. 결과 병합
        stepStartTime = System.currentTimeMillis();
        
        if (isAddMedia) {
            list.addAll(listMedia);
        }
        if (isAddSubtitleLyric) {
            list.addAll(listSubtitle);
        }
        
        stepEndTime = System.currentTimeMillis();
        mergeTime = stepEndTime - stepStartTime;
        Log.d("DalnimTag", "4단계 - 결과 병합: " + mergeTime + "ms");

        long totalEndTime = System.currentTimeMillis();
        long totalTime = totalEndTime - totalStartTime;
        long calculatedTotal = folderTime + mediaTime + subtitleTime + mergeTime;
        
        Log.d("DalnimTag", "=== 파일 스캔 성능 분석 ===");
        Log.d("DalnimTag", "폴더 처리: " + folderTime + "ms");
        Log.d("DalnimTag", "미디어 파일: " + mediaTime + "ms");
        Log.d("DalnimTag", "자막 파일: " + subtitleTime + "ms");
        Log.d("DalnimTag", "결과 병합: " + mergeTime + "ms");
        Log.d("DalnimTag", "단계별 합계: " + calculatedTotal + "ms");
        Log.d("DalnimTag", "전체 소요시간: " + totalTime + "ms");
        Log.d("DalnimTag", "오버헤드: " + (totalTime - calculatedTotal) + "ms");
        Log.d("DalnimTag", "총 파일 수: " + list.size());
        Log.d("DalnimTag", "========================");
        
        return list;
    }

//    private static boolean isTooSmallSizeMediaFile(File file) {
//        int minFileSize = 1024 * 10;
//        return file.length() <= minFileSize;
//    }

//    @NotNull
//    private static PlayerFileModel.SeriesType getSeriesType(File file) {
//        PlayerFileModel.SeriesType seriesType = PlayerFileModel.SeriesType.NONE;
//        List<Integer> listSeasonEpisode = StringUtils.getSeasonEpisodeNumber(file.getName());
//        int seasonNumber = listSeasonEpisode.get(0);
//        int episodeNumber = listSeasonEpisode.get(1);
//        boolean hasEpisode = ((seasonNumber != -1) && (episodeNumber != -1)) ? true : false;
//        if (hasEpisode) {
//            seriesType = PlayerFileModel.SeriesType.SERIES;
//        }
//        return seriesType;
//    }

//    private static boolean doNotAddThisFile(String cameraDirectory, boolean isShowVideoInDCIM, String araoneTempDirectory, File file) {
//
//        if (file.getAbsolutePath().startsWith(araoneTempDirectory)) {
//            DLog.d(TAG, "skip a file in AraOne/Temp : " + file.getAbsolutePath());
//            return true;
//        }
//
//        if (!isShowVideoInDCIM) {
//            if (file.getAbsolutePath().startsWith(cameraDirectory)) {
//                DLog.d(TAG, "skip a file in cameraDictory : " + file.getAbsolutePath());
//                return true;
//            }
//        }
//
//        String fileAbsPathLowercase = file.getAbsolutePath().toLowerCase();
//        if (fileAbsPathLowercase.contains("/trash/")
//            || fileAbsPathLowercase.contains("/android/data/")
//            || fileAbsPathLowercase.contains("/android/.trash/")
//                || fileAbsPathLowercase.contains("/araone/voice/")
//            || fileAbsPathLowercase.contains("/dcim/")) {
//            return true;
//        }
//
//        return false;
//    }

    public static List<PlayerFileModel> searchFileName(Context context, String path, String query, boolean isSameName) {
        DLog.d(TAG, "searchFileName - path=" + path + " - query=" + query);
        final List<PlayerFileModel> list = getAllSubtitlesOrLyrics(context, path, true);
        final List<PlayerFileModel> subtitles = new ArrayList<>();
        final List<String> listNames = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (PlayerFileModel file : list) {
                if ((isSameName || (!listNames.contains(file.getName()))) && FilenameUtils.getBaseName(file.getName()).equals(query)) {
                    listNames.add(file.getName());
                    subtitles.add(file);
                }
            }
        }
        return subtitles;
    }

    public static int getNumberOfFiles(File path, int mediaType) {
        int numberOfFiles = 0;
        if(path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return numberOfFiles;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    numberOfFiles += getNumberOfFiles(file, mediaType);
                } else if (mediaType == Constant.AppMediaType.VIDEO && FileUtil.isValidVideoExtension(file.getName())) {
                    numberOfFiles++;
                } else if (mediaType == Constant.AppMediaType.SUBTITLE && FileUtil.checkSubtitleExtension(file.getName())) {
                    numberOfFiles++;
                } else if (mediaType == Constant.AppMediaType.MUSIC && FileUtil.isValidMusicExtension(file.getName())) {
                    numberOfFiles++;
                } else if (mediaType == Constant.AppMediaType.LYRIC && FileUtil.checkLyricExtension(file.getName())) {
                    numberOfFiles++;
//                } else if (type == Constant.PLAYER.STORAGE.COUNT_OF_FILES.VIDEO && FileUtil.isValidVideoExtension(file.getName())) {
//                    numberOfFiles++;
//                } else if (type == Constant.PLAYER.STORAGE.COUNT_OF_FILES.SUBTITLE && FileUtil.checkSubtitleExtension(file.getName())) {
//                    numberOfFiles++;
                }
            }
        }
        return numberOfFiles;
    }

    public static String getResolutionInfo(FFmpegMediaMetadataRetriever mmr) {
        String videoHeight = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String videoWidth = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        if ((videoHeight == null) || (videoHeight == null))
            return "";

        return  videoHeight + " x " + videoWidth;
    }

    public static String getResolutionInfo(MediaMetadataRetriever mmr) {
        String videoHeight = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String videoWidth = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        if ((videoHeight == null) || (videoHeight == null))
            return "";

        return  videoHeight + " x " + videoWidth;
    }

    public static boolean isRecommenedCodec(FFmpegMediaMetadataRetriever mmr) {
        String videoCodec = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_CODEC);
        videoCodec = videoCodec.replace(".", "");
        String audioCodec = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_AUDIO_CODEC);
        if ((videoCodec != null) && videoCodec.toUpperCase().equals(Constant.PLAYER.RECOMMENDED_CODEC.VIDEO) &&
            (audioCodec != null) && audioCodec.toUpperCase().equals(Constant.PLAYER.RECOMMENDED_CODEC.AUDIO)) {
            return true;
        }
        return false;
    }
    public static String getCodecInfo(Context context, FFmpegMediaMetadataRetriever mmr) {
        String videoCodec = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_CODEC);
        String audioCodec = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_AUDIO_CODEC);
        String result = "";
        if (videoCodec == null)
            result = context.getString(R.string.video) + "(" + context.getString(R.string.codec_not_exist) + "), ";
        else
            result = context.getString(R.string.video) + "(" + videoCodec + "), ";

        if (audioCodec == null)
            result += context.getString(R.string.audio) + "(" + context.getString(R.string.codec_not_exist) + ")";
        else
            result += context.getString(R.string.audio) + "(" + audioCodec + ")";


        return result;
    }

    public static String getCodecInfoAudioFile(Context context, FFmpegMediaMetadataRetriever mmr) {
        String audioCodec = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_AUDIO_CODEC);
        String result = "";
        if (audioCodec == null)
            result += context.getString(R.string.audio) + "(" + context.getString(R.string.codec_not_exist) + ")";
        else
            result += context.getString(R.string.audio) + "(" + audioCodec + ")";


        return result;
    }

    public static Lyric getMusicInfo(FFmpegMediaMetadataRetriever mmr) {
        Lyric lyric = new Lyric();
        lyric.setTitle(mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE));
        lyric.setArtist(mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST));
        lyric.setAlbum(mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM));
        return lyric;
    }

    public static String generateTMDBPoster(String name, String path) {
        return name + Constant.PLAYER.THE_MOVIE_DB.BASE_FILE_POSTER + FilenameUtils.getExtension(path);
    }

    public static String generateTMDBSeasonPoster(String name, String path) {
        return name + Constant.PLAYER.THE_MOVIE_DB.BASE_FILE_POSTER + FilenameUtils.getExtension(path);
    }

    //For Movie
    public static String generateTMDBBackdrop(String name, String path) {
        return name + Constant.PLAYER.THE_MOVIE_DB.BASE_FILE_BACKDROP + FilenameUtils.getExtension(path);
    }

    //For Tv's backdrop
    public static String generateTMDBStill(String name, String path) {
        return name + Constant.PLAYER.THE_MOVIE_DB.BASE_FILE_STILL + FilenameUtils.getExtension(path);
    }

    public static String generateTMDBJson(int id, String mediaType) {
        return mediaType + "_" + id + Constant.PLAYER.THE_MOVIE_DB.BASE_FILE_JSON;
    }


    public static String getTMDBPath(Context context) {
        return getAppPath(context) + File.separator + Constant.FOLDER_ARAPLAYER.TMDB;
//        return context.getExternalFilesDir(null).getAbsolutePath() + File.separator + Constant.FOLDER_ARAPLAYER.TMDB;
//        return getRootPath(context, Constant.FOLDER_ARAONE.TMDB_PATH);
    }



    public static String getTMDBPath(Context context, int name) {
        return getTMDBPath(context, String.valueOf(name));
    }

    public static String getTMDBPath(Context context, String name) {
        return getTMDBPath(context) + File.separator + name;
    }

    public static String getTMDBPath(Context context, String folder, String name) {
        return getTMDBPath(context) + File.separator + folder + File.separator + name;
    }

    public static String getTMDBPath(Context context, VideoInformationModel video) {
        return getTMDBPath(context, video.generateFolderName());
    }
    //왜 지우지? sdcard/Android/data/com.dalnimsoft.araplayer/files밑의 Documents와 Download폴더는 뭔지 모르겠음.
    //참고로 자막 sqlite나 영화 썸네일 이미지는 Local밑에 storage밑에 각자 폴더 밑에 있음.
    public static void deleteBaseAndRelatedFolder(Context context) {
        deleteFolder(getAppPath(context) + File.separator + Constant.FOLDER_ARAONE.DOCUMENTS);
        deleteFolder(getAppPath(context) + File.separator + Constant.FOLDER_ARAONE.DOWNLOAD);
    }

    public static void createAppRelatedFolder(Context context) {
        createTmdbFolders(context);
        createAppMediaFolder(context);
    }
    private static void createTmdbFolders(Context context) {
        if ((AppFlavorUtil.isAraPlayerApp()) && (FileUtil.isVideoApp())) {
            createFolder(getTMDBPath(context));
        }
    }
    private static void createAppMediaFolder(Context context) {
        createFolder(getMediaAbsoluteFolder(context));
    }
    public static String getMediaAbsoluteFolder(Context context) {
        String folderPath = "";
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            folderPath = getMediaFolderWithoutLangName(context);
        } else {
            folderPath =getMediaFolderWithLangName(context);
        }
        return getAbsoluteDir(context, folderPath);
    }
    @NonNull
    public static String getMediaFolderWithoutLangName(Context context) {
        return getMediaFolderPathCommonForStudyLanguage(context, false, false);
    }
    @NonNull
    public static String getMediaFolderWithLangName(Context context) {
        return getMediaFolderPathCommonForStudyLanguage(context, false, true);
    }

//    public static void createVideoFolder(Context context) {
//        createFolder(getVideoFolderFullPath(context));
//    }

//    public static String getVideoFolderFullPath(Context context) {
//        String path = "";
//        SharedPreferencesDB sharedPreferencesDB = SharedPreferencesDB.getInstance(context);
//        EnumLanguage studyLanguage = EnumLanguage.findByFormatApi(sharedPreferencesDB.getStudyLanguage());
//        if ((studyLanguage == EnumLanguage.JAPANESE)
//                || (studyLanguage == EnumLanguage.CHINESE_SIMPLIFIED)
//                || (studyLanguage == EnumLanguage.ENGLISH)) {
//
//            path = getMediaFolderPathForStudyLanguage(context);
//        }
//        return getAbsoluteDir(context, path);
//    }



//    public static String getMediaFolderPathForStudyLanguage(Context context) {
////        SharedPreferencesDB sharedPreferencesDB = SharedPreferencesDB.getInstance(context);
////        EnumLanguage studyLanguage = EnumLanguage.findByFormatApi(sharedPreferencesDB.getStudyLanguage());
////
////        final String appName = context.getString(R.string.app_name);
////        return FilenameUtils.concat(Constant.FOLDER_ARAONE.DOWNLOAD, appName + "_" + studyLanguage.getMediaFolder());
//        return getMediaFolderPathCommonForStudyLanguage(context, false, true);
//    }

//    public static String getMediaFullFolderPathForStudyLanguage(Context context) {
//        return getMediaFolderPathCommonForStudyLanguage(context, true, true);
//    }

    public static String getMediaFolderPathCommonForStudyLanguage(Context context, boolean isFullPath, boolean isAddLanguageName) {
        String folderName = getMediaFolderName(context); //context.getString(R.string.app_name); + "_" + studyLanguage.getMediaFolder();

        if (isAddLanguageName) {
            SharedPreferencesDB sharedPreferencesDB = SharedPreferencesDB.getInstance(context);
            EnumLanguage studyLanguage = EnumLanguage.findByFormatApi(sharedPreferencesDB.getStudyLanguage());
            folderName = folderName + "_" + studyLanguage.getMediaFolder();
        }
        String pathName = Constant.FOLDER_ARAONE.DOWNLOAD;
        if (isFullPath)
            pathName = getDownloadFolderPath(context);
        return FilenameUtils.concat(pathName, folderName);
    }

    private static String getJsonFilePath(Context context, String tableName) {
        String folderName = getMediaFolderPathCommonForStudyLanguage(context, true, false);
        String fileName = "backup_" + tableName + "." + Constants.FILEEXT_ara;
        return FilenameUtils.concat(folderName, fileName);
    }

    public static String getDicPlayerScreenJsonPath(Context context, String tableName) {
        return getJsonFilePath(context, tableName);
    }

    public static String getPlaylistJsonPath(Context context) {
        return getJsonFilePath(context, PlaylistModel.class.getSimpleName());
    }

    private static String getMediaFolderName(@NonNull Context context) {
        final String mediaFolderAraMultiPlayer = "AraMultiPlayer";
        final String mediaFolderAraPlayer = "AraPlayer";
        final String mediaFolderAraMusicPlayer = "AraMusicPlayer";
        String result = context.getString(R.string.app_name);
        if (AppFlavorUtil.isAraPlayerApp()) {
            result = mediaFolderAraPlayer;
        } else if (AppFlavorUtil.isAraMultiPlayerApp()) {
            result = mediaFolderAraMultiPlayer;
        } else if (AppFlavorUtil.isAraMusicApp()) {
            result = mediaFolderAraMusicPlayer;
        }
        return result;
    }

    public static void removeSubtitleSQLiteFile(Context context, PlayerFileModel playerFileModel) {
        String sqliteFilePath = StorageUtil.generateSubtitleSQLitePathUnderAndroidFolder(context, playerFileModel.getVideoModel().getPath(), playerFileModel);
        String sqliteJournalFile = sqliteFilePath + "-journal";

        removeFile(sqliteFilePath);
        removeFile(sqliteJournalFile);
    }

    public static void removeLastPositionVideoScreenFile(Context context, PlayerFileModel playerFileModel) {
        removeFile(StorageUtil.generateLastPositionVideoScreenFilepath(context, playerFileModel.getVideoModel().getPath(), playerFileModel));
    }

    public static String getBaseFileNameWithNumberIncrement(String folderPath, String baseFileName, String extension) {
        String newFileName = baseFileName;
        String newFileNameWithExtension = baseFileName + "." + extension;
        int num = 0;

        File file = new File(folderPath, newFileNameWithExtension);
        while(file.exists()) {
            num++;
            newFileName = baseFileName + "_" + num;
            newFileNameWithExtension = newFileName + "." + extension;
            file = new File(folderPath, newFileNameWithExtension);
        }
        return newFileName;
    }

    public static String getFilesStoragePath(Context context, PlayerFileModel playerFileModel) {
        String path;
        if (playerFileModel.isLocal()) {
            path = getFolderLocalVideo(context);
        } else {
            path = getFolderNetworkVideo(context, playerFileModel);
        }
        return path;
    }

    public static String generateLastPositionVideoScreenFilepath(Context context, String videoFileName, PlayerFileModel playerFileModel) {
        return getFilesStoragePath(context, playerFileModel) + videoFileName + Constant.PLAYER.FILE_EXT_PNG;
    }

    public static String generateLastPositionVideoScreenFilepath(Context context, String videoFileName) {
        return getFolderLocalVideo(context) + videoFileName + Constant.PLAYER.FILE_EXT_PNG;
    }

    //영화 자막 SQL파일은 쓰기 권한이 없을수 있으므로 내 앱의 밑에 둔다.
    public static String generateSubtitleSQLitePathUnderAndroidFolder(Context context, String videoFileName, PlayerFileModel playerFileModel) {
        return getFilesStoragePath(context, playerFileModel) + videoFileName + Constant.PLAYER.DATABASE.SQLITE;
    }

    public static String getFolderNetworkVideo(Context context, PlayerFileModel playerFileModel) {
        return getFolderZip(context) + getNetworkPath(playerFileModel);
    }

    public static String getNetworkPath(PlayerFileModel playerFileModel) {
        String path = "";
        ServerModel serverModel = playerFileModel.getServerModel();
        if (!playerFileModel.isLocal() && serverModel != null) {
            path += File.separator + Constant.FILE.FOLDER_NETWORK + File.separator + serverModel.getHost() + "_" + serverModel.getPort() + "_" + serverModel.getTitle();
        }
        return path;
    }

//    protected static boolean doNotAddThisFile(String araoneTempDirectory, MediaAraPlayer media) {
//
//        if (media.getPath().contains(araoneTempDirectory)) {
//            return true;
//        }
//
////        if (!isShowVideoInDCIM) {
////            if (media.getPath().contains(cameraDirectory)) {
////                return true;
////            }
////        }
//
//        String fileAbsPathLowercase = media.getPath().toLowerCase();
//        if (fileAbsPathLowercase.contains("/trash/")
//                || fileAbsPathLowercase.contains("/android/data/")
//                || fileAbsPathLowercase.contains("/android/.trash/")
//                || fileAbsPathLowercase.contains("/araone/")
//                || fileAbsPathLowercase.contains("/android/media/")
//                || fileAbsPathLowercase.contains("/call/")
////                || fileAbsPathLowercase.contains("/dcim/")
//        ) {
//            return true;
//        }
//
//        return false;
//    }
//
//    protected static boolean doNotAddThisFile(String araoneTempDirectory, File file) {
//
//        if (file.getAbsolutePath().startsWith(araoneTempDirectory)) {
//            DLog.d(TAG, "skip a file in AraOne/Temp : " + file.getAbsolutePath());
//            return true;
//        }
//
////        if (!isShowVideoInDCIM) {
////            if (file.getAbsolutePath().startsWith(cameraDirectory)) {
////                DLog.d(TAG, "skip a file in cameraDictory : " + file.getAbsolutePath());
////                return true;
////            }
////        }
//
//        String fileAbsPathLowercase = file.getAbsolutePath().toLowerCase();
//        if (fileAbsPathLowercase.contains("/trash/")
//                || fileAbsPathLowercase.contains("/android/data/")
//                || fileAbsPathLowercase.contains("/android/.trash/")
////                || fileAbsPathLowercase.contains("/araone/")
////                || fileAbsPathLowercase.contains("/android/media/")
////                || fileAbsPathLowercase.contains("/dcim/")
//                || fileAbsPathLowercase.contains("/call/")
//                ) {
//            return true;
//        }
//
//        return false;
//    }
    public static String convertToHiddenFiles(PlayerFileModel item, boolean isToHidden) {
        boolean isConverted = false;
        String currentPath = item.getPath();
        File file = new File(currentPath);
        String newPath = "";

        boolean isHiddenFileName = file.getName().startsWith(".");
        if (isToHidden) {
            // 숨김 파일로 만들기: 파일명 앞에 점을 붙임
            if (!isHiddenFileName) {
                newPath = file.getParent() + File.separator + "." + file.getName();
            }
        } else {
            // 일반 파일로 만들기: 파일명 앞에 점을 제거
            if (isHiddenFileName) {
                newPath = file.getParent() + File.separator + file.getName().substring(1);
            }
        }

        if (!newPath.isEmpty()) {
            if (file.renameTo(new File(newPath))) {
                item.setPath(newPath);
                isConverted = true;
            }
        }
        return isConverted ? currentPath : "";
    }
}
