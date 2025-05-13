package com.devicecontrolapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig { //Para el email sender

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
