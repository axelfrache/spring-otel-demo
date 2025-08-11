package me.axelfrache.springoteldemo.service;

import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DemoService {
    private static final Logger log = LoggerFactory.getLogger(DemoService.class);

    public String doWork() {
        log.info("Début traitement dans DemoService");
        try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        log.info("Fin traitement dans DemoService");
        return "OK";
    }

    @Observed(name = "ui.button.click", contextualName = "button.generateTrace")
    public void generateTraceFromButton() {
        log.info("Bouton cliqué → génération d'une trace custom");
        innerStep();
    }

    @Observed(name = "ui.inner.step", contextualName = "service.innerStep")
    void innerStep() {
        try { Thread.sleep(120); } catch (InterruptedException ignored) {}
    }
}
