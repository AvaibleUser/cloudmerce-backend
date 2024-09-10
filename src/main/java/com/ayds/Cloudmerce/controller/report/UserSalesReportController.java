package com.ayds.Cloudmerce.controller.report;

import com.ayds.Cloudmerce.model.dto.report.UserSalesReportDto;
import com.ayds.Cloudmerce.model.dto.report.UserSalesReportPdf;
import com.ayds.Cloudmerce.model.entity.CompanyEntity;
import com.ayds.Cloudmerce.repository.CompanyRepository;
import com.ayds.Cloudmerce.service.CartResponseService;
import com.ayds.Cloudmerce.service.report.DownloadPdfService;
import com.ayds.Cloudmerce.service.report.UserSalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports/users")
public class UserSalesReportController {

    @Autowired
    private CartResponseService cartResponseService;

    @Autowired
    private UserSalesReportService userSalesReportService;

    @Autowired
    private DownloadPdfService downloadPdfService;
    @Autowired
    private CompanyRepository companyRepository;

    @GetMapping("/more")
    public ResponseEntity<Object>  getReportUserMoreShopping(@RequestParam(value = "size", defaultValue = "12") int size,
                                                                    @RequestParam(value = "startDate", defaultValue = "2000-01-01") String startDate,
                                                                    @RequestParam(value = "endDate", defaultValue = "2099-12-31") String endDate,
                                                                    @RequestParam(value = "paymentMethod", defaultValue = "0") int paymentMethod, @RequestParam(value = "processStatus", defaultValue = "0") int processStatus) {
        String order = "desc";
        try {
            UserSalesReportDto report = this.userSalesReportService.getTopCustomersByPurchases(size, startDate, endDate, order, processStatus,paymentMethod);
            return this.cartResponseService.responseSuccess(report,"Reporte generado con exito!",HttpStatus.OK);
        }catch (Exception e) {
            return this.cartResponseService.responseError("Error al intentar generar el reporte, comuniquese con soporte", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/less")
    public ResponseEntity<Object> getReportUserLessShopping(@RequestParam(value = "size", defaultValue = "12") int size,
                                                        @RequestParam(value = "startDate", defaultValue = "2000-01-01") String startDate,
                                                        @RequestParam(value = "endDate", defaultValue = "2099-12-31") String endDate,
                                                        @RequestParam(value = "paymentMethod", defaultValue = "0") int paymentMethod, @RequestParam(value = "processStatus", defaultValue = "0") int processStatus) {
        String order = "asc";
        try {
            UserSalesReportDto report = this.userSalesReportService.getTopCustomersByPurchases(size, startDate, endDate, order, processStatus,paymentMethod);
            return this.cartResponseService.responseSuccess(report,"Reporte generado con exito!",HttpStatus.OK);
        }catch (Exception e) {
            return this.cartResponseService.responseError("Error al intentar generar el reporte, comuniquese con soporte", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * apartado para la generacion de pdf y excel
     */

    @PostMapping("/downloadPDF")
    public ResponseEntity<Resource> downloadReport(@RequestBody UserSalesReportPdf userSalesReportPdf) {
        CompanyEntity companyEntity = companyRepository.findTopByOrderByIdAsc();
        String nameCompany = "compañia Z.X.Y";
        String companyLogo = "https://e7.pngegg.com/pngimages/996/491/png-clipart-shopify-e-commerce-logo-web-design-design-web-design-logo.png";
        if (companyEntity != null) {
            nameCompany = companyEntity.getName();
            companyLogo = companyEntity.getLogoPath();
        }

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("users", userSalesReportPdf.userSalesReportDto().users());
        templateVariables.put("totalPurchases", userSalesReportPdf.userSalesReportDto().totalPurchases());
        templateVariables.put("totalSpent", userSalesReportPdf.userSalesReportDto().totalSpent());
        templateVariables.put("dateReport", userSalesReportPdf.userSalesReportDto().dateReport());
        templateVariables.put("typeReport", userSalesReportPdf.typeReport());
        templateVariables.put("rangeDate", userSalesReportPdf.startDate() + " - " + userSalesReportPdf.endDate());
        templateVariables.put("payMethod", userSalesReportPdf.paymentMethod());
        templateVariables.put("status", userSalesReportPdf.processStatus());
        templateVariables.put("size", userSalesReportPdf.size());
        templateVariables.put("nameCompany", nameCompany);
        templateVariables.put("companyLogo", companyLogo);

        return this.downloadPdfService.downloadPdf("userSalesReport", templateVariables);
    }


}
