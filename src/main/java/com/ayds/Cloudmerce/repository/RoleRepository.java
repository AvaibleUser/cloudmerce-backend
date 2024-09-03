package com.ayds.Cloudmerce.repository;

import com.ayds.Cloudmerce.model.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity,Integer> {

    @Query(value = "SELECT * FROM user_control.role where id = :id", nativeQuery = true)
    RoleEntity finRoleById(@Param("id") int id);


}
