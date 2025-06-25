package com.dalread.network;

import android.content.Context;

import com.dalread.AraPlayerApplication;
import com.dalread.BuildConfig;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.Constant;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AraPlayerApiImpl {

    private final String TAG = "AraPlayerApiImpl";
    private AraPlayerApplication mApplication;

    public AraPlayerApiImpl(AraPlayerApplication mApplication) { this.mApplication = mApplication;
    }

    public void makeRubyTextFromSubtitle(Context context,
                                         PlayerFileModel playerFileModel,
//                                         String subPath,
                                         Object content,
//                                         String encoding,
                                         final DalApiListener<ResponseBody> listener) {
        makeRubyTextFromSubtitle(context, playerFileModel, content, false, listener);
    }

    public void makeRubyTextFromSubtitle(Context context,
                                         PlayerFileModel playerFileModel,
//                                         String subPath,
                                         Object content,
//                                         String encoding,
                                         boolean isSyncSubtitle,
                                         final DalApiListener<ResponseBody> listener) {
        SharedPreferencesDB sharedPreferences = mApplication.getSharedPref();
        String token = sharedPreferences.getToken();
        File file = new File(BaseStorageUtil.getPath(context, playerFileModel.getSubPath1()));
        RequestBody requestFile = null;
        MultipartBody.Part multipartBody = null;
        MultipartBody.Part multipartBody2 = null;

        if (content instanceof String) {
            //Send string of subtitle
            requestFile = RequestBody.create(String.valueOf(content), MediaType.parse("application/octet-stream"));
            multipartBody = MultipartBody.Part.createFormData("userfile", file.getName(), requestFile);

            if (playerFileModel.getVideoModel().hasSubPath2()) {
//                File file2 = new File(BaseStorageUtil.getPath(context, playerFileModel.getSubPath2()));
                RequestBody requestFile2 = null;

                //TODO : getFileContentsFromFile 사용가능한지 확인
                String content2 = "";// SubtitleUtil.parserContentSubTitle(playerFileModel, Constant.PLAYER.INTENT.SUBPATH_INDEX_2);
                requestFile2 = RequestBody.create(String.valueOf(content2), MediaType.parse("application/octet-stream"));
                multipartBody2 = MultipartBody.Part.createFormData("userfile2", file.getName(), requestFile2);
            }

        } else if (content instanceof File) {
            //Send SQLite file.
            final File sql = (File) content;
            requestFile = RequestBody.create(sql, MediaType.parse("application/octet-stream"));
            multipartBody = MultipartBody.Part.createFormData("userfile", sql.getName(), requestFile);
        }

        RequestBody requestId = RequestBody.create(sharedPreferences.getUid(), MediaType.parse(Constant.API.HEADER_CONTENT_TYPE));
        RequestBody requestStudyLang = RequestBody.create(String.valueOf(sharedPreferences.getLangStudyCode()), MediaType.parse(Constant.API.HEADER_CONTENT_TYPE));
        RequestBody requestDisplayLang = RequestBody.create(String.valueOf(sharedPreferences.getMotherTongueLangCode()), MediaType.parse(Constant.API.HEADER_CONTENT_TYPE));
        RequestBody requestFixedName = RequestBody.create(Constant.API_KEY.KEY_WITH_FIXED_NAME, MediaType.parse(Constant.API.HEADER_CONTENT_TYPE));
        RequestBody requestKeyParam = RequestBody.create("Value_Param", MediaType.parse(Constant.BASE_BLANK));
        RequestBody requestClientType = RequestBody.create(Constant.CLIENT_TYPE_ANDROID, MediaType.parse(Constant.API.HEADER_CONTENT_TYPE));
        RequestBody requestAppName = RequestBody.create(BuildConfig.APP_NAME, MediaType.parse(Constant.API.HEADER_CONTENT_TYPE));
        RequestBody requestSyncSubtitleAtServer;
        if (isSyncSubtitle) {
            requestSyncSubtitleAtServer = RequestBody.create(sharedPreferences.getSyncSubtitlesAtServer() ? "1" : "0", MediaType.parse(Constant.BASE_BLANK));
        } else {
            requestSyncSubtitleAtServer = RequestBody.create(Constant.BASE_BLANK, MediaType.parse(Constant.BASE_BLANK));
        }
        RequestBody requestTranslateSubtitle = RequestBody.create(sharedPreferences.getTranslateSubtitleFromServerValue(), MediaType.parse(Constant.BASE_BLANK));
        Call<ResponseBody> call = mApplication.getAraPlayerApi().makeRubyTextFromSubtitle(
                token, requestId, requestStudyLang, requestDisplayLang, requestFixedName, requestKeyParam,
                requestSyncSubtitleAtServer, requestTranslateSubtitle, requestClientType, requestAppName, multipartBody, multipartBody2);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (listener != null) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }
}
