package com.airmineral.airbagalert.data

import com.airmineral.airbagalert.BuildConfig
import com.airmineral.airbagalert.data.model.Places
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitInstance {
    @GET("/v1/revgeocode")
    suspend fun getLocation(
        @Query("at") at: String,
        @Query("apiKey") apiKey: String = BuildConfig.HERE_API_KEY
    ): Places

    companion object {
        private const val baseUrl = "https://revgeocode.search.hereapi.com"
        fun create(): RetrofitInstance {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(RetrofitInstance::class.java)
        }
    }
}