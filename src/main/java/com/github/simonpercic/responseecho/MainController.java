package com.github.simonpercic.responseecho;

import com.github.simonpercic.responseecho.config.Constants;
import com.github.simonpercic.responseecho.manager.ResponseManager;
import com.github.simonpercic.responseecho.manager.analytics.AnalyticsManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@RestController class MainController {

    private static final String BASE_ECHO_RESPONSE_PATH = "/re";
    private static final String ECHO_RESPONSE_PATH = BASE_ECHO_RESPONSE_PATH + "/{response}";

    private final ResponseManager responseManager;
    private final AnalyticsManager analyticsManager;

    @Autowired MainController(ResponseManager responseManager, AnalyticsManager analyticsManager) {
        this.responseManager = responseManager;
        this.analyticsManager = analyticsManager;
    }

    @RequestMapping(value = {ECHO_RESPONSE_PATH, Constants.V1 + ECHO_RESPONSE_PATH},
            method = RequestMethod.GET,
            produces = "application/json")
    @ResponseBody String echoResponse(@PathVariable(value = "response") String response) throws IOException {
        analyticsManager.sendPageView(BASE_ECHO_RESPONSE_PATH);

        return responseManager.decodeResponse(response);
    }
}
