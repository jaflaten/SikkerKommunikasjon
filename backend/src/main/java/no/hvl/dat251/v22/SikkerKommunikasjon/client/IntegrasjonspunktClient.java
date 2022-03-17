package no.hvl.dat251.v22.SikkerKommunikasjon.client;

import lombok.RequiredArgsConstructor;
import no.hvl.dat251.v22.SikkerKommunikasjon.config.SikkerKommunikasjonProperties;
import org.springframework.beans.factory.annotation.Qualifier;
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
        String response = webClient.post()
                .uri(getMultipartURI())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(body)
                .exchangeToMono(rs -> rs.bodyToMono(String.class))
                .block();

        return response;
    }

    public URI getMultipartURI() {
        return UriComponentsBuilder.fromUriString(properties.getIntegrasjonspunkt().getURL())
                .path("messages/out/multipart")
                .build()
                .toUri();
    }
}
