package com.ayds.Cloudmerce.service;

import com.ayds.Cloudmerce.model.dto.cart.PaymentMethodDTO;
import com.ayds.Cloudmerce.model.dto.cart.ProcessStatusDTO;
import com.ayds.Cloudmerce.model.entity.PaymentMethodEntity;
import com.ayds.Cloudmerce.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public List<PaymentMethodDTO> getAllPaymentMethods() {
        return this.paymentMethodRepository.findAll().stream()
                .map(this::convertPaymentMethodDTO)
                .collect(Collectors.toList());
    }

    private PaymentMethodDTO convertPaymentMethodDTO(PaymentMethodEntity paymentMethodEntity) {
        return new PaymentMethodDTO(paymentMethodEntity.getId(), paymentMethodEntity.getMethodName());
    }
}
