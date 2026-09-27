package com.qhxpro.adminportal.dashboard;

public record Metric(
        String label,
        String value,
        String delta
) {
}
