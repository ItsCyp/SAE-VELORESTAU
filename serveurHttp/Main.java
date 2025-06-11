import com.sun.net.httpserver.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

import static java.lang.module.ModuleDescriptor.read;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        ServeurHttpImpl serv = new ServeurHttpImpl(args[0]);
        Registry reg = LocateRegistry.getRegistry("localhost", 1099);
        reg.rebind("serveur_api", (ServeurHttp) UnicastRemoteObject.exportObject(serv, 0));
    }

}