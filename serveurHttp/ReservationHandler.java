import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

class ReservationHandler implements HttpHandler {

    private final ServiceDb serviceDb;

    public ReservationHandler(ServiceDb service) {
        this.serviceDb = service;
    }

    public void handle(HttpExchange t) throws IOException {
        System.out.println("[ReservationHandler] Nouvelle requête reçue : " + t.getRequestMethod() + " de " + t.getRemoteAddress().getAddress().toString());
        
        // Configuration des headers CORS
        t.getResponseHeaders().add("Access-Control-Allow-Origin", t.getRequestHeaders().getFirst("Origin"));
        t.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
        t.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        t.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        t.getResponseHeaders().add("Content-Type", "application/json");

        // Gestion de la requête OPTIONS (preflight)
        if (t.getRequestMethod().equals("OPTIONS")) {
            System.out.println("[ReservationHandler] Requête OPTIONS reçue, envoi des headers CORS");
            t.sendResponseHeaders(200, -1);
            t.close();
            return;
        }

        if (!t.getRequestMethod().equals("POST")) {
            System.out.println("[ReservationHandler] Méthode non autorisée : " + t.getRequestMethod());
            String response = "{\"error\": \"Méthode non autorisée\"}";
            t.sendResponseHeaders(405, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            return;
        }

        try {
            // Lire le corps de la requête
            InputStream requestBody = t.getRequestBody();
            Scanner scanner = new Scanner(requestBody).useDelimiter("\\A");
            String requestData = scanner.hasNext() ? scanner.next() : "";
            System.out.println("[ReservationHandler] Corps de la requête : " + requestData);

            // Extraire les données nécessaires
            int restaurantId = extractIntValue(requestData, "restaurantId");
            int tableId = extractIntValue(requestData, "tableId");
            int partySize = extractIntValue(requestData, "party_size");
            String firstName = extractStringValue(requestData, "first_name");
            String lastName = extractStringValue(requestData, "last_name");
            String phone = extractStringValue(requestData, "phone");
            String date = extractStringValue(requestData, "date");
            String time = extractStringValue(requestData, "time");
            
            System.out.println("[ReservationHandler] Données extraites : restaurantId=" + restaurantId + 
                             ", tableId=" + tableId + 
                             ", partySize=" + partySize + 
                             ", firstName=" + firstName + 
                             ", lastName=" + lastName + 
                             ", phone=" + phone + 
                             ", date=" + date + 
                             ", time=" + time);
            
            // Appeler le service de réservation
            String response = this.serviceDb.reserve(restaurantId, tableId, firstName, lastName, phone, partySize, date, time);
            System.out.println("[ReservationHandler] Réponse du serviceDb : " + response);
            
            // Vérifier si la réservation a réussi
            if (response.contains("\"status\":\"ok\"")) {
                t.sendResponseHeaders(200, response.length());
            } else {
                System.out.println("[ReservationHandler] Conflit lors de la réservation (409)");
                t.sendResponseHeaders(409, response.length()); // 409 Conflict
            }
            
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();

        } catch (Exception e) {
            System.out.println("[ReservationHandler] Erreur lors du traitement de la réservation : " + e.getMessage());
            e.printStackTrace();
            String errorResponse = "{\"error\": \"Une erreur est survenue lors de la réservation\"}";
            t.sendResponseHeaders(500, errorResponse.length());
            OutputStream os = t.getResponseBody();
            os.write(errorResponse.getBytes());
            os.close();
        }
    }

    private int extractIntValue(String json, String key) {
        String pattern = "\"" + key + "\":\\s*(\\d+)";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        throw new IllegalArgumentException("Clé non trouvée: " + key);
    }

    private String extractStringValue(String json, String key) {
        String pattern = "\"" + key + "\":\\s*\"([^\"]+)\"";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        throw new IllegalArgumentException("Clé non trouvée: " + key);
    }
} 