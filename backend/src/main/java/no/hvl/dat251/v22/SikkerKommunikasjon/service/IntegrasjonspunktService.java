package no.hvl.dat251.v22.SikkerKommunikasjon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import no.hvl.dat251.v22.SikkerKommunikasjon.config.SikkerKommunikasjonProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

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
}
