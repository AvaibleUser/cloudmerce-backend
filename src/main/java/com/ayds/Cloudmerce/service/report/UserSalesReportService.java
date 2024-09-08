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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSalesReportService {

    @PersistenceContext
    private EntityManager entityManager;

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserSalesReportDto getTopCustomersByPurchases(int size, String startDate, String endDate, String order, int processStatus, int paymentMethod) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<CartEntity> cart = cq.from(CartEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        //aplicando un rango/periodo de tiempo
        this.addFilterDate(predicates,cart,startDate,endDate,cb);

        //aplicando filtros para metodo de pago
        this.addFilterPayMethod(paymentMethod,predicates,cart,cb);

        //aplicando filtors de process estatus
        this.addFilterProcessSatatus(processStatus,predicates,cart,cb);

        // Selección y agrupación por userId, con count y suma del total gastado
        cq.multiselect(cart.get("userId"), cb.count(cart.get("id")), cb.sum(cart.get("total")))
                .where(cb.and(predicates.toArray(new Predicate[0])))
                .groupBy(cart.get("userId"))
                .orderBy(order.equalsIgnoreCase("desc")
                        ? cb.desc(cb.sum(cart.get("id")))
                        : cb.asc(cb.sum(cart.get("id"))));

        // Ejecutar consulta
        List<Object[]> users = entityManager.createQuery(cq).setMaxResults(size).getResultList();

        //personalizando el resultado..
        List<UserSalesDto> usersDto = users.stream().map(this::convertToDto).toList();
        return this.convertToReportDto(usersDto);
    }

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

    private void addFilterDate(List<Predicate> predicates, Root<CartEntity> cart , String startDate, String endDate, CriteriaBuilder cb ){
        predicates.add(cb.between(cart.get("createdAt"), LocalDate.parse(startDate), LocalDate.parse(endDate)));
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
        LocalDate date = LocalDate.now();
        return new UserSalesReportDto(users, totalSpent, totalPurchases,date.toString());
    }
}
