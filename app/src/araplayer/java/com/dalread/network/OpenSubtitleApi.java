package com.dalread.network;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dalread.BuildConfig;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.util.Constant;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public class OpenSubtitleApi {
    private final String TAG = "OpenSubtitleApi";

    private final OpenSubtitleService service;
    private final OpenSubtitleDownloadService downloadService;
    private Context context;
    private SharedPreferencesDB sharedPreferences;
    private static Retrofit retrofit;
    private static Retrofit downloadRetrofit;
    private final String bearerToken;
//    private final String API_KEY = "MNzZ7jNevxAk0sHdzSjsv00Ba87P0oOz";

    public OpenSubtitleApi(Context context) {
        this.context = context;
        this.sharedPreferences = SharedPreferencesDB.getInstance(context);
        bearerToken = "Bearer " + sharedPreferences.getOpenSubtitleLoginToken();

        OkHttpClient client = getOkHttpClient();

        this.service = getRetrofitInstance(client).create(OpenSubtitleService.class);
        this.downloadService = getDownloadRetrofitInstance(client).create(OpenSubtitleDownloadService.class);
    }

    //API를 부를때 URL경로등 로그를 제대로 다 보여줄려고.
    @NonNull
    private OkHttpClient getOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        return client;
    }

    private static Retrofit getRetrofitInstance(OkHttpClient client) {
        final String baseUrl = "https://api.opensubtitles.com/api/v1/";
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static Retrofit getDownloadRetrofitInstance(OkHttpClient client) {
        final String baseUrl = "https://www.opensubtitles.com";
        if (downloadRetrofit == null) {
            downloadRetrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create()) //GsonConverterFactory을 쓰면 string을 첫줄만 보여준다.
                    .build();
        }
        return downloadRetrofit;
    }


    public void login(OSLoginParam param, final DalApiListener<OSLoginResponse> listener) {
        Call<OSLoginResponse> call = service.login(param);
        call.enqueue(new Callback<OSLoginResponse>() {
            @Override
            public void onResponse(Call<OSLoginResponse> call, Response<OSLoginResponse> response) {
                if (response.isSuccessful()) {
                    if (listener != null) {
                        listener.onSuccess(response.body());
                    }
                } else {
                    Log.e(TAG, "response is not Successful(): ");
                    if (listener != null) {
                        listener.onFailure(null);
                    }
                }
            }

            @Override
            public void onFailure(Call<OSLoginResponse> call, Throwable t) {
                // Handle the failure here
                Log.e(TAG, "onFailure: " + t);
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void logout(DalApiListener<Boolean> listener) {
        Call<Void> call = service.logout(bearerToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (listener != null) {
                    if (response.isSuccessful()) {
                        listener.onSuccess(true);
                    } else {
                        listener.onFailure(null);
                    }
                } else {
                    listener.onFailure(null);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });
    }

    public void searchSubtitleList(OSSearchParam param, DalApiListener<OSSearchResponse> listener) {
        Map<String, String> options = new HashMap<>();
        options.put(Constant.API_KEY.KEY_query, param.query);
        options.put(Constant.API_KEY.KEY_languages, param.languages);
        Call<OSSearchResponse> call = service.searchSubtitleList(options);

        call.enqueue(new Callback<OSSearchResponse>() {
            @Override
            public void onResponse(Call<OSSearchResponse> call, Response<OSSearchResponse> response) {
                if (response.isSuccessful()) {
                    OSSearchResponse responseData = response.body();
                    if (responseData != null) {
                        listener.onSuccess(responseData);
                    } else {
                        listener.onFailure(null);
                    }
                } else {
                    listener.onFailure(null);
                }
            }

            @Override
            public void onFailure(Call<OSSearchResponse> call, Throwable t) {
                listener.onFailure(null);
            }
        });
    }

    public void getSubtitleDownloadLink(OSDownloadParam param, DalApiListener<OSDownloadResponse> listener) {
        RequestBodyOSDownloadSubtitle requestBody = new RequestBodyOSDownloadSubtitle( param.file_id);
        Call<OSDownloadResponse> call = service.getSubtitleDownloadLink(bearerToken, requestBody );
        call.enqueue(new Callback<OSDownloadResponse>() {
            @Override
            public void onResponse(Call<OSDownloadResponse> call, Response<OSDownloadResponse> response) {
                if (response.isSuccessful()) {
                    OSDownloadResponse responseData = response.body();
                    if (responseData != null) {
                        listener.onSuccess(responseData);
                    } else {
                        listener.onFailure(null);
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, "Error response: " + errorBody);
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error response", e);
                    }
                    listener.onFailure(null);
                }
            }

            @Override
            public void onFailure(Call<OSDownloadResponse> call, Throwable t) {
                listener.onFailure(null);
            }
        });
    }

    public void downloadSubtitle(String fileUrl, DalApiListener<String> listener) {
        Call<String> call = downloadService.downloadFile(fileUrl);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String content = response.body();
                    listener.onSuccess(content);
                } else {
                    listener.onFailure("");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                listener.onFailure("");
            }
        });
    }



    public static class OSLoginParam {
        private final String username;
        private final String password;

        public OSLoginParam(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    public class OSLoginResponse extends OSBaseResponse {
        private String token;
        private OSUserResponse user;

        public String getToken() {
            return token;
        }

        public OSUserResponse getUser() {
            return user;
        }
    }

    class OSBaseResponse {
        private int status;
        private String message;

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }

    class OSUserResponse {
        private int allowed_downloads;
        private String level;
        private int user_id;
        private boolean ext_installed;
        private boolean vip;

        public int getAllowed_downloads() {
            return allowed_downloads;
        }

        public String getLevel() {
            return level;
        }

        public int getUser_id() {
            return user_id;
        }

        public boolean isExt_installed() {
            return ext_installed;
        }

        public boolean isVip() {
            return vip;
        }
    }

    // Search params
    public static class OSSearchParam {
        public String query; // file name or text search
        public String languages; // Language code(s), coma separated (en,fr)
        public String moviehash; // Hash of video file, 근데 이건 안 먹는거 같다. query에서 찾을 자막 이름이 없이 이것만 있으면 자막을 못찾는다.

        public OSSearchParam(String query, String languages, String moviehash) {
            this.query = query;
            this.languages = languages;
            this.moviehash = moviehash;
        }
    }

    // Search response
    public class OSSearchResponse {
        public Integer total_pages;
        public Integer total_count;
        public Integer page;
        public List<OSSubtitleResponse> data;

        public OSSearchResponse(Integer total_pages, Integer total_count, Integer page, List<OSSubtitleResponse> data) {
            this.total_pages = total_pages;
            this.total_count = total_count;
            this.page = page;
            this.data = data;
        }
    }

    public final class OSSubtitleResponse {
        public String id;
        public String type;
        public OSSubtitleAttributesResponse attributes;
        public boolean IS_CHECK = false; // Local attribute to store checkmark status

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public OSSubtitleAttributesResponse getAttributes() {
            return attributes;
        }

        public void setAttributes(OSSubtitleAttributesResponse attributes) {
            this.attributes = attributes;
        }

        public boolean isIS_CHECK() {
            return IS_CHECK;
        }

        public void setIS_CHECK(boolean IS_CHECK) {
            this.IS_CHECK = IS_CHECK;
        }
    }

    public class OSSubtitleAttributesResponse implements Serializable {
        public String subtitle_id;
        public String language;
        public Integer download_count;
        public String upload_date;
        public String url;
        public String format;
        public List<OSFileResponse> files;
        public OSFeatureDetailResponse feature_details;

        public OSSubtitleAttributesResponse(String subtitle_id, String language, Integer download_count, String upload_date, String url, String format, List<OSFileResponse> files, OSFeatureDetailResponse feature_details) {
            this.subtitle_id = subtitle_id;
            this.language = language;
            this.download_count = download_count;
            this.upload_date = upload_date;
            this.url = url;
            this.format = format;
            this.files = files;
            this.feature_details = feature_details;
        }

    }

    public static class OSFileResponse implements Serializable {
        private Integer file_id;
        private Integer cd_number;
        private String file_name;

        public Integer getFile_id() {
            return file_id;
        }

        public void setFile_id(Integer file_id) {
            this.file_id = file_id;
        }

        public Integer getCd_number() {
            return cd_number;
        }

        public void setCd_number(Integer cd_number) {
            this.cd_number = cd_number;
        }

        public String getFile_name() {
            return file_name;
        }

        public void setFile_name(String file_name) {
            this.file_name = file_name;
        }
    }

    public class OSFeatureDetailResponse implements Serializable {
        private Integer imdb_id;
        private Integer feature_id;
        private String feature_type;
        private String movie_name;
        private String title;

        public OSFeatureDetailResponse(Integer imdb_id, Integer feature_id, String feature_type, String movie_name, String title) {
            this.imdb_id = imdb_id;
            this.feature_id = feature_id;
            this.feature_type = feature_type;
            this.movie_name = movie_name;
            this.title = title;
        }

        public Integer getImdb_id() {
            return imdb_id;
        }

        public void setImdb_id(Integer imdb_id) {
            this.imdb_id = imdb_id;
        }

        public Integer getFeature_id() {
            return feature_id;
        }

        public void setFeature_id(Integer feature_id) {
            this.feature_id = feature_id;
        }

        public String getFeature_type() {
            return feature_type;
        }

        public void setFeature_type(String feature_type) {
            this.feature_type = feature_type;
        }

        public String getMovie_name() {
            return movie_name;
        }

        public void setMovie_name(String movie_name) {
            this.movie_name = movie_name;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public static class OSDownloadParam implements Serializable {
        String file_id;

        public OSDownloadParam(String fileId) {
            this.file_id = fileId;
        }
    }

    public static class OSDownloadResponse implements Serializable {
        String link;
        @SerializedName("file_name")
        String fileName;
        int requests;
        int remaining;
        String message;

        public OSDownloadResponse(String link, String fileName, int requests, int remaining, String message) {
            this.link = link;
            this.fileName = fileName;
            this.requests = requests;
            this.remaining = remaining;
            this.message = message;
        }

        public String getLink() {
            return link;
        }

        public String getFileName() {
            return fileName;
        }

        public int getRequests() {
            return requests;
        }

        public int getRemaining() {
            return remaining;
        }

        public String getMessage() {
            return message;
        }
    }
}
interface OpenSubtitleService {
    String API_KEY = "MNzZ7jNevxAk0sHdzSjsv00Ba87P0oOz";
    String APP_NAME = BuildConfig.APP_NAME;
    @Headers({
            "Api-Key: " + API_KEY,
            "User-Agent: " + APP_NAME,
            "Content-Type: application/json"
    })
    @POST("login")
    Call<OpenSubtitleApi.OSLoginResponse> login(@Body OpenSubtitleApi.OSLoginParam param);

    @Headers({
            "Api-Key: " + API_KEY,
            "User-Agent: " + APP_NAME,
    })
    @DELETE("logout/")
    Call<Void> logout(@Header("Authorization") String token);

    @Headers({
            "Api-Key: " + API_KEY,
            "User-Agent: " + APP_NAME,
            "Content-Type: application/json"
    })
    @GET("subtitles/")
    Call<OpenSubtitleApi.OSSearchResponse> searchSubtitleList(@QueryMap Map<String, String> options);

    @Headers({
            "Api-Key: " + API_KEY,
            "User-Agent: " + APP_NAME,
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("download/")
    Call<OpenSubtitleApi.OSDownloadResponse> getSubtitleDownloadLink(
            @Header(Constant.API_KEY.KEY_Authorization) String token,
            @Body RequestBodyOSDownloadSubtitle requestBody);
}

interface OpenSubtitleDownloadService {
    @GET
    Call<String> downloadFile(@Url String url);
}

class RequestBodyOSDownloadSubtitle {
    @SerializedName("file_id")
    private String file_id;

    public RequestBodyOSDownloadSubtitle(String file_id) {
        this.file_id = file_id;
    }
}