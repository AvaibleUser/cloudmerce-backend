package com.ayds.Cloudmerce.service.report;

import com.ayds.Cloudmerce.model.dto.report.UserSalesDto;
import com.ayds.Cloudmerce.model.dto.report.UserSalesReportDto;
import com.ayds.Cloudmerce.model.entity.CartEntity;
import com.ayds.Cloudmerce.model.entity.UserEntity;
import com.ayds.Cloudmerce.repository.UserRepository;
import com.ayds.Cloudmerce.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSalesReportService {

    @PersistenceContext
    private EntityManager entityManager;

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserSalesReportDto getTopCustomersByPurchases(int size, String startDate, String endDate, String order) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<CartEntity> cart = cq.from(CartEntity.class);

        // Crear predicados para filtrar por fechas
        Predicate datePredicate = cb.between(cart.get("createdAt"),
                LocalDateTime.parse(startDate),
                LocalDateTime.parse(endDate)
        );

        // Selección y agrupación por userId, con count y suma del total gastado
        cq.multiselect(cart.get("userId"), cb.count(cart.get("id")), cb.sum(cart.get("total")))
                .where(datePredicate)
                .groupBy(cart.get("userId"))
                .orderBy(order.equalsIgnoreCase("desc")
                        ? cb.desc(cb.count(cart.get("id")))
                        : cb.asc(cb.count(cart.get("id"))));

        // Ejecutar consulta
        List<Object[]> users = entityManager.createQuery(cq).setMaxResults(size).getResultList();
        List<UserSalesDto> usersDto = users.stream().map(this::convertToDto).toList();
        return this.convertToReportDto(usersDto);
    }

    private UserSalesDto convertToDto(Object[] report){
        Long userId = (Long) report[0];
        UserEntity user = this.userRepository.findById((userId)).orElse(new UserEntity());
        return new UserSalesDto(
                user.getName(), user.getNit(), ((Number) report[1]).longValue(), (BigDecimal) report[2]
        );
    }

    private UserSalesReportDto convertToReportDto(List<UserSalesDto> users){
        BigDecimal totalSpent = BigDecimal.ZERO;
        Long totalPurchases = 0L;
        for (UserSalesDto user : users) {
            totalPurchases += user.totalPurchases();
            totalSpent = totalSpent.add(user.totalSpent());
        }
        return new UserSalesReportDto(users, totalSpent, totalPurchases);
    }
}
