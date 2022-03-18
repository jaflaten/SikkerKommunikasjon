package no.hvl.dat251.v22.SikkerKommunikasjon.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat251.v22.SikkerKommunikasjon.domain.Arkivmelding;
import no.hvl.dat251.v22.SikkerKommunikasjon.domain.Attachment;
import no.hvl.dat251.v22.SikkerKommunikasjon.domain.FormData;
import no.hvl.dat251.v22.SikkerKommunikasjon.service.IntegrasjonspunktService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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

    String json;
    ObjectMapper mapper = new ObjectMapper();

    MockMultipartFile mockMultipartFile;

    String receiver;

    Arkivmelding arkivmelding;

    Attachment attachment;
    Attachment form;


    @Before
    public void setup() throws IOException {
        receiver = "507369790";
        json = "{ \"process\" : \"arkivmelding\", \"serviceIdentifier\" : \"DPV\" }";
        jsonOptional = Optional.of(mapper.readTree(json));
        formData = new FormData("120592640214", "Ola Nordmann", "norsk.email@difi.no", receiver,
                "Manglende snø i Bergen", "Det er ikke nok snø i Bergen!", false);

        mockMultipartFile = new MockMultipartFile("test.pdf", "test.pdf", "application/pdf", "some content".getBytes());
        String arkivmeldingXmlFile = new String(new ClassPathResource("arkivmelding.xml").getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        attachment = Attachment.builder()
                .filename(mockMultipartFile.getName())
                .content(new String(mockMultipartFile.getResource().getInputStream().readAllBytes(), StandardCharsets.UTF_8))
                .contentType(MediaType.APPLICATION_PDF)
                .build();
        form = Attachment.builder()
                .content(formData.toString())
                .contentType(MediaType.TEXT_PLAIN)
                .filename("form")
                .build();
        List<Attachment> attachments = new ArrayList<>();
        attachments.add(form);
        attachments.add(attachment);

        arkivmelding = Arkivmelding.builder()
                .mainDocument(arkivmeldingXmlFile)
                .receiver(receiver)
                .attachments(attachments)
                .build();
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
        when(service.sendMultipartMessage(arkivmelding)).thenReturn(jsonOptional);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/messages/multipart")
                        .file(mockMultipartFile)
                        .param("ssn", "120592640214")
                        .param("name", "Ola Nordmann")
                        .param("email", "norsk.email@difi.no")
                        .param("receiver", "507369790")
                        .param("title", "Manglende snø i Bergen")
                        .param("content", "Det er ikke nok snø i Bergen!")
                        .param("isSensitive", "false"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void sendMultipartMessageServiceCallHasEmptyResponseReturnBadRequest() throws Exception {
        when(service.sendMultipartMessage(arkivmelding)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/messages/multipart")
                        .file(mockMultipartFile)
                        .param("ssn", "120592640214")
                        .param("name", "Ola Nordmann")
                        .param("email", "norsk.email@difi.no")
                        .param("receiver", "507369790")
                        .param("title", "Manglende snø i Bergen")
                        .param("content", "Det er ikke nok snø i Bergen!")
                        .param("isSensitive", "false"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void createArkivmeldingShouldCreateExpectedArkivmelding() {

    }
}
