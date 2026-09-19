package com.bths.platform.dashboard;

import com.bths.platform.dashboard.dto.DashboardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
