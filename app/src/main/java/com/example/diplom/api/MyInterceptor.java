package com.example.diplom.api;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class MyInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        chain.request().url().url().getHost(); // проверка хоста

        var request = chain.request()
                .newBuilder()
                .addHeader("apikey", "fca_live_BPwqxb36ONW52WvZNI7bTVIEIiQTBrPcFcI8aBow")
                .build();

        return chain.proceed(request);
    }
}
