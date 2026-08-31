package com.jpmarket.aiopsagent.application.tools;

import com.jpmarket.aiopsagent.application.service.DeploymentService;
import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto.WorkflowRun;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeploymentToolTest {

    @Mock
    private DeploymentService deploymentService;

    @InjectMocks
    private DeploymentTool deploymentTool;

    private static final WorkflowRun SUCCESSFUL_RUN = new WorkflowRun(
            1L, "CI - order-service", "completed", "success",
            Instant.parse("2024-06-01T10:00:00Z"), Instant.parse("2024-06-01T10:10:00Z"),
            "https://github.com/runs/1");

    private static final WorkflowRun FAILED_RUN = new WorkflowRun(
            2L, "CI - inventory-service", "completed", "failure",
            Instant.parse("2024-06-01T08:00:00Z"), Instant.parse("2024-06-01T08:10:00Z"),
            "https://github.com/runs/2");

    private static final WorkflowRun IN_PROGRESS_RUN = new WorkflowRun(
            3L, "CI - product-service", "in_progress", null,
            Instant.parse("2024-06-01T11:00:00Z"), null, null);

    // --- fetchLastDeploymentByService ---

    @Test
    void fetchLastDeploymentByService_shouldFormatSuccessfulRun() {
        when(deploymentService.getLastDeployment("order-service")).thenReturn(Optional.of(SUCCESSFUL_RUN));

        String result = deploymentTool.fetchLastDeploymentByService("order-service");

        assertThat(result)
                .contains("CI - order-service")
                .contains("completed")
                .contains("success")
                .contains("2024-06-01T10:00:00Z")
                .contains("https://github.com/runs/1");
    }

    @Test
    void fetchLastDeploymentByService_shouldReturnNotFoundMessage() {
        when(deploymentService.getLastDeployment("unknown")).thenReturn(Optional.empty());

        String result = deploymentTool.fetchLastDeploymentByService("unknown");

        assertThat(result).contains("No recent deployment found for service: unknown");
    }

    @Test
    void fetchLastDeploymentByService_shouldHandleNullConclusion() {
        when(deploymentService.getLastDeployment("product")).thenReturn(Optional.of(IN_PROGRESS_RUN));

        String result = deploymentTool.fetchLastDeploymentByService("product");

        assertThat(result).contains("in-progress");
    }

    @Test
    void fetchLastDeploymentByService_shouldHandleNullHtmlUrl() {
        when(deploymentService.getLastDeployment("product")).thenReturn(Optional.of(IN_PROGRESS_RUN));

        String result = deploymentTool.fetchLastDeploymentByService("product");

        assertThat(result).contains("N/A");
    }

    // --- fetchLastDeploymentOverall ---

    @Test
    void fetchLastDeploymentOverall_shouldReturnMostRecentRunDetails() {
        when(deploymentService.getLastDeploymentOverall()).thenReturn(Optional.of(SUCCESSFUL_RUN));

        String result = deploymentTool.fetchLastDeploymentOverall();

        assertThat(result)
                .contains("CI - order-service")
                .contains("most recent across all services")
                .contains("success");
    }

    @Test
    void fetchLastDeploymentOverall_shouldReturnNotFoundWhenEmpty() {
        when(deploymentService.getLastDeploymentOverall()).thenReturn(Optional.empty());

        String result = deploymentTool.fetchLastDeploymentOverall();

        assertThat(result).contains("No recent deployments found");
    }

    // --- fetchRecentFailedDeployments ---

    @Test
    void fetchRecentFailedDeployments_shouldListFailures() {
        when(deploymentService.getRecentFailedDeployments(5)).thenReturn(List.of(FAILED_RUN));

        String result = deploymentTool.fetchRecentFailedDeployments();

        assertThat(result)
                .contains("FAILED deployments")
                .contains("CI - inventory-service")
                .contains("failure")
                .contains("https://github.com/runs/2");
    }

    @Test
    void fetchRecentFailedDeployments_shouldReturnAllClearWhenNoFailures() {
        when(deploymentService.getRecentFailedDeployments(5)).thenReturn(List.of());

        String result = deploymentTool.fetchRecentFailedDeployments();

        assertThat(result).contains("No failed deployments found");
    }

    @Test
    void fetchRecentFailedDeployments_shouldNumberEachFailure() {
        WorkflowRun anotherFailure = new WorkflowRun(3L, "CI - notification-service", "completed", "failure",
                Instant.parse("2024-06-01T07:00:00Z"), null, null);

        when(deploymentService.getRecentFailedDeployments(5)).thenReturn(List.of(FAILED_RUN, anotherFailure));

        String result = deploymentTool.fetchRecentFailedDeployments();

        assertThat(result).contains("1.").contains("2.");
    }
}
