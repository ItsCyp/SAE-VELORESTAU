import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestReservations {
    public static void main(String[] args) {
        DatabaseManager manager = new DatabaseManager();
        
        try (Connection conn = manager.getConnection()) {
            System.out.println("Connexion à la base de données établie");
            
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM reservations");
                 ResultSet rs = ps.executeQuery()) {
                
                System.out.println("\nListe des réservations :");
                System.out.println("------------------------");
                
                int count = 0;
                while (rs.next()) {
                    count++;
                    System.out.println("\nRéservation #" + count);
                    System.out.println("ID: " + rs.getInt("id"));
                    System.out.println("Restaurant ID: " + rs.getInt("restaurant_id"));
                    System.out.println("Client: " + rs.getString("first_name") + " " + rs.getString("last_name"));
                    System.out.println("Téléphone: " + rs.getString("phone"));
                    System.out.println("Nombre de personnes: " + rs.getInt("party_size"));
                    System.out.println("Date: " + rs.getTimestamp("reservation_time"));
                }
                
                if (count == 0) {
                    System.out.println("Aucune réservation trouvée dans la base de données.");
                } else {
                    System.out.println("\nTotal des réservations : " + count);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'accès à la base de données :");
            e.printStackTrace();
        }
    }
} 