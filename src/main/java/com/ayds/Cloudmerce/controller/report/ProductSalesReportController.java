package com.ayds.Cloudmerce.controller.report;

import com.ayds.Cloudmerce.model.dto.report.*;
import com.ayds.Cloudmerce.model.entity.CompanyEntity;
import com.ayds.Cloudmerce.repository.CompanyRepository;
import com.ayds.Cloudmerce.service.CartResponseService;
import com.ayds.Cloudmerce.service.report.DownloadExcelService;
import com.ayds.Cloudmerce.service.report.DownloadPdfService;
import com.ayds.Cloudmerce.service.report.ProductSalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private DownloadExcelService downloadExcelService;


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

    @PostMapping("/downloadPDF")
    public ResponseEntity<Resource> downloadReport(@RequestBody ProductSalesPdfDto productSalesPdfDto) {
        CompanyEntity companyEntity = companyRepository.findTopByOrderByIdAsc();
        String nameCompany = "compañia Z.X.Y";
        String companyLogo = "https://e7.pngegg.com/pngimages/996/491/png-clipart-shopify-e-commerce-logo-web-design-design-web-design-logo.png";
        if (companyEntity != null) {
            nameCompany = companyEntity.getName();
            companyLogo = companyEntity.getLogoPath();
        }
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("products", productSalesPdfDto.productSalesReportDto().products());
        templateVariables.put("totalPurchases", productSalesPdfDto.productSalesReportDto().totalPurchases());
        templateVariables.put( "totalSpent", productSalesPdfDto.productSalesReportDto().totalSpent());
        templateVariables.put("dateReport", productSalesPdfDto.productSalesReportDto().dateReport());
        templateVariables.put( "typeReport", productSalesPdfDto.typeReport());
        templateVariables.put("rangeDate", productSalesPdfDto.startDate() + " - " + productSalesPdfDto.endDate());
        templateVariables.put("payMethod", productSalesPdfDto.paymentMethod());
        templateVariables.put("status", productSalesPdfDto.processStatus());
        templateVariables.put("size", productSalesPdfDto.size());
        templateVariables.put("nameCompany", nameCompany);
        templateVariables.put("companyLogo", companyLogo);

        return this.downloadPdfService.downloadPdf("productSalesReport", templateVariables);
    }

    @PostMapping("/download-excel")
    ResponseEntity<byte[]>downloadReportExcel(@RequestBody ProductSalesPdfDto productSalesPdfDto)throws IOException {
        List<ProductSalesDto> products = productSalesPdfDto.productSalesReportDto().products();
        List<String> headers = new ArrayList<>();
        headers.add("Producto");
        headers.add("Precio Unitario");
        headers.add("Cantidad Comprada");
        headers.add("Total");
        List<Object> userObjects = new ArrayList<>();
        for (ProductSalesDto product : products) {
            userObjects.add(product.name() == null ? "" : product.name());
            userObjects.add(product.price() == null ? "" : product.price());
            userObjects.add(product.totalPurchases() == null ? "" : product.totalPurchases());
            userObjects.add(product.totalSpent() == null ? "" : product.totalSpent());
        }
        userObjects.add("Total De Compras");
        userObjects.add("");
        userObjects.add(productSalesPdfDto.productSalesReportDto().totalPurchases());
        userObjects.add("");
        userObjects.add("Total General");
        userObjects.add("");
        userObjects.add("");
        userObjects.add(productSalesPdfDto.productSalesReportDto().totalSpent());
        return this.downloadExcelService.generateExcelReport(headers, userObjects, "reporte_productos_venta");
    }

}
