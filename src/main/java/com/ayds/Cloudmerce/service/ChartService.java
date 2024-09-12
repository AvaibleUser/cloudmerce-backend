package com.ayds.Cloudmerce.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ayds.Cloudmerce.model.dto.ChartDataDto;
import com.ayds.Cloudmerce.repository.CartRepository;

@Service
public class ChartService {

    @Autowired
    private CartRepository cartRepository;

    public List<ChartDataDto> getLastSixMonthsSalesOverviewChartData() {
        return cartRepository.findSalesOverviewFrom(LocalDateTime.now().minusMonths(6));
    }
}
