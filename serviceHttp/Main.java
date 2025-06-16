import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, NotBoundException {

        if (args.length < 2) {
            System.err.println("Usage: java Main <host> <confFile>");
            System.exit(1);
        }
        ServiceHttpImpl http = new ServiceHttpImpl(args[1]);

        Registry registry = LocateRegistry.getRegistry(args[0]);
        System.out.println("[ServiceHttp] Connecté au registry à l'adresse: " + args[0]);
        ServeurHttp serveur = (ServeurHttp) registry.lookup("serveur_api");

        ServiceHttp service = (ServiceHttp) UnicastRemoteObject.exportObject(http, 0);

        serveur.enregistrerServiceHttp(service);
        System.out.println("[ServiceHttp] ServiceHttp en cours...");

    }

    
}