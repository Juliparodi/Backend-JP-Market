package com.jpmarket.aiopsagent.application.service;

import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.GithubClient;
import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto.GithubWorkflowRunResponse;
import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto.WorkflowRun;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class DeploymentService {

    private final GithubClient githubClient;

    /**
     * Returns the most recent deployment filtered by service name.
     */
    public Optional<WorkflowRun> getLastDeployment(String serviceName) {
        log.info("Fetching last deployment for service: {}", serviceName);
        try {
            GithubWorkflowRunResponse response = githubClient.getRecentDeployments();

            if (response == null || response.workflowRuns() == null) {
                return Optional.empty();
            }

            return response.workflowRuns().stream()
                    .filter(run -> run.name() != null && run.name().toLowerCase().contains(serviceName.toLowerCase()))
                    .findFirst();
        } catch (Exception e) {
            log.error("Error fetching deployments from GitHub", e);
            return Optional.empty();
        }
    }

    /**
     * Returns the single most recent deployment across ALL services.
     */
    public Optional<WorkflowRun> getLastDeploymentOverall() {
        log.info("Fetching the overall last deployment across all services");
        try {
            GithubWorkflowRunResponse response = githubClient.getRecentDeployments();

            if (response == null || response.workflowRuns() == null || response.workflowRuns().isEmpty()) {
                return Optional.empty();
            }

            // GitHub returns runs sorted by created_at desc — the first is always the most recent
            return Optional.of(response.workflowRuns().getFirst());
        } catch (Exception e) {
            log.error("Error fetching deployments from GitHub", e);
            return Optional.empty();
        }
    }

    /**
     * Returns the N most recent FAILED deployments across all services.
     */
    public List<WorkflowRun> getRecentFailedDeployments(int limit) {
        log.info("Fetching up to {} failed deployments", limit);
        try {
            GithubWorkflowRunResponse response = githubClient.getRecentDeployments();

            if (response == null || response.workflowRuns() == null) {
                return List.of();
            }

            return response.workflowRuns().stream()
                    .filter(run -> "failure".equalsIgnoreCase(run.conclusion()))
                    .limit(limit)
                    .toList();
        } catch (Exception e) {
            log.error("Error fetching failed deployments from GitHub", e);
            return List.of();
        }
    }
}
