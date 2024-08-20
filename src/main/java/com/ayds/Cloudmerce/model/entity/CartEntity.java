package com.ayds.Cloudmerce.model.entity;

import com.ayds.Cloudmerce.enums.DeliveryType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "cart", schema = "sales_control")
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
    private Long paymentMethodId;

    @Column(name = "status_id", nullable = false)
    private Integer statusId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Relación uno a muchos con CartItemEntity
    @OneToMany(mappedBy = "cart", fetch = FetchType.EAGER)
    private List<CartItemEntity> cartItems = new ArrayList<>();

    // Relación uno a uno con OrderEntity
    @OneToOne(mappedBy = "cart", fetch = FetchType.EAGER)
    @JsonManagedReference
    private OrderEntity order;
}
