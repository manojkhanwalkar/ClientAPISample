import client.*;
import data.Request;
import util.IdCreator;

/**
 * Created by mkhanwalkar on 8/1/15.
 */
public class ClientTester {

    public static void main(String[] args) throws Exception {

        WebSocketClientFactory factory = (WebSocketClientFactory)ClientFactory.getFactory(FactoryType.WebSocket);

        WebSocketClient client = factory.getWebSocketClient("localhost",9090,"ws","/service/request");

        Notify notify =  (response) -> {

            System.out.println(response);
            System.out.println("$$$$$$$$$$$$$$$$$$$$$ Callback processed ");


        };


        for (int i=0;i<1;i++)
        {
            Thread t = new Thread(()->{

                Request request = new Request();
                request.setRequestId(IdCreator.getId());
                request.setPayLoad("HELLO WORLD FROM REQUEST");
                for (int j=0;j<1;j++) {
                    client.asend(request, notify);
                    client.send(request);
                }

            });

            t.start();
        }




        Thread.sleep(30000);
    }
}
