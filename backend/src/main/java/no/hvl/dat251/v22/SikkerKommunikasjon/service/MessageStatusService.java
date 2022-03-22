package no.hvl.dat251.v22.SikkerKommunikasjon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.hvl.dat251.v22.SikkerKommunikasjon.config.SikkerKommunikasjonProperties;
import no.hvl.dat251.v22.SikkerKommunikasjon.message_status.Direction;
import no.hvl.dat251.v22.SikkerKommunikasjon.message_status.MessageStatus;
import no.hvl.dat251.v22.SikkerKommunikasjon.message_status.Status;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageStatusService {

    @Qualifier("WebClient")
    private final WebClient webClient;

    private final ObjectMapper objectMapper;
    private final SikkerKommunikasjonProperties properties;


    /**
     * Handles incoming messages from Integrasjonspunktet.
     * @param body The body posted from Integrasjonspunktet
     */
    public void handleIncomingStatusMessage(String body) {
        log.info("Received body from integrasjonspunktet:\n"+body);
    }

    public Optional<MessageStatus> getMessageStatusFromBody(String body) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(body);

        try {
            Status status = Status.valueOf(node.get("status").asText());
            Direction direction = Direction.valueOf(node.get("direction").asText());
            return Optional.of(new MessageStatus(status, direction));
        } catch (IllegalArgumentException e) {
            log.error("Could not parse body into message status. Body:\n"+body);
            return Optional.empty();
        }
    }

    private URI getIntegrasjonspunktURI(String path) {
        return UriComponentsBuilder.fromUriString(properties.getIntegrasjonspunkt().getURL())
            .path(path)
            .build()
            .toUri();
    }

    private String getHostName() {
        String hostName = System.getenv("HOST_NAME");
        if (hostName == null) {
            return "localhost";
        }
        return hostName;
    }

    /**
     * This method makes a POST-request to subscribe to message statuses from Integrasjonspunktet.
     */
    @PostConstruct
    public void postMessageStatusSubscription() {
        String hostName = getHostName();

        String body =
                "{\n" +
                "  \"name\" : \"Incoming messages\",\n" +
                "  \"pushEndpoint\" : "+hostName+"/api/v1/messaging/incoming\",\n" +
                "  \"resource\" : \"messages\",\n" +
                "  \"event\" : \"status\",\n" +
                "  \"filter\" : \"status=INNKOMMENDE_MOTTAT&direction=INCOMING\"\n" +
                "}";

        try {
            String res = webClient.post()
                    .uri(getIntegrasjonspunktURI("subscriptions"))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Connected to "+getIntegrasjonspunktURI("subscriptions") + " [POST]");
            log.info("Successfully subscribed to Integrasjonspunktet, response:\n"+res);
        }
        catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError())
                log.warn("Error while trying to subscribe to message statuses (maybe already subscribed?):\n"+e.getStatusText());
        }
    }

    @PostConstruct
    public void logAllActiveSubscriptions() {
        try {
            String res = webClient
                    .get()
                    .uri(getIntegrasjonspunktURI("subscriptions"))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("All active message status subscriptions:\n"+res);
        }
        catch (WebClientResponseException e) {
            e.printStackTrace();
        }
    }
}
