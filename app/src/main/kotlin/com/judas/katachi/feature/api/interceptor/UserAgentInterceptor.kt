package com.judas.katachi.feature.api.interceptor

import com.judas.katachi.BuildConfig.*
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class UserAgentInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(addUserAgent(chain.request()))
    }

    private fun addUserAgent(original: Request): Request {
        return original.newBuilder()
            .header("User-Agent", "Android [$APPLICATION_ID] ($VERSION_NAME-$BUILD_TYPE)")
            .method(original.method, original.body)
            .build()
    }
}
