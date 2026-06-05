package org.techmarket.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom Rate Limiting Gateway Filter Factory
 * Implements sliding window rate limiting per client IP address
 * <p>
 * Usage in application.yml:
 * routes:
 *   - id: order-service
 *     uri: http://localhost:8081
 *     filters:
 *       - RateLimit=100,60000  # 100 requests per 60 seconds
 */
@Component
public class RateLimitGatewayFilterFactory
        extends AbstractGatewayFilterFactory<RateLimitGatewayFilterFactory.Config> {

    private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Instant> windowStarts = new ConcurrentHashMap<>();

    private static final int DEFAULT_MAX_REQUESTS = 100;
    private static final long DEFAULT_WINDOW_SIZE = 60_000; // 1 minute

    public RateLimitGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        int maxRequests = config.maxRequests > 0 ? config.maxRequests : DEFAULT_MAX_REQUESTS;
        long windowSize = config.windowSize > 0 ? config.windowSize : DEFAULT_WINDOW_SIZE;

        return (exchange, chain) -> {
            InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
            String clientIP = (remoteAddress != null && remoteAddress.getAddress() != null)
                    ? remoteAddress.getAddress().getHostAddress()
                    : "unknown";

            Instant now = Instant.now();
            Instant windowStart = windowStarts.get(clientIP);
            int currentCount = 1;
            boolean isLimited = false;

            if (windowStart == null || now.isAfter(windowStart.plusMillis(windowSize))) {
                // New window: reset counter
                windowStarts.put(clientIP, now);
                requestCounts.put(clientIP, new AtomicInteger(1));
                currentCount = 1;
            } else {
                // Existing window
                AtomicInteger count = requestCounts.computeIfAbsent(clientIP, k -> new AtomicInteger(0));
                currentCount = count.incrementAndGet();

                if (currentCount > maxRequests) {
                    isLimited = true;
                }
            }

            // Add rate limit headers to all responses
            exchange.getResponse().getHeaders().add("X-RateLimit-Limit", String.valueOf(maxRequests));
            exchange.getResponse().getHeaders().add("X-RateLimit-Remaining",
                    String.valueOf(Math.max(0, maxRequests - currentCount)));

            if (isLimited) {
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                exchange.getResponse().getHeaders().add("X-RateLimit-Reset",
                        String.valueOf(windowStart.plusMillis(windowSize).toEpochMilli()));
                return exchange.getResponse().setComplete();
            }


            return chain.filter(exchange);
        };
    }

    public static class Config {
        /**
         * Maximum number of requests allowed in the configured window
         */
        public int maxRequests = DEFAULT_MAX_REQUESTS;

        /**
         * Time window in milliseconds
         */
        public long windowSize = DEFAULT_WINDOW_SIZE;

        public Config() {
        }

        public Config(int maxRequests, long windowSize) {
            this.maxRequests = maxRequests;
            this.windowSize = windowSize;
        }

        public int getMaxRequests() {
            return maxRequests;
        }


        public long getWindowSize() {
            return windowSize;
        }

    }
}


