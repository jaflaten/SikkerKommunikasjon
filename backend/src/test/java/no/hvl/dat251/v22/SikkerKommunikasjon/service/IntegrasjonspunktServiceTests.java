package no.hvl.dat251.v22.SikkerKommunikasjon.service;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(IntegrasjonspunktService.class)
public class IntegrasjonspunktServiceTests {
    String json;
    String orgnr;
    IntegrasjonspunktService service;
    URI integrasjonspunktURI;

    @Autowired
    WebTestClient webTestClient;

    @Before
    public void setup() {
        orgnr = "123456789";
        json = "{ \"process\" : \"arkivmelding\", \"serviceIdentifier\" : \"DPV\" }";
        integrasjonspunktURI = UriComponentsBuilder.fromUriString("http://localhost:8081").build().toUri();

    }

    @Test
    public void getCapabilitiesShouldReturnExpectedJSON() {
        webTestClient.get()
                .uri(integrasjonspunktURI)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(r -> Assertions.assertEquals(json, new String(r.getResponseBody())));


    }


}
