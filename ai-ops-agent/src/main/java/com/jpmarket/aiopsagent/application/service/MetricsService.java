package com.jpmarket.aiopsagent.application.service;


import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.PrometheusClient;
import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto.PrometheusQueryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class MetricsService {

    private final PrometheusClient prometheusClient;

    public double getErrorRate(String serviceName) {

        double errorRequests = executeScalar("""
                sum(rate(http_server_requests_milliseconds_count{
                    service_name="%s",
                    outcome="SERVER_ERROR"
                }[5m]))
                """.formatted(serviceName));

        double totalRequests = executeScalar("""
                sum(rate(http_server_requests_milliseconds_count{
                    service_name="%s"
                }[5m]))
                """.formatted(serviceName));

        log.info("Error requests: {}", errorRequests);
        log.info("Total requests: {}", totalRequests);

        if (totalRequests == 0) {
            return 0;
        }

        return (errorRequests / totalRequests) * 100;
    }

    private double executeScalar(String promQl) {
        return extractDouble(prometheusClient.query(promQl));
    }

    private double extractDouble(PrometheusQueryResponse response) {

        if (response == null
                || response.data() == null
                || response.data().result().isEmpty()) {
            return 0;
        }

        Object value = response.data()
                .result()
                .getFirst()
                .value()
                .get(1);

        return Double.parseDouble(value.toString());
    }

    public double getAverageLatency(String serviceName) {

        String promQl = """
        (
            sum(rate(http_server_requests_milliseconds_sum{
                service_name="%s"
            }[5m]))
        /
            sum(rate(http_server_requests_milliseconds_count{
                service_name="%s"
            }[5m]))
        )
        """.formatted(serviceName, serviceName);

        return executeScalar(promQl);
    }
}
