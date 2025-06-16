import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

class RestaurantHandler implements HttpHandler {

    private final ServiceDb serviceDb;

    public RestaurantHandler(ServiceDb service) {
        this.serviceDb = service;
    }

    public void handle(HttpExchange t) throws IOException {
        System.out.println("[RestaurantHandler] Nouvelle requête reçue : " + t.getRequestMethod() + " de " + t.getRemoteAddress().getAddress().toString());
        t.getResponseHeaders().add("Access-Control-Allow-Origin", t.getRequestHeaders().getFirst("Origin"));
        t.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
        t.getResponseHeaders().add("Content-Type", "application/json");
        
        try {
            String response = this.serviceDb.getRestaurants();
            if (response != null) {
                byte[] responseBytes = response.getBytes();
                t.sendResponseHeaders(200, responseBytes.length);
                OutputStream os = t.getResponseBody();
                os.write(responseBytes);
                os.close();
            } else {
                // Si pas de restaurants, on renvoie un tableau vide
                String emptyResponse = "[]";
                byte[] emptyResponseBytes = emptyResponse.getBytes();
                t.sendResponseHeaders(200, emptyResponseBytes.length);
                OutputStream os = t.getResponseBody();
                os.write(emptyResponseBytes);
                os.close();
            }
        } catch (Exception e) {
            System.out.println("[RestaurantHandler] Erreur lors de la récupération des restaurants : " + e.getMessage());
            e.printStackTrace();
            String errorResponse = "{\"error\": \"Une erreur est survenue lors de la récupération des restaurants\"}";
            t.sendResponseHeaders(500, errorResponse.length());
            OutputStream os = t.getResponseBody();
            os.write(errorResponse.getBytes());
            os.close();
        }
    }
} 