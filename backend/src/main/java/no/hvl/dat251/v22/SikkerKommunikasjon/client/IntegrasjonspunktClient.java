package no.hvl.dat251.v22.SikkerKommunikasjon.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;
import no.hvl.dat251.v22.SikkerKommunikasjon.config.SikkerKommunikasjonProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@Slf4j
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

    public HttpStatus sendMessage(String messageId) {
        return webClient.post()
                .uri(getCreateUri() + "/" + messageId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .toEntity(String.class)
                .filter(clientResponse -> clientResponse.getStatusCode().is2xxSuccessful() || clientResponse.getStatusCode().is4xxClientError())
                .flatMap(clientResponse -> Mono.justOrEmpty(clientResponse.getStatusCode()))
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

    public String subscribe(String body) {
        return webClient.post()
                .uri(getSubscriptionUri())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


    public String create(StandardBusinessDocument standardBusinessDocument) {
        return webClient.post()
                .uri(getCreateUri())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(standardBusinessDocument)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public HttpStatus upload(String messageId, String contentTypeString, String contentDispositionString) {
        MediaType contentType = MediaType.parseMediaType(contentTypeString);

        return webClient.put()
                .uri(getUploadUri(messageId))
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionString)
                .retrieve()
                .toEntity(String.class)
                .filter(entity -> entity.getStatusCode().is2xxSuccessful() || entity.getStatusCode().is4xxClientError())
                .flatMap(entity -> Mono.justOrEmpty(entity.getStatusCode()))
                .block();
    }

    public HttpStatus upload(String messageId, String contentTypeString, String contentDispositionString, String content) {
        MediaType contentType = MediaType.parseMediaType(contentTypeString);

        return webClient.put()
                .uri(getUploadUri(messageId))
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionString)
                .bodyValue(content)
                .retrieve()
                .toEntity(String.class)
                .filter(entity -> entity.getStatusCode().is2xxSuccessful() || entity.getStatusCode().is4xxClientError())
                .flatMap(entity -> Mono.justOrEmpty(entity.getStatusCode()))
                .block();
    }

    private URI getCreateUri() {
        return UriComponentsBuilder.fromUriString(properties.getIntegrasjonspunkt().getURL())
                .path("messages/out")
                .build()
                .toUri();
    }

    private URI getUploadUri(String messageId) {
        return UriComponentsBuilder.fromUriString(properties.getIntegrasjonspunkt().getURL())
                .path("messages/out/" + messageId)
                .build()
                .toUri();
    }


    private URI getSubscriptionUri() {
        return UriComponentsBuilder.fromUriString((properties.getIntegrasjonspunkt().getURL()))
                .path("subscriptions")
                .build()
                .toUri();
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
