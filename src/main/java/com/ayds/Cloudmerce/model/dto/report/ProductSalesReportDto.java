package com.ayds.Cloudmerce.model.dto.report;

import java.math.BigDecimal;
import java.util.List;

public record ProductSalesReportDto(
        List<ProductSalesDto> products,
        BigDecimal totalSpent,
        Long totalPurchases,
        String dateReport
) {
}
