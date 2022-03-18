package no.hvl.dat251.v22.SikkerKommunikasjon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocumentHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IntegrasjonspunktServiceTests {

    @Autowired
    IntegrasjonspunktService service;

    @MockBean
    WebClient webClient;

    @MockBean
    ObjectMapper mapper;

    @Before
    public void setup() {

    }

    @Test
    public void createSBDHeaderShouldReturnCorrectHeader() {

        final String testReceiver = "test";

        StandardBusinessDocumentHeader header = service.getStandardBusinessDocumentHeader(testReceiver);

        Assertions.assertEquals(header.getDocumentIdentification().getStandard(), "urn:no:difi:arkivmelding:xsd::arkivmelding");
        Assertions.assertEquals(header.getDocumentIdentification().getTypeVersion(), "1.0");
        Assertions.assertEquals(header.getDocumentIdentification().getType(), "arkivmelding");

        Assertions.assertEquals(header.getReceiver().iterator().next().getIdentifier().getValue(), "0192:" + testReceiver);
        Assertions.assertEquals(header.getReceiver().iterator().next().getIdentifier().getAuthority(), "iso6523-actorid-upis");

        Assertions.assertEquals(header.getSender().iterator().next().getIdentifier().getValue(), "0192:987464291");
        Assertions.assertEquals(header.getSender().iterator().next().getIdentifier().getAuthority(), "iso6523-actorid-upis");

        Assertions.assertEquals(header.getBusinessScope().getScope().iterator().next().getIdentifier(), "urn:no:difi:profile:arkivmelding:administrasjon:ver1.0");
        Assertions.assertEquals(header.getBusinessScope().getScope().iterator().next().getType(), "ConversationId");
    }

}