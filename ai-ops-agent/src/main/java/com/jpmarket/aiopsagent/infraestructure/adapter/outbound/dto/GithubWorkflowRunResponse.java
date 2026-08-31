package com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record GithubWorkflowRunResponse(
    @JsonProperty("total_count") int totalCount,
    @JsonProperty("workflow_runs") List<WorkflowRun> workflowRuns
) {}
