package com.dalread.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.dalread.base.BasePlayerActivity;
import com.dalread.util.BaseMobileAd;
import com.dalread.util.Constant;
import com.dalread.util.FileUtil;
import com.dalread.util.MobileAd;
import com.google.android.gms.ads.AdView;

import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.text.Normalizer;
import java.util.List;

public class PlayerFileModel implements Parcelable {
    private String name;
    private FileType type;
    private DirectoryType directoryType;
    private SeriesType seriesType;
    private SeasonEpisodeInfo seasonEpisodeInfo;
    private VideoModel videoModel = new VideoModel();
    private ServerModel serverModel;
    // https://github.com/dalnim/IssueOnly/issues/113
    private boolean isKeepChange;
    private boolean isCheck;
    public enum FileType {NONE, DIRECTORY, VIDEO, MUSIC, SUBTITLE, LYRIC, ADS_BANNER, SEASON_LIST, SEASON_ITEM, LANGUAGE_FOLDER, MUSIC_CATEGORY, PLAYLIST}
    public enum DirectoryType {NONE, BLACK, WHITE};
    public enum SeriesType {NONE, SERIES, SEASON};
    private int count;
    private int index;
    private AdView adView;
    private List<VideoSeasonModel> videoSeasonModelList;
    private List<MusicCategoryModel> musicCategoryList;
    private PlaylistModel playlistModel;
    private VideoSeasonModel videoSeasonModel;

    public PlayerFileModel(String path) {
        this(Constant.BASE_BLANK, 0, 0, path, DirectoryType.NONE);
    }

    public PlayerFileModel(String name, boolean isDirectory) {
        this(name, 0, 0, Constant.BASE_BLANK, isDirectory);
    }

    public PlayerFileModel(String name, DirectoryType directoryType) {
        this(name, 0, 0, Constant.BASE_BLANK, directoryType);
    }

    public PlayerFileModel(String name, long size, long createdDate, String path, boolean isDirectory) {
        setName(name);
        videoModel.setName(name);
        videoModel.setSize(size);
        videoModel.setCreatedDate(createdDate);
        videoModel.setPath(path);
        this.directoryType = isDirectory ? DirectoryType.WHITE : DirectoryType.NONE;
        this.type = generateFileType();
    }

    public PlayerFileModel(String name, long size, long createdDate, String path, DirectoryType directoryType) {
        setName(name);
        videoModel.setName(name);
        videoModel.setSize(size);
        videoModel.setCreatedDate(createdDate);
        videoModel.setPath(path);
        this.directoryType = directoryType;
        this.type = generateFileType();
    }

    public PlayerFileModel(String name, long size, long duration, long createdDate, String path, DirectoryType directoryType, SeasonEpisodeInfo seasonEpisodeInfo, int mediaType) {
        setName(name);
        videoModel.setName(name);
        videoModel.setSize(size);
        videoModel.setDuration(duration);
        videoModel.setCreatedDate(createdDate);
        videoModel.setPath(path);
        videoModel.setMediaType(mediaType);
        this.directoryType = directoryType;
        this.seriesType = seasonEpisodeInfo.getSeriesType();
        this.seasonEpisodeInfo = seasonEpisodeInfo;
        if (seasonEpisodeInfo.getSeriesType() == SeriesType.SERIES) {
            videoModel.setSeasonNameVideoFile(seasonEpisodeInfo.getSeasonNameVideoFile());
        } else {
            videoModel.setSeasonNameVideoFile("");
        }

        this.type = generateFileType();
    }

    public PlayerFileModel(String name, long size, long duration, long createdDate, String path, DirectoryType directoryType, int mediaType, Tag tag) {
        try {
            setName(name);
            videoModel.setName(name);
            videoModel.setSize(size);
            videoModel.setDuration(duration);
            videoModel.setCreatedDate(createdDate);
            videoModel.setPath(path);
            videoModel.setMediaType(mediaType);
            if (tag != null) {
                videoModel.setDisplayTitle(tag.getFirst(FieldKey.TITLE));
                videoModel.setArtist(tag.getFirst(FieldKey.ARTIST));
                videoModel.setAlbum(tag.getFirst(FieldKey.ALBUM));
            }
            this.directoryType = directoryType;
            videoModel.setSeasonNameVideoFile("");
            this.type = generateFileType();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //For ADS_BANNER
    public PlayerFileModel(Context context) {
        this.name = "Ads";
        videoModel.setName(name);
        videoModel.setSize(0);
//        videoModel.setCreatedDate(createdDate);
        videoModel.setPath("");
        this.directoryType = DirectoryType.NONE;
        this.type = PlayerFileModel.FileType.ADS_BANNER;
        this.adView = new AdView(context);
        this.adView.setAdUnitId(MobileAd.getAdsBannerId(context));
        this.adView.setAdSize(BaseMobileAd.getAdSize(context));
    }

    //For TV Season
    public PlayerFileModel(Context context, VideoSeasonModel videoSeasonModel) {
        this.name = videoSeasonModel.getSeasonNameVideoFile();
        videoModel.setName(name);
        videoModel.setSize(0);
//        videoModel.setCreatedDate(createdDate);
        videoModel.setPath("");
        this.directoryType = DirectoryType.NONE;
        this.type = PlayerFileModel.FileType.SEASON_ITEM;
        this.videoSeasonModel = videoSeasonModel;
    }


    //For SEASON TOP
    public PlayerFileModel(FileType fileType, List<VideoSeasonModel> videoSeasonModelList) {
        this.name = "Season";
        this.type = FileType.SEASON_LIST;
        this.videoSeasonModelList = videoSeasonModelList;
        this.directoryType = DirectoryType.NONE;
        videoModel.setName(name);
        videoModel.setSize(0);
//        videoModel.setCreatedDate(createdDate);
        videoModel.setPath("");
    }

    //For Music Category List
    public PlayerFileModel(List<MusicCategoryModel> musicCategoryList) {
        this.name = "MusicCategory";
        this.type = FileType.MUSIC_CATEGORY;
        this.musicCategoryList = musicCategoryList;
        this.directoryType = DirectoryType.NONE;
        videoModel.setName(name);
        videoModel.setSize(0);
//        videoModel.setCreatedDate(createdDate);
        videoModel.setPath("");
    }

    //For Playlist
    public PlayerFileModel(PlaylistModel playlistModel) {
        this.name = "Playlist";
        this.type = FileType.PLAYLIST;
        this.playlistModel = playlistModel;
        this.directoryType = DirectoryType.NONE;
        videoModel.setName(name);
        videoModel.setSize(0);
        videoModel.setPath("");
    }

    //For LANGUAGE_FOLDER
    public PlayerFileModel(FileType fileType) {
        this.name = "Language_folder";
        this.type = FileType.LANGUAGE_FOLDER;
        this.directoryType = DirectoryType.NONE;
        videoModel.setName(name);
        videoModel.setSize(0);
//        videoModel.setCreatedDate(createdDate);
        videoModel.setPath("");
    }

    public PlayerFileModel(VideoModel v) {
        this.name = v.getName();
        this.type = FileType.VIDEO;
        this.directoryType = DirectoryType.NONE;
        this.setVideoModel(v);
    }

    public PlayerFileModel(VideoModel v, boolean isMediaTypeMusic ) {
        this.name = v.getName();
        if (isMediaTypeMusic)
            this.type = FileType.MUSIC;
        else
            this.type = FileType.VIDEO;
        this.directoryType = DirectoryType.NONE;
        this.setVideoModel(v);
    }

    protected PlayerFileModel(Parcel in) {
        name = in.readString();
        type = FileType.valueOf(in.readString());
        directoryType = DirectoryType.valueOf(in.readString());
        videoModel = in.readParcelable(VideoModel.class.getClassLoader());
        serverModel = in.readParcelable(ServerModel.class.getClassLoader());
        isKeepChange = in.readByte() != 0;
        isCheck = in.readByte() != 0;
        count = in.readInt();
        index = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type.name());
        dest.writeString(directoryType.name());
        dest.writeParcelable(videoModel, flags);
        dest.writeParcelable(serverModel, flags);
        dest.writeByte((byte) (isKeepChange ? 1 : 0));
        dest.writeByte((byte) (isCheck ? 1 : 0));
        dest.writeInt(count);
        dest.writeInt(index);
    }

    public static final Creator<PlayerFileModel> CREATOR = new Creator<PlayerFileModel>() {
        @Override
        public PlayerFileModel createFromParcel(Parcel in) {
            return new PlayerFileModel(in);
        }

        @Override
        public PlayerFileModel[] newArray(int size) {
            return new PlayerFileModel[size];
        }
    };

    public String getName() {
        //Just "name" has a problem with Korean language while sorting name.
        return Normalizer.normalize(name, Normalizer.Form.NFD);
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return getVideoModel().getSize();
    }

    public void setSize(long size) {
        getVideoModel().setSize(size);
    }

    public long getDuration() {
        return getVideoModel().getDuration();
    }

    public void setDuration(long duration) {
        this.getVideoModel().setDuration(duration);
    }

    public long getCreatedDate() {
        return getVideoModel().getCreatedDate();
    }

    public String getPath() {
        return getVideoModel().getPath();
    }

    public void setPath(String path) {
        this.getVideoModel().setPath(path);
    }

    public String getSubPath() {
        return getVideoModel().getSubPath();
    }

    public void setSubPath(String subPath) {
        getVideoModel().setSubPath(subPath);
    }

    public void setSubPath1(String subPath) {
        this.getVideoModel().setSubPath1(subPath);
    }

    public String getSubPath1() {
        return getVideoModel().getSubPath1();
    }

    public String getSubPath2() {
        return getVideoModel().getSubPath2();
    }

    public void setSubPath2(String subPath) {
        this.getVideoModel().setSubPath2(subPath);
    }

    public FileType getType() {
        return type;
    }

//    public boolean isTypeVideo() {
//        return type == FileType.VIDEO;
//    }
    public void setType(FileType type) {
        this.type = type;
    }

    public boolean isDuration() {
        return getVideoModel().getDuration() > 0;
    }

    public boolean isHasSubRuby(Context context) {
        if (context instanceof BasePlayerActivity) {
            return ((BasePlayerActivity) context).getSubDatabase().isHasSubRuby();
        } else {
            return false;
        }

//        return StorageUtil.isFileExist(StorageUtil.generateSubtitleSQLitePathUnderAndroidFolder(context, videoModel.getSubPath1()));
    }

    //TODO : remove this code later
    public boolean isShowRuby() {
        return true;
//        return typeMeaning == Constant.PLAYER.MEANING.WITH || typeMeaning == Constant.PLAYER.MEANING.REFRESH;
    }

    public VideoModel getVideoModel() {
        return videoModel;
    }

    public void setVideoModel(VideoModel videoModel) {
        this.videoModel = videoModel;
    }

    @Override
    public String toString() {
        return "PlayerFileModel{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", directoryType=" + directoryType +
                ", videoModel=" + videoModel +
                ", serverModel=" + serverModel +
                ", isKeepChange=" + isKeepChange +
                ", isCheck=" + isCheck +
                ", count=" + count +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public ServerModel getServerModel() {
        return serverModel;
    }

    public void setServerModel(ServerModel serverModel) {
        this.serverModel = serverModel;
    }

    public boolean isWebDAV() {
        return getServerModel() != null && getServerModel().isWebDAV();
    }

    public boolean isLocal() {
        return getServerModel() == null;
    }

    public boolean isKeepChange() {
        return isKeepChange;
    }

    public void setKeepChange(boolean keepChange) {
        isKeepChange = keepChange;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public void swapCheck() {
        isCheck = !isCheck;
    }

    public FileType generateFileType() {
        if (directoryType == DirectoryType.BLACK || directoryType == DirectoryType.WHITE)
            return FileType.DIRECTORY;
        if (seriesType == SeriesType.SEASON)
            return FileType.SEASON_LIST;
        if (FileUtil.isValidVideoExtension(name))
            return FileType.VIDEO;
        if (FileUtil.isValidMusicExtension(name))
            return FileType.MUSIC;
        if (FileUtil.checkSubtitleExtension(name))
            return FileType.SUBTITLE;
        if (FileUtil.checkLyricExtension(name))
            return FileType.LYRIC;
        return FileType.NONE;
    }

    public boolean isVideo() {
        return type == FileType.VIDEO;
    }

    public boolean isMusic() {
        return type == FileType.MUSIC;
    }

    public void setMusic() {
        this.type = FileType.MUSIC;
    }

    public boolean isMusicCategory() {
        return type == FileType.MUSIC_CATEGORY;
    }

    public boolean isSubtitle() {
        return type == FileType.SUBTITLE;
    }
    public boolean isLyric() {
        return type == FileType.LYRIC;
    }

    public boolean isNone() {
        return type == FileType.NONE;
    }

    public boolean isDirectory() {
        return type == FileType.DIRECTORY;
    }

    public boolean isAdsBanner() {
        return type == FileType.ADS_BANNER;
    }
    public boolean isSeasonList() {
        return type == FileType.SEASON_LIST;
    }
    
    public boolean isMusicCategoryList() {
        return type == FileType.MUSIC_CATEGORY;
    }

    public boolean isSeasonItem() {
        return type == FileType.SEASON_ITEM;
    }

    public boolean isLanguageFolder() {
        return type == FileType.LANGUAGE_FOLDER;
    }
//    public boolean isSeries() {
//        return seriesType == SeriesType.SERIES;
//    }

    public boolean isPlaylist() {
        return type == FileType.PLAYLIST;
    }

    public SeasonEpisodeInfo getSeasonEpisodeInfo() {
        return seasonEpisodeInfo;
    }

    public boolean isDirectoryBlack() {
        return directoryType == DirectoryType.BLACK;
    }

    public boolean isDirectoryWhite() {
        return directoryType == DirectoryType.WHITE;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPercentVocaKnow() {
        int value = 0;
        if (getVideoModel() != null) {
            value = (int) ((getVideoModel().getVocaKnowCount() / (double) getVideoModel().getVocaKnowAll()) * 100);
        }
        return value;
    }

    public boolean isHasTMDB() {
        return getVideoModel() != null && getVideoModel().getTmdbId() > 0;
    }

    public boolean isSearchAuto() {
        return getVideoModel() != null && getVideoModel().isUseMetadata();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public AdView getAdView() {
        return adView;
    }

    public void setAdView(AdView adView) {
        this.adView = adView;
    }

    public List<VideoSeasonModel> getVideoSeasonModelList() {
        return videoSeasonModelList;
    }

    public void setVideoSeasonModelList(List<VideoSeasonModel> videoSeasonModelList) {
        this.videoSeasonModelList = videoSeasonModelList;
    }

    public List<MusicCategoryModel> getMusicCategoryList() {
        return musicCategoryList;
    }

    public void setMusicCategoryList(List<MusicCategoryModel> musicCategoryList) {
        this.musicCategoryList = musicCategoryList;
    }

    public PlaylistModel getPlaylistModel() {
        return playlistModel;
    }

    public void setPlaylistModel(PlaylistModel playlistModel) {
        this.playlistModel = playlistModel;
    }

    public VideoSeasonModel getVideoSeasonModel() {
        return videoSeasonModel;
    }
}
