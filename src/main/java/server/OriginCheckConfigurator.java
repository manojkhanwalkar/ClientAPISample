package server;

import javax.websocket.server.ServerEndpointConfig;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Rejects WebSocket handshakes from browser pages whose Origin is not explicitly allowed
 * (protects against cross-site WebSocket hijacking). Non-browser clients, which send no
 * Origin header, are accepted.
 */
public class OriginCheckConfigurator extends ServerEndpointConfig.Configurator {

    private final Set<String> allowedOrigins;

    /** Used by the container; reads ws.allowedOrigins / WS_ALLOWED_ORIGINS. */
    public OriginCheckConfigurator() {
        this(WebSocketServer.config("ws.allowedOrigins", "WS_ALLOWED_ORIGINS", ""));
    }

    public OriginCheckConfigurator(String commaSeparatedOrigins) {
        Set<String> origins = new HashSet<>();
        if (commaSeparatedOrigins != null) {
            for (String o : Arrays.asList(commaSeparatedOrigins.split(","))) {
                if (!o.trim().isEmpty()) {
                    origins.add(o.trim().toLowerCase());
                }
            }
        }
        this.allowedOrigins = Collections.unmodifiableSet(origins);
    }

    @Override
    public boolean checkOrigin(String originHeaderValue) {
        if (originHeaderValue == null || originHeaderValue.isEmpty()) {
            return true; // non-browser client
        }
        return allowedOrigins.contains(originHeaderValue.trim().toLowerCase());
    }
}
