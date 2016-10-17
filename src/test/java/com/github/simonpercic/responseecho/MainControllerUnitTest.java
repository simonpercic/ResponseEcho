package com.github.simonpercic.responseecho;

import com.github.simonpercic.oklog.shared.data.BodyState;
import com.github.simonpercic.oklog.shared.data.HeaderData;
import com.github.simonpercic.oklog.shared.data.LogData;
import com.github.simonpercic.responseecho.manager.ResponseManager;
import com.github.simonpercic.responseecho.manager.analytics.AnalyticsManager;
import com.github.simonpercic.responseecho.manager.urlshortener.UrlShortenerManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import okhttp3.HttpUrl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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

        when(responseManager.decodeBody(eq(response))).thenReturn(decodedResponse);

        String result = mainController.echoResponse(response);
        assertEquals(decodedResponse, result);

        verify(analyticsManager).sendPageView(eq("/re"));
    }

    @Test
    public void testResponseInfo() throws Exception {
        String response = "response";
        String decodedResponse = "decodedResponse";

        when(responseManager.decodeBody(eq(response))).thenReturn(decodedResponse);

        HttpServletRequest request = mockRequest("http", "localhost", 8080);

        ModelAndView mav = mainController.responseInfo(request, response, null, null, false);
        assertEquals("response", mav.getViewName());

        Map<String, Object> model = mav.getModel();
        assertEquals(5, model.size());
        assertEquals("http://localhost:8080/v1/r/" + response + "?short=false", model.get("info_url"));
        assertEquals("http://localhost:8080/v1/re/" + response, model.get("response_body_url"));
        assertEquals(decodedResponse, model.get("response_body"));
        assertEquals(null, model.get("request_body"));
    }

    @Test
    public void testResponseInfoEmptyResponseBody() throws Exception {
        String response = "0";

        HttpServletRequest request = mockRequest("http", "localhost", 8080);

        ModelAndView mav = mainController.responseInfo(request, response, null, null, false);
        assertEquals("response", mav.getViewName());

        Map<String, Object> model = mav.getModel();
        assertEquals(5, model.size());
        assertEquals("http://localhost:8080/v1/r/" + response + "?short=false", model.get("info_url"));
        assertEquals("http://localhost:8080/v1/re/" + response, model.get("response_body_url"));
        assertEquals(null, model.get("response_body"));
        assertEquals(null, model.get("request_body"));
    }

    @Test
    public void testResponseInfoLogData() throws Exception {
        String responseBody = "response_body";
        String decodedResponse = "decoded_response_body";

        when(responseManager.decodeBody(eq(responseBody))).thenReturn(decodedResponse);

        String requestBody = "request_body";
        String decodedRequestBody = "decoded_request_body";

        when(responseManager.decodeBody(eq(requestBody))).thenReturn(decodedRequestBody);

        String logDataString = "log_data_string";

        List<HeaderData> requestHeaders = Arrays.asList(new HeaderData("q_n_1", "q_v_1"),
                new HeaderData("q_n_2", "q_v_2"));

        List<HeaderData> responseHeaders = Arrays.asList(new HeaderData("s_n_1", "s_v_1"),
                new HeaderData("s_n_2", "s_v_2"));

        LogData logData = new LogData("request_method", "request_url", "protocol", "request_content_type", 123L,
                requestHeaders, BodyState.PLAIN_BODY, false, 200, "response_message", 456L, 789L, responseHeaders,
                BodyState.ENCODED_BODY, 777L, "response_url");

        when(responseManager.parseLogData(eq(logDataString))).thenReturn(logData);

        HttpServletRequest request = mockRequest("http", "localhost", 8080);

        ModelAndView mav = mainController.responseInfo(request, responseBody, requestBody, logDataString, false);
        assertEquals("response", mav.getViewName());

        Map<String, Object> model = mav.getModel();
        assertEquals(20, model.size());
        assertEquals(
                "http://localhost:8080/v1/r/" + responseBody + "?qb=" + requestBody + "&d=" + logDataString + "&short=false",
                model.get("info_url"));

        assertEquals("http://localhost:8080/v1/re/" + responseBody, model.get("response_body_url"));
        assertEquals(decodedResponse, model.get("response_body"));
        assertEquals("http://localhost:8080/v1/re/" + requestBody, model.get("request_body_url"));
        assertEquals(decodedRequestBody, model.get("request_body"));
        assertEquals("request_method", model.get("data_request_method"));
        assertEquals("request_url", model.get("data_request_url"));
        assertEquals("protocol", model.get("data_protocol"));
        assertEquals("request_content_type", model.get("data_request_content_type"));
        assertEquals(123L, model.get("data_request_content_length"));
        assertEquals("Plain body", model.get("data_request_body_state"));
        assertEquals(logData.request_headers, model.get("data_request_headers"));
        assertEquals(false, model.get("data_request_failed"));
        assertEquals(200, model.get("data_response_code"));
        assertEquals("response_message", model.get("data_response_message"));
        assertEquals(456L, model.get("data_response_duration_ms"));
        assertEquals(789L, model.get("data_response_content_length"));
        assertEquals(777L, model.get("data_response_body_size"));
        assertEquals("Encoded body", model.get("data_response_body_state"));
        assertEquals(logData.response_headers, model.get("data_response_headers"));
    }

    @Test
    public void testDisplayBodyState() throws Exception {
        String state;

        state = MainController.displayBodyState(null);
        assertNull(state);

        state = MainController.displayBodyState(BodyState.PLAIN_BODY);
        assertEquals("Plain body", state);

        state = MainController.displayBodyState(BodyState.NO_BODY);
        assertEquals("No body", state);

        state = MainController.displayBodyState(BodyState.ENCODED_BODY);
        assertEquals("Encoded body", state);

        state = MainController.displayBodyState(BodyState.BINARY_BODY);
        assertEquals("Binary body", state);

        state = MainController.displayBodyState(BodyState.CHARSET_MALFORMED);
        assertEquals("Charset malformed", state);
    }

    @Test
    public void testResponseInfoShortenFalse() throws Exception {
        String response = "response";
        String decodedResponse = "decodedResponse";

        when(responseManager.decodeBody(eq(response))).thenReturn(decodedResponse);

        HttpServletRequest request = mockRequest("http", "localhost", 8080);

        ModelAndView mav = mainController.responseInfo(request, response, null, null, false);
        assertEquals("response", mav.getViewName());

        Map<String, Object> model = mav.getModel();
        assertEquals(5, model.size());
        assertEquals("http://localhost:8080/v1/r/" + response + "?short=false", model.get("info_url"));
        assertEquals("http://localhost:8080/v1/re/" + response, model.get("response_body_url"));
        assertEquals(decodedResponse, model.get("response_body"));

        verifyZeroInteractions(urlShortenerManager);
    }

    @Test
    public void testResponseInfoShortenTrueSuccess() throws Exception {
        String response = "response";
        String decodedResponse = "decodedResponse";

        when(responseManager.decodeBody(eq(response))).thenReturn(decodedResponse);

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

        ModelAndView mav = mainController.responseInfo(request, response, null, null, true);

        verify(urlShortenerManager).shorten(eq(longUrl));

        Map<String, Object> model = mav.getModel();
        assertEquals(5, model.size());
        assertEquals(shortUrl, model.get("info_url"));
    }

    @Test
    public void testResponseInfoShortenTrueFailed() throws Exception {
        String response = "response";
        String decodedResponse = "decodedResponse";

        when(responseManager.decodeBody(eq(response))).thenReturn(decodedResponse);

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

        ModelAndView mav = mainController.responseInfo(request, response, null, null, true);

        verify(urlShortenerManager).shorten(eq(longUrl));

        Map<String, Object> model = mav.getModel();
        assertEquals(5, model.size());
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
