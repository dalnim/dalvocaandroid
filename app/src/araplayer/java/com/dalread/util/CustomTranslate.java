package com.dalread.util;

import android.content.Context;

import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.listener.OnClickListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class CustomTranslate {

    private Context context;
    private TranslatorOptions options;
    private Translator translator;
    private SharedPreferencesDB sharedPreferences;

    public CustomTranslate(Context context) {
        this.context = context;
        sharedPreferences = SharedPreferencesDB.getInstance(context);
        EnumLanguage studyLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage());
        EnumLanguage tongueLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage());
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(studyLanguage.getFormatOs())
                        .setTargetLanguage(tongueLanguage.getFormatOs())
                        .build();
        translator = Translation.getClient(options);
    }

    public void translateText(String text, OnClickListener listener) {
        download(text, listener);
    }

    private void download(String text, OnClickListener listener) {
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener((OnSuccessListener) o -> translate(text, listener))
                .addOnFailureListener(e -> {
                    listener.onClick(null, null);
                });
    }

    private void translate(String text, OnClickListener listener) {
        translator.translate(text)
                .addOnSuccessListener((OnSuccessListener) o -> {
                    listener.onClick(null, o);
                })
                .addOnFailureListener(e -> {
                    listener.onClick(null, null);
                });
    }
}
