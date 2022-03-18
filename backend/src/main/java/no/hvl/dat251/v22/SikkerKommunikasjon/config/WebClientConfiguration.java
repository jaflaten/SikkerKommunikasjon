package no.hvl.dat251.v22.SikkerKommunikasjon.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class WebClientConfiguration {

    private static final String RESPONSE_MESSAGE = "Response {}: {}";

    @Bean(name = "WebClient")
    WebClient webClient(ObjectMapper mapper) {
        return WebClient.builder()
                .exchangeStrategies(getExchangeStategies(mapper))
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    private ExchangeStrategies getExchangeStategies(ObjectMapper mapper) {
        return ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(mapper));
                    config.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper));
                }).build();
    }
    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request {}: {} {}", clientRequest.logPrefix(), clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            switch (clientResponse.statusCode().series()) {
                case SUCCESSFUL:
                    log.info(RESPONSE_MESSAGE, clientResponse.logPrefix(), clientResponse.statusCode());
                    break;
                case INFORMATIONAL:
                case CLIENT_ERROR:
                    log.warn(RESPONSE_MESSAGE, clientResponse.logPrefix(), clientResponse.statusCode());
                    break;
                default:
                    log.error(RESPONSE_MESSAGE, clientResponse.logPrefix(), clientResponse.statusCode());
                    break;
            }
            return Mono.just(clientResponse);
        });
    }
}
