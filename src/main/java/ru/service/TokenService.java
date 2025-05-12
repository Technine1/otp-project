package ru.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import ru.model.Role;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TokenService {
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expirationMinutes;

    public TokenService(String secret, long expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).build();
    }

    public String generateToken(String username, Role role) {
        return JWT.create()
                .withSubject(username)
                .withClaim("role", role.name())
                .withExpiresAt(Date.from(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES)))
                .sign(algorithm);
    }

    public boolean validateToken(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }

    public Role getRoleFromToken(String token) {
        DecodedJWT jwt = verifier.verify(token);
        return Role.valueOf(jwt.getClaim("role").asString());
    }
}
