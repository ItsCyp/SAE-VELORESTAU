public class TestDbConnection {
    public static void main(String[] args) {
        DatabaseManager manager = new DatabaseManager(args[0]);
        try (java.sql.Connection conn = manager.getConnection()) {
            System.out.println("Database connection OK: " + (conn != null));
            System.out.println("Connected as: " + conn.getMetaData().getUserName());
        } catch (Exception e) {
            System.err.println("Failed to connect to Oracle database");
            e.printStackTrace();
            return;
        }

        try {
            System.out.println("Retrieving restaurants from Oracle database...");
            ServiceDb service = new ServiceDbImpl(args[0]);
            String json = service.getRestaurants();
            org.json.JSONArray arr = new org.json.JSONArray(json);
            System.out.println("Retrieved " + arr.length() + " restaurants as JSON");
            System.out.println(json);
        } catch (Exception e) {
            System.err.println("Failed to retrieve restaurants from Oracle");
            e.printStackTrace();
        }
    }
}