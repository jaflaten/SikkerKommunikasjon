package no.hvl.dat251.v22.SikkerKommunikasjon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.difi.meldingsutveksling.domain.sbdh.*;
import no.hvl.dat251.v22.SikkerKommunikasjon.config.SikkerKommunikasjonProperties;
import no.hvl.dat251.v22.SikkerKommunikasjon.entities.ArkivMelding;
import no.hvl.dat251.v22.SikkerKommunikasjon.utility.ArkivMeldingUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrasjonspunktService {

    @Qualifier("WebClient")
    private final WebClient webClient;
    ObjectMapper mapper = new ObjectMapper();

    private final SikkerKommunikasjonProperties properties;


    public Optional<JsonNode> getCapabilities(String orgnr) throws JsonProcessingException {
        URI integrasjonspunktURI = UriComponentsBuilder.fromUriString(properties.getIntegrasjonspunkt().getURL())
                .path("capabilities/" + orgnr)
                .build()
                .toUri();

        String capabilities = webClient.get()
                .uri(integrasjonspunktURI)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return Optional.of(mapper.readTree(capabilities));

    }

    public Optional<JsonNode> sendMultipartMessage(String ssn, String name, String email, String receiver, String title,
                                                 String content, Boolean isSensitive, File file) {

        StandardBusinessDocument document = new StandardBusinessDocument();

        document.setStandardBusinessDocumentHeader(createSBDHeader(receiver));

        ArkivMeldingUtil util = new ArkivMeldingUtil();
        String xml;
        Optional<String> s = util.arkivMeldingXMLToString();

        if(s.isPresent()) {
            xml = s.get();
        } else {
            log.warn("ArkivMelding XML is empty, cannot create and send message!");
            return Optional.empty();
        }

        ArkivMelding melding = new ArkivMelding();
        melding.setMainDocument(xml);

        document.setAny(melding);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        // builder.part("file", multipartFile.getResource());

        // Unfinished - return empty
        return Optional.empty();
    }

    public StandardBusinessDocumentHeader createSBDHeader(String receiver) {

        StandardBusinessDocumentHeader header = new StandardBusinessDocumentHeader();

        DocumentIdentification identification = new DocumentIdentification();
        identification.setStandard("urn:no:difi:arkivmelding:xsd::arkivmelding");
        identification.setTypeVersion("1.0");
        identification.setType("arkivmelding");

        PartnerIdentification receiverIdentification = new PartnerIdentification();
        receiverIdentification.setValue("0192:" + receiver);
        receiverIdentification.setAuthority("iso6523-actorid-upis");

        PartnerIdentification senderIdentification = new PartnerIdentification();
        senderIdentification.setValue("0192:" + "991825827");
        senderIdentification.setAuthority("iso6523-actorid-upis");

        Partner receiverPartner = new Partner();
        Partner senderPartner = new Partner();

        receiverPartner.setIdentifier(receiverIdentification);
        senderPartner.setIdentifier(senderIdentification);

        Set<Partner> receiverPartnerSet = Set.of(receiverPartner);
        Set<Partner> senderPartnerSet = Set.of(senderPartner);

        Scope scope = new Scope();
        scope.setType("ConversationId");
        scope.setIdentifier("urn:no:difi:profile:arkivmelding:administrasjon:ver1.0");
        BusinessScope bScope = new BusinessScope();
        bScope.addScope(scope);

        header.setDocumentIdentification(identification);
        header.setReceiver(receiverPartnerSet);
        header.setSender(senderPartnerSet);
        header.setBusinessScope(bScope);
        header.setHeaderVersion("1.0");

        return header;
    }
}
