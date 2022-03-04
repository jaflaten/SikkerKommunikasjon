package no.hvl.dat251.v22.SikkerKommunikasjon.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.hvl.dat251.v22.SikkerKommunikasjon.service.IntegrasjonspunktService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class IntegrasjonspunktController {

    private final IntegrasjonspunktService service;

    @GetMapping("/capabilities/{orgnr}")
    public ResponseEntity<?> getCapabilitiesOrgnr(@PathVariable String orgnr) {
        log.info("Looking up capabilities for organization: " + orgnr);
        if (orgnr.length() != 9 || orgnr.matches("[0-9]+]")) {
            log.warn("Orgnr must be 9 digits in length, this is orgnr is : {} in length", orgnr.length());
            return ResponseEntity.badRequest().body("Orgnr must be 9 digits");
        }
        Optional<JsonNode> capabilities;
        try {
            capabilities = service.getCapabilities(orgnr);
        } catch (JsonProcessingException jpe) {
            log.error("Error parsing json", jpe);
            return ResponseEntity.internalServerError()
                    .body("Error parsing JSON");
        }

        return capabilities.isPresent() ? ResponseEntity.ok(capabilities.get()) : ResponseEntity.notFound().build();
    }
}
