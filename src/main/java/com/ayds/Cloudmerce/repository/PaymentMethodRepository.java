package com.ayds.Cloudmerce.repository;

import com.ayds.Cloudmerce.model.entity.PaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity,Integer> {
}
