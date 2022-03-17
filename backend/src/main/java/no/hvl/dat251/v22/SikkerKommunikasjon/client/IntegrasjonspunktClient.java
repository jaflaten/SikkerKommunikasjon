package no.hvl.dat251.v22.SikkerKommunikasjon.client;

import lombok.RequiredArgsConstructor;
import no.hvl.dat251.v22.SikkerKommunikasjon.config.SikkerKommunikasjonProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class IntegrasjonspunktClient {

    @Qualifier(value = "WebClient")
    private final WebClient webClient;
    private final SikkerKommunikasjonProperties properties;

    public String sendMultipartMessage(MultiValueMap body) {
        return webClient.post()
                .uri(getMultipartURI())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(body)
                .exchangeToMono(rs -> rs.bodyToMono(String.class))
                .block();
    }

    public String getCapabilities(String identifier) {
        return webClient.get()
                .uri(getCapabilitiesURI(identifier))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public URI getMultipartURI() {
        return UriComponentsBuilder.fromUriString(properties.getIntegrasjonspunkt().getURL())
                .path("messages/out/multipart")
                .build()
                .toUri();
    }

    public URI getCapabilitiesURI(String identifier) {
        return UriComponentsBuilder.fromUriString(properties.getIntegrasjonspunkt().getURL())
                .path("capabilities/" + identifier)
                .build()
                .toUri();
    }
}
