package com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto;

import java.util.List;
import java.util.Map;

public record PrometheusResult(Map<String, String> metric,
                               List<Object> value) {
}
