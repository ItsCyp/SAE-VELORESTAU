import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: java Main <confFile>");
            System.exit(1);
        }
        ServeurHttpImpl serv = new ServeurHttpImpl(args[0]);
        Registry reg = LocateRegistry.getRegistry("localhost", 1099);
        reg.rebind("serveur_api", (ServeurHttp) UnicastRemoteObject.exportObject(serv, 0));
        System.out.println("[Serveur] Serveur en cours...");

    }

}