package me.axelfrache.springoteldemo.service;

import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DemoService {
    private static final Logger log = LoggerFactory.getLogger(DemoService.class);

    @Timed(value = "demo.work", description = "Temps de traitement fictif")
    public String doWork() {
        log.info("Début traitement dans DemoService");
        try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        log.info("Fin traitement dans DemoService");
        return "OK";
    }
}