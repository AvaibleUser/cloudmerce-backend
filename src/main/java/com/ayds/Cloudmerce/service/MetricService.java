package com.ayds.Cloudmerce.service;

import static com.ayds.Cloudmerce.enums.ProductState.VISIBLE;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ayds.Cloudmerce.repository.CartRepository;
import com.ayds.Cloudmerce.repository.OrderRepository;
import com.ayds.Cloudmerce.repository.ProductRepository;
import com.ayds.Cloudmerce.repository.UserRepository;

@Service
public class MetricService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public double findMonthSales() {
        return cartRepository.sumTotalSalesFrom(LocalDateTime.now()).orElse(0d);
    }

    public long findMonthOrders() {
        return orderRepository.countByOrderDateGreaterThan(LocalDateTime.now());
    }

    public long findTotalVisibleProducts() {
        return productRepository.countByState(VISIBLE);
    }

    public long findTotalCustomers() {
        return userRepository.countByRoleName("CLIENTE");
    }
}
