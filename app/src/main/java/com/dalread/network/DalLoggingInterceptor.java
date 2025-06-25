package com.dalread.network;

import com.dalread.util.DLog;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;

public class DalLoggingInterceptor implements Interceptor {

    private static final String TAG = "API";
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        //
        Request request = chain.request();
        RequestBody requestBody = request.body();
        Buffer buffer = new Buffer();
        MediaType contentType = null;
        if (requestBody != null) {
            requestBody.writeTo(buffer);
            contentType = requestBody.contentType();
        }
        Charset charset = UTF8;
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }
        String log = request.url() + "?" + buffer.readString(charset);
        //
        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            DLog.e(TAG, "" + e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        log += "\n(" + tookMs + "ms)";
        //
        if (response.body() != null && !request.url().toString().contains("DownloadTable")) {
            BufferedSource source = response.body().source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            buffer = source.buffer();
            charset = UTF8;
            contentType = response.body().contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            log += "\n-> " + buffer.clone().readString(charset);
        }
        DLog.i(TAG, log);
        return response;
    }
}
