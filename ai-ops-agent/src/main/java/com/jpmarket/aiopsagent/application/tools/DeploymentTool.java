package com.jpmarket.aiopsagent.application.tools;

import com.jpmarket.aiopsagent.application.service.DeploymentService;
import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto.WorkflowRun;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeploymentTool {

    private final DeploymentService deploymentService;

    @Tool(description = """
            Fetches the most recent GitHub Actions deployment for a specific microservice.
            Call this when the user explicitly mentions a service name and asks about its last deployment.
            Returns the workflow name, status, conclusion (success/failure), time it ran, and a GitHub Actions link.
            """)
    public String fetchLastDeploymentByService(String serviceName) {
        Optional<WorkflowRun> maybeRun = deploymentService.getLastDeployment(serviceName);

        return maybeRun
                .map(run -> formatRun(serviceName, run))
                .orElse("No recent deployment found for service: " + serviceName);
    }

    @Tool(description = """
            Fetches the single most recent GitHub Actions deployment across ALL microservices, regardless of which service it belongs to.
            Call this when the user asks generic questions like:
            - "How was the last deployment?"
            - "Was the last deployment successful or failed?"
            - "What was deployed last?"
            Returns the workflow name, which service it belongs to, status, conclusion (success/failure), timestamp, and a GitHub Actions link.
            """)
    public String fetchLastDeploymentOverall() {
        Optional<WorkflowRun> maybeRun = deploymentService.getLastDeploymentOverall();

        return maybeRun
                .map(run -> formatRun("(most recent across all services)", run))
                .orElse("No recent deployments found in the repository.");
    }

    @Tool(description = """
            Fetches the most recent FAILED GitHub Actions deployments across ALL microservices.
            Call this when the user asks questions like:
            - "Which service failed recently?"
            - "Which deployments broke something?"
            - "Show me recent failures"
            Returns a list of failed workflows including which service, when it failed, and a link to the run for further investigation.
            """)
    public String fetchRecentFailedDeployments() {
        List<WorkflowRun> failures = deploymentService.getRecentFailedDeployments(5);

        if (failures.isEmpty()) {
            return "No failed deployments found in the recent GitHub Actions history. All looks good!";
        }

        StringBuilder sb = new StringBuilder("Recent FAILED deployments:\n");
        for (int i = 0; i < failures.size(); i++) {
            WorkflowRun run = failures.get(i);
            sb.append(String.format(
                    "%d. Workflow='%s', Conclusion='%s', CreatedAt='%s', URL='%s'%n",
                    i + 1,
                    run.name(),
                    run.conclusion(),
                    run.createdAt() != null ? run.createdAt().toString() : "unknown",
                    run.htmlUrl() != null ? run.htmlUrl() : "N/A"
            ));
        }
        return sb.toString();
    }

    private String formatRun(String context, WorkflowRun run) {
        return String.format(
                "Deployment [%s]: Workflow='%s', Status='%s', Conclusion='%s', CreatedAt='%s', URL='%s'",
                context,
                run.name(),
                run.status(),
                run.conclusion() != null ? run.conclusion() : "in-progress",
                run.createdAt() != null ? run.createdAt().toString() : "unknown",
                run.htmlUrl() != null ? run.htmlUrl() : "N/A"
        );
    }
}
