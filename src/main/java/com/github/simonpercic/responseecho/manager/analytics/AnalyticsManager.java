package com.github.simonpercic.responseecho.manager.analytics;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public interface AnalyticsManager {
    void sendPageView(String path);
}
