package client;

        import com.fasterxml.jackson.databind.ObjectMapper;
        import data.Response;

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

    @OnMessage
    public void onWebSocketText(String message) {
        // System.out.println("Received TEXT message: " + message);

        ObjectMapper mapper = new ObjectMapper();

        try {
            Response response = mapper.readValue(message, Response.class);
            long id = response.getRequestId();
 //           RequestFutureTask task = waitList.get(id);
   //         task.getProcessor().setResponse(response);
//
  //          task.run();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @OnClose
    public void onWebSocketClose(CloseReason reason) {
        System.out.println("Socket Closed: " + reason);
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
