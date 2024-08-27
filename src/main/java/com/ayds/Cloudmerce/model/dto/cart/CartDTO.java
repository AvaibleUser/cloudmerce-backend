package com.ayds.Cloudmerce.model.dto.cart;

import com.ayds.Cloudmerce.enums.DeliveryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private Integer id;
    private BigDecimal total;
    private BigDecimal tax;
    private Integer paymentMethodId;
    private Integer statusId;
    private DeliveryType deliveryType;
    private Integer userId;
    private List<CartItemDTO> items;
    private LocalDateTime createdAt;
}
