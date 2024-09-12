package com.ayds.Cloudmerce.model.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.ayds.Cloudmerce.enums.NotificationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_notification_status", schema = "warehouse_control")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProductNotificationEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    @NonNull
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @NonNull
    private Integer stock;

    @Column(name = "registration_date")
    @CreationTimestamp
    private Instant createdAt;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;
}
