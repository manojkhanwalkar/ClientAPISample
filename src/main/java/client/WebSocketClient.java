package client;

        import com.fasterxml.jackson.databind.ObjectMapper;
        import data.Request;
        import util.IdCreator;

        import javax.websocket.*;
        import java.io.IOException;
        import java.net.URI;
        import java.util.*;
        import java.util.concurrent.ConcurrentHashMap;
        import java.util.concurrent.ConcurrentMap;


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


    private Session connect() {
            try {
                URI uri = URI.create(protocol+"://"+host+":"+port+ endPoint);
                 Session session = container.connectToServer(new ClientHandler(), uri);
                 return session;

            } catch (DeploymentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;


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

    public void send(Request task) {

                try {
                    String e = mapper.writeValueAsString(task);

                    Session session = localSession.get();
                    if (session==null)
                    {
                        session = connect();
                        localSession.set(session);
                    }

                    session.getBasicRemote().sendText(e);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }


        }

    static ConcurrentMap<Long,RequestHolder> waitList = new ConcurrentHashMap<>();

    public static RequestHolder removeRequest(long requestId)
    {
        RequestHolder holder = waitList.remove(requestId);
        return holder;
    }


    public void asend(Request task, Notify notify) {

        try {
            String e = mapper.writeValueAsString(task);

            Session session = localSession.get();
            if (session==null)
            {
                session = connect();
                localSession.set(session);
            }

            waitList.put(task.getRequestId(),new RequestHolder(task,notify));

            session.getBasicRemote().sendText(e);
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }



}



