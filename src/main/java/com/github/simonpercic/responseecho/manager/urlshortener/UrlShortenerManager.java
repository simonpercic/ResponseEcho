package com.github.simonpercic.responseecho.manager.urlshortener;

import com.github.simonpercic.responseecho.manager.urlshortener.api.request.UrlRequest;
import com.github.simonpercic.responseecho.manager.urlshortener.api.response.UrlResponse;
import com.google.gson.Gson;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@Component public class UrlShortenerManager {

    private static final Logger LOGGER = Logger.getLogger(UrlShortenerManager.class.getName());

    private final String googleApiKey;
    private final GoogleUrlShortenerApiService apiService;

    @Autowired public UrlShortenerManager(Environment environment) {
        googleApiKey = environment.getProperty("GOOGLE_SHORTENER_API_KEY");

        apiService = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/urlshortener/v1/")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build()
                .create(GoogleUrlShortenerApiService.class);
    }

    public HttpUrl shorten(HttpUrl longUrl) {
        if (longUrl == null) {
            LOGGER.warn("longUrl is null");
            return null;
        }

        if (StringUtils.isEmpty(googleApiKey)) {
            LOGGER.warn("googleApiKey is empty");
            return null;
        }

        Call<UrlResponse> call = apiService.shorten(googleApiKey, new UrlRequest(longUrl.toString()));

        try {
            Response<UrlResponse> result = call.execute();
            if (result.isSuccessful() && result.body() != null) {
                String shortUrl = result.body().getId();
                return HttpUrl.parse(shortUrl);
            } else {
                String message = String.format("Failed to get short url for: %s. Response: %s, body: %s",
                        longUrl, result.isSuccessful(), result.body() != null);

                LOGGER.warn(message);
            }
        } catch (IOException e) {
            String message = String.format("Failed to get short url for: %s due to: %s", longUrl, e.getMessage());
            LOGGER.warn(message, e);
        }

        return null;
    }
}
