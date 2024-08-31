package com.ayds.Cloudmerce.service;

import com.ayds.Cloudmerce.model.entity.RoleEntity;
import com.ayds.Cloudmerce.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleEntity findRoleById(Integer id){
        RoleEntity roleEntity = roleRepository.finRoleById(id);


        return roleEntity;
    }



}
