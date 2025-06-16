import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.NotBoundException;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                System.err.println("Usage: java Main <host>");
                System.exit(1);
            }

            ServiceDbImpl service = new ServiceDbImpl();
            Registry registry = LocateRegistry.getRegistry(args[0]);
            System.out.println("Connected to registry at: " + args[0]);

            ServeurHttp serveur = (ServeurHttp) registry.lookup("serveur_api");
            ServiceDb serviceDb = (ServiceDb) UnicastRemoteObject.exportObject(service, 0);

            serveur.enregistrerServiceDb(serviceDb);
            System.out.println("ServiceDb registered successfully");
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
