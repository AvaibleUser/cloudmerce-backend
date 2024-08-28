package com.ayds.Cloudmerce.controller;

import com.ayds.Cloudmerce.model.dto.cart.CartDTO;
import com.ayds.Cloudmerce.model.dto.cart.CartRequestDTO;
import com.ayds.Cloudmerce.model.dto.cart.OrderDTO;
import com.ayds.Cloudmerce.model.entity.CartEntity;
import com.ayds.Cloudmerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/sales/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<Object> createCartAndOrder(@RequestBody CartRequestDTO requestDTO, @RequestParam(value = "order", required = false, defaultValue = "false") boolean order) {
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
            System.out.println(e.getMessage());
            response.put("error", true);
            response.put("message", "A ocurrido un error al procesar el Carrito de Compras, Revisa los datos proporcionados, porfavor intentalo de nuevo");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
