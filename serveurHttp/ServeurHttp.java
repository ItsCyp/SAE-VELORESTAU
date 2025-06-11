import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServeurHttp extends Remote {

    void enregistrerServiceHttp(ServiceHttp serv) throws RemoteException;

    // void enregistrerServiceBd(ServiceBd serv)

}
