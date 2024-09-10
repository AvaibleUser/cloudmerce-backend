package com.ayds.Cloudmerce.controller.report;

import com.ayds.Cloudmerce.model.dto.report.ReportGeneralDto;
import com.ayds.Cloudmerce.model.dto.report.ReportGeneralPdfDto;
import com.ayds.Cloudmerce.model.dto.report.UserSalesReportDto;
import com.ayds.Cloudmerce.model.dto.report.UserSalesReportPdf;
import com.ayds.Cloudmerce.service.CartResponseService;
import com.ayds.Cloudmerce.service.report.DownloadPdfService;
import com.ayds.Cloudmerce.service.report.SalesReportService;
import com.ayds.Cloudmerce.service.report.UserSalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportGeneralController {

    @Autowired
    private CartResponseService cartResponseService;

    @Autowired
    private DownloadPdfService downloadPdfService;

    @Autowired
    private SalesReportService salesReportService;

    @GetMapping
    public ResponseEntity<Object> getReportGeneral(@RequestParam(value = "startDate", defaultValue = "2000-01-01") String startDate,
                                                   @RequestParam(value = "endDate", defaultValue = "2099-12-31") String endDate) {
         {
            ReportGeneralDto report = new ReportGeneralDto();
            report.setRangeDate(startDate + " - " + endDate);
            this.salesReportService.getTotalSales(startDate, endDate, report);
            this.salesReportService.getTotalUniqueProductsSold(startDate, endDate, report);
            this.salesReportService.getOrderReport(startDate, endDate, report);
            report.setTotalSalesNoOrder(report.getTotalSales() - report.getTotalOrders());
            report.setTotalAmountSoldNoOrder(report.getTotalAmountSold().subtract(report.getTotalAmountOrd()));
            report.setTotal(report.getTotalAmountSold().add(report.getTotalShippingCost()));
            report.setDateReport(LocalDate.now().toString());
            return this.cartResponseService.responseSuccess(report,"Reporte generado con exito!", HttpStatus.OK);
        }
    }

    @PostMapping("/downloadPDF")
    public ResponseEntity<Resource> downloadReport(@RequestBody ReportGeneralPdfDto reportGeneralPdfDto) {
        Map<String, Object> templateVariables = Map.of(
                "report", reportGeneralPdfDto
        );
        return this.downloadPdfService.downloadPdf("reportGeneral", templateVariables);
    }
}
