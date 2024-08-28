package com.ayds.Cloudmerce.service;

import com.ayds.Cloudmerce.model.dto.cart.CartDTO;
import com.ayds.Cloudmerce.model.dto.cart.CartItemDTO;
import com.ayds.Cloudmerce.model.dto.cart.CartRequestDTO;
import com.ayds.Cloudmerce.model.dto.cart.OrderDTO;
import com.ayds.Cloudmerce.model.entity.CartEntity;
import com.ayds.Cloudmerce.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemService cartItemService;
    private final OrderService orderService;

    @Transactional
    public CartDTO registerCart(CartDTO cartDTO) {
        CartDTO cartResponse = this.convertToCartDTO(cartRepository.save(this.convertToCartEntity(cartDTO)));
        List<CartItemDTO> items = this.cartItemService.registerCartItems(cartDTO.getItems(),cartResponse.getId());
        cartResponse.setItems(items);
        return cartResponse;
    }

    @Transactional
    public CartRequestDTO registerCartAndOrder(CartDTO cartDTO, OrderDTO orderDTO) {
        CartRequestDTO responseDTO = new CartRequestDTO();
        CartDTO cartResponse = this.convertToCartDTO(cartRepository.save(this.convertToCartEntity(cartDTO)));
        List<CartItemDTO> items = this.cartItemService.registerCartItems(cartDTO.getItems(),cartResponse.getId());
        cartResponse.setItems(items);
        OrderDTO orderResponse = this.orderService.registerOrder(orderDTO,cartResponse.getId());
        responseDTO.setOrder(orderResponse);
        responseDTO.setCart(cartResponse);
        return responseDTO;
    }

    /**
     * convert cartDTO a CartEntity
     * @param cartDTO cartDTO
     * @return CartEntity
     */
    private CartEntity convertToCartEntity(CartDTO cartDTO) {
        CartEntity cartEntity = new CartEntity();
        cartEntity.setTotal(cartDTO.getTotal());
        cartEntity.setTax(cartDTO.getTax());
        cartEntity.setDeliveryType(cartDTO.getDeliveryType());
        cartEntity.setPaymentMethodId(cartDTO.getPaymentMethodId());
        cartEntity.setStatusId(cartDTO.getStatusId());
        cartEntity.setUserId(cartDTO.getUserId());
        cartEntity.setCreatedAt(LocalDateTime.now());
        return cartEntity;
    }

    /**
     * convert cartEntity to cartDTO
     * @param cartEntity cartEntity
     * @return CartDTo
     */
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
