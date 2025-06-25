package com.dalread.util;

import android.app.Activity;
import android.content.Intent;

import com.dalread.activity.PlayListByTtsActivity;
import com.dalread.composition.PlayTTS;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.VocaTypeId;
import com.dalread.model.WordListType;

import java.io.Serializable;
import java.util.List;

public class PlaylistUtil {
    public static void openPlaylistByBookId(Activity activity, List<? extends IVocaFullPlayTTSItem> data, WordListType wordListType, int bookId, PlayTTS playTTS, String subDatabasePath) {
        Intent intent = new Intent(activity, PlayListByTtsActivity.class);
        List<VocaTypeId> vocaTypeIdList = BaseVocaList.convertToVocaTypeIdList(data);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, (Serializable) vocaTypeIdList);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, wordListType);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, bookId);
        intent.putExtra(Constant.BUNDLE.KEY_SUB_DATABASE_PATH, subDatabasePath);
        // intent.putExtra(Constant.BUNDLE.KEY_VOCA_KNOW_ACTIVITY, (Serializable) vocaKnowActivity);
        if (playTTS != null) {
            playTTS.stopPlayVoca();
        }
//        playTTS.setIncludeMeaning();
        activity.startActivity(intent);
    }

    public static void openPlaylistByVocaList(Activity activity, List<? extends IVocaFullPlayTTSItem> data, WordListType wordListType, PlayTTS playTTS, String subDatabasePath) {
        Intent intent = new Intent(activity, PlayListByTtsActivity.class);
        List<VocaTypeId> vocaTypeIdList = BaseVocaList.convertToVocaTypeIdList(data);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, (Serializable) vocaTypeIdList);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, wordListType);
        intent.putExtra(Constant.BUNDLE.KEY_SUB_DATABASE_PATH, subDatabasePath);
        // intent.putExtra(Constant.BUNDLE.KEY_VOCA_KNOW_ACTIVITY, (Serializable) vocaKnowActivity);
        if (playTTS != null) {
            playTTS.stopPlayVoca();
        }
//        playTTS.setIncludeMeaning();
        activity.startActivity(intent);
    }
}
