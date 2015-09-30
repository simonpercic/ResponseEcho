package com.github.simonpercic.oklogweb;

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
 * Created by simon on 30/09/15.
 */
@RestController
public class MainController {

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

        return sb.toString();
    }
}
