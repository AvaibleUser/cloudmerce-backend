package com.ayds.Cloudmerce.repository;

import com.ayds.Cloudmerce.model.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity,Integer> {

    @Query(value = "SELECT * FROM role where id = ?", nativeQuery = true)
    RoleEntity finRoleById(Integer id);


}
