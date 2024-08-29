package com.ayds.Cloudmerce.repository;

import com.ayds.Cloudmerce.model.entity.ProcessStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface ProcessStatusRepository extends JpaRepository<ProcessStatusEntity, Integer> {

    Optional<ProcessStatusEntity> findByStatus(String status);
}
