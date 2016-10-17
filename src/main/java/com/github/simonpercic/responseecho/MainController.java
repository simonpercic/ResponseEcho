package com.github.simonpercic.responseecho;

import com.github.simonpercic.oklog.shared.SharedConstants;
import com.github.simonpercic.oklog.shared.data.BodyState;
import com.github.simonpercic.oklog.shared.data.LogData;
import com.github.simonpercic.responseecho.config.Constants;
import com.github.simonpercic.responseecho.manager.ResponseManager;
import com.github.simonpercic.responseecho.manager.analytics.AnalyticsManager;
import com.github.simonpercic.responseecho.manager.urlshortener.UrlShortenerManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import okhttp3.HttpUrl;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@RestController class MainController {

    private static final String RESPONSE_ECHO = "re";
    private static final String RESPONSE_INFO = "r";

    private static final String RESPONSE_ECHO_LEGACY_URL = "/" + RESPONSE_ECHO + "/{response}";
    private static final String RESPONSE_ECHO_URL = "/" + Constants.V1 + RESPONSE_ECHO_LEGACY_URL;
    private static final String RESPONSE_INFO_URL = "/" + Constants.V1 + "/" + RESPONSE_INFO + "/{response}";

    private static final String Q_SHORTEN_URL = "short";

    private final ResponseManager responseManager;
    private final UrlShortenerManager urlShortenerManager;
    private final AnalyticsManager analyticsManager;

    @Autowired MainController(ResponseManager responseManager, UrlShortenerManager urlShortenerManager,
            AnalyticsManager analyticsManager) {
        this.responseManager = responseManager;
        this.urlShortenerManager = urlShortenerManager;
        this.analyticsManager = analyticsManager;
    }

    @RequestMapping(value = {RESPONSE_ECHO_LEGACY_URL, RESPONSE_ECHO_URL},
            method = RequestMethod.GET,
            produces = "application/json")
    @ResponseBody String echoResponse(@PathVariable("response") String response) throws IOException {
        analyticsManager.sendPageView("/" + RESPONSE_ECHO);

        return responseManager.decodeBody(response);
    }

    @RequestMapping(value = RESPONSE_INFO_URL, method = RequestMethod.GET)
    ModelAndView responseInfo(
            HttpServletRequest request,
            @PathVariable("response") String responseBodyString,
            @RequestParam(value = SharedConstants.QUERY_PARAM_REQUEST_BODY, required = false) String requestBodyString,
            @RequestParam(value = SharedConstants.QUERY_PARAM_DATA, required = false) String logDataString,
            @RequestParam(value = Q_SHORTEN_URL, required = false) boolean shortenUrl)
            throws IOException {

        HttpUrl.Builder infoUrlBuilder = requestHttpUrl(request)
                .addPathSegment(Constants.V1)
                .addPathSegment(RESPONSE_INFO)
                .addEncodedPathSegment(responseBodyString);

        if (!StringUtils.isEmpty(requestBodyString)) {
            infoUrlBuilder.addEncodedQueryParameter(SharedConstants.QUERY_PARAM_REQUEST_BODY, requestBodyString);
        }

        if (!StringUtils.isEmpty(logDataString)) {
            infoUrlBuilder.addEncodedQueryParameter(SharedConstants.QUERY_PARAM_DATA, logDataString);
        }

        HttpUrl infoUrl = infoUrlBuilder
                .addEncodedQueryParameter(Q_SHORTEN_URL, String.valueOf(shortenUrl))
                .build();

        ModelAndView mav = new ModelAndView("response");

        if (shortenUrl) {
            HttpUrl shortUrl = urlShortenerManager.shorten(infoUrl);
            if (shortUrl != null) {
                infoUrl = shortUrl;
            }
        }

        HttpUrl responseBodyUrl = requestHttpUrl(request)
                .addPathSegment(Constants.V1)
                .addPathSegment(RESPONSE_ECHO)
                .addEncodedPathSegment(responseBodyString)
                .build();

        String responseBody = SharedConstants.EMPTY_RESPONSE_BODY.equals(responseBodyString)
                ? null
                : responseManager.decodeBody(responseBodyString);

        String requestBody = null;
        String requestBodyUrl = null;
        if (!StringUtils.isEmpty(requestBodyString)) {
            requestBody = responseManager.decodeBody(requestBodyString);

            requestBodyUrl = requestHttpUrl(request)
                    .addPathSegment(Constants.V1)
                    .addPathSegment(RESPONSE_ECHO)
                    .addEncodedPathSegment(requestBodyString)
                    .build()
                    .toString();
        }

        mav.addObject("info_url", infoUrl.toString());
        mav.addObject("response_body_url", responseBodyUrl.toString());
        mav.addObject("response_body", responseBody);
        mav.addObject("request_body_url", requestBodyUrl);
        mav.addObject("request_body", requestBody);

        LogData logData = responseManager.parseLogData(logDataString);
        if (logData != null) {
            mav.addObject("data_request_method", logData.request_method);
            mav.addObject("data_request_url", logData.request_url);
            mav.addObject("data_protocol", logData.protocol);
            mav.addObject("data_request_content_type", logData.request_content_type);
            mav.addObject("data_request_content_length", logData.request_content_length);
            mav.addObject("data_request_body_state", displayBodyState(logData.request_body_state));
            mav.addObject("data_request_headers", logData.request_headers);
            mav.addObject("data_request_failed", logData.request_failed);
            mav.addObject("data_response_code", logData.response_code);
            mav.addObject("data_response_message", logData.response_message);
            mav.addObject("data_response_duration_ms", logData.response_duration_ms);
            mav.addObject("data_response_content_length", logData.response_content_length);
            mav.addObject("data_response_body_size", logData.response_body_size);
            mav.addObject("data_response_body_state", displayBodyState(logData.response_body_state));
            mav.addObject("data_response_headers", logData.response_headers);
            mav.addObject("data_response_url", logData.response_url);
        }

        return mav;
    }

    private static HttpUrl.Builder requestHttpUrl(HttpServletRequest request) {
        return new HttpUrl.Builder()
                .scheme(request.getScheme())
                .host(request.getServerName())
                .port(request.getServerPort());
    }

    static String displayBodyState(BodyState bodyState) {
        if (bodyState == null) {
            return null;
        }

        switch (bodyState) {
            case PLAIN_BODY:
                return "Plain body";
            case NO_BODY:
                return "No body";
            case ENCODED_BODY:
                return "Encoded body";
            case BINARY_BODY:
                return "Binary body";
            case CHARSET_MALFORMED:
                return "Charset malformed";
            default:
                throw new IllegalArgumentException("unknown body state");
        }
    }
}
