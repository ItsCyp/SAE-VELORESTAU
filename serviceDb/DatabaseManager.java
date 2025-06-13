import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:oracle:thin:@charlemagne.iutnc.univ-lorraine.fr:1521:infodb";
    private static final String DB_USER = "e0617u";
    private static final String DB_PASSWORD = "1234abcd&";

    public DatabaseManager() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Oracle JDBC Driver loaded successfully.");
            // Pas d'init ici, juste charger le driver
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Oracle JDBC Driver not found. Please add it to your classpath.", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}