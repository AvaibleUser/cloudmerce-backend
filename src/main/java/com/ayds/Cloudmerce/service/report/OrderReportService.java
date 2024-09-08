package com.ayds.Cloudmerce.service.report;

import com.ayds.Cloudmerce.model.dto.cart.OrderResponseDto;
import com.ayds.Cloudmerce.model.dto.report.OrderDto;
import com.ayds.Cloudmerce.model.dto.report.OrderReportDto;
import com.ayds.Cloudmerce.model.entity.UserEntity;
import com.ayds.Cloudmerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderReportService {

    private final UserRepository userRepository;

    public OrderReportDto getOrderReport(List<OrderResponseDto> list) {
        List<OrderDto> orderDtos = new ArrayList<>();
        BigDecimal totalSpent = BigDecimal.ZERO;

        for (OrderResponseDto order : list) {
            totalSpent = totalSpent.add(order.getCart().getTotal());
            Long userId = order.getCart().getUserId();
            UserEntity user = this.userRepository.findById(userId).orElse(new UserEntity());

            String orderDate = (order.getOrderDate() != null && order.getOrderDate().toLocalDate() != null)
                    ? order.getOrderDate().toLocalDate().toString()
                    : "---";

            String deliveryDate = (order.getDeliveryDate() != null && order.getDeliveryDate().toLocalDate() != null)
                    ? order.getDeliveryDate().toLocalDate().toString()
                    : "---";

            String shippingDate = (order.getShippingDate() != null && order.getShippingDate().toLocalDate() != null)
                    ? order.getShippingDate().toLocalDate().toString()
                    : "---";

            orderDtos.add(new OrderDto(user.getName(), order.getCart().getTotal(), orderDate, deliveryDate, shippingDate));
        }

        LocalDate date = LocalDate.now();
        return new OrderReportDto(orderDtos, totalSpent, date.toString());
    }

}
