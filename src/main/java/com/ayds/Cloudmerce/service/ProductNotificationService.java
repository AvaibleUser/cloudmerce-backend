package com.ayds.Cloudmerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayds.Cloudmerce.enums.NotificationStatus;
import com.ayds.Cloudmerce.model.dto.ProductNotificationDto;
import com.ayds.Cloudmerce.model.entity.ProductNotificationEntity;
import com.ayds.Cloudmerce.repository.ProductNotificationRepository;

@Service
public class ProductNotificationService {

    @Autowired
    private ProductNotificationRepository notificationRepository;

    private static ProductNotificationDto toNotificationDto(ProductNotificationEntity notification) {
        return new ProductNotificationDto(notification.getId(), notification.getStatus(), notification.getDescription(),
                notification.getCreatedAt(), notification.getProduct());
    }

    public List<ProductNotificationDto> findAllNotifications(boolean unread) {
        List<ProductNotificationEntity> notifications = unread
                ? notificationRepository.findByStatus(NotificationStatus.UNREAD)
                : notificationRepository.findAll();

        return notifications.stream()
                .map(ProductNotificationService::toNotificationDto)
                .toList();
    }

    public Optional<ProductNotificationDto> findNotification(long notificationId) {
        return notificationRepository.findById(notificationId)
                .map(ProductNotificationService::toNotificationDto);
    }

    @Transactional
    public ProductNotificationDto updateNotificationStatusToRead(long notificationId) {
        ProductNotificationEntity notification = notificationRepository.findById(notificationId).get();
        notification.setStatus(NotificationStatus.READ);
        return toNotificationDto(notificationRepository.save(notification));
    }
}
