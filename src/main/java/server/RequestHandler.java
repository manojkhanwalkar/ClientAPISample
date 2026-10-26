package server;

        import com.fasterxml.jackson.databind.ObjectMapper;
        import data.Request;
        import data.Response;

        import javax.websocket.*;
        import javax.websocket.server.ServerEndpoint;
        import java.io.IOException;
        import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;
        import java.util.concurrent.atomic.AtomicInteger;

// Browser Origins are checked by OriginCheckConfigurator; message size and idle timeout are bounded.
@ServerEndpoint(value = "/service/request", configurator = OriginCheckConfigurator.class)
public class RequestHandler   {
    Session session;

    static ObjectMapper mapper = new ObjectMapper();



    public RequestHandler() {
    }



    @OnOpen
    public void onWebSocketConnect(Session sess) {
        System.out.println("Socket Connected: " + sess);
        this.session = sess;
    }


    //TODO - async handling on client and server side
    // TODO - future impl required on the client side .


    @OnMessage(maxMessageSize = WebSocketServer.MAX_MESSAGE_SIZE)
    public void onWebSocketText(String message) {
        // Do not log the raw payload: it is untrusted (log injection) and may carry sensitive data.
        System.out.println(Thread.currentThread().getName() + " received message of " + message.length() + " chars");

        try {
            Request request = mapper.readValue(message,Request.class);
            Response response = new Response();

            response.setRequestId(request.getRequestId());
            response.setPayLoad("HELLO WORLD BACK FROM RESPONSE");
            synchronized (session) {
                session.getBasicRemote().sendText(mapper.writeValueAsString(response));
            }
        } catch (IOException e) {
            System.err.println("Rejected malformed request: " + e.getClass().getSimpleName());
        }
        //     processAdapter.process(new RequestHandlerImpl(appHandler,message,session));

    }

    @OnClose
    public void onWebSocketClose(CloseReason reason) {
        System.out.println("Socket Closed: " + reason);
    }

    @OnError
    public void onWebSocketError(Throwable cause) {
        cause.printStackTrace(System.err);
    }






}