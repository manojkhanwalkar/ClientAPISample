package client;

        import com.fasterxml.jackson.databind.ObjectMapper;
        import data.Request;
        import data.Response;
        import util.IdCreator;

        import javax.websocket.*;
        import java.io.IOException;
        import java.io.UncheckedIOException;
        import java.net.URI;
        import java.util.*;
        import java.util.concurrent.ConcurrentHashMap;
        import java.util.concurrent.ConcurrentMap;
        import java.util.concurrent.Future;


public class WebSocketClient {

    static WebSocketContainer container =  ContainerProvider.getWebSocketContainer();



    String host ;
    int port ;

    String protocol;

    String endPoint;


    static ThreadLocal<Session> localSession = new ThreadLocal<Session>();

    public WebSocketClient(String host, int port, String protocol, String endPoint) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
        this.endPoint = endPoint;
    }


    private Session connect() throws IOException {
        URI uri = URI.create(protocol + "://" + host + ":" + port + endPoint);
        try {
            return container.connectToServer(new ClientHandler(), uri);
        } catch (DeploymentException e) {
            throw new IOException("Unable to connect to " + uri, e);
        }
    }

    private Session session() throws IOException {
        Session session = localSession.get();
        if (session == null || !session.isOpen()) {
            session = connect();
            localSession.set(session);
        }
        return session;
    }


    public void disconnect() {

   /*     Set<Map.Entry<String, SessionURITuple>> entries = sessions.entrySet();

        for (Map.Entry<String,SessionURITuple> entry : entries) {
            try {
                Session session = entry.getValue().session;
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        sessions = null; */
    }



    static ObjectMapper mapper = new ObjectMapper();

    /**
     * Sends a request and returns a Future for its response. If the request cannot be sent, or the connection
     * closes before the response arrives, the returned Future fails with the cause instead of hanging.
     *
     * @throws NullPointerException if task is null
     */
    public Future<Response> send(Request task) {
        RequestHolder rh = new RequestHolder(Objects.requireNonNull(task, "task"));
        try {
            dispatch(rh);
        } catch (IOException | RuntimeException e) {
            rh.fail(e);
        }
        return rh.getFuture();
    }

    /**
     * Sends a request; notify is called when the response arrives, or {@link Notify#onFailure} if the
     * connection closes first.
     *
     * @throws java.io.UncheckedIOException if the request cannot be sent (no callback will follow)
     * @throws NullPointerException if task is null
     */
    public void asend(Request task, Notify notify) {
        RequestHolder rh = new RequestHolder(Objects.requireNonNull(task, "task"), notify);
        try {
            dispatch(rh);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Registers the request as pending and sends it; on failure it is no longer pending. */
    private void dispatch(RequestHolder rh) throws IOException {
        long id = rh.getRequest().getRequestId();
        String json = mapper.writeValueAsString(rh.getRequest());
        Session session = session();
        // set before registering, so a close that races with this send still finds and fails the request
        rh.setSession(session);
        if (waitList.putIfAbsent(id, rh) != null) {
            throw new IllegalStateException("Request id " + id + " is already waiting for a response");
        }
        try {
            session.getBasicRemote().sendText(json);
        } catch (IOException | RuntimeException e) {
            waitList.remove(id, rh);
            throw e;
        }
    }

    static ConcurrentMap<Long,RequestHolder> waitList = new ConcurrentHashMap<>();

    public static RequestHolder removeRequest(long requestId)
    {
        RequestHolder holder = waitList.remove(requestId);
        return holder;
    }

    /** Fails every request still waiting for a response on {@code session}, which has closed. */
    static void failPending(Session session, Throwable cause) {
        for (Map.Entry<Long, RequestHolder> entry : waitList.entrySet()) {
            RequestHolder holder = entry.getValue();
            if (holder.getSession() == session && waitList.remove(entry.getKey(), holder)) {
                holder.fail(cause);
            }
        }
    }
}
