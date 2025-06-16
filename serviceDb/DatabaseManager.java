import java.sql.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseManager {
    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    public DatabaseManager(String confFile) {
        try {
            // Charger le driver Oracle
            Class.forName("oracle.jdbc.driver.OracleDriver");
            
            // Charger la configuration depuis le fichier
            loadConfiguration(confFile);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Oracle JDBC Driver not found. Please add it to your classpath.", e);
        } catch (IOException e) {
            throw new RuntimeException("Error loading configuration file.", e);
        }
    }

    private void loadConfiguration(String confFile) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(confFile)) {
            properties.load(fis);
            dbUrl = properties.getProperty("db_url");
            dbUser = properties.getProperty("db_user");
            dbPassword = properties.getProperty("db_password");
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
}