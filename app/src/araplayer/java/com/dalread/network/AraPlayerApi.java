package com.dalread.network;

import com.dalread.util.Constant;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AraPlayerApi {
    @Multipart
    @POST("makeRubyTextFromSubtitle.ajax")
    Call<ResponseBody> makeRubyTextFromSubtitle(
            @Header(Constant.API_KEY.KEY_COOKIE) String token,
            @Part(Constant.API_KEY.KEY_UID) RequestBody uid,
            @Part(Constant.API_KEY.KEY_LANG_STUDY_CODE) RequestBody studyLang,
            @Part(Constant.API_KEY.KEY_LANG_MEANING_CODE) RequestBody langDisplay,
//            @Part(Constant.API_KEY.KEY_ENCODING) RequestBody encoding,
            @Part(Constant.API_KEY.KEY_WITH_FIXED_NAME) RequestBody withFixedName,
            @Part(Constant.API_KEY.KEY_PARAM) RequestBody keyParam,
            @Part(Constant.API_KEY.KEY_SYNC_SUBTITLE_AT_SERVER) RequestBody syncSubtitleAtServer,
            @Part(Constant.API_KEY.KEY_TRANSLATE_INPUT_TEXT) RequestBody translateSubtitle,
            @Part(Constant.API_KEY.KEY_CLIENT_TYPE) RequestBody client_type,
            @Part(Constant.API_KEY.KEY_APP_NAME) RequestBody appName,
            @Part MultipartBody.Part file,
            @Part MultipartBody.Part file2
    );
}
