import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.http.HttpResponse;
import java.util.HashMap;

class ApiHandler implements HttpHandler {

    private final String apiUrl;
    private final ServiceHttp serviceHttp;

    public ApiHandler(ServiceHttp service, String apiUrl) {
        this.apiUrl = apiUrl;
        this.serviceHttp = service;
    }

    public void handle(HttpExchange t) throws IOException {
        System.out.println("yohoho !");
        t.getResponseHeaders().add("Access-Control-Allow-Origin", t.getRequestHeaders().getFirst("Origin"));
        t.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
        HashMap<Integer, String> response = this.serviceHttp.fetchAPI(apiUrl);
        // normalement y'a qu'une clé mais c'est pour la récupérer
        for (int i : response.keySet()) {
            t.sendResponseHeaders(200, i);
            OutputStream os = t.getResponseBody();
            os.write(response.get(i).getBytes());
            os.close();
        }

    }
}
