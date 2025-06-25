package com.dalread.util;

import com.dalread.model.PlayerFileModel;
import com.dalread.model.VideoModel;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class MediaListUtil {
    static public List<PlayerFileModel> generateSearchData(String search, List<PlayerFileModel> fileListTotal) {
        final List<PlayerFileModel> list = new ArrayList<>();
        final String searchLowercase = (search == null ? "" : search.trim().toLowerCase());

        //Dalnim : Due to Korean video name, some name has NFD string. (ex.씰 can be found with this.) But this also find 동 when I type only 도)
        final String searchLowercaseNFD = Normalizer.normalize(searchLowercase, Normalizer.Form.NFD);
        final String searchLowercaseNFC = Normalizer.normalize(searchLowercase, Normalizer.Form.NFC);
        if (!Utils.isEmpty(searchLowercase)) {
            for (PlayerFileModel f : fileListTotal) {
                VideoModel videoModel = f.getVideoModel();
                if (f.getVideoModel() == null)
                    continue;

                String videoName = videoModel.getName().trim().toLowerCase();
                String memo = videoModel.getMemo() == null ? "" : videoModel.getMemo().trim().toLowerCase();
                String displayTitle = videoModel.getDisplayTitle() == null ? "" : videoModel.getDisplayTitle().trim().toLowerCase();
                String ttsTitle = videoModel.getTitleTts() == null ? "" : videoModel.getTitleTts().trim().toLowerCase();
                String ttsArtist = videoModel.getArtistTts() == null ? "" : videoModel.getArtistTts().trim().toLowerCase();
                String artist = videoModel.getArtist() == null ? "" : videoModel.getArtist().trim().toLowerCase();
                if (videoName.contains(searchLowercase)
                        || memo.contains(searchLowercase)
                        || displayTitle.contains(searchLowercase)
                        || ttsTitle.contains(searchLowercase)
                        || ttsArtist.contains(searchLowercase)
                        || artist.contains(searchLowercase)) {
                    list.add(f);
                } else if (videoName.contains(searchLowercaseNFD)
                        || memo.contains(searchLowercaseNFD)
                        || displayTitle.contains(searchLowercaseNFD)
                        || ttsTitle.contains(searchLowercaseNFD)
                        || ttsArtist.contains(searchLowercaseNFD)
                        || artist.contains(searchLowercaseNFD)) {
                    list.add(f);
                } else if (videoName.contains(searchLowercaseNFC)
                        || memo.contains(searchLowercaseNFC)
                        || displayTitle.contains(searchLowercaseNFC)
                        || ttsTitle.contains(searchLowercaseNFC)
                        || ttsArtist.contains(searchLowercaseNFC)
                        || artist.contains(searchLowercaseNFC)) {
                    list.add(f);
                }
            }
        } else {
            list.addAll(fileListTotal);
        }
        return list;
    }
}
