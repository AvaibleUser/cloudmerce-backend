package com.ayds.Cloudmerce.model.dto.report;

import java.math.BigDecimal;

public record OrderDto(
        String user,
        BigDecimal total,
        String orderDate,
        String deliveryDate,
        String shippingDate
) {

}
