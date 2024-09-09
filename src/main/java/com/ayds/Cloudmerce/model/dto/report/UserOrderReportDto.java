package com.ayds.Cloudmerce.model.dto.report;

import java.math.BigDecimal;
import java.util.List;

public record UserOrderReportDto (
        List<UserOrderDto> users,
        BigDecimal totalSpent,
        BigDecimal totalCostDelivery,
        Long totalPurchases,
        String dateReport
) {
}
