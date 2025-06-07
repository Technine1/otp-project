package otp.api.handlers.Public;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import otp.service.UserServiceImpl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterHandler implements HttpHandler {
    private final UserServiceImpl userService = new UserServiceImpl();
    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        try (InputStream body = exchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(body, StandardCharsets.UTF_8))) {

            String[] parts = reader.readLine().split(",");
            String username = parts[0].split("=")[1];
            String password = parts[1].split("=")[1];
            String role = parts[2].split("=")[1];

            log.info("Registering user: {}", username);
            userService.register(username, password, role);

            sendResponse(exchange, 201, "User registered");

        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            log.warn("Malformed request: {}", e.getMessage());
            sendResponse(exchange, 400, e.getMessage());

        } catch (Exception e) {
            log.warn("User registration failed: {}", e.getMessage());
            sendResponse(exchange, 400, e.getMessage());
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] response = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }
}
