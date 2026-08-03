package com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto;

public record PrometheusQueryResponse(String status,
                                      PrometheusData data) {
}
