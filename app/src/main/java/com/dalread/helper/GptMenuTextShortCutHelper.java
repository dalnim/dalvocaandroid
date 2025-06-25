package com.dalread.helper;

import android.content.Context;

import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.model.GptTextShortCut;

import java.util.List;

public class GptMenuTextShortCutHelper {
    private List<String> list;
    private Context context;
    private SharedPreferencesDB sharedPreferences;
    private SubDatabase subDatabase;
    public GptMenuTextShortCutHelper(Context context, SubDatabase subDatabase) {
        this.context = context;
        this.subDatabase = subDatabase;
        this.sharedPreferences = SharedPreferencesDB.getInstance(context);
    }

//    private void makeDefaultList() {
//        GptMenuTextShortCutListFactoryUtil.ListFactory listFactory = GptMenuTextShortCutListFactoryUtil.getListFactory(true);
//        list = listFactory.createList();
//        saveList(false);
//    }


    public List<GptTextShortCut> getList() {
        return subDatabase.getGptShortCutList();
    }

    public boolean delete(int id) {
        return subDatabase.deleteShortCut(id);
    }
    public boolean add(GptTextShortCut item) {
        return subDatabase.insertGptShortCut(item);
    }
    public void save(GptTextShortCut item) {
        subDatabase.updateGptShortCut(item);
//        Type type = new TypeToken<List<String>>(){}.getType();
//        String string;
//
//        if (list.size() > 1) {
//            string = hasClipboardText ? sharedPreferences.toJson(list.subList(1, list.size()), type)
//                    : sharedPreferences.toJson(list, type);
//        } else {
//            string = "[]";
//        }
//
//        sharedPreferences.setChatGptWebTextShortCut(string);
    }
}
