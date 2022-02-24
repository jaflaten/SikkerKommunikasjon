package no.hvl.dat251.v22.SikkerKommunikasjon.controller;

import no.hvl.dat251.v22.SikkerKommunikasjon.config.WebConfig;
import no.hvl.dat251.v22.SikkerKommunikasjon.service.IntegrasjonspunktService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringRunner.class)
@Import({WebConfig.class})
@WebMvcTest(IntegrasjonspunktController.class)
public class IntegrasjonspunktControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    IntegrasjonspunktService service;

    String orgnr;

    @Before
    public void setup() {
        orgnr = "123456789";
    }

    @Test
    public void getCapabiltiesRequestReturnsStatus200Ok() throws Exception {
        mockMvc.perform(get("/api/v1/capabilities/{orgnr}  ", orgnr))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }


}
