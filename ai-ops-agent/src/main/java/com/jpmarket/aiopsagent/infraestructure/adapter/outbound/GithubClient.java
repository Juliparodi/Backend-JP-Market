package com.jpmarket.aiopsagent.infraestructure.adapter.outbound;

import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto.GithubWorkflowRunResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Log4j2
@RequiredArgsConstructor
public class GithubClient {

    private final RestClient restClient;

    @Value("${jpmarket.github.api.owner}")
    private String owner;

    @Value("${jpmarket.github.api.repo}")
    private String repo;


    public GithubWorkflowRunResponse getRecentDeployments() {
        String uri = String.format("/repos/%s/%s/actions/runs", owner, repo);
        
        log.info("Fetching recent GitHub Actions runs from: {}", uri);
        
        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(GithubWorkflowRunResponse.class);
    }
}
