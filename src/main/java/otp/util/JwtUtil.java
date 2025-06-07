package otp.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import otp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class JwtUtil {
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    static {
        PropertiesLoader.load("jwt.properties");
    }

    private static final String SECRET = PropertiesLoader.get("JWT_SECRET", "default-secret");
    private static final long EXPIRATION_MS = Long.parseLong(
            PropertiesLoader.get("JWT_EXPIRATION_MS", "900000")); // 15 мин по умолчанию

    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);

    static {
        log.info("JWT initialized with expiration {} ms", EXPIRATION_MS);
    }

    public static String createToken(User user) {
        log.debug("Creating JWT token for user '{}', role '{}'", user.getUsername(), user.getRole());
        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("role", user.getRole())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .sign(ALGORITHM);
    }

    public static User decodeToken(String token) {
        try {
            var verifier = JWT.require(ALGORITHM).build();
            var decoded = verifier.verify(token);

            User user = new User();
            user.setUsername(decoded.getSubject());
            user.setRole(decoded.getClaim("role").asString());

            log.debug("Token decoded successfully for user '{}'", user.getUsername());
            return user;
        } catch (JWTVerificationException e) {
            log.error("JWT verification failed: {}", e.getMessage(), e);
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
}
