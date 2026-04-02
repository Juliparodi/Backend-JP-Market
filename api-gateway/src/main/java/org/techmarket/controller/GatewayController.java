package org.techmarket.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/gateway")
public class GatewayController {

    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.just("Gateway is healthy");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('admin')")
    public Mono<String> admin() {
        return Mono.just("Admin access granted");
    }
}
