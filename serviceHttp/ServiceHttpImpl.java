import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.rmi.RemoteException;
import java.time.Duration;
import java.util.HashMap;

public class ServiceHttpImpl implements ServiceHttp {

    private HttpClient httpClient;
    private HashMap<String, String> conf;

    public ServiceHttpImpl(String config){
        this.conf = new HashMap<>();
        try{
            this.getConf(config);
        } catch(IOException e){
            e.printStackTrace();
        }

        if(this.conf.containsKey("proxy-address")){
            this.init(conf.get("proxy-address"), Integer.parseInt(conf.get("proxy-port")));
        } else {
            this.init();
        }
    }

    public void init(String adresseProxy, int portProxy) {

        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .proxy(ProxySelector.of(new InetSocketAddress(adresseProxy, portProxy)))
                .build();
    }

    public void init() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    public DataTransfer fetchAPI(String url) throws RemoteException {
        // System.out.println("ca passe dans fetchAPI");
        if (httpClient == null) {
            System.err.println(
                    "∑x3 bip boup, utilisation d'un client par défaut, risque d'erreur dû à l'absence de proxy ");
            init();
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        DataTransfer response = null;
        try {
            HttpResponse<String> cl = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int len = Integer.parseInt(cl.headers().allValues("Content-Length").getFirst());
            response = new DataTransfer(len, cl.body());
        } catch (IOException | InterruptedException e) {
            System.err.println("∑x3 bip boup, y'a une erreur ");
            e.printStackTrace();
        }
        // System.out.println("ca renvoie le résultat du fecth");
        return response;
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
