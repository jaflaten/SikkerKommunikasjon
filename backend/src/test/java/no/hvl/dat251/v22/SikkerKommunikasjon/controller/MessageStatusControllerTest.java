package no.hvl.dat251.v22.SikkerKommunikasjon.controller;

import no.hvl.dat251.v22.SikkerKommunikasjon.service.MessageStatusService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@RunWith(SpringRunner.class)
@WebMvcTest(MessageStatusController.class)
public class MessageStatusControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private MessageStatusService messageStatusService;

    private String incomingJSONMessage;

    @Before
    public void setup() {
        incomingJSONMessage  =
            "{\n" +
            "  \"createdTs\" : \"2019-03-25T12:38:23+01:00\",\n" +
            "  \"resource\" : \"messages\",\n" +
            "  \"event\" : \"status\",\n" +
            "  \"messageId\" : \"e590b46b-1b40-420f-abc6-42d70d63cffc\",\n" +
            "  \"conversationId\" : \"36b486c7-10b6-4a0e-b6d8-2b018faba353\",\n" +
            "  \"direction\" : \"INCOMING\",\n" +
            "  \"serviceIdentifier\" : \"DPO\",\n" +
            "  \"status\" : \"LEVETID_UTLOPT\",\n" +
            "  \"description\" : \"Levetiden for meldingen er utgått. Må sendes på nytt\"\n" +
            "}";
    }

    @Test
    public void postMessageStatusShouldReturnStatusOk() throws Exception {
        Mockito.doNothing().when(messageStatusService).handleIncomingMessage(incomingJSONMessage);

        mockMvc.perform(
                post("/api/v1/messaging/incoming").content(incomingJSONMessage)
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
