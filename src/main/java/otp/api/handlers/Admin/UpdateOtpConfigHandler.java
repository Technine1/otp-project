package otp.api.handlers.Admin;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import otp.model.OtpConfig;
import otp.service.AdminServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class UpdateOtpConfigHandler implements HttpHandler {
    private final AdminServiceImpl adminService = new AdminServiceImpl();
    private static final Logger log = LoggerFactory.getLogger(UpdateOtpConfigHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            String[] parts = reader.readLine().split(",");
            int length = Integer.parseInt(parts[0].split("=")[1]);
            int ttl = Integer.parseInt(parts[1].split("=")[1]);

            log.info("Admin updates OTP config: codeLength={}, ttlSeconds={}", length, ttl);

            OtpConfig config = new OtpConfig();
            config.setCodeLength(length);
            config.setTtlSeconds(ttl);

            adminService.updateOtpConfig(config);
            sendResponse(exchange, 200, "Config updated");

        } catch (Exception e) {
            log.warn("Invalid config update attempt: {}", e.getMessage(), e);
            sendResponse(exchange, 400, "Invalid config format or failed update");
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
