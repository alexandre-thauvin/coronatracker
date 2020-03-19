package com.coronatracker.coronatracker.network


import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getClient(): Retrofit {
        retrofit = Retrofit.Builder()
            .baseUrl("https://coronavirus-19-api.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(getOkHttpClient())
            .build()

        return retrofit!!
    }

    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(null)
            .addNetworkInterceptor { chain ->

                chain.proceed(
                    chain.request()
                        .newBuilder()
                        .build()
                )
            }
            .callTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .connectTimeout(2, TimeUnit.MINUTES)
            .build()
    }
}