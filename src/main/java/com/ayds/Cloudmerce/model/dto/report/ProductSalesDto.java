package com.ayds.Cloudmerce.model.dto.report;

import java.math.BigDecimal;

public record ProductSalesDto(
        String name, Float price,
        Long totalPurchases,
        BigDecimal totalSpent
) {
}
