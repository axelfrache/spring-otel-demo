package me.axelfrache.springoteldemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.micrometer.tracing.Tracer;
import org.slf4j.MDC;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Configuration
public class LoggingTracingConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public Filter tracingMdcFilter(Tracer tracer) {
        return new GenericFilterBean() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                try {
                    if (tracer.currentSpan() != null) {
                        String traceId = tracer.currentSpan().context().traceId();
                        String spanId = tracer.currentSpan().context().spanId();
                        MDC.put("traceId", traceId);
                        MDC.put("spanId", spanId);
                    }
                    chain.doFilter(request, response);
                } finally {
                    MDC.remove("traceId");
                    MDC.remove("spanId");
                }
            }
        };
    }
}
