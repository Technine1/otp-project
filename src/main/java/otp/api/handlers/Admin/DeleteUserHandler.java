package otp.api.handlers.Admin;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import otp.service.AdminServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class DeleteUserHandler implements HttpHandler {
    private final AdminServiceImpl adminService = new AdminServiceImpl();
    private static final Logger log = LoggerFactory.getLogger(DeleteUserHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            int userId = Integer.parseInt(reader.readLine().split("=")[1]);
            log.info("Admin requests deletion of userId={}", userId);

            adminService.deleteUserWithCodes(userId);
            sendResponse(exchange, 200, "User deleted");

        } catch (Exception e) {
            log.warn("Failed to delete user: {}", e.getMessage(), e);
            sendResponse(exchange, 400, "Failed to delete user");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] response = message.getBytes();
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }
}
