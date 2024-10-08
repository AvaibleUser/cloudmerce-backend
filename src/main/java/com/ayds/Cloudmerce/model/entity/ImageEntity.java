package com.ayds.Cloudmerce.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Table(name = "image_url", schema = "warehouse_control")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class ImageEntity {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Column(name = "image_url")
    private String url;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;
}
