package com.ayds.Cloudmerce.service;

import com.ayds.Cloudmerce.mapper.UserMapper;
import com.ayds.Cloudmerce.model.dto.NewUserDto;
import com.ayds.Cloudmerce.model.entity.RoleEntity;
import com.ayds.Cloudmerce.model.entity.UserEntity;
import com.ayds.Cloudmerce.repository.UserRepository;
import com.ayds.Cloudmerce.utils.EncryptoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final EncryptoUtils encryptoUtils;

    public void saveUser(NewUserDto newUserDto){
        //TODO verificacion si existe el usuario
        UserEntity user = UserMapper.NewUserDtoToUserEntity(newUserDto);
        user.setPassword(encryptoUtils.encryptPassword(newUserDto.getPassword()));
        //buscamos el rol
        RoleEntity role = roleService.findRoleById(newUserDto.getRoleId());
        user.setRole(role);
        //TODO buscar el metodo de preferencia de pago
        user.setPaymentPreferenceId(newUserDto.getPaymentPreferenceId());
        userRepository.save(user);
    }


    public ArrayList<UserEntity> findAllUsers(){
        ArrayList<UserEntity> users = (ArrayList<UserEntity>)userRepository.findAll();
        return users;
    }
}
