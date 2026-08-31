package com.jpmarket.aiopsagent.infraestructure.adapter.outbound;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto.GithubWorkflowRunResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

class GithubClientIntegrationTest {

    private WireMockServer wireMockServer;
    private GithubClient githubClient;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        githubClient = new GithubClient(
                RestClient.builder(),
                "http://localhost:" + wireMockServer.port(),
                "fake-token",
                "juliparodi",
                "Backend-JP-Market"
        );
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void getRecentDeployments_shouldReturnWorkflowRuns() {
        wireMockServer.stubFor(get(urlPathEqualTo("/repos/juliparodi/Backend-JP-Market/actions/runs"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                              "total_count": 2,
                              "workflow_runs": [
                                {
                                  "id": 1,
                                  "name": "CI - order-service",
                                  "status": "completed",
                                  "conclusion": "success",
                                  "created_at": "2024-06-01T10:00:00Z",
                                  "updated_at": "2024-06-01T10:10:00Z",
                                  "html_url": "https://github.com/runs/1"
                                },
                                {
                                  "id": 2,
                                  "name": "CI - inventory-service",
                                  "status": "completed",
                                  "conclusion": "failure",
                                  "created_at": "2024-06-01T09:00:00Z",
                                  "updated_at": "2024-06-01T09:10:00Z",
                                  "html_url": "https://github.com/runs/2"
                                }
                              ]
                            }
                        """)));

        GithubWorkflowRunResponse response = githubClient.getRecentDeployments();

        assertThat(response).isNotNull();
        assertThat(response.totalCount()).isEqualTo(2);
        assertThat(response.workflowRuns()).hasSize(2);
        assertThat(response.workflowRuns().get(0).name()).isEqualTo("CI - order-service");
        assertThat(response.workflowRuns().get(0).conclusion()).isEqualTo("success");
        assertThat(response.workflowRuns().get(1).name()).isEqualTo("CI - inventory-service");
        assertThat(response.workflowRuns().get(1).conclusion()).isEqualTo("failure");
    }

    @Test
    void getRecentDeployments_shouldSendAuthorizationHeader() {
        wireMockServer.stubFor(get(urlPathEqualTo("/repos/juliparodi/Backend-JP-Market/actions/runs"))
                .withHeader("Authorization", equalTo("Bearer fake-token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"total_count\": 0, \"workflow_runs\": []}")));

        GithubWorkflowRunResponse response = githubClient.getRecentDeployments();

        assertThat(response.totalCount()).isEqualTo(0);
        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/repos/juliparodi/Backend-JP-Market/actions/runs"))
                .withHeader("Authorization", equalTo("Bearer fake-token")));
    }

    @Test
    void getRecentDeployments_shouldReturnEmptyListWhenNoRuns() {
        wireMockServer.stubFor(get(urlPathEqualTo("/repos/juliparodi/Backend-JP-Market/actions/runs"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"total_count\": 0, \"workflow_runs\": []}")));

        GithubWorkflowRunResponse response = githubClient.getRecentDeployments();

        assertThat(response.workflowRuns()).isEmpty();
    }
}
