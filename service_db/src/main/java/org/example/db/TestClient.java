package org.example.db;

import org.json.JSONArray;
import org.json.JSONObject;
import java.rmi.Naming;

public class TestClient {
    public static void main(String[] args) throws Exception {
        ServiceDb service = (ServiceDb) Naming.lookup("rmi://localhost/ServiceDb");


        // list registered services to ensure registration on central registry
        System.out.println("Services registered in RMI registry:");
        for (String name : Naming.list("rmi://localhost")) {
            System.out.println(" - " + name);
        }

        // Retrieve all restaurants in JSON format

        String all = service.getRestaurants();

        System.out.println("Received restaurants: " + all);
        JSONArray restaurants = new JSONArray(all);
        for (int i = 0; i < restaurants.length(); i++) {
            JSONObject r = restaurants.getJSONObject(i);
            System.out.printf("%d - %s at %s (%f,%f) phone %s%n",
                    r.getInt("id"),
                    r.getString("name"),
                    r.getString("address"),
                    r.getDouble("latitude"),
                    r.getDouble("longitude"),
                    r.getString("phone"));
        }

        // Reserve a table in the first restaurant returned
        if (!restaurants.isEmpty()) {
            int restId = restaurants.getJSONObject(0).getInt("id");
            String res = service.reserve(restId, "Jean", "Dupont", "0612345678", 4);
            System.out.println("Reservation response: " + res);
            JSONObject resp = new JSONObject(res);
            if (!"ok".equals(resp.optString("status"))) {
                throw new RuntimeException("Reservation failed: " + res);
            }
        }


        System.out.println("Client tests completed successfully.");
    }
}