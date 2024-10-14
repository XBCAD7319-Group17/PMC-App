package com.pmcmaApp.pmcma

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @GET("v1/bibles/{bibleId}/search")
    fun getVerseOfTheDay(
        @Path("bibleId") bibleId: String,
        @Query("query") verseId: String
    ): Call<VerseResponse>
}
