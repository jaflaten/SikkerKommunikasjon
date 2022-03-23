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
}
