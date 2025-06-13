import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

public class ServiceDbImpl extends UnicastRemoteObject implements ServiceDb {
    private final DatabaseManager dbManager;

    protected ServiceDbImpl() throws RemoteException {
        super();
        this.dbManager = new DatabaseManager();
    }

    @Override
    public String getRestaurants() throws RemoteException {
        JSONArray array = new JSONArray();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id,name,address,latitude,longitude,phone FROM restaurants")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject o = new JSONObject();
                o.put("id", rs.getInt("id"));
                o.put("name", rs.getString("name"));
                o.put("address", rs.getString("address"));
                o.put("latitude", rs.getDouble("latitude"));
                o.put("longitude", rs.getDouble("longitude"));
                o.put("phone", rs.getString("phone"));
                array.put(o);
            }
        } catch (SQLException e) {
            throw new RemoteException("SQL error", e);
        }
        return array.toString();
    }

    @Override
    public String reserve(int restaurantId, String firstName, String lastName, String phone, int partySize) throws RemoteException {
        JSONObject result = new JSONObject();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO reservations(restaurant_id,first_name,last_name,phone,party_size) VALUES(?,?,?,?,?)")) {
            ps.setInt(1, restaurantId);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, phone);
            ps.setInt(5, partySize);
            ps.executeUpdate();
            result.put("status", "ok");
        } catch (SQLException e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result.toString();
    }
}