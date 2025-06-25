package com.dalread.activity;

import android.os.Bundle;

import com.dalread.base.BasePlayListByTtsActivity;
import com.dalread.interfaces.IVocaCoreItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.WordListType;
import com.dalread.util.ConversationBookUtil;
import com.dalread.util.DLog;

import java.util.ArrayList;
import java.util.List;

public class PlayListByTtsActivity extends BasePlayListByTtsActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected ArrayList<IVocaFullPlayTTSItem> getFullItems(List<IVocaCoreItem> vocaCoreItemList) {
        List<IVocaFullPlayTTSItem> iVocaFullPlayTTSItemList = new ArrayList<>();
        handleConversationBook();
        if ((new ConversationBookUtil()).isConversationBook(wordListType, bookId)) {
            if (wordListType == WordListType.BOOK) {
                iVocaFullPlayTTSItemList = subDatabase.getVocaListByBookIdInVocaBook(bookId);
            } else if (wordListType == WordListType.USER_VOCA_BOOK_LOCAL) {
                iVocaFullPlayTTSItemList = subDatabase.getVocaListByBookIdInUserVocaBookLocal(bookId);
            }
        } else {
            iVocaFullPlayTTSItemList = subDatabase.getPlayTTSVocaListByVocaIdList(vocaCoreItemList);
        }
        return getFullItemsByInputOrder(iVocaFullPlayTTSItemList, vocaCoreItemList);
    }

//    private ArrayList<IVocaFullPlayTTSItem> getFullItemsByInputOrder(List<IVocaFullPlayTTSItem> iVocaFullPlayTTSItemList, List<IVocaCoreItem> vocaCoreItemList) {
//        Map<String, IVocaFullPlayTTSItem> mapVoca = iVocaFullPlayTTSItemList.stream()
//                .collect(Collectors.toMap(e -> e.getVIVocaTypeId(), e -> e, (p1, p2) -> p1));
//        List<IVocaFullPlayTTSItem> resultList = new ArrayList<>();
//        for(IVocaCoreItem item : vocaCoreItemList) {
//            if (mapVoca.containsKey(item.getVIVocaTypeId())) {
//                resultList.add(mapVoca.get(item.getVIVocaTypeId()));
//            }
//        }
//        return (ArrayList<IVocaFullPlayTTSItem>) resultList;
//    }

    @Override
    public void onStop() {
        DLog.d(getLogTag(), "onStop");
        super.onStop();
        if (!sharedPreferences.getKeepPlayingOnBackgroundMode()) {
            stopList();
        }
    }

    private void handleConversationBook() {
//        bookId = getIntent().getIntExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, -1);
//        wordListType = (WordListType) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE);
        isConversationBook = (new ConversationBookUtil()).isConversationBook(wordListType, bookId);
        upateVisibilityCheckItem(false);
    }
}
