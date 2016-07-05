package com.github.simonpercic.responseecho;

import com.github.simonpercic.responseecho.manager.ResponseManager;
import com.github.simonpercic.responseecho.manager.analytics.AnalyticsManager;
import com.github.simonpercic.responseecho.manager.urlshortener.UrlShortenerManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import okhttp3.HttpUrl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class MainControllerUnitTest {

    @Mock AnalyticsManager analyticsManager;
    @Mock UrlShortenerManager urlShortenerManager;
    @Mock ResponseManager responseManager;

    private MainController mainController;

    @Before
    public void setUp() throws Exception {
        mainController = new MainController(responseManager, urlShortenerManager, analyticsManager);
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
    public void testResponseInfoShortenFalse() throws Exception {
        String response = "response";
        String decodedResponse = "decodedResponse";

        when(responseManager.decodeResponse(eq(response))).thenReturn(decodedResponse);

        HttpServletRequest request = mockRequest("http", "localhost", 8080);

        ModelAndView mav = mainController.responseInfo(request, response, false);
        assertEquals("response", mav.getViewName());

        Map<String, Object> model = mav.getModel();
        assertEquals("http://localhost:8080/v1/r/" + response + "?short=false", model.get("info_url"));
        assertEquals("http://localhost:8080/v1/re/" + response, model.get("response_body_url"));
        assertEquals(decodedResponse, model.get("response_body"));

        verifyZeroInteractions(urlShortenerManager);
    }

    @Test
    public void testResponseInfoShortenTrueSuccess() throws Exception {
        String response = "response";
        String decodedResponse = "decodedResponse";

        when(responseManager.decodeResponse(eq(response))).thenReturn(decodedResponse);

        HttpUrl longUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("localhost")
                .port(8080)
                .addPathSegment("v1")
                .addPathSegment("r")
                .addEncodedPathSegment(response)
                .addQueryParameter("short", "true")
                .build();

        String shortUrl = "http://shorturl.com/";
        when(urlShortenerManager.shorten(eq(longUrl))).thenReturn(HttpUrl.parse(shortUrl));

        HttpServletRequest request = mockRequest("http", "localhost", 8080);

        ModelAndView mav = mainController.responseInfo(request, response, true);

        verify(urlShortenerManager).shorten(eq(longUrl));

        Map<String, Object> model = mav.getModel();
        assertEquals(shortUrl, model.get("info_url"));
    }

    @Test
    public void testResponseInfoShortenTrueFailed() throws Exception {
        String response = "response";
        String decodedResponse = "decodedResponse";

        when(responseManager.decodeResponse(eq(response))).thenReturn(decodedResponse);

        HttpUrl longUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("localhost")
                .port(8080)
                .addPathSegment("v1")
                .addPathSegment("r")
                .addEncodedPathSegment(response)
                .addQueryParameter("short", "true")
                .build();

        when(urlShortenerManager.shorten(eq(longUrl))).thenReturn(null);

        HttpServletRequest request = mockRequest("http", "localhost", 8080);

        ModelAndView mav = mainController.responseInfo(request, response, true);

        verify(urlShortenerManager).shorten(eq(longUrl));

        Map<String, Object> model = mav.getModel();
        assertEquals(longUrl.toString(), model.get("info_url"));
    }

    private static HttpServletRequest mockRequest(String scheme, String serverName, int serverPort) {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getScheme()).thenReturn(scheme);
        when(request.getServerName()).thenReturn(serverName);
        when(request.getServerPort()).thenReturn(serverPort);

        return request;
    }
}
