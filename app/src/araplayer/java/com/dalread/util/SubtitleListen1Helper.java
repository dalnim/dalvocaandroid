package com.dalread.util;

import com.dalread.model.ListenComprehensionModel;

import java.util.ArrayList;
import java.util.List;

//아직 사용하는거 아님. 나중에 이렇게 뺄려고 생각중.
public class SubtitleListen1Helper extends SubtitleRepeatHelper{
    private List<ListenComprehensionModel> listenComprehension1ModelList; //For Listen Comprehension 1, 자막 표시 방법 및 순서를 담아둠 (자막 숨기기, 자막 보이기등).
    private int listComprehensionCurrentIndex = 0;
    private int listComprehensionModelIndex = 0; //자막 표시 방법 및 순서의 인덱스
    private int listComprehension1PlaySubtitlesAtOnce; // 듣기 연습 1 옵션의 한번에 반복할 자막수 (예, 3) 자막 그룹이 됨.
    private int listComprehension1PlaySubtitlesAtOnceCount = 1; //"한번에 반복할 자막수"그룹을 현재 몇개째인지.(예, 5이면, 3개 자막씩 반복해서 현재 5번째 자막그룹(3개반복)이란 뜻), "한번에 반복할 자막수"그룹내의 현재 플레이중인 인덱스는 comprehensionRepeatedCount가 나타낸다.

    private int ccRepeatingCount = 0;

    private List<Integer> listComprehension1SubtitleList = new ArrayList<>(); //듣기 연습시 자막들의 ID를 담아둠. (듣기 연습을 시작한 자막부터 담음)
    private int comprehension1SubtitleIndexInList = 0; //listComprehension1SubtitleList에서의 index (영화 중간에 듣기 연습을 하면 처음 이 값은 0이 아니게 됨), 이건 listComprehension1SubtitleList의 값에서 계속 증가됨.
    private int comprehension1SubtitleIndexInListFirstIndex = 0; //listComprehension1SubtitleList에서의 index (영화 중간에 듣기 연습을 하면 처음 이 값은 0이 아니게 됨), 이건 자막 그룹내에서 맨처음 인덱스가 유지됨.


    //이건 아직 안쓰고 PlayerFragment에껄 쓰고 있음.
    //듣기 연습 1에서 1번 반복이 끝난후에 반복내 모든 자막이 아는거면 다음 반복 그룹으로 넘어갈려고
    private boolean isAllSubtitlesKnowInListenComp1RepeatGroup() {
        int subtitleIndexLocal = getStartSubtitleIndexInListen1();
        boolean isAllSubtitlesKnow = true;
        for (Integer i = 0; i < listComprehension1PlaySubtitlesAtOnce; i++) {
//            DicModel dicModel = getDicModel(subtitleIndexLocal + i);
//            if (dicModel != null) {
//                if (BaseVocaKnow.isUnknownAndLess(dicModel)) {
//                    isAllSubtitlesKnow = false;
//                    break;
//                }
//            }
        }
        return isAllSubtitlesKnow;
    }


    private Integer getStartSubtitleIndexInListen1() {
        return listComprehension1SubtitleList.get(comprehension1SubtitleIndexInListFirstIndex);
    }

    private Integer getEndSubtitleIndexInListen1() {
        int index = comprehension1SubtitleIndexInListFirstIndex + listComprehension1PlaySubtitlesAtOnce - 1;
        //듣기 연습1은 listComprehension1PlaySubtitlesAtOnce횟수만큼 반복할때, 마지막 라운드에서는 남은 자막수가 listComprehension1PlaySubtitlesAtOnce보다 작을수가 있으므로 싸이즈 체크를 해줘야한다.
        if (index > (listComprehension1SubtitleList.size() - 1)) {
            index = listComprehension1SubtitleList.size() - 1;
        }
        return listComprehension1SubtitleList.get(index);
    }

}
