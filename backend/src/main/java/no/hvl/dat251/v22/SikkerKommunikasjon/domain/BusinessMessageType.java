package no.hvl.dat251.v22.SikkerKommunikasjon.domain;

import lombok.Getter;
import no.difi.meldingsutveksling.jackson.StandardBusinessDocumentType;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public enum BusinessMessageType implements StandardBusinessDocumentType {
    ARKIVMELDING("arkivmelding");

    private final String type;

    BusinessMessageType(String type) {
        this.type = type;
    }

    public static BusinessMessageType fromType(String type) {
        return Arrays.stream(BusinessMessageType.values()).filter(p -> p.getType().equalsIgnoreCase(type))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown BusinessMessageType = %s. Expecting one of %s",
                        type,
                        Arrays.stream(values()).map(BusinessMessageType::getType).collect(Collectors.joining(",")))));
    }

    @Override
    public String getFieldName() {
        return type;
    }

    @Override
    public Class<?> getValueType() {
        return ArkivMeldingMessage.class;
    }
}
