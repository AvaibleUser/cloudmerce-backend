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
public class OrderResponseDto {
    private Integer id;
    private CartDTO cart;
    private String deliveryAddress;
    private BigDecimal deliveryCost;
    private Integer statusId;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private LocalDateTime shippingDate;
}
