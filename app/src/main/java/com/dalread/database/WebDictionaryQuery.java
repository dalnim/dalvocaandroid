package com.dalread.database;

import com.dalread.base.EnumWebDictionary;
import com.dalread.model.WebDictionaryModel;
import com.dalread.util.Constant;
import com.dalread.util.DLog;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class WebDictionaryQuery {

    private static final String TAG = "WebDictionaryQuery";

    public static int createIndex(Realm realm) {
        Number currentIdNum = realm.where(WebDictionaryModel.class).max(Constant.PLAYER.DATABASE.FIELD.INDEX);
        int nextId;
        if(currentIdNum == null) {
            nextId = 0;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }

    public static void add(Realm realm, WebDictionaryModel item) {
        DLog.d(TAG, "add - item=" + item.toString());
        realm.beginTransaction();
        realm.insertOrUpdate(item);
        realm.commitTransaction();
    }

    public static List<WebDictionaryModel> getAll(Realm realm) {
        final List<WebDictionaryModel> list = realm.where(WebDictionaryModel.class)
                .sort(Constant.PLAYER.DATABASE.FIELD.INDEX)
                .findAll();
        if (list != null && !list.isEmpty())
            return realm.copyFromRealm(list);
        return new ArrayList<>();
    }

    public static List<WebDictionaryModel> getAll(Realm realm, int studyLanguage, int motherTongue) {
        final List<WebDictionaryModel> list = realm.where(WebDictionaryModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.STUDY_LANGUAGE, studyLanguage)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.MOTHER_TONGUE, motherTongue)
                .sort(Constant.PLAYER.DATABASE.FIELD.INDEX)
                .findAll();
        if (list != null && !list.isEmpty())
            return realm.copyFromRealm(list);
        return new ArrayList<>();
    }

    public static List<WebDictionaryModel> getFourItems(Realm realm, int studyLanguage, int motherTongue) {
        final List<WebDictionaryModel> list = getAll(realm, studyLanguage, motherTongue);
        if (list.size() > Constant.PLAYER.WEB_DICTIONARY.TAB_MAX) {
            return list.subList(0, Constant.PLAYER.WEB_DICTIONARY.TAB_MAX);
        }
        return list;
    }

    public static WebDictionaryModel getFirst(Realm realm, int studyLanguage, int motherTongue) {
        final List<WebDictionaryModel> list = getAll(realm, studyLanguage, motherTongue);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static void update(Realm realm, WebDictionaryModel item) {
        add(realm, item);
    }

    public static void deleteById(Realm realm, long id) {
        DLog.d(TAG, "deleteById - id=" + id);
        realm.executeTransaction(realm1 ->
                realm1.where(WebDictionaryModel.class).equalTo(Constant.PLAYER.DATABASE.FIELD.ID, id).findFirst().deleteFromRealm());
    }

    public static void initDefault(Realm realm, EnumWebDictionary[] list) {
        if (!getAll(realm).isEmpty()) return;
        if (list == null || list.length <= 0) return;
        int count = 0;
        for (EnumWebDictionary web : list) {
            add(realm, new WebDictionaryModel(System.currentTimeMillis() + count, web.getTitle(), web.getUrl(), web.getStudyLang(), web.getMotherTongue(), count));
            count++;
        }
    }
}
