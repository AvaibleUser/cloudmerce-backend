package com.ayds.Cloudmerce.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.Cloudmerce.model.dto.UserDto;
import com.ayds.Cloudmerce.model.dto.cart.OrderDTO;
import com.ayds.Cloudmerce.model.dto.cart.OrderResponseDto;
import com.ayds.Cloudmerce.model.entity.CartEntity;
import com.ayds.Cloudmerce.model.entity.CompanyEntity;
import com.ayds.Cloudmerce.model.entity.OrderEntity;
import com.ayds.Cloudmerce.model.entity.ProcessStatusEntity;
import com.ayds.Cloudmerce.service.CartResponseService;
import com.ayds.Cloudmerce.service.CartService;
import com.ayds.Cloudmerce.service.CompanyService;
import com.ayds.Cloudmerce.service.EmailService;
import com.ayds.Cloudmerce.service.FileStorageService;
import com.ayds.Cloudmerce.service.OrderService;
import com.ayds.Cloudmerce.service.PdfGeneratorService;
import com.ayds.Cloudmerce.service.ProcessStatusService;
import com.ayds.Cloudmerce.service.TemplateRendererService;
import com.ayds.Cloudmerce.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;

@RestController
@RequestMapping("/api/sales/orders")
public class OrderController {

    @Autowired
    private ProcessStatusService processStatusService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private CartResponseService cartResponseService;
    @Autowired
    private TemplateRendererService templateRendererService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PdfGeneratorService pdfService;
    @Autowired
    private FileStorageService storageService;

    @GetMapping
    public ResponseEntity<Object> getAllOrders() {
        List<OrderResponseDto> list = this.orderService.getAllOrders();
        return this.cartResponseService.responseSuccess(list,"Lista de todas las ORDENES", HttpStatus.OK);
    }

    @GetMapping("/{orderId}/bill-url")
    public ResponseEntity<String> getGeneratedBillUrl(@PathVariable @Positive long orderId) {
        return ResponseEntity.ok(storageService.loadUrl("factura_" + orderId + ".pdf"));
    }

    private void generateBill(OrderEntity order) {
        CartEntity cart = order.getCart();
        UserDto client = userService.findUserById(cart.getUserId()).orElseThrow();
        CompanyEntity company = companyService.findCompany(1).orElseThrow();

        Map<String, Object> templateVariables = Map.of(
                "bill", order,
                "cart", cart,
                "client", client,
                "cartItems", cart.getCartItems(),
                "company", company);

        String billHtml = templateRendererService.renderTemplate("bill", templateVariables);

        try {
            byte[] pdfBytes = pdfService.generatePdfFromHtmlString(billHtml);
            String billUrl;

            try (InputStream inputStream = new ByteArrayInputStream(pdfBytes)) {
                billUrl = storageService.store("factura_" + order.getId() + ".pdf", inputStream, MediaType.APPLICATION_PDF_VALUE,
                        pdfBytes.length);
            }

            String billEmail = templateRendererService.renderTemplate("bill",
                    Map.of("client", client, "billUrl", billUrl));
    
            try {
                emailService.sendHtmlEmail(company.getName(), client.email(), "Factura de Cloudmerce", billEmail);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PatchMapping(path = "{orderId}")
    public ResponseEntity<Object> updateStatusOrder(@Valid @PathVariable("orderId") @NonNull @Positive Integer orderId, @RequestBody Map<String, Integer> body) {
        try{
            Integer statusId = body.get("statusId");
            System.out.println(statusId);
            //verifica si el status existe
            ProcessStatusEntity process = this.processStatusService.existsProcessStatusByProcessId(statusId);
            if (process == null) {
                return this.cartResponseService.responseError("No Existe el ESTADO al que intenta actulizar",HttpStatus.CONFLICT);
            }
            //verificar si existe la orden
            OrderEntity orderUpdate = this.orderService.existOrder(orderId);
            if (orderUpdate == null) {
                return this.cartResponseService.responseError("No Existe la ORDEN al que intenta actulizar ", HttpStatus.CONFLICT);
            }
            OrderDTO orderResponse = this.cartService.updateStatusOrderAndCart(orderUpdate,process);

            if (process.getStatus().equals("Completado")) {
                generateBill(orderUpdate);
            }
            return this.cartResponseService.responseSuccess(orderResponse,"Orden actulizado a: "+ process.getStatus()+" con exito!", HttpStatus.ACCEPTED);
        }catch (Exception e){
            return this.cartResponseService.responseError("A ocurrido un error al procesar el Carrito de Compras, Revisa los datos proporcionados, porfavor intentalo de nuevo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Object> getUserOrders(@PathVariable("id") Integer id, @RequestParam(value = "size", defaultValue = "12") int size,
                                                @RequestParam(value = "startDate", defaultValue = "2000-01-01") String startDate, @RequestParam(value = "endDate", defaultValue = "2099-12-31") String endDate,
                                                @RequestParam(value = "order", defaultValue = "asc") String order, @RequestParam(value = "orderIdInit", defaultValue = "0") int orderIdInit, @RequestParam(value = "processStatus", defaultValue = "0") int processStatus,
                                                @RequestParam(value = "dateFilter", defaultValue = "orderDate") String dateFilter, @RequestParam(value = "paymentMethod", defaultValue = "0") int paymentMethod) {
        try {
            if (id <= 0){
                return this.cartResponseService.responseError("El id del usuario no puede ser menor o igual a CERO",HttpStatus.CONFLICT);
            }
            List<OrderResponseDto> list = this.orderService.getOrdersFilterWithParams(id, size, startDate, endDate, order, orderIdInit, processStatus,dateFilter,paymentMethod);
            return cartResponseService.responseSuccess(list,"Lista de todas las ORDENES del usuario con id: "+id , HttpStatus.OK);
        }catch (Exception e){
            return this.cartResponseService.responseError("A ocurrido un error al procesar la ORDEN, Revisa los datos proporcionados, porfavor intentalo de nuevo",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/paymentMethods/{id}")
    public ResponseEntity<Object> getOrdersPayMethods(@PathVariable("id") Integer id, @RequestParam(value = "size", defaultValue = "12") int size,
                                                @RequestParam(value = "startDate", defaultValue = "2000-01-01") String startDate, @RequestParam(value = "endDate", defaultValue = "2099-12-31") String endDate,
                                                @RequestParam(value = "order", defaultValue = "asc") String order, @RequestParam(value = "orderIdInit", defaultValue = "0") int orderIdInit, @RequestParam(value = "processStatus", defaultValue = "0") int processStatus,
                                                @RequestParam(value = "dateFilter", defaultValue = "orderDate") String dateFilter) {
        try {
            if (id <= 0){
                return this.cartResponseService.responseError("El id del usuario no puede ser menor o igual a CERO",HttpStatus.CONFLICT);
            }
            List<OrderResponseDto> list = this.orderService.getOrdersFilterWithParams(0, size, startDate, endDate, order, orderIdInit, processStatus,dateFilter,id);
            return cartResponseService.responseSuccess(list,"Lista de todas las ORDENES con con el metodo de pago con id "+id , HttpStatus.OK);
        }catch (Exception e){
            return this.cartResponseService.responseError("A ocurrido un error al procesar la ORDEN, Revisa los datos proporcionados, porfavor intentalo de nuevo",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/processStatus/{id}")
    public ResponseEntity<Object> getOrdersProcessStatus(@PathVariable("id") Integer id, @RequestParam(value = "size", defaultValue = "12") int size,
                                                      @RequestParam(value = "startDate", defaultValue = "2000-01-01") String startDate, @RequestParam(value = "endDate", defaultValue = "2099-12-31") String endDate,
                                                      @RequestParam(value = "order", defaultValue = "asc") String order, @RequestParam(value = "orderIdInit", defaultValue = "0") int orderIdInit, @RequestParam(value = "paymentMethod", defaultValue = "0") int paymentMethod,
                                                      @RequestParam(value = "dateFilter", defaultValue = "orderDate") String dateFilter) {
        try {
            if (id <= 0){
                return this.cartResponseService.responseError("El id del usuario no puede ser menor o igual a CERO",HttpStatus.CONFLICT);
            }
            List<OrderResponseDto> list = this.orderService.getOrdersFilterWithParams(0, size, startDate, endDate, order, orderIdInit, id,dateFilter,paymentMethod);
            return cartResponseService.responseSuccess(list,"Lista de todas las ORDENES con con el estado de proceso con id: "+id , HttpStatus.OK);
        }catch (Exception e){
            return this.cartResponseService.responseError("A ocurrido un error al procesar la ORDEN, Revisa los datos proporcionados, porfavor intentalo de nuevo",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
