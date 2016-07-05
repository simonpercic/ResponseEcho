package com.github.simonpercic.responseecho;

import com.github.simonpercic.responseecho.manager.ResponseManager;
import com.github.simonpercic.responseecho.manager.analytics.AnalyticsManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class MainControllerResponseTest {

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
}
