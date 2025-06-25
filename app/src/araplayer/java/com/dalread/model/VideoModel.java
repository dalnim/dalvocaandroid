package com.dalread.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.dalread.util.Constant;
import com.dalread.util.Utils;

import org.apache.commons.io.FilenameUtils;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class VideoModel extends RealmObject implements Parcelable {
    public static final String FIELD_UNUSED = "unused";
    public static final String FIELD_FAVORITE = "favorite";
    @PrimaryKey
    private String path;
    private String name;
    private String seasonNameVideoFile;
    private long size;
    private long duration;
    private long createdDate;
    private int subtitleEncodingIndex1 = 1; // DEFAULT
    private String subtitleEncoding1 = Constant.PLAYER.OPTION.SUBTITLE.ENCODING.AUTO;
    private int subtitleEncodingIndex2 = 1; // DEFAULT
    private String subtitleEncoding2 = Constant.PLAYER.OPTION.SUBTITLE.ENCODING.AUTO;
    private long lastDuration;
    private float playBeforeSubtitle = Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_KEEP_PLAY_BEFORE_SUBTITLE; //It's seconds unit. (delaySubtitles is milliseconds unit)
    private float playAfterSubtitle = Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_KEEP_PLAY_AFTER_SUBTITLE; //It's  unit. (delaySubtitles is milliseconds unit)
    private float keepPlayBetweenSubtitle = Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE; //It's seconds unit. (delaySubtitles is milliseconds unit)


    private String subPath1;
    private String subPath2;
    private int useSecondSubtitle;
    private int isDisplaySubtitleLangStudyFirst = 1; // 0 or 1, if it's 1 then display study lang subtitle on on top. if it's 0 then display meaning subtitle on top.
    private int motherTongueSubtitleSelectedToDisplay = 1;

    private int vocaKnowCount, vocaKnowAll;
    private int tmdbId;
    private String tmdbKeyword;
    private String tmdbMediaType;
    private String tmdbPosterPath;
    private int tmdbSeasonId;
    private String tmdbSeasonName;
    private String tmdbSeasonPosterPath;
    private int tmdbEpisodeId;
    private String tmdbEpisodeName;
    private String tmdbEpisodePath;
    private String tmdbEpisodeOverview;
    private String tmdbEpisodeOverviewMotherTongue;
    private int tmdbSeasonNumber;
    private int tmdbEpisodeNumber;
    private int trash = Constant.INT_BOOLEAN.FASLE;  //If it's false, delete the record after refresh the video file list. (It's to delete the video record in the Realm for the remove video files on the phone).
    private int useMetadata = 1;
    private int bookmark = Constant.INT_BOOLEAN.FASLE;
    private int newFile = Constant.INT_BOOLEAN.FASLE;
    private int tongueLang;
    private long watchDuration;
    private int hide = Constant.INT_BOOLEAN.FASLE;
    private int delaySubtitles; //It's milliseconds unit. (playBeforeSubtitle, playAfterSubtitle, keepPlayBetweenSubtitle is  seconds unit)
    private int analyzeAgain = Constant.INT_BOOLEAN.FASLE;
    private String memo = Constant.BASE_BLANK;
    private String trackSelectionJsonStr = Constant.BASE_BLANK;
    private int videoFromNetwork = Constant.INT_BOOLEAN.FASLE;
    private int mediaType = 0; // 0 : video, 2 : music
    private String titleTts = Constant.BASE_BLANK; //media title to play by TTS
    private String displayTitle = Constant.BASE_BLANK; //media title to display on the view
    private String artistTts = Constant.BASE_BLANK;
    private String artist = Constant.BASE_BLANK;
    private String album = Constant.BASE_BLANK;
    //버전 2에서 추가
    //------
    private int unused = 0; // 1이상이면 안쓴다.
    private int favorite; // 선호도가 높은 항목에 대해 사용할 예정
    //------
    @Ignore
    private int subPathIndex;

    @Ignore
    private SubtitleLanguageModel subtitleLanguageModel;

    public VideoModel() {
        this(Constant.BASE_BLANK);
    }

    public VideoModel(String path) {
        this.path = path;
    }

    public VideoModel(VideoModel videoModel, String newFilePath) {
        if (videoModel == null) return;
        this.path = newFilePath;
        this.name = FilenameUtils.getName(newFilePath);
        this.seasonNameVideoFile = videoModel.getSeasonNameVideoFile();
        this.size = videoModel.getSize();
        this.duration = videoModel.getDuration();
        this.createdDate = videoModel.getCreatedDate();
        this.subtitleEncodingIndex1 = videoModel.getSubtitleEncodingIndex1();
        this.subtitleEncoding1 = videoModel.getSubtitleEncoding1();
        this.subtitleEncodingIndex2 = videoModel.getSubtitleEncodingIndex2();
        this.subtitleEncoding2 = videoModel.getSubtitleEncoding2();
        this.lastDuration = videoModel.getLastDuration();
        this.playBeforeSubtitle = videoModel.getPlayBeforeSubtitle();
        this.playAfterSubtitle = videoModel.getPlayAfterSubtitle();
        this.keepPlayBetweenSubtitle = videoModel.getKeepPlayBetweenSubtitle();
        this.subPath1 = videoModel.getSubPath1();
        this.subPath2 = videoModel.getSubPath2();
        this.useSecondSubtitle = videoModel.getUseSecondSubtitle();
        this.isDisplaySubtitleLangStudyFirst = videoModel.getIsDisplaySubtitleLangStudyFirst();
        this.motherTongueSubtitleSelectedToDisplay = videoModel.getMotherTongueSubtitleSelectedToDisplay();
        this.subPathIndex = videoModel.getSubPathIndex();
        this.vocaKnowCount = videoModel.getVocaKnowCount();
        this.vocaKnowAll = videoModel.getVocaKnowAll();
        this.tmdbId = videoModel.getTmdbId();
        this.tmdbKeyword = videoModel.getTmdbKeyword();
        this.tmdbMediaType = videoModel.getTmdbMediaType();
        this.tmdbPosterPath = videoModel.getTmdbPosterPath();
        this.tmdbSeasonId = videoModel.getTmdbSeasonId();
        this.tmdbSeasonName = videoModel.getTmdbSeasonName();
        this.tmdbSeasonPosterPath = videoModel.getTmdbSeasonPosterPath();
        this.tmdbEpisodeId = videoModel.getTmdbEpisodeId();
        this.tmdbEpisodeName = videoModel.getTmdbEpisodeName();
        this.tmdbEpisodePath = videoModel.getTmdbEpisodePath();
        this.tmdbEpisodeOverview = videoModel.getTmdbEpisodeOverview();
        this.tmdbEpisodeOverviewMotherTongue = videoModel.getTmdbEpisodeOverviewMotherTongue();
        this.tmdbSeasonNumber = videoModel.getTmdbSeasonNumber();
        this.tmdbEpisodeNumber = videoModel.getTmdbEpisodeNumber();
        this.trash = videoModel.getTrash();
        this.useMetadata = videoModel.getUseMetadata();
        this.bookmark = videoModel.getBookmark();
        this.newFile = videoModel.getNewFile();
        this.tongueLang = videoModel.getTongueLang();
        this.watchDuration = videoModel.getWatchDuration();
        this.hide = videoModel.getHide();
        this.delaySubtitles = videoModel.getDelaySubtitles();
        this.analyzeAgain = videoModel.getAnalyzeAgain();
        this.memo = videoModel.getMemo();
        this.titleTts = videoModel.getTitleTts();
        this.displayTitle = videoModel.getDisplayTitle();
        this.artistTts = videoModel.getArtistTts();
        this.artist = videoModel.getArtist();
        this.album = videoModel.getAlbum();
        this.trackSelectionJsonStr = videoModel.getTrackSelectionJsonStr();
        this.videoFromNetwork = videoModel.isVideoFromNetwork();
        this.mediaType = videoModel.getMediaType();
    }

    public VideoModel(VideoModel videoModel) {
        if (videoModel == null) return;
        this.path = videoModel.getPath();
        this.name = videoModel.getName();
        this.seasonNameVideoFile = videoModel.getSeasonNameVideoFile();
        this.size = videoModel.getSize();
        this.duration = videoModel.getDuration();
        this.createdDate = videoModel.getCreatedDate();
        this.subtitleEncodingIndex1 = videoModel.getSubtitleEncodingIndex1();
        this.subtitleEncoding1 = videoModel.getSubtitleEncoding1();
        this.subtitleEncodingIndex2 = videoModel.getSubtitleEncodingIndex2();
        this.subtitleEncoding2 = videoModel.getSubtitleEncoding2();
        this.lastDuration = videoModel.getLastDuration();
        this.playBeforeSubtitle = videoModel.getPlayBeforeSubtitle();
        this.playAfterSubtitle = videoModel.getPlayAfterSubtitle();
        this.keepPlayBetweenSubtitle = videoModel.getKeepPlayBetweenSubtitle();
        this.subPath1 = videoModel.getSubPath1();
        this.subPath2 = videoModel.getSubPath2();
        this.useSecondSubtitle = videoModel.getUseSecondSubtitle();
        this.isDisplaySubtitleLangStudyFirst = videoModel.getIsDisplaySubtitleLangStudyFirst();
        this.motherTongueSubtitleSelectedToDisplay = videoModel.getMotherTongueSubtitleSelectedToDisplay();
        this.subPathIndex = videoModel.getSubPathIndex();
        this.vocaKnowCount = videoModel.getVocaKnowCount();
        this.vocaKnowAll = videoModel.getVocaKnowAll();
        this.tmdbId = videoModel.getTmdbId();
        this.tmdbKeyword = videoModel.getTmdbKeyword();
        this.tmdbMediaType = videoModel.getTmdbMediaType();
        this.tmdbPosterPath = videoModel.getTmdbPosterPath();
        this.tmdbSeasonId = videoModel.getTmdbSeasonId();
        this.tmdbSeasonName = videoModel.getTmdbSeasonName();
        this.tmdbSeasonPosterPath = videoModel.getTmdbSeasonPosterPath();
        this.tmdbEpisodeId = videoModel.getTmdbEpisodeId();
        this.tmdbEpisodeName = videoModel.getTmdbEpisodeName();
        this.tmdbEpisodePath = videoModel.getTmdbEpisodePath();
        this.tmdbEpisodeOverview = videoModel.getTmdbEpisodeOverview();
        this.tmdbEpisodeOverviewMotherTongue = videoModel.getTmdbEpisodeOverviewMotherTongue();
        this.tmdbSeasonNumber = videoModel.getTmdbSeasonNumber();
        this.tmdbEpisodeNumber = videoModel.getTmdbEpisodeNumber();
        this.trash = videoModel.getTrash();
        this.useMetadata = videoModel.getUseMetadata();
        this.bookmark = videoModel.getBookmark();
        this.newFile = videoModel.getNewFile();
        this.tongueLang = videoModel.getTongueLang();
        this.watchDuration = videoModel.getWatchDuration();
        this.hide = videoModel.getHide();
        this.delaySubtitles = videoModel.getDelaySubtitles();
        this.analyzeAgain = videoModel.getAnalyzeAgain();
        this.memo = videoModel.getMemo();
        this.titleTts = videoModel.getTitleTts();
        this.displayTitle = videoModel.getDisplayTitle();
        this.artistTts = videoModel.getArtistTts();
        this.artist = videoModel.getArtist();
        this.album = videoModel.getAlbum();
        this.trackSelectionJsonStr = videoModel.getTrackSelectionJsonStr();
        this.videoFromNetwork = videoModel.isVideoFromNetwork();
        this.mediaType = videoModel.getMediaType();
    }

    protected VideoModel(Parcel in) {
        path = in.readString();
        name = in.readString();
        seasonNameVideoFile = in.readString();
        size = in.readLong();
        duration = in.readLong();
        createdDate = in.readLong();
        subtitleEncodingIndex1 = in.readInt();
        subtitleEncoding1 = in.readString();
        subtitleEncodingIndex2 = in.readInt();
        subtitleEncoding2 = in.readString();
        lastDuration = in.readLong();
        playBeforeSubtitle = in.readFloat();
        playAfterSubtitle = in.readFloat();
        keepPlayBetweenSubtitle = in.readFloat();
        subPath1 = in.readString();
        subPath2 = in.readString();
        useSecondSubtitle = in.readInt();
        isDisplaySubtitleLangStudyFirst = in.readInt();
        motherTongueSubtitleSelectedToDisplay = in.readInt();
        subPathIndex = in.readInt();
        vocaKnowCount = in.readInt();
        vocaKnowAll = in.readInt();
        tmdbId = in.readInt();
        tmdbKeyword = in.readString();
        tmdbMediaType = in.readString();
        tmdbPosterPath = in.readString();
        tmdbSeasonId = in.readInt();
        tmdbSeasonName = in.readString();
        tmdbSeasonPosterPath = in.readString();
        tmdbEpisodeId = in.readInt();
        tmdbEpisodeName = in.readString();
        tmdbEpisodePath = in.readString();
        tmdbEpisodeOverview = in.readString();
        tmdbEpisodeOverviewMotherTongue = in.readString();
        tmdbSeasonNumber = in.readInt();
        tmdbEpisodeNumber = in.readInt();
        trash = in.readInt();
        useMetadata = in.readInt();
        bookmark = in.readInt();
        newFile = in.readInt();
        tongueLang = in.readInt();
        watchDuration = in.readLong();
        hide = in.readInt();
        delaySubtitles = in.readInt();
        analyzeAgain = in.readInt();
        memo = in.readString();
        titleTts = in.readString();
        displayTitle = in.readString();
        artistTts = in.readString();
        artist = in.readString();
        album = in.readString();
        trackSelectionJsonStr = in.readString();
        videoFromNetwork = in.readInt();
        mediaType = in.readInt();
    }

    public static final Creator<VideoModel> CREATOR = new Creator<VideoModel>() {
        @Override
        public VideoModel createFromParcel(Parcel in) {
            return new VideoModel(in);
        }

        @Override
        public VideoModel[] newArray(int size) {
            return new VideoModel[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;

    }

    public String getSubPath2() {
        return subPath2;
    }

    public void setSubPath2(String subPath2) {
        this.subPath2 = subPath2;
    }

    public int getSubPathIndex() {
        return subPathIndex;
    }

    public void setSubPathIndex(int subPathIndex) {
        this.subPathIndex = subPathIndex;
    }

    public String getBaseName() {
        return FilenameUtils.getBaseName(name);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeasonNameVideoFile() {
        return seasonNameVideoFile;
    }

    public void setSeasonNameVideoFile(String seasonNameVideoFile) {
        this.seasonNameVideoFile = seasonNameVideoFile;
    }

    public boolean isSeason() {
        return Utils.isEmpty(seasonNameVideoFile) ? false : true;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public String getSubtitleEncoding() {
        return isSubPath1() ? subtitleEncoding1 : subtitleEncoding2;
    }

    public int getSubtitleEncodingIndex() {
        return isSubPath1() ? subtitleEncodingIndex1 : subtitleEncodingIndex2;
    }

    public void setSubtitleEncodingIndex(int subtitleEncodingIndex) {
        if (isSubPath1()) {
            this.subtitleEncodingIndex1 = subtitleEncodingIndex;
        } else {
            this.subtitleEncodingIndex2 = subtitleEncodingIndex;
        }
    }

    public void setSubtitleEncoding(String subtitleEncoding) {
        if (isSubPath1()) {
            this.subtitleEncoding1 = subtitleEncoding;
        } else {
            this.subtitleEncoding2 = subtitleEncoding;
        }
    }

    public String getEncoding() {
        if (isSubPath1()) {
            if (subtitleEncoding1.equals(Constant.PLAYER.OPTION.SUBTITLE.ENCODING.AUTO))
                return Constant.BASE_BLANK;
            return subtitleEncoding1;
        } else {
            if (subtitleEncoding2.equals(Constant.PLAYER.OPTION.SUBTITLE.ENCODING.AUTO))
                return Constant.BASE_BLANK;
            return subtitleEncoding2;
        }
    }

    public String getEncodingSubPath1() {
        if (subtitleEncoding1.equals(Constant.PLAYER.OPTION.SUBTITLE.ENCODING.AUTO))
            return Constant.BASE_BLANK;
        return subtitleEncoding1;
    }

    public String getEncodingSubPath2() {
        if (subtitleEncoding2.equals(Constant.PLAYER.OPTION.SUBTITLE.ENCODING.AUTO))
            return Constant.BASE_BLANK;
        return subtitleEncoding2;
    }

    public String getEncodingDefault() {
        if (isSubPath1()) {
            if (subtitleEncoding1.equals(Constant.PLAYER.OPTION.SUBTITLE.ENCODING.AUTO))
                return Constant.PLAYER.OPTION.SUBTITLE.ENCODING.ITEM_OTHER[0];
            return subtitleEncoding1;
        } else {
            if (subtitleEncoding2.equals(Constant.PLAYER.OPTION.SUBTITLE.ENCODING.AUTO))
                return Constant.PLAYER.OPTION.SUBTITLE.ENCODING.ITEM_OTHER[0];
            return subtitleEncoding2;
        }
    }

    public int getSubtitleEncodingIndex1() {
        return subtitleEncodingIndex1;
    }

    public void setSubtitleEncodingIndex1(int subtitleEncodingIndex1) {
        this.subtitleEncodingIndex1 = subtitleEncodingIndex1;
    }

    public String getSubtitleEncoding1() {
        return subtitleEncoding1;
    }

    public String getEncoding1() {
        if (subtitleEncoding1.equals(Constant.PLAYER.OPTION.SUBTITLE.ENCODING.AUTO))
            return Constant.BASE_BLANK;
        return subtitleEncoding1;
    }

    public String getEncoding1Default() {
        if (subtitleEncoding1.equals(Constant.PLAYER.OPTION.SUBTITLE.ENCODING.AUTO))
            return Constant.PLAYER.OPTION.SUBTITLE.ENCODING.ITEM_OTHER[0];
        return subtitleEncoding1;
    }

    public void setSubtitleEncoding1(String subtitleEncoding1) {
        this.subtitleEncoding1 = subtitleEncoding1;
    }

    public int getSubtitleEncodingIndex2() {
        return subtitleEncodingIndex2;
    }

    public void setSubtitleEncodingIndex2(int subtitleEncodingIndex2) {
        this.subtitleEncodingIndex2 = subtitleEncodingIndex2;
    }

    public String getSubtitleEncoding2() {
        return subtitleEncoding2;
    }

    public String getEncoding2() {
        if (subtitleEncoding2.equals(Constant.PLAYER.OPTION.SUBTITLE.ENCODING.AUTO))
            return Constant.BASE_BLANK;
        return subtitleEncoding2;
    }

    public String getEncoding2Default() {
        if (subtitleEncoding2.equals(Constant.PLAYER.OPTION.SUBTITLE.ENCODING.AUTO))
            return Constant.PLAYER.OPTION.SUBTITLE.ENCODING.ITEM_OTHER[0];
        return subtitleEncoding2;
    }

    public void setSubtitleEncoding2(String subtitleEncoding2) {
        this.subtitleEncoding2 = subtitleEncoding2;
    }

    public long getLastDuration() {
        return lastDuration;
    }

    public void setLastDuration(long lastDuration) {
        this.lastDuration = lastDuration;
        setWatchDuration(lastDuration);
    }

    public float getPlayBeforeSubtitle() {
        return playBeforeSubtitle;
    }

    public void setPlayBeforeSubtitle(float playBeforeSubtitle) {
        this.playBeforeSubtitle = playBeforeSubtitle;
    }

    public float getPlayAfterSubtitle() {
        return playAfterSubtitle;
    }

    public void setPlayAfterSubtitle(float playAfterSubtitle) {
        this.playAfterSubtitle = playAfterSubtitle;
    }

    public float getKeepPlayBetweenSubtitle() {
        return keepPlayBetweenSubtitle;
    }

    public void setKeepPlayBetweenSubtitle(float keepPlayBetweenSubtitle) {
        this.keepPlayBetweenSubtitle = keepPlayBetweenSubtitle;
    }

    public String getPlayBeforeAfterAndDelayTime() {
        return getPlayBeforeSubtitle() + ", " + getPlayAfterSubtitle() + ", " + getKeepPlayBetweenSubtitle() + ", " + getDelaySubtitles();
    }

    public String getSubPath() {
        if (isSubPath1()) {
            return getSubPath1();
        }
        return getSubPath2();
    }

    public void setSubPath(String subPath) {
        if (isSubPath1()) {
            setSubPath1(subPath);
        } else {
            setSubPath2(subPath);
        }
    }

    public boolean isSubPath1() {
        return getSubPathIndex() == Constant.PLAYER.INTENT.SUBPATH_INDEX_1 || !isUserSecondSubtitle();
    }

    public boolean hasSubPath1() {
        return !Utils.isEmpty(subPath1);
    }

    public boolean hasSubPath2() {
        return isUserSecondSubtitle() && !Utils.isEmpty(subPath2);
    }

    public String getSubPath1() {
        return subPath1;
    }

    public void setSubPath1(String subPath1) {
        this.subPath1 = subPath1;
    }

    public int getUseSecondSubtitle() {
        return useSecondSubtitle;
    }

    public void setUseSecondSubtitle(int useSecondSubtitle) {
        this.useSecondSubtitle = useSecondSubtitle;
    }

    public void setUseSecondSubtitle(boolean useSecondSubtitle) {
        setUseSecondSubtitle(useSecondSubtitle ? Constant.INT_BOOLEAN.TRUE : Constant.INT_BOOLEAN.FASLE);
    }

    public boolean isUserSecondSubtitle() {
        return getUseSecondSubtitle() == Constant.INT_BOOLEAN.TRUE;
    }

    public int getIsDisplaySubtitleLangStudyFirst() {
        return isDisplaySubtitleLangStudyFirst;
    }

    public void setIsDisplaySubtitleLangStudyFirst(int isDisplaySubtitleLangStudyFirst) {
        this.isDisplaySubtitleLangStudyFirst = isDisplaySubtitleLangStudyFirst;
    }

    public int getMotherTongueSubtitleSelectedToDisplay() {
        return motherTongueSubtitleSelectedToDisplay;
    }

    public void setMotherTongueSubtitleSelectedToDisplay(int motherTongueSubtitleSelectedToDisplay) {
        this.motherTongueSubtitleSelectedToDisplay = motherTongueSubtitleSelectedToDisplay;
    }

    public SubtitleLanguageModel getSubtitleLanguageModel() {
        return subtitleLanguageModel;
    }

    public void setSubtitleLanguageModel(SubtitleLanguageModel subtitleLanguageModel) {
        this.subtitleLanguageModel = subtitleLanguageModel;
    }

//    public List<SubtitleLanguageModel> getSubtitleLanguageModels() {
//        return subtitleLanguageModels;
//    }
//
//    public void setSubtitleLanguageModels(List<SubtitleLanguageModel> subtitleLanguageModels) {
//        this.subtitleLanguageModels = subtitleLanguageModels;
//    }

//    public String getSubPathOriginal() {
//        return subPathOriginal;
//    }
//
//    public void setSubPathOriginal(String subPathOriginal) {
//        this.subPathOriginal = subPathOriginal;
//    }

    public int getVocaKnowCount() {
        return vocaKnowCount;
    }

    public void setVocaKnowCount(int vocaKnowCount) {
        this.vocaKnowCount = vocaKnowCount;
    }

    public int getVocaKnowAll() {
        return vocaKnowAll;
    }

    public void setVocaKnowAll(int vocaKnowAll) {
        this.vocaKnowAll = vocaKnowAll;
    }

    public int getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(int tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getTmdbKeyword() {
        return tmdbKeyword;
    }

    public void setTmdbKeyword(String tmdbKeyword) {
        this.tmdbKeyword = tmdbKeyword;
    }

    public String getTmdbMediaType() {
        return tmdbMediaType;
    }

    public void setTmdbMediaType(String tmdbMediaType) {
        this.tmdbMediaType = tmdbMediaType;
    }

    public String getTmdbPosterPath() {
        return tmdbPosterPath;
    }

    public void setTmdbPosterPath(String tmdbPosterPath) {
        this.tmdbPosterPath = tmdbPosterPath;
    }

    public int getTmdbSeasonId() {
        return tmdbSeasonId;
    }

    public void setTmdbSeasonId(int tmdbSeasonId) {
        this.tmdbSeasonId = tmdbSeasonId;
    }

    public String getTmdbSeasonName() {
        return tmdbSeasonName;
    }

    public void setTmdbSeasonName(String tmdbSeasonName) {
        this.tmdbSeasonName = tmdbSeasonName;
    }

    public String getTmdbSeasonPosterPath() {
        return tmdbSeasonPosterPath;
    }

    public void setTmdbSeasonPosterPath(String tmdbSeasonPosterPath) {
        this.tmdbSeasonPosterPath = tmdbSeasonPosterPath;
    }

    public int getTmdbEpisodeId() {
        return tmdbEpisodeId;
    }

    public void setTmdbEpisodeId(int tmdbEpisodeId) {
        this.tmdbEpisodeId = tmdbEpisodeId;
    }

    public String getTmdbEpisodeName() {
        return tmdbEpisodeName;
    }

    public void setTmdbEpisodeName(String tmdbEpisodeName) {
        this.tmdbEpisodeName = tmdbEpisodeName;
    }

    public String getTmdbEpisodePath() {
        return tmdbEpisodePath;
    }

    public void setTmdbEpisodePath(String tmdbEpisodePath) {
        this.tmdbEpisodePath = tmdbEpisodePath;
    }

    public String getTmdbEpisodeOverview() {
        return tmdbEpisodeOverview;
    }

    public void setTmdbEpisodeOverview(String tmdbEpisodeOverview) {
        this.tmdbEpisodeOverview = tmdbEpisodeOverview;
    }

    public void appendTmdbEpisodeOverview(String tmdbEpisodeOverview) {
        this.tmdbEpisodeOverview = this.tmdbEpisodeOverview + "\n\n" + tmdbEpisodeOverview;
    }

    public String getTmdbEpisodeOverviewMotherTongue() {
        return tmdbEpisodeOverviewMotherTongue;
    }

    public void setTmdbEpisodeOverviewMotherTongue(String value) {
        if (Utils.isEmpty(value))
            this.tmdbEpisodeOverviewMotherTongue = "";
        else
            this.tmdbEpisodeOverviewMotherTongue = value;
    }

    public void appendTmdbEpisodeOverviewMotherTongue(String value) {
        if (Utils.isEmpty(this.tmdbEpisodeOverviewMotherTongue)) {
            this.tmdbEpisodeOverviewMotherTongue = value;
        } else {
            this.tmdbEpisodeOverviewMotherTongue = this.tmdbEpisodeOverviewMotherTongue + "\n\n" + value;
        }

    }

    public int getTmdbSeasonNumber() {
        return tmdbSeasonNumber;
    }

    public void setTmdbSeasonNumber(int tmdbSeasonNumber) {
        this.tmdbSeasonNumber = tmdbSeasonNumber;
    }

    public int getTmdbEpisodeNumber() {
        return tmdbEpisodeNumber;
    }

    public void setTmdbEpisodeNumber(int tmdbEpisodeNumber) {
        this.tmdbEpisodeNumber = tmdbEpisodeNumber;
    }

    public boolean isHasTmdbSeason() {
        return !Utils.isEmpty(getTmdbSeasonName());
    }

    public boolean isTrash() {
        return trash == Constant.INT_BOOLEAN.TRUE;
    }

    public int getTrash() {
        return trash;
    }

    public void setTrash(int trash) {
        this.trash = trash;
    }

    public int getUseMetadata() {
        return useMetadata;
    }

    public void setUseMetadata(int useMetadata) {
        this.useMetadata = useMetadata;
    }

    public boolean isUseMetadata() {
        return this.useMetadata == 1;
    }

    public int getBookmark() {
        return bookmark;
    }

    public void setBookmark(int bookmark) {
        this.bookmark = bookmark;
    }

    public boolean isBookmark() {
        return this.bookmark == Constant.INT_BOOLEAN.TRUE;
    }

    public int getNewFile() {
        return newFile;
    }

    public void setNewFile(int newFile) {
        this.newFile = newFile;
    }

    public boolean isNewFile() {
        return getNewFile() == Constant.INT_BOOLEAN.TRUE;
    }

    public int getTongueLang() {
        return tongueLang;
    }

    public void setTongueLang(int tongueLang) {
        this.tongueLang = tongueLang;
    }

    public long getWatchDuration() {
        return watchDuration;
    }

    public void setWatchDuration(long watchDuration) {
        if (watchDuration > this.watchDuration) {
            this.watchDuration = watchDuration;
        }
    }

    public boolean isHide() {
        return getHide() == Constant.INT_BOOLEAN.TRUE;
    }

    public int getHide() {
        return hide;
    }

    public void setHide(int hide) {
        this.hide = hide;
    }

    public void swapHide() {
        setHide(isHide() ? Constant.INT_BOOLEAN.FASLE : Constant.INT_BOOLEAN.TRUE);
    }

    public int getDelaySubtitles() {
        return delaySubtitles;
    }

    public void setDelaySubtitles(int delaySubtitles) {
        this.delaySubtitles = delaySubtitles;
    }

    public int getAnalyzeAgain() {
        return analyzeAgain;
    }

    public void setAnalyzeAgain(int analyzeAgain) {
        this.analyzeAgain = analyzeAgain;
    }

    public boolean isAnalyzeAgain() {
        return getAnalyzeAgain() == Constant.INT_BOOLEAN.TRUE;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getTitleTts() {
        if (Utils.isEmpty(titleTts))
            if (Utils.isEmpty(displayTitle))
                return getBaseName();
            else
                return displayTitle;
        return titleTts;
    }
    public String getPureTtsTitle() {
        return titleTts;
    }


    public void setTitleTts(String value) {
        this.titleTts = value.trim();
    }

    public String getDisplayTitle() {
        if (Utils.isEmpty(displayTitle))
            return getBaseName();
        return displayTitle;
    }

    public void setDisplayTitle(String value) {
        this.displayTitle = value.trim();
    }

    public String getArtist() {
//        if (Utils.isEmpty(artist))
//            return getBaseName();
        return artist;
    }
    public String getPureArtist() {
        return artist;
    }

    public void setArtist(String value) {
        this.artist = value.trim();
    }

    public String getArtistTts() {
        if (Utils.isEmpty(artistTts))
            return artist;
        return artistTts;
    }
    public String getPureTtsArtist() {
        return artistTts;
    }

    public void setArtistTts(String value) {
        this.artistTts = value.trim();
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String value) {
        this.album = value.trim();
    }

    public String getTrackSelectionJsonStr() {
        return trackSelectionJsonStr;
    }

    public void setTrackSelectionJsonStr(String trackSelectionJsonStr) {
        this.trackSelectionJsonStr = trackSelectionJsonStr;
    }

    public static Creator<VideoModel> getCREATOR() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "VideoModel{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", series_path='" + seasonNameVideoFile + '\'' +
                ", size=" + size +
                ", duration=" + duration +
                ", createdDate=" + createdDate +
                ", subtitleEncodingIndex=" + subtitleEncodingIndex1 +
                ", subtitleEncoding='" + subtitleEncoding1 + '\'' +
                ", lastDuration=" + lastDuration +
                ", playBeforeSubtitle=" + playBeforeSubtitle +
                ", playAfterSubtitle=" + playAfterSubtitle +
                ", keepPlayBetweenSubtitle=" + keepPlayBetweenSubtitle +
                ", subPath='" + subPath1 + '\'' +
                ", subPath2='" + subPath2 + '\'' +
//                ", subPathOriginal='" + subPathOriginal + '\'' +
                ", vocaKnowCount=" + vocaKnowCount +
                ", vocaKnowAll=" + vocaKnowAll +
                ", tmdbId=" + tmdbId +
                ", tmdbKeyword='" + tmdbKeyword + '\'' +
                ", tmdbMediaType='" + tmdbMediaType + '\'' +
                ", tmdbPosterPath='" + tmdbPosterPath + '\'' +
                ", tmdbSeasonId=" + tmdbSeasonId +
                ", tmdbSeasonName='" + tmdbSeasonName + '\'' +
                ", tmdbSeasonPosterPath='" + tmdbSeasonPosterPath + '\'' +
                ", tmdbEpisodeId=" + tmdbEpisodeId +
                ", tmdbEpisodeName='" + tmdbEpisodeName + '\'' +
                ", tmdbEpisodePath='" + tmdbEpisodePath + '\'' +
                ", tmdbEpisodeOverview='" + tmdbEpisodeOverview + '\'' +
                ", tmdbEpisodeOverviewMotherTongue='" + tmdbEpisodeOverviewMotherTongue + '\'' +
                ", tmdbSeasonNumber=" + tmdbSeasonNumber +
                ", tmdbEpisodeNumber=" + tmdbEpisodeNumber +
                ", trash=" + trash +
                ", useMetadata=" + useMetadata +
                ", bookmark=" + bookmark +
                ", newFile=" + newFile +
                ", tongueLang=" + tongueLang +
                ", watchDuration=" + watchDuration +
                ", memo=" + memo +
                ", ttsTitle=" + titleTts +
                ", displayTitle=" + displayTitle +
                ", ttsArtist=" + artistTts +
                ", artist=" + artist +
                ", album=" + album +
                ", trackSelectionJsonStr=" + trackSelectionJsonStr +
                ", videoFromNetwork=" + videoFromNetwork +
                ", mediaType=" + mediaType +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(name);
        dest.writeString(seasonNameVideoFile);
        dest.writeLong(size);
        dest.writeLong(duration);
        dest.writeLong(createdDate);
        dest.writeInt(subtitleEncodingIndex1);
        dest.writeString(subtitleEncoding1);
        dest.writeInt(subtitleEncodingIndex2);
        dest.writeString(subtitleEncoding2);
        dest.writeLong(lastDuration);
        dest.writeFloat(playBeforeSubtitle);
        dest.writeFloat(playAfterSubtitle);
        dest.writeFloat(keepPlayBetweenSubtitle);
        dest.writeString(subPath1);
        dest.writeString(subPath2);
        dest.writeInt(useSecondSubtitle);
        dest.writeInt(isDisplaySubtitleLangStudyFirst);
        dest.writeInt(motherTongueSubtitleSelectedToDisplay);
        dest.writeInt(subPathIndex);
        dest.writeInt(vocaKnowCount);
        dest.writeInt(vocaKnowAll);
        dest.writeInt(tmdbId);
        dest.writeString(tmdbKeyword);
        dest.writeString(tmdbMediaType);
        dest.writeString(tmdbPosterPath);
        dest.writeInt(tmdbSeasonId);
        dest.writeString(tmdbSeasonName);
        dest.writeString(tmdbSeasonPosterPath);
        dest.writeInt(tmdbEpisodeId);
        dest.writeString(tmdbEpisodeName);
        dest.writeString(tmdbEpisodePath);
        dest.writeString(tmdbEpisodeOverview);
        dest.writeString(tmdbEpisodeOverviewMotherTongue);
        dest.writeInt(tmdbSeasonNumber);
        dest.writeInt(tmdbEpisodeNumber);
        dest.writeInt(trash);
        dest.writeInt(useMetadata);
        dest.writeInt(bookmark);
        dest.writeInt(newFile);
        dest.writeInt(tongueLang);
        dest.writeLong(watchDuration);
        dest.writeInt(hide);
        dest.writeInt(delaySubtitles);
        dest.writeInt(analyzeAgain);
        dest.writeString(memo);
        dest.writeString(titleTts);
        dest.writeString(displayTitle);
        dest.writeString(artistTts);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeString(trackSelectionJsonStr);
        dest.writeInt(videoFromNetwork);
        dest.writeInt(mediaType);
    }

    public void clearTMDB() {
        setTmdbId(0);
        setTmdbKeyword(Constant.BASE_BLANK);
        setTmdbMediaType(Constant.BASE_BLANK);
        setTmdbPosterPath(Constant.BASE_BLANK);
        setTmdbSeasonId(0);
        setTmdbSeasonName(Constant.BASE_BLANK);
        setTmdbSeasonPosterPath(Constant.BASE_BLANK);
        setTmdbEpisodePath(tmdbSeasonPosterPath);
        setTmdbEpisodeId(0);
        setTmdbEpisodeName(Constant.BASE_BLANK);
        setTmdbEpisodePath(Constant.BASE_BLANK);
        setTmdbEpisodeOverview(Constant.BASE_BLANK);
        setTmdbEpisodeOverviewMotherTongue(Constant.BASE_BLANK);
        setTmdbSeasonNumber(0);
        setTmdbEpisodeNumber(0);
        setUseMetadata(0);
    }

    public boolean isTVMediaType() {
        return getTmdbMediaType().toLowerCase().equals(Constant.PLAYER.THE_MOVIE_DB.KEY_TV);
    }

    public String getImagePathOriginalSizeOnTmdbWebsite(String name) {
        return Constant.PLAYER.THE_MOVIE_DB.BASE_IMAGE_URL_ORIGINAL_SIZE + name;
    }
    public String getImagePathSmallSizeOnTmdbWebsite(String name) {
        return Constant.PLAYER.THE_MOVIE_DB.BASE_IMAGE_URL_SMALL_SIZE + name;
    }

    public int isVideoFromNetwork() {
        return videoFromNetwork;
    }

    public void setVideoFromNetwork(int videoFromNetwork) {
        this.videoFromNetwork = videoFromNetwork;
    }
    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

}
