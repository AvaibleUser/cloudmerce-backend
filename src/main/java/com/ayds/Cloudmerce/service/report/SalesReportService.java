package com.ayds.Cloudmerce.service.report;

import com.ayds.Cloudmerce.model.dto.report.ReportGeneralDto;
import com.ayds.Cloudmerce.model.entity.CartEntity;
import com.ayds.Cloudmerce.model.entity.CartItemEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class SalesReportService {

    @PersistenceContext
    private EntityManager entityManager;

    public void getTotalSales(String startDate, String endDate, ReportGeneralDto reportGeneralDto) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<CartEntity> cartRoot = cq.from(CartEntity.class);

        // Filtrar por rango de fechas usando createdAt
        Predicate datePredicate = cb.between(cartRoot.get("createdAt"), LocalDate.parse(startDate), LocalDate.parse(endDate));

        // Seleccionar el total de ventas (conteo de registros) y la suma de total vendido (suma de dinero)
        cq.multiselect(
                cb.count(cartRoot.get("id")),       // Conteo de registros
                cb.sum(cartRoot.get("total")),     // Suma del campo 'total'
                cb.countDistinct(cartRoot.get("userId"))  // Conteo de usuarios únicos
        ).where(datePredicate);

        // Ejecutar la consulta y obtener los resultados
        Object[] result = entityManager.createQuery(cq).getSingleResult();

        // Mapear los resultados a un DTO personalizado
        Long totalSales = (Long) result[0];
        BigDecimal totalAmountSold = (BigDecimal) result[1];
        Long totalUniqueUsers = (Long) result[2];

        //agregar al reportdto
        reportGeneralDto.setTotalSales(totalSales);
        reportGeneralDto.setTotalAmountSold(totalAmountSold);
        reportGeneralDto.setTotalUniqueUsers(totalUniqueUsers);

    }

    public void getTotalUniqueProductsSold(String startDate, String endDate, ReportGeneralDto reportGeneralDto) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        // Root para CartItemEntity
        Root<CartItemEntity> cartItemRoot = cq.from(CartItemEntity.class);

        // Filtrar por rango de fechas en base al createdAt del carrito o los items
        Predicate datePredicate = cb.between(cartItemRoot.get("cart").get("createdAt"), LocalDate.parse(startDate), LocalDate.parse(endDate));

        // Seleccionar el total de productos únicos vendidos (contar productos sin repetir)
        cq.select(cb.countDistinct(cartItemRoot.get("productId"))).where(datePredicate);

        // Ejecutar la consulta y obtener el resultado
        Long totalUniqueProducts = entityManager.createQuery(cq).getSingleResult();

        reportGeneralDto.setTotalUniqueProducts(totalUniqueProducts);

    }

    public void getOrderReport(String startDate, String endDate, ReportGeneralDto reportGeneralDto) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        // Root para CartEntity
        Root<CartEntity> cartRoot = cq.from(CartEntity.class);

        // Filtrar por rango de fechas en base al createdAt del carrito
        Predicate datePredicate = cb.between(cartRoot.get("createdAt"), LocalDate.parse(startDate), LocalDate.parse(endDate));

        // Filtrar para carritos que tengan una orden asociada
        Predicate hasOrderPredicate = cb.isNotNull(cartRoot.get("order"));

        // Seleccionar columnas necesarias: total de órdenes, suma de dinero, costo de envío y usuarios únicos
        cq.multiselect(
                cb.count(cartRoot.get("id")),                       // Total de órdenes
                cb.sum(cartRoot.get("total")),                      // Suma total del valor de los carritos con orden
                cb.sum(cartRoot.get("order").get("deliveryCost")),   // Suma de costos de envío (deliveryCost)
                cb.countDistinct(cartRoot.get("userId"))            // Total de usuarios únicos con órdenes (userId en lugar de user)
        ).where(cb.and(datePredicate, hasOrderPredicate));

        // Ejecutar la consulta
        Object[] result = entityManager.createQuery(cq).getSingleResult();

        reportGeneralDto.setTotalOrders((Long) result[0]);
        reportGeneralDto.setTotalAmountOrd((BigDecimal) result[1]);
        reportGeneralDto.setTotalShippingCost((BigDecimal) result[2]);
        reportGeneralDto.setTotalUniqueUsersOrders((Long) result[3]);
    }

}
