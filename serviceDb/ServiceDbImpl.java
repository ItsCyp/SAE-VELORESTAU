import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

public class ServiceDbImpl implements ServiceDb {
    private final DatabaseManager dbManager;

    protected ServiceDbImpl(String confFile) {
        this.dbManager = new DatabaseManager(confFile);
    }

    @Override
    public String getRestaurants() throws RemoteException {
        System.out.println("[ServiceDb] Récupération des restaurants");
        JSONArray array = new JSONArray();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT r.*, rt.id as table_id, rt.table_number, rt.seats " +
                     "FROM restaurants r " +
                     "LEFT JOIN restaurant_tables rt ON r.id = rt.restaurant_id " +
                     "ORDER BY r.id, rt.table_number")) {
            ResultSet rs = ps.executeQuery();
            int currentRestaurantId = -1;
            JSONObject currentRestaurant = null;
            JSONArray tables = null;

            while (rs.next()) {
                int restaurantId = rs.getInt("id");
                
                if (restaurantId != currentRestaurantId) {
                    if (currentRestaurant != null) {
                        currentRestaurant.put("tables", tables);
                        array.put(currentRestaurant);
                    }
                    
                    currentRestaurantId = restaurantId;
                    currentRestaurant = new JSONObject();
                    tables = new JSONArray();
                    
                    currentRestaurant.put("id", restaurantId);
                    currentRestaurant.put("name", rs.getString("name"));
                    currentRestaurant.put("address", rs.getString("address"));
                    currentRestaurant.put("latitude", rs.getDouble("latitude"));
                    currentRestaurant.put("longitude", rs.getDouble("longitude"));
                    currentRestaurant.put("phone", rs.getString("phone"));
                }

                if (rs.getInt("table_id") != 0) {
                    JSONObject table = new JSONObject();
                    table.put("id", rs.getInt("table_id"));
                    table.put("table_number", rs.getInt("table_number"));
                    table.put("seats", rs.getInt("seats"));
                    tables.put(table);
                }
            }
            
            if (currentRestaurant != null) {
                currentRestaurant.put("tables", tables);
                array.put(currentRestaurant);
            }
        } catch (SQLException e) {
            throw new RemoteException("SQL error", e);
        }
        return array.toString();
    }

    @Override
    public String reserve(int restaurantId, int tableId, String firstName, String lastName, String phone, int partySize, String reservationDate) throws RemoteException {
        System.out.println("[ServiceDb] Réservation pour le restaurant " + restaurantId + " et la table " + tableId);
        JSONObject result = new JSONObject();
        
        try {
            try (Connection conn = dbManager.getConnection()) {
                // Vérifier si la table est déjà réservée
                try (PreparedStatement checkPs = conn.prepareStatement(
                        "SELECT COUNT(*) FROM reservations WHERE restaurant_id = ? AND table_id = ? AND " +
                        "reservation_date BETWEEN TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS') - INTERVAL '2' HOUR " +
                        "AND TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS') + INTERVAL '2' HOUR")) {
                    checkPs.setInt(1, restaurantId);
                    checkPs.setInt(2, tableId);
                    checkPs.setString(3, reservationDate);
                    checkPs.setString(4, reservationDate);
                    
                    ResultSet rs = checkPs.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        result.put("status", "error");
                        result.put("message", "Cette table est déjà réservée pour cette période (dans un intervalle de 2 heures)");
                        return result.toString();
                    }
                }

                // Si la table est disponible, procéder à la réservation
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO reservations(restaurant_id, table_id, first_name, last_name, phone, party_size, reservation_date) VALUES(?,?,?,?,?,?,TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS'))")) {
                    ps.setInt(1, restaurantId);
                    ps.setInt(2, tableId);
                    ps.setString(3, firstName);
                    ps.setString(4, lastName);
                    ps.setString(5, phone);
                    ps.setInt(6, partySize);
                    ps.setString(7, reservationDate);
                    ps.executeUpdate();
                    result.put("status", "ok");
                    System.out.println("ok");
                }
            }
        // } catch (ParseException e) {
        //     result.put("status", "error");
        //     result.put("message", "Format de date invalide. Utilisez le format YYYY-MM-DD HH:mm (ex: 2024-03-20 19:30)");
        } catch (SQLException e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result.toString();
    }
}