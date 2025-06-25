package com.dalread.util;

import com.dalread.database.sqlite.model.DicModel;

import java.util.List;

//DicModel을 사용하는 로직들?
public class SubtitlePositionTimeUtil {
    //Dalnim added
    public static void resetSubtitlePosition(List<DicModel> dicModels) {
        int count = 1;
        for (DicModel dic : dicModels) {
            dic.setIndex(count);
            dic.setPosition(count - 1);
            count++;
        }
    }
}