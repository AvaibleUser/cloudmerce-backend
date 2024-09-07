package com.ayds.Cloudmerce.model.dto.cart;

import com.ayds.Cloudmerce.enums.DeliveryType;
import jakarta.validation.constraints.NotBlank;
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
public class CartDTO {

    private Integer id;
    @NonNull @Positive
    private BigDecimal total;
    @NonNull @Positive
    private BigDecimal tax;
    @NonNull @Positive
    private Long paymentMethodId;
    @NonNull @Positive
    private Integer statusId;
    @NonNull
    private DeliveryType deliveryType;
    @NonNull @Positive
    private Long userId;
    @NotEmpty
    private List<CartItemDTO> items;
    private LocalDateTime createdAt;
}
