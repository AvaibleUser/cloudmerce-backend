package com.ayds.Cloudmerce.service.report;

import com.ayds.Cloudmerce.model.dto.report.ProductDTO;
import com.ayds.Cloudmerce.model.dto.report.ProductReportDto;
import com.ayds.Cloudmerce.model.dto.report.UserSalesDto;
import com.ayds.Cloudmerce.model.dto.report.UserSalesReportDto;
import com.ayds.Cloudmerce.model.entity.ProductEntity;
import com.ayds.Cloudmerce.model.entity.UserEntity;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductReportService {

    @PersistenceContext
    private EntityManager entityManager;

    public ProductReportDto getProductStockReport(int size, String startDate, String endDate, String order,boolean stock) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        // Root para ProductEntity
        Root<ProductEntity> productRoot = cq.from(ProductEntity.class);

        // Convertir LocalDate a Instant para manejar las fechas correctamente
        Instant startDateInstant = LocalDate.parse(startDate).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endDateInstant = LocalDate.parse(endDate).atTime(23, 59, 59).atZone(ZoneOffset.UTC).toInstant();

        // Filtrar por rango de fechas usando creationAt
        Predicate datePredicate = cb.between(productRoot.get("creationAt"),
                startDateInstant,
                endDateInstant);

        if (stock) {
            // Seleccionar columnas necesarias
            cq.multiselect(
                            productRoot.get("name"),
                            productRoot.get("price"),
                            productRoot.get("stock"),
                            productRoot.get("creationAt"))
                    .where(datePredicate)
                    .orderBy(order.equalsIgnoreCase("desc") ? cb.desc(productRoot.get("stock")) : cb.asc(productRoot.get("stock")));

        }else{
            // Seleccionar columnas necesarias
            cq.multiselect(
                            productRoot.get("name"),
                            productRoot.get("price"),
                            productRoot.get("stock"),
                            productRoot.get("creationAt"))
                    .where(datePredicate)
                    .orderBy(order.equalsIgnoreCase("desc") ? cb.desc(productRoot.get("price")) : cb.asc(productRoot.get("price")));

        }
        // Ejecutar la consulta
        List<Object[]> results = entityManager.createQuery(cq)
                .setMaxResults(size)
                .getResultList();

        //personalizando el resultado..
        List<ProductDTO> productDTOS = results.stream().map(this::convertToDto).toList();
        return this.convertToReportDto(productDTOS);
    }

    private ProductDTO convertToDto(Object[] result) {
        // Convertir Instant a LocalDate
        LocalDate creationDate = ((Instant) result[3]).atZone(ZoneOffset.UTC).toLocalDate();
        return new ProductDTO(
                (String) result[0],
                (Float) result[1],
                (Long) result[2],
                creationDate.toString() // Solo la fecha
        );
    }


    private ProductReportDto convertToReportDto(List<ProductDTO> products){
        BigDecimal totalStock = BigDecimal.ZERO;
        for (ProductDTO product : products) {
            totalStock = totalStock.add(BigDecimal.valueOf(product.stock()));
        }
        LocalDate date = LocalDate.now();
        return new ProductReportDto(products, totalStock,date.toString());
    }
}
