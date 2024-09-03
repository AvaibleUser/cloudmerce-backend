package com.ayds.Cloudmerce.controller;

import com.ayds.Cloudmerce.model.dto.NewUserDto;
import com.ayds.Cloudmerce.model.dto.SignInDto;
import com.ayds.Cloudmerce.model.dto.TokenDto;
import com.ayds.Cloudmerce.model.entity.UserEntity;
import com.ayds.Cloudmerce.service.TokenService;
import com.ayds.Cloudmerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/auth/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final TokenService tokenService;

    private final AuthenticationManager authenticationManager;


    @PostMapping
    public ResponseEntity<Void> save(@RequestBody NewUserDto newUserDto){
        userService.saveUser(newUserDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<TokenDto> signIn(@RequestBody @Valid SignInDto sigInDto) {
        var authenticableUser = new UsernamePasswordAuthenticationToken(sigInDto.email(), sigInDto.password());
        UserEntity authUser = (UserEntity) authenticationManager.authenticate(authenticableUser).getPrincipal();
        String accessToken = tokenService.generateAccessToken(authUser);
        return ResponseEntity.ok(new TokenDto(accessToken));
    }


}
