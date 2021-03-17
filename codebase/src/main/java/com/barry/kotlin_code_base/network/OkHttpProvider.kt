package com.barry.kotlin_code_base.network

import com.barry.kotlin_code_base.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object OkHttpProvider {

    val connectTime : Long = 30000
    val readTime : Long = 30000

    val clientBuilder : OkHttpClient.Builder by lazy {
        var builder = OkHttpClient.Builder()
            .connectTimeout(connectTime, TimeUnit.MILLISECONDS)
            .readTimeout(readTime, TimeUnit.MILLISECONDS)
        if (BuildConfig.DEBUG) {
            // add interceptor
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        }
        builder
    }

}