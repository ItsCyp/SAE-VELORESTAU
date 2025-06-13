public class TestDbConnection {
    public static void main(String[] args) {
        DatabaseManager manager = new DatabaseManager();
        try (java.sql.Connection conn = manager.getConnection()) {
            System.out.println("Database connection OK: " + (conn != null));
        } catch (Exception e) {
            System.err.println("Failed to connect to database");
            e.printStackTrace();
            return;
        }

        try {
            ServiceDb service = new ServiceDbImpl();
            String json = service.getRestaurants();
            org.json.JSONArray arr = new org.json.JSONArray(json);
            System.out.println("Retrieved " + arr.length() + " restaurants as JSON");
            System.out.println(json);
        } catch (Exception e) {
            System.err.println("Failed to retrieve restaurants");
            e.printStackTrace();
        }
    }
}