package no.hvl.dat251.v22.SikkerKommunikasjon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class IntegrasjonspunktController {

    @GetMapping("/capabilities/{orgnr}")
    public ResponseEntity<?> getCapabilities(@PathVariable String orgnr) {
        log.info(orgnr);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
