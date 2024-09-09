package com.ayds.Cloudmerce.model.dto.report;

import java.math.BigDecimal;
import java.util.List;

public record UserSalesReportDto (
        List<UserSalesDto> users,
        BigDecimal totalSpent,
        Long totalPurchases,
        String dateReport
) {
}
