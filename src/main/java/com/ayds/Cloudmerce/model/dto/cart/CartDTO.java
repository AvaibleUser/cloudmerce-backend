package com.ayds.Cloudmerce.model.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private BigDecimal total;
    private BigDecimal tax;
    private Integer paymentMethodId;
    private Integer statusId;
    private Integer userId;
    private List<CartItemDTO> items;
}
