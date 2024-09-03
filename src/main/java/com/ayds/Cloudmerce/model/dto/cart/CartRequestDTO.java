package com.ayds.Cloudmerce.model.dto.cart;

import jakarta.validation.Valid;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartRequestDTO {
    @NonNull @Valid
    private CartDTO cart;
    @Valid
    private OrderDTO order;
}

