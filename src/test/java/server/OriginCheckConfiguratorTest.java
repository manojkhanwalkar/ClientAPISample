package server;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OriginCheckConfiguratorTest {

    @Test
    public void acceptsNonBrowserClientsWithoutOrigin() {
        OriginCheckConfigurator c = new OriginCheckConfigurator("");
        assertTrue(c.checkOrigin(null));
        assertTrue(c.checkOrigin(""));
    }

    @Test
    public void rejectsBrowserOriginsByDefault() {
        assertFalse(new OriginCheckConfigurator("").checkOrigin("https://evil.example"));
    }

    @Test
    public void acceptsOnlyConfiguredOrigins() {
        OriginCheckConfigurator c = new OriginCheckConfigurator("https://app.example, http://localhost:3000");
        assertTrue(c.checkOrigin("https://app.example"));
        assertTrue(c.checkOrigin("HTTP://LOCALHOST:3000"));
        assertFalse(c.checkOrigin("https://app.example.evil"));
    }
}
