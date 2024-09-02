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

    /*
     * aparado para peticiones GET con usuario especifico
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<Object>  getUserCart(@PathVariable("id") Integer id, @RequestParam(value = "size", defaultValue = "12") int size,
                                                @RequestParam(value = "startDate", defaultValue = "2000-01-01") String startDate, @RequestParam(value = "endDate", defaultValue = "2099-12-31") String endDate,
                                                @RequestParam(value = "order", defaultValue = "asc") String order, @RequestParam(value = "cartIdInit", defaultValue = "0") int cartIdInit,
                                                @RequestParam(value = "paymentMethod", defaultValue = "0") int paymentMethod, @RequestParam(value = "processStatus", defaultValue = "0") int processStatus) {


        try {
            if (id <= 0){
                return this.cartResponseService.responseError("El id del usuario no puede ser menor o igual a CERO",HttpStatus.CONFLICT);
            }
            List<CartResponseDto> list = this.cartService.getCartsFilterWithParams(id, size, startDate, endDate, order, cartIdInit, processStatus,paymentMethod);
            return this.cartResponseService.responseSuccess(list,"Lista de  carritos del usuario con id: "+ id, HttpStatus.OK);
        }catch (Exception e) {
            return this.cartResponseService.responseError("A ocurrido un error al procesar el Carrito de Compras, Revisa los datos proporcionados, porfavor intentalo de nuevo",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * aparado para peticiones GET con metodo de pago especifico y filtros
     */
    @GetMapping("/paymentMethods/{idPayMethod}")
    public ResponseEntity<Object>  getPayMethodCart(@PathVariable("idPayMethod") Integer idPayMethod, @RequestParam(value = "size", defaultValue = "12") int size,
                                                    @RequestParam(value = "startDate", defaultValue = "2000-01-01") String startDate, @RequestParam(value = "endDate", defaultValue = "2099-12-31") String endDate,
                                                    @RequestParam(value = "order", defaultValue = "asc") String order, @RequestParam(value = "cartIdInit", defaultValue = "0") int cartIdInit,
                                                    @RequestParam(value = "processStatus", defaultValue = "0") int processStatus) {


        try {
            if (idPayMethod <= 0){
                return this.cartResponseService.responseError("El id del METODO DE PAGO no puede ser menor o igual a CERO",HttpStatus.CONFLICT);
            }
            List<CartResponseDto> list = this.cartService.getCartsFilterWithParams(0, size, startDate, endDate, order, cartIdInit, processStatus,idPayMethod);
            return this.cartResponseService.responseSuccess(list,"Lista de  carritos con el metodo de pago con id: "+ idPayMethod, HttpStatus.OK);
        }catch (Exception e) {
            return this.cartResponseService.responseError("A ocurrido un error al procesar el Carrito de Compras, Revisa los datos proporcionados, porfavor intentalo de nuevo",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * aparado para peticiones GET con metodo de estado del proceso y filtros
     */
    @GetMapping("/processStatus/{idProcessStatus}")
    public ResponseEntity<Object>  getProcessStatusCart(@PathVariable("idProcessStatus") Integer idProcessStatus, @RequestParam(value = "size", defaultValue = "12") int size,
                                                        @RequestParam(value = "startDate", defaultValue = "2000-01-01") String startDate, @RequestParam(value = "endDate", defaultValue = "2099-12-31") String endDate,
                                                        @RequestParam(value = "order", defaultValue = "asc") String order, @RequestParam(value = "cartIdInit", defaultValue = "0") int cartIdInit,
                                                        @RequestParam(value = "paymentMethod", defaultValue = "0") int paymentMethod) {


        try {
            if (idProcessStatus <= 0){
                return this.cartResponseService.responseError("El id del ESTADO DE PROCESO no puede ser menor o igual a CERO",HttpStatus.CONFLICT);
            }
            List<CartResponseDto> list = this.cartService.getCartsFilterWithParams(0, size, startDate, endDate, order, cartIdInit, idProcessStatus,paymentMethod);
            return this.cartResponseService.responseSuccess(list,"Lista de  carritos con el metodo de pago con id: "+ idProcessStatus, HttpStatus.OK);
        }catch (Exception e) {
            return this.cartResponseService.responseError("A ocurrido un error al procesar el Carrito de Compras, Revisa los datos proporcionados, porfavor intentalo de nuevo",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
