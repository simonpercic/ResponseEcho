package com.github.simonpercic.responseecho.manager;

import com.github.simonpercic.oklog.shared.LogDataSerializer;
import com.github.simonpercic.oklog.shared.data.LogData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@Component
public class ResponseManager {

    private final Gson gson;
    private final JsonParser jsonParser;

    ResponseManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        this.jsonParser = new JsonParser();
    }

    public String decodeBody(String response) throws IOException {
        if (StringUtils.isEmpty(response)) {
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
        return toPrettyJsonFormat(json);
    }

    public LogData parseLogData(String logDataUrl) throws IOException {
        if (StringUtils.isEmpty(logDataUrl)) {
            return null;
        }

        byte[] bytes = Base64.getUrlDecoder().decode(logDataUrl);

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        GZIPInputStream gzipInputStream = new GZIPInputStream(bis);
        byte[] logData = IOUtils.toByteArray(gzipInputStream);

        return LogDataSerializer.deserialize(logData);
    }

    /**
     * Try to pretty-print the response string, if JSON.
     *
     * @param responseString response string
     * @return pretty-printed response string
     */
    String toPrettyJsonFormat(String responseString) {
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
