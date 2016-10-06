package com.github.simonpercic.responseecho.manager;

import com.github.simonpercic.oklog.shared.data.BodyState;
import com.github.simonpercic.oklog.shared.data.HeaderData;
import com.github.simonpercic.oklog.shared.data.LogData;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public class ResponseManagerUnitTest {

    private ResponseManager responseManager;

    @Before
    public void setUp() throws Exception {
        responseManager = new ResponseManager();
    }

    @Test
    public void testSimpleExample() throws Exception {
        String request = "H4sIAAAAAAAAAKtWKkotLs0pUbJKS8wpTq0FAJW4azcQAAAA";

        String response = "{\n" +
                "  \"result\": false\n" +
                "}";

        assertEquals(response, responseManager.decodeResponse(request));
    }

    @Test
    public void testObjectExample() throws Exception {
        String request = "H4sIAAAAAAAAAI2QP0_DQAzFv4p1C0vTJC1CqCszEhsjMjmTGO4f57uGgvrdcRqQGNks-_lnv_dl2JpDvzEBPZmDeWA" +
                "Xi9kYIZQY1kn1z5QvJXscVfVlPFmuXvVTKenQtuXo8ZMGG7ZD9G1NLqKV9iKXdhU_OQxWBkzU9u31_vZ2-5pGvRQzjxzQ_RP2K3-" +
                "qocQ6TGT_4s4bU_Mf1DzP2xV3YVFiiVYpfVuDpdyUiRobPTX9R9c36cd8VjYvcdx0mkT1HvNJmY8TBdANyCRsKRSB-AJ3E0mhfCV" +
                "wz87BCwe7iLyQO5JAyZgSWbjcAwSPInykpR8kYVYMLB_AzGWCEGHGE8RaNgvkBFJyHUen-ghS83FZRVk-iDUPylc8W3cCO-th1Wn" +
                "IkDDwAO-VhzedkIbusJBs1Rpytlqrm13X75vuptldr-3VsdntDl1nzt8omHacGAIAAA==";

        String response = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Pilot\",\n" +
                "  \"season\": 1,\n" +
                "  \"number\": 1,\n" +
                "  \"image\": {\n" +
                "    \"medium\": \"http://tvmazecdn.com/uploads/images/medium_landscape/1/4388.jpg\",\n" +
                "    \"original\": \"http://tvmazecdn.com/uploads/images/original_untouched/1/4388.jpg\"\n" +
                "  },\n" +
                "  \"url\": \"http://www.tvmaze.com/episodes/1/under-the-dome-1x01-pilot\",\n" +
                "  \"runtime\": 60,\n" +
                "  \"summary\": \"When the residents of Chester\\u0027s Mill find themselves trapped under a massive" +
                " transparent dome with no way out, they struggle to survive as resources rapidly dwindle and panic" +
                " quickly escalates.\",\n" +
                "  \"airdate\": \"2013-06-24\",\n" +
                "  \"airtime\": \"22:00\"\n" +
                "}";

        assertEquals(response, responseManager.decodeResponse(request));
    }

    @Test
    public void testToPrettyFormatJsonObject() {
        String input = "{\"id\":1,\"name\":\"Under the Dome\",\"runtime\":60,\"rating\":{\"average\":\"6.7\"}," +
                "\"network\":{\"id\":2,\"name\":\"CBS\"},\"image\":{\"medium\":\"http://example.com/1.jpg\"}}";

        String output = responseManager.toPrettyJsonFormat(input);
        String expected = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Under the Dome\",\n" +
                "  \"runtime\": 60,\n" +
                "  \"rating\": {\n" +
                "    \"average\": \"6.7\"\n" +
                "  },\n" +
                "  \"network\": {\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"CBS\"\n" +
                "  },\n" +
                "  \"image\": {\n" +
                "    \"medium\": \"http://example.com/1.jpg\"\n" +
                "  }\n" +
                "}";

        assertEquals(expected, output);
    }

    @Test
    public void testToPrettyFormatJsonArray() {
        String input = "[{\"id\":1,\"name\":\"Under the Dome\"},{\"id\":2,\"name\":\"True Detective\"}]";

        String output = responseManager.toPrettyJsonFormat(input);
        String expected = "[\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Under the Dome\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"True Detective\"\n" +
                "  }\n" +
                "]";

        assertEquals(expected, output);
    }

    @Test
    public void testToPrettyFormatNullField() {
        String input = "{\"id\":1,\"sample\":null,\"name\":\"Under the Dome\",\"runtime\":60," +
                "\"rating\":{\"average\":\"6.7\"},\"network\":{\"id\":2,\"name\":\"CBS\"}," +
                "\"image\":{\"medium\":\"http://example.com/1.jpg\"}}";

        String output = responseManager.toPrettyJsonFormat(input);
        String expected = "{\n" +
                "  \"id\": 1,\n" +
                "  \"sample\": null,\n" +
                "  \"name\": \"Under the Dome\",\n" +
                "  \"runtime\": 60,\n" +
                "  \"rating\": {\n" +
                "    \"average\": \"6.7\"\n" +
                "  },\n" +
                "  \"network\": {\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"CBS\"\n" +
                "  },\n" +
                "  \"image\": {\n" +
                "    \"medium\": \"http://example.com/1.jpg\"\n" +
                "  }\n" +
                "}";

        assertEquals(expected, output);
    }

    @Test
    public void testToPrettyFormatEmpty() {
        assertEquals("", responseManager.toPrettyJsonFormat(null));
        assertEquals("", responseManager.toPrettyJsonFormat(""));
    }

    @Test
    public void testToPrettyFormatNotJson() {
        String input = "Not a json";

        assertEquals(input, responseManager.toPrettyJsonFormat(input));
    }

    @Test
    public void testParseLogDataEmpty() throws Exception {
        LogData logData = responseManager.parseLogData("");
        assertNull(logData);
    }

    @Test
    public void testParseLogData() throws Exception {
        String logDataString = "H4sIAAAAAAAAAJ2Rz27TQBDGSYwKXXGo9gARpMISQkLIG69T8qc5YTlWW9HEUbKgHlmvN8lunV3L3jrNlbfiD" +
                "TjlmdgUiQPqicPMZX7zzcw3wLmICeytjSlGvl-UoqaGo3SQsk_I1NVabytaiI4NWu7QRrPbDtMb_6Hw-vmhzQ86wYe9M2x-fnL5q" +
                "zFvJl9ufj77vnfkCTha8LLmJTyK9DbVO9kCINJKcWaEVhDccl4gmouay7egdYPCP0PmdoNcbIRB14cMnaCL5TvQfgSY8w0VSqiVh" +
                "YJz6YIXVt9wZRDZFRye0KLIBaOHcb6stLLEm5AxXlXoAJY6R2Ge6y1KSrESCjY-yiloP0pMuFnrrIKdZEaukunCs755l3E49mbJg" +
                "nizr8Qbx9cxiT0yD6PYi5LpNI6IbIOX_-hN6D0KVxw2AyzPwKu_Z5GSqoo-eIOuMtjqDZa9ZZYGwTDr0nOcYrzsZn2Wyffg6dhaA" +
                "E_J-s5zcd9NmHG7OOi7eDA6w6Pe0L2YEOu_801QeGz_49Z8dVcWjfu986Pxf8_-DXy6JbEpAgAA";

        LogData logData = responseManager.parseLogData(logDataString);
        assertEquals("GET", logData.request_method);
        assertEquals("http://private-b7bc4-tvshowsapi.apiary-mock.com/shows", logData.request_url);
        assertEquals("http/1.1", logData.protocol);
        assertEquals((Long) 462L, logData.request_content_length);
        assertEquals(BodyState.NO_BODY, logData.request_body_state);
        assertEquals(false, logData.request_failed);
        assertEquals((Integer) 200, logData.response_code);
        assertEquals("OK", logData.response_message);
        assertEquals((Long) 953L, logData.response_duration_ms);
        assertEquals((Long) 462L, logData.response_content_length);
        assertEquals(0, logData.request_headers.size());
        assertEquals(11, logData.response_headers.size());
        assertHeader("Server", "Cowboy", logData.response_headers.get(0));
        assertHeader("Connection", "keep-alive", logData.response_headers.get(1));
        assertHeader("X-Apiary-Ratelimit-Limit", "120", logData.response_headers.get(2));
        assertHeader("X-Apiary-Ratelimit-Remaining", "119", logData.response_headers.get(3));
        assertHeader("Content-Type", "application/json", logData.response_headers.get(4));
        assertHeader("Access-Control-Allow-Origin", "*", logData.response_headers.get(5));
        assertHeader("Access-Control-Allow-Methods", "OPTIONS,GET,HEAD,POST,PUT,DELETE,TRACE,CONNECT",
                logData.response_headers.get(6));
        assertHeader("Access-Control-Max-Age", "10", logData.response_headers.get(7));
        assertHeader("X-Apiary-Transaction-Id", "57f5fdb118d2a90b00f2d6cd", logData.response_headers.get(8));
        assertHeader("Date", "Thu, 06 Oct 2016 07:30:58 GMT", logData.response_headers.get(9));
        assertHeader("Via", "1.1 vegur", logData.response_headers.get(10));
        assertEquals(BodyState.PLAIN_BODY, logData.response_body_state);
        assertEquals((Long) 462L, logData.response_body_size);
        assertEquals("http://private-b7bc4-tvshowsapi.apiary-mock.com/shows", logData.response_url);
    }

    private static void assertHeader(String name, String value, HeaderData headerData) {
        assertNotNull(headerData);
        assertEquals(name, headerData.name);
        assertEquals(value, headerData.value);
    }
}
