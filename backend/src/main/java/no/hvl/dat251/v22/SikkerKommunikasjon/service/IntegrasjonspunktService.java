package no.hvl.dat251.v22.SikkerKommunikasjon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.difi.meldingsutveksling.domain.sbdh.*;
import no.hvl.dat251.v22.SikkerKommunikasjon.client.IntegrasjonspunktClient;
import no.hvl.dat251.v22.SikkerKommunikasjon.config.SikkerKommunikasjonProperties;
import no.hvl.dat251.v22.SikkerKommunikasjon.domain.ArkivMeldingMessage;
import no.hvl.dat251.v22.SikkerKommunikasjon.domain.Arkivmelding;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.Set;

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
        log.info("New message created with messageId: {}", findMessageId(standardBusinessDocument));
        return Optional.of(standardBusinessDocument);
    }

    private String findMessageId(JsonNode standardBusinessDocument) {
        return standardBusinessDocument.elements().next().get("documentIdentification").get("instanceIdentifier").textValue();
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

    private BusinessScope newBusinessScope() {
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

    @PostConstruct
    public void postMessageStatusSubscription() {
        String hostName = System.getenv("HOST_NAME");
        log.info("Host name for this environment is: " + hostName);


        String body = "{\n" +
                "  \"name\" : \"Incoming messages\",\n" +
                "  \"pushEndpoint\" : \"https://sk-staging-backend.herokuapp.com/api/v1/messaging/incoming\",\n" +
                "  \"resource\" : \"messages\",\n" +
                "  \"event\" : \"status\",\n" +
                "  \"filter\" : \"status=INNKOMMENDE_MOTTAT&direction=INCOMING\"\n" +
                "}";

        try {
            String res = client.subscribe(body);
            log.info(res);
        } catch (RuntimeException e) {
            log.error(e.toString());
        }

    }

}
