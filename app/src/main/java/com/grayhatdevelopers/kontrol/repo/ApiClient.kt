package com.grayhatdevelopers.kontrol.repo

import android.util.Log
import com.google.gson.GsonBuilder
import com.grayhatdevelopers.kontrol.utils.AppConstants
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private val okHttpClient by lazy { OkHttpClient() }

    private val retrofit: Retrofit by lazy {
        Log.e("AppClient", "Creating Retrofit Client")
        val builder = Retrofit.Builder()
            .baseUrl(AppConstants.AWS_SERVER_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))


        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 1

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client: OkHttpClient = okHttpClient.newBuilder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor).addInterceptor {
                val request = it.request().newBuilder()
                    .addHeader(AppConstants.TOKEN, Repository.getInstance().getUserToken()).build()
                return@addInterceptor it.proceed(request)
            }
            .dispatcher(dispatcher)
            .build()
        builder.client(client).build()
    }

    fun <T> createService(tClass: Class<T>): T {
        return retrofit.create(tClass) as T
    }
}