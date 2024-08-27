package com.ayds.Cloudmerce.model.entity;

import com.ayds.Cloudmerce.enums.DeliveryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(schema = "sales_control")
@Entity(name = "cart")
@Getter
@Setter
@NoArgsConstructor
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal tax;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type", nullable = false)
    private DeliveryType deliveryType;

    @Column(name = "payment_method_id", nullable = false)
    private Integer paymentMethodId;

    @Column(name = "status_id", nullable = false)
    private Integer statusId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
