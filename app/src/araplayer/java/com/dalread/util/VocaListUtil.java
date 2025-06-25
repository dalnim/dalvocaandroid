package com.dalread.util;

import com.dalread.database.sqlite.SubDatabase;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.VocaBookmark;
import com.dalread.model.VocaKnowAndBookmarkList;
import com.dalread.model.VocaKnowAndKnowpronounce;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VocaListUtil extends BaseVocaList {
    //예전 이름 getAllWordListFromSentence
    public static List<IVocaFullPlayTTSItem> getAllWordListOfFromDB(String sentence, SubDatabase subDatabase) {
        String wordListWithComma = StringUtils.splitSentenceIntoWordListWithComma(sentence);
        return subDatabase.getAllWordListWordListWithComma(wordListWithComma);
    }

    public static List<IVocaFullPlayTTSItem> getAllWordListOfFromDB(List<? extends IVocaBasicItem> item, SubDatabase subDatabase) {
        String wordListWithComma = StringUtils.splitSentenceIntoWordListWithComma(BaseVocaList.getVocaStringList(item).toString());
        return subDatabase.getAllWordListWordListWithComma(wordListWithComma);
    }

    public static VocaKnowAndBookmarkList getVocaKnowAndBookmarkListToSync(VocaKnowAndBookmarkList vocaKnowAndBookmarkList) {
        if (!AppFlavorUtil.isAraHangulApp()) {
            return vocaKnowAndBookmarkList;
        }

        //아라 한글일때는 한글에 해당되는 ID만 가져온다.
        int vocaHangulStartSentenceId =1313;
        int vocaHangulEndSentenceId =1486;
        VocaKnowAndBookmarkList result = new VocaKnowAndBookmarkList();
        List<VocaBookmark> vocaBookmarkList = new ArrayList<>();
        List<VocaKnowAndKnowpronounce> vocaKnowKnowpronounceList = new ArrayList<>();
        for(VocaBookmark item : vocaKnowAndBookmarkList.getVocaBookmarkList()) {
            if (item.getVocaType() == Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE) {
                if ((item.getVIVocaId() >= vocaHangulStartSentenceId) && (item.getVocaId() <= vocaHangulEndSentenceId)) {
                    vocaBookmarkList.add(item);
                }
            }
        }
        for(VocaKnowAndKnowpronounce item : vocaKnowAndBookmarkList.getVocaKnowKnowpronounceList()) {
            if (item.getVocaType() == Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE) {
                if ((item.getVIVocaId() >= vocaHangulStartSentenceId) && (item.getVocaId() <= vocaHangulEndSentenceId)) {
                    vocaKnowKnowpronounceList.add(item);
                }
            }
        }
        result.setVocaBookmarkList(vocaBookmarkList);
        result.setVocaKnowKnowpronounceList(vocaKnowKnowpronounceList);
        return result;
    }

    public static List<IVocaFullPlayTTSItem> convertListDicModelToVocaFullPlayerTtsItemList(List<DicModel> list) {
        return  list.stream()
                .map(dicModel -> (IVocaFullPlayTTSItem) dicModel)
                .collect(Collectors.toList());
    }
}
