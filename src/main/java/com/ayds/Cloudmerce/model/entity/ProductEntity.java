package com.ayds.Cloudmerce.model.entity;

import static com.ayds.Cloudmerce.enums.ProductState.VISIBLE;
import static jakarta.persistence.FetchType.EAGER;

import java.time.Instant;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.ayds.Cloudmerce.enums.ProductState;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product", schema = "warehouse_control")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProductEntity {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private String description;

    @NonNull
    private Float price;

    @NonNull
    @Column(name = "available_quantity")
    private Long stock;

    @NonNull
    @Enumerated(EnumType.STRING)
    private ProductState state = VISIBLE;

    @Column(name = "creation_at")
    @CreationTimestamp
    private Instant creationAt;

    @OneToMany(mappedBy = "product", fetch = EAGER)
    private Set<ProductCategoryEntity> productCategories;

    @OneToMany(mappedBy = "product", fetch = EAGER)
    private Set<ProductImageEntity> productImages;

    @OneToMany(mappedBy = "product")
    private Set<ProductNotificationEntity> productNotifications;
}
