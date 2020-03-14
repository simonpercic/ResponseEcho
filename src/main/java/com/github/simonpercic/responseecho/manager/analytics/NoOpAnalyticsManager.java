package com.github.simonpercic.responseecho.manager.analytics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@Component @Profile("dev") class NoOpAnalyticsManager implements AnalyticsManager {

    private static final Logger LOGGER = LogManager.getLogger(NoOpAnalyticsManager.class);

    @Override public void sendPageView(String path) {
        LOGGER.info("Would send page view: " + path);
    }
}
