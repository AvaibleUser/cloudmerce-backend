package com.ayds.Cloudmerce.controller.report;

import com.ayds.Cloudmerce.model.dto.report.ProductSalesPdfDto;
import com.ayds.Cloudmerce.model.dto.report.ProductSalesReportDto;
import com.ayds.Cloudmerce.model.dto.report.UserSalesReportDto;
import com.ayds.Cloudmerce.model.dto.report.UserSalesReportPdf;
import com.ayds.Cloudmerce.service.CartResponseService;
import com.ayds.Cloudmerce.service.report.DownloadPdfService;
import com.ayds.Cloudmerce.service.report.ProductSalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports/products")
public class ProductSalesReportController {

    @Autowired
    private CartResponseService cartResponseService;
    @Autowired
    private ProductSalesReportService productSalesReportService;
    @Autowired
    private DownloadPdfService downloadPdfService;


    @GetMapping("/more")
    public ResponseEntity<Object> getReportProductMoreSales(@RequestParam(value = "size", defaultValue = "12") int size,
                                                            @RequestParam(value = "startDate", defaultValue = "2000-01-01") String startDate,
                                                            @RequestParam(value = "endDate", defaultValue = "2099-12-31") String endDate,
                                                            @RequestParam(value = "paymentMethod", defaultValue = "0") int paymentMethod, @RequestParam(value = "processStatus", defaultValue = "0") int processStatus) {
        String order = "desc";
        try {
            ProductSalesReportDto report = this.productSalesReportService.getProductSalesReport(size, startDate, endDate, order, processStatus,paymentMethod);
            return this.cartResponseService.responseSuccess(report,"Reporte generado con exito!", HttpStatus.OK);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return this.cartResponseService.responseError("Error al intentar generar el reporte, comuniquese con soporte", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/less")
    public ResponseEntity<Object> getReportProductLessSales(@RequestParam(value = "size", defaultValue = "12") int size,
                                                            @RequestParam(value = "startDate", defaultValue = "2000-01-01") String startDate,
                                                            @RequestParam(value = "endDate", defaultValue = "2099-12-31") String endDate,
                                                            @RequestParam(value = "paymentMethod", defaultValue = "0") int paymentMethod, @RequestParam(value = "processStatus", defaultValue = "0") int processStatus) {
        String order = "asc";
        try {
            ProductSalesReportDto report = this.productSalesReportService.getProductSalesReport(size, startDate, endDate, order, processStatus,paymentMethod);
            return this.cartResponseService.responseSuccess(report,"Reporte generado con exito!", HttpStatus.OK);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return this.cartResponseService.responseError("Error al intentar generar el reporte, comuniquese con soporte", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * apartado para la generacion de pdf y excel
     */

    @GetMapping("/downloadPDF")
    public ResponseEntity<Resource> downloadReport(@RequestBody ProductSalesPdfDto productSalesPdfDto) {
        Map<String, Object> templateVariables = Map.of(
                "products", productSalesPdfDto.productSalesReportDto().products(),
                "totalPurchases", productSalesPdfDto.productSalesReportDto().totalPurchases(),
                "totalSpent", productSalesPdfDto.productSalesReportDto().totalSpent(),
                "dateReport", productSalesPdfDto.productSalesReportDto().dateReport(),
                "typeReport", productSalesPdfDto.typeReport(),
                "rangeDate", productSalesPdfDto.startDate() + " - " + productSalesPdfDto.endDate(),
                "payMethod", productSalesPdfDto.paymentMethod(),
                "status", productSalesPdfDto.processStatus(),
                "size", productSalesPdfDto.size()
        );
        return this.downloadPdfService.downloadPdf("productSalesReport", templateVariables);
    }
}
