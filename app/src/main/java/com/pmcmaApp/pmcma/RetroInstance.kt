package com.pmcmaApp.pmcma

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroInstance {

    private const val BASE_URL = "https://dailyverses.net/"

    val api: ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .build()
            .create(ApiInterface::class.java)
    }
}
