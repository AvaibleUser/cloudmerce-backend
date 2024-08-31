package com.ayds.Cloudmerce.controller;

import com.ayds.Cloudmerce.model.dto.NewUserDto;
import com.ayds.Cloudmerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/auth/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping
    public ResponseEntity<Void> save(@RequestBody NewUserDto newUserDto){
        userService.saveUser(newUserDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


}
