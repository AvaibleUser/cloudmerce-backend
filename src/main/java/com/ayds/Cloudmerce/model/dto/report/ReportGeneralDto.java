package com.ayds.Cloudmerce.model.dto.report;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReportGeneralDto {

    //ventas general
    private Long totalSales;
    private BigDecimal totalAmountSold;
    private Long totalUniqueUsers;
    private Long totalUniqueProducts;

    //apartado de orden
    private Long totalOrders;
    private BigDecimal totalAmountOrd;
    private BigDecimal totalShippingCost;
    private Long totalUniqueUsersOrders;


    //ventas sin orden
    private Long totalSalesNoOrder;
    private BigDecimal totalAmountSoldNoOrder;

    //Total
    private BigDecimal total;

    //otros
    private String dateReport;
    private String rangeDate;
}
