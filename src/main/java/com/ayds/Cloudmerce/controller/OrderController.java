package com.ayds.Cloudmerce.controller;

import com.ayds.Cloudmerce.model.dto.cart.OrderDTO;
import com.ayds.Cloudmerce.model.dto.cart.OrderResponseDto;
import com.ayds.Cloudmerce.model.entity.OrderEntity;
import com.ayds.Cloudmerce.model.entity.ProcessStatusEntity;
import com.ayds.Cloudmerce.service.CartResponseService;
import com.ayds.Cloudmerce.service.CartService;
import com.ayds.Cloudmerce.service.OrderService;
import com.ayds.Cloudmerce.service.ProcessStatusService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private CartResponseService cartResponseService;

    @GetMapping
    public ResponseEntity<Object> getAllOrders() {
        List<OrderResponseDto> list = this.orderService.getAllOrders();
        return this.cartResponseService.responseSuccess(list,"Lista de todas las ORDENES", HttpStatus.OK);
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
}
