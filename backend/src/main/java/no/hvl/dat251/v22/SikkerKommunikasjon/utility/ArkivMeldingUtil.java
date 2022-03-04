package no.hvl.dat251.v22.SikkerKommunikasjon.utility;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public class ArkivMeldingUtil {

    public Optional<String> arkivMeldingXMLToString()  {

        try {

            InputStream stream = new ClassPathResource("arkivmelding.xml").getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String xmlString = reader.lines().collect(Collectors.joining());

            return Optional.of(xmlString);
        }

        catch(IOException e) {
            log.error("Transform XML to String failed", e);
        }

        return Optional.empty();
    }
}
