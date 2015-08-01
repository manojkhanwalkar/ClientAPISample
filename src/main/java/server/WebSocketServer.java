package server;


        import org.eclipse.jetty.server.Server;
        import org.eclipse.jetty.server.ServerConnector;
        import org.eclipse.jetty.servlet.ServletContextHandler;
        import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
        import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

public class WebSocketServer {
    public WebSocketServer() {
    }

    public static void main(String[] args) {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(9090);
        server.addConnector(connector);
        ServletContextHandler context = new ServletContextHandler(1);
        context.setContextPath("/");
        server.setHandler(context);

        try {
            ServerContainer t = WebSocketServerContainerInitializer.configureContext(context);
            t.addEndpoint(RequestHandler.class);

           // Handler.setProcessAdapter(new LMaxAdapter());
            // Handler.setProcessAdapter(new ThreadPoolAdapter());
           // Handler.setApphandler(new ApplicationHandlerImpl());

            server.start();
           // server.dump(System.err);
            server.join();
        } catch (Throwable var5) {
            var5.printStackTrace(System.err);
        }

    }
}