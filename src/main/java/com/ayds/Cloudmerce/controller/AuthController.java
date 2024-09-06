package com.ayds.Cloudmerce.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.Cloudmerce.model.dto.SignInDto;
import com.ayds.Cloudmerce.model.dto.SignUpDto;
import com.ayds.Cloudmerce.model.dto.TokenDto;
import com.ayds.Cloudmerce.model.dto.UserDto;
import com.ayds.Cloudmerce.service.TokenService;
import com.ayds.Cloudmerce.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService authService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/sign-up")
    public ResponseEntity<UserDto> signUp(@RequestBody @Valid SignUpDto user) {
        UserDto dbUser = authService.registerUser(user);
        return new ResponseEntity<>(dbUser, HttpStatus.CREATED);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<TokenDto> signIn(@RequestBody @Valid SignInDto user) {
        var authenticableUser = new UsernamePasswordAuthenticationToken(user.email(), user.password());
        authenticationManager.authenticate(authenticableUser);

        Optional<TokenDto> token = authService.findUserByEmail(user.email())
                .map(tokenService::generateAccessToken);

        return ResponseEntity.of(token);
    }
}
