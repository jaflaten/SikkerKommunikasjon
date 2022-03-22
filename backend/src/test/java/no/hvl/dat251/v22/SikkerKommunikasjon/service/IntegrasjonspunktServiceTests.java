package no.hvl.dat251.v22.SikkerKommunikasjon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.difi.meldingsutveksling.domain.sbdh.*;
import no.hvl.dat251.v22.SikkerKommunikasjon.domain.ArkivMeldingMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IntegrasjonspunktServiceTests {

    @Autowired
    IntegrasjonspunktService service;

    @MockBean
    WebClient webClient;

    @MockBean
    ObjectMapper mapper;

    Partner partner;
    Partner senderPartner;
    PartnerIdentification partnerId;
    PartnerIdentification senderPartnerId;
    String receiver;
    DocumentIdentification documentId;
    Scope scope;
    BusinessScope businessScope;
    StandardBusinessDocumentHeader header;
    ArkivMeldingMessage any;
    StandardBusinessDocument document;

    @Before
    public void setup() {
        receiver = "999777555";
        partnerId = new PartnerIdentification();
        partnerId.setAuthority("iso6523-actorid-upis");
        partnerId.setValue("0192:" + receiver);
        partner = new Partner();
        partner.setIdentifier(partnerId);

        documentId = new DocumentIdentification();
        documentId.setStandard("urn:no:difi:arkivmelding:xsd::arkivmelding");
        documentId.setTypeVersion("1.0");
        documentId.setType("arkivmelding");

        scope = new Scope();
        scope.setType("ConversationId");
        scope.setIdentifier("urn:no:difi:profile:arkivmelding:administrasjon:ver1.0");
        businessScope = new BusinessScope();
        businessScope.addScope(scope);

        senderPartnerId = new PartnerIdentification();
        senderPartnerId.setValue("0192:987464291");
        senderPartnerId.setAuthority("iso6523-actorid-upis");
        senderPartner = new Partner();
        senderPartner.setIdentifier(senderPartnerId);

        header = new StandardBusinessDocumentHeader();
        header.setDocumentIdentification(documentId);
        header.setReceiver(Set.of(partner));
        header.setSender(Set.of(senderPartner));
        header.setBusinessScope(businessScope);
        header.setHeaderVersion("1.0");

        any = new ArkivMeldingMessage();
        any.setMainDocument("arkivmelding.xml");
        any.setSecurityLevel(3);

        document = new StandardBusinessDocument();
        document.setStandardBusinessDocumentHeader(header);
        document.setAny(any);
    }

    @Test
    public void createNewPartnerShouldBeEqual() {
        assertEquals(service.newPartner(receiver), partner);
    }

    @Test
    public void createDocumentIdentificationShouldBeEqual() {
        assertEquals(service.newDocumentIdentification(), documentId);
    }

    @Test
    public void createBusinessScope_firstScopeShouldBeEqual() {
        assertEquals(service.newBusinessScope().getScope().iterator().next().getIdentifier(), scope.getIdentifier());
        assertEquals(service.newBusinessScope().getScope().iterator().next().getType(), scope.getType());
        assertEquals(service.newBusinessScope().getScope().size(), businessScope.getScope().size());
    }

    @Test
    public void createBusinessScope_sizeOfScopesIsNotEqual() {
        Scope scope2 = new Scope();
        scope2.setIdentifier("foo");
        scope2.setType("bar");
        businessScope.addScope(scope2);
        assertNotEquals(service.newBusinessScope().getScope().size(), businessScope.getScope().size());
    }

    @Test
    public void createStandardBusinessDocument_returnedAnyShouldBeOfExpectedClass() {
        assertEquals(service.getStandardBusinessDocument(receiver).getAny().getClass(), any.getClass());
    }

    @Test
    public void createSBDHeaderShouldReturnCorrectHeader() {

        final String testReceiver = "test";

        StandardBusinessDocumentHeader header = service.getStandardBusinessDocumentHeader(testReceiver);

        assertEquals(header.getDocumentIdentification().getStandard(), "urn:no:difi:arkivmelding:xsd::arkivmelding");
        assertEquals(header.getDocumentIdentification().getTypeVersion(), "1.0");
        assertEquals(header.getDocumentIdentification().getType(), "arkivmelding");

        assertEquals(header.getReceiver().iterator().next().getIdentifier().getValue(), "0192:" + testReceiver);
        assertEquals(header.getReceiver().iterator().next().getIdentifier().getAuthority(), "iso6523-actorid-upis");

        assertEquals(header.getSender().iterator().next().getIdentifier().getValue(), "0192:987464291");
        assertEquals(header.getSender().iterator().next().getIdentifier().getAuthority(), "iso6523-actorid-upis");

        assertEquals(header.getBusinessScope().getScope().iterator().next().getIdentifier(), "urn:no:difi:profile:arkivmelding:administrasjon:ver1.0");
        assertEquals(header.getBusinessScope().getScope().iterator().next().getType(), "ConversationId");
    }

}