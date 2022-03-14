package no.hvl.dat251.v22.SikkerKommunikasjon.message_status;

import lombok.Getter;
import lombok.Setter;

public class MessageStatus {

    @Getter @Setter private Status status;

    @Getter @Setter private Direction direction;

    public MessageStatus(Status status, Direction direction) {
        this.status = status;
        this.direction = direction;
    }
}