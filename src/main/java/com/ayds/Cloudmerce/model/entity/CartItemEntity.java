package com.ayds.Cloudmerce.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(schema = "sales_control")
@Entity(name = "cart_item")
@Getter
@NoArgsConstructor
public class CartItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private Double subtotal;

    @Column(name = "cart_id", nullable = false)
    private Integer cartId;

    @Column(name = "product_id", nullable = false)
    private Integer productId;


}
