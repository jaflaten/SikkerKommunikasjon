package no.hvl.dat251.v22.SikkerKommunikasjon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.hvl.dat251.v22.SikkerKommunikasjon.config.SikkerKommunikasjonProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntegrasjonspunktService {

    @Qualifier("WebClient")
    private final WebClient webClient;
    ObjectMapper mapper = new ObjectMapper();

    private final SikkerKommunikasjonProperties properties;


    public Optional<JsonNode> getCapabilities(String orgnr) throws JsonProcessingException {
        URI integrasjonspunktURI = UriComponentsBuilder.fromUriString(properties.getIntegrasjonspunkt().getURL())
                .path("capabilities/" + orgnr)
                .build()
                .toUri();

        String capabilities = webClient.get()
                .uri(integrasjonspunktURI)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return Optional.of(mapper.readTree(capabilities));
    }

    @PostConstruct
    public void postMessageStatusSubscription() throws JsonProcessingException {

        URI integrasjonspunktURI = UriComponentsBuilder.fromUriString((properties.getIntegrasjonspunkt().getURL()))
                .path("subscriptions")
                .build()
                .toUri();

        String hostName = System.getenv("HOST_NAME");
        log.info("Host name for this environment is: " + hostName);



        String body = "{\n" +
                "  \"name\" : \"Incoming messages\",\n" +
                "  \"pushEndpoint\" : \"https://sk-staging-backend.herokuapp.com/api/v1/messaging/incoming\",\n" +
                "  \"resource\" : \"messages\",\n" +
                "  \"event\" : \"status\",\n" +
                "  \"filter\" : \"status=INNKOMMENDE_MOTTAT&direction=INCOMING\"\n" +
                "}";

        try {
            String res = webClient.post()
                    .uri(integrasjonspunktURI)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info(res);
        }

        catch (RuntimeException e) {
            log.error(e.toString());
        }
    }

}
