package server;

import javax.websocket.server.ServerEndpointConfig;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
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
                    origins.add(normalize(o));
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
        return allowedOrigins.contains(normalize(originHeaderValue));
    }

    /**
     * Canonical form of an origin, as browsers send it: lower case, no trailing slash, no default port.
     * So "https://App.example/" and "https://app.example:443" both match "https://app.example".
     */
    static String normalize(String origin) {
        String o = origin.trim().toLowerCase(Locale.ROOT);
        while (o.endsWith("/")) {
            o = o.substring(0, o.length() - 1);
        }
        try {
            URI uri = new URI(o);
            String scheme = uri.getScheme();
            if (scheme == null || uri.getHost() == null) {
                return o; // e.g. "null" from sandboxed pages: compared as-is
            }
            int port = uri.getPort();
            boolean defaultPort = port == -1
                    || (port == 443 && (scheme.equals("https") || scheme.equals("wss")))
                    || (port == 80 && (scheme.equals("http") || scheme.equals("ws")));
            return scheme + "://" + uri.getHost() + (defaultPort ? "" : ":" + port);
        } catch (URISyntaxException e) {
            return o;
        }
    }
}
