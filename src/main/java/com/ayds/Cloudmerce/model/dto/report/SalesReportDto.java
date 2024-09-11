package com.ayds.Cloudmerce.model.dto.report;

import java.math.BigDecimal;
import java.util.List;

public record SalesReportDto(
        List<SalesDto> sales,
        BigDecimal totalSpent,
        String dateReport
) {
}
