package com.ayds.Cloudmerce.model.dto.report;

public record UserOrderReportDtoPdf(
        UserOrderReportDto userOrderReportDto,
        String startDate, String endDate, String paymentMethod,
        String processStatus, Integer size,
        String typeReport
) {
}
