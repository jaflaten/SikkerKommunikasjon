package no.hvl.dat251.v22.SikkerKommunikasjon.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.hvl.dat251.v22.SikkerKommunikasjon.entities.FormData;
import no.hvl.dat251.v22.SikkerKommunikasjon.service.IntegrasjonspunktService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
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

    @PostMapping("/messages/multipart")
    public ResponseEntity<?> sendMultipartMessage(@RequestParam String ssn, @RequestParam String name,
                                                  @RequestParam String email, @RequestParam String receiver,
                                                  @RequestParam String title, @RequestParam String content,
                                                  @RequestParam Boolean isSensitive, @RequestParam File attachment) throws IOException {


        log.info("multipart controller called");
        FormData formData = new FormData(ssn, name, email, receiver, title, content, isSensitive);
        log.info("formdata created");
        Optional<JsonNode> response = service.createAndSendMultipartMessage(formData, attachment);

        return response.isPresent() ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();

    }
}
