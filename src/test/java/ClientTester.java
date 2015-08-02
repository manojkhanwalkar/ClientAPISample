import client.*;
import data.Request;
import data.Response;
import util.IdCreator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
                for (int j=0;j<1;j++) {
                Request request = new Request();
                request.setRequestId(IdCreator.getId());
                request.setPayLoad("HELLO WORLD FROM REQUEST");
                    client.asend(request, notify);

                    Request request1 = new Request();
                    request1.setRequestId(IdCreator.getId());
                    request1.setPayLoad("HELLO WORLD FROM REQUEST");
                    Future<Response> f = client.send(request1);

                    try {
                        Response response = f.get();

                        System.out.println(response);
                        System.out.println("####################### Callback processed ");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                }

            });

            t.start();
        }




        Thread.sleep(30000);
    }
}
