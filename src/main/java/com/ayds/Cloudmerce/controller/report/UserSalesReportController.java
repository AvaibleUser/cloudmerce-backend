package com.ayds.Cloudmerce.controller.report;

import com.ayds.Cloudmerce.model.dto.report.UserSalesReportDto;
import com.ayds.Cloudmerce.service.CartService;
import com.ayds.Cloudmerce.service.report.UserSalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports/users")
public class UserSalesReportController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserSalesReportService userSalesReportService;

    @GetMapping("/more")
    public UserSalesReportDto getReportUserMoreShopping(@RequestParam(value = "size", defaultValue = "12") int size,
                                                                    @RequestParam(value = "startDate", defaultValue = "2000-01-01T00:00:00") String startDate,
                                                                    @RequestParam(value = "endDate", defaultValue = "2099-12-31T23:59:59") String endDate,
                                                                    @RequestParam(value = "order", defaultValue = "desc") String order) {

        UserSalesReportDto report = this.userSalesReportService.getTopCustomersByPurchases(size, startDate, endDate, order);
        return report;
    }

}
