package no.hvl.dat251.v22.SikkerKommunikasjon.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.MediaType;

@Data
@Builder
public class Attachment {
    private String filename;
    private String content;
    private MediaType contentType;
}
