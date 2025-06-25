package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dalread.database.sqlite.SubDatabase;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.Constant;

public class AraConvWordbooksHangulSubActivity extends WordbooksHangulSubActivity {
    private SubDatabase subDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getData(int categoryId) {
        initSubDatabase();
        vocaBooks = subDatabase.getServerVocaBookListByCategoryForHangulWriting(categoryId);


    }

    @Override
    protected void openStudyWritingScreen(int pos) {
        if (!vocaBooks.isEmpty() && pos < vocaBooks.size()) {
            Intent intent = new Intent(this, AraConvStudyHandWritingHangulActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, vocaBooks.get(pos));
            openNewScreen(intent);
        }
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
