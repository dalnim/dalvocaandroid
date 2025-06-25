package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.model.WordListType;

public class WordListMenuDialog extends ConvCommonMenuDialog implements View.OnClickListener {

    public WordListMenuDialog(@NonNull Context context, com.dalread.listener.OnClickListener listener, WordListType wordListType) {
        super(context, listener, wordListType);

    }
    protected void showOrHideMenus() {
        super.showOrHideMenus();
        binding.llWordList.setVisibility(View.GONE);
        binding.llMakeRolePlaying.setVisibility(View.GONE);
    }


}
