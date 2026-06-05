package org.techmarket.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RateLimitGatewayFilterFactory
 * Tests the rate limiting filter behavior with sliding window algorithm
 */
class RateLimitGatewayFilterFactoryTest {

    private RateLimitGatewayFilterFactory rateLimitFactory;

    @BeforeEach
    void setUp() {
        rateLimitFactory = new RateLimitGatewayFilterFactory();
    }

    /**
     * Test that requests within the limit are allowed
     * and requests exceeding the limit are rejected with 429
     */
    @Test
    void testRateLimitExceeded() {
        // Arrange
        RateLimitGatewayFilterFactory.Config config = new RateLimitGatewayFilterFactory.Config(3, 60_000);
        var filter = rateLimitFactory.apply(config);

        String clientIp = "127.0.0.1";
        GatewayFilterChain successChain = exchange -> {
            exchange.getResponse().setStatusCode(HttpStatus.OK);
            return Mono.empty();
        };

        // Act & Assert - Make 3 requests (should succeed)
        for (int i = 0; i < 3; i++) {
            MockServerHttpRequest request = MockServerHttpRequest
                    .get("/api/test")
                    .remoteAddress(new InetSocketAddress(clientIp, 8080))
                    .build();

            MockServerWebExchange exchange = MockServerWebExchange.from(request);

            StepVerifier.create(filter.filter(exchange, successChain))
                    .expectComplete()
                    .verify();

            assertEquals(HttpStatus.OK, exchange.getResponse().getStatusCode(),
                    "Request " + (i + 1) + " should succeed (status 200)");
        }

        // 4th request should be rejected with 429
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/test")
                .remoteAddress(new InetSocketAddress(clientIp, 8080))
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, successChain))
                .expectComplete()
                .verify();

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, exchange.getResponse().getStatusCode(),
                "Request 4 should be rejected with 429 Too Many Requests");
    }

    /**
     * Test that different clients have independent rate limit counters
     */
    @Test
    void testRateLimitPerClientIp() {
        // Arrange
        RateLimitGatewayFilterFactory.Config config = new RateLimitGatewayFilterFactory.Config(2, 60_000);
        var filter = rateLimitFactory.apply(config);

        GatewayFilterChain successChain = exchange -> {
            exchange.getResponse().setStatusCode(HttpStatus.OK);
            return Mono.empty();
        };

        // Act & Assert - Client 1: 2 successful requests
        for (int i = 0; i < 2; i++) {
            MockServerHttpRequest request = MockServerHttpRequest
                    .get("/api/test")
                    .remoteAddress(new InetSocketAddress("192.168.1.1", 8080))
                    .build();

            MockServerWebExchange exchange = MockServerWebExchange.from(request);

            StepVerifier.create(filter.filter(exchange, successChain))
                    .expectComplete()
                    .verify();

            assertEquals(HttpStatus.OK, exchange.getResponse().getStatusCode());
        }

        // Client 2: Should also get 2 successful requests (independent counter)
        for (int i = 0; i < 2; i++) {
            MockServerHttpRequest request = MockServerHttpRequest
                    .get("/api/test")
                    .remoteAddress(new InetSocketAddress("192.168.1.2", 8080))
                    .build();

            MockServerWebExchange exchange = MockServerWebExchange.from(request);

            StepVerifier.create(filter.filter(exchange, successChain))
                    .expectComplete()
                    .verify();

            assertEquals(HttpStatus.OK, exchange.getResponse().getStatusCode(),
                    "Client 2 should have independent rate limit counter");
        }
    }

    /**
     * Test that rate limit response headers are correctly set
     */
    @Test
    void testRateLimitHeadersPresent() {
        // Arrange
        RateLimitGatewayFilterFactory.Config config = new RateLimitGatewayFilterFactory.Config(10, 60_000);
        var filter = rateLimitFactory.apply(config);

        GatewayFilterChain successChain = exchange -> {
            exchange.getResponse().setStatusCode(HttpStatus.OK);
            return Mono.empty();
        };

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/test")
                .remoteAddress(new InetSocketAddress("10.0.0.1", 8080))
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Act
        StepVerifier.create(filter.filter(exchange, successChain))
                .expectComplete()
                .verify();

        // Assert - Check for rate limit headers
        assertNotNull(exchange.getResponse().getHeaders().getFirst("X-RateLimit-Limit"),
                "Response should contain X-RateLimit-Limit header");
        assertNotNull(exchange.getResponse().getHeaders().getFirst("X-RateLimit-Remaining"),
                "Response should contain X-RateLimit-Remaining header");

        assertEquals("10", exchange.getResponse().getHeaders().getFirst("X-RateLimit-Limit"),
                "X-RateLimit-Limit should be 10");
        assertEquals("9", exchange.getResponse().getHeaders().getFirst("X-RateLimit-Remaining"),
                "X-RateLimit-Remaining should be 9 after first request");
    }

    /**
     * Test that rate limit headers show 0 remaining when limit is exceeded
     */
    @Test
    void testRateLimitHeadersWhenExceeded() {
        // Arrange
        RateLimitGatewayFilterFactory.Config config = new RateLimitGatewayFilterFactory.Config(1, 60_000);
        var filter = rateLimitFactory.apply(config);

        GatewayFilterChain successChain = exchange -> {
            exchange.getResponse().setStatusCode(HttpStatus.OK);
            return Mono.empty();
        };

        String clientIp = "10.0.0.1";

        // Make first request (should succeed)
        MockServerHttpRequest request1 = MockServerHttpRequest
                .get("/api/test")
                .remoteAddress(new InetSocketAddress(clientIp, 8080))
                .build();

        MockServerWebExchange exchange1 = MockServerWebExchange.from(request1);

        StepVerifier.create(filter.filter(exchange1, successChain))
                .expectComplete()
                .verify();

        assertEquals(HttpStatus.OK, exchange1.getResponse().getStatusCode());

        // Make second request (should fail with 429)
        MockServerHttpRequest request2 = MockServerHttpRequest
                .get("/api/test")
                .remoteAddress(new InetSocketAddress(clientIp, 8080))
                .build();

        MockServerWebExchange exchange2 = MockServerWebExchange.from(request2);

        StepVerifier.create(filter.filter(exchange2, successChain))
                .expectComplete()
                .verify();

        // Assert - Status is 429 and headers show limit exceeded
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, exchange2.getResponse().getStatusCode());
        assertEquals("1", exchange2.getResponse().getHeaders().getFirst("X-RateLimit-Limit"));
        assertEquals("0", exchange2.getResponse().getHeaders().getFirst("X-RateLimit-Remaining"));
        assertNotNull(exchange2.getResponse().getHeaders().getFirst("X-RateLimit-Reset"),
                "X-RateLimit-Reset header should be present when limit exceeded");
    }

    /**
     * Test that configuration values are properly set
     */
    @Test
    void testConfigWithCustomValues() {
        // Arrange
        RateLimitGatewayFilterFactory.Config config = new RateLimitGatewayFilterFactory.Config(5, 120_000);

        // Assert
        assertEquals(5, config.getMaxRequests());
        assertEquals(120_000, config.getWindowSize());
    }

    /**
     * Test that default configuration values are used when not specified
     */
    @Test
    void testConfigWithDefaultValues() {
        // Arrange
        RateLimitGatewayFilterFactory.Config config = new RateLimitGatewayFilterFactory.Config();

        // Assert
        assertEquals(5, config.getMaxRequests(), "Default maxRequests should be 100");
        assertEquals(60_000, config.getWindowSize(), "Default windowSize should be 60000ms");
    }
}

