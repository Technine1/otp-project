package otp.util;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtpCodeGenerator {
    private static final Random random = new Random();
    private static final Logger log = LoggerFactory.getLogger(OtpCodeGenerator.class);

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        log.info("New Otp Code generated");
        return sb.toString();
    }
}
