package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dalread.base.EnumLanguage;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.model.VocaInBook;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.Voca;

import java.util.Collections;
import java.util.List;

public class AraConvPracticeHandWritingHangulActivity extends PracticeHandWritingHangulActivity {
    private SubDatabase subDatabase;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, AraConvPracticeHandWritingHangulActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getData() {
        initSubDatabase();
        List<VocaInBook> vocasTemp = subDatabase.getVocasToPracticeAlpahbetForWriting();
        int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
        for (VocaInBook voca : vocasTemp) {
            voca.setPath(Voca.getOutputRecordingFileName(
                    studyLang,
                    voca.getVocaType(),
                    voca.getVocaId(),
                    getUserID()
            ));
            vocaList.add(voca);
        }
        //        playTTS.checkVersionAndDownload(new ArrayList<>(vocas));
        Collections.shuffle(vocaList);
        countdownToStartPractice();
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
