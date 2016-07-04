package com.github.simonpercic.responseecho;

import com.github.simonpercic.responseecho.config.Constants;
import com.github.simonpercic.responseecho.manager.analytics.AnalyticsManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@RestController class MainController {

    private static final String BASE_ECHO_RESPONSE_PATH = "/re";
    private static final String ECHO_RESPONSE_PATH = BASE_ECHO_RESPONSE_PATH + "/{response}";

    private final Gson gson;
    private final JsonParser jsonParser;
    private final AnalyticsManager analyticsManager;

    @Autowired MainController(AnalyticsManager analyticsManager) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.jsonParser = new JsonParser();
        this.analyticsManager = analyticsManager;
    }

    @RequestMapping(value = {ECHO_RESPONSE_PATH, Constants.V1 + ECHO_RESPONSE_PATH},
            method = RequestMethod.GET,
            produces = "application/json")
    @ResponseBody String echoResponse(@PathVariable(value = "response") String response) throws IOException {
        if (response == null) {
            return "";
        }

        byte[] bytes = Base64.getUrlDecoder().decode(response);

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        GZIPInputStream gzipInputStream = new GZIPInputStream(bis);
        BufferedReader br = new BufferedReader(new InputStreamReader(gzipInputStream, "UTF-8"));

        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();
        gzipInputStream.close();
        bis.close();

        String json = sb.toString();
        String result = toPrettyFormat(json);

        analyticsManager.sendPageView(BASE_ECHO_RESPONSE_PATH);

        return result;
    }

    /**
     * Try to pretty-print the response string.
     *
     * @param responseString response string
     * @return pretty-printed response string
     */
    String toPrettyFormat(String responseString) {
        if (StringUtils.isEmpty(responseString)) {
            return "";
        }

        try {
            JsonElement jsonElement = jsonParser.parse(responseString);
            return gson.toJson(jsonElement);
        } catch (JsonSyntaxException e) {
            return responseString;
        }
    }
}
