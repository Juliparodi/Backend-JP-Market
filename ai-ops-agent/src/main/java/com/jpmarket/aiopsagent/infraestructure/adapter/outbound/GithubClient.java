package com.jpmarket.aiopsagent.infraestructure.adapter.outbound;

import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto.GithubWorkflowRunResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Log4j2
public class GithubClient {

    private final RestClient restClient;
    private final String owner;
    private final String repo;

    public GithubClient(
            RestClient.Builder restClientBuilder,
            @Value("${jpmarket.github.api.url}") String baseUrl,
            @Value("${jpmarket.github.api.token}") String token,
            @Value("${jpmarket.github.api.owner}") String owner,
            @Value("${jpmarket.github.api.repo}") String repo) {

        this.owner = owner;
        this.repo = repo;

        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github.v3+json")
                .build();
    }

    public GithubWorkflowRunResponse getRecentDeployments() {
        String uri = String.format("/repos/%s/%s/actions/runs", owner, repo);
        
        log.info("Fetching recent GitHub Actions runs from: {}", uri);
        
        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(GithubWorkflowRunResponse.class);
    }
}
