package no.hvl.dat251.v22.SikkerKommunikasjon.message_status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MessageStatus {

    private final Status status;
    private final Direction direction;
    private final String id;

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static MessageStatus fromJSON(String body) {
        try {
            JsonNode node = objectMapper.readTree(body);

            Status status = Status.valueOf(node.get("status").asText());
            Direction direction = Direction.valueOf(node.get("direction").asText());
            String id = node.get("messageId").asText();

            return new MessageStatus(status, direction, id);
        } catch (Exception e) {
            return new MessageStatus(Status.FEIL, Direction.NONE, "-1");
        }
    }
}