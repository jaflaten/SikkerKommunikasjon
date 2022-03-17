package no.hvl.dat251.v22.SikkerKommunikasjon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.difi.meldingsutveksling.domain.sbdh.*;
import no.hvl.dat251.v22.SikkerKommunikasjon.client.IntegrasjonspunktClient;
import no.hvl.dat251.v22.SikkerKommunikasjon.config.SikkerKommunikasjonProperties;
import no.hvl.dat251.v22.SikkerKommunikasjon.entities.ArkivMelding;
import no.hvl.dat251.v22.SikkerKommunikasjon.entities.FormData;
import no.hvl.dat251.v22.SikkerKommunikasjon.utility.ArkivMeldingUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
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
    private final IntegrasjonspunktClient client;


    public Optional<JsonNode> getCapabilities(String orgnr) throws JsonProcessingException {
        String capabilities = webClient.get()
                .uri(getCapabilitiesURI(orgnr))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return Optional.of(mapper.readTree(capabilities));

    }

    public Optional<JsonNode> createAndSendMultipartMessage(FormData formData, MultipartFile multipartFile) throws IOException {

        log.info("inside service before arkivmelding");
        String formDataJSON = mapper.writeValueAsString(formData);
        StandardBusinessDocument document = new StandardBusinessDocument();
        document.setStandardBusinessDocumentHeader(createSBDHeader(formData.getReceiver()));

        Optional<ArkivMelding> arkivmeldingXML = getArkivmeldingXML();
        if (arkivmeldingXML.isPresent()) {
            document.setAny(arkivmeldingXML.get());

            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("attachment", multipartFile.getResource(), MediaType.APPLICATION_PDF);
            builder.part("sbd", document);
            builder.part("form", formDataJSON);

            String response = client.sendMultipartMessage(builder.build());

            return Optional.of(mapper.readTree(response));
        } else {
            return Optional.empty();
        }

    }

    public StandardBusinessDocumentHeader createSBDHeader(String receiver) {

        StandardBusinessDocumentHeader header = new StandardBusinessDocumentHeader();

        DocumentIdentification identification = new DocumentIdentification();
        identification.setStandard("urn:no:difi:arkivmelding:xsd::arkivmelding");
        identification.setTypeVersion("1.0");
        identification.setType("arkivmelding");

        PartnerIdentification receiverIdentification = new PartnerIdentification();
        receiverIdentification.setAuthority("iso6523-actorid-upis");
        receiverIdentification.setValue("0192:" + receiver);

        PartnerIdentification senderIdentification = new PartnerIdentification();
        senderIdentification.setAuthority("iso6523-actorid-upis");
        senderIdentification.setValue("0192:" + "987464291");

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

    public URI getMultipartURI() {
        return UriComponentsBuilder.fromUriString(properties.getIntegrasjonspunkt().getURL())
                .path("messages/out/multipart")
                .build()
                .toUri();
    }

    public URI getCapabilitiesURI(String orgnr) {
        return UriComponentsBuilder.fromUriString(properties.getIntegrasjonspunkt().getURL())
                .path("capabilities/" + orgnr)
                .build()
                .toUri();
    }

    public Optional<ArkivMelding> getArkivmeldingXML() {
        Optional<String> arkivmeldingString = new ArkivMeldingUtil().arkivMeldingXMLToString();
        ArkivMelding arkivMelding = new ArkivMelding();

        if (arkivmeldingString.isPresent()) {
            arkivMelding.setMainDocument(arkivmeldingString.get());
            return Optional.of(arkivMelding);
        } else {
            log.warn("ArkivMelding XML is empty, cannot create and send message!");
            return Optional.empty();
        }
    }
}
