package com.ayds.Cloudmerce.repository;

import com.ayds.Cloudmerce.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository  extends JpaRepository<UserEntity, Integer> {
}
