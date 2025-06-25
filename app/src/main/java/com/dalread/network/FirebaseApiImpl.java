package com.dalread.network;

import com.dalread.BaseApplication;
import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.util.Constant;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseApiImpl {

    private BaseApplication application;
    private SharedPreferencesDB sharedPreferences;

    public FirebaseApiImpl(BaseApplication application) {
        this.application = application;
        sharedPreferences = application.getSharedPref();
    }

    public void trackLog(String logMessage, final DalApiListener<JSONObject> listener) {
        Call<JSONObject> call = application.getFirebaseApi().trackLog(
                Constant.API.HEADER_CONTENT_TYPE,
                sharedPreferences.getCookie(),
                sharedPreferences.getUid(),
                sharedPreferences.getUserName(),
                sharedPreferences.getFirebaseToken(),
                "Android",
                application.getString(R.string.app_name),
                "[" + (application.isBackground() ? "Background" : "Foreground") + "] " + logMessage
        );
        call.enqueue(new Callback<JSONObject>() {

            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }
}
