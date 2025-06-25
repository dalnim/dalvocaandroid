package com.dalread.util;

import com.dalread.database.sqlite.model.DicModel;

import java.util.List;
import java.util.stream.Collectors;

public class SubtitleContentUtil {

    public static boolean checkSearchSubtitleDialog(int type, int searchIn, DicModel model, String searchValue) {
        final String text = searchValue.replace(Constant.SEARCH.KEY_PERCENT, Constant.BASE_BLANK);
        DLog.d("checkSearchSubtitleDialog", "text=" + text);
        String str1 = Constant.BASE_BLANK;
        String str2 = Constant.BASE_BLANK;
        String strTitle = Constant.BASE_BLANK;
        String strMeaning = Constant.BASE_BLANK;
        String tmpTitle = model.getVocaDisplay();
        if (!Utils.isEmpty(tmpTitle)) {
            strTitle = tmpTitle.toLowerCase();
        }
        String tmpMeaning = model.getMeaning();
        if (!Utils.isEmpty(tmpMeaning)) {
            strMeaning = tmpMeaning.toLowerCase();
        }
        if (searchIn == Constant.SEARCH.VALUE.TITLE) {
            str1 = strTitle;
        } else if (searchIn == Constant.SEARCH.VALUE.MEANING) {
            str1 = strMeaning;
        } else {
            str1 = strTitle;
            str2 = strMeaning;
        }

        if (searchIn == Constant.SEARCH.VALUE.ALL) {
            if (type == Constant.SEARCH.TYPE.START) {
                return str1.startsWith(text) || str2.startsWith(text);
            } else if (type == Constant.SEARCH.TYPE.END) {
                return str1.endsWith(text) || str2.endsWith(text);
            }
            return str1.contains(text) || str2.contains(text);
        } else {
            if (type == Constant.SEARCH.TYPE.START) {
                return str1.startsWith(text);
            } else if (type == Constant.SEARCH.TYPE.END) {
                return str1.endsWith(text);
            }
            return str1.contains(text);
        }
    }
    public static int getSearchType(String value) {
        if (value.startsWith(Constant.SEARCH.KEY_PERCENT))
            return Constant.SEARCH.TYPE.START;
        if (value.endsWith(Constant.SEARCH.KEY_PERCENT))
            return Constant.SEARCH.TYPE.END;
        return Constant.SEARCH.TYPE.MATCHED;
    }

    public static int getValueSearchIn(boolean isSearchTitle, boolean isSearchMeaning) {
        if (isSearchTitle && isSearchMeaning)
            return Constant.SEARCH.VALUE.ALL;
        if (isSearchTitle)
            return Constant.SEARCH.VALUE.TITLE;
        return Constant.SEARCH.VALUE.MEANING;
    }
    public static List<DicModel> getCheckedSubtitle(List<DicModel> subtitleList) {
        return subtitleList.stream()
                .filter( e -> e.isVIChecked())
                .collect(Collectors.toList());
    }

    public static List<Integer> getIdListFromSubtitle(List<DicModel> subtitleList) {
        return subtitleList.stream()
                .map(DicModel::getId)
                .collect(Collectors.toList());
    }
}
