package com.github.simonpercic.responseecho.manager.urlshortener;

import com.github.simonpercic.responseecho.manager.urlshortener.api.request.UrlRequest;
import com.github.simonpercic.responseecho.manager.urlshortener.api.response.UrlResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
interface GoogleUrlShortenerApiService {

    @POST("url") Call<UrlResponse> shorten(
            @Query("key") String apiKey,
            @Body UrlRequest request);

}
