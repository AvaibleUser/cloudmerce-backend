package com.ayds.Cloudmerce.model.dto.report;

public record OrdersReportPdfDto (
        OrderReportDto orderReportDto,
        String startDate, String endDate, String paymentMethod,
        String processStatus, Integer size,
        String typeReport
){
}
