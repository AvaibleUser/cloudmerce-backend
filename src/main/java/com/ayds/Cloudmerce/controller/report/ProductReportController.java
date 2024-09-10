package com.ayds.Cloudmerce.controller.report;

import com.ayds.Cloudmerce.model.dto.report.ProductReportDto;
import com.ayds.Cloudmerce.model.dto.report.ProductReportPdfDto;
import com.ayds.Cloudmerce.model.dto.report.ProductSalesPdfDto;
import com.ayds.Cloudmerce.model.dto.report.UserSalesReportPdf;
import com.ayds.Cloudmerce.service.CartResponseService;
import com.ayds.Cloudmerce.service.report.DownloadPdfService;
import com.ayds.Cloudmerce.service.report.ProductReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/reports/inventory")
public class ProductReportController {

    @Autowired
    private CartResponseService cartResponseService;

    @Autowired
    private ProductReportService productReportService;

    @Autowired
    private DownloadPdfService downloadPdfService;

    @GetMapping("/more")
    public ResponseEntity<Object> getReportProductMore(@RequestParam(value = "size", defaultValue = "12") int size,
                                                            @RequestParam(value = "startDate", defaultValue = "2000-01-01") String startDate,
                                                            @RequestParam(value = "endDate", defaultValue = "2099-12-31") String endDate,
                                                       @RequestParam(value = "stock", required = false, defaultValue = "false") boolean stock) {
        String order = "desc";
        try {
            ProductReportDto report = this.productReportService.getProductStockReport(size, startDate, endDate, order, stock);
            return this.cartResponseService.responseSuccess(report,"Reporte generado con exito!", HttpStatus.OK);
        }catch (Exception e) {
            return this.cartResponseService.responseError("Error al intentar generar el reporte, comuniquese con soporte", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/less")
    public ResponseEntity<Object> getReportProductLess(@RequestParam(value = "size", defaultValue = "12") int size,
                                                            @RequestParam(value = "startDate", defaultValue = "2000-01-01") String startDate,
                                                            @RequestParam(value = "endDate", defaultValue = "2099-12-31") String endDate,
                                                       @RequestParam(value = "stock", required = false, defaultValue = "false") boolean stock) {
        String order = "asc";
        try {
            ProductReportDto report = this.productReportService.getProductStockReport(size, startDate, endDate, order, stock);
            return this.cartResponseService.responseSuccess(report,"Reporte generado con exito!", HttpStatus.OK);
        }catch (Exception e) {
            return this.cartResponseService.responseError("Error al intentar generar el reporte, comuniquese con soporte", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/downloadPDF")
    public ResponseEntity<Resource> downloadReport(@RequestBody ProductReportPdfDto productReportPdfDto) {
        Map<String, Object> templateVariables = Map.of(
                "products", productReportPdfDto.productReportDto().products(),
                "totalStock", productReportPdfDto.productReportDto().totalStock(),
                "dateReport", productReportPdfDto.productReportDto().dateReport(),
                "typeReport", productReportPdfDto.typeReport(),
                "rangeDate", productReportPdfDto.startDate() + " - " + productReportPdfDto.endDate(),
                "size", productReportPdfDto.size()
        );
        return this.downloadPdfService.downloadPdf("productReport", templateVariables);
    }
}
