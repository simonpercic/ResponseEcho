package com.github.simonpercic.responseecho.manager.analytics.ga;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
interface GoogleAnalyticsApiService {

    @Headers("User-Agent: ResponseEcho")
    @FormUrlEncoded
    @POST("collect") Call<Void> collect(
            @Field("v") String version,
            @Field("tid") String trackingId,
            @Field("cid") String clientId,
            @Field("t") String hitType,
            @Field("dh") String documentHostName,
            @Field("dp") String documentPath);

}
