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

        Request request = new Request();
        request.setRequestId(IdCreator.getId());

        client.send(request);


        Thread.sleep(10000);
    }
}
