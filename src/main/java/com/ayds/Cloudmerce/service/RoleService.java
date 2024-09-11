package com.ayds.Cloudmerce.service;

import java.util.List;
import java.util.Optional;

import com.ayds.Cloudmerce.model.dto.callaborator.RoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ayds.Cloudmerce.model.entity.RoleEntity;
import com.ayds.Cloudmerce.repository.RoleRepository;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Optional<RoleEntity> findRoleById(long id) {
        return roleRepository.findById(id);
    }

    public List<RoleDTO> findAllRoles() {
        return roleRepository.findAll().stream().map(this::convertToRoleDTO).toList();
    }

    private RoleDTO convertToRoleDTO(RoleEntity roleEntity) {
        return new RoleDTO(roleEntity.getId(), roleEntity.getName(), roleEntity.getDescription());
    }
}
