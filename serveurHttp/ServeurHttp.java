import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServeurHttp extends Remote {

    void enregistrerServiceHttp(ServiceHttp serv) throws RemoteException;

    void enregistrerServiceDb(ServiceDb serv) throws RemoteException;

}
