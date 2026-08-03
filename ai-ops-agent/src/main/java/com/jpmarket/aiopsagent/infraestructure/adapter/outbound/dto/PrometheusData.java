package com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto;

import java.util.List;

public record PrometheusData(String resultType,
                             List<PrometheusResult> result) {
}
