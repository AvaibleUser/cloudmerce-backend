package com.ayds.Cloudmerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.Cloudmerce.model.dto.ChartDataDto;
import com.ayds.Cloudmerce.service.ChartService;

@RestController
@RequestMapping("/api/dashboard/charts")
public class ChartController {

    @Autowired
    private ChartService chartService;

    @GetMapping("/sales")
    public List<ChartDataDto> getSalesOverview() {
        return chartService.getLastSixMonthsSalesOverviewChartData();
    }
}
