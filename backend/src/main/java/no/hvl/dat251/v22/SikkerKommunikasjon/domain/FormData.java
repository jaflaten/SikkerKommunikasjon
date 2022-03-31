package no.hvl.dat251.v22.SikkerKommunikasjon.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormData {
    String ssn;
    String name;
    String email;
    String receiver;
    String title;
    String content;
    Boolean isSensitive;
}
