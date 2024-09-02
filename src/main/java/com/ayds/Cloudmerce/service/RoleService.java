package com.ayds.Cloudmerce.service;

import com.ayds.Cloudmerce.model.entity.RoleEntity;
import com.ayds.Cloudmerce.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleEntity findRoleById(int id){
        RoleEntity roleEntity = roleRepository.findById(id).get();
        return roleEntity;
    }

    public ArrayList<RoleEntity> finAllroles(){
        ArrayList<RoleEntity> roles = (ArrayList<RoleEntity>)roleRepository.findAll();
        return roles;
    }



}
