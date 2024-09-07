package com.ayds.Cloudmerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayds.Cloudmerce.model.entity.UserPermissionEntity;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermissionEntity, Long> {

}
