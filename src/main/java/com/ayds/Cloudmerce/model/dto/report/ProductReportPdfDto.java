package com.ayds.Cloudmerce.model.dto.report;

public record ProductReportPdfDto(
        ProductReportDto productReportDto,
        String startDate, String endDate,
        Integer size,
        String typeReport
) {
}
