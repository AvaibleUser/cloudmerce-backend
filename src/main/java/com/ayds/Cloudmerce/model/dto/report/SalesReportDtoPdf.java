package com.ayds.Cloudmerce.model.dto.report;

public record SalesReportDtoPdf (
        SalesReportDto salesReportDto,
        String startDate, String endDate, String paymentMethod,
        String processStatus, Integer size,
        String typeReport
){
}
