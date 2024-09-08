package com.ayds.Cloudmerce.model.dto.report;

public record ProductSalesPdfDto (
        ProductSalesReportDto productSalesReportDto,
        String startDate, String endDate, String paymentMethod,
        String processStatus, Integer size,
        String typeReport
) {


}
