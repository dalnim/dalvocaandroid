package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dalread.databinding.ActivityGptWebMenuEditBinding;
import com.dalread.util.Constant;

import java.util.Map;
//이걸 쓸지 아님 ChatGptMenuEditDialog를 쓸지 고민중. 이건 나중에 확실할때 지울것
public class GptWebMenuEditActivity extends BaseConvActivity {
    Map.Entry<String, String> menu;


    private ActivityGptWebMenuEditBinding binding;
    public static Intent createIntentWithBookId(Context context, int bookId) {
        Intent intent = new Intent(context, GptWebMenuEditActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, bookId);
        return intent;
    }
    protected View getContentView() {
        binding = ActivityGptWebMenuEditBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected int getContentViewId() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



}


