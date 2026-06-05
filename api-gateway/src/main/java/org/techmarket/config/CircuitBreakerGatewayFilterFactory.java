package org.techmarket.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
        String circuitBreakerName = config.name;
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName);

        return (exchange, chain) -> {
            CircuitBreaker.State state = circuitBreaker.getState();

            if (state == CircuitBreaker.State.OPEN) {
                return onCircuitBreakerOpen(exchange, circuitBreakerName);
            }

            // Let the chain filter through - resilience4j will handle metrics automatically
            return chain.filter(exchange);
        };
    }

    /**
     * Handles requests when circuit breaker is OPEN
     * Returns 503 Service Unavailable status with descriptive message
     */
    private Mono<Void> onCircuitBreakerOpen(ServerWebExchange exchange, String serviceName) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        String errorMessage = "{\"error\":\"Service temporarily unavailable\",\"service\":\"" +
                serviceName + "\",\"message\":\"The requested service is currently unavailable. Please try again later.\"}";
        var body = exchange.getResponse().bufferFactory().wrap(errorMessage.getBytes());

        return exchange.getResponse().writeWith(Mono.just(body));
    }

    public static class Config {
        /**
         * Name of the circuit breaker instance (must match resilience4j config)
         * Example: "order-service"
         */
        public String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}



