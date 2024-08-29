package com.ayds.Cloudmerce.model.entity;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "image_url"/*, schema = "warehouse_control"*/)
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class ImageEntity {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String url;

    @OneToMany(mappedBy = "image")
    private Set<ProductImageEntity> productImages;
}
