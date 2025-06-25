package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;

import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.component.Toolbar;
import com.dalread.util.BaseVoca;

public class ChooseMotherLanguageActivity extends ChooseLanguageActivity {

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setViewTitle() {
        this.binding.header.setTitle(R.string.title_view_choose_mother_langauge);
    }


    @Override
    protected void getLanguageList() {
        languages = EnumLanguage.getLanguageList();
    }

    @Override
    protected void openNewScreenAfterChooseLanguage() {
        Intent i = new Intent(this, MainHomeActivity.class);
        startActivity(i);
        finishAffinity();
    }

    @Override
    protected void saveChosenLanguage(EnumLanguage enumLanguage) {
        BaseVoca.setMenuLanguage(sharedPreferences, enumLanguage);
        BaseVoca.setMotherTongueLanguage(sharedPreferences, enumLanguage);

        //TODO : Don't delete thi code until we're sure we don't need it.
//        if (EnumLanguage.checkSupportTranslation(enumLanguage.getFormatApi())) {
////            sharedPreferences.setMenuLanguage(enumLanguage.getFormatApi());
//            Voca.setMenuLanguage(sharedPreferences, enumLanguage);
//            Voca.setMotherTongueLanguage(sharedPreferences, enumLanguage);
//        } else {
//            sharedPreferences.setMenuLanguage(EnumLanguage.ENGLISH.getFormatApi());
//        }
    }
}
