package ru.api.common;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class ExceptionHandler extends Filter {
    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        try {
            chain.doFilter(exchange);
        } catch (Exception e) {
            handleException(exchange, e);
        }
    }

    //todo handle exceptions
    private void handleException(HttpExchange exchange, Exception e) throws IOException {
        if (e instanceof IllegalArgumentException) {
            sendErrorResponse(exchange, 400, "Bad Request: " + e.getMessage());
//            sendErrorResponse(exchange, 401, "Unauthorized: " + e.getMessage());
        } else {
            sendErrorResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //todo requestHandler
    private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        String response = "{\"error\": \"" + message + "\"}";
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    @Override
    public String description() {
        return "Exception handler";
    }
}