package no.hvl.dat251.v22.SikkerKommunikasjon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/messaging/incoming")
    public ResponseEntity<?> messageStatus(@RequestBody String body) {
        log.info("Endpoint '/messaging/incoming' accessed. Endpoint not implemented.");

        // 501 - not implemented
        return ResponseEntity.status(501).body("Endpoint not implemented.");
    }
}