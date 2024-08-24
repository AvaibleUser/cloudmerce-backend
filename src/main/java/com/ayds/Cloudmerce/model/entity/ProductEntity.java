package com.ayds.Cloudmerce.model.entity;

import static com.ayds.Cloudmerce.enums.ProductState.VISIBLE;

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

@Table
@Entity(name = "products")
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
    private Long stock;

    @NonNull
    @Enumerated(EnumType.STRING)
    private ProductState state = VISIBLE;

    @Column(name = "creation_at")
    @CreationTimestamp
    private Instant creationAt;

    @OneToMany(mappedBy = "product")
    private Set<ProductCategoryEntity> productCategories;

    @OneToMany(mappedBy = "product")
    private Set<ProductImageEntity> productImages;
}
