package com.github.simonpercic.responseecho

import com.github.simonpercic.responseecho.manager.urlshortener.api.FireBaseDataFetchService
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val LOG_URL = "prettylog-546b4.firebaseio.com"
const val TOKEN_EXPIRY_MINUTES = 55
const val TOKEN_EXPIRY_MILL_DIFF = (TOKEN_EXPIRY_MINUTES * 60 * 1000).toLong()
const val LOG_KEY = "RESPONSE_LOG"
const val LOG_TIMESTAMP_KEY = "RESPONSE_LOG_TIME"

object FirebaseUtils {

    var token: String? = null
    var tokenPair: Pair<String?, Long> = GoogleApiTokenProvider.getToken()
        set(value) {
            field = value
            token = value.first
        }

    fun getData(key: String?) : String? {
        var data: String? = null
        validateAccessToke()
        token?.let { apiToken ->
            key?.let {
                val firebaseService = Retrofit.Builder().apply {
                    baseUrl("https://prettylog-546b4.firebaseio.com/")
                    addConverterFactory(GsonConverterFactory.create(Gson()))
                }.build().create(FireBaseDataFetchService::class.java)

                try {
                    val response = firebaseService.getURLData(it, apiToken).execute()
                    if(response.isSuccessful){
                        val firebaseData = response.body()
                        data = firebaseData.logdata
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return data;
    }

    fun validateAccessToke() {
        if (((System.currentTimeMillis() - tokenPair.second) < TOKEN_EXPIRY_MILL_DIFF) && token != null)
            return
        else {
            retriveToken()
            return
        }
    }

    private val retriveToken = {
        tokenPair = GoogleApiTokenProvider.getToken()
    }
}