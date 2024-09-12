package com.ayds.Cloudmerce.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ayds.Cloudmerce.model.dto.ChartDataDto;
import com.ayds.Cloudmerce.model.entity.CartEntity;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Integer> {
    Optional<List<CartEntity>> findAllByUserId(Integer userId);

    @Query("SELECT SUM(c.total) FROM cart c INNER JOIN c.order o WHERE o.orderDate > :fromMonth AND c.statusId = 5")
    Optional<Double> sumTotalSalesFrom(@Param("fromMonth") LocalDateTime fromMonth);

    @Query("""
            SELECT TO_CHAR(o.orderDate, 'Month') AS name, SUM(c.total) AS value FROM cart c
                INNER JOIN c.order o
                WHERE o.orderDate > :fromMonth
                GROUP BY TO_CHAR(o.orderDate, 'Month')
            """)
    List<ChartDataDto> findSalesOverviewFrom(@Param("fromMonth") LocalDateTime fromMonth);
}
