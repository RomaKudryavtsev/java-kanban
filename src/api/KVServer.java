package api;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.google.gson.Gson;

public class KVServer {
    public static final int PORT = 8078;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();
    private final Gson gson;

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
        server.createContext("/clear", this::clear);
        gson = new Gson();
    }

    private void clear(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/clear");
            if (!hasAuth(h)) {
                System.out.println("Request is not authorized, API_TOKEN is needed");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("DELETE".equals(h.getRequestMethod())) {
                data.clear();
                h.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/clear awaits for DELETE-request, but received: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void load(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/load");
            if (!hasAuth(h)) {
                System.out.println("Request is not authorized, API_TOKEN is needed");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/load/".length());
                if (key.isEmpty()) {
                    System.out.println("Key for loading is empty. key is specified in: /load/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                if (!data.keySet().contains(key)) {
                    h.sendResponseHeaders(404, 0);
                    System.out.println(String.format("Key %s was not created", key));
                    return;
                }
                String response = data.get(key);
                h.sendResponseHeaders(200, 0);
                try (OutputStream os = h.getResponseBody()) {
                    String jsonResponse = gson.toJson(response);
                    os.write(jsonResponse.getBytes());

                }
            } else {
                System.out.println("/load awaits for GET-request, but received: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void save(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/save");
            if (!hasAuth(h)) {
                System.out.println("Request is not authorized, API_TOKEN is needed");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    System.out.println("Key for saving is empty. key is specified in: /save/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(h);
                if (value.isEmpty()) {
                    System.out.println("Value for saving is empty. value has to be specified in request body");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                System.out.println("Value for key " + key + " is successfully updated!");
                h.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/save awaits for POST-request, but received: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void register(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/register");
            if ("GET".equals(h.getRequestMethod())) {
                //apiToken = generateApiToken();
                sendText(h, apiToken);
            } else {
                System.out.println("/register awaits for GET-request, but received " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    public void start() {
        System.out.println("Server is started on port " + PORT);
        System.out.println("Open in browser http://localhost:" + PORT + "/");
        System.out.println("API_TOKEN: " + apiToken);
        server.start();
    }

    public void stop() {
        System.out.println("Server is stopped on " + PORT);
        server.stop(0);
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}

