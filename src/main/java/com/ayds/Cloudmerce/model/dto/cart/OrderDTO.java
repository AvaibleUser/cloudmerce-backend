package com.ayds.Cloudmerce.model.dto.cart;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Integer id;
    private Integer cartId;
    @NonNull @NotBlank
    private String deliveryAddress;
    @NonNull @Positive
    private BigDecimal deliveryCost;
    @NonNull @Positive
    private Integer statusId;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private LocalDateTime shippingDate;
}
