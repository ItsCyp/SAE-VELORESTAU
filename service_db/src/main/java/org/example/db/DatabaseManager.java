package org.example.db;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/velorestau";
    private static final String DB_USER = "myuser";
    private static final String DB_PASSWORD = "mdp";

    public DatabaseManager() {
        try {
            init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS restaurants(" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "name VARCHAR(255) NOT NULL," +
                        "address VARCHAR(255) NOT NULL," +
                        "latitude DOUBLE NOT NULL," +
                        "longitude DOUBLE NOT NULL," +
                        "phone VARCHAR(32) NOT NULL" +
                        ")");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS reservations(" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "restaurant_id INTEGER NOT NULL," +
                        "first_name VARCHAR(100) NOT NULL," +
                        "last_name VARCHAR(100) NOT NULL," +
                        "phone VARCHAR(32) NOT NULL," +
                        "party_size INTEGER NOT NULL," +
                        "reservation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY(restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE" +
                        ")");
            }

            // insert sample data if table empty
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM restaurants")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    try (PreparedStatement insert = conn.prepareStatement(
                            "INSERT INTO restaurants(name,address,latitude,longitude,phone) VALUES(?,?,?,?,?)")) {
                        insert.setString(1, "Le Gourmet");
                        insert.setString(2, "10 Rue des Ponts, 54000 Nancy");
                        insert.setDouble(3, 48.6937);
                        insert.setDouble(4, 6.1834);
                        insert.setString(5, "03 83 11 22 33");
                        insert.executeUpdate();

                        insert.setString(1, "Bistro des Arts");
                        insert.setString(2, "5 Place Stanislas, 54000 Nancy");
                        insert.setDouble(3, 48.6933);
                        insert.setDouble(4, 6.1839);
                        insert.setString(5, "03 83 44 55 66");
                        insert.executeUpdate();

                        insert.setString(1, "La Table Lorraine");
                        insert.setString(2, "15 Rue Saint-Georges, 54000 Nancy");
                        insert.setDouble(3, 48.6920);
                        insert.setDouble(4, 6.1836);
                        insert.setString(5, "03 83 77 88 99");
                        insert.executeUpdate();

                        insert.setString(1, "Chez Luigi");
                        insert.setString(2, "20 Rue des Dominicains, 54000 Nancy");
                        insert.setDouble(3, 48.6931);
                        insert.setDouble(4, 6.1825);
                        insert.setString(5, "03 83 12 34 56");
                        insert.executeUpdate();

                        insert.setString(1, "L'Estaminet");
                        insert.setString(2, "8 Rue des Soeurs Macarons, 54000 Nancy");
                        insert.setDouble(3, 48.6925);
                        insert.setDouble(4, 6.1842);
                        insert.setString(5, "03 83 98 76 54");
                        insert.executeUpdate();
                    }
                }
            }
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}