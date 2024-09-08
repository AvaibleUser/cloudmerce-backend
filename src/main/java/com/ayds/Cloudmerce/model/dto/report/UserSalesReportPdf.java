package com.ayds.Cloudmerce.model.dto.report;

public record UserSalesReportPdf (
        UserSalesReportDto userSalesReportDto,
        String startDate, String endDate, String paymentMethod,
        String processStatus, Integer size,
        String typeReport
) {

}
