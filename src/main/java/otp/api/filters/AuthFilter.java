package otp.api.filters;

import com.sun.net.httpserver.*;
import otp.model.User;
import otp.service.UserServiceImpl;
import otp.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

public class AuthFilter extends Filter {
    private final String requiredRole;
    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    public AuthFilter(String requiredRole) {
        this.requiredRole = requiredRole;
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        log.debug("Checking Authorization header");
        String header = exchange.getRequestHeaders().getFirst("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header");
            sendError(exchange, 401, "Unauthorized: missing or malformed Authorization header");
            return;
        }

        String token = header.substring("Bearer ".length());
        try {
            User tokenUser = JwtUtil.decodeToken(token);
            User user = new UserServiceImpl().getUserByUsername(tokenUser.getUsername());
            if (!user.getRole().equalsIgnoreCase(requiredRole)) {
                log.warn("Access denied: user role '{}' does not match required '{}'", user.getRole(), requiredRole);
                sendError(exchange, 403, "Forbidden: insufficient role");
                return;
            }

            log.info("Authenticated user '{}', role='{}'", user.getUsername(), user.getRole());
            exchange.setAttribute("user", user);
            chain.doFilter(exchange);

        } catch (Exception e) {
            log.error("Token verification failed: {}", e.getMessage());
            sendError(exchange, 401, "Unauthorized: invalid token");
        }
    }

    @Override
    public String description() {
        return "JWT Authorization Filter";
    }

    private void sendError(HttpExchange exchange, int status, String message) throws IOException {
        byte[] bytes = message.getBytes();
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
