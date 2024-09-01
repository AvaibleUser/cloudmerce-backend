package com.ayds.Cloudmerce.service;

import com.ayds.Cloudmerce.model.dto.cart.CartDTO;
import com.ayds.Cloudmerce.model.dto.cart.CartItemDTO;
import com.ayds.Cloudmerce.model.dto.cart.CartRequestDTO;
import com.ayds.Cloudmerce.model.dto.cart.OrderDTO;
import com.ayds.Cloudmerce.model.entity.CartEntity;
import com.ayds.Cloudmerce.model.entity.OrderEntity;
import com.ayds.Cloudmerce.model.entity.ProcessStatusEntity;
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
    private final ProcessStatusService processStatusService;

    public List<CartEntity> getAllCartsWithItems() {
        return cartRepository.findAll();
    }

    public CartEntity existCart(Integer idCart){
        return this.cartRepository.findById(idCart).orElse(null);
    }

    public CartDTO updateCart(CartEntity cartUpdate, Integer statusId) {
        cartUpdate.setStatusId(statusId);
        return this.convertToCartDTO(this.cartRepository.save(cartUpdate));
    }

    @Transactional
    public OrderDTO updateStatusOrderAndCart(OrderEntity orderUpdate , ProcessStatusEntity processStatus){
        this.updateDateProcessOrder(orderUpdate, processStatus);
        return this.orderService.updateStatusOrder(orderUpdate, processStatus);
    }

    private void updateDateProcessOrder(OrderEntity orderUpdate, ProcessStatusEntity processStatus){
        LocalDateTime dateTime = LocalDateTime.now();
        switch (processStatus.getStatus()){
            case "Enviado":
                orderUpdate.setShippingDate(dateTime);
                break;
            case "Entregado":
                orderUpdate.setDeliveryDate(dateTime);
                //marcar como completado el carrito
                ProcessStatusEntity process = this.processStatusService.existsProcessStatusByProcessStatus("Completado");
                CartEntity cartUpdate = this.existCart(orderUpdate.getCartId());
                this.updateCart(cartUpdate,process.getId());
                break;
        }
    }


    @Transactional
    public CartDTO registerCart(CartDTO cartDTO) {
        CartEntity cart = cartRepository.save(this.convertToCartEntity(cartDTO));
        CartDTO cartResponse = this.convertToCartDTO(cart);
        List<CartItemDTO> items = this.cartItemService.registerCartItems(cartDTO.getItems(),cart);
        cartResponse.setItems(items);
        return cartResponse;
    }

    @Transactional
    public CartRequestDTO registerCartAndOrder(CartDTO cartDTO, OrderDTO orderDTO) {
        CartRequestDTO responseDTO = new CartRequestDTO();
        CartEntity cart = cartRepository.save(this.convertToCartEntity(cartDTO));
        CartDTO cartResponse = this.convertToCartDTO(cart);
        List<CartItemDTO> items = this.cartItemService.registerCartItems(cartDTO.getItems(),cart);
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
