package com.devicecontrolapi.communication;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PasswordResetSender {

    private final RestTemplate restTemplate;

    public PasswordResetSender(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public void sendResetCode(String to, String username, String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();
        payload.put("subject", "Restablece tu contraseña");
        payload.put("templateName", "passwordResetDC.html");

        Map<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("resetCode", code);

        payload.put("dataTemplate", data);
        payload.put("to", to);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        String azureFunctionUrl = "https://funcion-device.azurewebsites.net/api/httptrigger1";
        restTemplate.postForEntity(azureFunctionUrl, request, String.class);
    }
}
