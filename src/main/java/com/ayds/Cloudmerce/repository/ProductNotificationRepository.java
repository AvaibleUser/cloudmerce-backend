package com.ayds.Cloudmerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayds.Cloudmerce.enums.NotificationStatus;
import com.ayds.Cloudmerce.model.entity.ProductNotificationEntity;

@Repository
public interface ProductNotificationRepository extends JpaRepository<ProductNotificationEntity, Long> {

    List<ProductNotificationEntity> findByStatus(NotificationStatus status);
}
