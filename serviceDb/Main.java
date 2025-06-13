import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.NotBoundException;

public class Main {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        ServiceDb service = new ServiceDbImpl();

        Registry registry = LocateRegistry.getRegistry(args[0]);
        System.out.println("reg : " + String.valueOf(registry));

        ServeurHttp serveur = (ServeurHttp) registry.lookup("serveur_api");
        ServiceDb serviceDb = (ServiceDb) UnicastRemoteObject.exportObject(service, 0);

        serveur.enregistrerServiceDb(serviceDb);
    }

}
