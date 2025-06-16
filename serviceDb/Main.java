import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.NotBoundException;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                System.err.println("Usage: java Main <host> <confFile>");
                System.exit(1);
            }

            ServiceDbImpl service = new ServiceDbImpl(args[1]);
            Registry registry = LocateRegistry.getRegistry(args[0]);
            System.out.println("[ServiceDb] Connected to registry at: " + args[0]);

            ServeurHttp serveur = (ServeurHttp) registry.lookup("serveur_api");
            ServiceDb serviceDb = (ServiceDb) UnicastRemoteObject.exportObject(service, 0);

            serveur.enregistrerServiceDb(serviceDb);
            System.out.println("[ServiceDb] ServiceDb en cours...");
        } catch (RemoteException e) {
            System.err.println("Remote error: " + e.getMessage());
            e.printStackTrace();
        } catch (NotBoundException e) {
            System.err.println("Service not found: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
