package com.dalread.util;

import java.util.Collection;
/**
 * Created by Dalnim on 2/17/2022.
 */
public class BaseCollectionUtil {

    //Dalnim add
    public static int getDefaultIndexIfOutOfIndex(Collection<?> list, int index) {
        if (index >= list.size())
            index = list.size() - 1;

        if (index <= 0)
            index = 0;

        return index;
    }

    //Dalnim add
    public static int getFirstIndexIfOutOfIndex(Collection<?> list, int index) {
        if (index >= list.size())
            index = 0;

        return index;
    }

//    //Dalnim add
//    public static <T extends  Number> T getDefaultIndexIfOutOfIndex(List<?> list, T index) {
//        if (index > list.size())
//            index = list.size() - 1;
//
//        if (index <= 0)
//            index = 0;
//
//        return index;
//    }

    //Dalnim add
    public static boolean isIndexInsideList(Collection<?> list, int index) {
        boolean blnResult = false;
        if (!Utils.isEmpty(list)) {
            if ((0 <= index) && (index < list.size())) {
                blnResult = true;
            }
        }
        return blnResult;
    }

    public static boolean isSubtitleIndexInsideList(Collection<?> list, long index) {
        if (list == null)
            return false;
        //Subtitle index can have -1. (It's the playing time is before 1st subtitle, AraPlayer shows empty subtitle for this case
        if ((index >= -1) && (index < list.size()))
            return true;

        return false;
    }

    public static int decreaseIndexInList(Collection<?> list, int inputIndex) {
        if (Utils.isEmpty(list))
            return inputIndex;
        int index = inputIndex - 1;
        return getDefaultIndexIfOutOfIndex(list, index);
    }
}
