package com.dalread.database;

import com.dalread.model.BookmarkPlayerModel;
import com.dalread.util.Constant;
import com.dalread.util.DLog;

import java.util.List;

import io.realm.Realm;

public class BookmarkPlayerModelQuery {

    private static final String TAG = "BookmarkPlayerModelQuery";

    public static int createIndex(Realm realm, String path) {
        Number currentIdNum = realm
                .where(BookmarkPlayerModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.PATH, path)
                .max(Constant.PLAYER.DATABASE.FIELD.INDEX);
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }

    public static void add(Realm realm, BookmarkPlayerModel bookmarkModel) {
        DLog.d(TAG, "add - bookmarkModel=" + bookmarkModel.toString());
        realm.beginTransaction();
        realm.insertOrUpdate(bookmarkModel);
        realm.commitTransaction();
    }

    public static List<BookmarkPlayerModel> getAllByPath(Realm realm, String path) {
        final List<BookmarkPlayerModel> list = realm.where(BookmarkPlayerModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.PATH, path)
                .sort(Constant.PLAYER.DATABASE.FIELD.INDEX)
                .findAll();
        if (list != null)
            return realm.copyFromRealm(list);
        return null;
    }

    public static List<BookmarkPlayerModel> getSelectedByPath(Realm realm, String path) {
        final List<BookmarkPlayerModel> list = realm.where(BookmarkPlayerModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.PATH, path)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.SELECTED, true)
                .sort(Constant.PLAYER.DATABASE.FIELD.INDEX)
                .findAll();
        if (list != null)
            return realm.copyFromRealm(list);
        return null;
    }

    public static boolean checkBookmarkByPath(Realm realm, String path) {
        return realm
                .where(BookmarkPlayerModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.PATH, path)
                .findFirst() != null;
    }

    public static void deleteById(Realm realm, long id) {
        DLog.d(TAG, "deleteById - id=" + id);
        realm.executeTransaction(realm1 -> {
            final BookmarkPlayerModel data = realm1.where(BookmarkPlayerModel.class)
                    .equalTo(Constant.PLAYER.DATABASE.FIELD.ID, id)
                    .findFirst();
            if (data != null) {
                data.deleteFromRealm();
            }
        });
    }

    public static void update(Realm realm, BookmarkPlayerModel bookmarkModel) {
        add(realm, bookmarkModel);
    }
}
