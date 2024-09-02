package com.ayds.Cloudmerce.model.dto.cart;

import com.ayds.Cloudmerce.enums.DeliveryType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDto {

    private Integer id;
    private BigDecimal total;
    private BigDecimal tax;
    private String paymentMethod;
    private String status;
    private OrderDTO order;
    private DeliveryType deliveryType;
    private Integer userId;
    private List<CartItemDTO> items;
    private LocalDateTime createdAt;
}
