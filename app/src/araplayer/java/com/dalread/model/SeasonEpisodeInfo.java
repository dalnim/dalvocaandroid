package com.dalread.model;

import com.dalread.util.Constant;
import com.dalread.util.Utils;

import org.apache.commons.io.FilenameUtils;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeasonEpisodeInfo {
//    private String tvShowTitle = "";
    private String seasonNameVideoFile = "";
    private String seasonNameTmdb = "";
    private String tvShowNameVideoFile = "";
    private int seasonNumber = -1;
    private int episodeNumber = -1;
    private PlayerFileModel.SeriesType seriesType = PlayerFileModel.SeriesType.NONE;


    public SeasonEpisodeInfo(String path) {

        try {
            if (parseFileName(path)) {
                seriesType = PlayerFileModel.SeriesType.SERIES;
                seasonNameTmdb = tvShowNameVideoFile;
                seasonNameVideoFile = tvShowNameVideoFile + Constant.BASE_ONE_SPACE + seasonNumber;
            } else {
                seriesType = PlayerFileModel.SeriesType.NONE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean parseFileName(String path) {
        String fileName = FilenameUtils.getName(path);
        if (Utils.isEmpty(fileName))
            return false;

        //ex) ~~.1x1.~~ , ~~.01x01.~~
        if (getSeasonInfoFromMatcher(Pattern.compile("(.*)\\.(\\d{1,2})x(\\d{1,2})\\.(.*)", Pattern.CASE_INSENSITIVE).matcher(fileName))) {
            return true;
        }


        //ex) ~~S01E01~~ , ~~S01EP01~~ , ~~S01 EP01~~ ,~~S1E1~~, ~~S1E01~~, ~~S01E1~~
        if (getSeasonInfoFromMatcher(Pattern.compile("(.*)s(\\d{1,2}) ?ep?(\\d{1,2})(.*)", Pattern.CASE_INSENSITIVE).matcher(fileName))) {
            return true;
        }
        //ex) ~~S01x01~~~, ~~S1x1~~~, ~~S1x01~~~, ~~S01x1~~~
        if (getSeasonInfoFromMatcher(Pattern.compile("(.*)s(\\d{1,2}) ?x(\\d{1,2})(.*)", Pattern.CASE_INSENSITIVE).matcher(fileName))) {
            return true;
        }
        final String koreanSeason = "시즌";
        //ex) ~~시즌01E01~~~
        if (getSeasonInfoFromMatcher(Pattern.compile("(.*)" + koreanSeason + " ?(\\d{1,2}) ?\\.?ep?(\\d{1,2})(.*)", Pattern.CASE_INSENSITIVE).matcher(fileName))) {
            return true;
        }
        //ex) ~~시즌01x01~~~
        if (getSeasonInfoFromMatcher(Pattern.compile("(.*)" + koreanSeason + " ?(\\d{1,2}) ?\\.?x(\\d{1,2})(.*)", Pattern.CASE_INSENSITIVE).matcher(fileName))) {
            return true;
        }
        final String koreanSeasonNFD = Normalizer.normalize(koreanSeason, Normalizer.Form.NFD);
        //ex) ~~시즌01E01~~~
        if (getSeasonInfoFromMatcher(Pattern.compile("(.*)" + koreanSeasonNFD + " ?(\\d{1,2}) ?\\.?ep?(\\d{1,2})(.*)", Pattern.CASE_INSENSITIVE).matcher(fileName))) {
            return true;
        }
        //ex) ~~시즌01x01~~~
        if (getSeasonInfoFromMatcher(Pattern.compile("(.*)" + koreanSeasonNFD + " ?(\\d{1,2}) ?\\.?x(\\d{1,2})(.*)", Pattern.CASE_INSENSITIVE).matcher(fileName))) {
            return true;
        }
        return false;
    }

    private boolean getSeasonInfoFromMatcher(Matcher matcher) {
        boolean blnResult = false;
        try {
            if (matcher.find()) {
                tvShowNameVideoFile = matcher.group(1).trim();
                seasonNumber = Integer.valueOf(matcher.group(2).trim());
                episodeNumber = Integer.valueOf(matcher.group(3).trim());
                tvShowNameVideoFile = getAfterSeasonNumberFileNameIfPreviousNameIsTooShort(matcher);
                blnResult = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blnResult;
    }

    private String getAfterSeasonNumberFileNameIfPreviousNameIsTooShort(Matcher matcher) {
        if (tvShowNameVideoFile.length() < 2) {
            String tvShowNameVideoFile2 = matcher.group(4).trim();
            if (tvShowNameVideoFile2.length() > tvShowNameVideoFile.length())
                return FilenameUtils.getBaseName(tvShowNameVideoFile2);
        }
        return tvShowNameVideoFile;
    }
//    public String getTvShowTitle() {
//        return tvShowTitle;
//    }

    public String getSeasonNameTmdb() {
        return seasonNameTmdb;
    }

    public String getSeasonNameVideoFile() {
        return seasonNameVideoFile;
    }

    //    public void setSeries_path(String series_path) {
//        this.series_path = series_path;
//    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

//    public void setSeasonNo(int seasonNo) {
//        this.seasonNo = seasonNo;
//    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

//    public void setEpisodeNo(int episodeNo) {
//        this.episodeNo = episodeNo;
//    }

    public PlayerFileModel.SeriesType getSeriesType() {
        return seriesType;
    }

//    public void setSeriesType(PlayerFileModel.SeriesType seriesType) {
//        this.seriesType = seriesType;
//    }


//    public String getTvShowNameVideoFile() {
//        return tvShowNameVideoFile;
//    }

    public boolean hasEpisode() {
        return seasonNumber != -1 && episodeNumber != -1;
    }
}
