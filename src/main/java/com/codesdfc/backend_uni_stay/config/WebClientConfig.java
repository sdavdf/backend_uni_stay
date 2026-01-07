package com.codesdfc.backend_uni_stay.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${recomm.base-url:http://localhost:8000}")
    private String recommBase;

    @Bean
    public WebClient recommClient() {
        return WebClient.builder()
                .baseUrl(recommBase)
                .build();
    }
}
