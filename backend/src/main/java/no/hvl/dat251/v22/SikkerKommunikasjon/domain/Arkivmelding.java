package no.hvl.dat251.v22.SikkerKommunikasjon.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Arkivmelding {
    private String receiver;
    private String mainDocument;
    private List<Attachment> attachments;
}
