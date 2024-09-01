package com.ayds.Cloudmerce.controller;

import com.ayds.Cloudmerce.model.dto.cart.*;
import com.ayds.Cloudmerce.model.entity.CartEntity;
import com.ayds.Cloudmerce.model.entity.ProcessStatusEntity;
import com.ayds.Cloudmerce.service.CartService;
import com.ayds.Cloudmerce.service.PaymentMethodService;
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
@RequestMapping("/api/sales/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProcessStatusService processStatusService;

    @Autowired
    private PaymentMethodService paymentMethodService;

    @GetMapping
    public List<CartEntity> getAllCartsWithItems() {
        return cartService.getAllCartsWithItems();
    }

    @PostMapping
    public ResponseEntity<Object> createCartAndOrder(@Valid @RequestBody CartRequestDTO requestDTO, @RequestParam(value = "order", required = false, defaultValue = "false") boolean order) {
        HashMap<String,Object> response = new HashMap<>();
        try {
            CartDTO cartDTO = requestDTO.getCart();
            OrderDTO orderDTO = requestDTO.getOrder();
            if (order) {
                CartRequestDTO responseDTO = this.cartService.registerCartAndOrder(cartDTO, orderDTO);
                response.put("data", responseDTO);
                response.put("message", "Se guardo con exito el carrito y el pedido!");
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
            cartDTO = this.cartService.registerCart(cartDTO);
            response.put("data", cartDTO);
            response.put("message", "el carrito se guardo con exito!");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch (Exception e) {
            response.put("error", true);
            response.put("message", "A ocurrido un error al procesar el Carrito de Compras, Revisa los datos proporcionados, porfavor intentalo de nuevo");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping(path = "{cartId}")
    public ResponseEntity<Object> updateStatusCart(@Valid @PathVariable("cartId") @NonNull @Positive Integer cartId, @RequestBody Map<String, Integer> body) {
        HashMap<String,Object> response = new HashMap<>();
        try {
            Integer statusId = body.get("statusId");
            //verifica si el status existe
            ProcessStatusEntity process = this.processStatusService.existsProcessStatusByProcessId(statusId);
            if (process == null) {
                response.put("error", true);
                response.put("message", "No Existe el ESTADO al que intenta actulizar ");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
            //verifica si el id del carrito es valido
            CartEntity cartUpdate = this.cartService.existCart(cartId);
            if (cartUpdate == null) {
                response.put("error", true);
                response.put("message", "No Existe el CARRITO al que intenta actulizar ");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
            CartDTO cartResponse = this.cartService.updateCart(cartUpdate, statusId);
            response.put("message", "Carrito actulizado a: "+ process.getStatus()+" con exito!");
            response.put("data", cartResponse);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }catch (Exception e) {
            response.put("error", true);
            response.put("message", "A ocurrido un error al procesar el Carrito de Compras, Revisa los datos proporcionados, porfavor intentalo de nuevo");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/processStatus")
    public ResponseEntity<Object> getAllProcessStatus(){
        HashMap<String,Object> response = new HashMap<>();
        List<ProcessStatusDTO> processRes = this.processStatusService.getAllProcessStatus();
        response.put("message", "Lista de procesos que el carrito, pedido soportan");
        response.put("data", processRes);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/paymentMethods")
    public ResponseEntity<Object> getAllPaymentMethod(){
        HashMap<String,Object> response = new HashMap<>();
        List<PaymentMethodDTO> paymentMethodRess = this.paymentMethodService.getAllPaymentMethods();
        response.put("message", "Lista de metodos de pago que soporta la aplicacion");
        response.put("data", paymentMethodRess);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
