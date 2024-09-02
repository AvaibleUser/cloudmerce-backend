package com.ayds.Cloudmerce.service;

import com.ayds.Cloudmerce.model.dto.cart.*;
import com.ayds.Cloudmerce.model.entity.CartEntity;
import com.ayds.Cloudmerce.model.entity.OrderEntity;
import com.ayds.Cloudmerce.model.entity.PaymentMethodEntity;
import com.ayds.Cloudmerce.model.entity.ProcessStatusEntity;
import com.ayds.Cloudmerce.repository.CartRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
public class CartService {

    @PersistenceContext
    private EntityManager entityManager;

    private final CartRepository cartRepository;
    private final CartItemService cartItemService;
    private final OrderService orderService;
    private final ProcessStatusService processStatusService;
    private final PaymentMethodService paymentMethodService;

    public List<CartResponseDto> getAllCartsWithItems() {
        return cartRepository.findAll().stream()
                .map(this::convertToCartDtoResponse)
                .collect(Collectors.toList());
    }

    public CartEntity existCart(Integer idCart){
        return this.cartRepository.findById(idCart).orElse(null);
    }

    public CartResponseDto getCartById(Integer idCart) {
        CartEntity cartEntity = this.cartRepository.findById(idCart).orElse(null);
        if (cartEntity == null) {
            return new CartResponseDto();
        }
        return this.convertToCartDtoResponse(cartEntity);
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

    /**
     * esta funcion es especificamente usada cuando se actulize la orden
     * @param orderUpdate
     * @param processStatus
     */
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
        if (!cartEntity.getCartItems().isEmpty()){
            List<CartItemDTO> listDTO = cartEntity.getCartItems().stream()
                    .map(this.cartItemService::convertToCartItemDTO)
                    .collect(Collectors.toList());
            cartDTO.setItems(listDTO);
        }
        return cartDTO;
    }

    private CartResponseDto convertToCartDtoResponse(CartEntity cartEntity) {
        CartResponseDto cartDTO = new CartResponseDto();
        List<ProcessStatusEntity> listProcess = this.processStatusService.getAllProcessStatusEntity();
        List<PaymentMethodEntity> listPayment = this.paymentMethodService.getAllPaymentMethodsEntity();
        cartDTO.setId(cartEntity.getId());
        cartDTO.setTax(cartEntity.getTax());
        cartDTO.setTotal(cartEntity.getTotal());
        cartDTO.setDeliveryType(cartEntity.getDeliveryType());
        cartDTO.setUserId(cartEntity.getUserId());
        cartDTO.setStatus(this.processStatusService.processStatus(listProcess,cartEntity.getStatusId()));
        cartDTO.setPaymentMethod(this.paymentMethodService.paymentMethod(listPayment,cartEntity.getPaymentMethodId()));
        cartDTO.setCreatedAt(cartEntity.getCreatedAt());
        if (!cartEntity.getCartItems().isEmpty()){
            List<CartItemDTO> listDTO = cartEntity.getCartItems().stream()
                    .map(this.cartItemService::convertToCartItemDTO)
                    .collect(Collectors.toList());
            cartDTO.setItems(listDTO);
        }
        return cartDTO;
    }

    //apartado de busqueda por usuario + otros parametros

    public List<CartEntity> getAllCartsUser(Integer userId){
        return this.cartRepository.findAllByUserId(userId).orElse(List.of());
    }

    /*
    * apartado para funciones para agregar filtros a la peticiones
     */
    private void addFilterProcessSatatus(int processStatus, List<Predicate> predicates, Root<CartEntity> cart,  CriteriaBuilder cb){
        if (processStatus > 0){
            predicates.add(cb.equal(cart.get("statusId"), processStatus));
        }
    }

    private void addFilterPayMethod(int paymentMethod, List<Predicate> predicates, Root<CartEntity> cart,  CriteriaBuilder cb){
        if (paymentMethod > 0){
            predicates.add(cb.equal(cart.get("paymentMethodId"), paymentMethod));
        }
    }

    private void addFilterOrder(Root<CartEntity> cart, CriteriaQuery<CartEntity> cq, CriteriaBuilder cb, String order){
        // Ordenar la consulta por fecha de creación
        if ("desc".equalsIgnoreCase(order)) {
            cq.orderBy(cb.desc(cart.get("createdAt")));
        } else {
            cq.orderBy(cb.asc(cart.get("createdAt")));
        }
    }

    private void addFilterDate(List<Predicate> predicates, Root<CartEntity> cart , String startDate, String endDate, CriteriaBuilder cb ){
        // Parsear y aplicar filtro de startDate
        if (startDate != null && !startDate.isEmpty()) {
            LocalDate start = LocalDate.parse(startDate);  // Usar LocalDate para manejar solo fechas
            predicates.add(cb.greaterThanOrEqualTo(cart.get("createdAt").as(LocalDate.class), start));
        }

        // Parsear y aplicar filtro de endDate
        if (endDate != null && !endDate.isEmpty()) {
            LocalDate end = LocalDate.parse(endDate);
            predicates.add(cb.lessThanOrEqualTo(cart.get("createdAt").as(LocalDate.class), end));
        }
    }

    private void addFilterUser(List<Predicate> predicates, Root<CartEntity> cart, CriteriaBuilder cb, int userId){
        if (userId > 0) predicates.add(cb.equal(cart.get("userId"), userId));
    }

    private void addFilterCartInit(List<Predicate> predicates, Root<CartEntity> cart, CriteriaBuilder cb, int cartIdInit){
        if (cartIdInit > 0) {
            predicates.add(cb.greaterThanOrEqualTo(cart.get("id"), cartIdInit));
        }
    }

    /**
     *  funcion para obtener los carritos de un usuario en especifico, con parametros
     * @param userId el id del usuario
     * @param size para el maximo de devolucions
     * @param startDate fecha del inicio para el filtro
     * @param endDate fecha de final para el rango
     * @param order asc o desc
     * @param cartIdInit si se quiere iniciar en un carrito como punto de partida
     * @return
     */
    public List<CartResponseDto> getCartsFilterWithParams(Integer userId, int size, String startDate, String endDate, String order, int cartIdInit, int processStatus, int paymentMethod) {
        // Crear CriteriaBuilder y CriteriaQuery
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CartEntity> cq = cb.createQuery(CartEntity.class);
        Root<CartEntity> cart = cq.from(CartEntity.class);

        // Crear lista de predicados para aplicar filtros
        List<Predicate> predicates = new ArrayList<>();

        //filtro de usuario
        this.addFilterUser(predicates,cart,cb,userId);

        //filtro de fechas inicio y final
        this.addFilterDate(predicates,cart,startDate,endDate,cb);

        // Aplicar filtro de un id de carrito para iniciar la consulta
        this.addFilterCartInit(predicates,cart,cb,cartIdInit);

        // aplica el filtro de paymethod
        this.addFilterPayMethod(paymentMethod,predicates,cart,cb);

        // aplica el filtro de processstatus
        this.addFilterProcessSatatus(processStatus,predicates,cart,cb);

        // Aplicar los predicados a la consulta
        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        // aplica el orden especifico
        this.addFilterOrder(cart,cq,cb,order);

        // Ejecutar la consulta con el tamaño máximo de resultados
        TypedQuery<CartEntity> query = entityManager.createQuery(cq);
        query.setMaxResults(size);

        return query.getResultList().stream()
                .map(this::convertToCartDtoResponse)
                .collect(Collectors.toList());
    }

}
