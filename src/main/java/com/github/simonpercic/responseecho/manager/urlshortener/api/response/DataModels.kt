package com.github.simonpercic.responseecho.manager.urlshortener.api.response

import com.google.gson.annotations.SerializedName

data class FirebaseDataModel(@SerializedName("RESPONSE_LOG") val logdata : String?,@SerializedName("RESPONSE_LOG_TIME") val timestamp : Long?)