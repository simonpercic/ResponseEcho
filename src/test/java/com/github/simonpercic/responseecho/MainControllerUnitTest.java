package com.github.simonpercic.responseecho;

import com.github.simonpercic.responseecho.manager.ResponseManager;
import com.github.simonpercic.responseecho.manager.analytics.AnalyticsManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class MainControllerUnitTest {

    @Mock AnalyticsManager analyticsManager;
    @Mock ResponseManager responseManager;

    private MainController mainController;

    @Before
    public void setUp() throws Exception {
        mainController = new MainController(responseManager, analyticsManager);
    }

    @Test
    public void testEchoResponse() throws Exception {
        String response = "response";
        String decodedResponse = "decodedResponse";

        when(responseManager.decodeResponse(eq(response))).thenReturn(decodedResponse);

        String result = mainController.echoResponse(response);
        assertEquals(decodedResponse, result);

        verify(analyticsManager).sendPageView(eq("/re"));
    }

    @Test
    public void testResponseInfo() throws Exception {
        String response = "response";
        String decodedResponse = "decodedResponse";

        when(responseManager.decodeResponse(eq(response))).thenReturn(decodedResponse);

        ModelAndView mav = mainController.responseInfo(response);
        assertEquals("response", mav.getViewName());

        Map<String, Object> model = mav.getModel();
        assertEquals("/v1/re/" + response, model.get("response_body_url"));
        assertEquals(decodedResponse, model.get("response_body"));
    }
}
