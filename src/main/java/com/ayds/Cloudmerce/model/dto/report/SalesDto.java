package com.ayds.Cloudmerce.model.dto.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SalesDto(
        BigDecimal total,
        BigDecimal tax,
        String createdAt,
        String user,
        String order,
        String paymentMethod,
        String status
) {
}
