package com.ayds.Cloudmerce.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ayds.Cloudmerce.model.dto.cart.PaymentMethodDTO;
import com.ayds.Cloudmerce.model.entity.PaymentMethodEntity;
import com.ayds.Cloudmerce.repository.PaymentMethodRepository;

@Service
public class PaymentMethodService {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    public Optional<PaymentMethodEntity> finById(long id) {
        return paymentMethodRepository.findById(id);
    }

    public List<PaymentMethodDTO> getAllPaymentMethods() {
        return this.paymentMethodRepository.findAll().stream()
                .map(this::convertPaymentMethodDTO)
                .collect(Collectors.toList());
    }

    private PaymentMethodDTO convertPaymentMethodDTO(PaymentMethodEntity paymentMethodEntity) {
        return new PaymentMethodDTO(paymentMethodEntity.getId(), paymentMethodEntity.getName());
    }
}
