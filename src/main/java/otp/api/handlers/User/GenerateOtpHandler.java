package otp.api.handlers.User;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import otp.model.User;
import otp.service.OtpServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class GenerateOtpHandler implements HttpHandler {
    private final OtpServiceImpl otpService = new OtpServiceImpl();
    private static final Logger log = LoggerFactory.getLogger(GenerateOtpHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            String[] parts = reader.readLine().split(",");
            String operationId = parts[0].split("=")[1];
            String channel = parts[1].split("=")[1];

            User user = (User) exchange.getAttribute("user");
            log.info("Generate OTP requested by userId={}, operationId={}, channel={}", user.getId(), operationId, channel);

            otpService.generateAndSendCode(user.getId(), operationId, channel);
            sendResponse(exchange, 200, "OTP generated");

        } catch (IllegalArgumentException e) {
            log.warn("Unknown delivery channel: {}", e.getMessage());
            sendResponse(exchange, 400, "Invalid channel: " + e.getMessage());

        } catch (Exception e) {
            log.error("Failed to generate OTP: {}", e.getMessage(), e);
            sendResponse(exchange, 500, "Failed to generate OTP");
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
