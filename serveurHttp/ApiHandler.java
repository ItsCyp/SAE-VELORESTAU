import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

class ApiHandler implements HttpHandler {

    private final String apiUrl;
    private final ServiceHttp serviceHttp;

    public ApiHandler(ServiceHttp service, String apiUrl) {
        this.apiUrl = apiUrl;
        this.serviceHttp = service;
    }

    public void handle(HttpExchange t) throws IOException {
        System.out.println("[ApiHandler] Requête reçue : " + t.getRequestMethod() + " de " + t.getRemoteAddress().getAddress().toString());
        t.getResponseHeaders().add("Access-Control-Allow-Origin", t.getRequestHeaders().getFirst("Origin"));
        t.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
        DataTransfer response = this.serviceHttp.fetchAPI(apiUrl);
        if (response != null) {
            t.sendResponseHeaders(200, response.getDataLength());
            OutputStream os = t.getResponseBody();
            os.write(response.getData().getBytes());
            os.close();
        }

    }
}
