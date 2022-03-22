package no.hvl.dat251.v22.SikkerKommunikasjon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.hvl.dat251.v22.SikkerKommunikasjon.message_status.Direction;
import no.hvl.dat251.v22.SikkerKommunikasjon.message_status.MessageStatus;
import no.hvl.dat251.v22.SikkerKommunikasjon.message_status.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageStatusService {

    private final ObjectMapper objectMapper;

    @Autowired
    public MessageStatusService() {
        objectMapper = new ObjectMapper();
    }

    /**
     * Handles incoming message from integrasjonspunktet.
     */
    public void handleIncomingMessage(String body) {
        log.info("Received body from integrasjonspunktet:\n"+body);
    }

    public MessageStatus getMessageStatusFromBody(String body) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(body);

        Status status = Status.valueOf(node.get("status").asText());
        Direction direction = Direction.valueOf(node.get("direction").asText());

        return new MessageStatus(status, direction);
    }
}
