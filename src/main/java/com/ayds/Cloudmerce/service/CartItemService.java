package com.ayds.Cloudmerce.service;

import com.ayds.Cloudmerce.model.dto.cart.CartItemDTO;
import com.ayds.Cloudmerce.model.entity.CartEntity;
import com.ayds.Cloudmerce.model.entity.CartItemEntity;
import com.ayds.Cloudmerce.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;


    public List<CartItemDTO> registerCartItems(List<CartItemDTO> cartItemsDTO, CartEntity cart) {
        // Guardar los elementos y convertir a DTOs
        return cartItemsDTO.stream()
                .map(cartItemDTO -> convertToCartItemEntity(cartItemDTO, cart))
                .map(this.cartItemRepository::save)
                .map(this::convertToCartItemDTO)
                .collect(Collectors.toList());
    }

    private CartItemEntity convertToCartItemEntity(CartItemDTO cartItemDTO, CartEntity cart) {
        CartItemEntity cartItemEntity = new CartItemEntity();
        cartItemEntity.setCart(cart);
        cartItemEntity.setProductId(cartItemDTO.getProductId());
        cartItemEntity.setQuantity(cartItemDTO.getQuantity());
        cartItemEntity.setSubtotal(cartItemDTO.getSubTotal());
        return cartItemEntity;
    }

    private CartItemDTO convertToCartItemDTO(CartItemEntity cartItemEntity) {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductId(cartItemEntity.getProductId());
        cartItemDTO.setQuantity(cartItemEntity.getQuantity());
        cartItemDTO.setSubTotal(cartItemEntity.getSubtotal());
        cartItemDTO.setCartId(cartItemEntity.getCart().getId());
        cartItemDTO.setId(cartItemEntity.getId());
        return cartItemDTO;
    }
}
