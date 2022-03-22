package no.hvl.dat251.v22.SikkerKommunikasjon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.hvl.dat251.v22.SikkerKommunikasjon.service.MessageStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class MessageStatusController {

    private final MessageStatusService messageStatusService;

    @PostMapping("/messaging/incoming")
    public ResponseEntity postMessageStatus(@RequestBody String body) {
        messageStatusService.handleIncomingMessage(body);
        return ResponseEntity.ok().build();
    }
}