package org.techmarket.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token Bucket Rate Limiter per client IP.
 * <p>
 * Each IP gets a bucket with capacity {@code burstCapacity} tokens.
 * Tokens are replenished at {@code replenishRate} tokens/second.
 * Each request consumes 1 token. When the bucket is empty the request
 * is rejected with HTTP 429 and standard {@code X-RateLimit-*} headers.
 * <p>
 * YAML configuration example (matches field names exactly):
 * <pre>
 *   - name: RateLimit
 *     args:
 *       replenishRate: 4    # tokens added per second
 *       burstCapacity: 8    # maximum bucket size (allows short bursts)
 * </pre>
 */
@Component
public class RateLimitGatewayFilterFactory
        extends AbstractGatewayFilterFactory<RateLimitGatewayFilterFactory.Config> {

    /** Per-IP token buckets. */
    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public RateLimitGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        int replenishRate  = config.replenishRate  > 0 ? config.replenishRate  : Config.DEFAULT_REPLENISH_RATE;
        int burstCapacity  = config.burstCapacity  > 0 ? config.burstCapacity  : Config.DEFAULT_BURST_CAPACITY;

        return (exchange, chain) -> {
            InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
            String clientIP = (remoteAddress != null && remoteAddress.getAddress() != null)
                    ? remoteAddress.getAddress().getHostAddress()
                    : "unknown";

            TokenBucket bucket = buckets.computeIfAbsent(
                    clientIP, k -> new TokenBucket(replenishRate, burstCapacity));

            boolean allowed = bucket.tryConsume();
            long remaining  = bucket.getTokens();

            exchange.getResponse().getHeaders().add("X-RateLimit-Limit",     String.valueOf(burstCapacity));
            exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", String.valueOf(Math.max(0, remaining)));

            if (!allowed) {
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                long resetEpochMs = Instant.now().plusSeconds(1).toEpochMilli();
                exchange.getResponse().getHeaders().add("X-RateLimit-Reset", String.valueOf(resetEpochMs));
                exchange.getResponse().getHeaders().add("Content-Type", "application/json");
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    // -------------------------------------------------------------------------
    // Token Bucket
    // -------------------------------------------------------------------------

    /**
     * Thread-safe token bucket implementation.
     * Tokens are added lazily on each {@link #tryConsume()} call
     * based on the time elapsed since the last refill.
     */
    static final class TokenBucket {

        private final int    replenishRate; // tokens per second
        private final int    burstCapacity; // max tokens
        private double       tokens;        // current token count (fractional during refill)
        private long         lastRefillNanos;

        TokenBucket(int replenishRate, int burstCapacity) {
            this.replenishRate   = replenishRate;
            this.burstCapacity   = burstCapacity;
            this.tokens          = burstCapacity; // start full
            this.lastRefillNanos = System.nanoTime();
        }

        /**
         * Refill the bucket based on elapsed time, then attempt to consume 1 token.
         *
         * @return {@code true} if the request is allowed, {@code false} if rate-limited.
         */
        synchronized boolean tryConsume() {
            refill();
            if (tokens >= 1.0) {
                tokens -= 1.0;
                return true;
            }
            return false;
        }

        /** Current token count (rounded down for display). */
        synchronized long getTokens() {
            refill();
            return (long) tokens;
        }

        private void refill() {
            long nowNanos     = System.nanoTime();
            double elapsedSec = (nowNanos - lastRefillNanos) / 1_000_000_000.0;
            double newTokens  = elapsedSec * replenishRate;

            if (newTokens > 0) {
                tokens          = Math.min(burstCapacity, tokens + newTokens);
                lastRefillNanos = nowNanos;
            }
        }
    }

    // -------------------------------------------------------------------------
    // Config
    // -------------------------------------------------------------------------

    public static class Config {

        static final int DEFAULT_REPLENISH_RATE = 10; // tokens per second
        static final int DEFAULT_BURST_CAPACITY  = 20; // max burst

        /** Tokens added to the bucket per second. Maps to YAML key {@code replenishRate}. */
        public int replenishRate = DEFAULT_REPLENISH_RATE;

        /** Maximum number of tokens the bucket can hold. Maps to YAML key {@code burstCapacity}. */
        public int burstCapacity = DEFAULT_BURST_CAPACITY;

        public Config() {}

        public Config(int replenishRate, int burstCapacity) {
            this.replenishRate = replenishRate;
            this.burstCapacity = burstCapacity;
        }

    }
}
