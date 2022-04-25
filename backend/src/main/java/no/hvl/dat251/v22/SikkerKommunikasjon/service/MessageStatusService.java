package no.hvl.dat251.v22.SikkerKommunikasjon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.hvl.dat251.v22.SikkerKommunikasjon.message_status.MessageStatus;
import no.hvl.dat251.v22.SikkerKommunikasjon.message_status.Status;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageStatusService {

    private final EmailService emailService;

    /**
     * Handles incoming messages sent by Integrasjonspunktet
     */
    public void handleIncomingStatusMessage(String body) {
        MessageStatus messageStatus = MessageStatus.fromJSON(body);
        if (messageStatus.getStatus() == Status.FEIL) {
            log.error("Error when parsing message status, status resolved to ´Feil´.");
            return;
        }

        log.info("Received body from integrasjonspunktet with status: " + messageStatus);

        // Only send email if status == SENDT
        if (messageStatus.getStatus() != Status.SENDT)
            return;

        // Get email from messageId
        String messageId = messageStatus.getId();
        String email = EmailService.emailFromMessageId(messageId);

        // notify associated user about the updated status
        emailService.sendSimpleEmail(
                email,
                "Updated Status: " + messageStatus.getStatus(),
                "Updated status:  " + messageStatus.getStatus() +
                        "\nDirection: " + messageStatus.getDirection() +
                        "\nID: " + messageStatus.getId()
        );
    }
}
