package otp.api.handlers.Admin;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import otp.model.User;
import otp.service.AdminServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ListUsersHandler implements HttpHandler {
    private final AdminServiceImpl adminService = new AdminServiceImpl();
    private static final Logger log = LoggerFactory.getLogger(ListUsersHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        log.info("Admin requested user list");

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            List<User> users = adminService.getAllNonAdminUsers();
            log.info("Users returned: {}", users.size());

            StringBuilder sb = new StringBuilder();
            for (User u : users) {
                sb.append(u.getId()).append(", ").append(u.getUsername()).append("\n");
            }

            sendResponse(exchange, 200, sb.toString());

        } catch (Exception e) {
            log.error("Failed to return user list: {}", e.getMessage(), e);
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] bytes = message.getBytes();
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
