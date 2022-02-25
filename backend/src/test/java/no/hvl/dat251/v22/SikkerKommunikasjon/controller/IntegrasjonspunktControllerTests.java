package no.hvl.dat251.v22.SikkerKommunikasjon.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat251.v22.SikkerKommunikasjon.service.IntegrasjonspunktService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(IntegrasjonspunktController.class)
public class IntegrasjonspunktControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    IntegrasjonspunktService service;

    String orgnr;
    String wrongInputOrgnr;
    String notExistingOrgnr;

    Optional<JsonNode> jsonOptional;

    String json;
    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() throws JsonProcessingException {
        orgnr = "123456789";
        wrongInputOrgnr = "1234567891";
        notExistingOrgnr = "999888777";

        json = "{ \"process\" : \"arkivmelding\", \"serviceIdentifier\" : \"DPV\" }";
        jsonOptional = Optional.of(mapper.readTree(json));
    }

    @Test
    public void getCapabilitiesRequestReturnsStatus200Ok() throws Exception {
        Mockito.when(service.getCapabilities(orgnr)).thenReturn(jsonOptional);
        mockMvc.perform(get("/api/v1/capabilities/{orgnr}", orgnr))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getCapabilitiesRequestShouldReturnExpectedContent() throws Exception {
        Mockito.when(service.getCapabilities(orgnr)).thenReturn(jsonOptional);
        mockMvc.perform(get("/api/v1/capabilities/{orgnr}", orgnr))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.process", Matchers.is("arkivmelding")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.serviceIdentifier", Matchers.is("DPV")));
    }

    @Test
    public void getCapabilitiesRequestWithWrongInputShouldGiveBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/capabilities/{orgnr}", wrongInputOrgnr))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void getCapabilitiesRequestWithNotExistingOrgnrShouldReturnNotFound() throws Exception {
        Mockito.when(service.getCapabilities(notExistingOrgnr)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/v1/capabilities/{orgnr}", notExistingOrgnr))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getCapabilitiesRequestCausesJsonParseExceptionShouldReturnServerError() throws Exception {
        Mockito.when(service.getCapabilities(orgnr)).thenThrow(JsonParseException.class);
        mockMvc.perform(get("/api/v1/capabilities/{orgnr}", orgnr))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

    }


}
