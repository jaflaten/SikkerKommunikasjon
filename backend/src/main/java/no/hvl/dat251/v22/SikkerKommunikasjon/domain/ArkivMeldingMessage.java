package no.hvl.dat251.v22.SikkerKommunikasjon.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@ToString
@NoArgsConstructor
@XmlRootElement(name = "arkivmelding", namespace = "urn:no:difi:meldingsutveksling:2.0")
public class ArkivMeldingMessage {

    @XmlElement(name = "sikkerhetsnivaa")
    @JsonProperty("sikkerhetsnivaa")
    private Integer securityLevel;

    @XmlElement(name = "hoveddokument")
    @JsonProperty("hoveddokument")
    private String mainDocument;

    public ArkivMeldingMessage setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
        return this;
    }
}
