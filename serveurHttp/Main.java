import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Main {
    public static void main(String[] args) throws IOException {
        ServeurHttpImpl serv = new ServeurHttpImpl(args[0]);
        Registry reg = LocateRegistry.getRegistry("localhost", 1099);
        reg.rebind("serveur_api", (ServeurHttp) UnicastRemoteObject.exportObject(serv, 0));
    }

}