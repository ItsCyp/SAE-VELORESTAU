import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.rmi.RemoteException;
import java.time.Duration;
import java.util.HashMap;

import javax.xml.crypto.Data;

public class ServiceHttpImpl implements ServiceHttp {

    private HttpClient httpClient;

    @Override
    public void init(String adresseProxy, int portProxy) throws RemoteException {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .proxy(ProxySelector.of(new InetSocketAddress(adresseProxy, portProxy)))
                .authenticator(Authenticator.getDefault())
                .build();
    }

    @Override
    public void init() throws RemoteException {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    @Override
    public DataTransfer fetchAPI(String url) throws RemoteException {
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
        return response;
    }
}
