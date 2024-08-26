package com.ayds.Cloudmerce.model.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {

    private Integer productId;
    private Integer quantity;
    private BigDecimal subTotal;
}
