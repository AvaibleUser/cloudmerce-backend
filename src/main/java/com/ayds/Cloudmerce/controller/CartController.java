package com.ayds.Cloudmerce.controller;

import com.ayds.Cloudmerce.model.dto.cart.CartDTO;
import com.ayds.Cloudmerce.model.dto.cart.CartRequestDTO;
import com.ayds.Cloudmerce.model.dto.cart.OrderDTO;
import com.ayds.Cloudmerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<String> createCartAndOrder(@RequestBody CartRequestDTO requestDTO, @RequestParam(value = "order", required = false, defaultValue = "false") boolean order) {
        CartDTO cartDTO = requestDTO.getCart();
        OrderDTO orderDTO = requestDTO.getOrder();

        System.out.println("valor de la order: "+ order);
        // Guardar el carrito en la base de datos...
        System.out.println(cartDTO.toString());
        if (orderDTO != null) {
            // Guardar el pedido si está presente...
        }

        return ResponseEntity.ok("Cart and order processed successfully");
    }
}
