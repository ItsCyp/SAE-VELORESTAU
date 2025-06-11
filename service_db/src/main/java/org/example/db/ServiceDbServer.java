package org.example.db;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ServiceDbServer {
    public static void main(String[] args) throws Exception {
        DatabaseManager manager = new DatabaseManager();
        ServiceDb service = new ServiceDbImpl(manager);

        LocateRegistry.createRegistry(1099);
        Naming.rebind("ServiceDb", service);
        System.out.println("ServiceDb registered and ready");
    }
}