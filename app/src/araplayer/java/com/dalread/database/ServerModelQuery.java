package com.dalread.database;

import com.dalread.model.ServerModel;
import com.dalread.util.Constant;
import com.dalread.util.DLog;

import java.util.List;

import io.realm.Realm;

public class ServerModelQuery {

    private static final String TAG = "ServerModelQuery";

    public static void add(Realm realm, ServerModel serverModel) {
        DLog.d(TAG, "add - serverModel=" + serverModel.toString());
        realm.beginTransaction();
        realm.insertOrUpdate(serverModel);
        realm.commitTransaction();
    }

    public static ServerModel getById(Realm realm, long id) {
        final ServerModel data = realm
                .where(ServerModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.ID, id)
                .findFirst();
        if (data != null) {
            return realm.copyFromRealm(data);
        }
        return null;
    }

    public static List<ServerModel> getAll(Realm realm) {
        return realm
                .where(ServerModel.class)
                .sort(Constant.PLAYER.DATABASE.FIELD.ID)
                .findAll();
    }

    public static void deleteById(Realm realm, long id) {
        DLog.d(TAG, "deleteById - id=" + id);
        realm.executeTransaction(realm1 ->{
                final ServerModel data = realm1
                        .where(ServerModel.class)
                        .equalTo(Constant.PLAYER.DATABASE.FIELD.ID, id)
                        .findFirst();
                if (data != null) {
                    data.deleteFromRealm();
                }
        });
    }

    public static void update(Realm realm, ServerModel serverModel) {
        add(realm, serverModel);
    }
}
