package com.fithub.fithub_api.dashboard.controller;

import com.fithub.fithub_api.dashboard.dto.DashboardStatsDto;
import com.fithub.fithub_api.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController implements DashboardIController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Apenas Admin pode ver o financeiro/stats
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }
}