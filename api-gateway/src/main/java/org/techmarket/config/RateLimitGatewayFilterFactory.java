package org.techmarket.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitGatewayFilterFactory
        extends AbstractGatewayFilterFactory<RateLimitGatewayFilterFactory.Config> {

    private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Instant> windowStarts = new ConcurrentHashMap<>();

    private final int maxRequests = 10; // per minute
    private final long windowSize = 60 * 1000; // 1 minute

    public RateLimitGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
            String clientIP = (remoteAddress != null && remoteAddress.getAddress() != null)
                    ? remoteAddress.getAddress().getHostAddress()
                    : "unknown";

            Instant now = Instant.now();
            Instant windowStart = windowStarts.get(clientIP);

            if (windowStart == null || now.isAfter(windowStart.plusMillis(windowSize))) {
                windowStarts.put(clientIP, now);
                requestCounts.put(clientIP, new AtomicInteger(1));
            } else {
                AtomicInteger count = requestCounts.computeIfAbsent(clientIP, k -> new AtomicInteger(0));
                int currentCount = count.incrementAndGet();

                if (currentCount > maxRequests) {
                    exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                    return exchange.getResponse().setComplete();
                }
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
        // optional config later
    }
}