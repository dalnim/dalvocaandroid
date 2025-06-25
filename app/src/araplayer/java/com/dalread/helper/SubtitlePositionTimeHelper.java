package com.dalread.helper;

import com.dalread.database.sqlite.model.DicModel;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.VideoModel;
import com.dalread.util.DLog;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.List;

//자막들의 시간에 관련된 Helper
public class SubtitlePositionTimeHelper {
    private PlayerFileModel playerFileModel;
    private List<DicModel> subtitleListTotal; //아직은 안쓰고 있음. 나중에 여기껄 쓸려고.
    private List<DicModel> subtitleList;
    public SubtitlePositionTimeHelper(PlayerFileModel playerFileModel) {
        this.playerFileModel = playerFileModel;
    }

    //Dalnim : update logic to use binary search and if it's empty dialog, then return previous dialog and set mIsOnEmptyDialog to FALSE
    //이진 검색, 바이너리 검색, binary search
    protected SubtitleIndexResult getSubtitleIndexFromCurrentTime(ArrayList<DicModel> subtitleList, long position) {
        int subtitleIndexFound = -1;
        boolean mIsOnEmptyDialog = false;
        if (Utils.isEmpty(subtitleList)) {
            mIsOnEmptyDialog = true;
        } else {
            DLog.d("dalnim", "getSubtitleIndexFromCurrentTime position : " + position);
            int subtitleIndexPrevious = -1;
            int midIndex = 0;
            int firstIndex = 0;
            int lastIndex = subtitleList.size() - 1;
            while (lastIndex >= firstIndex) {
                midIndex = (lastIndex + firstIndex) / 2;
                DicModel dicModel = subtitleList.get(midIndex);
                long startPositionInSubtitleDicModel = getMinTime(dicModel); //Seach surbitlte Index from Playing times doesn't need to use Play Before/After a time
                long endPositionInSubtitleDicModel = getMaxTime(dicModel);
                if ((startPositionInSubtitleDicModel <= position) && (position < endPositionInSubtitleDicModel)) {
                    subtitleIndexFound = midIndex;
                    break;
                }
                if (position <= startPositionInSubtitleDicModel) {
                    lastIndex = midIndex - 1;
                    subtitleIndexPrevious = lastIndex;
                } else {
                    firstIndex = midIndex + 1;
                    subtitleIndexPrevious = midIndex;
                }

                //Dalnim : Don't delete this, I need to dubeg this logic many times.
                if (Utils.isDebug()) {
                    boolean isSafe = true;
                    if ((firstIndex < 0) || (firstIndex >= subtitleList.size())) {
//                    DLog.d(getLogTag(), "firstIndex is out of bound, firstIndex = " + firstIndex);
                        isSafe = false;
                    }
                    if ((midIndex < 0) || (midIndex >= subtitleList.size())) {
//                    DLog.d(getLogTag(), "midIndex is out of bound, midIndex = " + midIndex);
                        isSafe = false;
                    }
                    if ((lastIndex < 0) || (lastIndex >= subtitleList.size())) {
//                    DLog.d(getLogTag(), "lastIndex is out of bound, lastIndex = " + lastIndex);
                        isSafe = false;
                    }
                    if ((subtitleIndexPrevious < 0) || (subtitleIndexPrevious >= subtitleList.size())) {
//                    DLog.d(getLogTag(), "subtitleIndexPrevious is out of bound, subtitleIndexPrevious = " + subtitleIndexPrevious);
                        isSafe = false;
                    }

                    if (isSafe) {
                        DicModel dicModel1 = subtitleList.get(firstIndex);
                        DicModel dicModel2 = subtitleList.get(midIndex);
                        DicModel dicModel3 = subtitleList.get(lastIndex);
                        DicModel dicModel4 = subtitleList.get(subtitleIndexPrevious);
                        DicModel dicModel5 = subtitleList.get(subtitleIndexPrevious);
                    } else {
//                    DLog.d(getLogTag(), "out of bound bug");
                    }
                }
            }

            if (subtitleIndexFound == -1) {
                mIsOnEmptyDialog = true;
                // Dalnim : If it's empty dialog, use subtitleIndexPrevious. And if it's next of last dialog, then use last dialog otherwise it goes first dialog when I swipe to right at the last empty dialog.
                subtitleIndexFound = subtitleIndexPrevious >= (subtitleList.size() - 1) ? subtitleList.size() - 1 : subtitleIndexPrevious;
            } else {
                mIsOnEmptyDialog = false;
            }
            DLog.d("dalnim", "getSubtitleIndexFromCurrentTime subtitleIndexFound : " + subtitleIndexFound);
        }
        return new SubtitleIndexResult(mIsOnEmptyDialog, subtitleIndexFound);

    }

    private long getMinTime(DicModel dicModel) {
//        minSub = getStartTime(dicModel);
//        long minSub1 = Voca.getStartTime(dicModel, activity.playerFileModel.getVideoModel());
        return (long) getStartTime(dicModel);
    }

    private long getMaxTime(DicModel dicModel) {
//        maxSub = getEndTime(dicModel);
//        long maxSub1 = Voca.getEndTime(dicModel, activity.playerFileModel.getVideoModel());
        return (long) getEndTime(dicModel);
    }

    private long getStartTime(DicModel item) {
        if (item == null)
            return 0;
        return Voca.getSubtitleTimeWithDelaySubtitleTime(item.getStartTime(), playerFileModel.getVideoModel());
    }
    //    @Override
    private long getEndTime(DicModel item) {
        if (item == null)
            return 0;
        return Voca.getSubtitleTimeWithDelaySubtitleTime(item.getEndTime(), playerFileModel.getVideoModel());
    }

    private long getSubtitleTimeWithDelaySubtitleTime(long time, VideoModel videoModel) {
        int extraTime = videoModel.getDelaySubtitles();
        return getSubtitleTime(videoModel.getDuration(), time, extraTime);
    }

    private long getSubtitleTime(long duration, long time, int extraValue) {
        long value = 0;
        value += time + extraValue;
        if (value > duration) {
            value = time;
        }
        if (value < 0) {
            value = time < 0 ? 0 : time;
        }
        return value;
    }
}

class SubtitleIndexResult {
    public final boolean mIsOnEmptyDialog;
    public final int subtitleIndexFound;

    public SubtitleIndexResult(boolean mIsOnEmptyDialog, int subtitleIndexFound) {
        this.mIsOnEmptyDialog = mIsOnEmptyDialog;
        this.subtitleIndexFound = subtitleIndexFound;
    }
}
