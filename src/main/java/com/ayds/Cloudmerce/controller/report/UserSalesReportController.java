package com.ayds.Cloudmerce.controller.report;

import com.ayds.Cloudmerce.model.dto.report.UserSalesReportDto;
import com.ayds.Cloudmerce.model.dto.report.UserSalesReportPdf;
import com.ayds.Cloudmerce.service.CartResponseService;
import com.ayds.Cloudmerce.service.CartService;
import com.ayds.Cloudmerce.service.PdfGeneratorService;
import com.ayds.Cloudmerce.service.TemplateRendererService;
import com.ayds.Cloudmerce.service.report.UserSalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports/users")
public class UserSalesReportController {

    @Autowired
    private CartResponseService cartResponseService;

    @Autowired
    private UserSalesReportService userSalesReportService;

    @Autowired
    private TemplateRendererService templateRendererService;

    @Autowired
    private PdfGeneratorService pdfService;

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

    @GetMapping("/downloadPDF")
    public ResponseEntity<Resource> downloadReport(@RequestBody UserSalesReportPdf userSalesReportPdf) {
        System.out.println(userSalesReportPdf);
        Map<String, Object> templateVariables = Map.of(
                "users", userSalesReportPdf.userSalesReportDto().users(),
                "totalPurchases", userSalesReportPdf.userSalesReportDto().totalPurchases(),
                "totalSpent", userSalesReportPdf.userSalesReportDto().totalSpent(),
                "dateReport", userSalesReportPdf.userSalesReportDto().dateReport(),
                "typeReport", userSalesReportPdf.typeReport(),
                "rangeDate", userSalesReportPdf.startDate() + " - " + userSalesReportPdf.endDate(),
                "payMethod", userSalesReportPdf.paymentMethod(),
                "status", userSalesReportPdf.processStatus(),
                "size", userSalesReportPdf.size()
        );
        String billHtml = templateRendererService.renderTemplate("userSalesReport", templateVariables);
        try {
            byte[] pdfBytes = pdfService.generatePdfFromHtmlString(billHtml);

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(resource.contentLength())
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment()
                                    .filename("pdf-test.pdf")
                                    .build()
                                    .toString())
                    .body(resource);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return ResponseEntity.badRequest().build();
        }
    }


}
