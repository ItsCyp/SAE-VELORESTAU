import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.security.KeyStore;
import java.util.HashMap;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

public class ServeurHttpImpl implements ServeurHttp {

    private final HttpsServer server;
    private ServiceHttp servicesHttp;
    private ServiceDb servicesDb;
    HashMap<String, String> conf;

    // private ArrayList<ServiceBd> servicesBd;

    public ServeurHttpImpl(String confFile) throws IOException {
        this.getConf(confFile);

        this.server = HttpsServer.create(new InetSocketAddress(Integer.parseInt(conf.get("port"))), 0);
        server.setExecutor(null);

        try {
            char[] passphrase = this.conf.get("certificate-password").toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(this.conf.get("certificate-path")), passphrase);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, passphrase);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            SSLContext ssl = SSLContext.getInstance("TLS");
            ssl.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            server.setHttpsConfigurator(new HttpsConfigurator(ssl) {
                public void configure(HttpsParameters params) {

                    // get the remote address if needed
                    // InetSocketAddress remote = params.getClientAddress();

                    SSLContext c = getSSLContext();

                    // get the default parameters
                    SSLParameters sslparams = c.getDefaultSSLParameters();
                    /*
                     * if (remote.equals (...) ) {
                     * // modify the default set for client x
                     * }
                     */

                    params.setSSLParameters(sslparams);
                    // statement above could throw IAE if any params invalid.
                    // eg. if app has a UI and parameters supplied by a user.

                }
            });

        } catch (Exception e) {
            System.out.println("euh y'a un problème Houston");
            e.printStackTrace();
        }

        server.start();
        System.out.println("serveur démarré sur le port " + conf.get("port"));
    }

    @Override
    public void enregistrerServiceHttp(ServiceHttp serv) throws RemoteException {
        System.out.println("enregistrement du service http");
        if (this.servicesHttp == null) {
            servicesHttp = serv;
            server.createContext("/api/meteo", new ApiHandler(serv, conf.get("meteo")));
            server.createContext("/api/accidents", new ApiHandler(serv, conf.get("accidents")));
        }
    }

    @Override
    public void enregistrerServiceDb(ServiceDb serv) throws RemoteException {
        System.out.println("enregistrement du service db");
        if (this.servicesDb == null) {
            servicesDb = serv;
            server.createContext("/api/restaurants", new RestaurantHandler(serv));
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
