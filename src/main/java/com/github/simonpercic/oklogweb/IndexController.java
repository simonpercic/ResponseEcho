package com.github.simonpercic.oklogweb;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by simon on 30/09/15.
 */
@RestController
public class IndexController implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public ModelAndView error() {
        return new ModelAndView("redirect:" + "https://github.com/simonpercic");
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
