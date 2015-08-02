import client.WebSocketClient;
import data.Request;
import util.IdCreator;

/**
 * Created by mkhanwalkar on 8/1/15.
 */
public class ClientTester {

    public static void main(String[] args) throws Exception {
        WebSocketClient client = new WebSocketClient();

       // client.connect();

        for (int i=0;i<1;i++)
        {
            Thread t = new Thread(()->{

                Request request = new Request();
                request.setRequestId(IdCreator.getId());
                for (int j=0;j<1;j++) {
                    client.send(request);
                }

            });

            t.start();
        }




        Thread.sleep(30000);
    }
}
