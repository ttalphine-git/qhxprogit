package com.qhxpro.adminportal.dashboard;

import java.time.Instant;
import java.util.List;

public record DashboardResponse(
        String greeting,
        List<Metric> metrics,
        List<Activity> activities,
        Instant generatedAt
) {
}
