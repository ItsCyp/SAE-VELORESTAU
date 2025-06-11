package org.example.db;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceDb extends Remote {
    /** Return JSON containing all restaurant coordinates */
    String getRestaurants() throws RemoteException;

    /** Reserve table for a restaurant. Returns JSON response */
    String reserve(int restaurantId, String firstName, String lastName, String phone, int partySize) throws RemoteException;
}