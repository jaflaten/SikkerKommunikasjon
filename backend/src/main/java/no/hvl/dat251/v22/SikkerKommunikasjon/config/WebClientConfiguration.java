package no.hvl.dat251.v22.SikkerKommunikasjon.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {
    @Bean(name = "WebClient")
    WebClient webClient() {
        return WebClient.builder()
                .build();
    }
}
