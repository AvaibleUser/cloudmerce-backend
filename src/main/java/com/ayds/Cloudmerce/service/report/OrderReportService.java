package com.ayds.Cloudmerce.service.report;

import com.ayds.Cloudmerce.model.dto.cart.OrderResponseDto;
import com.ayds.Cloudmerce.model.dto.report.*;
import com.ayds.Cloudmerce.model.entity.CartEntity;
import com.ayds.Cloudmerce.model.entity.OrderEntity;
import com.ayds.Cloudmerce.model.entity.UserEntity;
import com.ayds.Cloudmerce.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderReportService {

    @PersistenceContext
    private EntityManager entityManager;

    private final UserRepository userRepository;

    public OrderReportDto getOrderReport(List<OrderResponseDto> list) {
        List<OrderDto> orderDtos = new ArrayList<>();
        BigDecimal totalSpent = BigDecimal.ZERO;

        for (OrderResponseDto order : list) {
            totalSpent = totalSpent.add(order.getCart().getTotal());
            Long userId = order.getCart().getUserId();
            UserEntity user = this.userRepository.findById(userId).orElse(new UserEntity());

            String orderDate = (order.getOrderDate() != null && order.getOrderDate().toLocalDate() != null)
                    ? order.getOrderDate().toLocalDate().toString()
                    : "---";

            String deliveryDate = (order.getDeliveryDate() != null && order.getDeliveryDate().toLocalDate() != null)
                    ? order.getDeliveryDate().toLocalDate().toString()
                    : "---";

            String shippingDate = (order.getShippingDate() != null && order.getShippingDate().toLocalDate() != null)
                    ? order.getShippingDate().toLocalDate().toString()
                    : "---";

            orderDtos.add(new OrderDto(user.getName(), order.getCart().getTotal(), orderDate, deliveryDate, shippingDate));
        }

        LocalDate date = LocalDate.now();
        return new OrderReportDto(orderDtos, totalSpent, date.toString());
    }

    @Transactional(readOnly = true)
    public UserOrderReportDto getUserOrdersReport(int size, String startDate, String endDate, String order, int processStatus, int paymentMethod) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        // Root para OrderEntity
        Root<OrderEntity> orderRoot = cq.from(OrderEntity.class);

        // Join con CartEntity
        Join<OrderEntity, CartEntity> cartJoin = orderRoot.join("cart");

        List<Predicate> predicates = new ArrayList<>();

        //aplicando un rango/periodo de tiempo
        this.addFilterDate(predicates,cartJoin,startDate,endDate,cb);

        //aplicando filtros para metodo de pago
        this.addFilterPayMethod(paymentMethod,predicates,cartJoin,cb);

        //aplicando filtors de process estatus
        this.addFilterProcessStatus(processStatus,predicates,cartJoin,cb);

        // Agrupar por usuario (userId) y sumar cantidad de órdenes, total de órdenes, y deliveryCost
        cq.multiselect(cartJoin.get("userId"),
                        cb.count(orderRoot.get("id")),  // Contar las órdenes
                        cb.sum(cartJoin.get("total")),  // Sumar el total de los carritos
                        cb.sum(orderRoot.get("deliveryCost")))  // Sumar los costos de entrega
                .where(cb.and(predicates.toArray(new Predicate[0])))
                .groupBy(cartJoin.get("userId"))
                .orderBy(order.equalsIgnoreCase("desc") ? cb.desc(cb.count(orderRoot.get("id"))) : cb.asc(cb.count(orderRoot.get("id"))));

        // Ejecutar la consulta
        List<Object[]> results = entityManager.createQuery(cq).setMaxResults(size).getResultList();

        //personalizando el resultado..
        List<UserOrderDto> usersDto = results.stream().map(this::convertToDto).toList();
        return this.convertToReportDto(usersDto);
    }

    private void addFilterProcessStatus(int processStatus, List<Predicate> predicates, Join<OrderEntity, CartEntity> cart,  CriteriaBuilder cb){
        if (processStatus > 0){
            predicates.add(cb.equal(cart.get("statusId"), processStatus));
        }
    }

    private void addFilterPayMethod(int paymentMethod, List<Predicate> predicates, Join<OrderEntity, CartEntity> cart,  CriteriaBuilder cb){
        if (paymentMethod > 0){
            predicates.add(cb.equal(cart.get("paymentMethodId"), paymentMethod));
        }
    }

    private void addFilterDate(List<Predicate> predicates, Join<OrderEntity, CartEntity> cart, String startDate, String endDate, CriteriaBuilder cb ){
        predicates.add(cb.between(cart.get("createdAt"), LocalDate.parse(startDate), LocalDate.parse(endDate)));
    }

    private UserOrderDto convertToDto(Object[] report){
        Long userId = (Long) report[0];
        UserEntity user = this.userRepository.findById((userId)).orElse(new UserEntity());
        return new UserOrderDto(
                user.getName(), user.getNit(), ((Number) report[1]).longValue(), (BigDecimal) report[2], (BigDecimal) report[3]
        );
    }

    private UserOrderReportDto convertToReportDto(List<UserOrderDto> users){
        BigDecimal totalSpent = BigDecimal.ZERO;
        Long totalPurchases = 0L;
        BigDecimal totalCostDelivery = BigDecimal.ZERO;
        for (UserOrderDto user : users) {
            totalPurchases += user.totalPurchases();
            totalSpent = totalSpent.add(user.totalSpent());
            totalCostDelivery = totalCostDelivery.add(user.totalCostDelivery());

        }
        LocalDate date = LocalDate.now();
        return new UserOrderReportDto(users, totalSpent, totalCostDelivery, totalPurchases,date.toString());
    }

}
