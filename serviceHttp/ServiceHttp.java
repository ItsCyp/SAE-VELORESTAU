import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceHttp extends Remote {

    DataTransfer fetchAPI(String url) throws RemoteException;

}
