package com.dalread.asynctask;

import android.text.TextUtils;

import com.dalread.BaseApplication;
import com.dalread.model.GRAMMAR;
import com.dalread.model.READING;
import com.dalread.model.SERVER_VOCABOOKS;
import com.dalread.model.TBL_MESSAGE;
import com.dalread.model.TBL_VERSION;
import com.dalread.network.DalApiListener;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.DateUtils;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmObject;

public class GetTableVersionAndDownloadTableTask {

    private BaseApplication application;
    private String logTag;
    private List<String> tableNames;

    public GetTableVersionAndDownloadTableTask(BaseApplication application) {
        this.application = application;
        logTag = getClass().getSimpleName();
    }

    public void start() {
        List<String> tableNames = new ArrayList<>();
        tableNames.add(Constant.TABLE_NAME.TBL_SERVER_VOCABOOKS);
        tableNames.add(Constant.TABLE_NAME.TBL_MESSAGE);
        tableNames.add(Constant.TABLE_NAME.TBL_GRAMMAR);
        tableNames.add(Constant.TABLE_NAME.TBL_READING);
        start(tableNames);
    }

    private void start(List<String> tableNames) {
        this.tableNames = tableNames;
        if (this.tableNames != null && !this.tableNames.isEmpty()) {
            getTableVersion(this.tableNames.remove(0));
        }
    }

    private void getTableVersion(final String tableName) {
        if (application.getSharedPref().getRealUid() > 0 && Utils.isConnected(application)) {
            application.getDalAiImpl().getTableVersion(tableName, new DalApiListener<String>() {

                @Override
                public void onSuccess(String response) {
                    if (TextUtils.isEmpty(response)) {
                        start(tableNames);
                    } else {
                        final TBL_VERSION[] tblVersions = new TBL_VERSION[1];
                        Voca.executeRealmTransaction(realm -> {
                            TBL_VERSION tblVersion = realm.where(TBL_VERSION.class)
                                    .equalTo(Constant.TABLE_NAME.KEY, tableName)
                                    .findFirst();
                            if (tblVersion != null) {
                                tblVersions[0] = realm.copyFromRealm(tblVersion);
                            }
                        });
                        DLog.i(logTag, "Table " + tableName + " is at version " + (tblVersions[0] == null ? "0.0.0" : tblVersions[0].getVERSION()));
                        if (tblVersions[0] == null || tblVersions[0].hasUpdate(response)) {
                            downloadTable(tableName, response);
                        } else {
                            start(tableNames);
                        }
                    }
                }

                @Override
                public void onFailure(String error) {
                    start(tableNames);
                }
            });
        }
    }

    private void downloadTable(final String tableName, final String version) {
        if (application.getSharedPref().getRealUid() > 0 && Utils.isConnected(application)) {
            application.getDalAiImpl().downloadTable(tableName, new DalApiListener<String>() {

                @Override
                public void onSuccess(final String response) {
                    if (response != null) {
                        Voca.executeRealmTransaction(realm -> {
                            Class<? extends RealmObject> aClass;
                            switch (tableName) {
                                case Constant.TABLE_NAME.TBL_MESSAGE:
                                    aClass = TBL_MESSAGE.class;
                                    break;
                                case Constant.TABLE_NAME.TBL_SERVER_VOCABOOKS:
                                    aClass = SERVER_VOCABOOKS.class;
                                    break;
                                case Constant.TABLE_NAME.TBL_GRAMMAR:
                                    aClass = GRAMMAR.class;
                                    break;
                                case Constant.TABLE_NAME.TBL_READING:
                                    aClass = READING.class;
                                    break;
                                default:
                                    aClass = null;
                                    break;
                            }
                            if (aClass != null) {
                                // copy table's data to Realm
                                realm.delete(aClass);
                                realm.createAllFromJson(aClass, response);
                                DLog.i(logTag, "Table " + tableName + " has " + realm.where(aClass).count() + " records");
                                // copy table's version to Realm
                                String updateDate = DateUtils.getDateFullFormat().format(new Date());
                                TBL_VERSION tblVersion = realm.where(TBL_VERSION.class)
                                        .equalTo(Constant.TABLE_NAME.KEY, tableName)
                                        .findFirst();
                                if (tblVersion == null) {
                                    tblVersion = new TBL_VERSION();
                                    tblVersion.setTBL_NAME(tableName);
                                    tblVersion.setVERSION(version);
                                    tblVersion.setUPDATE_DATE(updateDate);
                                    realm.copyToRealm(tblVersion);
                                } else {
                                    tblVersion.setUPDATE_DATE(updateDate);
                                }
                            }
                        });
                    }
                    start(tableNames);
                }

                @Override
                public void onFailure(String error) {
                    start(tableNames);
                }
            });
        }
    }
}
