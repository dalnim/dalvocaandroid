package com.dalread.database;

import com.dalread.model.DownloadModel;
import com.dalread.util.Constant;
import com.dalread.util.DLog;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class DownloadModelQuery {

    private static final String TAG = "DownloadModelQuery";

    public static int createId(Realm realm) {
        Number currentIdNum = realm.where(DownloadModel.class).max(Constant.PLAYER.DATABASE.FIELD.ID);
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }

    public static void add(Realm realm, DownloadModel model) {
        DLog.d(TAG, "add - ServerDownloadModel=" + model.toString());
        realm.beginTransaction();
        realm.insertOrUpdate(model);
        realm.commitTransaction();
    }

    public static DownloadModel getById(Realm realm, String id) {
        final DownloadModel file = realm
                .where(DownloadModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.ID, id)
                .findFirst();
        if (file != null)
            return realm.copyFromRealm(file);
        return null;
    }

    public static List<DownloadModel> getAll(Realm realm) {
        List<DownloadModel> list = realm
                .where(DownloadModel.class)
                .sort(Constant.PLAYER.DATABASE.FIELD.CREATE_DATE, Sort.ASCENDING)
                .findAll();
        if (list != null)
            return realm.copyFromRealm(list);
        return null;
    }

    public static List<DownloadModel> getByCompleted(Realm realm) {
        List<DownloadModel> list = realm
                .where(DownloadModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.STATUS, Constant.PLAYER.SERVER.DOWNLOAD.STATUS.COMPLETE)
                .findAll();
        if (list != null)
            return realm.copyFromRealm(list);
        return null;
    }

    public static DownloadModel getDownloadNext(Realm realm) {
        return realm
                .where(DownloadModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.STATUS, Constant.PLAYER.SERVER.DOWNLOAD.STATUS.WAIT)
                .sort(Constant.PLAYER.DATABASE.FIELD.CREATE_DATE, Sort.ASCENDING)
                .findFirst();
    }

    public static DownloadModel getByStatus(Realm realm, int status) {
        return realm
                .where(DownloadModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.STATUS, status)
                .findFirst();
    }

    public static DownloadModel getByDownload(Realm realm) {
        return getByStatus(realm, Constant.PLAYER.SERVER.DOWNLOAD.STATUS.DOWNLOAD);
    }

    public static DownloadModel getByPath(Realm realm, String path) {
        return realm
                .where(DownloadModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.PATH, path)
                .findFirst();
    }

    public static void deleteById(Realm realm, String id) {
        DLog.d(TAG, "deleteById - id=" + id);
        realm.executeTransaction(realm1 ->
                realm1.where(DownloadModel.class).equalTo(Constant.PLAYER.DATABASE.FIELD.ID, id).findFirst().deleteFromRealm());
    }

    public static void deleteAll(Realm realm) {
        DLog.d(TAG, "deleteAll");
        realm.executeTransaction(realm1 ->
                realm1.where(DownloadModel.class)
                        .findAll().deleteAllFromRealm());
    }

    public static void deleteByComplete(Realm realm) {
        DLog.d(TAG, "deleteAll");
        realm.executeTransaction(realm1 ->{
                final RealmResults<DownloadModel> list = realm1.where(DownloadModel.class)
                        .equalTo(Constant.PLAYER.DATABASE.FIELD.STATUS, Constant.PLAYER.SERVER.DOWNLOAD.STATUS.COMPLETE)
                        .findAll();
                if (list != null) {
                    list.deleteAllFromRealm();
                }
        });
    }

    public static void update(Realm realm, DownloadModel serverModel) {
        add(realm, serverModel);
    }
}
