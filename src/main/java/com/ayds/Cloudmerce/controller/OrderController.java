package com.ayds.Cloudmerce.controller;

import com.ayds.Cloudmerce.model.dto.cart.OrderDTO;
import com.ayds.Cloudmerce.model.entity.OrderEntity;
import com.ayds.Cloudmerce.model.entity.ProcessStatusEntity;
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

    @PatchMapping(path = "{orderId}")
    public ResponseEntity<Object> updateStatusOrder(@Valid @PathVariable("orderId") @NonNull @Positive Integer orderId, @RequestBody Map<String, Integer> body) {
        HashMap<String,Object> response = new HashMap<>();
        try{
            Integer statusId = body.get("statusId");
            System.out.println(statusId);
            //verifica si el status existe
            ProcessStatusEntity process = this.processStatusService.existsProcessStatusByProcessId(statusId);
            if (process == null) {
                response.put("error", true);
                response.put("message", "No Existe el ESTADO al que intenta actulizar");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
            //verificar si existe la orden
            OrderEntity orderUpdate = this.orderService.existOrder(orderId);
            if (orderUpdate == null) {
                response.put("error", true);
                response.put("message", "No Existe la ORDEN al que intenta actulizar ");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
            OrderDTO orderResponse = this.cartService.updateStatusOrderAndCart(orderUpdate,process);
            response.put("message", "Orden actulizado a: "+ process.getStatus()+" con exito!");
            response.put("data", orderResponse);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }catch (Exception e){
            System.out.println(e.getMessage());
            response.put("error", true);
            response.put("message", "A ocurrido un error al procesar el Carrito de Compras, Revisa los datos proporcionados, porfavor intentalo de nuevo");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
