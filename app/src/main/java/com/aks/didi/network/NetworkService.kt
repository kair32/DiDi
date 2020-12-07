package com.aks.didi.network

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkService {
    private const val BASE_URL = "https://api.taxididi.com.ru"

    private val loggingInterceptor = run {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val baseInterceptor: Interceptor = Interceptor.invoke { chain ->
        val newUrl = chain
                .request()
                .url
                .newBuilder()
                .build()

        val request = chain
                .request()
                .newBuilder()
                .url(newUrl)
                .build()

        return@invoke chain.proceed(request)
    }

    private val client: OkHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(baseInterceptor)
            .build()

    private val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create()

    fun retrofitService(): Api {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
                .create(Api::class.java)
    }
}