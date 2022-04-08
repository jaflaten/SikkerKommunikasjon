package no.hvl.dat251.v22.SikkerKommunikasjon.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.hvl.dat251.v22.SikkerKommunikasjon.domain.Arkivmelding;
import no.hvl.dat251.v22.SikkerKommunikasjon.domain.Attachment;
import no.hvl.dat251.v22.SikkerKommunikasjon.domain.FormData;
import no.hvl.dat251.v22.SikkerKommunikasjon.service.IntegrasjonspunktService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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

    @PostMapping(path = "/messages/multipart", consumes = "multipart/form-data")
    public ResponseEntity<?> sendMultipartMessage(@RequestParam String ssn, @RequestParam String name,
                                                  @RequestParam String email, @RequestParam String receiver,
                                                  @RequestParam String title, @RequestParam String content,
                                                  @RequestParam Boolean isSensitive, @RequestParam("attachment") MultipartFile attachment) throws IOException {

        FormData formData = new FormData(ssn, name, email, receiver, title, content, isSensitive);
        Arkivmelding arkivmelding = createArkivmelding(formData, attachment);
        Optional<JsonNode> response = service.sendMultipartMessage(arkivmelding);
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

        ResponseEntity<?> response = service.uploadAttachment(messageId, contentType, contentDisposition);
        return response.getStatusCode().is2xxSuccessful() ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping(path = "messages/send")
    public ResponseEntity<?> sendMessage(@RequestParam String messageId) {
        return service.sendMessage(messageId);
    }


    public Arkivmelding createArkivmelding(FormData form, MultipartFile attachment) throws IOException {
        Attachment a1 = Attachment.builder()
                .filename("test.pdf")
                .content(getFile(attachment.getResource()))
                .contentType(MediaType.APPLICATION_PDF)
                .build();

        List<Attachment> attachments = new ArrayList<>();
        attachments.add(a1);
        attachments.add(Attachment.builder()
                .filename("form")
                .content(form.toString())
                .contentType(MediaType.TEXT_PLAIN)
                .build());

        return Arkivmelding.builder()
                .receiver(form.getReceiver())
                .mainDocument(getFile(new ClassPathResource("arkivmelding.xml").getPath()))
                .attachments(attachments)
                .build();
    }

    private String getFile(String path) throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(path);

        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private String getFile(Resource resource) throws IOException {
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
