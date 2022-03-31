package no.hvl.dat251.v22.SikkerKommunikasjon.service;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageStatusServiceTest {

    @Autowired
    MessageStatusService messageStatusService;

    private final String incomingJSONMessage =
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
