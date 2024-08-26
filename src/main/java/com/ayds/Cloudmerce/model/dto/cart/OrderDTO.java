package com.ayds.Cloudmerce.model.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private String deliveryAddress;
    private BigDecimal shippingCost;
    private Integer statusId;
    private LocalDateTime orderDate;
}
