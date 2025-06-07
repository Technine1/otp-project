package otp.util;

import java.security.MessageDigest;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordHasher {
    private static final Logger log = LoggerFactory.getLogger(PasswordHasher.class);

    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (Exception e) {
            log.error("Failed to hash password", e);
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    public static boolean check(String raw, String hashed) {
        return hash(raw).equals(hashed);
    }
}
