package org.example.db;

import java.rmi.Naming;

public class TestClient {
    public static void main(String[] args) throws Exception {
        ServiceDb service = (ServiceDb) Naming.lookup("rmi://localhost/ServiceDb");
        String all = service.getRestaurants();
        System.out.println(all);

        String res = service.reserve(1, "Jean", "Dupont", "0612345678", 4);
        System.out.println(res);
    }
}