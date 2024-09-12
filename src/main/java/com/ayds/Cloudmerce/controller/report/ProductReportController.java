package com.ayds.Cloudmerce.controller.report;

import com.ayds.Cloudmerce.model.dto.report.*;
import com.ayds.Cloudmerce.model.entity.CompanyEntity;
import com.ayds.Cloudmerce.repository.CompanyRepository;
import com.ayds.Cloudmerce.service.CartResponseService;
import com.ayds.Cloudmerce.service.report.DownloadExcelService;
import com.ayds.Cloudmerce.service.report.DownloadPdfService;
import com.ayds.Cloudmerce.service.report.ProductReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DownloadExcelService downloadExcelService;

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
        CompanyEntity companyEntity = companyRepository.findTopByOrderByIdAsc();
        String nameCompany = "compañia Z.X.Y";
        String companyLogo = "https://e7.pngegg.com/pngimages/996/491/png-clipart-shopify-e-commerce-logo-web-design-design-web-design-logo.png";
        if (companyEntity != null) {
            nameCompany = companyEntity.getName();
            companyLogo = companyEntity.getLogoPath();
        }
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("products", productReportPdfDto.productReportDto().products());
        templateVariables.put("totalStock", productReportPdfDto.productReportDto().totalStock());
        templateVariables.put("dateReport", productReportPdfDto.productReportDto().dateReport());
        templateVariables.put("typeReport", productReportPdfDto.typeReport());
        templateVariables.put("rangeDate", productReportPdfDto.startDate() + " - " + productReportPdfDto.endDate());
        templateVariables.put("size", productReportPdfDto.size());
        templateVariables.put("nameCompany", nameCompany);
        templateVariables.put("companyLogo", companyLogo);
        return this.downloadPdfService.downloadPdf("productReport", templateVariables);
    }

    @PostMapping("/download-excel")
    ResponseEntity<byte[]>downloadReportExcel(@RequestBody ProductReportPdfDto productReportPdfDto)throws IOException {
        List<ProductDTO> products = productReportPdfDto.productReportDto().products();
        List<String> headers = new ArrayList<>();
        headers.add("Producto");
        headers.add("Precio");
        headers.add("Fecha Registro");
        headers.add("Stock");
        List<Object> userObjects = new ArrayList<>();
        for (ProductDTO product : products) {
            userObjects.add(product.name() == null ? "" : product.name());
            userObjects.add(product.price() == null ? "" : product.price());
            userObjects.add(product.creationAt() == null ? "" : product.creationAt() );
            userObjects.add(product.stock() == null ? "" : product.stock());
        }
        userObjects.add("Total General");
        userObjects.add("");
        userObjects.add("");
        userObjects.add(productReportPdfDto.productReportDto().totalStock());
        return this.downloadExcelService.generateExcelReport(headers, userObjects, "reporte_ordenes");
    }

}
