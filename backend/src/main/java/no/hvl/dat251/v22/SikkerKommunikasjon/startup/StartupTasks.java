package no.hvl.dat251.v22.SikkerKommunikasjon.startup;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.hvl.dat251.v22.SikkerKommunikasjon.config.SikkerKommunikasjonProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartupTasks {

    @Qualifier("WebClient")
    private final WebClient webClient;

    private final ObjectMapper objectMapper;
    private final SikkerKommunikasjonProperties properties;


    private URI getIntegrasjonspunktURI(String path) {
        return UriComponentsBuilder.fromUriString(properties.getIntegrasjonspunkt().getURL())
                .path(path)
                .build()
                .toUri();
    }

    private String getHostName() {
        String hostName = System.getenv("HOST_NAME");
        if (hostName == null) {
            return "localhost";
        }
        return hostName;
    }

    /**
     * This method makes a POST-request to subscribe to message statuses from Integrasjonspunktet.
     */
    @PostConstruct
    public void postMessageStatusSubscription() {
        String hostName = getHostName();

        String body =
                "{\n" +
                        "  \"name\" : \"Incoming messages\",\n" +
                        "  \"pushEndpoint\" : " + hostName + "/api/v1/messaging/incoming\",\n" +
                        "  \"resource\" : \"messages\",\n" +
                        "  \"event\" : \"status\",\n" +
                        "  \"filter\" : \"direction=INCOMING\"\n" +
                        "}";

        try {
            String res = webClient.post()
                    .uri(getIntegrasjonspunktURI("subscriptions"))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Connected to " + getIntegrasjonspunktURI("subscriptions") + " [POST]");
            log.info("Successfully subscribed to Integrasjonspunktet, response:\n" + res);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError())
                log.info("Could not subscribe to message statuses, likely already subscribed.");
            else
                e.printStackTrace();
        } catch (Exception e) {
            log.error("Error while trying to subscribe to message statuses.");
        }
    }

    @PostConstruct
    public void logAllActiveSubscriptions() {
        try {
            String res = webClient
                    .get()
                    .uri(getIntegrasjonspunktURI("subscriptions"))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("Active message status subscriptions:\n" + res);
        } catch (WebClientResponseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            log.error("Error while trying to subscribe to message statuses.");
        }
    }
}
