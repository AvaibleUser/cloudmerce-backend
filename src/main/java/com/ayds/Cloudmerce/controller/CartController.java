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

@RestController
@RequestMapping("/api/sales/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<Object> createCartAndOrder(@RequestBody CartRequestDTO requestDTO, @RequestParam(value = "order", required = false, defaultValue = "false") boolean order) {
        CartDTO cartDTO = requestDTO.getCart();
        OrderDTO orderDTO = requestDTO.getOrder();
        CartEntity cartEntity = this.cartService.registerCart(cartDTO);
        if (orderDTO != null) {
            // Guardar el pedido si está presente...
        }

        return new ResponseEntity<>(cartEntity, HttpStatus.CREATED);
    }
}
