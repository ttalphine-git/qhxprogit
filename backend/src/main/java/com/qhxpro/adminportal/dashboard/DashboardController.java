package com.qhxpro.adminportal.dashboard;

import com.qhxpro.adminportal.auth.SuperAdminPrincipal;
import java.time.Instant;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @GetMapping
    public DashboardResponse dashboard(@AuthenticationPrincipal SuperAdminPrincipal principal) {
        return new DashboardResponse(
                "Welcome back, " + principal.username(),
                List.of(
                        new Metric("Active users", "1,248", "+12%"),
                        new Metric("Pending approvals", "23", "-4%"),
                        new Metric("System health", "99.98%", "Stable"),
                        new Metric("Revenue", "$84.2K", "+18%")
                ),
                List.of(
                        new Activity("New tenant workspace created", "2 minutes ago"),
                        new Activity("Security policy updated", "18 minutes ago"),
                        new Activity("Billing reconciliation completed", "1 hour ago"),
                        new Activity("Admin access audit exported", "Today")
                ),
                Instant.now()
        );
    }
}
