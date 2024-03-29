package com.github.simonpercic.responseecho.manager.urlshortener;

import com.github.simonpercic.responseecho.manager.log.NoOpLogger;
import com.github.simonpercic.responseecho.manager.log.SimpleLogger;
import com.github.simonpercic.responseecho.manager.urlshortener.api.request.UrlRequest;
import com.github.simonpercic.responseecho.manager.urlshortener.api.response.UrlResponse;
import com.google.gson.Gson;

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

    private final String googleApiKey;
    private final GoogleUrlShortenerApiService apiService;
    private final SimpleLogger logger;

    @Autowired public UrlShortenerManager(Environment environment) {
        googleApiKey = environment.getProperty("GOOGLE_SHORTENER_API_KEY");

        apiService = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/urlshortener/v1/")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build()
                .create(GoogleUrlShortenerApiService.class);

        logger = new NoOpLogger();
    }

    public HttpUrl shorten(HttpUrl longUrl) {
        if (longUrl == null) {
            logger.warn("longUrl is null");
            return null;
        }

        if (StringUtils.isEmpty(googleApiKey)) {
            logger.warn("googleApiKey is empty");
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

                logger.warn(message);
            }
        } catch (IOException e) {
            String message = String.format("Failed to get short url for: %s due to: %s", longUrl, e.getMessage());
            logger.warn(message, e);
        }

        return null;
    }
}
