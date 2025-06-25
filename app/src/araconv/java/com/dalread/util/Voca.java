package com.dalread.util;

import android.content.Context;

import com.dalread.base.EnumLanguage;
import com.dalread.composition.VocaKnowActivity;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.interfaces.IVocaFullPlayTTSItem;

import java.io.File;
import java.util.List;

public class Voca extends BaseVoca {
    public static File getVoiceFolderOnLocal(Context context) {
        return getVoiceFolderInApp(context);
    }

    //Same as updateMyVoiceFileInLocal in MainHomeActivity
    //Tying to use KProgressHUD to show progress but failed.
    public static int updateMyVoiceFileInLocal(Context context, SubDatabase subDatabase, VocaKnowActivity vocaKnowActivity) {
        List<IVocaFullPlayTTSItem> vocaList = subDatabase.getAllSentenceVocaList();
//        KProgressHUD progressHUD = new KProgressHUD(context);
        int cntOfVoiceFiles = 0;
        if (!Utils.isEmpty(vocaList)) {
//            progressHUD.setMaxProgress(vocaList.size());
            int studyLangCode = EnumLanguage.findByFormatApi(SharedPreferencesDB.getInstance(context).getStudyLanguage()).getIdApi();
            int uid = SharedPreferencesDB.getInstance(context).getRealUid();
            for (int i = 0; i < vocaList.size(); i++) {
//                progressHUD.setProgress(i+1);
                IVocaFullPlayTTSItem item = vocaList.get(i);
                String voiceFileName = BaseVoca.getOutputRecordingFileName(studyLangCode, item.getVIVocaType(), item.getVIVocaId(), uid);
                if ((voiceFileName != null) && (BaseFileUtil.isVoiceFileExistInVoiceFolder(context, voiceFileName))) {
                    vocaKnowActivity.updateHasVoiceFile(item, Constant.INT_BOOLEAN.TRUE);
//                    item.setVIVoiceFile(Constant.INT_BOOLEAN.TRUE);
                    cntOfVoiceFiles++;
                } else {
                    vocaKnowActivity.updateHasVoiceFile(item, Constant.INT_BOOLEAN.FASLE);
                }
            }
        }
//        progressHUD.dismiss();
        return cntOfVoiceFiles;
    }

//    public static void updateVocaKnowAndVocaKnowpronounceAndBookmark(SubDatabase subDatabase, VocaKnowAndBookmarkList vocaKnowAndBookmarkList) {
//        subDatabase.resetVocaKnowAndBookmark();
//        subDatabase.updateVocaKnowAndVocaKnowpronounce(vocaKnowAndBookmarkList.getVocaKnowKnowpronounceList());
//        subDatabase.updateVocaBookmark(vocaKnowAndBookmarkList.getVocaBookmarkList());
//    }
}
