package com.ayds.Cloudmerce.service;

import com.ayds.Cloudmerce.model.dto.cart.CartDTO;
import com.ayds.Cloudmerce.model.entity.CartEntity;
import com.ayds.Cloudmerce.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    public CartEntity registerCart(CartDTO cartDTO) {
        return cartRepository.save(this.convertToCartEntity(cartDTO));
    }

    private CartEntity convertToCartEntity(CartDTO cartDTO) {
        CartEntity cartEntity = new CartEntity();
        cartEntity.setTotal(cartDTO.getTotal());
        cartEntity.setTax(cartDTO.getTax());
        cartEntity.setDeliveryType(cartDTO.getDeliveryType());
        cartEntity.setPaymentMethodId(cartDTO.getPaymentMethodId());
        cartEntity.setStatusId(cartDTO.getStatusId());
        cartEntity.setUserId(cartDTO.getUserId());
        return cartEntity;
    }

    //TODO: ver la logica sobre obtener los items del entity
    private CartDTO convertToCartDTO(CartEntity cartEntity) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cartEntity.getId());
        cartDTO.setTax(cartEntity.getTax());
        cartDTO.setTotal(cartEntity.getTotal());
        cartDTO.setDeliveryType(cartEntity.getDeliveryType());
        cartDTO.setUserId(cartEntity.getUserId());
        cartDTO.setStatusId(cartEntity.getStatusId());
        cartDTO.setPaymentMethodId(cartEntity.getPaymentMethodId());
        cartDTO.setCreatedAt(cartEntity.getCreatedAt());
        return cartDTO;
    }
}
