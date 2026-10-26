sample code that shows how to create a client api on top of web sockets 

## Running

Build with `mvn package` (runs the unit tests). Start the server with `server.WebSocketServer`
and run `ClientTester` (in `src/test/java`) as a sample client.

Server configuration (Java system property, then environment variable, then default):

| Property / env var | Default | Meaning |
|---|---|---|
| `ws.host` / `WS_HOST` | `127.0.0.1` | Bind address. Loopback only by default; set to `0.0.0.0` to accept remote clients. |
| `ws.port` / `WS_PORT` | `9090` | Listen port. |
| `ws.allowedOrigins` / `WS_ALLOWED_ORIGINS` | _(empty)_ | Comma-separated browser `Origin` values allowed to connect. Clients that send no `Origin` header (non-browser clients such as `WebSocketClient`) are always accepted; browser pages from any other origin are rejected with 403. |

Messages larger than 64 KB are rejected (close code 1009) and idle connections are closed after 5 minutes.

The sample has no authentication and uses plain `ws://`. Before exposing it beyond localhost,
put it behind TLS (`wss://`) and add authentication.
