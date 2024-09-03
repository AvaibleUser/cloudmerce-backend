package com.ayds.Cloudmerce.service;

import com.ayds.Cloudmerce.model.dto.cart.*;
import com.ayds.Cloudmerce.model.entity.CartEntity;
import com.ayds.Cloudmerce.model.entity.OrderEntity;
import com.ayds.Cloudmerce.model.entity.ProcessStatusEntity;
import com.ayds.Cloudmerce.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    @PersistenceContext
    private EntityManager entityManager;

    private final OrderRepository orderRepository;
    private final CartItemService cartItemService;


    public List<OrderResponseDto> getAllOrders() {
        return this.orderRepository.findAll().stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }

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
     * @param cart id the cart
     * @return OrderDTO registed
     */
    public OrderDTO registerOrder(OrderDTO orderDTO, CartEntity cart) {
        orderDTO = this.convertToDTO(this.orderRepository.save(this.convertToEntity(orderDTO, cart)));
        return orderDTO;
    }

    /**
     * Convert OrderEntity to OrderTDO
     * @param orderEntity order a convert
     * @return OrderTDO
     */
    public OrderDTO convertToDTO(OrderEntity orderEntity) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(orderEntity.getId());
        orderDTO.setStatusId(orderEntity.getStatusId());
        orderDTO.setDeliveryAddress(orderEntity.getDeliveryAddress());
        orderDTO.setCartId(orderEntity.getCart().getId());
        orderDTO.setOrderDate(orderEntity.getOrderDate());
        orderDTO.setDeliveryCost(orderEntity.getDeliveryCost());
        orderDTO.setDeliveryDate(orderEntity.getDeliveryDate());
        orderDTO.setShippingDate(orderEntity.getShippingDate());
        return orderDTO;
    }

    public OrderResponseDto convertToResponseDto(OrderEntity orderEntity) {
        OrderResponseDto orderDTO = new OrderResponseDto();
        orderDTO.setId(orderEntity.getId());
        orderDTO.setStatusId(orderEntity.getStatusId());
        orderDTO.setDeliveryAddress(orderEntity.getDeliveryAddress());
        orderDTO.setOrderDate(orderEntity.getOrderDate());
        orderDTO.setCart(this.convertToCartDTO(orderEntity.getCart()));
        orderDTO.setDeliveryCost(orderEntity.getDeliveryCost());
        orderDTO.setDeliveryDate(orderEntity.getDeliveryDate());
        orderDTO.setShippingDate(orderEntity.getShippingDate());
        return orderDTO;
    }

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
        if (!cartEntity.getCartItems().isEmpty()){
            List<CartItemDTO> listDTO = cartEntity.getCartItems().stream()
                    .map(this.cartItemService::convertToCartItemDTO)
                    .collect(Collectors.toList());
            cartDTO.setItems(listDTO);
        }
        return cartDTO;
    }

    /**
     * Convert OrderTDO to OrderEntity
     * @param orderDTO order a convert
     * @param Cart   id the cart
     * @return OrderEntity
     */
    private OrderEntity convertToEntity(OrderDTO orderDTO, CartEntity Cart) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setDeliveryAddress(orderDTO.getDeliveryAddress());
        orderEntity.setCart(Cart);
        orderEntity.setDeliveryCost(orderDTO.getDeliveryCost());
        orderEntity.setStatusId(orderDTO.getStatusId());
        return orderEntity;
    }


    /*
     * apartado para peticiones get de las ordenes, con sus filtraciones
     */

    private void addFilterPayMethod(int paymentMethod, List<Predicate> predicates, Root<OrderEntity> orderEnti,  CriteriaBuilder cb){
        //if (paymentMethod > 0){
          //  Join<OrderEntity, CartEntity> cartJoin = orderEnti.join("cart");
           // predicates.add(cb.equal(cartJoin.get("paymentMethodId"), paymentMethod));
        //}
    }

    private void addFilterProcessSatatus(int processStatus, List<Predicate> predicates, Root<OrderEntity> cart,  CriteriaBuilder cb){
        if (processStatus > 0){
            predicates.add(cb.equal(cart.get("statusId"), processStatus));
        }
    }

    private void addFilterOrder(Root<OrderEntity> orderEnti, CriteriaQuery<OrderEntity> cq, CriteriaBuilder cb, String order){
        // Ordenar la consulta por fecha de creación
        if ("desc".equalsIgnoreCase(order)) {
            cq.orderBy(cb.desc(orderEnti.get("id")));
        } else {
            cq.orderBy(cb.asc(orderEnti.get("id")));
        }
    }

    private void addFilterDate(List<Predicate> predicates, Root<OrderEntity> orderEnti , String startDate, String endDate, CriteriaBuilder cb, String dateFilter){
        if (dateFilter.equalsIgnoreCase("orderDate") || dateFilter.equalsIgnoreCase("deliveryDate") || dateFilter.equalsIgnoreCase("shippingDate")){
            this.addFilterDateCase(predicates, orderEnti, startDate, endDate, cb, dateFilter);
        }
    }

    private void addFilterDateCase(List<Predicate> predicates, Root<OrderEntity> orderEnti , String startDate, String endDate, CriteriaBuilder cb, String dateFilter){
        // Parsear y aplicar filtro de startDate
        if (startDate != null && !startDate.isEmpty()) {
            LocalDate start = LocalDate.parse(startDate);  // Usar LocalDate para manejar solo fechas
            predicates.add(cb.greaterThanOrEqualTo(orderEnti.get(dateFilter).as(LocalDate.class), start));
        }

        // Parsear y aplicar filtro de endDate
        if (endDate != null && !endDate.isEmpty()) {
            LocalDate end = LocalDate.parse(endDate);
            predicates.add(cb.lessThanOrEqualTo(orderEnti.get(dateFilter).as(LocalDate.class), end));
        }
    }

    private void addFilterUser(List<Predicate> predicates, Root<OrderEntity> orderEnti, CriteriaBuilder cb, int userId){
        if (userId > 0) {
            // Unir con la entidad CartEntity
            Join<OrderEntity, CartEntity> cartJoin = orderEnti.join("cart");
            // Agregar el predicado para filtrar por userId
            predicates.add(cb.equal(cartJoin.get("userId"), userId));
        }
    }

    private void addFilterCartInit(List<Predicate> predicates, Root<OrderEntity> orderEnti, CriteriaBuilder cb, int cartIdInit){
        if (cartIdInit > 0) {
            predicates.add(cb.greaterThanOrEqualTo(orderEnti.get("id"), cartIdInit));
        }
    }

    public List<OrderResponseDto> getOrdersFilterWithParams(Integer userId, int size, String startDate, String endDate, String order, int cartIdInit, int processStatus, String dateFilter, int paymentMethod) {
        // Crear CriteriaBuilder y CriteriaQuery
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderEntity> cq = cb.createQuery(OrderEntity.class);
        Root<OrderEntity> orderEnti = cq.from(OrderEntity.class);

        // Crear lista de predicados para aplicar filtros
        List<Predicate> predicates = new ArrayList<>();

        //filtro de usuario
        this.addFilterUser(predicates,orderEnti,cb,userId);

        //filtro de fechas inicio y final
        this.addFilterDate(predicates,orderEnti,startDate,endDate,cb,dateFilter);

        // Aplicar filtro de un id de carrito para iniciar la consulta
        this.addFilterCartInit(predicates,orderEnti,cb,cartIdInit);

        // aplica el filtro de processstatus
        this.addFilterProcessSatatus(processStatus,predicates,orderEnti,cb);

        //aplica el filtro de paymethod del carrito
        this.addFilterPayMethod(paymentMethod,predicates,orderEnti,cb);

        // Aplicar los predicados a la consulta
        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        // aplica el orden especifico
        this.addFilterOrder(orderEnti,cq,cb,order);

        // Ejecutar la consulta con el tamaño máximo de resultados
        TypedQuery<OrderEntity> query = entityManager.createQuery(cq);
        query.setMaxResults(size);

        return query.getResultList().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

}
