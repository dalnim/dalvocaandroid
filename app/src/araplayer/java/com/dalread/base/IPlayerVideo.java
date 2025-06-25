package com.dalread.base;

import com.dalread.database.sqlite.model.DicModel;

public interface IPlayerVideo {
    DicModel getDicModel(int index);

//    long getStartTimeWithoutBeforeTime(DicModel item); //Dalnim add : When AraPlayer repeats dialogs, then need to get getPlayBeforeSubtitle/getPlayAfterSubtitle time. But just finding subtitles to mvoe doesn't need them
//    long getEndTimeWithoutAfterTime(DicModel item); //Dalnim add
    long getStartTime(DicModel item);
    long getEndTime(DicModel item);
}
