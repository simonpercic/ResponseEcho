package com.github.simonpercic.responseecho.manager.urlshortener.api

import com.github.simonpercic.responseecho.manager.urlshortener.api.response.FirebaseDataModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FireBaseDataFetchService {
    @GET("logData/logs/{key}.json")
    fun getURLData(@Path("key") key : String,
                           @Query("access_token") access_token : String) : Call<FirebaseDataModel>
}