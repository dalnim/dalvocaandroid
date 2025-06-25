package com.dalread.network;

import android.content.Context;

import androidx.annotation.NonNull;

import com.dalread.database.SharedPreferencesDB;
import com.dalread.listener.OnFinishGetDataListener;
import com.dalread.model.NativeSpeaker;
import com.dalread.model.VocaMemorize;
import com.dalread.model.VocaStudy;
import com.dalread.util.BaseVoca;
import com.dalread.util.Loading;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.Realm;
//이건 뭐하는거지?
public class GetMainDataHelper {

    private final Context context;
//    private final AlertDialog alertDialog;
    private final DalApiImpl apiImpl;
    private SharedPreferencesDB sharedPreferences;
    private final OnFinishGetDataListener listener;
    private int loadingCount;

//    public GetMainDataHelper(Context context, AlertDialog alertDialog, DalApiImpl apiImpl, SharedPreferencesDB sharedPreferences, OnFinishGetDataListener listener) {
//        this.context = context;
//        this.alertDialog = alertDialog;
//        this.apiImpl = apiImpl;
//        this.sharedPreferences = sharedPreferences;
//        this.listener = listener;
//    }

    public GetMainDataHelper(Context context, DalApiImpl apiImpl, SharedPreferencesDB sharedPreferences, OnFinishGetDataListener listener) {
        this.context = context;
        this.apiImpl = apiImpl;
        this.sharedPreferences = sharedPreferences;
        this.listener = listener;
    }

    public void getData(boolean isRefreshing, int uid) {
//        if (uid > 0) {
            if (UserUtil.isLoggedIn(context, true)) {
                BaseVoca.deleteAllRealmData();
                if (!isRefreshing) {
                    Loading.show(context);
                }
                getToMemorizeVocaList(uid);
//                getPronounceFeedback(uid);
                getVocasFromTargetVoca(uid);
                getPreferredNativeSpeakers(uid);
            } else {
//                alertDialog.showNoInternet();
                finishGetData();
            }
//        } else {
////            alertDialog.showLogInRequired();
//            finishGetData();
//        }
    }

    public void getDataNew(int uid) {
        if (uid > 0 && Utils.isConnected(context)) {
            getToMemorizeVocaList(uid);
            getVocasFromTargetVoca(uid);
            getPreferredNativeSpeakers(uid);
        } else {
            finishGetData();
        }
    }

    public void getDataToDalPlayer(int uid) {
        if (uid > 0 && Utils.isConnected(context)) {
            getPreferredNativeSpeakers(uid);
        } else {
            finishGetData();
        }
    }

    private void getToMemorizeVocaList(int uid) {
        loadingCount++;
        apiImpl.getToMemorizeVocaList(
                String.valueOf(uid),
                sharedPreferences.getLangStudyCode(),
                sharedPreferences.getMotherTongueLangCode(),
                new DalApiListener<List<VocaStudy>>() {

                    @Override
                    public void onSuccess(final List<VocaStudy> response) {
                        BaseVoca.executeRealmTransaction(new Realm.Transaction() {

                            @Override
                            public void execute(@NonNull Realm realm) {
                                realm.copyToRealmOrUpdate(response);
                            }
                        });
                        finishGetData();
                    }

                    @Override
                    public void onFailure(String error) {
                        finishGetData();
                    }
                }
        );
    }

    private void getVocasFromTargetVoca(int uid) {
        loadingCount++;
        apiImpl.getVocasFromTargetVoca(
                String.valueOf(uid),
                sharedPreferences.getLangStudyCode(),
                sharedPreferences.getMotherTongueLangCode(),
                new DalApiListener<List<VocaMemorize>>() {

                    @Override
                    public void onSuccess(final List<VocaMemorize> response) {
                        BaseVoca.executeRealmTransaction(new Realm.Transaction() {

                            @Override
                            public void execute(@NonNull Realm realm) {
                                realm.copyToRealmOrUpdate(response);
                            }
                        });
                        finishGetData();
                    }

                    @Override
                    public void onFailure(String error) {
                        finishGetData();
                    }
                }
        );
    }

    private void getPreferredNativeSpeakers(int uid) {
        loadingCount++;
        apiImpl.getAllNativeSpeakers(
                String.valueOf(uid),
                sharedPreferences.getLangStudyCode(),
                new DalApiListener<List<NativeSpeaker>>() {

                    @Override
                    public void onSuccess(List<NativeSpeaker> response) {
                        if (response == null) return;
                        ArrayList<NativeSpeaker> preferredNativeSpeakers = new ArrayList<>();
                        for (NativeSpeaker nativeSpeaker : response) {
                            if (nativeSpeaker.isPreferred()) {
                                preferredNativeSpeakers.add(nativeSpeaker);
                            }
                        }
                        String ids = "";
                        int count = 0;
                        if (!preferredNativeSpeakers.isEmpty()) {
                            Collections.sort(preferredNativeSpeakers, new Comparator<NativeSpeaker>() {

                                @Override
                                public int compare(NativeSpeaker o1, NativeSpeaker o2) {
                                    return o1.getRank() - o2.getRank();
                                }
                            });
                            for (NativeSpeaker nativeSpeaker : preferredNativeSpeakers) {
                                ids += "," + nativeSpeaker.getNativeSpeakerId();
                                count++;
                            }
                            if (!ids.isEmpty()) {
                                ids = ids.substring(1);
                            }
                        }
                        sharedPreferences.setPreferredNativeSpeakers(ids);
                        sharedPreferences.setPreferredNativeSpeakersCount(count);
                        finishGetData();
                    }

                    @Override
                    public void onFailure(String error) {
                        finishGetData();
                    }
                }
        );
    }
    //TODO : I think this is useless
    private void finishGetData() {
        if (--loadingCount <= 0) {
//            Loading.hide();
            if (listener != null) {
                listener.onFinish();
            }
        }
    }

//    public boolean isShowingDialog() {
//        return alertDialog.isShowing();
//    }
}
