package no.hvl.dat251.v22.SikkerKommunikasjon.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.difi.meldingsutveksling.jackson.PartnerIdentifierModule;
import no.difi.meldingsutveksling.jackson.StandardBusinessDocumentModule;
import no.hvl.dat251.v22.SikkerKommunikasjon.domain.BusinessMessageType;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {

        public static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("Europe/Oslo");
        public static final ZoneId DEFAULT_ZONE_ID = DEFAULT_TIME_ZONE.toZoneId();

        @Bean
        @SuppressWarnings("deprecation") // JsonReadFeature not yet supported by builder
        public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer(Clock clock) {

            return builder ->
                    builder.modulesToInstall(new JavaTimeModule(), new StandardBusinessDocumentModule(BusinessMessageType::fromType), new PartnerIdentifierModule())
                            .serializationInclusion(JsonInclude.Include.NON_NULL)
                            .featuresToEnable(
                                    SerializationFeature.INDENT_OUTPUT,
                                    JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS,
                                    MapperFeature.DEFAULT_VIEW_INCLUSION)
                            .featuresToDisable(
                                    SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                                    SerializationFeature.CLOSE_CLOSEABLE,
                                    DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
                                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                                    DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        }

        @Bean
        public Clock systemClock() {
            return Clock.system(DEFAULT_ZONE_ID);
        }

}
