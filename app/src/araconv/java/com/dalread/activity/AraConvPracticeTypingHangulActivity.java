package com.dalread.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dalread.base.EnumLanguage;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.dialog.AlertDialog;
import com.dalread.model.VocaInBook;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.Constant;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import java.util.Collections;
import java.util.List;

public class AraConvPracticeTypingHangulActivity extends PracticeTypingHangulActivity {
    private SubDatabase subDatabase;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, AraConvPracticeTypingHangulActivity.class);
        return intent;
    }

    public static Intent createIntentByBookId(Context context, int bookId) {
        Intent intent = new Intent(context, AraConvPracticeTypingHangulActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE);
//        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.BOOK);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, bookId);
//        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.VOCA_TYPE_ID_LIST);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onHeaderTextRightClick() {
        String info = voca.getVIMeaningDetailed(LanguageUtil.getMotherTongueLanguage(this));
        if (!Utils.isEmpty(info)) {
            if (finishPracticeTimer != null) {
                finishPracticeTimer.cancel();
            }

            AlertDialog alertDialog = new AlertDialog(this);
            alertDialog.show(info, null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finishPracticeTimer.start();
                }
            });
        }
    }

    @Override
    protected void getData() {
        initSubDatabase();
        int bookId = getIntent().getIntExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, -1);
        List<VocaInBook> vocasTemp = subDatabase.getVocasToPracticeAlpahbetForWriting(bookId);
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
