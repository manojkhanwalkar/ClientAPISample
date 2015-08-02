package client;

public class WebSocketClientFactory implements Factory {


   private WebSocketClientFactory()
   {

   }

    public static WebSocketClientFactory getInstance() {


        return Holder.webFactory;

    }

    static class Holder {
        static WebSocketClientFactory webFactory = new WebSocketClientFactory();


    }


    public WebSocketClient getWebSocketClient(String host, int port , String protocol , String endPoint)
    {
        return new WebSocketClient(host,port,protocol,endPoint);
    }
}
