import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.util.HashMap;

public class ServeurHttpImpl implements ServeurHttp {

    private final HttpServer server;
    private ServiceHttp servicesHttp;
    HashMap<String, String> conf;

    // private ArrayList<ServiceBd> servicesBd;

    public ServeurHttpImpl(String confFile) throws IOException {
        this.getConf(confFile);
        this.server = HttpServer.create(new InetSocketAddress(Integer.parseInt(conf.get("port"))), 0);
        server.setExecutor(null);
        server.start();
        System.out.println("serveur démarré sur le port " + conf.get("port"));
    }

    @Override
    public void enregistrerServiceHttp(ServiceHttp serv) throws RemoteException {
        if (this.servicesHttp == null) {
            servicesHttp = serv;
            server.createContext("/api/meteo", new ApiHandler(serv, conf.get("meteo")));
            server.createContext("/api/accidents", new ApiHandler(serv, conf.get("accidents")));
        }
    }

    private void getConf(String confFile) throws IOException {
        HashMap<String, String> conf = new HashMap<>();
        BufferedReader buff = new BufferedReader(new FileReader(confFile));
        String line = buff.readLine();
        while (line != null) {
            String[] split = line.split(": ");
            conf.put(split[0], split[1]);
            line = buff.readLine();
        }
        buff.close();

        this.conf = conf;
    }

}
