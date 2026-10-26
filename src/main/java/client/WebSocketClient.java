package client;

        import com.fasterxml.jackson.databind.ObjectMapper;
        import data.Request;
        import data.Response;
        import util.IdCreator;

        import javax.websocket.*;
        import java.io.IOException;
        import java.net.URI;
        import java.util.*;
        import java.util.concurrent.CompletableFuture;
        import java.util.concurrent.ConcurrentHashMap;
        import java.util.concurrent.ConcurrentMap;
        import java.util.concurrent.Future;
        import java.util.concurrent.FutureTask;


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
     * Sends a request and returns a Future for its response. If the request cannot be sent,
     * the returned Future fails with the cause instead of this method returning null.
     */
    public Future<Response> send(Request task) {
        RequestHolder rh = new RequestHolder(task);
        FutureTask<Response> ft = new FutureTask<>(rh);
        rh.setFutureTask(ft);
        try {
            String e = mapper.writeValueAsString(task);
            Session session = session();
            waitList.put(task.getRequestId(), rh);
            session.getBasicRemote().sendText(e);
            return ft;
        } catch (IOException | RuntimeException e1) {
            waitList.remove(task.getRequestId(), rh);
            CompletableFuture<Response> failed = new CompletableFuture<>();
            failed.completeExceptionally(e1);
            return failed;
        }
    }

    static ConcurrentMap<Long,RequestHolder> waitList = new ConcurrentHashMap<>();

    public static RequestHolder removeRequest(long requestId)
    {
        RequestHolder holder = waitList.remove(requestId);
        return holder;
    }


    /**
     * Sends a request; notify is called when the response arrives.
     *
     * @throws java.io.UncheckedIOException if the request cannot be sent (no callback will follow)
     */
    public void asend(Request task, Notify notify) {
        RequestHolder rh = new RequestHolder(task, notify);
        try {
            String e = mapper.writeValueAsString(task);
            Session session = session();
            waitList.put(task.getRequestId(), rh);
            session.getBasicRemote().sendText(e);
        } catch (IOException e1) {
            waitList.remove(task.getRequestId(), rh);
            throw new java.io.UncheckedIOException(e1);
        } catch (RuntimeException e1) {
            waitList.remove(task.getRequestId(), rh);
            throw e1;
        }
    }
}
