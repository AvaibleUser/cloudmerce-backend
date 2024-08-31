package com.ayds.Cloudmerce.mapper;


import com.ayds.Cloudmerce.model.dto.NewUserDto;
import com.ayds.Cloudmerce.model.entity.UserEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class UserMapper {

    public static UserEntity NewUserDtoToUserEntity(NewUserDto newUserDto){
        UserEntity user = new UserEntity();
        user.setName(newUserDto.getName());
        user.setEmail(newUserDto.getEmail());
        user.setAddress(newUserDto.getAddress());
        user.setNit(newUserDto.getNit());

        user.setCreatedAt(new Date().toInstant().atZone(ZoneId.systemDefault())
                .toLocalDateTime());
        return user;
    }

}
