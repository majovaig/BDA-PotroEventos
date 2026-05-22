/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexion;

import excepciones.ZeroBounceException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 *
 * @author aaron
 */
public class Conexion {
    // protocolo por el que viajan los datos
    private final HttpClient httpClient;
    private static final String API_KEY = "llave"; // mi clave ultra secreta
    private static final String BASE_URL = "https://api.zerobounce.net/v2/validate"; // la url de la api

    public Conexion() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5)) // un maximo de espera de 5 segundos
                .build();
    }

    public String ejecutarValidacion(String email) throws ZeroBounceException {
        try {
            String url = String.format("%s?api_key=%s&email=%s&ip_address=", BASE_URL, API_KEY, email); // el formato de comunicacion con la api
            
            HttpRequest request = HttpRequest.newBuilder() // inicia la configuración de la petición HTTP
                    .uri(URI.create(url)) // asigna la direccion donde se hace la peticion
                    .GET() // es para obtener, mera inspeccion
                    .timeout(Duration.ofSeconds(5))
                    .build(); // Contruye la solicitud

            // la respuesta la recibirá en tipo de dato string, el body handler 
            // recibe los bytes y en este caso los transforma en texto
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            // recibe un status, que el status son como codigos que manda dependiendo la respuesta
            // o el resultado de la busqueda/operacion
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new ZeroBounceException("Error de API ZeroBounce. Código HTTP: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new ZeroBounceException("Fallo de conexión de red con ZeroBounce");
        }
    }
}
