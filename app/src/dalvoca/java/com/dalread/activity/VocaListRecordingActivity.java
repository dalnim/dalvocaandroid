package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dalread.base.BaseVocaListRecordingActivity;
import com.dalread.model.VocaBook;
import com.dalread.util.Constant;

public class VocaListRecordingActivity extends BaseVocaListRecordingActivity {
    public static Intent createIntent(Context context, VocaBook book) {
        Intent intent = new Intent(context, VocaListRecordingActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, book);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getData(final boolean isLoadMore) {
        super.getData(isLoadMore);
    }
}
