package com.github.simonpercic.responseecho;

import com.google.gson.Gson;
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
@RestController
public class MainController {

    private final Gson gson;
    private final JsonParser jsonParser;

    @Autowired
    public MainController(Gson gson) {
        this.gson = gson;
        this.jsonParser = new JsonParser();
    }

    @RequestMapping(value = "/re/{response}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody String echoResponse(@PathVariable(value = "response") String response) throws IOException {
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
        return toPrettyFormat(json);
    }

    /**
     * Try to pretty-print the response string.
     *
     * @param responseString response string
     * @return pretty-printed response string
     */
    private String toPrettyFormat(String responseString) {
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
