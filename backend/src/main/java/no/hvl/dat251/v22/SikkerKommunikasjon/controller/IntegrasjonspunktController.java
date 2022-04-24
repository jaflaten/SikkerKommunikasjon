package no.hvl.dat251.v22.SikkerKommunikasjon.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.hvl.dat251.v22.SikkerKommunikasjon.domain.FormData;
import no.hvl.dat251.v22.SikkerKommunikasjon.service.IntegrasjonspunktService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@RequestMapping("/api/v1")
public class IntegrasjonspunktController {

    private final IntegrasjonspunktService
            service;

    @CrossOrigin
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

    @PostMapping(path = "/messages/out", consumes = "multipart/form-data")
    public ResponseEntity<?> sendMessage(@RequestParam String ssn, @RequestParam String name,
                                         @RequestParam String email, @RequestParam String receiver,
                                         @RequestParam String title, @RequestParam String content,
                                         @RequestParam Boolean isSensitive, @RequestParam("attachment") File attachment) throws IOException {

        FormData formData = new FormData(ssn, name, email, receiver, title, content, isSensitive);

        Optional<JsonNode> response = service.messageHandler(formData, attachment);

        return response.isPresent() ? ResponseEntity.ok(response.get()) : ResponseEntity.badRequest().build();
    }

    @PostMapping(path = "/messages/multipart", consumes = "multipart/form-data")
    public ResponseEntity<?> sendMultipartMessage(@RequestParam String ssn, @RequestParam String name,
                                                  @RequestParam String email, @RequestParam String receiver,
                                                  @RequestParam String title, @RequestParam String content,
                                                  @RequestParam Boolean isSensitive, @RequestParam("attachment") File attachment) throws IOException {

        FormData formData = new FormData(ssn, name, email, receiver, title, content, isSensitive);

        Optional<JsonNode> response = service.sendMultipartMessage(formData, attachment);

        return response.isPresent() ? ResponseEntity.ok(response.get()) : ResponseEntity.badRequest().build();
    }

    @PostMapping(path = "/messages/create")
    public ResponseEntity<?> createMessage(@RequestParam String receiver) throws JsonProcessingException {
        Optional<JsonNode> response = service.createMessage(receiver);
        return response.isPresent() ? ResponseEntity.ok(response.get()) : ResponseEntity.badRequest().build();
    }

    @PutMapping(path = "/messages/upload/{messageId}")
    public ResponseEntity<?> uploadAttachmentToMessage(@PathVariable String messageId,
                                                       @RequestHeader(HttpHeaders.CONTENT_TYPE) String contentType,
                                                       @RequestHeader(HttpHeaders.CONTENT_DISPOSITION) String contentDisposition) {
        HttpStatus httpStatus = service.uploadAttachment(messageId, contentType, contentDisposition);
        return httpStatus.is2xxSuccessful() ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    //To test service to upload arkivmelding. otherwise do it manually, but need service method to upload the arkivmelding. How to take a file in the project and add to content-disp if so ?
    //Delete this method after manual testing and the logic to decide which send method should be used is implemented.
    @PutMapping(path = "/messages/upload/{messageId}/arkivmelding")
    @SneakyThrows(IOException.class)
    public ResponseEntity<?> uploadArkivmeldingToMessage(@PathVariable String messageId) {
        HttpStatus httpStatus = service.uploadArkivmeldingXML(messageId);
        return httpStatus.is2xxSuccessful() ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping(path = "messages/send/{messageId}")
    public ResponseEntity<?> sendLargeMessage(@PathVariable String messageId) {
        HttpStatus httpStatus = service.sendMessage(messageId);
        return httpStatus.is2xxSuccessful() ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
