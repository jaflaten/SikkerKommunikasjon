package no.hvl.dat251.v22.SikkerKommunikasjon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocumentHeader;
import no.hvl.dat251.v22.SikkerKommunikasjon.entities.ArkivMelding;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IntegrasjonspunktServiceTests {

    @Autowired
    IntegrasjonspunktService service;

    @MockBean
    WebClient webClient;

    @MockBean
    ObjectMapper mapper;

    ArkivMelding arkivMelding;
    String arkivmeldingXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><arkivmelding xmlns=\"http://www.arkivverket.no/standarder/noark5/arkivmelding\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.arkivverket.no/standarder/noark5/arkivmelding arkivmelding.xsd\">    <system>LandLord</system>    <meldingId>3380ed76-5d4c-43e7-aa70-8ed8d97e4835</meldingId>    <tidspunkt>20170523</tidspunkt>    <antallFiler>1</antallFiler>    <mappe xsi:type=\"saksmappe\">        <systemID>43fbe161-7aac-4c9f-a888-d8167aab4144</systemID>        <ReferanseForeldermappe>43fbe161-7aac-4c9f-a888-d8167aab4144</ReferanseForeldermappe>        <tittel>Nye lysrør Hauketo Skole</tittel>        <opprettetDato>20170601</opprettetDato>        <opprettetAv/>        <klassifikasjon>            <referanseKlassifikasjonssystem>Funksjoner</referanseKlassifikasjonssystem>            <klasseID>vedlikehold av skole</klasseID>            <tittel>vedlikehold av skole</tittel>            <opprettetDato>20170523</opprettetDato>            <opprettetAv>Knut Hansen</opprettetAv>        </klassifikasjon>        <klassifikasjon>            <referanseKlassifikasjonssystem>Objekter</referanseKlassifikasjonssystem>            <klasseID>20500</klasseID>            <tittel>Hauketo Skole</tittel>            <opprettetDato>20170523</opprettetDato>            <opprettetAv>Knut Hansen</opprettetAv>        </klassifikasjon>        <basisregistrering xsi:type=\"journalpost\">            <systemID>430a6710-a3d4-4863-8bd0-5eb1021bee45</systemID>            <opprettetDato>20120217</opprettetDato>            <opprettetAv>LandLord</opprettetAv>            <arkivertDato>20120217</arkivertDato>            <arkivertAv>LandLord</arkivertAv>            <referanseForelderMappe>43fbe161-7aac-4c9f-a888-d8167aab4144</referanseForelderMappe>            <dokumentbeskrivelse>                <systemID>3e518e5b-a361-42c7-8668-bcbb9eecf18d</systemID>                <dokumenttype>Bestilling</dokumenttype>                <dokumentstatus>Dokumentet er ferdigstilt</dokumentstatus>                <tittel>Bestilling - nye lysrør</tittel>                <opprettetDato>20120217</opprettetDato>                <opprettetAv>Landlord</opprettetAv>                <tilknyttetRegistreringSom>Hoveddokument</tilknyttetRegistreringSom>                <dokumentnummer>1</dokumentnummer>                <tilknyttetDato>20120217</tilknyttetDato>                <tilknyttetAv>Landlord</tilknyttetAv>                <dokumentobjekt>                    <versjonsnummer>1</versjonsnummer>                    <variantformat>Produksjonsformat</variantformat>                    <opprettetDato>20120217</opprettetDato>                    <opprettetAv>Landlord</opprettetAv>                    <referanseDokumentfil>test.pdf</referanseDokumentfil>                </dokumentobjekt>            </dokumentbeskrivelse>            <tittel>Rick and Morty season 5 20.06.21</tittel>            <offentligTittel>Rick and Morty season 5</offentligTittel>            <virksomhetsspesifikkeMetadata>                <forvaltningsnummer>20050</forvaltningsnummer>                <objektnavn>Hauketo Skole</objektnavn>                <eiendom>200501</eiendom>                <bygning>2005001</bygning>                <bestillingtype>Materiell, elektro</bestillingtype>                <rammeavtale>K-123123-elektriker</rammeavtale>            </virksomhetsspesifikkeMetadata>            <journalposttype>Utgående dokument</journalposttype>            <journalstatus>Journalført</journalstatus>            <journaldato>20170523</journaldato>            <korrespondansepart>                <korrespondanseparttype>Mottaker</korrespondanseparttype>                <korrespondansepartNavn>elektrikeren AS, Veien 100, Oslo</korrespondansepartNavn>            </korrespondansepart>        </basisregistrering>        <saksdato>20170601</saksdato>        <administrativEnhet>Blah</administrativEnhet>        <saksansvarlig>KNUTKÅRE</saksansvarlig>        <saksstatus>Avsluttet</saksstatus>    </mappe></arkivmelding>";
    @Before
    public void setup() {

        arkivMelding = new ArkivMelding();
        arkivMelding.setMainDocument(arkivmeldingXML);
    }

    @Test
    public void getArkivmeldingXMLShouldReturnArkivmelding() {

        Assertions.assertEquals(service.getArkivmeldingXML().get(), arkivMelding);
    }

    @Test
    public void createSBDHeaderShouldReturnCorrectHeader() {

        final String testReceiver = "test";

        StandardBusinessDocumentHeader header = service.createSBDHeader(testReceiver);

        Assertions.assertEquals(header.getDocumentIdentification().getStandard(), "urn:no:difi:arkivmelding:xsd::arkivmelding");
        Assertions.assertEquals(header.getDocumentIdentification().getTypeVersion(), "1.0");
        Assertions.assertEquals(header.getDocumentIdentification().getType(), "arkivmelding");

        Assertions.assertEquals(header.getReceiver().iterator().next().getIdentifier().getValue(), "0192:" + testReceiver);
        Assertions.assertEquals(header.getReceiver().iterator().next().getIdentifier().getAuthority(), "iso6523-actorid-upis");

        Assertions.assertEquals(header.getSender().iterator().next().getIdentifier().getValue(), "0192:987464291");
        Assertions.assertEquals(header.getSender().iterator().next().getIdentifier().getAuthority(), "iso6523-actorid-upis");

        Assertions.assertEquals(header.getBusinessScope().getScope().iterator().next().getIdentifier(), "urn:no:difi:profile:arkivmelding:administrasjon:ver1.0");
        Assertions.assertEquals(header.getBusinessScope().getScope().iterator().next().getType(), "ConversationId");
    }

}