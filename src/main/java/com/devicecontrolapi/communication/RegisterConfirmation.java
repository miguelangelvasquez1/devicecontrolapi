package com.devicecontrolapi.communication;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class RegisterConfirmation {

    private static final String SERVICE_URL = "https://function-192-adso.azurewebsites.net/api/httptrigger1";

    @Async
    public void realizarLlamadaHttp(String email, String nombre, String rol) {
        try {
            @SuppressWarnings("deprecation")
            URL url = new URL(SERVICE_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            String plantilla = "confirmationTemplate.html";
            String jsonInputString;

            if (plantilla.equals("confirmationTemplate.html")) {
                jsonInputString = "{"
                    + "\"subject\": \"¡Gracias por registrarse!\","
                    + "\"to\": \"" + email +"\","
                    + "\"dataTemplate\": {"
                    + "    \"nombre\": \"" + nombre + "\","
                    + "    \"role\": \"" + rol + "\""
                    + "},"
                    + "\"templateName\": \"" + plantilla + "\""
                    + "}";
            } else {
                jsonInputString = "{}"; // fallback
            }

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Opcional: leer respuesta si la necesitas
            } else {
                System.err.println("Fallo al enviar correo. Código: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error al enviar correo de confirmación: " + e.getMessage());
        }
    }
}
