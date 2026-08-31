package com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record WorkflowRun(
    Long id,
    String name,
    String status,
    String conclusion,
    @JsonProperty("created_at") Instant createdAt,
    @JsonProperty("updated_at") Instant updatedAt,
    @JsonProperty("html_url") String htmlUrl
) {}
