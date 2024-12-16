package com.example.bookapp.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient {
    private const val BASE_URL_WORD = "https://book-api-115351686935.asia-southeast2.run.app/"
    private const val BASE_URL_RANDOM = "https://random-book-api-115351686935.asia-southeast2.run.app/"

    // Retrofit instance for word recommendations
    val wordRecommendationApi: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_WORD)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // Retrofit instance for random recommendations
    val randomRecommendationApi: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_RANDOM)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
