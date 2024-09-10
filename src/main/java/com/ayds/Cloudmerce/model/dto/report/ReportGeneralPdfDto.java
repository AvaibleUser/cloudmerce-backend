package com.ayds.Cloudmerce.model.dto.report;

import java.math.BigDecimal;

public record ReportGeneralPdfDto(
        //ventas general
         Long totalSales,
        BigDecimal totalAmountSold,
        Long totalUniqueUsers,
        Long totalUniqueProducts,
                //apartado de orden
        Long totalOrders,
        BigDecimal totalAmountOrd,
        BigDecimal totalShippingCost,
        Long totalUniqueUsersOrders,
                //ventas sin orden
        Long totalSalesNoOrder,
        BigDecimal totalAmountSoldNoOrder,
                //Total
        BigDecimal total,
                //otros
        String dateReport,
        String rangeDate
) {
}
