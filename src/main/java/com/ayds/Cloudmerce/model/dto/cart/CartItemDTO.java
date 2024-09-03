package com.ayds.Cloudmerce.model.dto.cart;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {

    private Integer id;
    @NonNull @Positive
    private Integer productId;
    @NonNull @Positive
    private Integer quantity;
    @NonNull @Positive
    private BigDecimal subTotal;
    private Integer cartId;
}
