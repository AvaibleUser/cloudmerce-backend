package com.ayds.Cloudmerce.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface RecentSaleDto {

    String getProduct();

    String getCustomer();

    BigDecimal getTotal();

    LocalDateTime getDate();
}
