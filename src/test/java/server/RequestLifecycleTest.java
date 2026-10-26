package server;

import client.Notify;
import client.WebSocketClient;
import data.Request;
import data.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import util.Protocol;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/** Requests against a real server: every Future completes, and nothing stays pending. */
public class RequestLifecycleTest {

    private static Server server;
    private static WebSocketClient client;

    @BeforeClass
    public static void startServer() throws Exception {
        server = WebSocketServer.start("127.0.0.1", 0);
        int port = ((ServerConnector) server.getConnectors()[0]).getLocalPort();
        client = new WebSocketClient("127.0.0.1", port, "ws", "/service/request");
    }

    @AfterClass
    public static void stopServer() throws Exception {
        server.stop();
    }

    private static Request request(long id, String payload) {
        Request r = new Request();
        r.setRequestId(id);
        r.setPayLoad(payload);
        return r;
    }

    @Test
    public void responseCompletesTheFuture() throws Exception {
        Response response = client.send(request(100L, "hi")).get(10, TimeUnit.SECONDS);
        assertEquals(100L, response.getRequestId());
        assertNull(WebSocketClient.removeRequest(100L));
    }

    @Test
    public void connectionClosedByTheServerFailsThePendingRequest() throws Exception {
        // over the size limit: the server closes the connection (1009) instead of answering
        Future<Response> f = client.send(request(101L, new String(new char[Protocol.MAX_MESSAGE_SIZE + 1]).replace('\0', 'x')));
        try {
            f.get(10, TimeUnit.SECONDS);
            fail("expected the request to fail");
        } catch (ExecutionException expected) {
            assertTrue(expected.getCause().getMessage(), expected.getCause().getMessage().contains("closed"));
        }
        assertNull("failed request still pending", WebSocketClient.removeRequest(101L));
        // the client reconnects for the next request
        assertEquals(102L, client.send(request(102L, "again")).get(10, TimeUnit.SECONDS).getRequestId());
    }

    @Test
    public void asendReportsAClosedConnectionThroughOnFailure() throws Exception {
        CountDownLatch failed = new CountDownLatch(1);
        client.asend(request(103L, new String(new char[Protocol.MAX_MESSAGE_SIZE + 1]).replace('\0', 'x')),
                new Notify() {
                    @Override
                    public void callback(Response response) {
                    }

                    @Override
                    public void onFailure(Throwable cause) {
                        failed.countDown();
                    }
                });
        assertTrue("onFailure not called", failed.await(10, TimeUnit.SECONDS));
        assertNull(WebSocketClient.removeRequest(103L));
    }

    @Test
    public void throwingCallbackDoesNotLoseTheResponse() throws Exception {
        CountDownLatch called = new CountDownLatch(1);
        client.asend(request(104L, "hi"), response -> {
            called.countDown();
            throw new IllegalStateException("bug in user code");
        });
        assertTrue(called.await(10, TimeUnit.SECONDS));
        // the connection survives and the next request is answered
        assertEquals(105L, client.send(request(105L, "next")).get(10, TimeUnit.SECONDS).getRequestId());
    }

    @Test
    public void duplicateRequestIdIsRejectedWithoutOrphaningTheFirst() throws Exception {
        Future<Response> first = client.send(request(106L, "first"));
        Future<Response> second = client.send(request(106L, "second"));
        Response r = first.get(10, TimeUnit.SECONDS);
        assertEquals(106L, r.getRequestId());
        // either the duplicate was rejected, or the first had already been answered and it went through
        try {
            second.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
        }
        assertFalse(first.isCancelled());
    }

    @Test(expected = NullPointerException.class)
    public void nullRequestIsRejectedUpFront() {
        client.send(null);
    }
}
