package com.devicecontrolapi.communication;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PasswordResetSender {

    private final RestTemplate restTemplate;

    @Autowired
    public PasswordResetSender(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendResetCode(String to, String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();
        payload.put("subject", "Restablece tu contraseña");
        payload.put("templateName", "template_password_reset.html");

        Map<String, String> data = new HashMap<>();
        data.put("username", to);
        data.put("resetCode", code);

        payload.put("dataTemplate", data);
        payload.put("to", to);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        String azureFunctionUrl = "https://<TU-AZURE-FUNCTION-URL>";
        restTemplate.postForEntity(azureFunctionUrl, request, String.class);
    }

    @Configuration
    public class RestTemplateConfig {

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }

}
