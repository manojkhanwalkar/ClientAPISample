package client;

import data.Request;
import data.Response;
import org.junit.Test;

import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class WebSocketClientFailureTest {

    private static int unusedPort() throws Exception {
        try (ServerSocket s = new ServerSocket(0)) {
            return s.getLocalPort();
        }
    }

    @Test
    public void sendFailsFutureAndDoesNotLeakWhenServerIsDown() throws Exception {
        WebSocketClient client = new WebSocketClient("127.0.0.1", unusedPort(), "ws", "/service/request");
        Request r = new Request();
        r.setRequestId(42L);
        Future<Response> f = client.send(r);
        assertNotNull("send must not return null", f);
        try {
            f.get(5, TimeUnit.SECONDS);
            fail("expected failure");
        } catch (ExecutionException expected) {
            // ok
        }
        assertTrue(WebSocketClient.removeRequest(42L) == null);
    }

    @Test
    public void asendThrowsAndDoesNotLeakWhenServerIsDown() throws Exception {
        WebSocketClient client = new WebSocketClient("127.0.0.1", unusedPort(), "ws", "/service/request");
        Request r = new Request();
        r.setRequestId(43L);
        try {
            client.asend(r, response -> { });
            fail("expected failure");
        } catch (UncheckedIOException expected) {
            // ok
        }
        assertTrue(WebSocketClient.removeRequest(43L) == null);
    }
}
