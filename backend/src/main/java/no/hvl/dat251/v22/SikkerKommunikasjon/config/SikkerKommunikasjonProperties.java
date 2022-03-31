package no.hvl.dat251.v22.SikkerKommunikasjon.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "sk")
@Component
public class SikkerKommunikasjonProperties {

    @Valid
    private Integrasjonspunkt integrasjonspunkt;

    @Data
    public static class Integrasjonspunkt {
        @NotNull
        private String URL;

        @NotNull
        private String sender;
    }

}
