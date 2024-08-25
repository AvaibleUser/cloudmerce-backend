package com.ayds.Cloudmerce.repository;

import com.ayds.Cloudmerce.model.entity.UserPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPermissionRepository extends CrudRepository<UserPermissionEntity,Integer> {


}
