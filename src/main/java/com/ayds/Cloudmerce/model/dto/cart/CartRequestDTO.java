package com.ayds.Cloudmerce.model.dto.cart;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartRequestDTO {
    @NonNull @Valid
    private CartDTO cart;
    @Valid
    private OrderDTO order;
}

