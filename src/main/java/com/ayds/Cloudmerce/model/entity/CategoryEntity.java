package com.ayds.Cloudmerce.model.entity;

import java.util.Set;

import jakarta.persistence.Column;
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
import lombok.Setter;

@Table
@Entity(name = "categories")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class CategoryEntity {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Column(length = 100)
    private String name;

    @NonNull
    @Setter
    private String description;

    @OneToMany(mappedBy = "category")
    private Set<ProductCategoryEntity> productCategories;
}
