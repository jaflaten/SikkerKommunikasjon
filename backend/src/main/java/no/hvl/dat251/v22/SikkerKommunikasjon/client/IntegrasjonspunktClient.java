package no.hvl.dat251.v22.SikkerKommunikasjon.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;
import no.hvl.dat251.v22.SikkerKommunikasjon.config.SikkerKommunikasjonProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

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

    public ResponseEntity<?> sendMessage(String messageId) {
        return webClient.post()
                .uri(getCreateUri() + "/" + messageId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(ResponseEntity.class)
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

    public ResponseEntity<?> upload(String messageId, String fileName, String title, File file) throws IOException {
        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(fileName)
                .name(title)
                .build();

        //MediaType.asMediaType(MimeType.valueOf("text/plain;UTF-8"));
        MediaType contentType = MediaType.valueOf(Files.probeContentType(file.getAbsoluteFile().toPath()));
        log.info("Content type is: " + contentType);
        return webClient.put()
                .uri(getUploadUri(messageId))
                .header(contentDisposition.toString())
                .contentType(contentType)
                .body(BodyInserters.fromValue(file))
                .retrieve()
                .bodyToMono(ResponseEntity.class)
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
