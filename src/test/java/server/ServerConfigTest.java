package server;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ServerConfigTest {

    @Test
    public void originsMatchHowBrowsersSendThem() {
        OriginCheckConfigurator check =
                new OriginCheckConfigurator("https://App.example/, https://other.example:443, http://dev.example:8080");
        assertTrue(check.checkOrigin("https://app.example"));
        assertTrue(check.checkOrigin("https://other.example"));
        assertTrue(check.checkOrigin("http://dev.example:8080"));
        assertFalse(check.checkOrigin("http://dev.example"));
        assertFalse(check.checkOrigin("https://evil.example"));
        assertFalse(check.checkOrigin("null"));
        assertTrue("non-browser clients send no Origin", check.checkOrigin(null));
    }

    @Test
    public void portSettingIsValidated() {
        assertEquals(9090, WebSocketServer.port("9090"));
        for (String bad : new String[]{"90a0", "70000", "-1", ""}) {
            try {
                WebSocketServer.port(bad);
                throw new AssertionError("accepted " + bad);
            } catch (IllegalArgumentException expected) {
                assertTrue(expected.getMessage().contains("WS_PORT"));
            }
        }
    }
}
