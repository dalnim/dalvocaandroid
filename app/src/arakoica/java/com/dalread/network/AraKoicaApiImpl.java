package com.dalread.network;

import com.dalread.AraKoicaApplication;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.sqlite.model.DIC_ICT_TERM;
import com.dalread.util.Constant;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AraKoicaApiImpl {
    private final String TAG = "AraKoicaApiImpl";
    private AraKoicaApplication mApplication;

    public AraKoicaApiImpl(AraKoicaApplication mApplication) {
        this.mApplication = mApplication;
    }

    public void getNewIctTermList(String afterTime, final DalApiListener<List<DIC_ICT_TERM>> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        Call<List<DIC_ICT_TERM>> call = mApplication.getAraKoicaApi().getNewIctTermList(
                sharedPreferences.getToken(), Constant.API.HEADER_CONTENT_TYPE, afterTime
        );
        call.enqueue(new Callback<List<DIC_ICT_TERM>>() {

            @Override
            public void onResponse(Call<List<DIC_ICT_TERM>> call, Response<List<DIC_ICT_TERM>> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<DIC_ICT_TERM>> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }
    public void updateIctTerm(
            DIC_ICT_TERM item, int uid, final DalApiListener<Boolean> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        String token = sharedPreferences.getToken();
//        String json = new GsonBuilder().create().toJson(item);
        Call<Boolean> call = mApplication.getAraKoicaApi().updateIctTerm(token, Constant.API.HEADER_CONTENT_TYPE,
                uid,
                item.getVIId(),
                item.getTERM_ENG_ABBR(),
                item.getTERM_ENG_FULL(),
                item.getTERM_KO_TITLE(),
                item.getTERM_HANJA_TITLE(),
                item.getTERM_KO_SHORT(),
                item.getTERM_KO_FULL(),
                item.getGROUP_GIS(),
                item.getGROUP_HW(),
                item.getGROUP_GIS(),
                item.getGROUP_ETC(),
                item.getGROUP_NATION(),
                item.getORIGINAL_ID(),
                item.getURL(),
                item.getMEMO(),
                item.getTERM_LEVEL());
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void addIctTerm(
            DIC_ICT_TERM item, int uid, final DalApiListener<Integer> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        String token = sharedPreferences.getToken();
        Call<Integer> call = mApplication.getAraKoicaApi().addIctTerm(token, Constant.API.HEADER_CONTENT_TYPE,
                uid,
                item.getTERM_ENG_ABBR(),
                item.getTERM_ENG_FULL(),
                item.getTERM_KO_TITLE(),
                item.getTERM_HANJA_TITLE(),
                item.getTERM_KO_SHORT(),
                item.getTERM_KO_FULL(),
                item.getGROUP_GIS(),
                item.getGROUP_HW(),
                item.getGROUP_GIS(),
                item.getGROUP_ETC(),
                item.getGROUP_NATION(),
                item.getORIGINAL_ID(),
                item.getURL(),
                item.getMEMO(),
                item.getTERM_LEVEL());
        call.enqueue(new Callback<Integer>() {

            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

}
