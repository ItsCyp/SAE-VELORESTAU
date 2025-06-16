import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceDb extends Remote {
    String getRestaurants() throws RemoteException;
    String reserve(int restaurantId, int tableId, String firstName, String lastName, String phone, int partySize) throws RemoteException;
}