package no.hvl.dat251.v22.SikkerKommunikasjon.entities;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;

@Data
public class ArkivMelding {

    @XmlElement(name = "hoveddokument")
    private String mainDocument;

}
