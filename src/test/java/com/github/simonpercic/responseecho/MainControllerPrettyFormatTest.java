package com.github.simonpercic.responseecho;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public class MainControllerPrettyFormatTest {

    private MainController mainController;

    @Before
    public void setUp() throws Exception {
        mainController = new MainController();
    }

    @Test
    public void testToPrettyFormatJsonObject() {
        String input = "{\"id\":1,\"name\":\"Under the Dome\",\"runtime\":60,\"rating\":{\"average\":\"6.7\"}," +
                "\"network\":{\"id\":2,\"name\":\"CBS\"},\"image\":{\"medium\":\"http://example.com/1.jpg\"}}";

        String output = mainController.toPrettyFormat(input);
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

        String output = mainController.toPrettyFormat(input);
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
        assertEquals("", mainController.toPrettyFormat(null));
        assertEquals("", mainController.toPrettyFormat(""));
    }

    @Test
    public void testToPrettyFormatNotJson() {
        String input = "Not a json";

        assertEquals(input, mainController.toPrettyFormat(input));
    }
}
