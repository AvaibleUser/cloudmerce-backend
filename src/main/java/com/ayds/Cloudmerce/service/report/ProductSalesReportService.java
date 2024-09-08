package com.ayds.Cloudmerce.service.report;

import com.ayds.Cloudmerce.model.dto.report.ProductSalesDto;
import com.ayds.Cloudmerce.model.dto.report.ProductSalesReportDto;
import com.ayds.Cloudmerce.model.dto.report.UserSalesDto;
import com.ayds.Cloudmerce.model.dto.report.UserSalesReportDto;
import com.ayds.Cloudmerce.model.entity.CartEntity;
import com.ayds.Cloudmerce.model.entity.CartItemEntity;
import com.ayds.Cloudmerce.model.entity.ProductEntity;
import com.ayds.Cloudmerce.model.entity.UserEntity;
import com.ayds.Cloudmerce.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSalesReportService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ProductRepository productRepository;

    private void addFilterProcessStatus(int processStatus, List<Predicate> predicates, Join<CartItemEntity, CartEntity> cart,  CriteriaBuilder cb){
        if (processStatus > 0){
            predicates.add(cb.equal(cart.get("statusId"), processStatus));
        }
    }

    private void addFilterPayMethod(int paymentMethod, List<Predicate> predicates, Join<CartItemEntity, CartEntity>cart,  CriteriaBuilder cb){
        if (paymentMethod > 0){
            predicates.add(cb.equal(cart.get("paymentMethodId"), paymentMethod));
        }
    }

    private void addFilterDate(List<Predicate> predicates, Join<CartItemEntity, CartEntity> cart , String startDate, String endDate, CriteriaBuilder cb ){
        predicates.add(cb.between(cart.get("createdAt"), LocalDate.parse(startDate), LocalDate.parse(endDate)));
    }


    @Transactional(readOnly = true)
    public ProductSalesReportDto getProductSalesReport(int size, String startDate, String endDate, String order, int processStatus, int paymentMethod) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<CartItemEntity> cartItem = cq.from(CartItemEntity.class);
        // Join con CartEntity
        Join<CartItemEntity, CartEntity> cart = cartItem.join("cart");
        List<Predicate> predicates = new ArrayList<>();

        //aplicando un rango/periodo de tiempo
        this.addFilterDate(predicates,cart,startDate,endDate,cb);

        //aplicando filtros para metodo de pago
        this.addFilterPayMethod(paymentMethod,predicates,cart,cb);

        //aplicando filtors de process estatus
        this.addFilterProcessStatus(processStatus,predicates,cart,cb);

        // Agrupar por productId y sumar cantidad y subtotal
        cq.multiselect(cartItem.get("productId"),
                        cb.sum(cartItem.get("quantity")),
                        cb.sum(cartItem.get("subtotal")))
                .where(cb.and(predicates.toArray(new Predicate[0])))
                .groupBy(cartItem.get("productId"))
                .orderBy(order.equalsIgnoreCase("desc") ? cb.desc(cb.sum(cartItem.get("subtotal"))) : cb.asc(cb.sum(cartItem.get("subtotal"))));

        // Ejecutar consulta
        List<Object[]> products = entityManager.createQuery(cq).setMaxResults(size).getResultList();

        //personalizando el resultado..
        List<ProductSalesDto> productSalesDtos = products.stream().map(this::convertToDto).toList();
        return this.convertToReportDto(productSalesDtos);
    }

    private ProductSalesDto convertToDto(Object[] report){
        Long productId = ((Integer) report[0]).longValue();
        ProductEntity productEntity = this.productRepository.findById(productId).orElse(new ProductEntity());
        return new ProductSalesDto(
                productEntity.getName(), productEntity.getPrice(), ((Number) report[1]).longValue(), (BigDecimal) report[2]
        );
    }

    private ProductSalesReportDto convertToReportDto(List<ProductSalesDto> products){
        BigDecimal totalSpent = BigDecimal.ZERO;
        Long totalPurchases = 0L;
        for (ProductSalesDto product : products) {
            totalPurchases += product.totalPurchases();
            totalSpent = totalSpent.add(product.totalSpent());
        }
        LocalDate date = LocalDate.now();
        return new ProductSalesReportDto(products, totalSpent, totalPurchases,date.toString());
    }
}
