package com.ayds.Cloudmerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.Cloudmerce.model.dto.GoogleAuthDto;
import com.ayds.Cloudmerce.model.dto.GoogleAuthKeyDto;
import com.ayds.Cloudmerce.model.dto.PasswordChangeDto;
import com.ayds.Cloudmerce.model.dto.UserDto;
import com.ayds.Cloudmerce.model.exception.BadRequestException;
import com.ayds.Cloudmerce.service.GoogleAuthService;
import com.ayds.Cloudmerce.service.TokenService;
import com.ayds.Cloudmerce.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private GoogleAuthService googleAuthService;

    @Autowired
    private TokenService tokenService;

    @PatchMapping("/password")
    public ResponseEntity<UserDto> changePassword(@NonNull HttpServletRequest request,
            @RequestBody @Valid PasswordChangeDto user) {
        long id = tokenService.getIdFromToken(request);

        UserDto dbUser = userService.changeUserPassword(id, user.password(), user.repeatedPassword());

        return ResponseEntity.ok(dbUser);
    }

    @GetMapping("/multifactor-authentication")
    public ResponseEntity<GoogleAuthDto> generateMultiFactorAuthentication(@NonNull HttpServletRequest request) {
        long id = tokenService.getIdFromToken(request);

        GoogleAuthDto googleAuth = userService.findUserById(id)
                .filter(UserDto::hasMultiFactorAuth)
                .map(user -> {
                    String googleAuthKey = googleAuthService.getUserGoogleAuthKey();
                    String qrUrl = googleAuthService.generateGoogleAuthQrUrl("Cloudmerce", user.name(), googleAuthKey);
                    return new GoogleAuthDto(qrUrl, googleAuthKey);
                })
                .orElseThrow(() -> new BadRequestException(
                        "El usuario ya tiene activada la autenticacion por dos factores"));

        return ResponseEntity.ok(googleAuth);
    }

    @PatchMapping("/multifactor-authentication")
    public ResponseEntity<UserDto> addMultiFactorAuthentication(@NonNull HttpServletRequest request,
            @RequestBody GoogleAuthKeyDto googleKey) {
        long id = tokenService.getIdFromToken(request);

        UserDto user = userService.addGoogleAuthentication(id, googleKey.authKey());

        return ResponseEntity.ok(user);
    }
}
