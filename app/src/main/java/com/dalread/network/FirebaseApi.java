package com.dalread.network;

import com.dalread.util.Constant;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface FirebaseApi {

    @POST("trackLogAsia")
    @FormUrlEncoded
    Call<JSONObject> trackLog(
            @Header("Content-Type") String contentType,
            @Header("Cookie") String cookie,
            @Field(Constant.API_KEY.KEY_UID) String uid,
            @Field(Constant.API_KEY.KEY_NAME) String userName,
            @Field(Constant.API_KEY.KEY_DEVICE_TOKEN) String deviceToken,
            @Field(Constant.API_KEY.KEY_OS_TYPE) String osType,
            @Field(Constant.API_KEY.KEY_APPNAME) String appName,
            @Field(Constant.API_KEY.KEY_LOG_MESSAGE) String logMessage
    );
}
