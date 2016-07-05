package com.github.simonpercic.responseecho.manager.urlshortener.api.request;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 * @noinspection FieldCanBeLocal
 */
public class UrlRequest {

    private final String longUrl;

    public UrlRequest(String longUrl) {
        this.longUrl = longUrl;
    }
}
