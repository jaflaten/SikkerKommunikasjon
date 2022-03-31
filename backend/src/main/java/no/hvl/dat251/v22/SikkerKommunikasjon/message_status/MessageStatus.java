package no.hvl.dat251.v22.SikkerKommunikasjon.message_status;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageStatus {

    private Status status;
    private Direction direction;
}