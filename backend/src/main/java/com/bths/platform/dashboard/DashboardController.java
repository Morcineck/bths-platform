package com.bths.platform.dashboard;

import com.bths.platform.dashboard.dto.DashboardResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Dashboard", description = "Visão operacional consolidada das viagens e principais indicadores.")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/viagens/{viagemId}")
    public ResponseEntity<DashboardResponse> buscarDashboard(
            @PathVariable Long viagemId
    ) {

        DashboardResponse response =
                dashboardService.buscarDashboard(viagemId);

        return ResponseEntity.ok(response);
    }
}
