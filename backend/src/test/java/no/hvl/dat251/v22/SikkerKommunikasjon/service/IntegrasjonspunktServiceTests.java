package no.hvl.dat251.v22.SikkerKommunikasjon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.difi.meldingsutveksling.domain.sbdh.Partner;
import no.difi.meldingsutveksling.domain.sbdh.PartnerIdentification;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocumentHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    PartnerIdentification partnerId;
    String receiver;

    @Before
    public void setup() {
        receiver = "999777555";
        partnerId = new PartnerIdentification();
        partnerId.setAuthority("iso6523-actorid-upis");
        partnerId.setValue("0192:" + receiver);
        partner = new Partner();
        partner.setIdentifier(partnerId);
    }

    @Test
    public void createNewPartnerShouldBeEqual() {
        assertEquals(service.newPartner(receiver), partner);
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