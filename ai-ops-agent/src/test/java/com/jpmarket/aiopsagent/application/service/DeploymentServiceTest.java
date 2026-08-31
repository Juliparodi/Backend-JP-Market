package com.jpmarket.aiopsagent.application.service;

import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.GithubClient;
import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto.GithubWorkflowRunResponse;
import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto.WorkflowRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeploymentServiceTest {

    @Mock
    private GithubClient githubClient;

    @InjectMocks
    private DeploymentService deploymentService;

    private WorkflowRun successRun;
    private WorkflowRun failureRun;
    private WorkflowRun orderRun;

    @BeforeEach
    void setUp() {
        successRun = new WorkflowRun(1L, "CI - product-service", "completed", "success",
                Instant.parse("2024-01-01T10:00:00Z"), Instant.parse("2024-01-01T10:05:00Z"),
                "https://github.com/runs/1");
        failureRun = new WorkflowRun(2L, "CI - inventory-service", "completed", "failure",
                Instant.parse("2024-01-01T09:00:00Z"), Instant.parse("2024-01-01T09:05:00Z"),
                "https://github.com/runs/2");
        orderRun = new WorkflowRun(3L, "CI - order-service", "completed", "success",
                Instant.parse("2024-01-01T08:00:00Z"), Instant.parse("2024-01-01T08:05:00Z"),
                "https://github.com/runs/3");
    }

    // --- getLastDeployment(serviceName) ---

    @Test
    void getLastDeployment_shouldReturnMatchingRun() {
        when(githubClient.getRecentDeployments())
                .thenReturn(new GithubWorkflowRunResponse(3, List.of(successRun, failureRun, orderRun)));

        Optional<WorkflowRun> result = deploymentService.getLastDeployment("order-service");

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("CI - order-service");
    }

    @Test
    void getLastDeployment_shouldReturnEmptyWhenNoMatchFound() {
        when(githubClient.getRecentDeployments())
                .thenReturn(new GithubWorkflowRunResponse(1, List.of(successRun)));

        Optional<WorkflowRun> result = deploymentService.getLastDeployment("unknown-service");

        assertThat(result).isEmpty();
    }

    @Test
    void getLastDeployment_shouldReturnEmptyWhenResponseIsNull() {
        when(githubClient.getRecentDeployments()).thenReturn(null);

        Optional<WorkflowRun> result = deploymentService.getLastDeployment("order");

        assertThat(result).isEmpty();
    }

    @Test
    void getLastDeployment_shouldReturnEmptyWhenRunsListIsNull() {
        when(githubClient.getRecentDeployments())
                .thenReturn(new GithubWorkflowRunResponse(0, null));

        Optional<WorkflowRun> result = deploymentService.getLastDeployment("order");

        assertThat(result).isEmpty();
    }

    @Test
    void getLastDeployment_shouldReturnEmptyOnException() {
        when(githubClient.getRecentDeployments()).thenThrow(new RuntimeException("Network error"));

        Optional<WorkflowRun> result = deploymentService.getLastDeployment("order");

        assertThat(result).isEmpty();
    }

    // --- getLastDeploymentOverall() ---

    @Test
    void getLastDeploymentOverall_shouldReturnFirstRun() {
        when(githubClient.getRecentDeployments())
                .thenReturn(new GithubWorkflowRunResponse(3, List.of(successRun, failureRun, orderRun)));

        Optional<WorkflowRun> result = deploymentService.getLastDeploymentOverall();

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("CI - product-service");
    }

    @Test
    void getLastDeploymentOverall_shouldReturnEmptyWhenListEmpty() {
        when(githubClient.getRecentDeployments())
                .thenReturn(new GithubWorkflowRunResponse(0, List.of()));

        Optional<WorkflowRun> result = deploymentService.getLastDeploymentOverall();

        assertThat(result).isEmpty();
    }

    @Test
    void getLastDeploymentOverall_shouldReturnEmptyOnException() {
        when(githubClient.getRecentDeployments()).thenThrow(new RuntimeException("Timeout"));

        Optional<WorkflowRun> result = deploymentService.getLastDeploymentOverall();

        assertThat(result).isEmpty();
    }

    // --- getRecentFailedDeployments(limit) ---

    @Test
    void getRecentFailedDeployments_shouldReturnOnlyFailures() {
        when(githubClient.getRecentDeployments())
                .thenReturn(new GithubWorkflowRunResponse(3, List.of(successRun, failureRun, orderRun)));

        List<WorkflowRun> result = deploymentService.getRecentFailedDeployments(5);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().conclusion()).isEqualTo("failure");
        assertThat(result.getFirst().name()).isEqualTo("CI - inventory-service");
    }

    @Test
    void getRecentFailedDeployments_shouldRespectLimit() {
        WorkflowRun anotherFailure = new WorkflowRun(4L, "CI - notification-service", "completed", "failure",
                Instant.parse("2024-01-01T07:00:00Z"), Instant.parse("2024-01-01T07:05:00Z"), null);

        when(githubClient.getRecentDeployments())
                .thenReturn(new GithubWorkflowRunResponse(4, List.of(successRun, failureRun, orderRun, anotherFailure)));

        List<WorkflowRun> result = deploymentService.getRecentFailedDeployments(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void getRecentFailedDeployments_shouldReturnEmptyWhenNoFailures() {
        when(githubClient.getRecentDeployments())
                .thenReturn(new GithubWorkflowRunResponse(1, List.of(successRun)));

        List<WorkflowRun> result = deploymentService.getRecentFailedDeployments(5);

        assertThat(result).isEmpty();
    }

    @Test
    void getRecentFailedDeployments_shouldReturnEmptyOnException() {
        when(githubClient.getRecentDeployments()).thenThrow(new RuntimeException("error"));

        List<WorkflowRun> result = deploymentService.getRecentFailedDeployments(5);

        assertThat(result).isEmpty();
    }

    @Test
    void getRecentFailedDeployments_shouldReturnEmptyWhenRunsNull() {
        when(githubClient.getRecentDeployments())
                .thenReturn(new GithubWorkflowRunResponse(0, null));

        List<WorkflowRun> result = deploymentService.getRecentFailedDeployments(5);

        assertThat(result).isEmpty();
    }
}
