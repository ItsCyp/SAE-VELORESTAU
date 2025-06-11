import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface ServiceHttp extends Remote {

    void init(String adresseProxy, int portProxy) throws RemoteException;

    void init() throws RemoteException;

    HashMap<Integer, String> fetchAPI(String url) throws RemoteException;

}
