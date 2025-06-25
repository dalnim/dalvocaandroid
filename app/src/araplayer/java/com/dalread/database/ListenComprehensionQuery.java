package com.dalread.database;

import com.dalread.model.ListenComprehensionModel;
import com.dalread.util.Constant;
import com.dalread.util.DLog;

import java.util.List;

import io.realm.Realm;

public class ListenComprehensionQuery {

    private static final String TAG = "ListenComprehensionQuery";

    public static int createIndex(Realm realm) {
        Number currentIdNum = realm.where(ListenComprehensionModel.class).max(Constant.PLAYER.DATABASE.FIELD.INDEX);
        int nextId;
        if(currentIdNum == null) {
            nextId = 0;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }

    public static void add(Realm realm, ListenComprehensionModel item) {
        DLog.d(TAG, "add - item=" + item.toString());
        realm.beginTransaction();
        realm.insertOrUpdate(item);
        realm.commitTransaction();
    }

    public static List<ListenComprehensionModel> getAll(Realm realm) {
        final List<ListenComprehensionModel> list = realm.where(ListenComprehensionModel.class)
                .sort(Constant.PLAYER.DATABASE.FIELD.INDEX)
                .findAll();
        if (list != null && !list.isEmpty())
            return realm.copyFromRealm(list);
        return null;
    }

    public static List<ListenComprehensionModel> getAllByCount(Realm realm) {
        final List<ListenComprehensionModel> list = realm.where(ListenComprehensionModel.class)
                .greaterThan(Constant.PLAYER.DATABASE.FIELD.COUNT, 0)
                .sort(Constant.PLAYER.DATABASE.FIELD.INDEX)
                .findAll();
        if (list != null && !list.isEmpty())
            return realm.copyFromRealm(list);
        return null;
    }

    public static void update(Realm realm, ListenComprehensionModel item) {
        add(realm, item);
    }

    public static void initData(Realm realm) {
        final List<ListenComprehensionModel> list = getAll(realm);
        if (list == null || list.isEmpty()) {
            add(realm, new ListenComprehensionModel(1, Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.TYPE.HIDE_THE_SUBTITLE, 0, 1));
            add(realm, new ListenComprehensionModel(2, Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.TYPE.SHOW_THE_SUBTITLE, 1, 1));
            add(realm, new ListenComprehensionModel(3, Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.TYPE.SHOW_DIFFICULT_WORDS_ONLY, 2, 1));
//            add(realm, new ListenComprehensionModel(4, Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION.TYPE.HIDE_THE_SUBTITLE, 3, 1));
        }
    }

    public static void deleteById(Realm realm, long id) {
        DLog.d(TAG, "deleteById - id=" + id);
        realm.executeTransaction(realm1 ->
                realm1.where(ListenComprehensionModel.class).equalTo(Constant.PLAYER.DATABASE.FIELD.ID, id).findFirst().deleteFromRealm());
    }
}
