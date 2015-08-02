package client;

public class ClientFactory {


    public static Factory getFactory(FactoryType name)
    {
        switch (name)
        {
            case WebSocket :
                return WebSocketClientFactory.getInstance();
            default :
                return null ; // for future expansion .
        }
    }


}
