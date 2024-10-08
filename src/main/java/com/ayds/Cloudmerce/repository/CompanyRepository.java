package com.ayds.Cloudmerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayds.Cloudmerce.model.entity.CompanyEntity;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    // Obtener el primer registro por orden de ID ascendente
    CompanyEntity findTopByOrderByIdAsc();
}
