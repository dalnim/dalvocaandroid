package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;

import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.util.Voca;

public class ChooseStudyLanguageActivity extends ChooseLanguageActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setViewTitle() {
        this.toolbar.setTitle(R.string.title_view_choose_study_langauge);
    }

    @Override
    protected void getLanguageList() {
        languages = EnumLanguage.getLanguageList();
    }

    @Override
    protected void openNewScreenAfterChooseLanguage() {
        Intent i = new Intent(this, ChooseMenuLanguageActivity.class);
        startActivity(i);
    }

    @Override
    protected void saveChosenLanguage(EnumLanguage enumLanguage) {
        Voca.setStudyLanguage(sharedPreferences, enumLanguage);
    }
}
