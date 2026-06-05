package org.techmarket.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global Error Handler for standardized API error responses
 * <p>
 * Returns consistent error format across all endpoints
 * Example error response:
 * {
 *   "timestamp": "2024-06-04T10:30:00Z",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Resource not found",
 *   "path": "/api/order/999"
 * }
 */
@Component
public class GlobalErrorHandler implements WebExceptionHandler {

    private final JsonMapper objectMapper = new JsonMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        Map<String, Object> errorAttributes = buildErrorResponse(exchange, ex);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(errorAttributes);
        } catch (Exception e) {
            return Mono.error(e);
        }

        exchange.getResponse().setStatusCode(
                HttpStatus.valueOf((Integer) errorAttributes.get("status"))
        );
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
        );
    }

    /**
     * Build standardized error response
     */
    private Map<String, Object> buildErrorResponse(ServerWebExchange exchange, Throwable error) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        // Determine HTTP status based on exception type
        if (error instanceof org.springframework.web.server.ResponseStatusException rse) {
            httpStatus = HttpStatus.valueOf(rse.getStatusCode().value());
        } else if (error instanceof org.springframework.security.core.AuthenticationException) {
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (error instanceof org.springframework.security.access.AccessDeniedException) {
            httpStatus = HttpStatus.FORBIDDEN;
        } else if (error instanceof org.springframework.web.server.ServerWebInputException) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (error instanceof IllegalArgumentException) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        // Build standardized error response
        errorAttributes.put("timestamp", new Date());
        errorAttributes.put("status", httpStatus.value());
        errorAttributes.put("error", httpStatus.getReasonPhrase());
        errorAttributes.put("message", getErrorMessage(error, httpStatus));
        errorAttributes.put("path", exchange.getRequest().getPath().value());

        // Add trace ID if available (from OpenTelemetry)
        String traceId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");
        if (traceId != null && !traceId.isEmpty()) {
            errorAttributes.put("traceId", traceId);
        }


        return errorAttributes;
    }

    /**
     * Generate error message based on exception type
     * Uses early returns to avoid chained if-else statements
     * Leverages HttpStatus.getReasonPhrase() for generic messages
     */
    private String getErrorMessage(Throwable error, HttpStatus status) {
        // Handle ResponseStatusException with custom reason
        if (error instanceof org.springframework.web.server.ResponseStatusException rse) {
            return rse.getReason() != null ? rse.getReason() : status.getReasonPhrase();
        }

        // Handle IllegalArgumentException with details
        if (error instanceof IllegalArgumentException iae) {
            return "Invalid argument: " + (iae.getMessage() != null ? iae.getMessage() : "");
        }

        // For security exceptions, provide context-specific guidance
        if (error instanceof org.springframework.security.core.AuthenticationException) {
            return "Authentication failed. Please provide valid credentials.";
        }

        if (error instanceof org.springframework.security.access.AccessDeniedException) {
            return "Access denied. You do not have permission to access this resource.";
        }

        if (error instanceof org.springframework.web.server.ServerWebInputException) {
            return "Please check your input parameters.";
        }

        // Default: use standard HTTP status reason phrase (e.g., "Not Found", "Bad Request")
        return status.getReasonPhrase();
    }
}





