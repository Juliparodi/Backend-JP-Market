package org.techmarket.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitGatewayFilterFactory extends AbstractGatewayFilterFactory<RateLimitGatewayFilterFactory.Config> {

    private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Instant> windowStarts = new ConcurrentHashMap<>();
    private final int maxRequests = 10; // per minute
    private final long windowSize = 60 * 1000; // 1 minute in milliseconds

    public RateLimitGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String clientIP = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            Instant now = Instant.now();
            Instant windowStart = windowStarts.get(clientIP);

            if (windowStart == null || now.isAfter(windowStart.plusMillis(windowSize))) {
                windowStarts.put(clientIP, now);
                requestCounts.put(clientIP, new AtomicInteger(1));
            } else {
                AtomicInteger count = requestCounts.get(clientIP);
                if (count == null) {
                    count = new AtomicInteger(1);
                    requestCounts.put(clientIP, count);
                } else {
                    int currentCount = count.incrementAndGet();
                    if (currentCount > maxRequests) {
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        return exchange.getResponse().setComplete();
                    }
                }
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
        // Configuration properties if needed
    }
}
