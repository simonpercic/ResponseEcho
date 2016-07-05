package com.github.simonpercic.responseecho.manager;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

        String output = responseManager.toPrettyFormat(input);
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

        String output = responseManager.toPrettyFormat(input);
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
    public void testToPrettyFormatEmpty() {
        assertEquals("", responseManager.toPrettyFormat(null));
        assertEquals("", responseManager.toPrettyFormat(""));
    }

    @Test
    public void testToPrettyFormatNotJson() {
        String input = "Not a json";

        assertEquals(input, responseManager.toPrettyFormat(input));
    }
}
