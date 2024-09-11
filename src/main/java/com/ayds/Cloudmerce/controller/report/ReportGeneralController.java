package com.ayds.Cloudmerce.controller.report;

import com.ayds.Cloudmerce.model.dto.report.*;
import com.ayds.Cloudmerce.model.entity.CompanyEntity;
import com.ayds.Cloudmerce.repository.CompanyRepository;
import com.ayds.Cloudmerce.service.CartResponseService;
import com.ayds.Cloudmerce.service.report.DownloadExcelService;
import com.ayds.Cloudmerce.service.report.DownloadPdfService;
import com.ayds.Cloudmerce.service.report.SalesReportService;
import com.ayds.Cloudmerce.service.report.UserSalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DownloadExcelService downloadExcelService;

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
        CompanyEntity companyEntity = companyRepository.findTopByOrderByIdAsc();
        String nameCompany = "compañia Z.X.Y";
        String companyLogo = "https://e7.pngegg.com/pngimages/996/491/png-clipart-shopify-e-commerce-logo-web-design-design-web-design-logo.png";
        if (companyEntity != null) {
            nameCompany = companyEntity.getName();
            companyLogo = companyEntity.getLogoPath();
        }

        Map<String, Object> templateVariables = Map.of(
                "report", reportGeneralPdfDto,
                "nameCompany", nameCompany,
                "companyLogo", companyLogo
        );
        return this.downloadPdfService.downloadPdf("reportGeneral", templateVariables);
    }

    @PostMapping("/download-excel")
    ResponseEntity<byte[]>downloadReportExcel(@RequestBody ReportGeneralPdfDto reportGeneralPdfDto)throws IOException {
        List<String> headers = new ArrayList<>();
        headers.add("Grupo");
        headers.add("Cantidad");
        headers.add("Total (Q)");
        List<Object> userObjects = new ArrayList<>();
        userObjects.add("Productos Vendidos (no repetidos)");
        userObjects.add(reportGeneralPdfDto.totalUniqueProducts() == null ? 0 : reportGeneralPdfDto.totalUniqueProducts());
        userObjects.add(reportGeneralPdfDto.totalAmountSold() == null ? 0 : reportGeneralPdfDto.totalAmountSold());
        //
        userObjects.add("Usuarios Compras (no repetidos)");
        userObjects.add(reportGeneralPdfDto.totalUniqueUsers() == null ? 0 : reportGeneralPdfDto.totalUniqueUsers());
        userObjects.add(reportGeneralPdfDto.totalAmountSold() == null ? 0 : reportGeneralPdfDto.totalAmountSold());
        //
        userObjects.add("Ordenes");
        userObjects.add(reportGeneralPdfDto.totalOrders() == null ? 0 : reportGeneralPdfDto.totalOrders());
        userObjects.add(reportGeneralPdfDto.totalAmountOrd() == null ? 0 : reportGeneralPdfDto.totalAmountOrd());
        //
        userObjects.add("Ingreso por Envio de Odenes");
        userObjects.add(reportGeneralPdfDto.totalOrders() == null ? 0 : reportGeneralPdfDto.totalOrders());
        userObjects.add(reportGeneralPdfDto.totalShippingCost() == null ? 0 : reportGeneralPdfDto.totalShippingCost());
        //
        userObjects.add("Ventas sin Orden");
        userObjects.add(reportGeneralPdfDto.totalSalesNoOrder() == null ? 0 : reportGeneralPdfDto.totalSalesNoOrder());
        userObjects.add(reportGeneralPdfDto.totalAmountSoldNoOrder() == null ? 0 : reportGeneralPdfDto.totalAmountSoldNoOrder());
        //
        userObjects.add("Ventas");
        userObjects.add(reportGeneralPdfDto.totalSales() == null ? 0 : reportGeneralPdfDto.totalSales());
        userObjects.add(reportGeneralPdfDto.totalAmountSold() == null ? 0 : reportGeneralPdfDto.totalAmountSold());
        //
        userObjects.add("Total Ingreso (ventas (odenes) + costo envio)");
        userObjects.add("");
        userObjects.add(reportGeneralPdfDto.total() == null ? 0 : reportGeneralPdfDto.total());
        return this.downloadExcelService.generateExcelReport(headers, userObjects, "reporte_ordenes");
    }

}
