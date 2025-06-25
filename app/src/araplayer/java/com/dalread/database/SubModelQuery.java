package com.dalread.database;

import com.dalread.model.SubModel;
import com.dalread.util.Constant;
import com.dalread.util.DLog;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class SubModelQuery {

    private static final String TAG = "VideoModelQuery";

    public static int createId(Realm realm) {
        Number currentIdNum = realm.where(SubModel.class).max(Constant.PLAYER.DATABASE.FIELD.ID);
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }

    public static void add(Realm realm, SubModel subModel) {
        DLog.d(TAG, "add - subModel=" + subModel.toString());
        realm.beginTransaction();
        realm.insertOrUpdate(subModel);
        realm.commitTransaction();
    }

    public static List<SubModel> getAll(Realm realm) {
        final List<SubModel> list = realm.where(SubModel.class).findAll();
        if (list != null)
            return realm.copyFromRealm(list);
        return null;
    }

    public static List<SubModel> getByPath(Realm realm, String path) {
        final List<SubModel> list = realm
                .where(SubModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.PATH, path)
                .sort(Constant.PLAYER.DATABASE.FIELD.ID)
                .findAll();
        if (list != null)
            return realm.copyFromRealm(list);
        return null;
    }

    public static List<SubModel> getByLanguage(Realm realm, String path, int language) {
        DLog.d(TAG, "getByLanguage - path=" + path + " - language=" + language);
        final List<SubModel> list = realm
                .where(SubModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.PATH, path)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.LANGUAGE, language)
                .sort(Constant.PLAYER.DATABASE.FIELD.ID)
                .findAll();
        if (list != null)
            return realm.copyFromRealm(list);
        return null;
    }

    public static void deleteByPath(Realm realm, String path) {
        DLog.d(TAG, "deleteByPath - path=" + path);
        realm.executeTransaction(realm1 -> {
            final RealmResults<SubModel> list = realm1
                    .where(SubModel.class)
                    .equalTo(Constant.PLAYER.DATABASE.FIELD.PATH, path)
                    .findAll();
            if (list != null) {
                list.deleteAllFromRealm();
            }
        });
    }

    public static boolean checkPath(Realm realm, String path) {
        return realm
                .where(SubModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.PATH, path)
                .findFirst() != null;
    }

    public static void update(Realm realm, SubModel subModel) {
        add(realm, subModel);
    }
}
