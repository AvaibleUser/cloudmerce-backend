package com.ayds.Cloudmerce.model.dto.report;



import java.math.BigDecimal;

public record UserSalesDto(String name,
        String nit,
        Long totalPurchases,
        BigDecimal totalSpent){

}
