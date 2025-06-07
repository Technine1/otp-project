package otp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private static final Logger log = LoggerFactory.getLogger(PropertiesLoader.class);
    private static final Properties globalProps = new Properties();

    public static Properties load(String filename) {
        try (InputStream is = PropertiesLoader.class.getClassLoader().getResourceAsStream(filename)) {
            Properties fileProps = new Properties();
            fileProps.load(is);
            globalProps.putAll(fileProps);
            log.debug("Loaded {} properties from '{}'", fileProps.size(), filename);
            return fileProps;
        } catch (Exception e) {
            log.error("Failed to load properties from '{}': {}", filename, e.getMessage());
            throw new RuntimeException("Failed to load properties: " + filename, e);
        }
    }

    public static String get(String key) {
        String envValue = System.getenv(key);
        if (envValue != null) {
            log.debug("Loaded '{}' from environment variable", key);
            return envValue;
        }

        String fileValue = globalProps.getProperty(key);
        if (fileValue != null) {
            log.debug("Loaded '{}' from properties file", key);
        } else {
            log.warn("Property '{}' not found in environment or file", key);
        }
        return fileValue;
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }
}
