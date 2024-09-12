package com.ayds.Cloudmerce.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.Cloudmerce.service.MetricService;

@RestController
@RequestMapping("/api/dashboard/metrics")
public class MetricController {

    @Autowired
    private MetricService metricService;

    @GetMapping("/sales")
    public ResponseEntity<Map<String, Number>> getMonthSales() {
        double sales = metricService.findMonthSales();

        return ResponseEntity.ok(Map.of("value", sales, "change", 0));
    }

    @GetMapping("/orders")
    public ResponseEntity<Map<String, Number>> getMonthOrders() {
        long orders = metricService.findMonthOrders();

        return ResponseEntity.ok(Map.of("value", orders, "change", 0));
    }

    @GetMapping("/products")
    public ResponseEntity<Map<String, Number>> getTotalVisibleProducts() {
        long products = metricService.findTotalVisibleProducts();

        return ResponseEntity.ok(Map.of("value", products, "change", 0));
    }

    @GetMapping("/customers")
    public ResponseEntity<Map<String, Number>> getTotalCustomers() {
        long customers = metricService.findTotalCustomers();

        return ResponseEntity.ok(Map.of("value", customers, "change", 0));
    }
}
