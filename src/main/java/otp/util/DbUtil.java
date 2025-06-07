package otp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbUtil {
    private static final String PROPERTIES_FILE = "db.properties";
    private static Connection connection;
    private static final Logger log = LoggerFactory.getLogger(DbUtil.class);

    public static Connection getConnection() {
        if (connection == null) {
            try {
                log.debug("Loading database configuration from db.properties");
                Properties props = PropertiesLoader.load(PROPERTIES_FILE);
                String url = props.getProperty("db.url");
                String user = props.getProperty("db.username");
                String pass = props.getProperty("db.password");
                log.info("Connecting to database: {}", url);
                connection = DriverManager.getConnection(url, user, pass);
            } catch (SQLException e) {
                log.error("Database connection failed: {}", e.getMessage());
                throw new RuntimeException("Failed to connect to database", e);
            }
        }
        return connection;
    }
}