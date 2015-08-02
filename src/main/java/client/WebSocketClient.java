package client;

        import com.fasterxml.jackson.databind.ObjectMapper;
        import data.Request;
        import util.IdCreator;

        import javax.websocket.*;
        import java.io.IOException;
        import java.net.URI;
        import java.util.*;


public class WebSocketClient {

    static WebSocketContainer container =  ContainerProvider.getWebSocketContainer();


    //URI uri = URI.create("ws://localhost:8080/healthcheck");

   // Map<String,SessionURITuple> sessions = new HashMap<>();

   // List<String> endPoints = new ArrayList<>();

    String host = "localhost";
    int port = 9090;

    String protocol = "ws";

    String endPoint = "/service/request";

   // List<String> handlerClasses;

  /*  public WebSocketConnectionAdapter(List<String> endPoints, List<String> handlerClasses, String host, int port, String protocol, String clientId, String secret)
    {

        this.endPoints = endPoints ;
        this.host = host;
        this.port = port;
        this.protocol = protocol ;
        this.clientId = clientId;
        this.secret = secret;
        this.handlerClasses = handlerClasses;

    } */

    static ThreadLocal<Session> localSession = new ThreadLocal<Session>();




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

        task.setRequestId(IdCreator.getId());


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





}



/*
class SessionURITuple
{
    Session session ;
    URI uri ;

    public SessionURITuple(Session session , URI uri)
    {
        this.session = session ;
        this.uri = uri;

    }
}
*/

