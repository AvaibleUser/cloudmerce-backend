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

    public PaymentMethodEntity getPaymentMethodById(Long id) {
        return this.paymentMethodRepository.findById(id).orElse(null);
    }

    private PaymentMethodDTO convertPaymentMethodDTO(PaymentMethodEntity paymentMethodEntity) {
        return new PaymentMethodDTO(paymentMethodEntity.getId(), paymentMethodEntity.getName());
    }

    public List<PaymentMethodEntity> getAllPaymentMethodsEntity() {
        return this.paymentMethodRepository.findAll();
    }

    public String paymentMethod(List<PaymentMethodEntity> lis, Long id) {
        for (PaymentMethodEntity paymentMethodEntity : lis) {
            if (paymentMethodEntity.getId().equals(id)) {
                return paymentMethodEntity.getName();
            }
        }
        return null;
    }
}
