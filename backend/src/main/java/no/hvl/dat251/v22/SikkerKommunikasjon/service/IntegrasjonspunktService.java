package no.hvl.dat251.v22.SikkerKommunikasjon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.difi.meldingsutveksling.domain.sbdh.*;
import no.hvl.dat251.v22.SikkerKommunikasjon.client.IntegrasjonspunktClient;
import no.hvl.dat251.v22.SikkerKommunikasjon.config.SikkerKommunikasjonProperties;
import no.hvl.dat251.v22.SikkerKommunikasjon.domain.ArkivMeldingMessage;
import no.hvl.dat251.v22.SikkerKommunikasjon.domain.Arkivmelding;
import no.hvl.dat251.v22.SikkerKommunikasjon.domain.Attachment;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrasjonspunktService {
    private static String EFORMIDLING_AUTHORITY = "iso6523-actorid-upis";
    private static String TYPE_VERSION = "1.0";
    private static String HEADER_VERSION = "1.0";

    ObjectMapper mapper = new ObjectMapper();
    private final IntegrasjonspunktClient client;
    private final SikkerKommunikasjonProperties properties;


    public Optional<JsonNode> getCapabilities(String identifier) throws JsonProcessingException {
        return Optional.of(mapper.readTree(client.getCapabilities(identifier)));
    }

    public Optional<JsonNode> sendMultipartMessage(Arkivmelding melding) throws IOException {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("sbd", getStandardBusinessDocument(melding.getReceiver()), MediaType.APPLICATION_JSON);
        builder.part("arkivmelding", melding.getMainDocument(), MediaType.APPLICATION_XML).filename("arkivmelding.xml");

        melding.getAttachments().forEach(
                attachment -> builder.part(attachment.getFilename(), attachment.getContent(), attachment.getContentType())
                        .filename(attachment.getFilename())
        );

        String response = client.sendMultipartMessage(builder.build());
        JsonNode standardBusinessDocument = mapper.readTree(response);
        val messageId = findMessageId(standardBusinessDocument);
        log.info("New message created with messageId: {}", messageId);
        sendEmailToUser(melding, messageId);

        return Optional.of(standardBusinessDocument);
    }

    /** Procedure for finding email is from: https://stackoverflow.com/a/15703751 **/
    private static String findEmail(Attachment attachment) {
        Matcher matcher = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+")
                .matcher(attachment.getContent());

        while (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    public HttpStatus sendMessage(String messageId) {
        var res = client.sendMessage(messageId);
        log.info(res.is2xxSuccessful() ? "Successfully sent message: " + messageId
                : "Failed to send message: " + messageId);
        return res;
    }

    public HttpStatus uploadAttachment(String messageId, String contentType, String contentDisposition) {
        var res = client.upload(messageId, contentType, contentDisposition);
        log.info(res.is2xxSuccessful() ? "Succesfully uploaded attachment to message: " + messageId
                : "Failed to upload attachment to message: " + messageId);
        return res;
    }

    private String findMessageId(JsonNode standardBusinessDocument) {
        return standardBusinessDocument.elements().next().get("documentIdentification").get("instanceIdentifier").textValue();
    }

    public HttpStatus uploadArkivmeldingXML(String messageId) throws IOException {
        String contentDisposition = ContentDisposition.attachment().name("arkivmelding").filename("arkivmelding.xml").build().toString();
        String contentType = "text/xml";
        File arkivmeldingXML = new ClassPathResource("arkivmelding.xml").getFile();
        String content = FileUtils.readFileToString(arkivmeldingXML, "UTF-8").trim().replaceFirst("^([\\W]+)<", "<");

        var res = client.upload(messageId, contentType, contentDisposition, content);
        log.info(res.is2xxSuccessful() ? "Succesfully uploaded arkivmeldingXml to message: " + messageId
                                        : "Failed to upload arkivmeldingXML to message: " + messageId);
        return res;
    }

    public Optional<JsonNode> createMessage(String receiver) throws JsonProcessingException {
        String sbd = client.create(getStandardBusinessDocument(receiver));
        JsonNode jsonNode = mapper.readTree(sbd);
        log.info(sbd.length() > 0 ? "Successfully created message with id: " + findMessageId(jsonNode) + " and SBD: " + jsonNode
                : "Failed to create message");
        return Optional.of(jsonNode);
    }

    public StandardBusinessDocument getStandardBusinessDocument(String receiver) {
        ArkivMeldingMessage any = new ArkivMeldingMessage();
        any.setMainDocument("arkivmelding.xml");
        any.setSecurityLevel(3);

        StandardBusinessDocument document = new StandardBusinessDocument();
        document.setStandardBusinessDocumentHeader(getStandardBusinessDocumentHeader(receiver));
        document.setAny(any);

        return document;
    }

    public StandardBusinessDocumentHeader getStandardBusinessDocumentHeader(String receiver) {
        StandardBusinessDocumentHeader header = new StandardBusinessDocumentHeader();
        header.setDocumentIdentification(newDocumentIdentification());
        header.setReceiver(Set.of(newPartner(receiver)));
        header.setSender(Set.of(newPartner(properties.getIntegrasjonspunkt().getSender())));
        header.setBusinessScope(newBusinessScope());
        header.setHeaderVersion(HEADER_VERSION);

        return header;
    }

    public BusinessScope newBusinessScope() {
        Scope scope = new Scope();
        scope.setType("ConversationId");
        scope.setIdentifier("urn:no:difi:profile:arkivmelding:administrasjon:ver1.0");
        BusinessScope businessScope = new BusinessScope();
        businessScope.addScope(scope);

        return businessScope;
    }

    public Partner newPartner(String identifier) {
        Partner partner = new Partner();
        PartnerIdentification partnerId = new PartnerIdentification();
        partnerId.setAuthority(EFORMIDLING_AUTHORITY);
        partnerId.setValue("0192:" + identifier);
        partner.setIdentifier(partnerId);

        return partner;
    }


    public DocumentIdentification newDocumentIdentification() {
        DocumentIdentification identification = new DocumentIdentification();
        identification.setStandard("urn:no:difi:arkivmelding:xsd::arkivmelding");
        identification.setTypeVersion(TYPE_VERSION);
        identification.setType("arkivmelding");

        return identification;
    }

    public void sendEmailToUser(Arkivmelding melding, String messageId) {
        Optional<Attachment> attachment =
                melding.getAttachments()
                        .stream()
                        .filter(p -> p.getFilename().equals("form"))
                        .findFirst();

        if (attachment.isPresent()) {
            String email = findEmail(attachment.get());

            if (!email.equals("") && !messageId.equals("")) {
                // Cache the messageId along with the user email
                EmailService.addEmailMessageIdPair(
                        email,
                        messageId
                );
            }
        }
    }
}
