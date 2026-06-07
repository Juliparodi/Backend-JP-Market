package org.techmarket.config;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Custom CircuitBreaker Gateway Filter that integrates with Resilience4j.
 * <p>
 * Unlike a plain state-check, this filter wraps the downstream call with
 * {@link CircuitBreakerOperator}, ensuring every request outcome (success,
 * failure, timeout) is recorded into the Resilience4j metrics. This allows
 * the circuit breaker to automatically trip when the configured
 * {@code failureRateThreshold} is exceeded.
 * <p>
 * Circuit breaker instances are configured in {@code application.yml} under
 * {@code resilience4j.circuitbreaker.instances}.
 * <p>
 * YAML configuration example:
 * <pre>
 *   - name: CircuitBreaker
 *     args:
 *       name: order-service   # must match a resilience4j instance name
 * </pre>
 * <p>
 * When the circuit is OPEN, {@link CallNotPermittedException} is thrown by
 * Resilience4j and caught here to return an HTTP 503 with a JSON body.
 */
@Component
public class CircuitBreakerGatewayFilterFactory
        extends AbstractGatewayFilterFactory<CircuitBreakerGatewayFilterFactory.Config> {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakerGatewayFilterFactory(CircuitBreakerRegistry circuitBreakerRegistry) {
        super(Config.class);
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @Override
    public GatewayFilter apply(Config config) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(config.getName());

        return (exchange, chain) ->
            chain.filter(exchange)
                 // Wrap the entire downstream chain with the Resilience4j operator.
                 // This records every call outcome so the breaker can trip/recover.
                 .transform(CircuitBreakerOperator.of(circuitBreaker))
                 .onErrorResume(CallNotPermittedException.class,
                         ex -> onCircuitOpen(exchange, config.getName()))
                 .onErrorResume(ex -> isServerError(ex),
                         ex -> {
                             // Other errors still need to be surfaced; re-throw so upstream
                             // error handlers (GlobalErrorHandler) deal with them.
                             return Mono.error(ex);
                         });
    }

    /**
     * Returns {@code true} for server-side errors that should propagate normally.
     * Resilience4j already recorded these; we just don't want to swallow them.
     */
    private boolean isServerError(Throwable ex) {
        return !(ex instanceof CallNotPermittedException);
    }

    /**
     * Called when the circuit is OPEN and the request is not permitted.
     * Returns HTTP 503 Service Unavailable with a descriptive JSON body.
     */
    private Mono<Void> onCircuitOpen(ServerWebExchange exchange, String serviceName) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        exchange.getResponse().getHeaders().setContentType(
                MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"error\":\"Service temporarily unavailable\",\"service\":\"%s\","
                + "\"message\":\"The circuit breaker is OPEN. The service is experiencing issues. "
                + "Please try again later.\"}",
                serviceName);

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse()
                       .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }

    // -------------------------------------------------------------------------
    // Config
    // -------------------------------------------------------------------------

    public static class Config {

        /**
         * Name of the Resilience4j circuit breaker instance.
         * Must match an entry under {@code resilience4j.circuitbreaker.instances} in YAML.
         * Example: {@code "order-service"}
         */
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
