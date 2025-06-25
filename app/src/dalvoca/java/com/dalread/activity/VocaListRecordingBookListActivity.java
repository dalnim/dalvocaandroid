package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dalread.base.BaseVocaListRecordingBookListActivity;

public class VocaListRecordingBookListActivity extends BaseVocaListRecordingBookListActivity{
    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, VocaListRecordingBookListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}
