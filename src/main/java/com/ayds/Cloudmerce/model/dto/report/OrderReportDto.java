package com.ayds.Cloudmerce.model.dto.report;

import java.math.BigDecimal;
import java.util.List;

public record OrderReportDto (
        List<OrderDto> orders,
        BigDecimal totalSpent,
        String dateReport
){
}
