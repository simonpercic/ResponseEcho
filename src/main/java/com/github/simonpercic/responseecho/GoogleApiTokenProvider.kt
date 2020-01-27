package com.github.simonpercic.responseecho

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import org.springframework.core.io.ClassPathResource
import java.util.*

const val JSON_FILE_NAME = "prettylog_546b4_firebase_adminsdk_g2afy_917a2ad7d2.json"

object GoogleApiTokenProvider {
    fun getToken(): Pair<String?, Long> {
        var token: Pair<String?, Long> = Pair(null, System.currentTimeMillis())
        try {
            val serviceKeyStream = ClassPathResource(JSON_FILE_NAME).inputStream
                    ?: return token
            // GoogleApiClient::class.java.classLoader.getResourceAsStream(SERVICE_JSON_FILE_NAME)

            val googleCred = GoogleCredential.fromStream(serviceKeyStream)
            // Add the required scopes to the Google credential
            val scoped = googleCred.createScoped(
                    Arrays.asList(
                            "https://www.googleapis.com/auth/firebase.database",
                            "https://www.googleapis.com/auth/userinfo.email"
                    )
            )
            // Use the Google credential to generate an access token
            scoped.refreshToken()
            token = Pair(scoped.accessToken, System.currentTimeMillis())
        } catch (e: Exception) {
            println(e.message)
        }
        return token
    }
}