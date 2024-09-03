package com.ayds.Cloudmerce.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.Cloudmerce.model.dto.CategoryUpdateDto;
import com.ayds.Cloudmerce.model.dto.ProductNotificationDto;
import com.ayds.Cloudmerce.service.ProductNotificationService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/products/notifications")
public class ProductNotificationController {

    @Autowired
    private ProductNotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<ProductNotificationDto>> getAllNotifications(
            @RequestParam(name = "unreaded", required = false, defaultValue = "false") boolean unreaded) {
        List<ProductNotificationDto> notifications = notificationService.findAllNotifications(unreaded);

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<ProductNotificationDto> getAllNotifications(
            @PathVariable @Positive long notificationId) {
        Optional<ProductNotificationDto> notification = notificationService.findNotification(notificationId);

        return ResponseEntity.of(notification);
    }

    @PatchMapping("/{notificationId}")
    public ResponseEntity<ProductNotificationDto> patchNotificationStatus(@PathVariable @Positive long categoryId,
            @RequestBody @Valid CategoryUpdateDto category) {
        ProductNotificationDto updatedCategory = notificationService.updateNotificationStatusToRead(categoryId);

        return ResponseEntity.ok(updatedCategory);
    }
}
