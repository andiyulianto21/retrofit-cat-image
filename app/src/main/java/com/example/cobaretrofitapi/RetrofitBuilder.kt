package com.example.cobaretrofitapi

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitBuilder {
    val BASE_URL = "http://10.0.2.2/retrofit_user/"

    private fun build(): Retrofit{
        val request = HttpLoggingInterceptor()
        request.setLevel(HttpLoggingInterceptor.Level.BODY);
        val client = OkHttpClient.Builder().addInterceptor(request).build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    fun apiService(): JsonPlaceholderApi {
        return build().create(JsonPlaceholderApi::class.java)
    }

}