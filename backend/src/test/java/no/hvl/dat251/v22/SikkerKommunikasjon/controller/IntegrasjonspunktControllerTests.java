package no.hvl.dat251.v22.SikkerKommunikasjon.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat251.v22.SikkerKommunikasjon.entities.FormData;
import no.hvl.dat251.v22.SikkerKommunikasjon.service.IntegrasjonspunktService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(IntegrasjonspunktController.class)
public class IntegrasjonspunktControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    IntegrasjonspunktService service;

    @MockBean
    ObjectMapper objectMapper;



    private final String orgnr = "123456789";
    private final String wrongInputOrgnr = "1234567891";
    private final String notExistingOrgnr = "999888777";

    Optional<JsonNode> jsonOptional;
    FormData formData;
    MockMultipartFile attachment;

    String json;
    ObjectMapper mapper = new ObjectMapper();

    File testfile;

    @Before
    public void setup() throws JsonProcessingException, URISyntaxException {
        json = "{ \"process\" : \"arkivmelding\", \"serviceIdentifier\" : \"DPV\" }";
        jsonOptional = Optional.of(mapper.readTree(json));
        testfile = new File(String.valueOf(Paths.get(ClassLoader.getSystemResource("arkivmelding.xml").toString())));
        formData = new FormData("12345678910", "Kari Nordmann", "kari@nordmann.no", orgnr,
                "No snow in Bergen in march", "I want more snow in Bergen to go skiing", false);

    }

    @Test
    public void getCapabilitiesRequestReturnsStatus200Ok() throws Exception {
        when(service.getCapabilities(orgnr)).thenReturn(jsonOptional);
        mockMvc.perform(get("/api/v1/capabilities/{orgnr}", orgnr))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getCapabilitiesRequestShouldReturnExpectedContent() throws Exception {
        when(service.getCapabilities(orgnr)).thenReturn(jsonOptional);
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
        when(service.getCapabilities(notExistingOrgnr)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/v1/capabilities/{orgnr}", notExistingOrgnr))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getCapabilitiesRequestCausesJsonParseExceptionShouldReturnServerError() throws Exception {
        when(service.getCapabilities(orgnr)).thenThrow(JsonParseException.class);
        mockMvc.perform(get("/api/v1/capabilities/{orgnr}", orgnr))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

    }

    @Test
    public void sendMultipartMessageShouldReturn200Ok() throws Exception {
        when(service.createAndSendMultipartMessage(formData, attachment)).thenReturn(jsonOptional);
        mockMvc.perform(post("/api/v1/messages/multipart")
                        .param("ssn", "120592640214")
                        .param("name", "Ola Nordmann")
                        .param("email", "norsk.email@difi.no")
                        .param("receiver", "507369790")
                        .param("title", "Manglende snø i Bergen")
                        .param("content", "Det er ikke nok snø i Bergen!")
                        .param("isSensitive", "false")
                        .param("attachment", "blabla"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
