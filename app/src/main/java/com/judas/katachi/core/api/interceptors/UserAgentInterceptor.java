package com.judas.katachi.core.api.interceptors;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UserAgentInterceptor implements Interceptor {
    private static final String USER_AGENT_HEADER = "User-Agent";

    @NonNull
    private final String userAgent;

    public UserAgentInterceptor(@NonNull final String userAgent) {
        this.userAgent = userAgent;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull final Chain chain) throws IOException {
        return chain.proceed(addUserAgent(chain.request()));
    }

    private Request addUserAgent(final Request original) {
        return original.newBuilder()
                .header(USER_AGENT_HEADER, userAgent)
                .method(original.method(), original.body())
                .build();
    }
}
