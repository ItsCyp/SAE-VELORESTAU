import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, NotBoundException {
        ServiceHttpImpl http = new ServiceHttpImpl();

        Registry registry = LocateRegistry.getRegistry(args[0]);
        System.out.println("reg : " + String.valueOf(registry));
        ServeurHttp serveur = (ServeurHttp) registry.lookup("serveur_api");

        ServiceHttp service = (ServiceHttp) UnicastRemoteObject.exportObject(http, 0);

        serveur.enregistrerServiceHttp(service);

    }
}