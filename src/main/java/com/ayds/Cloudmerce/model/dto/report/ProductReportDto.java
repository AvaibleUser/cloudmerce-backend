package com.ayds.Cloudmerce.model.dto.report;

import java.math.BigDecimal;
import java.util.List;

public record ProductReportDto (
        List<ProductDTO> products,
        BigDecimal totalStock,
        String dateReport
) {
}
