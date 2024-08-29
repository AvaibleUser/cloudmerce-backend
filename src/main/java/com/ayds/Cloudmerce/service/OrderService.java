package com.ayds.Cloudmerce.service;

import com.ayds.Cloudmerce.model.dto.cart.OrderDTO;
import com.ayds.Cloudmerce.model.entity.CartEntity;
import com.ayds.Cloudmerce.model.entity.OrderEntity;
import com.ayds.Cloudmerce.model.entity.ProcessStatusEntity;
import com.ayds.Cloudmerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderEntity existOrder(Integer id) {
        return this.orderRepository.findById(id).orElse(null);
    }

    public OrderDTO updateStatusOrder(OrderEntity orderUpdate , ProcessStatusEntity processStatus) {
        orderUpdate.setStatusId(processStatus.getId());
        return this.convertToDTO(this.orderRepository.save(orderUpdate));
    }


    /**
     * register the order in BD
     * @param orderDTO order the request
     * @param cartId id the cart
     * @return OrderDTO registed
     */
    public OrderDTO registerOrder(OrderDTO orderDTO, Integer cartId) {
        orderDTO = this.convertToDTO(this.orderRepository.save(this.convertToEntity(orderDTO, cartId)));
        return orderDTO;
    }

    /**
     * Convert OrderEntity to OrderTDO
     * @param orderEntity order a convert
     * @return OrderTDO
     */
    private OrderDTO convertToDTO(OrderEntity orderEntity) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(orderEntity.getId());
        orderDTO.setStatusId(orderEntity.getStatusId());
        orderDTO.setDeliveryAddress(orderEntity.getDeliveryAddress());
        orderDTO.setCartId(orderEntity.getCartId());
        orderDTO.setOrderDate(orderEntity.getOrderDate());
        orderDTO.setDeliveryCost(orderEntity.getDeliveryCost());
        orderDTO.setDeliveryDate(orderEntity.getDeliveryDate());
        orderDTO.setShippingDate(orderEntity.getShippingDate());
        return orderDTO;
    }

    /**
     * Convert OrderTDO to OrderEntity
     * @param orderDTO order a convert
     * @param idCart   id the cart
     * @return OrderEntity
     */
    private OrderEntity convertToEntity(OrderDTO orderDTO,  Integer idCart) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setDeliveryAddress(orderDTO.getDeliveryAddress());
        orderEntity.setCartId(idCart);
        orderEntity.setDeliveryCost(orderDTO.getDeliveryCost());
        orderEntity.setStatusId(orderDTO.getStatusId());
        return orderEntity;
    }
}
