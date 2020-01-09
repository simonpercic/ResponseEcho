package com.github.simonpercic.responseecho.manager.analytics.ga;

import com.github.simonpercic.responseecho.manager.analytics.AnalyticsManager;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@Component @Profile("prod") class GoogleAnalyticsManager implements AnalyticsManager, Callback<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleAnalyticsManager.class.getName());

    private final GoogleAnalyticsApiService apiService;

    public GoogleAnalyticsManager() {
        apiService = new Retrofit.Builder()
                .baseUrl(GoogleAnalyticsConstants.GA_URL)
                .build()
                .create(GoogleAnalyticsApiService.class);
    }

    @Override
    public void sendPageView(String path) {
        Call<Void> call = apiService.collect(
                GoogleAnalyticsConstants.GA_VERSION,
                GoogleAnalyticsConstants.GA_TRACKING_ID,
                GoogleAnalyticsConstants.GA_CLIENT_ID,
                GoogleAnalyticsConstants.GA_HIT_TYPE_PAGEVIEW,
                GoogleAnalyticsConstants.GA_HOSTNAME,
                path);

        call.enqueue(this);
    }

    @Override public void onResponse(Call<Void> call, Response<Void> response) {
        LOGGER.info("Analytics Call Success");
    }

    @Override public void onFailure(Call<Void> call, Throwable t) {
        LOGGER.warn("Analytics Call Failed", t);
    }
}
