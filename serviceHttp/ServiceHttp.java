import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceHttp extends Remote {

    void init(String adresseProxy, int portProxy) throws RemoteException;

    void init() throws RemoteException;

    DataTransfer fetchAPI(String url) throws RemoteException;

}
