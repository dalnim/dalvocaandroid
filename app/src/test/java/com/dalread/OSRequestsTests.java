package com.dalread;

import junit.framework.TestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class OSRequestsTests extends TestCase {
    private static final String BASE_URL = "https://api.opensubtitles.com/api/v1/";
    private static final String API_KEY = "MNzZ7jNevxAk0sHdzSjsv00Ba87P0oOz";
    private static final int TIMEOUT = 60;

    public void testLogin() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        OSLoginParam param = new OSLoginParam("dalnimbest", "dal0192");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenSubtitleService service = retrofit.create(OpenSubtitleService.class);

        Call<OSLoginResponse> call = service.openSubtitleLogin(param);
        call.enqueue(new Callback<OSLoginResponse>() {
            @Override
            public void onResponse(Call<OSLoginResponse> call, Response<OSLoginResponse> response) {
                if (response.isSuccessful()) {
                    OSLoginResponse result = response.body();
                    assertNotNull(result.getToken());
                    latch.countDown();
                } else {
                    fail("Log in failed with error: " + response.errorBody().toString());
                    latch.countDown();
                }
            }

            @Override
            public void onFailure(Call<OSLoginResponse> call, Throwable t) {
                fail("Log in failed with error: " + t.getMessage());
                latch.countDown();
            }
        });

        latch.await(TIMEOUT, TimeUnit.SECONDS);
    }
}


interface OpenSubtitleService {
    String API_KEY = "MNzZ7jNevxAk0sHdzSjsv00Ba87P0oOz";
    String BASE_URL = "https://api.opensubtitles.com/api/v1/";

    @retrofit2.http.Headers({
            "Api-Key: " + API_KEY,
            "Content-Type: application/json"
    })
    @POST("login")
    Call<OSLoginResponse> openSubtitleLogin(@Body OSLoginParam param);
}

class OSLoginParam {
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

class OSLoginResponse extends OSBaseResponse {
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
