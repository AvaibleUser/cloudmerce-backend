package com.ayds.Cloudmerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ayds.Cloudmerce.model.entity.UserEntity;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Integer> {

    UserEntity findByName(String username);

    boolean existsByName(String username);
}
