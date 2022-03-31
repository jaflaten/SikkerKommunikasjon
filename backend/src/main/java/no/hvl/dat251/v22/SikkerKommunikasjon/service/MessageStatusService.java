package no.hvl.dat251.v22.SikkerKommunikasjon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.hvl.dat251.v22.SikkerKommunikasjon.message_status.MessageStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageStatusService {

    /**
     * Handles incoming messages sent by Integrasjonspunktet
     */
    public void handleIncomingStatusMessage(String body) {
        MessageStatus messageStatus = MessageStatus.fromJSON(body);

        log.info("Received body from integrasjonspunktet with status: " + messageStatus);
    }
}
