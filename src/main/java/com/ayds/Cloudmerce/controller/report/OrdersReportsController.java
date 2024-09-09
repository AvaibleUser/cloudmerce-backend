package com.ayds.Cloudmerce.controller.report;

import com.ayds.Cloudmerce.model.dto.cart.OrderResponseDto;
import com.ayds.Cloudmerce.model.dto.report.*;
import com.ayds.Cloudmerce.service.*;
import com.ayds.Cloudmerce.service.report.DownloadPdfService;
import com.ayds.Cloudmerce.service.report.OrderReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports/orders")
public class OrdersReportsController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartResponseService cartResponseService;

    @Autowired
    private OrderReportService orderReportService;

    @Autowired
    private DownloadPdfService downloadPdfService;

    @GetMapping("/users/more")
    public ResponseEntity<Object>  getReportUserMoreShopping(@RequestParam(value = "size", defaultValue = "12") int size,
                                                             @RequestParam(value = "startDate", defaultValue = "2000-01-01") String startDate,
                                                             @RequestParam(value = "endDate", defaultValue = "2099-12-31") String endDate,
                                                             @RequestParam(value = "paymentMethod", defaultValue = "0") int paymentMethod, @RequestParam(value = "processStatus", defaultValue = "0") int processStatus) {
        String order = "desc";
        try {
            UserOrderReportDto report = this.orderReportService.getUserOrdersReport(size, startDate, endDate, order, processStatus,paymentMethod);
            return this.cartResponseService.responseSuccess(report,"Reporte generado con exito!",HttpStatus.OK);
        }catch (Exception e) {
            return this.cartResponseService.responseError("Error al intentar generar el reporte, comuniquese con soporte", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/less")
    public ResponseEntity<Object> getReportUserLessShopping(@RequestParam(value = "size", defaultValue = "12") int size,
                                                            @RequestParam(value = "startDate", defaultValue = "2000-01-01") String startDate,
                                                            @RequestParam(value = "endDate", defaultValue = "2099-12-31") String endDate,
                                                            @RequestParam(value = "paymentMethod", defaultValue = "0") int paymentMethod, @RequestParam(value = "processStatus", defaultValue = "0") int processStatus) {
        String order = "asc";
        try {
            UserOrderReportDto report = this.orderReportService.getUserOrdersReport(size, startDate, endDate, order, processStatus,paymentMethod);
            return this.cartResponseService.responseSuccess(report,"Reporte generado con exito!",HttpStatus.OK);
        }catch (Exception e) {
            return this.cartResponseService.responseError("Error al intentar generar el reporte, comuniquese con soporte", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }



    @GetMapping("/processStatus/{id}")
    public ResponseEntity<Object> getOrdersProcessStatus(@PathVariable("id") Integer id, @RequestParam(value = "size", defaultValue = "12") int size,
                                                         @RequestParam(value = "startDate", defaultValue = "2000-01-01") String startDate, @RequestParam(value = "endDate", defaultValue = "2099-12-31") String endDate,
                                                         @RequestParam(value = "order", defaultValue = "asc") String order, @RequestParam(value = "orderIdInit", defaultValue = "0") int orderIdInit, @RequestParam(value = "paymentMethod", defaultValue = "0") int paymentMethod,
                                                         @RequestParam(value = "dateFilter", defaultValue = "orderDate") String dateFilter) {
        try {
            List<OrderResponseDto> list = this.orderService.getOrdersFilterWithParams(0, size, startDate, endDate, order, orderIdInit, id,dateFilter,paymentMethod);
            OrderReportDto result = this.orderReportService.getOrderReport(list);
            return cartResponseService.responseSuccess(result,"Reporte generado con exito" , HttpStatus.OK);
        }catch (Exception e){
            return this.cartResponseService.responseError("A ocurrido un error al procesar la ORDEN, Revisa los datos proporcionados, porfavor intentalo de nuevo",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/downloadPDF")
    public ResponseEntity<Resource> downloadReport(@RequestBody OrdersReportPdfDto ordersReportPdfDto) {
        Map<String, Object> templateVariables = Map.of(
                "orders", ordersReportPdfDto.orderReportDto().orders(),
                "totalSpent", ordersReportPdfDto.orderReportDto().totalSpent(),
                "dateReport", ordersReportPdfDto.orderReportDto().dateReport(),
                "typeReport", ordersReportPdfDto.typeReport(),
                "rangeDate", ordersReportPdfDto.startDate() + " - " + ordersReportPdfDto.endDate(),
                "payMethod", ordersReportPdfDto.paymentMethod(),
                "status", ordersReportPdfDto.processStatus(),
                "size", ordersReportPdfDto.size()
        );
        return this.downloadPdfService.downloadPdf("orderReport", templateVariables);
    }

    @GetMapping("/users/downloadPDF")
    public ResponseEntity<Resource> downloadReportUser(@RequestBody UserOrderReportDtoPdf userOrderReportDtoPdf) {
        Map<String, Object> templateVariables = Map.of(
                "users", userOrderReportDtoPdf.userOrderReportDto().users(),
                "totalCostDelivery", userOrderReportDtoPdf.userOrderReportDto().totalCostDelivery(),
                "totalPurchases", userOrderReportDtoPdf.userOrderReportDto().totalPurchases(),
                "totalSpent", userOrderReportDtoPdf.userOrderReportDto().totalSpent(),
                "dateReport", userOrderReportDtoPdf.userOrderReportDto().dateReport(),
                "typeReport", userOrderReportDtoPdf.typeReport(),
                "rangeDate", userOrderReportDtoPdf.startDate() + " - " + userOrderReportDtoPdf.endDate(),
                "payMethod", userOrderReportDtoPdf.paymentMethod(),
                "status", userOrderReportDtoPdf.processStatus(),
                "size", userOrderReportDtoPdf.size()
        );
        return this.downloadPdfService.downloadPdf("userOrderReport", templateVariables);
    }
}
