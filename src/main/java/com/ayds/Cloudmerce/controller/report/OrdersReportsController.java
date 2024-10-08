package com.ayds.Cloudmerce.controller.report;

import com.ayds.Cloudmerce.model.dto.cart.OrderDTO;
import com.ayds.Cloudmerce.model.dto.cart.OrderResponseDto;
import com.ayds.Cloudmerce.model.dto.report.*;
import com.ayds.Cloudmerce.model.entity.CompanyEntity;
import com.ayds.Cloudmerce.repository.CompanyRepository;
import com.ayds.Cloudmerce.service.*;
import com.ayds.Cloudmerce.service.report.DownloadExcelService;
import com.ayds.Cloudmerce.service.report.DownloadPdfService;
import com.ayds.Cloudmerce.service.report.OrderReportService;
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

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DownloadExcelService downloadExcelService;

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

    @PostMapping("/downloadPDF")
    public ResponseEntity<Resource> downloadReport(@RequestBody OrdersReportPdfDto ordersReportPdfDto) {
        CompanyEntity companyEntity = companyRepository.findTopByOrderByIdAsc();
        String nameCompany = "compañia Z.X.Y";
        String companyLogo = "https://e7.pngegg.com/pngimages/996/491/png-clipart-shopify-e-commerce-logo-web-design-design-web-design-logo.png";
        if (companyEntity != null) {
            nameCompany = companyEntity.getName();
            companyLogo = companyEntity.getLogoPath();
        }

        Map<String, Object> templateVariables = Map.of(
                "orders", ordersReportPdfDto.orderReportDto().orders(),
                "totalSpent", ordersReportPdfDto.orderReportDto().totalSpent(),
                "dateReport", ordersReportPdfDto.orderReportDto().dateReport(),
                "typeReport", ordersReportPdfDto.typeReport(),
                "rangeDate", ordersReportPdfDto.startDate() + " - " + ordersReportPdfDto.endDate(),
                "payMethod", ordersReportPdfDto.paymentMethod(),
                "status", ordersReportPdfDto.processStatus(),
                "size", ordersReportPdfDto.size(),
                "nameCompany", nameCompany,
                "companyLogo", companyLogo
        );
        return this.downloadPdfService.downloadPdf("orderReport", templateVariables);
    }

    @PostMapping("/users/downloadPDF")
    public ResponseEntity<Resource> downloadReportUser(@RequestBody UserOrderReportDtoPdf userOrderReportDtoPdf) {
        CompanyEntity companyEntity = companyRepository.findTopByOrderByIdAsc();
        String nameCompany = "compañia Z.X.Y";
        String companyLogo = "https://e7.pngegg.com/pngimages/996/491/png-clipart-shopify-e-commerce-logo-web-design-design-web-design-logo.png";
        if (companyEntity != null) {
            nameCompany = companyEntity.getName();
            companyLogo = companyEntity.getLogoPath();
        }

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("users", userOrderReportDtoPdf.userOrderReportDto().users());
        templateVariables.put("totalCostDelivery", userOrderReportDtoPdf.userOrderReportDto().totalCostDelivery());
        templateVariables.put("totalPurchases", userOrderReportDtoPdf.userOrderReportDto().totalPurchases());
        templateVariables.put("totalSpent", userOrderReportDtoPdf.userOrderReportDto().totalSpent());
        templateVariables.put("dateReport", userOrderReportDtoPdf.userOrderReportDto().dateReport());
        templateVariables.put("typeReport", userOrderReportDtoPdf.typeReport());
        templateVariables.put("rangeDate", userOrderReportDtoPdf.startDate() + " - " + userOrderReportDtoPdf.endDate());
        templateVariables.put("payMethod", userOrderReportDtoPdf.paymentMethod());
        templateVariables.put("status", userOrderReportDtoPdf.processStatus());
        templateVariables.put("size", userOrderReportDtoPdf.size());
        templateVariables.put("nameCompany", nameCompany);
        templateVariables.put("companyLogo", companyLogo);


        return this.downloadPdfService.downloadPdf("userOrderReport", templateVariables);
    }

    @PostMapping("/download-excel")
    ResponseEntity<byte[]>downloadReportExcel(@RequestBody OrdersReportPdfDto ordersReportPdfDto)throws IOException {
        List<OrderDto> orders = ordersReportPdfDto.orderReportDto().orders();
        List<String> headers = new ArrayList<>();
        headers.add("Cliente");
        headers.add("Fecha de Orden");
        headers.add("Fecha de Envio");
        headers.add("Fecha de Entrega");
        headers.add("Total");
        List<Object> userObjects = new ArrayList<>();
        for (OrderDto order : orders) {
            userObjects.add(order.user() == null ? "" : order.user());
            userObjects.add(order.orderDate() == null ? "" : order.orderDate());
            userObjects.add(order.shippingDate() == null ? "" : order.shippingDate() );
            userObjects.add(order.deliveryDate() == null ? "" : order.deliveryDate());
            userObjects.add(order.total() == null ? "" : order.total());
        }
        userObjects.add("Total General");
        userObjects.add("");
        userObjects.add("");
        userObjects.add("");
        userObjects.add(ordersReportPdfDto.orderReportDto().totalSpent());
        return this.downloadExcelService.generateExcelReport(headers, userObjects, "reporte_ordenes");
    }

    @PostMapping("/users/download-excel")
    ResponseEntity<byte[]>downloadReportExcelUser(@RequestBody UserOrderReportDtoPdf userOrderReportDtoPdf)throws IOException {
        List<UserOrderDto> users = userOrderReportDtoPdf.userOrderReportDto().users();
        List<String> headers = new ArrayList<>();
        headers.add("Cliente");
        headers.add("NIT");
        headers.add("Cantidad Ordenes");
        headers.add("Total de Costo Envio");
        headers.add("Total Gastado");
        List<Object> userObjects = new ArrayList<>();
        for (UserOrderDto user : users) {
            userObjects.add(user.name() == null ? "" : user.name());
            userObjects.add(user.nit() == null ? "" : user.nit());
            userObjects.add(user.totalPurchases() == null ? "" : user.totalPurchases() );
            userObjects.add(user.totalCostDelivery() == null ? "" : user.totalCostDelivery());
            userObjects.add(user.totalSpent() == null ? "" : user.totalSpent());
        }
        userObjects.add("Total Ventas");
        userObjects.add("");
        userObjects.add(userOrderReportDtoPdf.userOrderReportDto().totalPurchases());
        userObjects.add("");
        userObjects.add("");
        //
        userObjects.add("Total por envio");
        userObjects.add("");
        userObjects.add("");
        userObjects.add(userOrderReportDtoPdf.userOrderReportDto().totalCostDelivery());
        userObjects.add("");
        //
        userObjects.add("Total General");
        userObjects.add("");
        userObjects.add("");
        userObjects.add("");
        userObjects.add(userOrderReportDtoPdf.userOrderReportDto().totalSpent());
        return this.downloadExcelService.generateExcelReport(headers, userObjects, "reporte_usuarios_orden");
    }


}
