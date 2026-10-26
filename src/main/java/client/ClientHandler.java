package client;

        import com.fasterxml.jackson.databind.ObjectMapper;
        import data.Response;
        import util.Protocol;

        import javax.websocket.ClientEndpoint;
        import javax.websocket.CloseReason;
        import javax.websocket.OnClose;
        import javax.websocket.OnError;
        import javax.websocket.OnMessage;
        import javax.websocket.OnOpen;
        import javax.websocket.Session;
        import java.io.IOException;
        import java.util.concurrent.ConcurrentHashMap;
        import java.util.concurrent.ConcurrentMap;

@ClientEndpoint
public class ClientHandler {
    Session session;

    public ClientHandler() {
    }

    @OnOpen
    public void onWebSocketConnect(Session sess) {
        System.out.println("Socket Connected: " + sess);
        this.session = sess;
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    @OnMessage(maxMessageSize = Protocol.MAX_MESSAGE_SIZE)
    public void onWebSocketText(String message) {
        // System.out.println("Received TEXT message: " + message);

        Response response;
        try {
            response = mapper.readValue(message, Response.class);
        } catch (IOException e) {
            System.err.println("Ignored malformed response: " + e.getClass().getSimpleName());
            return;
        }
        RequestHolder holder = WebSocketClient.removeRequest(response.getRequestId());
        if (holder != null) {
            holder.complete(response);
        }
    }

    @OnClose
    public void onWebSocketClose(Session closed, CloseReason reason) {
        System.out.println("Socket Closed: " + reason);
        // no response can arrive on this session any more
        WebSocketClient.failPending(closed,
                new IOException("Connection closed before the response arrived: " + reason));
    }

    @OnError
    public void onWebSocketError(Throwable cause) {
        cause.printStackTrace(System.err);
    }

  /*  public static void addRequestToWaitList(RequestFutureTask task, long id)
    {
        waitList.put(id,task);

    }

    static ConcurrentMap<Long,RequestFutureTask> waitList = new ConcurrentHashMap<>(); */

}
