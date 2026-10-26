package client;

import data.Request;
import data.Response;

import javax.websocket.Session;
import java.util.concurrent.CompletableFuture;

/**
 * A request waiting for its response: completes the caller's future and, for {@link WebSocketClient#asend},
 * calls its {@link Notify}. Remembers the session it was sent on, so it can be failed if that session closes.
 */
public class RequestHolder {

    private final Request request;
    private final Notify notify;
    private final CompletableFuture<Response> future = new CompletableFuture<>();
    private volatile Session session;

    public RequestHolder(Request request, Notify notify) {
        this.request = request;
        this.notify = notify;
    }

    public RequestHolder(Request request) {
        this(request, null);
    }

    public Request getRequest() {
        return request;
    }

    public Notify getNotify() {
        return notify;
    }

    public CompletableFuture<Response> getFuture() {
        return future;
    }

    Session getSession() {
        return session;
    }

    void setSession(Session session) {
        this.session = session;
    }

    /** Completes the future, then calls the callback; a throwing callback can't leave the future pending. */
    void complete(Response response) {
        future.complete(response);
        if (notify != null) {
            try {
                notify.callback(response);
            } catch (RuntimeException e) {
                System.err.println("Callback for request " + request.getRequestId() + " failed: " + e);
            }
        }
    }

    void fail(Throwable cause) {
        future.completeExceptionally(cause);
        if (notify != null) {
            try {
                notify.onFailure(cause);
            } catch (RuntimeException e) {
                System.err.println("Failure callback for request " + request.getRequestId() + " failed: " + e);
            }
        }
    }
}
