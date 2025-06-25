package com.dalread.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dalread.base.EnumLanguage;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.model.VocaInBook;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.Voca;

import java.util.List;

public class AraConvStudyHandWritingHangulActivity extends StudyHandWritingHangulActivity {
    private SubDatabase subDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getData(String vocaBooksIdListByComma) {
        initSubDatabase();
        List<VocaInBook> vocasTemp = subDatabase.getVocasFromAllVocaBook(vocaBooksIdListByComma);
        int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
        for (VocaInBook voca : vocasTemp) {
            voca.setPath(Voca.getOutputRecordingFileName(
                    studyLang,
                    voca.getVocaType(),
                    voca.getVocaId(),
                    getUserID()
            ));
            vocas.add(voca);
        }
        updateLayout();
//        playTTS.checkVersionAndDownload(new ArrayList<>(vocas));

    }

    private void initSubDatabase() {
        if (subDatabase != null) {
            subDatabase.close();
        }
        subDatabase = null;
        String destPathWithFileName = BaseStorageUtil.getAraConvDBPathWithFileName(this);
        subDatabase = SubDatabase.getInstance(this, destPathWithFileName);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subDatabase != null) {
            subDatabase.close();
            subDatabase = null;
        }
    }
}
