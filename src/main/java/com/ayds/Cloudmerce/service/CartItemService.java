package com.ayds.Cloudmerce.service;

import com.ayds.Cloudmerce.model.dto.cart.CartItemDTO;
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


    public List<CartItemDTO> registerCartItems(List<CartItemDTO> cartItemsDTO, Integer idCart) {
        // Guardar los elementos y convertir a DTOs
        return cartItemsDTO.stream()
                .map(cartItemDTO -> convertToCartItemEntity(cartItemDTO, idCart))
                .map(this.cartItemRepository::save)
                .map(this::convertToCartItemDTO)
                .collect(Collectors.toList());
    }

    private CartItemEntity convertToCartItemEntity(CartItemDTO cartItemDTO,  Integer idCart) {
        CartItemEntity cartItemEntity = new CartItemEntity();
        cartItemEntity.setCartId(idCart);
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
        cartItemDTO.setCartId(cartItemEntity.getCartId());
        cartItemDTO.setId(cartItemEntity.getId());
        return cartItemDTO;
    }
}
