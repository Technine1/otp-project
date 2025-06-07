package otp.util;

import java.io.FileWriter;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCodeWriter {
    private static final Logger log = LoggerFactory.getLogger(FileCodeWriter.class);

    public static void save(String code, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("OTP Code: " + code);
            log.info("OTP code written to file '{}'", filename);
        } catch (IOException e) {
            log.error("Failed to write OTP code to file '{}': {}", filename, e.getMessage(), e);
            throw new RuntimeException("Failed to write code to file", e);
        }
    }
}
