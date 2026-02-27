package com.openlms.api.commons.configs;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class XenditConfig {
    @Value("${xendit.secret-key}")
    private String secretKey;

    @Bean
    public RestTemplate xenditRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            String credentials = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes());
            request.getHeaders().set("Authorization", "Basic " + credentials);
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
