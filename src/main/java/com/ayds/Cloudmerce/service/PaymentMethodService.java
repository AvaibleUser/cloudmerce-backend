package com.ayds.Cloudmerce.service;

import com.ayds.Cloudmerce.model.entity.PaymentMethodEntity;
import com.ayds.Cloudmerce.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;


    public PaymentMethodEntity finById(Integer id){
        return paymentMethodRepository.findById(id).get();
    }



}
