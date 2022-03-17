package no.hvl.dat251.v22.SikkerKommunikasjon.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class IntegrasjonspunktClient {

    @Qualifier(value = "WebClient")
    private final WebClient webClient;

    public String sendMultipartMessage(MultiValueMap body) {
        String response = webClient.post()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(body)
                .exchangeToMono(rs -> rs.bodyToMono(String.class))
                .block();

        return response;
    }
}
