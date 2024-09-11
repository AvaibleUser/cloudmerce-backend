package com.ayds.Cloudmerce.controller.report;

import com.ayds.Cloudmerce.model.dto.cart.CartResponseDto;
import com.ayds.Cloudmerce.model.dto.report.*;
import com.ayds.Cloudmerce.model.entity.CompanyEntity;
import com.ayds.Cloudmerce.model.entity.UserEntity;
import com.ayds.Cloudmerce.repository.CompanyRepository;
import com.ayds.Cloudmerce.repository.UserRepository;
import com.ayds.Cloudmerce.service.CartResponseService;
import com.ayds.Cloudmerce.service.CartService;
import com.ayds.Cloudmerce.service.report.DownloadPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports/sales")
public class SalesReportController {
    @Autowired
    private CartService cartService;
    @Autowired
    private CartResponseService cartResponseService;

    @Autowired
    private DownloadPdfService downloadPdfService;

    @Autowired
    private CompanyRepository companyRepository;

    @GetMapping
    public ResponseEntity<Object> getUserCart(@RequestParam(value = "size", defaultValue = "12") int size,
                                              @RequestParam(value = "startDate", defaultValue = "2000-01-01") String startDate, @RequestParam(value = "endDate", defaultValue = "2099-12-31") String endDate,
                                              @RequestParam(value = "order", defaultValue = "asc") String order, @RequestParam(value = "cartIdInit", defaultValue = "0") int cartIdInit,
                                              @RequestParam(value = "paymentMethod", defaultValue = "0") int paymentMethod, @RequestParam(value = "processStatus", defaultValue = "0") int processStatus) {

        try {
            SalesReportDto result=this.cartService.getCartsFilterWithReport(0, size, startDate, endDate, order, cartIdInit, processStatus,paymentMethod);

            return this.cartResponseService.responseSuccess(result,"Reporte generado con exito", HttpStatus.OK);
        }catch (Exception e) {
            System.out.println(e);
            return this.cartResponseService.responseError("A ocurrido un error al procesar el Carrito de Compras, Revisa los datos proporcionados, porfavor intentalo de nuevo",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/downloadPDF")
    public ResponseEntity<Resource> downloadReport(@RequestBody SalesReportDtoPdf salesReportDtoPdf) {
        CompanyEntity companyEntity = companyRepository.findTopByOrderByIdAsc();
        String nameCompany = "compañia Z.X.Y";
        String companyLogo = "https://e7.pngegg.com/pngimages/996/491/png-clipart-shopify-e-commerce-logo-web-design-design-web-design-logo.png";
        if (companyEntity != null) {
            nameCompany = companyEntity.getName();
            companyLogo = companyEntity.getLogoPath();
        }
        Map<String, Object> templateVariables = Map.of(
                "sales", salesReportDtoPdf.salesReportDto().sales(),
                "totalSpent", salesReportDtoPdf.salesReportDto().totalSpent(),
                "dateReport", salesReportDtoPdf.salesReportDto().dateReport(),
                "typeReport", salesReportDtoPdf.typeReport(),
                "rangeDate", salesReportDtoPdf.startDate() + " - " + salesReportDtoPdf.endDate(),
                "payMethod", salesReportDtoPdf.paymentMethod(),
                "status", salesReportDtoPdf.processStatus(),
                "size", salesReportDtoPdf.size(),
                "nameCompany", nameCompany,
                "companyLogo", companyLogo
        );
        return this.downloadPdfService.downloadPdf("SalesReport", templateVariables);
    }



}
