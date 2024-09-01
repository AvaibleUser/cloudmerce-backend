package com.ayds.Cloudmerce.controller;

import com.ayds.Cloudmerce.model.dto.cart.*;
import com.ayds.Cloudmerce.model.entity.CartEntity;
import com.ayds.Cloudmerce.model.entity.ProcessStatusEntity;
import com.ayds.Cloudmerce.service.CartResponseService;
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

    @Autowired
    private CartResponseService cartResponseService;

    @GetMapping
    public ResponseEntity<Object> getAllCartsWithItems() {
        List<CartResponseDto> cartDTOList = cartService.getAllCartsWithItems();
        return this.cartResponseService.responseSuccess(cartDTOList,"Lista de todos los carrito sin restricciones",HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> createCartAndOrder(@Valid @RequestBody CartRequestDTO requestDTO, @RequestParam(value = "order", required = false, defaultValue = "false") boolean order) {
        try {
            CartDTO cartDTO = requestDTO.getCart();
            OrderDTO orderDTO = requestDTO.getOrder();
            CartRequestDTO responseDTO = new CartRequestDTO();
            if (order) {
                responseDTO = this.cartService.registerCartAndOrder(cartDTO, orderDTO);
                return this.cartResponseService.responseSuccess(responseDTO,"Se guardo con exito el carrito y el pedido!",HttpStatus.CREATED);
            }
            cartDTO = this.cartService.registerCart(cartDTO);
            responseDTO.setCart(cartDTO);
            return this.cartResponseService.responseSuccess(responseDTO,"el carrito se guardo con exito!",HttpStatus.CREATED);
        }catch (Exception e) {
            return this.cartResponseService.responseError("A ocurrido un error al procesar el Carrito de Compras, Revisa los datos proporcionados, porfavor intentalo de nuevo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping(path = "{cartId}")
    public ResponseEntity<Object> updateStatusCart(@Valid @PathVariable("cartId") @NonNull @Positive Integer cartId, @RequestBody Map<String, Integer> body) {
        try {
            Integer statusId = body.get("statusId");
            //verifica si el status existe
            ProcessStatusEntity process = this.processStatusService.existsProcessStatusByProcessId(statusId);
            if (process == null) {
                return this.cartResponseService.responseError("No Existe el ESTADO al que intenta actulizar ", HttpStatus.CONFLICT);
            }
            //verifica si el id del carrito es valido
            CartEntity cartUpdate = this.cartService.existCart(cartId);
            if (cartUpdate == null) {
                return this.cartResponseService.responseError("No Existe el CARRITO al que intenta actulizar ", HttpStatus.CONFLICT);
            }
            CartDTO cartResponse = this.cartService.updateCart(cartUpdate, statusId);
            return this.cartResponseService.responseSuccess(cartResponse,"Carrito actulizado a: "+ process.getStatus()+" con exito!",HttpStatus.ACCEPTED);
        }catch (Exception e) {
            return this.cartResponseService.responseError("A ocurrido un error al procesar el Carrito de Compras, Revisa los datos proporcionados, porfavor intentalo de nuevo",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/processStatus")
    public ResponseEntity<Object> getAllProcessStatus(){
        List<ProcessStatusDTO> processRes = this.processStatusService.getAllProcessStatus();
        return this.cartResponseService.responseSuccess(processRes,"Lista de procesos que el carrito, pedido soportan", HttpStatus.OK);
    }

    @GetMapping("/paymentMethods")
    public ResponseEntity<Object> getAllPaymentMethod(){
        List<PaymentMethodDTO> paymentMethodRess = this.paymentMethodService.getAllPaymentMethods();
        return this.cartResponseService.responseSuccess(paymentMethodRess,"Lista de metodos de pago que soporta la aplicacion", HttpStatus.OK);

    }

}
