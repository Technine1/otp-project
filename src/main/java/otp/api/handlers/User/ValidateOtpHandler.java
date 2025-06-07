package otp.api.handlers.User;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import otp.model.User;
import otp.service.OtpServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ValidateOtpHandler implements HttpHandler {
    private final OtpServiceImpl otpService = new OtpServiceImpl();
    private static final Logger log = LoggerFactory.getLogger(ValidateOtpHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            String[] parts = reader.readLine().split(",");
            String operationId = parts[0].split("=")[1];
            String code = parts[1].split("=")[1];

            User user = (User) exchange.getAttribute("user");
            log.info("Validate OTP requested by userId={}, operationId={}", user.getId(), operationId);

            boolean valid = otpService.validateCode(user.getId(), operationId, code);
            log.info("OTP validation result: {}", valid);

            sendResponse(exchange, valid ? 200 : 403, valid ? "Code is valid" : "Code is invalid");
        } catch (Exception e) {
            log.error("OTP validation failed: {}", e.getMessage(), e);
            sendResponse(exchange, 500, "Internal Server Error");
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
