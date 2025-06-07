package otp.api.handlers.Public;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import otp.service.AuthServiceImpl;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class LoginHandler implements HttpHandler {
    private final AuthServiceImpl authService = new AuthServiceImpl();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            String[] parts = reader.readLine().split(",");
            String username = parts[0].split("=")[1];
            String password = parts[1].split("=")[1];

            String token = authService.login(username, password);

            // Формируем JSON-ответ
            String json = String.format("""
                {
                  "token": "%s",
                  "type": "Bearer",
                  "expiresIn": 900
                }
                """, token);

            sendJson(exchange, 200, json);

        } catch (Exception e) {
            sendJson(exchange, 401, "{\"error\":\"Invalid credentials\"}");
        }
    }

    private void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
