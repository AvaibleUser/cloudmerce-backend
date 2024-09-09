package com.ayds.Cloudmerce.model.dto.report;

import java.math.BigDecimal;

public record UserOrderDto (
        String name,
        String nit,
        Long totalPurchases,
        BigDecimal totalSpent,
        BigDecimal totalCostDelivery
) {
}
