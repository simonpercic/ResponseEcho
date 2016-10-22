# ResponseEcho
Java Spring web app that gets a Base64 encoded and gzipped string param, decodes and unpacks it and then echoes it back as a plain string.

[![Build Status](https://api.travis-ci.org/simonpercic/ResponseEcho.svg?branch=master)](https://travis-ci.org/simonpercic/ResponseEcho)


## Purpose
Used as a complementary web app for [OkLog](https://github.com/simonpercic/OkLog).


## Run
```
cd ResponseEcho
./gradlew stage bootRun
```


## Privacy
OkLog in combination with ResponseEcho are able to work by encoding request and response data in the URL path and query parameters. 
Consequently, this data might be intercepted on the network. 
The hosted instance of ResponseEcho that OkLog points to by default is accessible over plain HTTP (not HTTPS).
If you're concerned about your request and response data being intercepted, I strongly suggest you self-host ResponseEcho and set OkLog to point to your hosted instance (either locally or on your own server). 

### Url shortening
When using the url-shortening option (either via an option in OkLog or by using the shorten button on the response info page), the response info is shortened using the [goo.gl](https://goo.gl) url shortener service via their REST API, see: [UrlShortenerManager.java](src/main/java/com/github/simonpercic/responseecho/manager/urlshortener/UrlShortenerManager.java).
Since the request and response data is included in the URL itself, shortening it using an external service consequently means that data is stored by the url shortening service provider.
If you're concerned about your request and response data being stored by the shortening service, I strongly suggest you don't shorten the url.

### Google Analytics
Google Analytics is used in ResponseEcho to track its popularity and usage. 
There are two analytics methods included:
- using the Google Analytics API, when showing the plain response data, see: [GoogleAnalyticsManager.java](src/main/java/com/github/simonpercic/responseecho/manager/analytics/ga/GoogleAnalyticsManager.java).
- using the Google Analytics Web tracking via JavaScript, when showing the response info, see: [base_head.html](src/main/resources/templates/base_head.html).
In either of these methods, NO response data is included in the analytics tracking.


## Change Log
See [CHANGELOG.md](CHANGELOG.md)


## License
Open source, distributed under the MIT License. See [LICENSE](LICENSE) for details.
