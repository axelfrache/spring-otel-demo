package me.axelfrache.springoteldemo.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.handler.DefaultTracingObservationHandler;
import io.micrometer.tracing.handler.PropagatingReceiverTracingObservationHandler;
import io.micrometer.tracing.handler.PropagatingSenderTracingObservationHandler;
import io.micrometer.tracing.propagation.Propagator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {

    @Bean
    public ObservationRegistry observationRegistry(Tracer tracer, Propagator propagator) {
        ObservationRegistry registry = ObservationRegistry.create();
        registry.observationConfig().observationHandler(
            new DefaultTracingObservationHandler(tracer))
            .observationHandler(new PropagatingReceiverTracingObservationHandler(tracer, propagator))
            .observationHandler(new PropagatingSenderTracingObservationHandler(tracer, propagator));
        return registry;
    }
}
